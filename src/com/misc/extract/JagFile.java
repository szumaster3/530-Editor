package com.misc.extract;

import com.cache.io.ByteStream;


public class JagFile {
    public byte[] fBytes;
    public int dataSize;
    public int[] filename;
    public int[] fileSize;
    public int[] fileLength;
    public int[] fileStart;
    private boolean decompressed;


    public JagFile(byte[] paramArrayOfByte) {
        loadStream(paramArrayOfByte);

    }

    private void loadStream(byte[] paramArrayOfByte) {
        ByteStream localByteStream = new ByteStream(paramArrayOfByte);
        int i = localByteStream.read3Bytes();
        int j = localByteStream.read3Bytes();
        if (j != i) {
            byte[] arrayOfByte = new byte[i];
            PackJAG.method73(arrayOfByte, i, paramArrayOfByte, j, 6);
            this.fBytes = arrayOfByte;
            localByteStream = new ByteStream(this.fBytes);
            this.decompressed = true;
        } else {
            this.fBytes = paramArrayOfByte;
            this.decompressed = false;
        }
        this.dataSize = localByteStream.readUnsignedWord();
        this.filename = new int[this.dataSize];
        this.fileSize = new int[this.dataSize];
        this.fileLength = new int[this.dataSize];
        this.fileStart = new int[this.dataSize];
        int k = localByteStream.Offset + this.dataSize * 10;
        for (int m = 0; m < this.dataSize; m++) {
            this.filename[m] = localByteStream.readDWord();
            this.fileSize[m] = localByteStream.read3Bytes();
            this.fileLength[m] = localByteStream.read3Bytes();
            this.fileStart[m] = k;
            k += this.fileLength[m];
        }

    }


    public byte[] readfile(String paramString, byte[] paramArrayOfByte) {
        int i = 0;
        paramString = paramString.toUpperCase();
        for (int j = 0; j < paramString.length(); j++) {
            i = i * 61 + paramString.charAt(j) - 32;
        }
        for (int j = 0; j < this.dataSize; j++) {
            if (this.filename[j] == i) {
                if (paramArrayOfByte == null) paramArrayOfByte = new byte[this.fileSize[j]];
                if (!this.decompressed) {
                    PackJAG.method73(paramArrayOfByte, this.fileSize[j], this.fBytes, this.fileLength[j], this.fileStart[j]);
                } else {

                    if (this.fileSize[j] >= 0)
                        System.arraycopy(this.fBytes, this.fileStart[j], paramArrayOfByte, 0, this.fileSize[j]);
                }
                return paramArrayOfByte;
            }
        }
        return null;

    }


    public byte[] readfile(int paramInt, byte[] paramArrayOfByte) {
        if (paramArrayOfByte == null) paramArrayOfByte = new byte[this.fileSize[paramInt]];
        if (!this.decompressed) {
            PackJAG.method73(paramArrayOfByte, this.fileSize[paramInt], this.fBytes, this.fileLength[paramInt], this.fileStart[paramInt]);
        } else {

            if (this.fileSize[paramInt] >= 0)
                System.arraycopy(this.fBytes, this.fileStart[paramInt], paramArrayOfByte, 0, this.fileSize[paramInt]);
        }
        return paramArrayOfByte;

    }

}

