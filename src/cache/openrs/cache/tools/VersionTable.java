package cache.openrs.cache.tools;

import cache.openrs.cache.Cache;
import cache.openrs.cache.Constants;
import cache.openrs.cache.FileStore;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.ByteBuffer;


public final class VersionTable {

    public static void main(String[] args) {
        try (Cache cache = new Cache(FileStore.open(Constants.CACHE_PATH))) {
            ByteBuffer table = cache.createChecksumTable().encode();

            ByteBuffer buf = ByteBuffer.allocate(table.limit() + 8);
            buf.put((byte) 0xFF);
            buf.putShort((short) 0xFF);
            buf.put((byte) 0);
            buf.putInt(table.limit());
            buf.put(table);
            buf.flip();

            saveToTextFile(buf, "dumps/version_table.txt");
            System.out.println("Version table saved to dumps/version_table.txt");
        } catch (IOException e) {
            System.err.println("Failed to generate or save version table:");
            e.printStackTrace();
        }
    }

    
    private static void saveToTextFile(ByteBuffer buf, String filePath) throws IOException {
        File outputFile = new File(filePath);
        File parentDir = outputFile.getParentFile();
        if (!parentDir.exists()) {
            parentDir.mkdirs();
        }

        try (BufferedWriter writer = new BufferedWriter(new FileWriter(outputFile))) {
            writer.write("public static final int[] VERSION_TABLE = new int[] {\n");
            writer.write("    ");
            for (int i = 0; i < buf.limit(); i++) {
                String hex = String.format("0x%02X", buf.get(i));
                writer.write(hex + ", ");
                if ((i + 1) % 8 == 0 && i != buf.limit() - 1) {
                    writer.write("\n    ");
                }
            }
            writer.write("\n};\n");
        }
    }
}