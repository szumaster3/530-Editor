

package cache.openrs.util.tukaani;

import java.io.InputStream;

class LZMA2Decoder extends LZMA2Coder implements FilterDecoder {
    private int dictSize;

    LZMA2Decoder(byte[] props) throws UnsupportedOptionsException {


        if (props.length != 1 || (props[0] & 0xFF) > 37)
            throw new UnsupportedOptionsException(
                    "Unsupported LZMA2 properties");

        dictSize = 2 | (props[0] & 1);
        dictSize <<= (props[0] >>> 1) + 11;
    }

    public int getMemoryUsage() {
        return LZMA2InputStream.getMemoryUsage(dictSize);
    }

    public InputStream getInputStream(InputStream in) {
        return new LZMA2InputStream(in, dictSize);
    }
}
