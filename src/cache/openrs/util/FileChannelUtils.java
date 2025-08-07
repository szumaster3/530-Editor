package cache.openrs.util;

import java.io.EOFException;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.channels.FileChannel;


public final class FileChannelUtils {

    
    public static void readFully(FileChannel channel, ByteBuffer buffer, long ptr) throws IOException {
        while (buffer.remaining() > 0) {
            long read = channel.read(buffer, ptr);
            if (read == -1L) {
                throw new EOFException();
            } else {
                ptr += read;
            }
        }
    }

    
    private FileChannelUtils() {

    }

}
