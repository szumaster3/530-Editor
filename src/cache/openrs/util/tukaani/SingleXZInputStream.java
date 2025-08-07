

package cache.openrs.util.tukaani;

import cache.openrs.util.tukaani.check.Check;
import cache.openrs.util.tukaani.common.DecoderUtil;
import cache.openrs.util.tukaani.common.StreamFlags;
import cache.openrs.util.tukaani.index.IndexHash;

import java.io.DataInputStream;
import java.io.EOFException;
import java.io.IOException;
import java.io.InputStream;


public class SingleXZInputStream extends InputStream {
    private InputStream in;
    private int memoryLimit;
    private StreamFlags streamHeaderFlags;
    private Check check;
    private BlockInputStream blockDecoder = null;
    private final IndexHash indexHash = new IndexHash();
    private boolean endReached = false;
    private IOException exception = null;

    private final byte[] tempBuf = new byte[1];

    
    public SingleXZInputStream(InputStream in) throws IOException {
        initialize(in, -1);
    }

    
    public SingleXZInputStream(InputStream in, int memoryLimit)
            throws IOException {
        initialize(in, memoryLimit);
    }

    SingleXZInputStream(InputStream in, int memoryLimit,
                        byte[] streamHeader) throws IOException {
        initialize(in, memoryLimit, streamHeader);
    }

    private void initialize(InputStream in, int memoryLimit)
            throws IOException {
        byte[] streamHeader = new byte[DecoderUtil.STREAM_HEADER_SIZE];
        new DataInputStream(in).readFully(streamHeader);
        initialize(in, memoryLimit, streamHeader);
    }

    private void initialize(InputStream in, int memoryLimit,
                            byte[] streamHeader) throws IOException {
        this.in = in;
        this.memoryLimit = memoryLimit;
        streamHeaderFlags = DecoderUtil.decodeStreamHeader(streamHeader);
        check = Check.getInstance(streamHeaderFlags.checkType);
    }

    
    public int getCheckType() {
        return streamHeaderFlags.checkType;
    }

    
    public String getCheckName() {
        return check.getName();
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

        int size = 0;

        try {
            while (len > 0) {
                if (blockDecoder == null) {
                    try {
                        blockDecoder = new BlockInputStream(
                                in, check, memoryLimit, -1, -1);
                    } catch (IndexIndicatorException e) {
                        indexHash.validate(in);
                        validateStreamFooter();
                        endReached = true;
                        return size > 0 ? size : -1;
                    }
                }

                int ret = blockDecoder.read(buf, off, len);

                if (ret > 0) {
                    size += ret;
                    off += ret;
                    len -= ret;
                } else if (ret == -1) {
                    indexHash.add(blockDecoder.getUnpaddedSize(),
                                  blockDecoder.getUncompressedSize());
                    blockDecoder = null;
                }
            }
        } catch (IOException e) {
            exception = e;
            if (size == 0)
                throw e;
        }

        return size;
    }

    private void validateStreamFooter() throws IOException {
        byte[] buf = new byte[DecoderUtil.STREAM_HEADER_SIZE];
        new DataInputStream(in).readFully(buf);
        StreamFlags streamFooterFlags = DecoderUtil.decodeStreamFooter(buf);

        if (!DecoderUtil.areStreamFlagsEqual(streamHeaderFlags,
                                             streamFooterFlags)
                || indexHash.getIndexSize() != streamFooterFlags.backwardSize)
            throw new CorruptedInputException(
                    "XZ Stream Footer does not match Stream Header");
    }

    
    public int available() throws IOException {
        if (in == null)
            throw new XZIOException("Stream closed");

        if (exception != null)
            throw exception;

        return blockDecoder == null ? 0 : blockDecoder.available();
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
