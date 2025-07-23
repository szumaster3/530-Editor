package openrs.cache.tools;

import openrs.cache.*;
import openrs.cache.util.CompressionUtils;

import java.io.DataOutputStream;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;

/**
 * Dumps all models from the cache into .gz compressed files.
 */
public class ModelDumper {

    /**
     * Entry point for dumping models.
     *
     * @param args not used
     * @throws IOException if cache loading or writing fails
     */
    public static void main(String[] args) throws IOException {
        File outputDir = new File(Constants.MODEL_PATH);

        if (!outputDir.exists()) {
            outputDir.mkdirs();
        }

        try (Cache cache = new Cache(FileStore.open(Constants.CACHE_PATH))) {
            ReferenceTable table = cache.getReferenceTable(7);
            int total = table.capacity();

            for (int fileId = 0; fileId < total; fileId++) {
                if (table.getEntry(fileId) == null)
                    continue;

                Container container = cache.read(7, fileId);
                byte[] uncompressedData = new byte[container.getData().limit()];
                container.getData().get(uncompressedData);

                File outputFile = new File(outputDir, fileId + ".gz");
                try (DataOutputStream dos = new DataOutputStream(Files.newOutputStream(outputFile.toPath()))) {
                    dos.write(CompressionUtils.gzip(uncompressedData));
                }

                double progress = (double) fileId / total * 100;
                System.out.printf("%.2f%%%n", progress);
            }
        }
    }
}