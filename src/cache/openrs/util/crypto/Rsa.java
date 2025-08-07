package cache.openrs.util.crypto;

import java.math.BigInteger;
import java.nio.ByteBuffer;


public final class Rsa {

    
    public static ByteBuffer crypt(ByteBuffer buffer, BigInteger modulus, BigInteger key) {
        byte[] bytes = new byte[buffer.limit()];
        buffer.get(bytes);

        BigInteger in = new BigInteger(bytes);
        BigInteger out = in.modPow(key, modulus);

        return ByteBuffer.wrap(out.toByteArray());
    }

    
    private Rsa() {

    }

}
