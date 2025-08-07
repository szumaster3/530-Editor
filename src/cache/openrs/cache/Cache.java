package cache.openrs.cache;

import cache.alex.util.XTEAManager;
import cache.openrs.cache.type.CacheIndex;
import cache.openrs.util.ByteBufferUtils;
import cache.openrs.util.crypto.Djb2;
import cache.openrs.util.crypto.Whirlpool;

import java.io.Closeable;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.util.HashMap;
import java.util.Map;
import java.util.zip.CRC32;


public final class Cache implements Closeable {

    private static final Map<String, Integer> identifiers = new HashMap<>();


    private final FileStore store;

    private ReferenceTable[] references;


    public Cache(FileStore store) throws IOException {

        this.store = store;
        this.references = new ReferenceTable[store.getTypeCount()];

        for (int type = 0; type < store.getTypeCount(); type++) {
            ByteBuffer buf = store.read(255, type);









            if (buf != null && buf.limit() > 0) {
                references[type] = ReferenceTable.decode(Container.decode(buf, XTEAManager.lookupTable(type)).getData());
            }
        }
    }


    public int getTypeCount() throws IOException {
        return store.getTypeCount();
    }


    public int getFileCount(int type) throws IOException {
        return store.getFileCount(type);
    }


    public FileStore getStore() {
        return store;
    }

    public final ReferenceTable getReferenceTable(int type) {
        return references[type];
    }

    public final ReferenceTable getReferenceTable(CacheIndex index) {

        return references[index.getID()];
    }


    public ChecksumTable createChecksumTable() throws IOException {

        int size = store.getTypeCount();
        ChecksumTable table = new ChecksumTable(size);


        for (int i = 0; i < size; i++) {
            ByteBuffer buf = store.read(255, i);

            int crc = 0;
            int version = 0;
            byte[] whirlpool = new byte[64];


            if (buf.limit() > 0) {
                ReferenceTable ref = references[i];
                crc = ByteBufferUtils.getCrcChecksum(buf);
                version = ref.getVersion();
                buf.position(0);
                whirlpool = ByteBufferUtils.getWhirlpoolDigest(buf);
            }

            table.setEntry(i, new ChecksumTable.Entry(crc, version, whirlpool));
        }


        return table;
    }


    public Container read(int type, int file) throws IOException {

        if (type == 255)
            throw new IOException("Reference tables can only be read with the low level FileStore API!");


        return Container.decode(store.read(type, file));
    }


    public void write(int type, int file, Container container) throws IOException {

        if (type == 255)
            throw new IOException("Reference tables can only be modified with the low level FileStore API!");


        container.setVersion(container.getVersion() + 1);


        Container tableContainer = Container.decode(store.read(255, type));
        ReferenceTable table = ReferenceTable.decode(tableContainer.getData());


        ByteBuffer buffer = container.encode();
        byte[] bytes = new byte[buffer.limit() - 2];
        buffer.mark();
        try {
            buffer.position(0);
            buffer.get(bytes, 0, bytes.length);
        } finally {
            buffer.reset();
        }


        CRC32 crc = new CRC32();
        crc.update(bytes, 0, bytes.length);


        ReferenceTable.Entry entry = table.getEntry(file);
        if (entry == null) {

            entry = new ReferenceTable.Entry();
            table.putEntry(file, entry);
        }
        entry.setVersion(container.getVersion());
        entry.setCrc((int) crc.getValue());


        if ((table.getFlags() & ReferenceTable.FLAG_WHIRLPOOL) != 0) {
            byte[] whirlpool = Whirlpool.whirlpool(bytes, 0, bytes.length);
            entry.setWhirlpool(whirlpool);
        }


        table.setVersion(table.getVersion() + 1);


        tableContainer = new Container(tableContainer.getType(), table.encode());
        store.write(255, type, tableContainer.encode());


        store.write(type, file, buffer);
    }


    public ByteBuffer read(int type, int file, int member) throws IOException {

        Container container = read(type, file);
        Container tableContainer = Container.decode(store.read(255, type));
        ReferenceTable table = ReferenceTable.decode(tableContainer.getData());


        ReferenceTable.Entry entry = table.getEntry(file);
        if (entry == null || member < 0 || member >= entry.capacity())
            throw new FileNotFoundException();


        Archive archive = Archive.decode(container.getData(), entry.capacity());
        return archive.getEntry(member);
    }


    public int getFileId(int type, String name) throws IOException {
        if (!identifiers.containsKey(name)) {
            ReferenceTable table = references[type];
            identifiers.put(name, table.getIdentifiers().getFile(Djb2.hash(name)));
        }

        Integer i = identifiers.get(name);
        return i == null ? -1 : i.intValue();
    }

    public Container read(int type, int file, int[] keys) throws IOException {

        if (type == 255)
            throw new IOException("Reference tables can only be read with the low level FileStore API!");


        return Container.decode(store.read(type, file), keys);
    }


    public Container getFile(int index, int archive) throws IOException {
        return read(index, archive);
    }


    public ByteBuffer getFile(int index, int archive, int file) throws IOException {
        return read(index, archive, file);
    }


    public void write(int type, int file, int member, ByteBuffer data) throws IOException {

        Container tableContainer = Container.decode(store.read(255, type));
        ReferenceTable table = ReferenceTable.decode(tableContainer.getData());


        ReferenceTable.Entry entry = table.getEntry(file);
        int oldArchiveSize = -1;
        if (entry == null) {
            entry = new ReferenceTable.Entry();
            table.putEntry(file, entry);
        } else {
            oldArchiveSize = entry.capacity();
        }


        ReferenceTable.ChildEntry child = entry.getEntry(member);
        if (child == null) {
            child = new ReferenceTable.ChildEntry();
            entry.putEntry(member, child);
        }


        Archive archive;
        int containerType, containerVersion;
        if (file < store.getFileCount(type) && oldArchiveSize != -1) {
            Container container = read(type, file);
            containerType = container.getType();
            containerVersion = container.getVersion();
            archive = Archive.decode(container.getData(), oldArchiveSize);
        } else {
            containerType = Container.COMPRESSION_GZIP;
            containerVersion = 1;
            archive = new Archive(member + 1);
        }


        if (member >= archive.size()) {
            Archive newArchive = new Archive(member + 1);
            for (int id = 0; id < archive.size(); id++) {
                newArchive.putEntry(id, archive.getEntry(id));
            }
            archive = newArchive;
        }


        archive.putEntry(member, data);


        for (int id = 0; id < archive.size(); id++) {
            if (archive.getEntry(id) == null) {
                entry.putEntry(id, new ReferenceTable.ChildEntry());
                archive.putEntry(id, ByteBuffer.allocate(1));
            }
        }


        tableContainer = new Container(tableContainer.getType(), table.encode());
        store.write(255, type, tableContainer.encode());


        Container container = new Container(containerType, archive.encode(), containerVersion);
        write(type, file, container);
    }


    public int getLastArchiveId(int index) {
        if (references[index] == null) {
            throw new IllegalStateException("Reference table is null for index: " + index);
        }
        return references[index].capacity() - 1;
    }


    public int getValidFilesCount(int index, int archiveId) {
        ReferenceTable ref = references[index];
        if (ref == null) {
            throw new IllegalStateException("Reference table is null for index: " + index);
        }
        ReferenceTable.Entry entry = ref.getEntry(archiveId);
        if (entry == null) {
            return 0;
        }
        return entry.size();
    }

    @Override
    public void close() throws IOException {
        store.close();
    }
}