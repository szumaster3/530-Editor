package cache.alex.util.whirlpool;


public final class Djb2 {

    
    private Djb2() {

    }

    
    public static int djb2(String str) {
        int hash = 0;
        for (int i = 0; i < str.length(); i++) {
            hash = str.charAt(i) + ((hash << 5) - hash);
        }
        return hash;
    }

}
