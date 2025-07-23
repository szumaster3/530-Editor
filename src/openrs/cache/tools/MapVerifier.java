package openrs.cache.tools;

import com.cache.util.XTEAManager;
import openrs.cache.Cache;
import openrs.cache.Constants;
import openrs.cache.FileStore;

import java.util.Arrays;

/**
 * Utility for verifying maps.
 */
public class MapVerifier {

    /**
     * Iterates over all possible region ids.
     */
    public static void main(String[] args) {
        int count = 0;

        try (Cache cache = new Cache(FileStore.open(Constants.CACHE_PATH))) {
            for (int regionId = 0; regionId < 32_768; regionId++) {
                int[] keys = XTEAManager.lookup(regionId);
                String landscapeName = "l" + (regionId >> 8) + "_" + (regionId & 0xFF);
                int landFileId = cache.getFileId(5, landscapeName);

                if (landFileId == -1) {
                    continue;
                }

                try {
                    cache.read(5, landFileId, keys).getData();
                } catch (Exception e) {
                    if (keys != null && keys.length > 0 && keys[0] != 0) {
                        int baseX = (regionId >> 8) << 6;
                        int baseY = (regionId & 0xFF) << 6;

                        System.out.printf(
                                "Region ID: %d, Coords: (%d, %d), File: (5, %d), Keys: %s%n",
                                regionId, baseX, baseY, landFileId, Arrays.toString(keys)
                        );

                        count++;

                        try {
                            cache.read(5, landFileId).getData();
                            System.out.printf("Region ID: %d no longer encrypted%n", regionId);
                        } catch (Exception ignored) {
                        }
                    }
                }
            }

            System.out.println("Incorrect: " + count);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

}
