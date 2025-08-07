

package cache.openrs.util.tukaani;

import cache.openrs.util.tukaani.check.Check;
import cache.openrs.util.tukaani.common.DecoderUtil;

import java.io.ByteArrayInputStream;
import java.io.DataInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Arrays;

class BlockInputStream extends InputStream {
    private final DataInputStream inData;
    private final CountingInputStream inCounted;
    private InputStream filterChain;
    private final Check check;

    private long uncompressedSizeInHeader = -1;
    private long compressedSizeInHeader = -1;
    private long compressedSizeLimit;
    private final int headerSize;
    private long uncompressedSize = 0;
    private boolean endReached = false;

    private final byte[] tempBuf = new byte[1];

    public BlockInputStream(InputStream in, Check check, int memoryLimit,
                            long unpaddedSizeInIndex,
                            long uncompressedSizeInIndex)
            throws IOException, IndexIndicatorException {
        this.check = check;
        inData = new DataInputStream(in);

        byte[] buf = new byte[DecoderUtil.BLOCK_HEADER_SIZE_MAX];


        inData.readFully(buf, 0, 1);


        if (buf[0] == 0x00)
            throw new IndexIndicatorException();


        headerSize = 4 * ((buf[0] & 0xFF) + 1);
        inData.readFully(buf, 1, headerSize - 1);


        if (!DecoderUtil.isCRC32Valid(buf, 0, headerSize - 4, headerSize - 4))
            throw new CorruptedInputException("XZ Block Header is corrupt");


        if ((buf[1] & 0x3C) != 0)
            throw new UnsupportedOptionsException(
                    "Unsupported options in XZ Block Header");


        int filterCount = (buf[1] & 0x03) + 1;
        long[] filterIDs = new long[filterCount];
        byte[][] filterProps = new byte[filterCount][];



        ByteArrayInputStream bufStream = new ByteArrayInputStream(
                buf, 2, headerSize - 6);

        try {


            compressedSizeLimit = (DecoderUtil.VLI_MAX & ~3)
                                  - headerSize - check.getSize();



            if ((buf[1] & 0x40) != 0x00) {
                compressedSizeInHeader = DecoderUtil.decodeVLI(bufStream);

                if (compressedSizeInHeader == 0
                        || compressedSizeInHeader > compressedSizeLimit)
                    throw new CorruptedInputException();

                compressedSizeLimit = compressedSizeInHeader;
            }



            if ((buf[1] & 0x80) != 0x00)
                uncompressedSizeInHeader = DecoderUtil.decodeVLI(bufStream);


            for (int i = 0; i < filterCount; ++i) {
                filterIDs[i] = DecoderUtil.decodeVLI(bufStream);

                long filterPropsSize = DecoderUtil.decodeVLI(bufStream);
                if (filterPropsSize > bufStream.available())
                    throw new CorruptedInputException();

                filterProps[i] = new byte[(int)filterPropsSize];
                bufStream.read(filterProps[i]);
            }

        } catch (IOException e) {
            throw new CorruptedInputException("XZ Block Header is corrupt");
        }


        for (int i = bufStream.available(); i > 0; --i)
            if (bufStream.read() != 0x00)
                throw new UnsupportedOptionsException(
                        "Unsupported options in XZ Block Header");



        if (unpaddedSizeInIndex != -1) {



            int headerAndCheckSize = headerSize + check.getSize();
            if (headerAndCheckSize >= unpaddedSizeInIndex)
                throw new CorruptedInputException(
                        "XZ Index does not match a Block Header");




            long compressedSizeFromIndex
                    = unpaddedSizeInIndex - headerAndCheckSize;
            if (compressedSizeFromIndex > compressedSizeLimit
                    || (compressedSizeInHeader != -1
                        && compressedSizeInHeader != compressedSizeFromIndex))
                throw new CorruptedInputException(
                        "XZ Index does not match a Block Header");




            if (uncompressedSizeInHeader != -1
                    && uncompressedSizeInHeader != uncompressedSizeInIndex)
                throw new CorruptedInputException(
                        "XZ Index does not match a Block Header");



            compressedSizeLimit = compressedSizeFromIndex;
            compressedSizeInHeader = compressedSizeFromIndex;
            uncompressedSizeInHeader = uncompressedSizeInIndex;
        }




        FilterDecoder[] filters = new FilterDecoder[filterIDs.length];

        for (int i = 0; i < filters.length; ++i) {
            if (filterIDs[i] == LZMA2Coder.FILTER_ID)
                filters[i] = new LZMA2Decoder(filterProps[i]);

            else if (filterIDs[i] == DeltaCoder.FILTER_ID)
                filters[i] = new DeltaDecoder(filterProps[i]);

            else if (BCJDecoder.isBCJFilterID(filterIDs[i]))
                filters[i] = new BCJDecoder(filterIDs[i], filterProps[i]);

            else
                throw new UnsupportedOptionsException(
                        "Unknown Filter ID " + filterIDs[i]);
        }

        RawCoder.validate(filters);


        if (memoryLimit >= 0) {
            int memoryNeeded = 0;
            for (int i = 0; i < filters.length; ++i)
                memoryNeeded += filters[i].getMemoryUsage();

            if (memoryNeeded > memoryLimit)
                throw new MemoryLimitException(memoryNeeded, memoryLimit);
        }



        inCounted = new CountingInputStream(in);


        filterChain = inCounted;
        for (int i = filters.length - 1; i >= 0; --i)
            filterChain = filters[i].getInputStream(filterChain);
    }

    public int read() throws IOException {
        return read(tempBuf, 0, 1) == -1 ? -1 : (tempBuf[0] & 0xFF);
    }

    public int read(byte[] buf, int off, int len) throws IOException {
        if (endReached)
            return -1;

        int ret = filterChain.read(buf, off, len);

        if (ret > 0) {
            check.update(buf, off, ret);
            uncompressedSize += ret;


            long compressedSize = inCounted.getSize();
            if (compressedSize < 0
                    || compressedSize > compressedSizeLimit
                    || uncompressedSize < 0
                    || (uncompressedSizeInHeader != -1
                        && uncompressedSize > uncompressedSizeInHeader))
                throw new CorruptedInputException();








            if (ret < len || uncompressedSize == uncompressedSizeInHeader) {
                if (filterChain.read() != -1)
                    throw new CorruptedInputException();

                validate();
                endReached = true;
            }
        } else if (ret == -1) {
            validate();
            endReached = true;
        }

        return ret;
    }

    private void validate() throws IOException {
        long compressedSize = inCounted.getSize();



        if ((compressedSizeInHeader != -1
                    && compressedSizeInHeader != compressedSize)
                || (uncompressedSizeInHeader != -1
                    && uncompressedSizeInHeader != uncompressedSize))
            throw new CorruptedInputException();


        while ((compressedSize++ & 3) != 0)
            if (inData.readUnsignedByte() != 0x00)
                throw new CorruptedInputException();


        byte[] storedCheck = new byte[check.getSize()];
        inData.readFully(storedCheck);
        if (!Arrays.equals(check.finish(), storedCheck))
            throw new CorruptedInputException("Integrity check ("
                    + check.getName() + ") does not match");
    }

    public int available() throws IOException {
        return filterChain.available();
    }

    public long getUnpaddedSize() {
        return headerSize + inCounted.getSize() + check.getSize();
    }

    public long getUncompressedSize() {
        return uncompressedSize;
    }
}
