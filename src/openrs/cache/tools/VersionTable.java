package openrs.cache.tools;

import openrs.cache.Cache;
import openrs.cache.Constants;
import openrs.cache.FileStore;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.ByteBuffer;

/**
 * Generates the update version table.
 * Originally by Discardedx2 & Graham.
 */
public final class VersionTable {

    public static void main(String[] args) {
        try (Cache cache = new Cache(FileStore.open(Constants.CACHE_PATH))) {
            ByteBuffer table = cache.createChecksumTable().encode();

            ByteBuffer buf = ByteBuffer.allocate(table.limit() + 8);
            buf.put((byte) 0xFF); // Type
            buf.putShort((short) 0xFF); // File id
            buf.put((byte) 0); // Compression
            buf.putInt(table.limit()); // Payload size
            buf.put(table);
            buf.flip();

            saveToTextFile(buf, "dumps/version_table.txt");
            System.out.println("Version table saved to dumps/version_table.txt");
        } catch (IOException e) {
            System.err.println("Failed to generate or save version table:");
            e.printStackTrace();
        }
    }

    /**
     * Saves the contents of the buffer as a formatted Java-style hex array to a file.
     *
     * @param buf      the buffer to write
     * @param filePath the path to the output file
     * @throws IOException if the file cannot be written
     */
    private static void saveToTextFile(ByteBuffer buf, String filePath) throws IOException {
        File outputFile = new File(filePath);
        File parentDir = outputFile.getParentFile();
        if (!parentDir.exists()) {
            parentDir.mkdirs(); // Create dumps/ if it doesn't exist
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