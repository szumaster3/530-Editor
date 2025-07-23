package com.cache.store;

import com.cache.io.ByteStream;
import launcher.Main;

import java.io.*;

public class FileOperations {
    public static int TotalRead;
    public static int TotalWrite;
    public static int CompleteWrite;

    public static final ByteStream ReadFileStream(String paramString) {
        return new ByteStream(ReadFile(paramString));
    }

    public static final void WriteFileStream(String paramString, ByteStream paramByteStream) {
        WriteFile(paramString, paramByteStream.Buffer);
    }

    public static final String ReadFileText(String paramString) {
        return new String(ReadFile(paramString));
    }

    public static void WriteFileText(String paramString, byte[] paramArrayOfByte) {
        WriteFile(paramString, paramString.getBytes());
    }

    public static final byte[] ReadFile(String paramString) {
        try {
            File localFile = new File(paramString);
            int i = (int) localFile.length();
            byte[] arrayOfByte = new byte[i];
            DataInputStream localDataInputStream = new DataInputStream(new BufferedInputStream(new FileInputStream(paramString)));
            localDataInputStream.readFully(arrayOfByte, 0, i);
            localDataInputStream.close();
            TotalRead += 1;
            return arrayOfByte;
        } catch (Throwable localThrowable) {
            Main.log("Extractor", "Read Error: " + paramString);
        }
        return null;
    }

    public static final void WriteFile(String paramString, byte[] paramArrayOfByte) {
        try {
            new File(new File(paramString).getParent()).mkdirs();
            FileOutputStream localFileOutputStream = new FileOutputStream(paramString);
            localFileOutputStream.write(paramArrayOfByte, 0, paramArrayOfByte.length);
            localFileOutputStream.close();
            TotalWrite += 1;
            CompleteWrite += 1;
        } catch (Throwable localThrowable) {
            Main.log("Extractor", "Write Error: " + paramString);
        }
    }
}