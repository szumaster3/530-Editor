

package cache.openrs.util.tukaani;


public class XZFormatException extends XZIOException {
    private static final long serialVersionUID = 3L;

    
    public XZFormatException() {
        super("Input is not in the XZ format");
    }
}
