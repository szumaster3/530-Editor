package openrs.cache.util;

import com.cache.util.bzip2.CBZip2InputStream;
import com.cache.util.bzip2.CBZip2OutputStream;
import tukaani.LZMAInputStream;

import java.io.*;
import java.util.zip.GZIPInputStream;
import java.util.zip.GZIPOutputStream;
import java.util.zip.ZipException;

/**
 * A class that contains methods to compress and uncompress.
 *
 * @author Graham
 * @author `Discardedx2
 */
public final class CompressionUtils {

    /**
     * Uncompresses a BZIP2 file.
     *
     * @param bytes The compressed bytes without the header.
     * @return The uncompressed bytes.
     * @throws IOException if an I/O error occurs.
     */
    public static byte[] bunzip2(byte[] bytes) throws IOException {
        /* prepare a new byte array with the bzip2 header at the start */
        byte[] bzip2 = new byte[bytes.length + 2];
        bzip2[0] = 'h';
        bzip2[1] = '1';
        System.arraycopy(bytes, 0, bzip2, 2, bytes.length);

        InputStream is = new CBZip2InputStream(new ByteArrayInputStream(bzip2));
        try {
            ByteArrayOutputStream os = new ByteArrayOutputStream();
            try {
                byte[] buf = new byte[4096];
                int len = 0;
                while ((len = is.read(buf, 0, buf.length)) != -1) {
                    os.write(buf, 0, len);
                }
            } finally {
                os.close();
            }

            return os.toByteArray();
        } finally {
            is.close();
        }
    }

    /**
     * Compresses a BZIP2 file.
     *
     * @param bytes The uncompressed bytes.
     * @return The compressed bytes without the header.
     * @throws IOException if an I/O erorr occurs.
     */
    public static byte[] bzip2(byte[] bytes) throws IOException {
        InputStream is = new ByteArrayInputStream(bytes);
        try {
            ByteArrayOutputStream bout = new ByteArrayOutputStream();
            OutputStream os = new CBZip2OutputStream(bout, 1);
            try {
                byte[] buf = new byte[4096];
                int len = 0;
                while ((len = is.read(buf, 0, buf.length)) != -1) {
                    os.write(buf, 0, len);
                }
            } finally {
                os.close();
            }

            /* strip the header from the byte array and return it */
            bytes = bout.toByteArray();
            byte[] bzip2 = new byte[bytes.length - 2];
            System.arraycopy(bytes, 2, bzip2, 0, bzip2.length);
            return bzip2;
        } finally {
            is.close();
        }
    }

    /**
     * Uncompresses a GZIP file.
     *
     * @param bytes The compressed bytes.
     * @return The uncompressed bytes.
     * @throws IOException if an I/O error occurs.
     */
    public static byte[] gunzip(byte[] bytes) throws IOException {
        /* create the streams */
        InputStream is = new GZIPInputStream(new ByteArrayInputStream(bytes));
        try {
            ByteArrayOutputStream os = new ByteArrayOutputStream();
            try {
                /* copy data between the streams */
                byte[] buf = new byte[4096];
                int len = 0;
                while ((len = is.read(buf, 0, buf.length)) != -1) {
                    os.write(buf, 0, len);
                }
            } finally {
                os.close();
            }

            /* return the uncompressed bytes */
            return os.toByteArray();
        } finally {
            is.close();
        }
    }

    /**
     * Compresses a GZIP file.
     *
     * @param bytes The uncompressed bytes.
     * @return The compressed bytes.
     * @throws IOException if an I/O error occurs.
     */
    public static byte[] gzip(byte[] bytes) throws IOException {
        /* create the streams */
        InputStream is = new ByteArrayInputStream(bytes);
        try {
            ByteArrayOutputStream bout = new ByteArrayOutputStream();
            OutputStream os = new GZIPOutputStream(bout);
            try {
                /* copy data between the streams */
                byte[] buf = new byte[4096];
                int len = 0;
                while ((len = is.read(buf, 0, buf.length)) != -1) {
                    os.write(buf, 0, len);
                }
            } finally {
                os.close();
            }

            /* return the compressed bytes */
            return bout.toByteArray();
        } finally {
            is.close();
        }
    }

    /**
     * Uncompresses a LZMA file.
     *
     * @param bytes The compressed bytes without the header.
     * @return The uncompressed bytes.
     * @throws IOException if an I/O error occurs.
     */
    public static byte[] unlzma(byte[] bytes, int size) throws IOException {
        /* prepare a new byte array with the lzma header at the start */
        byte[] lzma = new byte[bytes.length + 8];
        System.arraycopy(bytes, 0, lzma, 0, 5);
        lzma[5] = (byte) (size >>> 0);
        lzma[6] = (byte) (size >>> 8);
        lzma[7] = (byte) (size >>> 16);
        lzma[8] = (byte) (size >>> 24);
        lzma[9] = lzma[10] = lzma[11] = lzma[12] = 0;
        System.arraycopy(bytes, 5, lzma, 13, bytes.length - 5);

        InputStream is = new LZMAInputStream(new ByteArrayInputStream(lzma));
        try {
            ByteArrayOutputStream os = new ByteArrayOutputStream();
            try {
                byte[] buf = new byte[4096];
                int len = 0;
                while ((len = is.read(buf, 0, buf.length)) != -1) {
                    os.write(buf, 0, len);
                }
            } finally {
                os.close();
            }

            return os.toByteArray();
        } finally {
            is.close();
        }
    }

    /**
     * Decompresses data using GZIP or BZIP2; returns original if both fail.
     *
     * @param data compressed bytes
     * @return decompressed bytes or original data
     * @throws IOException on I/O error
     */
    public static byte[] decompressData(byte[] data) throws IOException {
        try {
            return CompressionUtils.gunzip(data);
        } catch (ZipException e) {
            try {
                return CompressionUtils.bunzip2(data);
            } catch (Exception ex) {
                return data;
            }
        }
    }

    /**
     * Default private constructor to prevent instantiation.
     */
    private CompressionUtils() {

    }

}