

package cache.openrs.util.tukaani;

import cache.openrs.util.tukaani.lz.LZDecoder;
import cache.openrs.util.tukaani.lzma.LZMADecoder;
import cache.openrs.util.tukaani.rangecoder.RangeDecoderFromBuffer;

import java.io.DataInputStream;
import java.io.IOException;
import java.io.InputStream;


public class LZMA2InputStream extends InputStream {

    public static final int DICT_SIZE_MIN = 4096;


    public static final int DICT_SIZE_MAX = Integer.MAX_VALUE & ~15;

    private static final int COMPRESSED_SIZE_MAX = 1 << 16;

    private DataInputStream in;

    private final LZDecoder lz;
    private final RangeDecoderFromBuffer rc
            = new RangeDecoderFromBuffer(COMPRESSED_SIZE_MAX);
    private LZMADecoder lzma;

    private int uncompressedSize = 0;
    private boolean isLZMAChunk;

    private boolean needDictReset = true;
    private boolean needProps = true;
    private boolean endReached = false;

    private IOException exception = null;

    private final byte[] tempBuf = new byte[1];


    public static int getMemoryUsage(int dictSize) {



        return 40 + COMPRESSED_SIZE_MAX / 1024 + getDictSize(dictSize) / 1024;
    }

    private static int getDictSize(int dictSize) {
        if (dictSize < DICT_SIZE_MIN || dictSize > DICT_SIZE_MAX)
            throw new IllegalArgumentException(
                    "Unsupported dictionary size " + dictSize);





        return (dictSize + 15) & ~15;
    }


    public LZMA2InputStream(InputStream in, int dictSize) {
        this(in, dictSize, null);
    }


    public LZMA2InputStream(InputStream in, int dictSize, byte[] presetDict) {


        if (in == null)
            throw new NullPointerException();

        this.in = new DataInputStream(in);
        this.lz = new LZDecoder(getDictSize(dictSize), presetDict);

        if (presetDict != null && presetDict.length > 0)
            needDictReset = false;
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
                if (uncompressedSize == 0) {
                    decodeChunkHeader();
                    if (endReached)
                        return size == 0 ? -1 : size;
                }

                int copySizeMax = Math.min(uncompressedSize, len);

                if (!isLZMAChunk) {
                    lz.copyUncompressed(in, copySizeMax);
                } else {
                    lz.setLimit(copySizeMax);
                    lzma.decode();
                    if (!rc.isInBufferOK())
                        throw new CorruptedInputException();
                }

                int copiedSize = lz.flush(buf, off);
                off += copiedSize;
                len -= copiedSize;
                size += copiedSize;
                uncompressedSize -= copiedSize;

                if (uncompressedSize == 0)
                    if (!rc.isFinished() || lz.hasPending())
                        throw new CorruptedInputException();
            }

            return size;

        } catch (IOException e) {
            exception = e;
            throw e;
        }
    }

    private void decodeChunkHeader() throws IOException {
        int control = in.readUnsignedByte();

        if (control == 0x00) {
            endReached = true;
            return;
        }

        if (control >= 0xE0 || control == 0x01) {
            needProps = true;
            needDictReset = false;
            lz.reset();
        } else if (needDictReset) {
            throw new CorruptedInputException();
        }

        if (control >= 0x80) {
            isLZMAChunk = true;

            uncompressedSize = (control & 0x1F) << 16;
            uncompressedSize += in.readUnsignedShort() + 1;

            int compressedSize = in.readUnsignedShort() + 1;

            if (control >= 0xC0) {
                needProps = false;
                decodeProps();

            } else if (needProps) {
                throw new CorruptedInputException();

            } else if (control >= 0xA0) {
                lzma.reset();
            }

            rc.prepareInputBuffer(in, compressedSize);

        } else if (control > 0x02) {
            throw new CorruptedInputException();

        } else {
            isLZMAChunk = false;
            uncompressedSize = in.readUnsignedShort() + 1;
        }
    }

    private void decodeProps() throws IOException {
        int props = in.readUnsignedByte();

        if (props > (4 * 5 + 4) * 9 + 8)
            throw new CorruptedInputException();

        int pb = props / (9 * 5);
        props -= pb * 9 * 5;
        int lp = props / 9;
        int lc = props - lp * 9;

        if (lc + lp > 4)
            throw new CorruptedInputException();

        lzma = new LZMADecoder(lz, rc, lc, lp, pb);
    }


    public int available() throws IOException {
        if (in == null)
            throw new XZIOException("Stream closed");

        if (exception != null)
            throw exception;

        return uncompressedSize;
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
