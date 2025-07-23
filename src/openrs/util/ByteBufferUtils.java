package openrs.util;

import openrs.util.crypto.Whirlpool;

import java.nio.ByteBuffer;
import java.util.Arrays;
import java.util.Map;
import java.util.zip.CRC32;
import java.util.zip.Checksum;

/**
 * Contains {@link ByteBuffer}-related utility methods.
 *
 * @author Graham
 * @author `Discardedx2
 */
public final class ByteBufferUtils {

    /**
     * The modified set of 'extended ASCII' characters used by the client.
     */
    private static char CHARACTERS[] = {'\u20AC', '\0', '\u201A', '\u0192', '\u201E', '\u2026', '\u2020', '\u2021', '\u02C6', '\u2030', '\u0160', '\u2039', '\u0152', '\0', '\u017D', '\0', '\0', '\u2018', '\u2019', '\u201C', '\u201D', '\u2022', '\u2013', '\u2014', '\u02DC', '\u2122', '\u0161', '\u203A', '\u0153', '\0', '\u017E', '\u0178'};

    /**
     * Gets a null-terminated string from the specified buffer, using a
     * modified ISO-8859-1 character set.
     *
     * @param buf The buffer.
     * @return The decoded string.
     */
    public static String getJagexString(ByteBuffer buf) {
        StringBuilder bldr = new StringBuilder();
        int b;
        while ((b = buf.get()) != 0) {
            if (b >= 127 && b < 160) {
                char curChar = CHARACTERS[b - 128];
                if (curChar != 0) {
                    bldr.append(curChar);
                }
            } else {
                bldr.append((char) b);
            }
        }
        return bldr.toString();
    }

    /**
     * Converts a byte array segment to a String using a modified ISO-8859-1 charset.
     * Bytes in the range 128-159 are mapped via a custom character array.
     *
     * @param buffer the byte array containing encoded characters
     * @param off    the starting offset in the byte array
     * @param len    the number of bytes to read
     * @return the decoded String
     */
    public static final String getStringFromBytes(byte buffer[], int off, int len) {
        char ac[] = new char[len];
        int l = 0;
        for (int i1 = 0; len > i1; i1++) {
            int j1 = 0xff & buffer[off + i1];
            if (j1 != 0) {
                if (j1 >= 128 && j1 < 160) {
                    char c = CHARACTERS[-128 + j1];
                    if (c == 0) {
                        c = '?';
                    }
                    j1 = c;
                }
                ac[l++] = (char) j1;
            }
        }
        return new String(ac, 0, l);
    }

    /**
     * Encodes a substring of a string into a byte array using a custom mapping.
     *
     * @param s      the source string
     * @param strOff start index in the string
     * @param strLen end index in the string
     * @param buffer output byte array
     * @param bufOff start index in the byte array
     * @return number of bytes written
     */
    public static final int getStringBytes(String s, int strOff, int strLen, byte buffer[], int bufOff) {
        int l = -strOff + strLen;
        for (int i1 = 0; i1 < l; i1++) {
            char c = s.charAt(strOff + i1);
            if (c > '\0' && c < '\200' || c >= '\240' && c <= '\377') {
                buffer[i1 + bufOff] = (byte) c;
            } else if (c == '\u20AC') {
                buffer[i1 + bufOff] = -128;
            } else if (c != '\u201A') {
                if (c == '\u0192') {
                    buffer[bufOff + i1] = -125;
                } else if (c != '\u201E') {
                    if (c == '\u2026') {
                        buffer[bufOff + i1] = -123;
                    } else if (c != '\u2020') {
                        if (c == '\u2021') {
                            buffer[bufOff + i1] = -121;
                        } else if (c != '\u02C6') {
                            if (c == '\u2030') {
                                buffer[i1 + bufOff] = -119;
                            } else if (c == '\u0160') {
                                buffer[bufOff + i1] = -118;
                            } else if (c == '\u2039') {
                                buffer[i1 + bufOff] = -117;
                            } else if (c == '\u0152') {
                                buffer[i1 + bufOff] = -116;
                            } else if (c == '\u017D') {
                                buffer[i1 + bufOff] = -114;
                            } else if (c == '\u2018') {
                                buffer[i1 + bufOff] = -111;
                            } else if (c == '\u2019') {
                                buffer[i1 + bufOff] = -110;
                            } else if (c == '\u201C') {
                                buffer[bufOff + i1] = -109;
                            } else if (c == '\u201D') {
                                buffer[i1 + bufOff] = -108;
                            } else if (c == '\u2022') {
                                buffer[i1 + bufOff] = -107;
                            } else if (c != '\u2013') {
                                if (c == '\u2014') {
                                    buffer[i1 + bufOff] = -105;
                                } else if (c != '\u02DC') {
                                    if (c != '\u2122') {
                                        if (c == '\u0161') {
                                            buffer[i1 + bufOff] = -102;
                                        } else if (c == '\u203A') {
                                            buffer[bufOff + i1] = -101;
                                        } else if (c == '\u0153') {
                                            buffer[bufOff + i1] = -100;
                                        } else if (c == '\u017E') {
                                            buffer[bufOff + i1] = -98;
                                        } else if (c == '\u0178') {
                                            buffer[i1 + bufOff] = -97;
                                        } else {
                                            buffer[i1 + bufOff] = 63;
                                        }
                                    } else {
                                        buffer[bufOff + i1] = -103;
                                    }
                                } else {
                                    buffer[i1 + bufOff] = -104;
                                }
                            } else {
                                buffer[i1 + bufOff] = -106;
                            }
                        } else {
                            buffer[i1 + bufOff] = -120;
                        }
                    } else {
                        buffer[bufOff + i1] = -122;
                    }
                } else {
                    buffer[i1 + bufOff] = -124;
                }
            } else {
                buffer[bufOff + i1] = -126;
            }
        }
        return l;
    }

    /**
     * Reads a 'tri-byte' from the specified buffer.
     *
     * @param buf The buffer.
     * @return The value.
     */
    public static int getTriByte(ByteBuffer buf) {
        return ((buf.get() & 0xFF) << 16) | ((buf.get() & 0xFF) << 8) | (buf.get() & 0xFF);
    }

    /**
     * Writes a 'tri-byte' to the specified buffer.
     *
     * @param buf   The buffer.
     * @param value The value.
     */
    public static void putTriByte(ByteBuffer buf, int value) {
        buf.put((byte) (value >> 16));
        buf.put((byte) (value >> 8));
        buf.put((byte) value);
    }

    /**
     * Calculates the CRC32 checksum of the specified buffer.
     *
     * @param buffer The buffer.
     * @return The CRC32 checksum.
     */
    public static int getCrcChecksum(ByteBuffer buffer) {
        Checksum crc = new CRC32();
        for (int i = 0; i < buffer.limit(); i++) {
            crc.update(buffer.get(i));
        }
        return (int) crc.getValue();
    }

    /**
     * Calculates the whirlpool digest of the specified buffer.
     *
     * @param buf The buffer.
     * @return The 64-byte whirlpool digest.
     */
    public static byte[] getWhirlpoolDigest(ByteBuffer buf) {
        byte[] bytes = new byte[buf.limit()];
        buf.get(bytes);
        return Whirlpool.whirlpool(bytes, 0, bytes.length);
    }

    /**
     * Converts the contents of the specified byte buffer to a string, which is
     * formatted similarly to the output of the {@link Arrays#toString()}
     * method.
     *
     * @param buffer The buffer.
     * @return The string.
     */
    public static String toString(ByteBuffer buffer) {
        StringBuilder builder = new StringBuilder("[");
        for (int i = 0; i < buffer.limit(); i++) {
            String hex = Integer.toHexString(buffer.get(i) & 0xFF).toUpperCase();
            if (hex.length() == 1) hex = "0" + hex;

            builder.append("0x").append(hex);
            if (i != buffer.limit() - 1) {
                builder.append(", ");
            }
        }
        builder.append("]");
        return builder.toString();
    }

    /**
     * Writes a Jagex-style string to the given ByteBuffer.
     * <p>
     * This method writes the characters of the string one by one,
     * followed by a terminating zero byte.
     *
     * @param buffer The ByteBuffer to write to.
     * @param s      The string to write.
     */
    public static void putJagexString(ByteBuffer buffer, String s) {
        for (int i = 0; i < s.length(); i++) {
            buffer.put((byte) s.charAt(i));
        }
        buffer.put((byte) 0);
    }

    /**
     * Writes a parameter map to the ByteBuffer in Jagex format.
     * <p>
     * The map entries are written as:
     * - Number of entries (byte)
     * - For each entry:
     * - A byte indicating if the value is a String (1) or an Integer (0)
     * - The 3-byte key (tri-byte)
     * - The value: either a Jagex-style string or a 4-byte integer
     * </p>
     *
     * @param buffer The ByteBuffer to write to.
     * @param params The parameter map to write. Keys are integers, values are either String or Integer.
     */
    public static void putParams(ByteBuffer buffer, Map<Integer, Object> params) {
        if (params == null) {
            buffer.put((byte) 0);
            return;
        }
        buffer.put((byte) params.size());
        for (Map.Entry<Integer, Object> entry : params.entrySet()) {
            Object value = entry.getValue();
            if (value instanceof String) {
                buffer.put((byte) 1);
                putTriByte(buffer, entry.getKey());
                putJagexString(buffer, (String) value);
            } else if (value instanceof Integer) {
                buffer.put((byte) 0);
                putTriByte(buffer, entry.getKey());
                buffer.putInt((Integer) value);
            } else {
                throw new IllegalArgumentException("Unsupported parameter type: " + value.getClass());
            }
        }
    }

    /**
     * Default private constructor to prevent instantiation.
     */
    private ByteBufferUtils() {

    }

    public static int getSmartInt(ByteBuffer buffer) {
        if (buffer.get(buffer.position()) < 0) {
            return buffer.getInt() & 0x7fffffff;
        }
        int shortValue = buffer.getShort() & 0xFFFF;
        return shortValue == 32767 ? -1 : shortValue;
    }

    /**
     * Gets a unsigned smart from the buffer.
     *
     * @return The value.
     */
    public static int getUnsignedSmart(ByteBuffer buf) {
        int peek = buf.get(buf.position()) & 0xFF;
        if (peek < 128) return buf.get() & 0xFF;
        else return (buf.getShort() & 0xFFFF) - 32768;
    }
}
