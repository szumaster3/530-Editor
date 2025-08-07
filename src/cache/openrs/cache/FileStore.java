package cache.openrs.cache;

import cache.openrs.util.FileChannelUtils;

import java.io.*;
import java.nio.ByteBuffer;
import java.nio.channels.FileChannel;
import java.util.ArrayList;
import java.util.List;


public final class FileStore implements Closeable {

    
    public static FileStore open(String root) throws FileNotFoundException {
        return open(new File(root));
    }

    
    public static FileStore open(File root) throws FileNotFoundException {
        File data = new File(root, "main_file_cache.dat2");
        if (!data.exists())
            throw new FileNotFoundException();

        RandomAccessFile raf = new RandomAccessFile(data, "rw");
        FileChannel dataChannel = raf.getChannel();

        List<FileChannel> indexChannels = new ArrayList<FileChannel>();
        for (int i = 0; i < 254; i++) {
            File index = new File(root, "main_file_cache.idx" + i);
            if (!index.exists())
                break;

            raf = new RandomAccessFile(index, "rw");
            FileChannel indexChannel = raf.getChannel();
            indexChannels.add(indexChannel);
        }

        if (indexChannels.isEmpty())
            throw new FileNotFoundException();

        File meta = new File(root, "main_file_cache.idx255");
        if (!meta.exists())
            throw new FileNotFoundException();

        raf = new RandomAccessFile(meta, "rw");
        FileChannel metaChannel = raf.getChannel();

        return new FileStore(dataChannel, indexChannels.toArray(new FileChannel[0]), metaChannel);
    }

    
    private final FileChannel dataChannel;

    
    private final FileChannel[] indexChannels;

    
    private final FileChannel metaChannel;

    
    public FileStore(FileChannel data, FileChannel[] indexes, FileChannel meta) {
        this.dataChannel = data;
        this.indexChannels = indexes;
        this.metaChannel = meta;
    }

    
    public int getTypeCount() throws IOException {
        return indexChannels.length;
    }

    
    public int getFileCount(int type) throws IOException {
        if ((type < 0 || type >= indexChannels.length) && type != 255)
            throw new FileNotFoundException();

        if (type == 255)
            return (int) (metaChannel.size() / Index.SIZE);
        return (int) (indexChannels[type].size() / Index.SIZE);
    }

    
    public void write(int type, int id, ByteBuffer data) throws IOException {
        data.mark();
        if (!write(type, id, data, true)) {
            data.reset();
            write(type, id, data, false);
        }
    }

    
    private boolean write(int type, int id, ByteBuffer data, boolean overwrite) throws IOException {
        if ((type < 0 || type >= indexChannels.length) && type != 255)
            throw new FileNotFoundException();

        FileChannel indexChannel = type == 255 ? metaChannel : indexChannels[type];

        int nextSector = 0;
        long ptr = id * Index.SIZE;
        if (overwrite) {
            if (ptr < 0)
                throw new IOException();
            else if (ptr >= indexChannel.size())
                return false;

            ByteBuffer buf = ByteBuffer.allocate(Index.SIZE);
            FileChannelUtils.readFully(indexChannel, buf, ptr);

            Index index = Index.decode((ByteBuffer) buf.flip());
            nextSector = index.getSector();
            if (nextSector <= 0 || nextSector > dataChannel.size() * Sector.SIZE)
                return false;
        } else {
            nextSector = (int) ((dataChannel.size() + Sector.SIZE - 1) / Sector.SIZE);
            if (nextSector == 0)
                nextSector = 1;
        }

        Index index = new Index(data.remaining(), nextSector);
        indexChannel.write(index.encode(), ptr);

        ByteBuffer buf = ByteBuffer.allocate(Sector.SIZE);

        int chunk = 0, remaining = index.getSize();
        do {
            int curSector = nextSector;
            ptr = curSector * Sector.SIZE;
            nextSector = 0;

            if (overwrite) {
                buf.clear();
                FileChannelUtils.readFully(dataChannel, buf, ptr);

                Sector sector = Sector.decode((ByteBuffer) buf.flip());

                if (sector.getType() != type)
                    return false;

                if (sector.getId() != id)
                    return false;

                if (sector.getChunk() != chunk)
                    return false;

                nextSector = sector.getNextSector();
                if (nextSector < 0 || nextSector > dataChannel.size() / Sector.SIZE)
                    return false;
            }

            if (nextSector == 0) {
                overwrite = false;
                nextSector = (int) ((dataChannel.size() + Sector.SIZE - 1) / Sector.SIZE);
                if (nextSector == 0)
                    nextSector++;
                if (nextSector == curSector)
                    nextSector++;
            }

            byte[] bytes = new byte[Sector.DATA_SIZE];
            if (remaining < Sector.DATA_SIZE) {
                data.get(bytes, 0, remaining);
                nextSector = 0;
                remaining = 0;
            } else {
                remaining -= Sector.DATA_SIZE;
                data.get(bytes, 0, Sector.DATA_SIZE);
            }

            Sector sector = new Sector(type, id, chunk++, nextSector, bytes);
            dataChannel.write(sector.encode(), ptr);
        } while (remaining > 0);

        return true;
    }

    
    public ByteBuffer read(int type, int id) throws IOException {
        if ((type < 0 || type >= indexChannels.length) && type != 255)
            throw new FileNotFoundException();

        FileChannel indexChannel = type == 255 ? metaChannel : indexChannels[type];

        long ptr = (long) id * (long) Index.SIZE;
        if (ptr < 0 || ptr >= indexChannel.size()) {
            throw new FileNotFoundException("Invalid pointer " + ptr + " for archive " + type + ", file " + id);
        }

        ByteBuffer buf = ByteBuffer.allocate(Index.SIZE);
        FileChannelUtils.readFully(indexChannel, buf, ptr);

        Index index = Index.decode((ByteBuffer) buf.flip());

        ByteBuffer data = ByteBuffer.allocate(index.getSize());
        buf = ByteBuffer.allocate(Sector.SIZE);

        int chunk = 0, remaining = index.getSize();
        ptr = (long) index.getSector() * (long) Sector.SIZE;
        do {
            buf.clear();
            FileChannelUtils.readFully(dataChannel, buf, ptr);
            boolean extended = id > 65535;
            Sector sector = extended ? Sector.decodeExtended((ByteBuffer) buf.flip()) : Sector.decode((ByteBuffer) buf.flip());
            int dataSize = extended ? Sector.EXTENDED_DATA_SIZE : Sector.DATA_SIZE;
            if (remaining > dataSize) {
                data.put(sector.getData(), 0, dataSize);
                remaining -= dataSize;

                if (sector.getType() != type)
                    throw new IOException("File type mismatch.");

                if (sector.getId() != id)
                    throw new IOException("File id mismatch.");

                if (sector.getChunk() != chunk++)
                    throw new IOException("Chunk mismatch.");

                ptr = (long) sector.getNextSector() * (long) Sector.SIZE;
            } else {
                data.put(sector.getData(), 0, remaining);
                remaining = 0;
            }
        } while (remaining > 0);
        return (ByteBuffer) data.flip();
    }

    @Override
    public void close() throws IOException {
        dataChannel.close();

        for (FileChannel channel : indexChannels)
            channel.close();

        metaChannel.close();
    }

}