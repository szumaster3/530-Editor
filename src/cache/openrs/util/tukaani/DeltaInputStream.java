

package cache.openrs.util.tukaani;

import cache.openrs.util.tukaani.delta.DeltaDecoder;

import java.io.IOException;
import java.io.InputStream;


public class DeltaInputStream extends InputStream {
    
    public static final int DISTANCE_MIN = 1;

    
    public static final int DISTANCE_MAX = 256;

    private InputStream in;
    private final DeltaDecoder delta;

    private IOException exception = null;

    private final byte[] tempBuf = new byte[1];

    
    public DeltaInputStream(InputStream in, int distance) {


        if (in == null)
            throw new NullPointerException();

        this.in = in;
        this.delta = new DeltaDecoder(distance);
    }

    
    public int read() throws IOException {
        return read(tempBuf, 0, 1) == -1 ? -1 : (tempBuf[0] & 0xFF);
    }

    
    public int read(byte[] buf, int off, int len) throws IOException {
        if (len == 0)
            return 0;

        if (in == null)
            throw new XZIOException("Stream closed");

        if (exception != null)
            throw exception;

        int size;
        try {
            size = in.read(buf, off, len);
        } catch (IOException e) {
            exception = e;
            throw e;
        }

        if (size == -1)
            return -1;

        delta.decode(buf, off, size);
        return size;
    }

    
    public int available() throws IOException {
        if (in == null)
            throw new XZIOException("Stream closed");

        if (exception != null)
            throw exception;

        return in.available();
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
