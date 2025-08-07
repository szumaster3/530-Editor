
package cache.openrs.cache.region;


public class Location {

    private final int id;
    private final int type;
    private final int orientation;
    private final Position position;

    public Location(int id, int type, int ori, Position pos) {
        this.id = id;
        this.type = type;
        this.orientation = ori;
        this.position = pos;
    }

    
    public final int getId() {
        return id;
    }

    
    public final int getType() {
        return type;
    }

    
    public final int getOrientation() {
        return orientation;
    }

    
    public final Position getPosition() {
        return position;
    }

}