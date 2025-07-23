package com.misc.model.view.render;


public final class Buffer {
    public int offset;
    private final byte[] buffer;

    public Buffer(byte[] data) {
        this.buffer = data;
        this.offset = 0;
    }

    public int readUnsignedByte() {
        return this.buffer[this.offset++] & 255;
    }

    public byte readByte() {
        return this.buffer[this.offset++];
    }

    public int readUnsignedShort() {
        this.offset += 2;
        return ((this.buffer[this.offset - 2] & 255) << 8) + (this.buffer[this.offset - 1] & 255);
    }

    public int readMid() {
        this.offset += 3;
        return ((this.buffer[this.offset - 3] & 255) << 16) + ((this.buffer[this.offset - 2] & 255) << 8) + (this.buffer[this.offset - 1] & 255);
    }

    public int readShortv2() {
        this.offset += 2;
        return ((this.buffer[this.offset - 2] & 255) << 8) + (this.buffer[this.offset - 1] & 255);
    }

    public int readShortSmart() {
        return (this.buffer[this.offset] & 255) < 128 ? this.readUnsignedByte() - 64 : this.readUnsignedShort() - 49152;
    }
}
