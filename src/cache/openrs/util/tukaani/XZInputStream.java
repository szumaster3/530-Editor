

package cache.openrs.util.tukaani;

import cache.openrs.util.tukaani.common.DecoderUtil;

import java.io.DataInputStream;
import java.io.EOFException;
import java.io.IOException;
import java.io.InputStream;


public class XZInputStream extends InputStream {
    private final int memoryLimit;
    private InputStream in;
    private SingleXZInputStream xzIn;
    private boolean endReached = false;
    private IOException exception = null;

    private final byte[] tempBuf = new byte[1];

    
    public XZInputStream(InputStream in) throws IOException {
        this(in, -1);
    }

    
    public XZInputStream(InputStream in, int memoryLimit) throws IOException {
        this.in = in;
        this.memoryLimit = memoryLimit;
        this.xzIn = new SingleXZInputStream(in, memoryLimit);
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
                if (xzIn == null) {
                    prepareNextStream();
                    if (endReached)
                        return size == 0 ? -1 : size;
                }

                int ret = xzIn.read(buf, off, len);

                if (ret > 0) {
                    size += ret;
                    off += ret;
                    len -= ret;
                } else if (ret == -1) {
                    xzIn = null;
                }
            }
        } catch (IOException e) {
            exception = e;
            if (size == 0)
                throw e;
        }

        return size;
    }

    private void prepareNextStream() throws IOException {
        DataInputStream inData = new DataInputStream(in);
        byte[] buf = new byte[DecoderUtil.STREAM_HEADER_SIZE];



        do {


            int ret = inData.read(buf, 0, 1);
            if (ret == -1) {
                endReached = true;
                return;
            }



            inData.readFully(buf, 1, 3);

        } while (buf[0] == 0 && buf[1] == 0 && buf[2] == 0 && buf[3] == 0);




        inData.readFully(buf, 4, DecoderUtil.STREAM_HEADER_SIZE - 4);

        try {
            xzIn = new SingleXZInputStream(in, memoryLimit, buf);
        } catch (XZFormatException e) {


            throw new CorruptedInputException(
                    "Garbage after a valid XZ Stream");
        }
    }

    
    public int available() throws IOException {
        if (in == null)
            throw new XZIOException("Stream closed");

        if (exception != null)
            throw exception;

        return xzIn == null ? 0 : xzIn.available();
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
