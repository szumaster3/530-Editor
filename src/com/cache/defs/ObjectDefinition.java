package com.cache.defs;

import com.cache.store.Cache;
import com.cache.io.InputStream;
import com.cache.io.OutputStream;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

public class ObjectDefinition implements Cloneable {
    public static int anInt3832;
    public static int anInt3836;
    public static int anInt3842;
    public static int anInt3843;
    public static int anInt3846;
    public HashMap<Integer, Object> clientScriptData;
    public short[] originalModelColors;
    public int[] childrenIds;
    public int anInt3834;
    public int anInt3835;
    public byte aByte3837;
    public int anInt3838 = -1;
    public boolean aBoolean3839;
    public int anInt3840;
    public int anInt3841;
    public int anInt3844;
    public byte aByte3847;
    public byte aByte3849;
    public int anInt3850;
    public int anInt3851;
    public boolean secondBool;
    public boolean aBoolean3853;
    public int anInt3855;
    public boolean notCliped;
    public int anInt3857;
    public int anInt3860;
    public String[] options;
    public short[] modifiedModelColors;
    public int anInt3865;
    public boolean projectileCliped;
    public int sizeY;
    public int thirdInt;
    public int anInt3875;
    public int anInt3876;
    public int anInt3877;
    public int anInt3878;
    public int clipType;
    public int anInt3881;
    public int anInt3882;
    public int anInt3883;
    public int anInt3889;
    public int sizeX;
    public boolean aBoolean3891;
    public int anInt3892;
    public int secondInt;
    public int anInt3896;
    public int configId;
    public int anInt3900;
    public String name;
    public int anInt3902;
    public int anInt3904;
    public int anInt3905;
    public byte aByte3912;
    public int anInt3913;
    public byte aByte3914;
    public int anInt3915;
    public int anInt3917;
    public short[] modifiedTextureColors;
    public short[] originalTextureColors;
    public int anInt3921;
    public int anInt3925;
    public int[] models;
    public int[] arrayForModelId;
    public int id;
    int[] anIntArray3833 = null;
    public boolean aBoolean3845;
    int[] anIntArray3859;
    int configFileId;
    public boolean aBoolean3866;
    public boolean aBoolean3867;
    public boolean aBoolean3870;
    boolean aBoolean3872;
    boolean aBoolean3873;
    Object loader;
    boolean aBoolean3894;
    boolean aBoolean3895;
    boolean aBoolean3906;
    int[] anIntArray3908;
    boolean aBoolean3923;
    boolean aBoolean3924;
    private byte[] aByteArray3858;
    private int[] anIntArray3869;
    private byte[] aByteArray3899;
    private int[][] anIntArrayArray3916;
    private Object aClass194_3922;

    public ObjectDefinition(Cache cache, int id) {
        this(cache, id, true);
    }

    public ObjectDefinition(Cache cache, int id, boolean load) {
        this.id = id;
        setDefaultsVariableValues();
        setDefaultOptions();
        if (load)
            loadObjectDefinitions(cache);
    }

    public static ObjectDefinition getObjectDefinition(Cache cache, int itemId) {
        return getObjectDefinition(cache, itemId, true);
    }

    public static ObjectDefinition getObjectDefinition(Cache cache, int itemId, boolean load) {
        return new ObjectDefinition(cache, itemId, load);
    }

    public void setDefaultOptions() {
        options = new String[5];
    }


    public void write(Cache cache) {
        cache.getIndexes()[16].putFile(this.getArchiveId(), this.getFileId(), this.encode());
    }

    public byte[] encode() {
        OutputStream stream = new OutputStream();

        if (this.models != null) {
            stream.writeByte(5);
            stream.writeByte(models.length);
            for (int i = 0; i < models.length; i++) {
                stream.writeShort(models[i]);

                if (this.arrayForModelId != null && this.arrayForModelId.length > i) {
                    stream.writeByte(this.arrayForModelId[i]);
                }
            }
        }

        if (this.name != null && !this.name.isEmpty() && !"null".equals(this.name)) {
            stream.writeByte(2);
            stream.writeString(this.name);
        }

        if (this.sizeX != 1) {
            stream.writeByte(14);
            stream.write128Byte(this.sizeX);
        }
        if (this.sizeY != 1) {
            stream.writeByte(15);
            stream.writeByte(this.sizeY);
        }

        if (this.options != null) {
            for (int i = 0; i < this.options.length; i++) {
                String option = this.options[i];
                if (option == null || "hidden".equals(option)) {
                    continue;
                }
                stream.writeByte(30 + i);
                stream.writeString(option);
            }
        }
        if (this.clipType == 0 && this.projectileCliped) {
            stream.writeByte(17);
        } else if (this.projectileCliped) {
            stream.writeByte(18);
        }
        if (this.clipType == 1 || this.clipType == 2) {
            stream.writeByte(27);
        }

        if (this.originalModelColors != null && this.modifiedModelColors != null &&
                this.originalModelColors.length == this.modifiedModelColors.length) {
            stream.writeByte(40);
            stream.writeByte(this.originalModelColors.length);
            for (int i = 0; i < this.originalModelColors.length; ++i) {
                stream.writeShort(this.originalModelColors[i]);
                stream.writeShort(this.modifiedModelColors[i]);
            }
        }

        if ((this.originalTextureColors != null) && (this.modifiedTextureColors != null)) {
            stream.writeByte(41);
            stream.writeByte(this.originalTextureColors.length);
            for (int index = 0; index < this.originalTextureColors.length; index++) {
                stream.writeShort(this.originalTextureColors[index]);
                stream.writeShort(this.modifiedTextureColors[index]);
            }
        }

        if (this.clientScriptData != null) {
            stream.writeByte(249);
            stream.writeByte(this.clientScriptData.size());
            for (Map.Entry<Integer, Object> entry : this.clientScriptData.entrySet()) {
                int key = entry.getKey();
                Object value = entry.getValue();
                stream.writeByte((value instanceof String) ? 1 : 0);
                stream.writeMedium(key);
                if (value instanceof String) {
                    stream.writeString((String) value);
                } else {
                    stream.writeInt((Integer) value);
                }
            }
        }

        stream.writeByte(0);

        byte[] encodedData = new byte[stream.getOffset()];
        stream.setOffset(0);
        stream.getBytes(encodedData, 0, encodedData.length);
        return encodedData;
    }


    private void readValues(InputStream stream, int opcode) {
        if (opcode == 1 || opcode == 5) {
            int length = stream.readUnsignedByte();
            models = new int[length];
            if (opcode == 1) {
                arrayForModelId = new int[length];
            }
            for (int index = 0; index < length; index++) {
                models[index] = stream.readUnsignedShort();
                if (opcode == 1) {
                    arrayForModelId[index] = stream.readUnsignedByte();
                }
            }
        }
        if (opcode != 2) {
            if (opcode != 14) {
                if (opcode != 15) {
                    if (opcode == 17) {
                        projectileCliped = false;
                        clipType = 0;
                    } else if (opcode != 18) {
                        if (opcode == 19)
                            secondInt = stream.readUnsignedByte();
                        else if (opcode == 21)
                            aByte3912 = (byte) 1;
                        else if (opcode != 22) {
                            if (opcode != 23) {
                                if (opcode != 24) {
                                    if (opcode == 27)
                                        clipType = 1;
                                    else if (opcode == 28)
                                        anInt3892 = (stream
                                                .readUnsignedByte() << 2);
                                    else if (opcode != 29) {
                                        if (opcode != 39) {
                                            if (opcode < 30 || opcode >= 35) {
                                                if (opcode == 40) {
                                                    int i_53_ = (stream
                                                            .readUnsignedByte());
                                                    originalModelColors = new short[i_53_];
                                                    modifiedModelColors = new short[i_53_];
                                                    for (int i_54_ = 0; i_53_ > i_54_; i_54_++) {
                                                        originalModelColors[i_54_] = (short) (stream
                                                                .readUnsignedShort());
                                                        modifiedModelColors[i_54_] = (short) (stream
                                                                .readUnsignedShort());
                                                    }
                                                } else if (opcode != 41) {
                                                    if (opcode != 42) {
                                                        if (opcode != 62) {
                                                            if (opcode != 64) {
                                                                if (opcode == 65)
                                                                    anInt3902 = stream
                                                                            .readUnsignedShort();
                                                                else if (opcode != 66) {
                                                                    if (opcode != 67) {
                                                                        if (opcode == 69)
                                                                            anInt3925 = stream
                                                                                    .readUnsignedByte();
                                                                        else if (opcode != 70) {
                                                                            if (opcode == 71)
                                                                                anInt3889 = stream
                                                                                        .readShort() << 2;
                                                                            else if (opcode != 72) {
                                                                                if (opcode == 73)
                                                                                    secondBool = true;
                                                                                else if (opcode == 74)
                                                                                    notCliped = true;
                                                                                else if (opcode != 75) {
                                                                                    if (opcode != 77 &&
                                                                                            opcode != 92) {
                                                                                        if (opcode == 78) {
                                                                                            anInt3860 = stream
                                                                                                    .readUnsignedShort();
                                                                                            anInt3904 = stream
                                                                                                    .readUnsignedByte();
                                                                                        } else if (opcode != 79) {
                                                                                            if (opcode == 81) {
                                                                                                aByte3912 = (byte) 2;
                                                                                                anInt3882 = 256 * stream
                                                                                                        .readUnsignedByte();
                                                                                            } else if (opcode != 82) {
                                                                                                if (opcode == 88)
                                                                                                    aBoolean3853 = false;
                                                                                                else if (opcode != 89) {
                                                                                                    if (opcode == 90)
                                                                                                        aBoolean3870 = true;
                                                                                                    else if (opcode != 91) {
                                                                                                        if (opcode != 93) {
                                                                                                            if (opcode == 94)
                                                                                                                aByte3912 = (byte) 4;
                                                                                                            else if (opcode != 95) {
                                                                                                                if (opcode != 96) {
                                                                                                                    if (opcode == 97)
                                                                                                                        aBoolean3866 = true;
                                                                                                                    else if (opcode == 98)
                                                                                                                        aBoolean3923 = true;
                                                                                                                    else if (opcode == 99) {
                                                                                                                        anInt3857 = stream
                                                                                                                                .readUnsignedByte();
                                                                                                                        anInt3835 = stream
                                                                                                                                .readUnsignedShort();
                                                                                                                    } else if (opcode == 100) {
                                                                                                                        anInt3844 = stream
                                                                                                                                .readUnsignedByte();
                                                                                                                        anInt3913 = stream
                                                                                                                                .readUnsignedShort();
                                                                                                                    } else if (opcode != 101) {
                                                                                                                        if (opcode == 102)
                                                                                                                            anInt3838 = stream
                                                                                                                                    .readUnsignedShort();
                                                                                                                        else if (opcode == 103)
                                                                                                                            thirdInt = 0;
                                                                                                                        else if (opcode != 104) {
                                                                                                                            if (opcode == 105)
                                                                                                                                aBoolean3906 = true;
                                                                                                                            else if (opcode == 106) {
                                                                                                                                int i_55_ = stream
                                                                                                                                        .readUnsignedByte();
                                                                                                                                anIntArray3869 = new int[i_55_];
                                                                                                                                anIntArray3833 = new int[i_55_];
                                                                                                                                for (int i_56_ = 0; i_56_ < i_55_; i_56_++) {
                                                                                                                                    anIntArray3833[i_56_] = stream
                                                                                                                                            .readUnsignedShort();
                                                                                                                                    int i_57_ = stream
                                                                                                                                            .readUnsignedByte();
                                                                                                                                    anIntArray3869[i_56_] = i_57_;
                                                                                                                                    anInt3881 += i_57_;
                                                                                                                                }
                                                                                                                            } else if (opcode == 107)
                                                                                                                                anInt3851 = stream
                                                                                                                                        .readUnsignedShort();
                                                                                                                            else if (opcode >= 150 && opcode < 155) {
                                                                                                                                options[opcode + -150] = stream.readString();
                                                                                                                                /*if (!loader.showOptions)
                                                                                                                                	options[opcode	+ -150] = null;*/
                                                                                                                            } else if (opcode != 160) {
                                                                                                                                if (opcode == 162) {
                                                                                                                                    aByte3912 = (byte) 3;
                                                                                                                                    anInt3882 = stream
                                                                                                                                            .readInt();
                                                                                                                                } else if (opcode == 163) {
                                                                                                                                    aByte3847 = (byte) stream
                                                                                                                                            .readByte();
                                                                                                                                    aByte3849 = (byte) stream
                                                                                                                                            .readByte();
                                                                                                                                    aByte3837 = (byte) stream
                                                                                                                                            .readByte();
                                                                                                                                    aByte3914 = (byte) stream
                                                                                                                                            .readByte();
                                                                                                                                } else if (opcode != 164) {
                                                                                                                                    if (opcode != 165) {
                                                                                                                                        if (opcode != 166) {
                                                                                                                                            if (opcode == 167)
                                                                                                                                                anInt3921 = stream
                                                                                                                                                        .readUnsignedShort();
                                                                                                                                            else if (opcode != 168) {
                                                                                                                                                if (opcode == 169) {
                                                                                                                                                    aBoolean3845 = true;
                                                                                                                                                    //added opcode
                                                                                                                                                } else if (opcode == 170) {
                                                                                                                                                    int anInt3383 = stream.readUnsignedSmart();
                                                                                                                                                    //added opcode
                                                                                                                                                } else if (opcode == 171) {
                                                                                                                                                    int anInt3362 = stream.readUnsignedSmart();
                                                                                                                                                    //added opcode
                                                                                                                                                } else if (opcode == 173) {
                                                                                                                                                    int anInt3302 = stream.readUnsignedShort();
                                                                                                                                                    int anInt3336 = stream.readUnsignedShort();
                                                                                                                                                    //added opcode
                                                                                                                                                } else if (opcode == 177) {
                                                                                                                                                    boolean ub = true;
                                                                                                                                                    //added opcode
                                                                                                                                                } else if (opcode == 178) {
                                                                                                                                                    int db = stream.readUnsignedByte();
                                                                                                                                                } else if (opcode == 249) {
                                                                                                                                                    int i_58_ = stream.readUnsignedByte();
                                                                                                                                                    if (clientScriptData == null)
                                                                                                                                                        clientScriptData = new HashMap<Integer, Object>(i_58_);
                                                                                                                                                    for (int i_60_ = 0; i_60_ < i_58_; i_60_++) {
                                                                                                                                                        boolean bool = stream.readUnsignedByte() == 1;
                                                                                                                                                        int key = stream.read24BitInt();
                                                                                                                                                        Object value;
                                                                                                                                                        if (bool)
                                                                                                                                                            value = stream.readString();
                                                                                                                                                        else
                                                                                                                                                            value = stream.readInt();
                                                                                                                                                        clientScriptData.put(key, value);
                                                                                                                                                    }
                                                                                                                                                }
                                                                                                                                            } else
                                                                                                                                                aBoolean3894 = true;
                                                                                                                                        } else
                                                                                                                                            anInt3877 = stream
                                                                                                                                                    .readShort();
                                                                                                                                    } else
                                                                                                                                        anInt3875 = stream
                                                                                                                                                .readShort();
                                                                                                                                } else
                                                                                                                                    anInt3834 = stream
                                                                                                                                            .readShort();
                                                                                                                            } else {
                                                                                                                                int i_62_ = stream
                                                                                                                                        .readUnsignedByte();
                                                                                                                                anIntArray3908 = new int[i_62_];
                                                                                                                                for (int i_63_ = 0; i_62_ > i_63_; i_63_++)
                                                                                                                                    anIntArray3908[i_63_] = stream
                                                                                                                                            .readUnsignedShort();
                                                                                                                            }
                                                                                                                        } else
                                                                                                                            anInt3865 = stream
                                                                                                                                    .readUnsignedByte();
                                                                                                                    } else
                                                                                                                        anInt3850 = stream
                                                                                                                                .readUnsignedByte();
                                                                                                                } else
                                                                                                                    aBoolean3924 = true;
                                                                                                            } else {
                                                                                                                aByte3912 = (byte) 5;
                                                                                                                anInt3882 = stream
                                                                                                                        .readShort();
                                                                                                            }
                                                                                                        } else {
                                                                                                            aByte3912 = (byte) 3;
                                                                                                            anInt3882 = stream
                                                                                                                    .readUnsignedShort();
                                                                                                        }
                                                                                                    } else
                                                                                                        aBoolean3873 = true;
                                                                                                } else
                                                                                                    aBoolean3895 = false;
                                                                                            } else
                                                                                                aBoolean3891 = true;
                                                                                        } else {
                                                                                            anInt3900 = stream
                                                                                                    .readUnsignedShort();
                                                                                            anInt3905 = stream
                                                                                                    .readUnsignedShort();
                                                                                            anInt3904 = stream
                                                                                                    .readUnsignedByte();
                                                                                            int i_64_ = stream
                                                                                                    .readUnsignedByte();
                                                                                            anIntArray3859 = new int[i_64_];
                                                                                            for (int i_65_ = 0; i_65_ < i_64_; i_65_++)
                                                                                                anIntArray3859[i_65_] = stream
                                                                                                        .readUnsignedShort();
                                                                                        }
                                                                                    } else {
                                                                                        configFileId = stream
                                                                                                .readUnsignedShort();
                                                                                        if (configFileId == 65535)
                                                                                            configFileId = -1;
                                                                                        configId = stream
                                                                                                .readUnsignedShort();
                                                                                        if (configId == 65535)
                                                                                            configId = -1;
                                                                                        int i_66_ = -1;
                                                                                        if (opcode == 92) {
                                                                                            i_66_ = stream
                                                                                                    .readUnsignedShort();
                                                                                            if (i_66_ == 65535)
                                                                                                i_66_ = -1;
                                                                                        }
                                                                                        int i_67_ = stream
                                                                                                .readUnsignedByte();
                                                                                        childrenIds = new int[i_67_ -
                                                                                                -2];
                                                                                        for (int i_68_ = 0; i_67_ >= i_68_; i_68_++) {
                                                                                            childrenIds[i_68_] = stream
                                                                                                    .readUnsignedShort();
                                                                                            if (childrenIds[i_68_] == 65535)
                                                                                                childrenIds[i_68_] = -1;
                                                                                        }
                                                                                        childrenIds[i_67_ + 1] = i_66_;
                                                                                    }
                                                                                } else
                                                                                    anInt3855 = stream
                                                                                            .readUnsignedByte();
                                                                            } else
                                                                                anInt3915 = stream
                                                                                        .readShort() << 2;
                                                                        } else
                                                                            anInt3883 = stream
                                                                                    .readShort() << 2;
                                                                    } else
                                                                        anInt3917 = stream
                                                                                .readUnsignedShort();
                                                                } else
                                                                    anInt3841 = stream
                                                                            .readUnsignedShort();
                                                            } else
                                                                aBoolean3872 = false;
                                                        } else
                                                            aBoolean3839 = true;
                                                    } else {
                                                        int i_69_ = (stream
                                                                .readUnsignedByte());
                                                        aByteArray3858 = (new byte[i_69_]);
                                                        for (int i_70_ = 0; i_70_ < i_69_; i_70_++)
                                                            aByteArray3858[i_70_] = (byte) (stream
                                                                    .readByte());
                                                    }
                                                } else {
                                                    int i_71_ = (stream
                                                            .readUnsignedByte());
                                                    originalTextureColors = new short[i_71_];
                                                    modifiedTextureColors = new short[i_71_];
                                                    for (int i_72_ = 0; i_71_ > i_72_; i_72_++) {
                                                        originalTextureColors[i_72_] = (short) (stream
                                                                .readUnsignedShort());
                                                        modifiedTextureColors[i_72_] = (short) (stream
                                                                .readUnsignedShort());
                                                    }
                                                }
                                            } else
                                                options[-30 +
                                                        opcode] = (stream
                                                        .readString());
                                        } else
                                            anInt3840 = (stream.readByte() * 5);
                                    } else
                                        anInt3878 = stream.readByte();
                                } else {
                                    anInt3876 = stream.readUnsignedShort();
                                    if (anInt3876 == 65535)
                                        anInt3876 = -1;
                                }
                            } else
                                thirdInt = 1;
                        } else
                            aBoolean3867 = true;
                    } else
                        projectileCliped = false;
                } else
                    sizeY = stream.readUnsignedByte();
            } else
                sizeX = stream.readUnsignedByte();
        } else {
            name = stream.readString();
        }
    }

    private void readOpcodeValues(InputStream stream) {
        while (true) {
            int opcode = stream.readUnsignedByte();
            if (opcode == 0)
                break;
            readValues(stream, opcode);
        }
    }

    private void setDefaultsVariableValues() {
        anInt3835 = -1;
        anInt3860 = -1;
        configFileId = -1;
        aBoolean3866 = false;
        anInt3851 = -1;
        anInt3865 = 255;
        aBoolean3845 = false;
        aBoolean3867 = false;
        anInt3850 = 0;
        anInt3844 = -1;
        anInt3881 = 0;
        anInt3857 = -1;
        aBoolean3872 = true;
        anInt3882 = -1;
        anInt3834 = 0;
        anInt3875 = 0;
        aBoolean3839 = false;
        anIntArray3869 = null;
        sizeY = 1;
        thirdInt = -1;
        projectileCliped = true;
        anInt3883 = 0;
        aBoolean3895 = true;
        anInt3840 = 0;
        aBoolean3870 = false;
        anInt3889 = 0;
        aBoolean3853 = true;
        secondBool = false;
        clipType = 2;
        anInt3855 = -1;
        anInt3878 = 0;
        anInt3904 = 0;
        sizeX = 1;
        anInt3876 = -1;
        notCliped = false;
        aBoolean3891 = false;
        anInt3905 = 0;
        name = "null";
        anInt3913 = -1;
        aBoolean3906 = false;
        aBoolean3873 = false;
        aByte3914 = (byte) 0;
        anInt3915 = 0;
        anInt3900 = 0;
        secondInt = -1;
        aBoolean3894 = false;
        aByte3912 = (byte) 0;
        anInt3921 = 0;
        anInt3902 = 128;
        configId = -1;
        anInt3877 = 0;
        anInt3925 = 0;
        anInt3892 = 64;
        aBoolean3923 = false;
        aBoolean3924 = false;
        anInt3841 = 128;
        anInt3917 = 128;
    }

    public int getArchiveId() {
        return id >>> 1998118472;
    }

    public int getFileId() {
        return id & 0xff;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void loadObjectDefinitions(Cache cache) {
        byte[] data = cache.getIndexes()[16].getFile(getArchiveId(), getFileId());
        if (data == null) {
            return;
        }
        readOpcodeValues(new InputStream(data));
    }

    @Override
    public String toString() {
        return id + " - " + name;
    }

    public int getClipType() {
        return clipType;
    }

    public boolean isProjectileClipped() {
        return projectileCliped;
    }

    public int getSizeX() {
        return sizeX;
    }

    public int getSizeY() {
        return sizeY;
    }

    public boolean containsOption(int i, String option) {
        if (options == null || options[i] == null || options.length <= i)
            return false;
        return options[i].equals(option);
    }

    public boolean containsOption(String o) {
        if (options == null)
            return false;
        for (String option : options) {
            if (option == null)
                continue;
            if (option.equalsIgnoreCase(o))
                return true;
        }
        return false;
    }

    public String getOption(int option) {
        if (options == null || options.length < option || option == 0)
            return "";
        return options[option - 1];
    }

    public Object clone() {
        try {
            return super.clone();
        } catch (CloneNotSupportedException var2) {
            var2.printStackTrace();
            return null;
        }
    }

    public void setModels(int[] models) {
        this.models = models;
    }

    public int getConfigFileId() {
        return configFileId;
    }

    public void setConfigFileId(int configFileId) {
        this.configFileId = configFileId;
    }

    public int getConfigId() {
        return configId;
    }

    public void setConfigId(int configId) {
        this.configId = configId;
    }

    public void readConfigData(InputStream stream) throws IOException {
        configFileId = stream.readUnsignedShort();
        if (configFileId == 65535)
            configFileId = -1;

        configId = stream.readUnsignedShort();
        if (configId == 65535)
            configId = -1;
    }

    public boolean getClipped() {
        return notCliped;
    }

    public void setClipped(boolean notClipped) {
        this.notCliped = notClipped;
    }

    public boolean isWalkable() {
        return aBoolean3870;
    }

    public boolean isSolid() {
        return aBoolean3853;
    }

    public boolean isInteractive() {
        return aBoolean3867;
    }

    public boolean castsShadow() {
        return aBoolean3845;
    }

    public boolean blocksProjectile() {
        return aBoolean3866;
    }

    public void setWalkable(boolean walkable) {
        this.aBoolean3870 = walkable;
    }

    public void setSolid(boolean solid) {
        this.aBoolean3853 = solid;
    }

    public void setInteractive(boolean interactive) {
        this.aBoolean3867 = interactive;
    }

    public void setCastsShadow(boolean castsShadow) {
        this.aBoolean3845 = castsShadow;
    }

    public void setBlockProjectile(boolean blockProjectile) {
        this.aBoolean3866 = blockProjectile;
    }

    public void setScaleX(int scaleX) {
        this.anInt3841 = scaleX;
    }

    public void setScaleY(int scaleY) {
        this.anInt3917 = scaleY;
    }

    public void setScaleZ(int scaleZ) {
        this.anInt3902 = scaleZ;
    }

    public void setRotationX(int rotationX) {
        this.anInt3840 = rotationX;
    }

    public void setRotationY(int rotationY) {
        this.anInt3878 = rotationY;
    }

    public void setRotationZ(int rotationZ) {
        this.anInt3876 = rotationZ;
    }

    public void setAnimationId(int animationId) {
        this.anInt3855 = animationId;
    }

    public void setSizeX(int sizeX) {
        this.sizeX = sizeX;
    }

    public void setSizeY(int sizeY) {
        this.sizeY = sizeY;
    }

    public void setHeightOffsetX(int heightOffsetX) {
        this.anInt3883 = heightOffsetX;
    }

    public void setHeightOffsetY(int heightOffsetY) {
        this.anInt3915 = heightOffsetY;
    }

    public void setOptions(String[] options) {
        this.options = options;
    }

    public void setChildrenIds(int[] childrenIds) {
        this.childrenIds = childrenIds;
    }

    public void setOriginalModelColors(short[] originalModelColors) {
        this.originalModelColors = originalModelColors;
    }

    public void setModifiedModelColors(short[] modifiedModelColors) {
        this.modifiedModelColors = modifiedModelColors;
    }

    public void setOriginalTextureColors(short[] originalTextureColors) {
        this.originalTextureColors = originalTextureColors;
    }

    public void setModifiedTextureColors(short[] modifiedTextureColors) {
        this.modifiedTextureColors = modifiedTextureColors;
    }

    public void setClipType(int clipType) {
        this.clipType = clipType;
    }
}