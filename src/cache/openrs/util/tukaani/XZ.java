

package cache.openrs.util.tukaani;


public class XZ {

    public static final byte[] HEADER_MAGIC = {
            (byte)0xFD, '7', 'z', 'X', 'Z', '\0' };


    public static final byte[] FOOTER_MAGIC = { 'Y', 'Z' };


    public static final int CHECK_NONE = 0;


    public static final int CHECK_CRC32 = 1;


    public static final int CHECK_CRC64 = 4;


    public static final int CHECK_SHA256 = 10;

    private XZ() {}
}
