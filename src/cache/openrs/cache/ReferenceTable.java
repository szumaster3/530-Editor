package cache.openrs.cache;

import cache.openrs.util.ByteBufferUtils;
import cache.openrs.util.crypto.Djb2;

import java.io.ByteArrayOutputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.util.SortedMap;
import java.util.TreeMap;


public class ReferenceTable {

    
    public static final int FLAG_IDENTIFIERS = 0x01;

    
    public static final int FLAG_WHIRLPOOL = 0x02;
    private static Identifiers identifiers;
    

    

    public static final int FLAG_UNKNOWN4 = 0x04;

    public static final int FLAG_UNKNOWN8 = 0x08;

    public ReferenceTable(Identifiers identifiers) {
        this.identifiers = identifiers;
    }

    
    
    public static class ChildEntry {

        
        private int identifier = -1;

        
        public int getIdentifier() {
            return identifier;
        }

        
        public void setIdentifier(int identifier) {
            this.identifier = identifier;
        }

    }

    
    public static class Entry {

        
        private int identifier = -1;

        
        private int crc;

        
        private byte[] whirlpool = new byte[64];

        
        private int version;

        
        private SortedMap<Integer, ChildEntry> entries = new TreeMap<Integer, ChildEntry>();

        
        public int getIdentifier() {
            return identifier;
        }

        
        public void setIdentifier(int identifier) {
            this.identifier = identifier;
        }

        
        public int getCrc() {
            return crc;
        }

        
        public void setCrc(int crc) {
            this.crc = crc;
        }

        
        public byte[] getWhirlpool() {
            return whirlpool;
        }

        
        public void setWhirlpool(byte[] whirlpool) {
            if (whirlpool.length != 64) throw new IllegalArgumentException();

            System.arraycopy(whirlpool, 0, this.whirlpool, 0, whirlpool.length);
        }

        
        private Identifiers identifiers;

        
        public int getVersion() {
            return version;
        }

        
        public void setVersion(int version) {
            this.version = version;
        }

        
        public int size() {
            return entries.size();
        }

        
        public int capacity() {
            if (entries.isEmpty()) return 0;

            return entries.lastKey() + 1;
        }

        
        public ChildEntry getEntry(int id) {
            return entries.get(id);
        }

        
        public void putEntry(int id, ChildEntry entry) {
            entries.put(id, entry);
        }

        
        public void removeEntry(int id, ChildEntry entry) {
            entries.remove(id);
        }

    }

    
    @SuppressWarnings("unused")
    public static ReferenceTable decode(ByteBuffer buffer) {
        
        ReferenceTable table = new ReferenceTable(identifiers);

        
        table.format = buffer.get() & 0xFF;
        if (table.format < 5 || table.format > 7) {
            throw new RuntimeException();
        }
        if (table.format >= 6) {
            table.version = buffer.getInt();
        }
        table.flags = buffer.get() & 0xFF;

        
        int[] ids = new int[table.format >= 7 ? ByteBufferUtils.getSmartInt(buffer) : buffer.getShort() & 0xFFFF];
        int accumulator = 0, size = -1;
        for (int i = 0; i < ids.length; i++) {
            int delta = table.format >= 7 ? ByteBufferUtils.getSmartInt(buffer) : buffer.getShort() & 0xFFFF;
            ids[i] = accumulator += delta;
            if (ids[i] > size) {
                size = ids[i];
            }
        }
        size++;


        
        int index = 0;
        for (int id : ids) {
            table.entries.put(id, new Entry());
        }

        
        int[] identifiersArray = new int[size];
        if ((table.flags & FLAG_IDENTIFIERS) != 0) {
            for (int id : ids) {
                int identifier = buffer.getInt();
                identifiersArray[id] = identifier;
                table.entries.get(id).identifier = identifier;
            }
        }
        table.identifiers = new Identifiers(identifiersArray);

        
        for (int id : ids) {
            table.entries.get(id).crc = buffer.getInt();
        }

        
        if ((table.flags & FLAG_UNKNOWN8) != 0) {
            for (int id : ids) {
                buffer.getInt();
            }
        }

        
        if ((table.flags & FLAG_WHIRLPOOL) != 0) {
            for (int id : ids) {
                buffer.get(table.entries.get(id).whirlpool);
            }
        }

        
        if ((table.flags & FLAG_UNKNOWN4) != 0) {
            for (int id : ids) {
                buffer.getInt();
                buffer.getInt();
            }
        }

        
        for (int id : ids) {
            table.entries.get(id).version = buffer.getInt();
        }

        
        int[][] members = new int[size][];
        for (int id : ids) {
            members[id] = new int[table.format >= 7 ? ByteBufferUtils.getSmartInt(buffer) : buffer.getShort() & 0xFFFF];
        }

        
        for (int id : ids) {
            
            accumulator = 0;
            size = -1;

            
            for (int i = 0; i < members[id].length; i++) {
                int delta = table.format >= 7 ? ByteBufferUtils.getSmartInt(buffer) : buffer.getShort() & 0xFFFF;
                members[id][i] = accumulator += delta;
                if (members[id][i] > size) {
                    size = members[id][i];
                }
            }
            size++;

            
            index = 0;
            for (int child : members[id]) {
                table.entries.get(id).entries.put(child, new ChildEntry());
            }
        }

        
        if ((table.flags & FLAG_IDENTIFIERS) != 0) {
            for (int id : ids) {
                int max = members[id][members[id].length - 1] + 1;
                identifiersArray = new int[max];
                for (int child : members[id]) {
                    int identifier = buffer.getInt();
                    identifiersArray[child] = identifier;
                    table.entries.get(id).entries.get(child).identifier = identifier;
                }
                table.entries.get(id).identifiers = new Identifiers(identifiersArray);
            }
        }

        
        return table;
    }

    
    private int format;

    
    private int version;

    
    private int flags;

    
    private SortedMap<Integer, Entry> entries = new TreeMap<Integer, Entry>();

    
    public int getFormat() {
        return format;
    }

    
    public void setFormat(int format) {
        this.format = format;
    }

    
    public int getVersion() {
        return version;
    }

    
    public void setVersion(int version) {
        this.version = version;
    }

    
    public int getFlags() {
        return flags;
    }

    
    public void setFlags(int flags) {
        this.flags = flags;
    }

    
    public Entry getEntry(int id) {
        return entries.get(id);
    }

    
    public ChildEntry getEntry(int id, int child) {
        Entry entry = entries.get(id);
        if (entry == null) return null;

        return entry.getEntry(child);
    }

    
    public void putEntry(int id, Entry entry) {
        entries.put(id, entry);
    }

    
    public void removeEntry(int id) {
        entries.remove(id);
    }

    
    public int size() {
        return entries.size();
    }

    
    public int capacity() {
        if (entries.isEmpty()) return 0;

        return entries.lastKey() + 1;
    }

    
    public Identifiers getIdentifiers() {
        return identifiers;
    }

    
    public ByteBuffer encode() throws IOException {
        
        ByteArrayOutputStream bout = new ByteArrayOutputStream();
        DataOutputStream os = new DataOutputStream(bout);
        try {
            
            os.write(format);
            if (format >= 6) {
                os.writeInt(version);
            }
            os.write(flags);

            
            os.writeShort(entries.size());

            
            int last = 0;
            for (int id = 0; id < capacity(); id++) {
                if (entries.containsKey(id)) {
                    int delta = id - last;
                    os.writeShort(delta);
                    last = id;
                }
            }

            
            if ((flags & FLAG_IDENTIFIERS) != 0) {
                for (Entry entry : entries.values()) {
                    os.writeInt(entry.identifier);
                }
            }

            
            for (Entry entry : entries.values()) {
                os.writeInt(entry.crc);
            }

            
            if ((flags & FLAG_WHIRLPOOL) != 0) {
                for (Entry entry : entries.values()) {
                    os.write(entry.whirlpool);
                }
            }

            
            for (Entry entry : entries.values()) {
                os.writeInt(entry.version);
            }

            
            for (Entry entry : entries.values()) {
                os.writeShort(entry.entries.size());
            }

            
            for (Entry entry : entries.values()) {
                last = 0;
                for (int id = 0; id < entry.capacity(); id++) {
                    if (entry.entries.containsKey(id)) {
                        int delta = id - last;
                        os.writeShort(delta);
                        last = id;
                    }
                }
            }

            
            if ((flags & FLAG_IDENTIFIERS) != 0) {
                for (Entry entry : entries.values()) {
                    for (ChildEntry child : entry.entries.values()) {
                        os.writeInt(child.identifier);
                    }
                }
            }

            
            byte[] bytes = bout.toByteArray();
            return ByteBuffer.wrap(bytes);
        } finally {
            os.close();
        }
    }

    
    public void updateRevision() {
        if (version <= 0) {
            version = 1;
        } else {
            version++;
        }
    }
}