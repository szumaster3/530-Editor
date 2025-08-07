package cache.alex.util;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;

import java.io.*;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

public final class XTEAManager {

    private static final Map<Integer, int[]> maps = new HashMap<>();
    private static final Map<Integer, int[]> tables = new HashMap<>();

    public static final int[] NULL_KEYS = new int[4];

    public static int[] lookupTable(int id) {
        int[] keys = tables.get(id);
        return keys == null ? NULL_KEYS : keys;
    }

    public static Map<Integer, int[]> allKeys() {
        return Collections.unmodifiableMap(maps);
    }

    public static int[] lookup(int region) {
        return maps.getOrDefault(region, NULL_KEYS);
    }

    
    public static boolean load(File jsonFile) {
        maps.clear();

        try (FileReader reader = new FileReader(jsonFile)) {
            JSONParser parser = new JSONParser();
            JSONObject obj = (JSONObject) parser.parse(reader);
            JSONArray xteas = (JSONArray) obj.get("xteas");

            for (Object entryObj : xteas) {
                JSONObject entry = (JSONObject) entryObj;
                String r = (String) entry.get("regionId");
                String k = (String) entry.get("keys");

                if (r == null || k == null) {
                    System.err.println("Invalid entry in XTEA JSON: " + entry);
                    continue;
                }

                int id;
                try {
                    id = Integer.parseInt(r);
                } catch (NumberFormatException e) {
                    System.err.println("Invalid regionId format: " + r);
                    continue;
                }

                String[] keyParts = k.split(",");
                if (keyParts.length != 4) {
                    System.err.println("Invalid keys count for region " + id + ": " + k);
                    continue;
                }

                int[] keys = new int[4];
                try {
                    for (int i = 0; i < 4; i++) {
                        keys[i] = Integer.parseInt(keyParts[i].trim());
                    }
                } catch (NumberFormatException e) {
                    System.err.println("Invalid key format for region " + id + ": " + k);
                    continue;
                }

                maps.put(id, keys);
            }

            System.out.println("Loaded " + maps.size() + " XTEA keys from JSON.");
            return !maps.isEmpty();

        } catch (Exception e) {
            System.err.println("Failed to load XTEA keys from JSON: " + e.getMessage());
            return false;
        }
    }

    
    public static void main(String[] args) {
        File jsonFile = new File("data/xteas");
        File outputDir = new File("data/xteas" + "/txt/");

        if (!outputDir.exists()) {
            outputDir.mkdirs();
        }

        if (XTEAManager.load(jsonFile)) {
            for (Map.Entry<Integer, int[]> entry : XTEAManager.allKeys().entrySet()) {
                int region = entry.getKey();
                int[] keys = entry.getValue();
                String keyString = Arrays.toString(keys);

                System.out.println("Keys for region=" + region + " = " + keyString);

                File regionFile = new File(outputDir, region + ".txt");
                try (BufferedWriter writer = new BufferedWriter(new FileWriter(regionFile))) {
                    writer.write(keyString);
                } catch (IOException e) {
                    System.err.println("Failed to write keys for region " + region + ": " + e.getMessage());
                }
            }
        } else {
            System.err.println("Failed to load XTEA keys.");
        }
    }

    public static boolean isLoaded() {
        return !maps.isEmpty();
    }
}