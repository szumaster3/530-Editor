

package cache.openrs.util.tukaani.index;

public class BlockInfo {
    public int blockNumber = -1;
    public long compressedOffset = -1;
    public long uncompressedOffset = -1;
    public long unpaddedSize = -1;
    public long uncompressedSize = -1;

    IndexDecoder index;

    public BlockInfo(IndexDecoder indexOfFirstStream) {
        index = indexOfFirstStream;
    }

    public int getCheckType() {
        return index.getStreamFlags().checkType;
    }

    public boolean hasNext() {
        return index.hasRecord(blockNumber + 1);
    }

    public void setNext() {
        index.setBlockInfo(this, blockNumber + 1);
    }
}
