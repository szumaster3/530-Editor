package com.cache.util.whirlpool;

import java.nio.ByteBuffer;

/**
 * An implementation of the XTEA block cipher.
 *
 * @author Graham
 * @author `Discardedx2
 */
public final class Xtea {

    /**
     * The golden ratio.
     */
    public static final int GOLDEN_RATIO = 0x9E3779B9;

    /**
     * The number of rounds.
     */
    public static final int ROUNDS = 32;

    /**
     * Default private constructor to prevent instantiation.
     */
    private Xtea() {

    }

    /**
     * Deciphers the specified {@link ByteBuffer} with the given key.
     *
     * @param buffer The buffer.
     * @param key    The key.
     * @throws IllegalArgumentException if the key is not exactly 4 elements
     *                                  long.
     */
    public static void decipher(ByteBuffer buffer, int[] key) {
        if (key.length != 4) throw new IllegalArgumentException();

        for (int i = 0; i < buffer.limit(); i += 8) {
            int sum = GOLDEN_RATIO * ROUNDS;
            int v0 = buffer.getInt(i * 4);
            int v1 = buffer.getInt(i * 4 + 4);
            for (int j = 0; j < ROUNDS; j++) {
                v1 = (((v0 << 4) ^ (v0 >> 5)) + v0) ^ (sum + key[(sum >> 11) & 3]);
                sum -= GOLDEN_RATIO;
                v0 = (((v1 << 4) ^ (v1 >> 5)) + v1) ^ (sum + key[sum & 3]);
            }
            buffer.putInt(i * 4, v0);
            buffer.putInt(i * 4 + 4, v1);
        }
    }

    /**
     * Enciphers the specified {@link ByteBuffer} with the given key.
     *
     * @param buffer The buffer.
     * @param key    The key.
     * @throws IllegalArgumentException if the key is not exactly 4 elements
     *                                  long.
     */
    public static void encipher(ByteBuffer buffer, int[] key) {
        if (key.length != 4) throw new IllegalArgumentException();

        for (int i = 0; i < buffer.limit(); i += 8) {
            int sum = 0;
            int v0 = buffer.getInt(i * 4);
            int v1 = buffer.getInt(i * 4 + 4);
            for (int j = 0; j < ROUNDS; j++) {
                v0 = (((v1 << 4) ^ (v1 >> 5)) + v1) ^ (sum + key[sum & 3]);
                sum += GOLDEN_RATIO;
                v1 = (((v0 << 4) ^ (v0 >> 5)) + v0) ^ (sum + key[(sum >> 11) & 3]);
            }
            buffer.putInt(i * 4, v0);
            buffer.putInt(i * 4 + 4, v1);
        }
    }

    public static void decipher(int[] key, ByteBuffer buffer, int start, int end) {
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

    public static byte[] decipher(int[] key, byte[] data, int offset, int length) {
        int numBlocks = (length - offset) / 8;
        ByteBuffer buffer = ByteBuffer.wrap(data);
        buffer.position(offset);
        for (int i = 0; i < numBlocks; i++) {
            int y = buffer.getInt();
            int z = buffer.getInt();
            int sum = -957401312;
            int delta = -1640531527;
            int numRounds = 32;
            while (numRounds > 0) {
                z -= ((y >>> 5 ^ y << 4) + y ^ sum + key[sum >>> 11 & 0x56c00003]);
                sum -= delta;
                y -= ((z >>> 5 ^ z << 4) - -z ^ sum + key[sum & 0x3]);
                numRounds--;
            }
            buffer.position(buffer.position() - 8);
            buffer.putInt(y);
            buffer.putInt(z);
        }
        return buffer.array();
    }

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


}
