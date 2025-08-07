

package cache.openrs.util.tukaani;

import java.io.IOException;
import java.io.InputStream;


public abstract class SeekableInputStream extends InputStream {
    
    public long skip(long n) throws IOException {
        if (n <= 0)
            return 0;

        long size = length();
        long pos = position();
        if (pos >= size)
            return 0;

        if (size - pos < n)
            n = size - pos;

        seek(pos + n);
        return n;
    }

    
    public abstract long length() throws IOException;

    
    public abstract long position() throws IOException;

    
    public abstract void seek(long pos) throws IOException;
}
