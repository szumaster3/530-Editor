package cache.openrs.cache;

import cache.openrs.util.ByteBufferUtils;

import java.nio.ByteBuffer;


public final class Index {


    public static final int SIZE = 6;


    public static Index decode(ByteBuffer buf) {
        if (buf.remaining() != SIZE) throw new IllegalArgumentException();

        int size = ByteBufferUtils.getTriByte(buf);
        int sector = ByteBufferUtils.getTriByte(buf);
        return new Index(size, sector);
    }


    private int size;


    private int sector;


    public Index(int size, int sector) {
        this.size = size;
        this.sector = sector;
    }


    public int getSize() {
        return size;
    }


    public int getSector() {
        return sector;
    }


    public ByteBuffer encode() {
        ByteBuffer buf = ByteBuffer.allocate(Index.SIZE);
        ByteBufferUtils.putTriByte(buf, size);
        ByteBufferUtils.putTriByte(buf, sector);
        return (ByteBuffer) buf.flip();
    }

}