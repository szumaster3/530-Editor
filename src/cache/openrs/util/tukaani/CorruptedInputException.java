

package cache.openrs.util.tukaani;


public class CorruptedInputException extends XZIOException {
    private static final long serialVersionUID = 3L;

    
    public CorruptedInputException() {
        super("Compressed data is corrupt");
    }

    
    public CorruptedInputException(String s) {
        super(s);
    }
}
