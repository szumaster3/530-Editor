
package cache.openrs.util.crypto;

import java.nio.ByteBuffer;


public final class Xtea {

    
    public static final int GOLDEN_RATIO = 0x9E3779B9;

    
    public static final int ROUNDS = 32;

    
    public static void decipher(ByteBuffer buffer, int start, int end, int[] key) {
        if (key.length != 4) throw new IllegalArgumentException();

        int numQuads = (end - start) / 8;
        for (int i = 0; i < numQuads; i++) {
            int sum = GOLDEN_RATIO * ROUNDS;
            int v0 = buffer.getInt(start + i * 8);
            int v1 = buffer.getInt(start + i * 8 + 4);
            for (int j = 0; j < ROUNDS; j++) {
                v1 -= (((v0 << 4) ^ (v0 >>> 5)) + v0) ^ (sum + key[(sum >>> 11) & 3]);
                sum -= GOLDEN_RATIO;
                v0 -= (((v1 << 4) ^ (v1 >>> 5)) + v1) ^ (sum + key[sum & 3]);
            }
            buffer.putInt(start + i * 8, v0);
            buffer.putInt(start + i * 8 + 4, v1);
        }
    }

    
    public static void encipher(ByteBuffer buffer, int start, int end, int[] key) {
        if (key.length != 4) throw new IllegalArgumentException();

        int numQuads = (end - start) / 8;
        for (int i = 0; i < numQuads; i++) {
            int sum = 0;
            int v0 = buffer.getInt(start + i * 8);
            int v1 = buffer.getInt(start + i * 8 + 4);
            for (int j = 0; j < ROUNDS; j++) {
                v0 += (((v1 << 4) ^ (v1 >>> 5)) + v1) ^ (sum + key[sum & 3]);
                sum += GOLDEN_RATIO;
                v1 += (((v0 << 4) ^ (v0 >>> 5)) + v0) ^ (sum + key[(sum >>> 11) & 3]);
            }
            buffer.putInt(start + i * 8, v0);
            buffer.putInt(start + i * 8 + 4, v1);
        }
    }

    
    private Xtea() {

    }

}
