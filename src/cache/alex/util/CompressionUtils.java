package cache.alex.util;

import cache.alex.util.bzip2.CBZip2InputStream;
import cache.alex.util.bzip2.CBZip2OutputStream;
import cache.openrs.util.tukaani.LZMAInputStream;

import java.io.*;
import java.util.zip.GZIPInputStream;
import java.util.zip.GZIPOutputStream;
import java.util.zip.ZipException;


public final class CompressionUtils {

    
    public static byte[] bunzip2(byte[] bytes) throws IOException {
        
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

            
            bytes = bout.toByteArray();
            byte[] bzip2 = new byte[bytes.length - 2];
            System.arraycopy(bytes, 2, bzip2, 0, bzip2.length);
            return bzip2;
        } finally {
            is.close();
        }
    }

    
    public static byte[] gunzip(byte[] bytes) throws IOException {
        
        InputStream is = new GZIPInputStream(new ByteArrayInputStream(bytes));
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

    
    public static byte[] gzip(byte[] bytes) throws IOException {
        
        InputStream is = new ByteArrayInputStream(bytes);
        try {
            ByteArrayOutputStream bout = new ByteArrayOutputStream();
            OutputStream os = new GZIPOutputStream(bout);
            try {
                
                byte[] buf = new byte[4096];
                int len = 0;
                while ((len = is.read(buf, 0, buf.length)) != -1) {
                    os.write(buf, 0, len);
                }
            } finally {
                os.close();
            }

            
            return bout.toByteArray();
        } finally {
            is.close();
        }
    }

    
    public static byte[] unlzma(byte[] bytes, int size) throws IOException {
        
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

    
    private CompressionUtils() {

    }

}