

package cache.openrs.util.tukaani;

import cache.openrs.util.tukaani.lz.LZDecoder;
import cache.openrs.util.tukaani.lzma.LZMADecoder;
import cache.openrs.util.tukaani.rangecoder.RangeDecoderFromStream;

import java.io.DataInputStream;
import java.io.EOFException;
import java.io.IOException;
import java.io.InputStream;


public class LZMAInputStream extends InputStream {
    
    public static final int DICT_SIZE_MAX = Integer.MAX_VALUE & ~15;

    private InputStream in;
    private LZDecoder lz;
    private RangeDecoderFromStream rc;
    private LZMADecoder lzma;

    private boolean endReached = false;

    private final byte[] tempBuf = new byte[1];

    
    private long remainingSize;

    private IOException exception = null;

    
    public static int getMemoryUsage(int dictSize, byte propsByte)
            throws UnsupportedOptionsException, CorruptedInputException {
        if (dictSize < 0 || dictSize > DICT_SIZE_MAX)
            throw new UnsupportedOptionsException(
                    "LZMA dictionary is too big for this implementation");

        int props = propsByte & 0xFF;
        if (props > (4 * 5 + 4) * 9 + 8)
            throw new CorruptedInputException("Invalid LZMA properties byte");

        props %= 9 * 5;
        int lp = props / 9;
        int lc = props - lp * 9;

        return getMemoryUsage(dictSize, lc, lp);
    }

    
    public static int getMemoryUsage(int dictSize, int lc, int lp) {
        if (lc < 0 || lc > 8 || lp < 0 || lp > 4)
            throw new IllegalArgumentException("Invalid lc or lp");








        return 10 + getDictSize(dictSize) / 1024
               + ((2 * 0x300) << (lc + lp)) / 1024;
    }

    private static int getDictSize(int dictSize) {
        if (dictSize < 0 || dictSize > DICT_SIZE_MAX)
            throw new IllegalArgumentException(
                    "LZMA dictionary is too big for this implementation");











        if (dictSize < 4096)
            dictSize = 4096;



        return (dictSize + 15) & ~15;
    }

    
    public LZMAInputStream(InputStream in) throws IOException {
        this(in, -1);
    }

    
    public LZMAInputStream(InputStream in, int memoryLimit)
            throws IOException {
        DataInputStream inData = new DataInputStream(in);


        byte propsByte = inData.readByte();


        int dictSize = 0;
        for (int i = 0; i < 4; ++i)
            dictSize |= inData.readUnsignedByte() << (8 * i);





        long uncompSize = 0;
        for (int i = 0; i < 8; ++i)
            uncompSize |= (long)inData.readUnsignedByte() << (8 * i);


        int memoryNeeded = getMemoryUsage(dictSize, propsByte);
        if (memoryLimit != -1 && memoryNeeded > memoryLimit)
            throw new MemoryLimitException(memoryNeeded, memoryLimit);

        initialize(in, uncompSize, propsByte, dictSize, null);
    }

    
    public LZMAInputStream(InputStream in, long uncompSize, byte propsByte,
                           int dictSize) throws IOException {
        initialize(in, uncompSize, propsByte, dictSize, null);
    }

    
    public LZMAInputStream(InputStream in, long uncompSize, byte propsByte,
                           int dictSize, byte[] presetDict)
            throws IOException {
        initialize(in, uncompSize, propsByte, dictSize, presetDict);
    }

    
    public LZMAInputStream(InputStream in, long uncompSize,
                           int lc, int lp, int pb,
                           int dictSize, byte[] presetDict)
            throws IOException {
        initialize(in, uncompSize, lc, lp, pb, dictSize, presetDict);
    }

    private void initialize(InputStream in, long uncompSize, byte propsByte,
                            int dictSize, byte[] presetDict)
            throws IOException {


        if (uncompSize < -1)
            throw new UnsupportedOptionsException(
                    "Uncompressed size is too big");



        int props = propsByte & 0xFF;
        if (props > (4 * 5 + 4) * 9 + 8)
            throw new CorruptedInputException("Invalid LZMA properties byte");

        int pb = props / (9 * 5);
        props -= pb * 9 * 5;
        int lp = props / 9;
        int lc = props - lp * 9;



        if (dictSize < 0 || dictSize > DICT_SIZE_MAX)
            throw new UnsupportedOptionsException(
                    "LZMA dictionary is too big for this implementation");

        initialize(in, uncompSize, lc, lp, pb, dictSize, presetDict);
    }

    private void initialize(InputStream in, long uncompSize,
                            int lc, int lp, int pb,
                            int dictSize, byte[] presetDict)
            throws IOException {


        if (uncompSize < -1 || lc < 0 || lc > 8 || lp < 0 || lp > 4
                || pb < 0 || pb > 4)
            throw new IllegalArgumentException();

        this.in = in;



        dictSize = getDictSize(dictSize);
        if (uncompSize >= 0 && dictSize > uncompSize)
            dictSize = getDictSize((int)uncompSize);

        lz = new LZDecoder(getDictSize(dictSize), presetDict);
        rc = new RangeDecoderFromStream(in);
        lzma = new LZMADecoder(lz, rc, lc, lp, pb);
        remainingSize = uncompSize;
    }

    
    public int read() throws IOException {
        return read(tempBuf, 0, 1) == -1 ? -1 : (tempBuf[0] & 0xFF);
    }

    
    public int read(byte[] buf, int off, int len) throws IOException {
        if (off < 0 || len < 0 || off + len < 0 || off + len > buf.length)
            throw new IndexOutOfBoundsException();

        if (len == 0)
            return 0;

        if (in == null)
            throw new XZIOException("Stream closed");

        if (exception != null)
            throw exception;

        if (endReached)
            return -1;

        try {
            int size = 0;

            while (len > 0) {



                int copySizeMax = len;
                if (remainingSize >= 0 && remainingSize < len)
                    copySizeMax = (int)remainingSize;

                lz.setLimit(copySizeMax);


                try {
                    lzma.decode();
                } catch (CorruptedInputException e) {




                    if (remainingSize != -1 || !lzma.endMarkerDetected())
                        throw e;

                    endReached = true;





                    rc.normalize();
                }


                int copiedSize = lz.flush(buf, off);
                off += copiedSize;
                len -= copiedSize;
                size += copiedSize;

                if (remainingSize >= 0) {

                    remainingSize -= copiedSize;
                    assert remainingSize >= 0;

                    if (remainingSize == 0)
                        endReached = true;
                }

                if (endReached) {




                    if (!rc.isFinished() || lz.hasPending())
                        throw new CorruptedInputException();

                    return size == 0 ? -1 : size;
                }
            }

            return size;

        } catch (IOException e) {
            exception = e;
            throw e;
        }
    }

    
    public void close() throws IOException {
        if (in != null) {
            try {
                in.close();
            } finally {
                in = null;
            }
        }
    }
}
