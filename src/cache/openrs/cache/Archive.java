package cache.openrs.cache;

import java.io.ByteArrayOutputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.nio.ByteBuffer;


public class Archive {

    
    public static Archive decode(ByteBuffer buffer, int size) {
        
        Archive archive = new Archive(size);

        
        buffer.position(buffer.limit() - 1);
        int chunks = buffer.get() & 0xFF;

        
        int[][] chunkSizes = new int[chunks][size];
        int[] sizes = new int[size];
        buffer.position(buffer.limit() - 1 - chunks * size * 4);
        for (int chunk = 0; chunk < chunks; chunk++) {
            int chunkSize = 0;
            for (int id = 0; id < size; id++) {
                
                int delta = buffer.getInt();
                chunkSize += delta;

                chunkSizes[chunk][id] = chunkSize; 
                sizes[id] += chunkSize; 
            }
        }

        
        for (int id = 0; id < size; id++) {
            archive.entries[id] = ByteBuffer.allocate(sizes[id]);
        }

        
        buffer.position(0);
        for (int chunk = 0; chunk < chunks; chunk++) {
            for (int id = 0; id < size; id++) {
                
                int chunkSize = chunkSizes[chunk][id];

                
                byte[] temp = new byte[chunkSize];
                buffer.get(temp);

                
                archive.entries[id].put(temp);
            }
        }

        
        for (int id = 0; id < size; id++) {
            archive.entries[id].flip();
        }

        
        return archive;
    }

    
    private final ByteBuffer[] entries;

    
    public Archive(int size) {
        this.entries = new ByteBuffer[size];
    }

    
    public ByteBuffer encode() throws IOException {
        ByteArrayOutputStream bout = new ByteArrayOutputStream();
        DataOutputStream os = new DataOutputStream(bout);
        try {
            
            for (int id = 0; id < entries.length; id++) {
                
                byte[] temp = new byte[entries[id].limit()];
                entries[id].position(0);
                try {
                    entries[id].get(temp);
                } finally {
                    entries[id].position(0);
                }

                
                os.write(temp);
            }

            
            int prev = 0;
            for (int id = 0; id < entries.length; id++) {
                
                int chunkSize = entries[id].limit();
                os.writeInt(chunkSize - prev);
                prev = chunkSize;
            }

            
            bout.write(1);

            
            byte[] bytes = bout.toByteArray();
            return ByteBuffer.wrap(bytes);
        } finally {
            os.close();
        }
    }

    
    public int size() {
        return entries.length;
    }

    
    public ByteBuffer getEntry(int id) {
        return entries[id];
    }

    
    public void putEntry(int id, ByteBuffer buffer) {
        entries[id] = buffer;
    }

}