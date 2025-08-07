package cache.openrs.cache;

import cache.openrs.util.crypto.Rsa;
import cache.openrs.util.crypto.Whirlpool;

import java.io.ByteArrayOutputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.math.BigInteger;
import java.nio.ByteBuffer;


public class ChecksumTable {

    
    public static ChecksumTable decode(ByteBuffer buffer) throws IOException {
        return decode(buffer, false);
    }

    
    public static ChecksumTable decode(ByteBuffer buffer, boolean whirlpool) throws IOException {
        return decode(buffer, whirlpool, null, null);
    }

    
    public static ChecksumTable decode(ByteBuffer buffer, boolean whirlpool, BigInteger modulus, BigInteger publicKey) throws IOException {
        
        int size = whirlpool ? (buffer.limit() / 8) : (buffer.get() & 0xFF);
        ChecksumTable table = new ChecksumTable(size);

        
        byte[] masterDigest = null;
        if (whirlpool) {
            byte[] temp = new byte[size * 72 + 1];
            buffer.position(0);
            buffer.get(temp);
            masterDigest = Whirlpool.whirlpool(temp, 0, temp.length);
        }

        
        buffer.position(1);
        for (int i = 0; i < size; i++) {
            int crc = buffer.getInt();
            int version = buffer.getInt();
            byte[] digest = new byte[64];
            if (whirlpool) {
                buffer.get(digest);
            }
            table.entries[i] = new Entry(crc, version, digest);
        }

        
        if (whirlpool) {
            byte[] bytes = new byte[buffer.remaining()];
            buffer.get(bytes);
            ByteBuffer temp = ByteBuffer.wrap(bytes);

            if (modulus != null && publicKey != null) {
                temp = Rsa.crypt(buffer, modulus, publicKey);
            }

            if (temp.limit() != 66)
                throw new IOException("Decrypted data is not 66 bytes long");

            for (int i = 0; i < 64; i++) {
                if (temp.get(i + 1) != masterDigest[i])
                    throw new IOException("Whirlpool digest mismatch");
            }
        }

        
        return table;
    }

    
    public static class Entry {

        
        private final int crc;

        
        private final int version;

        
        private final byte[] whirlpool;

        
        public Entry(int crc, int version, byte[] whirlpool) {
            if (whirlpool.length != 64)
                throw new IllegalArgumentException();

            this.crc = crc;
            this.version = version;
            this.whirlpool = whirlpool;
        }

        
        public int getCrc() {
            return crc;
        }

        
        public int getVersion() {
            return version;
        }

        
        public byte[] getWhirlpool() {
            return whirlpool;
        }

    }

    
    private Entry[] entries;

    
    public ChecksumTable(int size) {
        entries = new Entry[size];
    }

    
    public ByteBuffer encode() throws IOException {
        return encode(false);
    }

    
    public ByteBuffer encode(boolean whirlpool) throws IOException {
        return encode(whirlpool, null, null);
    }

    
    public ByteBuffer encode(boolean whirlpool, BigInteger modulus, BigInteger privateKey) throws IOException {
        ByteArrayOutputStream bout = new ByteArrayOutputStream();
        DataOutputStream os = new DataOutputStream(bout);
        try {
            
            if (whirlpool)
                os.write(entries.length);

            
            for (int i = 0; i < entries.length; i++) {
                Entry entry = entries[i];
                os.writeInt(entry.getCrc());
                os.writeInt(entry.getVersion());
                if (whirlpool)
                    os.write(entry.getWhirlpool());
            }

            
            if (whirlpool) {
                byte[] bytes = bout.toByteArray();
                ByteBuffer temp = ByteBuffer.allocate(66);
                temp.put((byte) 0);
                temp.put(Whirlpool.whirlpool(bytes, 5, bytes.length - 5));
                temp.put((byte) 0);
                temp.flip();

                if (modulus != null && privateKey != null) {
                    temp = Rsa.crypt(temp, modulus, privateKey);
                }

                bytes = new byte[temp.limit()];
                temp.get(bytes);
                os.write(bytes);
            }

            byte[] bytes = bout.toByteArray();
            return ByteBuffer.wrap(bytes);
        } finally {
            os.close();
        }
    }

    
    public int getSize() {
        return entries.length;
    }

    
    public void setEntry(int id, Entry entry) {
        if (id < 0 || id >= entries.length)
            throw new IndexOutOfBoundsException();
        entries[id] = entry;
    }

    
    public Entry getEntry(int id) {
        if (id < 0 || id >= entries.length)
            throw new IndexOutOfBoundsException();
        return entries[id];
    }

}