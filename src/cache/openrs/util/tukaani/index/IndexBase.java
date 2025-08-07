

package cache.openrs.util.tukaani.index;

import cache.openrs.util.tukaani.XZIOException;
import cache.openrs.util.tukaani.common.Util;

abstract class IndexBase {
    private final XZIOException invalidIndexException;
    long blocksSum = 0;
    long uncompressedSum = 0;
    long indexListSize = 0;
    long recordCount = 0;

    IndexBase(XZIOException invalidIndexException) {
        this.invalidIndexException = invalidIndexException;
    }

    private long getUnpaddedIndexSize() {

        return 1 + Util.getVLISize(recordCount) + indexListSize + 4;
    }

    public long getIndexSize() {
        return (getUnpaddedIndexSize() + 3) & ~3;
    }

    public long getStreamSize() {
        return Util.STREAM_HEADER_SIZE + blocksSum + getIndexSize()
                + Util.STREAM_HEADER_SIZE;
    }

    int getIndexPaddingSize() {
        return (int)((4 - getUnpaddedIndexSize()) & 3);
    }

    void add(long unpaddedSize, long uncompressedSize) throws XZIOException {
        blocksSum += (unpaddedSize + 3) & ~3;
        uncompressedSum += uncompressedSize;
        indexListSize += Util.getVLISize(unpaddedSize)
                         + Util.getVLISize(uncompressedSize);
        ++recordCount;

        if (blocksSum < 0 || uncompressedSum < 0
                || getIndexSize() > Util.BACKWARD_SIZE_MAX
                || getStreamSize() < 0)
            throw invalidIndexException;
    }
}
