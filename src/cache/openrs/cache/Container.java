package cache.openrs.cache;

import cache.alex.util.XTEAManager;
import cache.openrs.cache.util.CompressionUtils;
import cache.openrs.util.crypto.Xtea;

import java.io.IOException;
import java.nio.ByteBuffer;


public final class Container {

    
    public static final int COMPRESSION_NONE = 0;

    
    public static final int COMPRESSION_BZIP2 = 1;

    
    public static final int COMPRESSION_GZIP = 2;
    
    public static final int COMPRESSION_LZMA = 3;

    
    public static Container decode(ByteBuffer buffer) throws IOException {
        return Container.decode(buffer, XTEAManager.NULL_KEYS);
    }

    
    public static Container decode(ByteBuffer buffer, int[] keys) throws IOException {
        
        int type = buffer.get() & 0xFF;
        int length = buffer.getInt();
        
        if (keys[0] != 0 || keys[1] != 0 || keys[2] != 0 || keys[3] != 0) {
            Xtea.decipher(buffer, 5, length + (type == COMPRESSION_NONE ? 5 : 9), keys);
        }
        
        if (type == COMPRESSION_NONE) {
            
            byte[] temp = new byte[length];
            buffer.get(temp);
            ByteBuffer data = ByteBuffer.wrap(temp);

            
            int version = -1;
            if (buffer.remaining() >= 2) {
                version = buffer.getShort();
            }

            
            return new Container(type, data, version);
        } else {
            
            int uncompressedLength = buffer.getInt();

            
            byte[] compressed = new byte[length];
            buffer.get(compressed);

            
            byte[] uncompressed;
            if (type == COMPRESSION_BZIP2) {
                uncompressed = CompressionUtils.bunzip2(compressed);
            } else if (type == COMPRESSION_GZIP) {
                uncompressed = CompressionUtils.gunzip(compressed);
            } else if (type == COMPRESSION_LZMA) {
                uncompressed = CompressionUtils.unlzma(compressed, uncompressedLength);
            } else {
                throw new IOException("Invalid compression type");
            }

            
            if (uncompressed.length != uncompressedLength) {
                throw new IOException("Length mismatch");
            }

            
            int version = -1;
            if (buffer.remaining() >= 2) {
                version = buffer.getShort();
            }

            
            return new Container(type, ByteBuffer.wrap(uncompressed), version);
        }
    }

    
    private int type;

    
    private ByteBuffer data;

    
    private int version;

    
    public Container(int type, ByteBuffer data) {
        this(type, data, -1);
    }

    
    public Container(int type, ByteBuffer data, int version) {
        this.type = type;
        this.data = data;
        this.version = version;
    }

    
    public boolean isVersioned() {
        return version != -1;
    }

    
    public int getVersion() {
        if (!isVersioned()) throw new IllegalStateException();

        return version;
    }

    
    public void setVersion(int version) {
        this.version = version;
    }

    
    public void removeVersion() {
        this.version = -1;
    }

    
    public void setType(int type) {
        this.type = type;
    }

    
    public int getType() {
        return type;
    }

    
    public ByteBuffer getData() {
        return data.asReadOnlyBuffer();
    }

    
    public ByteBuffer encode() throws IOException {
        ByteBuffer data = getData();

        
        byte[] bytes = new byte[data.limit()];
        data.mark();
        data.get(bytes);
        data.reset();

        
        byte[] compressed;
        if (type == COMPRESSION_NONE) {
            compressed = bytes;
        } else if (type == COMPRESSION_GZIP) {
            compressed = CompressionUtils.gzip(bytes);
        } else if (type == COMPRESSION_BZIP2) {
            compressed = CompressionUtils.bzip2(bytes);
        } else {
            throw new IOException("Invalid compression type");
        }

        
        int header = 5 + (type == COMPRESSION_NONE ? 0 : 4) + (isVersioned() ? 2 : 0);
        ByteBuffer buf = ByteBuffer.allocate(header + compressed.length);

        
        buf.put((byte) type);
        buf.putInt(compressed.length);
        if (type != COMPRESSION_NONE) {
            buf.putInt(data.limit());
        }

        
        buf.put(compressed);

        
        if (isVersioned()) {
            buf.putShort((short) version);
        }

        
        return (ByteBuffer) buf.flip();
    }

}
