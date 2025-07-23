package com.cache.io;

public final class InputStream extends Stream {
    private static final int[] BIT_MASK = new int[]{0, 1, 3, 7, 15, 31, 63, 127, 255, 511, 1023, 2047, 4095, 8191, 16383, 32767, '\uffff', 131071, 262143, 524287, 1048575, 2097151, 4194303, 8388607, 16777215, 33554431, 67108863, 134217727, 268435455, 536870911, 1073741823, Integer.MAX_VALUE, -1};

    public InputStream(int capacity) {
        this.buffer = new byte[capacity];
    }

    public InputStream(byte[] buffer) {
        this.buffer = buffer;
        this.length = buffer.length;
    }

    public void checkCapacity(int length) {
        if (this.offset + length >= this.buffer.length) {
            byte[] newBuffer = new byte[(this.offset + length) * 2];
            System.arraycopy(this.buffer, 0, newBuffer, 0, this.buffer.length);
            this.buffer = newBuffer;
        }

    }

    public void skip(int length) {
        this.offset += length;
    }

    public void setLength(int length) {
        this.length = length;
    }

    public void setOffset(int offset) {
        this.offset = offset;
    }

    public int getRemaining() {
        return this.offset < this.length ? this.length - this.offset : 0;
    }

    public int readByte() {
        return this.getRemaining() > 0 ? this.buffer[this.offset++] : 0;
    }

    public void readBytes(byte[] buffer, int off, int len) {
        for (int k = off; k < len + off; ++k) {
            buffer[k] = (byte) this.readByte();
        }

    }

    public int readUnsignedByte() {
        return this.readByte() & 255;
    }

    public int readShort() {
        int i = (this.readUnsignedByte() << 8) + this.readUnsignedByte();
        if (i > 32767) {
            i -= 65536;
        }

        return i;
    }

    public int readUnsignedShortLE() {
        return this.readUnsignedByte() + (this.readUnsignedByte() << 8);
    }

    public int readUnsignedShort() {
        return (this.readUnsignedByte() << 8) + this.readUnsignedByte();
    }

    public int readInt() {
        return (this.readUnsignedByte() << 24) + (this.readUnsignedByte() << 16) + (this.readUnsignedByte() << 8) + this.readUnsignedByte();
    }

    public int read24BitInt() {
        return (this.readUnsignedByte() << 16) + (this.readUnsignedByte() << 8) + this.readUnsignedByte();
    }

    public int readIntV2() {
        return (this.readUnsignedByte() << 16) + (this.readUnsignedByte() << 24) + this.readUnsignedByte() + (this.readUnsignedByte() << 8);
    }

    public String readString() {
        String s;
        int b;
        for (s = ""; (b = this.readByte()) != 0; s = s + (char) b) {
        }

        return s;
    }

    public String readJagString() {
        this.readByte();

        String s;
        int b;
        for (s = ""; (b = this.readByte()) != 0; s = s + (char) b) {
        }

        return s;
    }

    public int readBigSmart() {
        if (~this.buffer[this.offset] <= -1) {
            int value = this.readUnsignedShort();
            return value == 32767 ? -1 : value;
        } else {
            return this.readInt() & Integer.MAX_VALUE;
        }
    }

    public int readMedium() {
        return (this.readUnsignedByte() << 16) + (this.readUnsignedByte() << 8) + this.readUnsignedByte();
    }

    public int readUnsignedSmart() {
        int i = 255 & this.buffer[this.offset];
        return i >= 128 ? -32768 + this.readUnsignedShort() : this.readUnsignedByte();
    }

    public int readUnsignedSmart3() {
        int i = 0xff & getBuffer()[offset];
        if (i >= 128) return -49152 + readUnsignedShort();
        return -64 + readUnsignedByte();
    }

    public int readSmartInt() {
        if (~this.buffer[this.offset] <= -1) {
            int value = this.readUnsignedShort();
            return value == 32767 ? -1 : value;
        } else {
            return this.readInt() & Integer.MAX_VALUE;
        }
    }
}
