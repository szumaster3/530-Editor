package com.cache.util;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.nio.ByteBuffer;

public class XTEA {
    private static final int DELTA = 0x9E3779B9;
    private static final int GOLDEN_RATIO = 0x9E3779B9;
    private static final int NUM_ROUNDS = 32;
    private static final int SUM = DELTA * NUM_ROUNDS;

    private XTEA() {
    }

    /**
     * Decrypts a byte array using XTEA.
     *
     * @param buffer Data to decrypt.
     * @param key Key (must have 4 ints).
     * @param start Start index (default 0).
     */
    public static void decipher(byte[] buffer, int[] key, int start) {
        if (key.length != 4) {
            throw new IllegalArgumentException("Key must contain exactly 4 integers.");
        }

        int numQuads = buffer.length / 8;
        for (int i = 0; i < numQuads; i++) {
            int sum = GOLDEN_RATIO * NUM_ROUNDS;
            int v0 = getInt(buffer, start + i * 8);
            int v1 = getInt(buffer, start + i * 8 + 4);
            for (int j = 0; j < NUM_ROUNDS; j++) {
                v1 -= ((v0 << 4 ^ v0 >>> 5) + v0) ^ (sum + key[(sum >>> 11) & 3]);
                sum -= GOLDEN_RATIO;
                v0 -= ((v1 << 4 ^ v1 >>> 5) + v1) ^ (sum + key[sum & 3]);
            }
            putInt(buffer, start + i * 8, v0);
            putInt(buffer, start + i * 8 + 4, v1);
        }
    }

    /**
     * Overload for decipher starting at zero.
     */
    public static void decipher(byte[] buffer, int[] key) {
        decipher(buffer, key, 0);
    }

    /**
     * Encrypts a byte array using XTEA.
     *
     * @param buffer Data to encrypt.
     * @param start Start index.
     * @param end End index (exclusive).
     * @param key Key (must have 4 ints).
     */
    public static void encipher(byte[] buffer, int start, int end, int[] key) {
        if (key.length != 4) {
            throw new IllegalArgumentException("Key must contain exactly 4 integers.");
        }

        int numQuads = (end - start) / 8;
        for (int i = 0; i < numQuads; i++) {
            int sum = 0;
            int v0 = getInt(buffer, start + i * 8);
            int v1 = getInt(buffer, start + i * 8 + 4);
            for (int j = 0; j < NUM_ROUNDS; j++) {
                v0 += ((v1 << 4 ^ v1 >>> 5) + v1) ^ (sum + key[sum & 3]);
                sum += GOLDEN_RATIO;
                v1 += ((v0 << 4 ^ v0 >>> 5) + v0) ^ (sum + key[(sum >>> 11) & 3]);
            }
            putInt(buffer, start + i * 8, v0);
            putInt(buffer, start + i * 8 + 4, v1);
        }
    }

    /**
     * Gets an int from a byte array at index (big-endian).
     */
    private static int getInt(byte[] buffer, int index) {
        return ((buffer[index] & 0xFF) << 24)
                | ((buffer[index + 1] & 0xFF) << 16)
                | ((buffer[index + 2] & 0xFF) << 8)
                | (buffer[index + 3] & 0xFF);
    }

    /**
     * Puts an int into a byte array at index (big-endian).
     */
    private static void putInt(byte[] buffer, int index, int value) {
        buffer[index] = (byte) (value >>> 24);
        buffer[index + 1] = (byte) (value >>> 16);
        buffer[index + 2] = (byte) (value >>> 8);
        buffer[index + 3] = (byte) value;
    }

    /**
     * Decrypts a ByteBuffer.
     */
    public static ByteBuffer decrypt(int[] keys, ByteBuffer buffer) {
        return decrypt(keys, buffer, buffer.position(), buffer.limit());
    }

    /**
     * Decrypts a ByteBuffer from offset to length.
     */
    public static ByteBuffer decrypt(int[] keys, ByteBuffer buffer, int offset, int length) {
        int numBlocks = (length - offset) / 8;
        int[] block = new int[2];
        for (int i = 0; i < numBlocks; i++) {
            int index = offset + i * 8;
            block[0] = buffer.getInt(index);
            block[1] = buffer.getInt(index + 4);
            decipher(keys, block);
            buffer.putInt(index, block[0]);
            buffer.putInt(index + 4, block[1]);
        }
        return buffer;
    }

    /**
     * Deciphers a 2-int block with keys.
     */
    private static void decipher(int[] keys, int[] block) {
        int sum = SUM;
        for (int i = 0; i < NUM_ROUNDS; i++) {
            block[1] -= ((keys[(sum >>> 11) & 3] + sum) ^ ((block[0] << 4) ^ (block[0] >>> 5)) + block[0]);
            sum -= DELTA;
            block[0] -= ((keys[sum & 3] + sum) ^ ((block[1] << 4) ^ (block[1] >>> 5)) + block[1]);
        }
    }

    /**
     * Encrypts a ByteBuffer.
     */
    public static void encrypt(int[] keys, ByteBuffer buffer) {
        encrypt(keys, buffer, buffer.position(), buffer.limit());
    }

    /**
     * Encrypts a ByteBuffer from offset to length.
     */
    public static void encrypt(int[] keys, ByteBuffer buffer, int offset, int length) {
        int numBlocks = (length - offset) / 8;
        int[] block = new int[2];
        for (int i = 0; i < numBlocks; i++) {
            int index = offset + i * 8;
            block[0] = buffer.getInt(index);
            block[1] = buffer.getInt(index + 4);
            encipher(keys, block);
            buffer.putInt(index, block[0]);
            buffer.putInt(index + 4, block[1]);
        }
    }

    /**
     * Enciphers a 2-int block with keys.
     */
    private static void encipher(int[] keys, int[] block) {
        int sum = 0;
        for (int i = 0; i < NUM_ROUNDS; i++) {
            block[0] += ((keys[sum & 3] + sum) ^ ((block[1] << 4) ^ (block[1] >>> 5)) + block[1]);
            sum += DELTA;
            block[1] += ((keys[(sum >>> 11) & 3] + sum) ^ ((block[0] << 4) ^ (block[0] >>> 5)) + block[0]);
        }
    }
}