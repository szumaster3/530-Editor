package cache.alex.util;


public final class Djb2 {

    
    public static int hash(String str) {
        int hash = 0;
        for (int i = 0; i < str.length(); i++) {
            hash = str.charAt(i) + ((hash << 5) - hash);
        }
        return hash;
    }

    
    private Djb2() {

    }

}
