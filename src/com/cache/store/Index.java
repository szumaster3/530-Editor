package com.cache.store;

import com.cache.io.InputStream;
import com.cache.io.OutputStream;
import com.cache.util.Utils;
import com.cache.util.crc32.CRC32HGenerator;
import com.cache.util.whirlpool.Whirlpool;

public final class Index {
    private final MainFile mainFile;
    private final MainFile index255;
    private ReferenceTable table;
    private byte[][][] cachedFiles;
    private int crc;
    private byte[] whirlpool;

    Index(MainFile index255, MainFile mainFile, int[] keys) {
        this.mainFile = mainFile;
        this.index255 = index255;
        byte[] archiveData = index255.getArchiveData(this.getId());
        if (archiveData != null) {
            this.crc = CRC32HGenerator.getHash(archiveData);
            this.whirlpool = Whirlpool.whirlpool(archiveData, 0, archiveData.length);
            Archive archive = new Archive(this.getId(), archiveData, keys);
            this.table = new ReferenceTable(archive);
            this.resetCachedFiles();
        }

    }

    public void resetCachedFiles() {
        this.cachedFiles = new byte[this.getLastArchiveId() + 1][][];
    }

    public int getLastFileId(int archiveId) {
        return !this.archiveExists(archiveId) ? -1 : this.table.getArchives()[archiveId].files.length - 1;
    }

    public int getLastArchiveId() {
        return this.table.getArchives().length - 1;
    }

    public int getValidArchivesCount() {
        return this.table.getValidArchiveIds().length;
    }

    public int getValidFilesCount(int archiveId) {
        return !this.archiveExists(archiveId) ? -1 : this.table.getArchives()[archiveId].validFileIds.length;
    }

    public boolean archiveExists(int archiveId) {
        if (archiveId < 0) {
            return false;
        } else {
            ArchiveReference[] archives = this.table.getArchives();
            return archives.length > archiveId && archives[archiveId] != null;
        }
    }

    public boolean fileExists(int archiveId, int fileId) {
        if (!this.archiveExists(archiveId)) {
            return false;
        } else {
            FileReference[] files = this.table.getArchives()[archiveId].files;
            return files.length > fileId && files[fileId] != null;
        }
    }

    public int getArchiveId(String name) {
        int nameHash = Utils.getNameHash(name);
        ArchiveReference[] archives = this.table.getArchives();
        int[] validArchiveIds = this.table.getValidArchiveIds();
        int[] arr$ = validArchiveIds;
        int len$ = validArchiveIds.length;

        for (int i$ = 0; i$ < len$; ++i$) {
            int archiveId = arr$[i$];
            if (archives[archiveId].nameHash == nameHash) {
                return archiveId;
            }
        }

        return -1;
    }

    public int getFileId(int archiveId, String name) {
        if (!this.archiveExists(archiveId)) {
            return -1;
        } else {
            int nameHash = Utils.getNameHash(name);
            FileReference[] files = this.table.getArchives()[archiveId].files;
            int[] validFileIds = this.table.getArchives()[archiveId].validFileIds;

            for (int index = 0; index < validFileIds.length; ++index) {
                int fileId = validFileIds[index];
                if (files[fileId].nameHash == nameHash) {
                    return fileId;
                }
            }

            return -1;
        }
    }

    public byte[] getFile(int archiveId) {
        return !this.archiveExists(archiveId) ? null : this.getFile(archiveId, this.table.getArchives()[archiveId].validFileIds[0]);
    }

    public byte[] getFile(int archiveId, int fileId) {
        return this.getFile(archiveId, fileId, null);
    }

    public byte[] getFile(int archiveId, int fileId, int[] keys) {
        try {
            if (!this.fileExists(archiveId, fileId)) {
                return null;
            } else {
                if (this.cachedFiles[archiveId] == null || this.cachedFiles[archiveId][fileId] == null) {
                    this.cacheArchiveFiles(archiveId, keys);
                }

                byte[] var5 = this.cachedFiles[archiveId][fileId];
                this.cachedFiles[archiveId][fileId] = null;
                return var5;
            }
        } catch (Throwable var51) {
            var51.printStackTrace();
            return null;
        }
    }

    public boolean packIndex(Cache originalCache) {
        return this.packIndex(originalCache, false);
    }

    public boolean packIndex(Cache originalCache, boolean checkCRC) {
        try {
            return this.packIndex(this.getId(), originalCache, checkCRC);
        } catch (Exception var4) {
            return this.packIndex(this.getId(), originalCache, checkCRC);
        }
    }

    public boolean packIndex(int id, Cache originalCache, boolean checkCRC) {
        try {
            Index var9 = originalCache.getIndexes()[id];
            int[] arr$ = var9.table.getValidArchiveIds();
            int len$ = arr$.length;

            for (int i$ = 0; i$ < len$; ++i$) {
                int archiveId = arr$[i$];
                if ((!checkCRC || !this.archiveExists(archiveId) || var9.table.getArchives()[archiveId].getCRC() != this.table.getArchives()[archiveId].getCRC()) && !this.putArchive(id, archiveId, originalCache, false, false)) {
                    return false;
                }
            }

            if (!this.rewriteTable()) {
                return false;
            } else {
                this.resetCachedFiles();
                return true;
            }
        } catch (Exception var91) {
            return true;
        }
    }

    public boolean putArchive(int archiveId, Cache originalCache) {
        return this.putArchive(this.getId(), archiveId, originalCache, true, true);
    }

    public boolean putArchive(int archiveId, Cache originalCache, boolean rewriteTable, boolean resetCache) {
        return this.putArchive(this.getId(), archiveId, originalCache, rewriteTable, resetCache);
    }

    public void removeArchive(int archiveId) {
        if (archiveId < 0 || archiveId >= cachedFiles.length) {
            throw new IllegalArgumentException("archiveId out of bounds: " + archiveId);
        }
        byte temp[][][] = new byte[cachedFiles.length - 1][][];
        int count = 0;
        for (int i = 0; i < cachedFiles.length; i++) {
            if (i != archiveId) {
                temp[count] = cachedFiles[i];
                count++;
            }
        }
        cachedFiles = temp;
        table.removeArchive(archiveId);
        rewriteTable();
    }


    public boolean putArchive(int id, int archiveId, Cache originalCache, boolean rewriteTable, boolean resetCache) {
        try {
            Index var11 = originalCache.getIndexes()[id];
            byte[] data = var11.getMainFile().getArchiveData(archiveId);
            if (data == null) {
                return false;
            } else {
                if (!this.archiveExists(archiveId)) {
                    this.table.addEmptyArchiveReference(archiveId);
                }

                ArchiveReference reference = this.table.getArchives()[archiveId];
                reference.updateRevision();
                ArchiveReference originalReference = var11.table.getArchives()[archiveId];
                reference.copyHeader(originalReference);
                int revision = reference.revision;
                data[data.length - 2] = (byte) (revision >> 8);
                data[data.length - 1] = (byte) revision;
                if (!this.mainFile.putArchiveData(archiveId, data)) {
                    return false;
                } else if (rewriteTable && !this.rewriteTable()) {
                    return false;
                } else {
                    if (resetCache) {
                        this.resetCachedFiles();
                    }

                    return true;
                }
            }
        } catch (Exception var111) {
            return true;
        }
    }

    public boolean putFile(int archiveId, int fileId, byte[] data) {
        return this.putFile(archiveId, fileId, 2, data, null, true, true, -1, -1);
    }

    public boolean removeFile(int archiveId, int fileId) {
        return this.removeFile(archiveId, fileId, 2, null);
    }

    public boolean removeFile(int archiveId, int fileId, int compression, int[] keys) {
        if (!this.fileExists(archiveId, fileId)) {
            System.out.println("removeFile aborted: file does not exist for archiveId=" + archiveId + ", fileId=" + fileId);
            return false;
        } else {
            this.cacheArchiveFiles(archiveId, keys);
            ArchiveReference reference = this.table.getArchives()[archiveId];
            reference.removeFileReference(fileId);
            int filesCount = this.getValidFilesCount(archiveId);
            byte[] archiveData;
            if (filesCount == 1) {
                archiveData = this.getFile(archiveId, reference.validFileIds[0], keys);
            } else {
                int[] var13 = new int[filesCount];
                OutputStream var14 = new OutputStream();

                int index;
                int offset;
                for (index = 0; index < filesCount; ++index) {
                    offset = reference.validFileIds[index];
                    byte[] fileData = this.getFile(archiveId, offset, keys);
                    var13[index] = fileData.length;
                    var14.writeBytes(fileData);
                }

                for (index = 0; index < var13.length; ++index) {
                    offset = var13[index];
                    if (index != 0) {
                        offset -= var13[index - 1];
                    }

                    var14.writeInt(offset);
                }

                var14.writeByte(1);
                archiveData = new byte[var14.getOffset()];
                var14.setOffset(0);
                var14.getBytes(archiveData, 0, archiveData.length);
            }

            reference.updateRevision();
            Archive var131 = new Archive(archiveId, compression, reference.revision, archiveData);
            byte[] var141 = var131.compress();
            reference.setCrc(CRC32HGenerator.getHash(var141, 0, var141.length - 2));
            reference.whirpool = Whirlpool.whirlpool(var141, 0, var141.length - 2);

            boolean putArchiveResult = this.mainFile.putArchiveData(archiveId, var141);
            if (!putArchiveResult) {
                System.out.println("putArchiveData failed for archiveId " + archiveId);
                return false;
            }

            boolean rewriteTableResult = this.rewriteTable();
            if (!rewriteTableResult) {
                System.out.println("rewriteTable failed");
                return false;
            }

            this.resetCachedFiles();
            System.out.println("removeFile succeeded for archiveId=" + archiveId + ", fileId=" + fileId);
            return true;
        }
    }

    public boolean putFile(int archiveId, int fileId, int compression, byte[] data, int[] keys, boolean rewriteTable, boolean resetCache, int archiveName, int fileName) {
        if (!this.archiveExists(archiveId)) {
            this.table.addEmptyArchiveReference(archiveId);
            this.resetCachedFiles();
            this.cachedFiles[archiveId] = new byte[1][];
        } else {
            this.cacheArchiveFiles(archiveId, keys);
        }

        ArchiveReference reference = this.table.getArchives()[archiveId];
        if (!this.fileExists(archiveId, fileId)) {
            reference.addEmptyFileReference(fileId);
        }

        reference.sortFiles();
        int filesCount = this.getValidFilesCount(archiveId);
        byte[] archiveData;
        if (filesCount == 1) {
            archiveData = data;
        } else {
            int[] var18 = new int[filesCount];
            OutputStream var19 = new OutputStream();

            int index;
            int offset;
            for (index = 0; index < filesCount; ++index) {
                offset = reference.validFileIds[index];
                byte[] fileData;
                if (offset == fileId) {
                    fileData = data;
                } else {
                    fileData = this.getFile(archiveId, offset, keys);
                }

                var18[index] = fileData.length;
                var19.writeBytes(fileData);
            }

            for (index = 0; index < filesCount; ++index) {
                offset = var18[index];
                if (index != 0) {
                    offset -= var18[index - 1];
                }

                var19.writeInt(offset);
            }

            var19.writeByte(1);
            archiveData = new byte[var19.getOffset()];
            var19.setOffset(0);
            var19.getBytes(archiveData, 0, archiveData.length);
        }

        reference.updateRevision();
        Archive var181 = new Archive(archiveId, compression, reference.revision, archiveData);
        byte[] var191 = var181.compress();
        reference.setCrc(CRC32HGenerator.getHash(var191, 0, var191.length - 2));
        reference.whirpool = Whirlpool.whirlpool(var191, 0, var191.length - 2);
        if (archiveName != -1) {
            reference.nameHash = archiveName;
        }

        if (fileName != -1) {
            reference.files[fileId].nameHash = fileName;
        }

        if (!this.mainFile.putArchiveData(archiveId, var191)) {
            return false;
        } else if (rewriteTable && !this.rewriteTable()) {
            return false;
        } else {
            if (resetCache) {
                this.resetCachedFiles();
            }

            return true;
        }
    }

    public boolean encryptArchive(int archiveId, int[] keys) {
        return this.encryptArchive(archiveId, null, keys, true, true);
    }

    public boolean encryptArchive(int archiveId, int[] oldKeys, int[] keys, boolean rewriteTable, boolean resetCache) {
        if (!this.archiveExists(archiveId)) {
            return false;
        } else {
            Archive archive = this.mainFile.getArchive(archiveId, oldKeys);
            if (archive == null) {
                return false;
            } else {
                ArchiveReference reference = this.table.getArchives()[archiveId];
                if (reference.revision != archive.getRevision()) {
                    throw new RuntimeException("ERROR REVISION");
                } else {
                    reference.updateRevision();
                    archive.setRevision(reference.revision);
                    archive.setKeys(keys);
                    byte[] closedArchive = archive.compress();
                    reference.setCrc(CRC32HGenerator.getHash(closedArchive, 0, closedArchive.length - 2));
                    reference.whirpool = Whirlpool.whirlpool(closedArchive, 0, closedArchive.length - 2);
                    if (!this.mainFile.putArchiveData(archiveId, closedArchive)) {
                        return false;
                    } else if (rewriteTable && !this.rewriteTable()) {
                        return false;
                    } else {
                        if (resetCache) {
                            this.resetCachedFiles();
                        }

                        return true;
                    }
                }
            }
        }
    }

    public boolean rewriteTable() {
        this.table.updateRevision();
        this.table.sortTable();
        Object[] hashes = this.table.encodeHeader(this.index255);
        if (hashes == null) {
            return false;
        } else {
            this.whirlpool = (byte[]) hashes[1];
            return true;
        }
    }

    public int[] getKeys() {
        return this.table.getKeys();
    }

    public void setKeys(int[] keys) {
        this.table.setKeys(keys);
    }

    private void cacheArchiveFiles(int archiveId, int[] keys) {
        Archive archive = this.getArchive(archiveId, keys);
        int lastFileId = this.getLastFileId(archiveId);
        this.cachedFiles[archiveId] = new byte[lastFileId + 1][];
        if (archive != null) {
            byte[] data = archive.getData();
            if (data != null) {
                int filesCount = this.getValidFilesCount(archiveId);
                if (filesCount == 1) {
                    this.cachedFiles[archiveId][lastFileId] = data;
                } else {
                    int readPosition = data.length;
                    --readPosition;
                    int amtOfLoops = data[readPosition] & 255;
                    readPosition -= amtOfLoops * filesCount * 4;
                    InputStream stream = new InputStream(data);
                    stream.setOffset(readPosition);
                    int[] filesSize = new int[filesCount];

                    int sourceOffset;
                    int count;
                    for (int var18 = 0; var18 < amtOfLoops; ++var18) {
                        sourceOffset = 0;

                        for (count = 0; count < filesCount; ++count) {
                            filesSize[count] += sourceOffset += stream.readInt();
                        }
                    }

                    byte[][] var181 = new byte[filesCount][];

                    for (sourceOffset = 0; sourceOffset < filesCount; ++sourceOffset) {
                        var181[sourceOffset] = new byte[filesSize[sourceOffset]];
                        filesSize[sourceOffset] = 0;
                    }

                    stream.setOffset(readPosition);
                    sourceOffset = 0;

                    int len$;
                    for (count = 0; count < amtOfLoops; ++count) {
                        int var19 = 0;

                        for (len$ = 0; len$ < filesCount; ++len$) {
                            var19 += stream.readInt();
                            System.arraycopy(data, sourceOffset, var181[len$], filesSize[len$], var19);
                            sourceOffset += var19;
                            filesSize[len$] += var19;
                        }
                    }

                    count = 0;
                    int[] var191 = this.table.getArchives()[archiveId].validFileIds;
                    len$ = var191.length;

                    for (int i$ = 0; i$ < len$; ++i$) {
                        int fileId = var191[i$];
                        this.cachedFiles[archiveId][fileId] = var181[count++];
                    }
                }
            }
        }

    }

    public int getId() {
        return this.mainFile.id;
    }

    public ReferenceTable getTable() {
        return this.table;
    }

    public MainFile getMainFile() {
        return this.mainFile;
    }

    public Archive getArchive(int id) {
        return this.mainFile.getArchive(id, null);
    }

    public Archive getArchive(int id, int[] keys) {
        return this.mainFile.getArchive(id, keys);
    }

    public int getCRC() {
        return this.crc;
    }

    public byte[] getWhirlpool() {
        return this.whirlpool;
    }
}
