package cache.openrs.cache;

import java.util.Arrays;


public class Identifiers {

    int[] table;

    public int getFile(int identifier) {
        
        int mask = (table.length >> 1) - 1;
        int i = identifier & mask;

        while (true) {
            
            int id = table[i + i + 1];
            if (id == -1) {
                return -1;
            }

            
            if (table[i + i] == identifier) {
                return id;
            }

            
            i = i + 1 & mask;
        }
    }

    public Identifiers(int[] identifiers) {
        
        int length = identifiers.length;
        int halfLength = identifiers.length >> 1;

        
        int size = 1;
        int mask = 1;
        for (int i = 1; i <= length + (halfLength); i <<= 1) {
            mask = i;
            size = i << 1;
        }

        
        mask <<= 1;
        size <<= 1;

        
        table = new int[size];

        
        Arrays.fill(table, -1);

        
        for (int id = 0; id < identifiers.length; id++) {
            int i;
            for (i = identifiers[id] & mask - 1; table[i + i + 1] != -1; i = i + 1 & mask - 1) ;

            table[i + i] = identifiers[id];
            table[i + i + 1] = id;
        }

    }

}