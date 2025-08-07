

package cache.openrs.util.tukaani.index;

import cache.openrs.util.tukaani.CorruptedInputException;
import cache.openrs.util.tukaani.XZIOException;
import cache.openrs.util.tukaani.common.DecoderUtil;

import java.io.DataInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.ByteBuffer;
import java.util.Arrays;
import java.util.zip.CheckedInputStream;

public class IndexHash extends IndexBase {
    private cache.openrs.util.tukaani.check.Check hash;

    public IndexHash() {
        super(new CorruptedInputException());

        try {
            hash = new cache.openrs.util.tukaani.check.SHA256();
        } catch (java.security.NoSuchAlgorithmException e) {
            hash = new cache.openrs.util.tukaani.check.CRC32();
        }
    }

    public void add(long unpaddedSize, long uncompressedSize)
            throws XZIOException {
        super.add(unpaddedSize, uncompressedSize);

        ByteBuffer buf = ByteBuffer.allocate(2 * 8);
        buf.putLong(unpaddedSize);
        buf.putLong(uncompressedSize);
        hash.update(buf.array());
    }

    public void validate(InputStream in) throws IOException {


        java.util.zip.CRC32 crc32 = new java.util.zip.CRC32();
        crc32.update('\0');
        CheckedInputStream inChecked = new CheckedInputStream(in, crc32);


        long storedRecordCount = DecoderUtil.decodeVLI(inChecked);
        if (storedRecordCount != recordCount)
            throw new CorruptedInputException("XZ Index is corrupt");



        IndexHash stored = new IndexHash();
        for (long i = 0; i < recordCount; ++i) {
            long unpaddedSize = DecoderUtil.decodeVLI(inChecked);
            long uncompressedSize = DecoderUtil.decodeVLI(inChecked);

            try {
                stored.add(unpaddedSize, uncompressedSize);
            } catch (XZIOException e) {
                throw new CorruptedInputException("XZ Index is corrupt");
            }

            if (stored.blocksSum > blocksSum
                    || stored.uncompressedSum > uncompressedSum
                    || stored.indexListSize > indexListSize)
                throw new CorruptedInputException("XZ Index is corrupt");
        }

        if (stored.blocksSum != blocksSum
                || stored.uncompressedSum != uncompressedSum
                || stored.indexListSize != indexListSize
                || !Arrays.equals(stored.hash.finish(), hash.finish()))
            throw new CorruptedInputException("XZ Index is corrupt");


        DataInputStream inData = new DataInputStream(inChecked);
        for (int i = getIndexPaddingSize(); i > 0; --i)
            if (inData.readUnsignedByte() != 0x00)
                throw new CorruptedInputException("XZ Index is corrupt");


        long value = crc32.getValue();
        for (int i = 0; i < 4; ++i)
            if (((value >>> (i * 8)) & 0xFF) != inData.readUnsignedByte())
                throw new CorruptedInputException("XZ Index is corrupt");
    }
}
