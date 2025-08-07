package cache.openrs.cache;

import cache.openrs.util.ByteBufferUtils;

import java.nio.ByteBuffer;


public final class Sector {


    public static final int HEADER_SIZE = 8;


    public static final int DATA_SIZE = 512;


    public static final int EXTENDED_DATA_SIZE = 510;


    public static final int EXTENDED_HEADER_SIZE = 10;


    public static final int SIZE = HEADER_SIZE + DATA_SIZE;


    public static Sector decode(ByteBuffer buf) {
        if (buf.remaining() != SIZE) throw new IllegalArgumentException();

        int id = buf.getShort() & 0xFFFF;
        int chunk = buf.getShort() & 0xFFFF;
        int nextSector = ByteBufferUtils.getTriByte(buf);
        int type = buf.get() & 0xFF;
        byte[] data = new byte[DATA_SIZE];
        buf.get(data);

        return new Sector(type, id, chunk, nextSector, data);
    }


    public static Sector decodeExtended(ByteBuffer buf) {
        if (buf.remaining() != SIZE) throw new IllegalArgumentException();

        int id = buf.getInt();
        int chunk = buf.getShort() & 0xFFFF;
        int nextSector = ByteBufferUtils.getTriByte(buf);
        int type = buf.get() & 0xFF;
        byte[] data = new byte[EXTENDED_DATA_SIZE];
        buf.get(data);

        return new Sector(type, id, chunk, nextSector, data);
    }


    private final int type;


    private final int id;


    private final int chunk;


    private final int nextSector;


    private final byte[] data;


    public Sector(int type, int id, int chunk, int nextSector, byte[] data) {
        this.type = type;
        this.id = id;
        this.chunk = chunk;
        this.nextSector = nextSector;
        this.data = data;
    }


    public ByteBuffer encode() {
        ByteBuffer buf = ByteBuffer.allocate(SIZE);
        if (id > 65535) {
            buf.putInt(id);
        } else {
            buf.putShort((short) id);
        }
        buf.putShort((short) chunk);
        ByteBufferUtils.putTriByte(buf, nextSector);
        buf.put((byte) type);
        buf.put(data);

        return (ByteBuffer) buf.flip();
    }


    public int getChunk() {
        return chunk;
    }


    public byte[] getData() {
        return data;
    }


    public int getId() {
        return id;
    }


    public int getNextSector() {
        return nextSector;
    }


    public int getType() {
        return type;
    }

}