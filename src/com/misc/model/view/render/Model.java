package com.misc.model.view.render;

import com.cache.io.InputStream;
import com.cache.io.OutputStream;

public class Model implements Cloneable {
    public static int[] anIntArray1490;
    public static int anInt1492;
    //public byte[] faceRenderType; aByteArray1493
    public static int anInt1494 = 0;
    public static int anInt1497;
    public static int anInt1498;
    public static int anInt1503;
    //public byte[] faceAlpha; aByteArray1505
    public static int anInt1507;
    public static int anInt1508;
    public static int anInt1509;
    public static int anInt1510;
    //public short[] colorValues; modelColors
    public static int anInt1517;
    public static int[] anIntArray1518;
    public static int anInt1519;
    //public static int anInt1526;
    public static int anInt1527;
    //public int[] vertexX;
    //public int[] vertexZ;
    //public int numTriangles1;
    //public short[] texTrianglesPoint2; textureTriangleMIndex
    public static int anInt1538;
    public static int anInt1539;
    public boolean isNewHeader = false;
    public Surface[] surfaces;
    public int modelId = 0;
    //public int[] vertexY;
    public byte[] aByteArray1491;
    public int numTexTriangles = 0;
    public short[] aShortArray1496;
    public int[] anIntArray1500;
    //public short[] texTrianglesPoint1; textureTriangleNIndex
    public short[] aShortArray1502;
    public short[] aShortArray1504;
    /// public short[] texTrianglesPoint3; textureTrianglePIndex
    //public short[] aShortArray1513; triangleViewspaceY
    //public short[] aShortArray1514; triangleViewspaceX
    public byte[] aByteArray1515;
    //public short[] faceTexture;
    public byte[] aByteArray1521;
    //public byte[] trianglePriorities;
    public byte[] aByteArray1523;
    //public int[] anIntArray1524;//faceTexture
    public short[] aShortArray1525;
    //public int numVertices1;
    public byte[] aByteArray1529;
    public short[] aShortArray1530;
    public byte[] aByteArray1532;
    //public byte[] textureCoords;
    //public short[] aShortArray1541; triangleViewspaceZ
    public boolean isNewModel;


    public int[] anIntArray1231;
    public int anInt1234;
    public byte[] aByteArray1266;
    public byte[] aByteArray1241;
    public byte[] aByteArray1243;
    public float[] texCoordU;
    public float[] texCoordV;
    public boolean newFormat;
    public int version = 12;
    public short[] triangleViewspaceX;
    public short[] triangleViewspaceY;
    public short[] triangleViewspaceZ;
    public short[] textureTrianglePIndex;
    public short[] textureTriangleMIndex;
    public short[] textureTriangleNIndex;
    public int[] verticesX;
    public int[] verticesY;
    public int[] verticesZ;
    public short[] faceColors;
    public short[] faceTexture;
    public int[] vertexSkins;
    public byte[] hasTexture;
    public byte[] faceRenderType;
    public byte[] textureRenderTypes;
    public byte[] faceAlpha;
    public byte[] trianglePriorities;
    public short[] triangleSkinValues;
    public int numVertices;
    public int numTriangles;
    public int numTextureTriangles;
    public int maxDepth;
    public byte priority;
    public int[] particleDirectionZ;
    public byte[] particleLifespanY;
    public int[] particleDirectionX;
    public byte[] particleLifespanX;
    public int[] particleLifespanZ;
    public int[] particleDirectionY;
    public int[] texturePrimaryColor;
    public int[] textureSecondaryColor;
    /*public Surface[] surfaces;
    public SurfaceSkin[] surfaceSkins;
    public VertexNormal[] isolatedVertexNormals;*/
    //private boolean useEffects = true;
    public int[] temp_i_120_;

    public int temp_isolated_normal_count;
    public int[] temp_face;
    public int[] temp_face_point;

    public int temp_skinAmount;
    public int[] temp_skin;
    public int[] temp_skin_point;

    public Model(byte[] data) {
        this(0, data);
    }

    public Model(int modelId, byte[] data) {
        this.modelId = modelId;
        maxDepth = 0;
        priority = (byte) 0;
        numVertices = 0;
        numTriangles = 0;
        try {
            if (data[data.length - 1] == -1 && data[data.length - 2] == -1) {
                //decodeNew(arg0);
                newHeader(data, 23);
            } else {
                oldHeader(true, data);
            }
        } catch (RuntimeException runtimeexception) {
            runtimeexception.printStackTrace();
        }
    }

    public byte[] encode(Model model) {
        OutputStream stream = new OutputStream();
        stream.writeShort(numVertices);
        stream.writeShort(numTriangles);
        stream.writeByte(numTexTriangles);
        int hash = 0;
        if (faceRenderType != null) hash = 1;
        if (temp_isolated_normal_count != 0 || temp_skinAmount != 0) hash += 2;

        stream.writeByte(hash);
        //add has_large_size
        //if has_large_size
        //stream.writeByte(newformat);
        stream.writeByte(trianglePriorities != null ? 255 : 0);
        stream.writeByte(faceAlpha != null ? 1 : 0);
        stream.writeByte(triangleSkinValues != null ? 1 : 0);
        stream.writeByte(faceTexture != null ? 1 : 0);
        stream.writeByte(anIntArray1500 != null ? 1 : 0);
		
		/* need to add
		 int modelVerticesX = stream.readUnsignedShort();
		int modelVerticesY = stream.readUnsignedShort();
		int modelVerticesZ = stream.readUnsignedShort();
		int modelVertexPoint = stream.readUnsignedShort();
		int modelTextureCoordinates = stream.readUnsignedShort();
		 */
        if (numTexTriangles > 0) {
            for (int i = 0; i < numTexTriangles; i++) {
                stream.writeByte(aByteArray1521[i]);
            }
        }
        if (numVertices > 0) {
            for (int i = 0; i < numVertices; i++) {
                stream.writeByte(temp_i_120_[i]);
                if (anIntArray1500 != null) stream.writeByte(anIntArray1500[i]);
            }
        }
        for (int i = 0; i < numTriangles; i++) {
            stream.writeShort(faceColors[i]);
            if (faceRenderType != null) stream.writeByte(faceRenderType[i]);
            if (trianglePriorities != null) stream.writeByte(trianglePriorities[i]);
            if (faceAlpha != null) stream.writeByte(faceAlpha[i]);
            if (triangleSkinValues != null) stream.writeByte(triangleSkinValues[i]);
            if (faceTexture != null) stream.writeShort(faceTexture[i]);
            if (hasTexture != null) if (faceTexture[i] != -1) stream.writeByte(hasTexture[i]);
        }

        for (int i_129_ = 0; i_129_ < numTriangles; i_129_++) {
            ///????
        }

        for (int i = 0; i < numTexTriangles; i++) {
            int opcode = 0xff & aByteArray1521[i];
            if (opcode == 0) {
                stream.writeShort(textureTriangleNIndex[i]);
                stream.writeShort(textureTriangleMIndex[i]);
                stream.writeShort(textureTrianglePIndex[i]);
            } else if (opcode > 0) {
				/*stream.writeShort(textureTriangleNIndex[i_132_]);//stream_72_
				stream.writeShort(textureTriangleMIndex[i_132_]);//stream_72_
				stream.writeShort(textureTrianglePIndex[i_132_]);//stream_72_
				//new
				  if (newformat < 15) {
					  stream.writeShort(aShortArray1496[i_132_]);//stream_73_
			          if (newformat >= 14)
			        	  stream.writeMedium(aShortArray1530[i_132_]);//stream_73_
			            else
			            	stream.writeShort(aShortArray1530[i_132_]);//stream_73_
		            	stream.writeShort(aShortArray1504[i_132_]);//stream_73_
				  } else {
					  stream.writeMedium(aShortArray1496[i_132_]);//stream_73_
					  stream.writeMedium(aShortArray1530[i_132_]);//stream_73_
					  stream.writeMedium(aShortArray1504[i_132_]);//stream_73_
				  }
				  //
				  stream.writeByte(aByteArray1523[i_132_]);//stream_74_
				  stream.writeByte(aByteArray1529[i_132_]);//stream_75_
				  stream.writeByte(aByteArray1515[i_132_]);//stream_76_
				  if (i_133_ == 2) {
					  stream.writeByte(aByteArray1491[i_132_]);//stream_76_
					  stream.writeByte(aByteArray1532[i_132_]);//stream_76_
				  }*/
            }
        }

        if (temp_isolated_normal_count > 0 || temp_skinAmount > 0) {
            stream.writeByte(temp_isolated_normal_count);
            if (temp_isolated_normal_count > 0) {
                for (int i = 0; i < temp_face.length; i++) {
                    stream.writeShort(temp_face[i]);
                    stream.writeShort(temp_face_point[i]);
                }
            }
            stream.writeByte(temp_skinAmount);
            if (temp_skinAmount > 0) {
                for (int i = 0; i < temp_face.length; i++) {
                    stream.writeShort(temp_skin[i]);
                    stream.writeShort(temp_skin_point[i]);
                }
            }
        }

        // write model back into bytes
        //end
        stream.writeByte(0);

        byte[] data = new byte[stream.getOffset()];
        stream.setOffset(0);
        stream.getBytes(data, 0, data.length);
        return data;
    }

    @SuppressWarnings("unused")
    public void newHeader(byte[] arg0, int arg1) {
        isNewHeader = true;
        //anInt1526++;
        InputStream stream = new InputStream(arg0);
        InputStream stream_72_ = new InputStream(arg0);
        InputStream stream_73_ = new InputStream(arg0);
        InputStream stream_74_ = new InputStream(arg0);
        InputStream stream_75_ = new InputStream(arg0);
        InputStream stream_76_ = new InputStream(arg0);
        InputStream stream_77_ = new InputStream(arg0);
        stream.offset = arg0.length - arg1;
        numVertices = stream.readUnsignedShort();
        numTriangles = stream.readUnsignedShort();
        numTexTriangles = stream.readUnsignedByte();
        int hash = stream.readUnsignedByte();
        //if (hash > 0)
        //System.out.println(hash + " : " + (0x1 & hash) + " : " + (0x2 & hash));
        boolean has_fill_attr = (0x1 & hash) == 1;
        boolean has_surface_fx = (hash & 0x2) == 2;

        //new
        boolean has_vertex_normals = (0x4 & hash) == 4;
        boolean has_large_size = (0x8 & hash) == 8;
        int newformat = 0;
        if (has_large_size) {
            stream.offset -= 7;
            newformat = stream.readUnsignedByte();
            stream.offset += 6;
        }
        if (has_large_size || has_vertex_normals) isNewModel = true;
        //

        int model_priority = stream.readUnsignedByte();
        int has_alpha = stream.readUnsignedByte();
        int has_triangle_skin_types = stream.readUnsignedByte();
        int has_textures = stream.readUnsignedByte();
        int has_skins = stream.readUnsignedByte();
        int modelVerticesX = stream.readUnsignedShort();
        int modelVerticesY = stream.readUnsignedShort();
        int modelVerticesZ = stream.readUnsignedShort();
        int modelVertexPoint = stream.readUnsignedShort();
        int modelTextureCoordinates = stream.readUnsignedShort();
        int i_89_ = 0;
        int particle_count = 0;
        int particle_color = 0;
        if (numTexTriangles > 0) {
            stream.offset = 0;
            aByteArray1521 = new byte[numTexTriangles];
            for (int i_92_ = 0; i_92_ < numTexTriangles; i_92_++) {
                byte i_93_ = aByteArray1521[i_92_] = (byte) stream.readByte();
                if ((i_93_) == 2) particle_color++;
                if ((i_93_) == 0) i_89_++;
                if ((i_93_ ^ 0xffffffff) <= -2 && (i_93_ ^ 0xffffffff) >= -4) particle_count++;
            }
        }
        int tex_tri_count = numTexTriangles;
        int i_95_ = tex_tri_count;
        tex_tri_count += numVertices;
        int i_96_ = tex_tri_count;
        if (has_fill_attr) tex_tri_count += numTriangles;
        int i_97_ = tex_tri_count;
        tex_tri_count += numTriangles;
        int i_98_ = tex_tri_count;
        if (model_priority == 255) tex_tri_count += numTriangles;
        int i_99_ = tex_tri_count;
        if (has_triangle_skin_types == 1) tex_tri_count += numTriangles;
        int i_100_ = tex_tri_count;
        if (has_skins == 1) tex_tri_count += numVertices;
        int i_101_ = tex_tri_count;
        if (has_alpha == 1) tex_tri_count += numTriangles;
        int i_102_ = tex_tri_count;
        tex_tri_count += modelVertexPoint;
        int i_103_ = tex_tri_count;
        if (has_textures == 1) tex_tri_count += numTriangles * 2;
        int i_104_ = tex_tri_count;
        tex_tri_count += modelTextureCoordinates;
        int i_105_ = tex_tri_count;
        tex_tri_count += numTriangles * 2;
        int i_106_ = tex_tri_count;
        tex_tri_count += modelVerticesX;
        int i_107_ = tex_tri_count;
        tex_tri_count += modelVerticesY;
        int i_108_ = tex_tri_count;
        tex_tri_count += modelVerticesZ;
        int i_109_ = tex_tri_count;
        tex_tri_count += i_89_ * 6;
        int i_110_ = tex_tri_count;
        tex_tri_count += 6 * particle_count;
        int i_111_ = tex_tri_count;

        //added
        int newValue = 6;
        if (newformat != 14) {
            if (newformat >= 15) newValue = 9;
        } else newValue = 7;
        tex_tri_count += particle_count * newValue;
        //i_94_ += i_90_ * 6;

        int i_112_ = tex_tri_count;
        tex_tri_count += particle_count;
        int i_113_ = tex_tri_count;
        tex_tri_count += particle_count;
        int i_114_ = tex_tri_count;
        tex_tri_count += particle_count - -(particle_color * 2);
        if (has_alpha == 1) faceAlpha = new byte[numTriangles];
        if (has_fill_attr) faceRenderType = new byte[numTriangles];//faceRenderType
        if (model_priority == 255) trianglePriorities = new byte[numTriangles];
        else priority = (byte) model_priority;
        if (has_skins == 1) anIntArray1500 = new int[numVertices];
        verticesY = new int[numVertices];
        if (numTexTriangles > 0) {
            if (particle_count > 0) {
                aShortArray1530 = new short[particle_count];
                aByteArray1529 = new byte[particle_count];
                aShortArray1496 = new short[particle_count];
                aShortArray1504 = new short[particle_count];
                aByteArray1515 = new byte[particle_count];
                aByteArray1523 = new byte[particle_count];
            }
            if (particle_color > 0) {
                aByteArray1532 = new byte[particle_color];
                aByteArray1491 = new byte[particle_color];
            }
            textureTrianglePIndex = new short[numTexTriangles];
            textureTriangleMIndex = new short[numTexTriangles];
            textureTriangleNIndex = new short[numTexTriangles];
        }
        verticesX = new int[numVertices];
        if (has_triangle_skin_types == 1) this.triangleSkinValues = new short[numTriangles];
        int i_115_ = tex_tri_count;
        stream.offset = i_95_;
        verticesZ = new int[numVertices];
        triangleViewspaceY = new short[numTriangles];
        if (has_textures == 1) faceTexture = new short[numTriangles];
        triangleViewspaceX = new short[numTriangles];
        if (has_textures == 1 && numTexTriangles > 0) hasTexture = new byte[numTriangles];
        faceColors = new short[numTriangles];
        triangleViewspaceZ = new short[numTriangles];
        stream_72_.offset = i_106_;
        stream_73_.offset = i_107_;
        stream_74_.offset = i_108_;
        stream_75_.offset = i_100_;
        int i_116_ = 0;
        int i_117_ = 0;
        int i_118_ = 0;
        temp_i_120_ = new int[numVertices];
        for (int i_119_ = 0; i_119_ < numVertices; i_119_++) {
            temp_i_120_[i_119_] = stream.readUnsignedByte();
            int i_121_ = 0;
            if ((temp_i_120_[i_119_] & 0x1) != 0) i_121_ = stream_72_.readUnsignedSmart3();
            int i_122_ = 0;
            if ((0x2 & temp_i_120_[i_119_]) != 0) i_122_ = stream_73_.readUnsignedSmart3();
            int i_123_ = 0;
            if ((0x4 & temp_i_120_[i_119_]) != 0) i_123_ = stream_74_.readUnsignedSmart3();
            verticesX[i_119_] = i_116_ - -i_121_;
            verticesY[i_119_] = i_122_ + i_117_;
            verticesZ[i_119_] = i_123_ + i_118_;
            i_117_ = verticesY[i_119_];
            i_116_ = verticesX[i_119_];
            i_118_ = verticesZ[i_119_];
            if (has_skins == 1) anIntArray1500[i_119_] = stream_75_.readUnsignedByte();
        }
        stream.offset = i_105_;
        stream_72_.offset = i_96_;
        stream_73_.offset = i_98_;
        stream_74_.offset = i_101_;
        stream_75_.offset = i_99_;
        stream_76_.offset = i_103_;
        stream_77_.offset = i_104_;
        for (int tri_x = 0; (numTriangles ^ 0xffffffff) < (tri_x ^ 0xffffffff); tri_x++) {
            faceColors[tri_x] = (short) stream.readUnsignedShort();
            if (has_fill_attr) this.faceRenderType[tri_x] = (byte) stream_72_.readByte();//faceRenderType
            if (model_priority == 255) trianglePriorities[tri_x] = (byte) stream_73_.readByte();//trianglePriorities
            if (has_alpha == 1) faceAlpha[tri_x] = (byte) stream_74_.readByte();//faceAlpha
            if (has_triangle_skin_types == 1)
                this.triangleSkinValues[tri_x] = (short) stream_75_.readUnsignedByte();//triangleSkinValues
            if (has_textures == 1) faceTexture[tri_x] = (short) (-1 + stream_76_.readUnsignedShort());//faceTextures
            if (hasTexture != null) {//textureCoords
                if (faceTexture[tri_x] == -1) hasTexture[tri_x] = (byte) -1;
                else hasTexture[tri_x] = (byte) (stream_77_.readUnsignedByte() + -1);
            }
        }

        stream.offset = i_102_;
        maxDepth = -1;
        stream_72_.offset = i_97_;
        short i_125_ = 0;
        short i_126_ = 0;
        short i_127_ = 0;
        int i_128_ = 0;
        for (int i_129_ = 0; i_129_ < numTriangles; i_129_++) {
            int i_130_ = stream_72_.readUnsignedByte();
            if (i_130_ == 1) {
                i_125_ = (short) (stream.readUnsignedSmart3() + i_128_);
                i_128_ = i_125_;
                i_126_ = (short) (stream.readUnsignedSmart3() + i_128_);
                i_128_ = i_126_;
                i_127_ = (short) (stream.readUnsignedSmart3() + i_128_);
                i_128_ = i_127_;
                triangleViewspaceX[i_129_] = i_125_;
                triangleViewspaceY[i_129_] = i_126_;
                triangleViewspaceZ[i_129_] = i_127_;
                if ((i_125_ ^ 0xffffffff) < (maxDepth ^ 0xffffffff)) maxDepth = i_125_;
                if (i_126_ > maxDepth) maxDepth = i_126_;
                if (i_127_ > maxDepth) maxDepth = i_127_;
            }
            if (i_130_ == 2) {
                i_126_ = i_127_;
                i_127_ = (short) (stream.readUnsignedSmart3() + i_128_);
                i_128_ = i_127_;
                triangleViewspaceX[i_129_] = i_125_;
                triangleViewspaceY[i_129_] = i_126_;
                triangleViewspaceZ[i_129_] = i_127_;
                if ((i_127_ ^ 0xffffffff) < (maxDepth ^ 0xffffffff)) maxDepth = i_127_;
            }
            if (i_130_ == 3) {
                i_125_ = i_127_;
                i_127_ = (short) (i_128_ + stream.readUnsignedSmart3());
                i_128_ = i_127_;
                triangleViewspaceX[i_129_] = i_125_;
                triangleViewspaceY[i_129_] = i_126_;
                triangleViewspaceZ[i_129_] = i_127_;
                if ((i_127_ ^ 0xffffffff) < (maxDepth ^ 0xffffffff)) maxDepth = i_127_;
            }
            if (i_130_ == 4) {
                short i_131_ = i_125_;
                i_125_ = i_126_;
                i_126_ = i_131_;
                i_127_ = (short) (stream.readUnsignedSmart3() + i_128_);
                i_128_ = i_127_;
                triangleViewspaceX[i_129_] = i_125_;
                triangleViewspaceY[i_129_] = i_126_;
                triangleViewspaceZ[i_129_] = i_127_;
                if (maxDepth < i_127_) maxDepth = i_127_;
            }
            //System.out.print(triangleViewspaceZ[i_129_] + " ");
        }
        maxDepth++;
        stream.offset = i_109_;
        stream_72_.offset = i_110_;
        stream_73_.offset = i_111_;
        stream_74_.offset = i_112_;
        stream_75_.offset = i_113_;
        stream_76_.offset = i_114_;
        for (int i_132_ = 0; (numTexTriangles ^ 0xffffffff) < (i_132_ ^ 0xffffffff); i_132_++) {
            int i_133_ = 0xff & aByteArray1521[i_132_];
            if (i_133_ == 0) {
                textureTriangleNIndex[i_132_] = (short) stream.readUnsignedShort();
                textureTriangleMIndex[i_132_] = (short) stream.readUnsignedShort();
                textureTrianglePIndex[i_132_] = (short) stream.readUnsignedShort();
            }
            if (i_133_ == 1) {
                textureTriangleNIndex[i_132_] = (short) stream_72_.readUnsignedShort();
                textureTriangleMIndex[i_132_] = (short) stream_72_.readUnsignedShort();
                textureTrianglePIndex[i_132_] = (short) (stream_72_.readUnsignedShort());

                //new
                if (newformat < 15) {
                    aShortArray1496[i_132_] = (short) stream_73_.readUnsignedShort();
                    if (newformat >= 14) aShortArray1530[i_132_] = (short) stream_73_.read24BitInt();
                    else aShortArray1530[i_132_] = (short) stream_73_.readUnsignedShort();
                    aShortArray1504[i_132_] = (short) stream_73_.readUnsignedShort();
                } else {
                    aShortArray1496[i_132_] = (short) stream_73_.read24BitInt();
                    aShortArray1530[i_132_] = (short) stream_73_.read24BitInt();
                    aShortArray1504[i_132_] = (short) stream_73_.read24BitInt();
                }
                //
				
				/*aShortArray1496[i_132_] = (short) stream_73_.readUnsignedShort();
				aShortArray1530[i_132_] = (short) stream_73_.readUnsignedShort();
				aShortArray1504[i_132_] = (short) stream_73_.readUnsignedShort();*/
                aByteArray1523[i_132_] = (byte) stream_74_.readByte();
                aByteArray1529[i_132_] = (byte) stream_75_.readByte();
                aByteArray1515[i_132_] = (byte) stream_76_.readByte();
            }
            if (i_133_ == 2) {
                textureTriangleNIndex[i_132_] = (short) (stream_72_.readUnsignedShort());
                textureTriangleMIndex[i_132_] = (short) stream_72_.readUnsignedShort();
                textureTrianglePIndex[i_132_] = (short) stream_72_.readUnsignedShort();

                //new
                if (newformat < 15) {
                    aShortArray1496[i_132_] = (short) stream_73_.readUnsignedShort();
                    if (newformat >= 14) aShortArray1530[i_132_] = (short) stream_73_.read24BitInt();
                    else aShortArray1530[i_132_] = (short) stream_73_.readUnsignedShort();
                    aShortArray1504[i_132_] = (short) stream_73_.readUnsignedShort();
                } else {
                    aShortArray1496[i_132_] = (short) stream_73_.read24BitInt();
                    aShortArray1530[i_132_] = (short) stream_73_.read24BitInt();
                    aShortArray1504[i_132_] = (short) stream_73_.read24BitInt();
                }
                //
				
				/*aShortArray1496[i_132_] = (short) stream_73_.readUnsignedShort();
				aShortArray1530[i_132_] = (short) stream_73_.readUnsignedShort();
				aShortArray1504[i_132_] = (short) stream_73_.readUnsignedShort();*/
                aByteArray1523[i_132_] = (byte) stream_74_.readByte();
                aByteArray1529[i_132_] = (byte) stream_75_.readByte();
                aByteArray1515[i_132_] = (byte) stream_76_.readByte();
                aByteArray1491[i_132_] = (byte) stream_76_.readByte();
                aByteArray1532[i_132_] = (byte) stream_76_.readByte();
            }
            if (i_133_ == 3) {
                textureTriangleNIndex[i_132_] = (short) (stream_72_.readUnsignedShort());
                textureTriangleMIndex[i_132_] = (short) stream_72_.readUnsignedShort();
                textureTrianglePIndex[i_132_] = (short) (stream_72_.readUnsignedShort());

                //new
                if (newformat < 15) {
                    aShortArray1496[i_132_] = (short) stream_73_.readUnsignedShort();
                    if (newformat >= 14) aShortArray1530[i_132_] = (short) stream_73_.read24BitInt();
                    else aShortArray1530[i_132_] = (short) stream_73_.readUnsignedShort();
                    aShortArray1504[i_132_] = (short) stream_73_.readUnsignedShort();
                } else {
                    aShortArray1496[i_132_] = (short) stream_73_.read24BitInt();
                    aShortArray1530[i_132_] = (short) stream_73_.read24BitInt();
                    aShortArray1504[i_132_] = (short) stream_73_.read24BitInt();
                }
				
				/*aShortArray1496[i_132_] = (short) stream_73_.readUnsignedShort();
				aShortArray1530[i_132_] = (short) stream_73_.readUnsignedShort();
				aShortArray1504[i_132_] = (short) stream_73_.readUnsignedShort();*/
                aByteArray1523[i_132_] = (byte) stream_74_.readByte();
                aByteArray1529[i_132_] = (byte) stream_75_.readByte();
                aByteArray1515[i_132_] = (byte) stream_76_.readByte();
            }
        }
        if (has_surface_fx) {
            stream.offset = i_115_;
            temp_isolated_normal_count = stream.readUnsignedByte();
            if (temp_isolated_normal_count > 0) {
                surfaces = new Surface[temp_isolated_normal_count];
                temp_face = new int[temp_isolated_normal_count];
                temp_face_point = new int[temp_isolated_normal_count];
                for (int face = 0; face < temp_isolated_normal_count; face++) {
                    temp_face[face] = stream.readUnsignedShort();
                    temp_face_point[face] = stream.readUnsignedShort();
                    byte pri;
                    if (model_priority == 255) pri = trianglePriorities[temp_face_point[face]];
                    else pri = (byte) model_priority;
                    surfaces[face] = new Surface(temp_face[face], triangleViewspaceX[temp_face_point[face]], triangleViewspaceY[temp_face_point[face]], triangleViewspaceZ[temp_face_point[face]], pri);
                }
            }
            temp_skinAmount = stream.readUnsignedByte();
            if (temp_skinAmount > 0) {
                //aClass104Array1499 = new Class104[i_139_];
                temp_skin = new int[temp_skinAmount];
                temp_skin_point = new int[temp_skinAmount];
                for (int face = 0; face < temp_skinAmount; face++) {
                    temp_skin[face] = stream.readUnsignedShort();//added
                    temp_skin_point[face] = stream.readUnsignedShort();//added
                    //aClass104Array1499[i_140_] = new Class104(stream.readUnsignedShort(), stream.readUnsignedShort());//Class104 = SurfaceSkin
                }
            }
        }
    }

    public void oldHeader(boolean arg0, byte[] arg1) {
        anInt1519++;
        boolean bool = false;
        boolean bool_15_ = false;
        InputStream stream = new InputStream(arg1);
        InputStream stream_16_ = new InputStream(arg1);
        InputStream stream_17_ = new InputStream(arg1);
        InputStream stream_18_ = new InputStream(arg1);
        InputStream stream_19_ = new InputStream(arg1);
        stream.offset = -18 + arg1.length;
        numVertices = stream.readUnsignedShort();
        numTriangles = stream.readUnsignedShort();
        numTexTriangles = stream.readUnsignedByte();
        int i = stream.readUnsignedByte();
        int i_20_ = stream.readUnsignedByte();
        int i_21_ = stream.readUnsignedByte();
        int i_22_ = stream.readUnsignedByte();
        int i_23_ = stream.readUnsignedByte();
        int i_24_ = stream.readUnsignedShort();
        int i_25_ = stream.readUnsignedShort();
        int i_26_ = stream.readUnsignedShort();
        int i_27_ = stream.readUnsignedShort();
        int i_28_ = 0;
        int i_29_ = i_28_;
        i_28_ += numVertices;
        int i_30_ = i_28_;
        i_28_ += numTriangles;
        int i_31_ = i_28_;
        if ((i_20_ ^ 0xffffffff) == -256) i_28_ += numTriangles;
        int i_32_ = i_28_;
        if (i_22_ == 1) i_28_ += numTriangles;
        int i_33_ = i_28_;
        if ((i ^ 0xffffffff) == -2) i_28_ += numTriangles;
        int i_34_ = i_28_;
        if ((i_23_ ^ 0xffffffff) == -2) i_28_ += numVertices;
        int i_35_ = i_28_;
        if ((i_21_ ^ 0xffffffff) == -2) i_28_ += numTriangles;
        int i_36_ = i_28_;
        i_28_ += i_27_;
        int i_37_ = i_28_;
        i_28_ += numTriangles * 2;
        int i_38_ = i_28_;
        i_28_ += numTexTriangles * 6;
        int i_39_ = i_28_;
        i_28_ += i_24_;
        int i_40_ = i_28_;
        i_28_ += i_25_;
        int i_41_ = i_28_;
        if (i_21_ == 1) faceAlpha = new byte[numTriangles];
        faceColors = new short[numTriangles];
        stream.offset = i_29_;
        if ((i_22_ ^ 0xffffffff) == -2) this.triangleSkinValues = new short[numTriangles];
        verticesZ = new int[numVertices];
        if ((i_20_ ^ 0xffffffff) != -256) priority = (byte) i_20_;
        else trianglePriorities = new byte[numTriangles];
        i_28_ += i_26_;
        verticesX = new int[numVertices];
        verticesY = new int[numVertices];
        triangleViewspaceY = new short[numTriangles];
        if ((i ^ 0xffffffff) == -2) {
            faceRenderType = new byte[numTriangles];
            faceTexture = new short[numTriangles];
            hasTexture = new byte[numTriangles];
        }
        if ((numTexTriangles ^ 0xffffffff) < -1) {
            textureTrianglePIndex = new short[numTexTriangles];
            textureTriangleMIndex = new short[numTexTriangles];
            aByteArray1521 = new byte[numTexTriangles];
            textureTriangleNIndex = new short[numTexTriangles];
        }
        triangleViewspaceX = new short[numTriangles];
        triangleViewspaceZ = new short[numTriangles];
        if (i_23_ == 1) anIntArray1500 = new int[numVertices];
        stream_16_.offset = i_39_;
        stream_17_.offset = i_40_;
        stream_18_.offset = i_41_;
        stream_19_.offset = i_34_;
        int i_42_ = 0;
        int i_43_ = 0;
        int i_44_ = 0;
        for (int i_45_ = 0; i_45_ < numVertices; i_45_++) {
            int i_46_ = stream.readUnsignedByte();
            int i_47_ = 0;
            if ((i_46_ & 0x1 ^ 0xffffffff) != -1) i_47_ = stream_16_.readUnsignedSmart3();
            int i_48_ = 0;
            if ((i_46_ & 0x2) != 0) i_48_ = stream_17_.readUnsignedSmart3();
            int i_49_ = 0;
            if ((0x4 & i_46_ ^ 0xffffffff) != -1) i_49_ = stream_18_.readUnsignedSmart3();
            verticesX[i_45_] = i_42_ + i_47_;
            verticesY[i_45_] = i_43_ - -i_48_;
            verticesZ[i_45_] = i_44_ + i_49_;
            i_43_ = verticesY[i_45_];
            i_42_ = verticesX[i_45_];
            i_44_ = verticesZ[i_45_];
            if ((i_23_ ^ 0xffffffff) == -2) anIntArray1500[i_45_] = stream_19_.readUnsignedByte();
        }
        stream.offset = i_37_;
        stream_16_.offset = i_33_;
        stream_17_.offset = i_31_;
        stream_18_.offset = i_35_;
        stream_19_.offset = i_32_;
        for (int i_50_ = 0; (numTriangles ^ 0xffffffff) < (i_50_ ^ 0xffffffff); i_50_++) {
            faceColors[i_50_] = (short) stream.readUnsignedShort();
            if (i == 1) {
                int i_51_ = stream_16_.readUnsignedByte();
                if ((i_51_ & 0x1 ^ 0xffffffff) != -2) faceRenderType[i_50_] = (byte) 0;
                else {
                    bool = true;
                    faceRenderType[i_50_] = (byte) 1;
                }
                if ((i_51_ & 0x2 ^ 0xffffffff) == -3) {
                    hasTexture[i_50_] = (byte) (i_51_ >> -1804536926);
                    faceTexture[i_50_] = faceColors[i_50_];
                    faceColors[i_50_] = (short) 127;
                    if (faceTexture[i_50_] != -1) bool_15_ = true;
                } else {
                    hasTexture[i_50_] = (byte) -1;
                    faceTexture[i_50_] = (short) -1;
                }
            }
            if ((i_20_ ^ 0xffffffff) == -256) trianglePriorities[i_50_] = (byte) stream_17_.readByte();
            if (i_21_ == 1) faceAlpha[i_50_] = (byte) stream_18_.readByte();
            if ((i_22_ ^ 0xffffffff) == -2)
                this.triangleSkinValues[i_50_] = (short) stream_19_.readUnsignedByte();//textureCoords
        }
        maxDepth = -1;
        stream.offset = i_36_;
        stream_16_.offset = i_30_;
        short i_52_ = 0;
        short i_53_ = 0;
        short i_54_ = 0;
        int i_55_ = 0;
        for (int i_56_ = 0; i_56_ < numTriangles; i_56_++) {
            int i_57_ = stream_16_.readUnsignedByte();
            if (i_57_ == 1) {
                i_52_ = (short) (i_55_ + stream.readUnsignedSmart3());
                i_55_ = i_52_;
                i_53_ = (short) (stream.readUnsignedSmart3() + i_55_);
                i_55_ = i_53_;
                i_54_ = (short) (stream.readUnsignedSmart3() + i_55_);
                i_55_ = i_54_;
                triangleViewspaceX[i_56_] = i_52_;
                triangleViewspaceY[i_56_] = i_53_;
                triangleViewspaceZ[i_56_] = i_54_;
                if (maxDepth < i_52_) maxDepth = i_52_;
                if (i_53_ > maxDepth) maxDepth = i_53_;
                if (maxDepth < i_54_) maxDepth = i_54_;
            }
            if ((i_57_ ^ 0xffffffff) == -3) {
                i_53_ = i_54_;
                i_54_ = (short) (i_55_ + stream.readUnsignedSmart3());
                i_55_ = i_54_;
                triangleViewspaceX[i_56_] = i_52_;
                triangleViewspaceY[i_56_] = i_53_;
                triangleViewspaceZ[i_56_] = i_54_;
                if ((maxDepth ^ 0xffffffff) > (i_54_ ^ 0xffffffff)) maxDepth = i_54_;
            }
            if (i_57_ == 3) {
                i_52_ = i_54_;
                i_54_ = (short) (stream.readUnsignedSmart3() + i_55_);
                i_55_ = i_54_;
                triangleViewspaceX[i_56_] = i_52_;
                triangleViewspaceY[i_56_] = i_53_;
                triangleViewspaceZ[i_56_] = i_54_;
                if (i_54_ > maxDepth) maxDepth = i_54_;
            }
            if ((i_57_ ^ 0xffffffff) == -5) {
                short i_58_ = i_52_;
                i_52_ = i_53_;
                i_54_ = (short) (stream.readUnsignedSmart3() + i_55_);
                i_53_ = i_58_;
                triangleViewspaceX[i_56_] = i_52_;
                i_55_ = i_54_;
                triangleViewspaceY[i_56_] = i_53_;
                triangleViewspaceZ[i_56_] = i_54_;
                if ((maxDepth ^ 0xffffffff) > (i_54_ ^ 0xffffffff)) maxDepth = i_54_;
            }
        }
        stream.offset = i_38_;
        maxDepth++;
        for (int i_59_ = 0; (numTexTriangles ^ 0xffffffff) < (i_59_ ^ 0xffffffff); i_59_++) {
            aByteArray1521[i_59_] = (byte) 0;
            textureTriangleNIndex[i_59_] = (short) stream.readUnsignedShort();
            textureTriangleMIndex[i_59_] = (short) stream.readUnsignedShort();
            textureTrianglePIndex[i_59_] = (short) stream.readUnsignedShort();
        }
        if (hasTexture != null) {
            boolean bool_60_ = false;
            for (int i_61_ = 0; (i_61_ ^ 0xffffffff) > (numTriangles ^ 0xffffffff); i_61_++) {
                int i_62_ = 0xff & hasTexture[i_61_];
                if ((i_62_ ^ 0xffffffff) != -256) {
                    if (triangleViewspaceX[i_61_] != (textureTrianglePIndex[i_62_] & 0xffff) || ((triangleViewspaceY[i_61_] ^ 0xffffffff) != (0xffff & textureTriangleMIndex[i_62_] ^ 0xffffffff)) || triangleViewspaceZ[i_61_] != (textureTriangleNIndex[i_62_] & 0xffff))
                        bool_60_ = true;
                    else hasTexture[i_61_] = (byte) -1;
                }
            }
            if (!bool_60_) hasTexture = null;
        }
        if (!bool_15_) faceTexture = null;
        if (!bool) faceRenderType = null;
        //if (arg0 != true)
        //method993(106, -114, -65, 70);
    }

    @SuppressWarnings("unused")
    public void decodeNew(byte[] data) {

        InputStream stream = new InputStream(data);
        InputStream stream_25_ = new InputStream(data);
        InputStream stream_26_ = new InputStream(data);
        InputStream stream_27_ = new InputStream(data);
        InputStream stream_28_ = new InputStream(data);
        InputStream stream_29_ = new InputStream(data);
        InputStream stream_30_ = new InputStream(data);
        stream.offset = data.length - 23;

        this.numVertices = stream.readUnsignedShort();
        this.numTriangles = stream.readUnsignedShort();
        this.numTextureTriangles = stream.readUnsignedByte();
        int flag = stream.readUnsignedByte();
        boolean has_fill_attr = (flag & 1) != 0;
        boolean has_surface_fx = (flag & 2) != 0;
        boolean has_vertex_normals = (flag & 4) != 0;
        boolean has_large_size = (flag & 8) != 0;
        if (has_large_size) {
            stream.skip(-7);
            stream.offset -= 7;
            this.version = stream.readUnsignedByte();
            stream.skip(6);
            stream.offset += 6;
        }

        int model_priority = stream.readUnsignedByte();
        if (model_priority == 255) {
            model_priority = -1;
        }

        int has_alpha = stream.readUnsignedByte();
        int has_triangle_skin_types = stream.readUnsignedByte();
        int has_textures = stream.readUnsignedByte();
        int has_vertex_skin_types = stream.readUnsignedByte();
        int i_40_ = stream.readUnsignedShort();
        int i_41_ = stream.readUnsignedShort();
        int i_42_ = stream.readUnsignedShort();
        int i_43_ = stream.readUnsignedShort();
        int i_44_ = stream.readUnsignedShort();
        int i_45_ = 0;
        int particle_count = 0;
        int particle_color = 0;
        int tex_tri_count;
        if (this.numTextureTriangles != 0) {
            this.textureRenderTypes = new byte[this.numTextureTriangles];
            stream.offset = 0;

            for (tex_tri_count = 0; tex_tri_count != this.numTextureTriangles; ++tex_tri_count) {
                byte type = this.textureRenderTypes[tex_tri_count] = (byte) stream.readByte();

                if (type == 0) {
                    i_45_++;
                }
                if (type >= 1 && type <= 3) {
                    particle_count++;
                }
                if (type == 2) {
                    particle_color++;
                }

            }
        }

        tex_tri_count = this.numTextureTriangles;
        int var63 = tex_tri_count;
        tex_tri_count += this.numVertices;
        int i_51_ = tex_tri_count;
        if (has_fill_attr) {
            tex_tri_count += this.numTriangles;
        }

        int i_52_ = tex_tri_count;
        tex_tri_count += this.numTriangles;
        int i_53_ = tex_tri_count;
        if (model_priority == -1) {
            tex_tri_count += this.numTriangles;
        }

        int i_54_ = tex_tri_count;
        if (has_triangle_skin_types == 1) {
            tex_tri_count += this.numTriangles;
        }

        //int i_55_ = tex_tri_count;
        if (has_vertex_skin_types == 1) {
            tex_tri_count += this.numVertices;
        }

        int i_56_ = tex_tri_count;
        if (has_alpha == 1) {
            tex_tri_count += this.numTriangles;
        }

        int i_57_ = tex_tri_count;
        tex_tri_count += i_43_;
        int i_58_ = tex_tri_count;
        if (has_textures == 1) {
            tex_tri_count += this.numTriangles * 2;
        }

        int i_59_ = tex_tri_count;
        tex_tri_count += i_44_;
        int i_60_ = tex_tri_count;
        tex_tri_count += 2 * this.numTriangles;
        int i_61_ = tex_tri_count;
        tex_tri_count += i_40_;
        int i_62_ = tex_tri_count;
        tex_tri_count += i_41_;
        int i_63_ = tex_tri_count;
        tex_tri_count += i_42_;
        int i_64_ = tex_tri_count;
        tex_tri_count += 6 * i_45_;
        int i_65_ = tex_tri_count;
        tex_tri_count += particle_count * 6;
        byte i_66_ = 6;
        if (this.version == 14) {
            i_66_ = 7;
        } else if (this.version >= 15) {
            i_66_ = 9;
        }

        int i_67_ = tex_tri_count;
        tex_tri_count += particle_count * i_66_;
        int i_68_ = tex_tri_count;
        tex_tri_count += particle_count;
        int i_69_ = tex_tri_count;
        tex_tri_count += particle_count;
        int i_70_ = tex_tri_count;
        tex_tri_count += particle_count + particle_color * 2;
        this.faceColors = new short[this.numTriangles];
        stream.offset = var63;
        if (has_fill_attr) {
            this.faceRenderType = new byte[this.numTriangles];
        }

        if (this.numTextureTriangles != 0) {
            this.textureTrianglePIndex = new short[this.numTextureTriangles];
            this.textureTriangleMIndex = new short[this.numTextureTriangles];
            this.textureTriangleNIndex = new short[this.numTextureTriangles];
            if (particle_count != 0) {
                this.particleDirectionZ = new int[particle_count];
                this.particleLifespanY = new byte[particle_count];
                this.particleDirectionX = new int[particle_count];
                this.particleLifespanX = new byte[particle_count];
                this.particleLifespanZ = new int[particle_count];
                this.particleDirectionY = new int[particle_count];
            }

            if (particle_color != 0) {
                this.texturePrimaryColor = new int[particle_color];
                this.textureSecondaryColor = new int[particle_color];
            }
        }

        this.verticesX = new int[this.numVertices];
        this.verticesY = new int[this.numVertices];
        this.verticesZ = new int[this.numVertices];
        if (has_vertex_skin_types == 1) {
            this.vertexSkins = new int[this.numVertices];
        }

        this.triangleViewspaceX = new short[this.numTriangles];
        this.triangleViewspaceY = new short[this.numTriangles];
        this.triangleViewspaceZ = new short[this.numTriangles];
        if (has_textures == 1) {
            this.faceTexture = new short[this.numTriangles];
        }

        this.priority = (byte) model_priority;
        if (model_priority == -1) {
            this.trianglePriorities = new byte[this.numTriangles];
        }

        if (has_alpha == 1) {
            this.faceAlpha = new byte[this.numTriangles];
        }

        if (has_textures == 1 && this.numTextureTriangles != 0) {
            this.hasTexture = new byte[this.numTriangles];
        }

        if (has_triangle_skin_types == 1) {
            this.triangleSkinValues = new short[this.numTriangles];
        }

        stream_25_.offset = i_61_;
        stream_26_.offset = i_62_;
        stream_27_.offset = i_63_;
        stream_28_.offset = i_64_;
        int vertex_x = 0;
        int vertex_y = 0;
        int vertex_z = 0;

        int tri_x;
        int isolated_normal_count;
        for (tri_x = 0; tri_x != this.numVertices; ++tri_x) {
            int tri_y = stream.readUnsignedByte();
            int tri_z = (tri_y & 1) != 0 ? stream_25_.readUnsignedSmart3() : 0;
            int prev_z_view = (tri_y & 2) != 0 ? stream_26_.readUnsignedSmart3() : 0;
            isolated_normal_count = (tri_y & 4) != 0 ? stream_27_.readUnsignedSmart3() : 0;
            vertex_x += tri_z;
            vertex_y += prev_z_view;
            vertex_z += isolated_normal_count;
            this.verticesX[tri_x] = vertex_x;
            this.verticesY[tri_x] = vertex_y;
            this.verticesZ[tri_x] = vertex_z;
            if (has_vertex_skin_types == 1) {
                this.vertexSkins[tri_x] = stream_28_.readByte();
            }
        }

        stream.offset = i_60_;
        stream_25_.offset = i_51_;
        stream_26_.offset = i_53_;
        stream_27_.offset = i_56_;
        stream_28_.offset = i_54_;
        stream_29_.offset = i_58_;
        stream_30_.offset = i_59_;


        for (tri_x = 0; tri_x != this.numTriangles; ++tri_x) {
            this.faceColors[tri_x] = (short) stream.readUnsignedShort();
            if (has_fill_attr) {
                this.faceRenderType[tri_x] = (byte) stream_25_.readByte();
            }

            if (model_priority == -1) {
                this.trianglePriorities[tri_x] = (byte) stream_26_.readByte();
            }

            if (has_alpha == 1) {
                this.faceAlpha[tri_x] = (byte) stream_27_.readByte();
            }

            if (has_triangle_skin_types == 1) {
                this.triangleSkinValues[tri_x] = (short) stream_28_.readByte();
            }

            if (has_textures == 1) {
                this.faceTexture[tri_x] = (short) (stream_29_.readUnsignedShort() - 1);
            }

            if (this.hasTexture != null) {
                if (this.faceTexture[tri_x] == -1) {
                    this.hasTexture[tri_x] = -1;
                } else {
                    this.hasTexture[tri_x] = (byte) (stream_30_.readUnsignedByte() - 1);
                }
            }
        }

        this.maxDepth = -1;
        stream.offset = i_57_;
        stream_25_.offset = i_52_;
        short var67 = 0;
        short var66 = 0;
        short var65 = 0;
        short var64 = 0;

        int isolated_normal;
        for (isolated_normal_count = 0; isolated_normal_count != this.numTriangles; ++isolated_normal_count) {
            isolated_normal = stream_25_.readUnsignedByte();

            if (isolated_normal == 1) {
                var67 = (short) (var64 + stream.readUnsignedSmart3());
                var66 = (short) (var67 + stream.readUnsignedSmart3());
                var65 = (short) (var66 + stream.readUnsignedSmart3());
                var64 = var65;
                this.triangleViewspaceX[isolated_normal_count] = var67;
                this.triangleViewspaceY[isolated_normal_count] = var66;
                this.triangleViewspaceZ[isolated_normal_count] = var65;
                if (this.maxDepth < var67) {
                    this.maxDepth = var67;
                }

                if (this.maxDepth < var66) {
                    this.maxDepth = var66;
                }

                if (this.maxDepth < var65) {
                    this.maxDepth = var65;
                }
            }
            if (isolated_normal == 2) {
                var66 = var65;
                var65 = (short) (stream.readUnsignedSmart3() + var64);
                var64 = var65;
                this.triangleViewspaceX[isolated_normal_count] = var67;
                this.triangleViewspaceY[isolated_normal_count] = var66;
                this.triangleViewspaceZ[isolated_normal_count] = var65;
                if (this.maxDepth < var65) {
                    this.maxDepth = var65;
                }
            }
            if (isolated_normal == 3) {
                var67 = var65;
                var65 = (short) (stream.readUnsignedSmart3() + var64);
                var64 = var65;
                this.triangleViewspaceX[isolated_normal_count] = var67;
                this.triangleViewspaceY[isolated_normal_count] = var66;
                this.triangleViewspaceZ[isolated_normal_count] = var65;
                if (this.maxDepth < var65) {
                    this.maxDepth = var65;
                }
            }
            if (isolated_normal == 4) {
                short x = var67;
                var67 = var66;
                var65 = (short) (stream.readUnsignedSmart3() + var64);
                var66 = x;
                var64 = var65;
                this.triangleViewspaceX[isolated_normal_count] = var67;
                this.triangleViewspaceY[isolated_normal_count] = x;
                this.triangleViewspaceZ[isolated_normal_count] = var65;
                if (this.maxDepth < var65) {
                    this.maxDepth = var65;
                }
            }
        }

        stream.offset = i_64_;
        ++this.maxDepth;

        stream_25_.offset = i_65_;
        stream_26_.offset = i_67_;
        stream_27_.offset = i_68_;
        stream_28_.offset = i_69_;
        stream_29_.offset = i_70_;


        for (isolated_normal_count = 0; isolated_normal_count != this.numTextureTriangles; ++isolated_normal_count) {
            byte var70 = this.textureRenderTypes[isolated_normal_count];

            if (var70 == 0) {
                this.textureTrianglePIndex[isolated_normal_count] = (short) stream.readUnsignedShort();
                this.textureTriangleMIndex[isolated_normal_count] = (short) stream.readUnsignedShort();
                this.textureTriangleNIndex[isolated_normal_count] = (short) stream.readUnsignedShort();
            }
            if (var70 == 1) {
                this.textureTrianglePIndex[isolated_normal_count] = (short) stream_25_.readUnsignedShort();
                this.textureTriangleMIndex[isolated_normal_count] = (short) stream_25_.readUnsignedShort();
                this.textureTriangleNIndex[isolated_normal_count] = (short) stream_25_.readUnsignedShort();
                if (this.version < 15) {
                    this.particleDirectionX[isolated_normal_count] = stream_26_.readUnsignedShort();
                    this.particleDirectionY[isolated_normal_count] = this.version < 14 ? stream_26_.readUnsignedShort() : stream_26_.readIntV2();
                    this.particleDirectionZ[isolated_normal_count] = stream_26_.readUnsignedShort();
                } else {
                    this.particleDirectionX[isolated_normal_count] = stream_26_.readIntV2();//readMedInt
                    this.particleDirectionY[isolated_normal_count] = stream_26_.readIntV2();
                    this.particleDirectionZ[isolated_normal_count] = stream_26_.readIntV2();
                }

                this.particleLifespanX[isolated_normal_count] = (byte) stream_27_.readByte();
                this.particleLifespanY[isolated_normal_count] = (byte) stream_28_.readByte();
                this.particleLifespanZ[isolated_normal_count] = (byte) stream_29_.readUnsignedByte();
            }
            if (var70 == 2) {
                this.textureTrianglePIndex[isolated_normal_count] = (short) stream_25_.readUnsignedShort();
                this.textureTriangleMIndex[isolated_normal_count] = (short) stream_25_.readUnsignedShort();
                this.textureTriangleNIndex[isolated_normal_count] = (short) stream_25_.readUnsignedShort();
                if (this.version < 15) {
                    this.particleDirectionX[isolated_normal_count] = stream_26_.readUnsignedShort();
                    this.particleDirectionY[isolated_normal_count] = this.version < 14 ? stream_26_.readUnsignedShort() : stream_26_.readIntV2();
                    this.particleDirectionZ[isolated_normal_count] = stream_26_.readUnsignedShort();
                } else {
                    this.particleDirectionX[isolated_normal_count] = stream_26_.readIntV2();//readMedInt
                    this.particleDirectionY[isolated_normal_count] = stream_26_.readIntV2();
                    this.particleDirectionZ[isolated_normal_count] = stream_26_.readIntV2();
                }

                this.particleLifespanX[isolated_normal_count] = (byte) stream_27_.readByte();
                this.particleLifespanY[isolated_normal_count] = (byte) stream_28_.readByte();
                this.particleLifespanZ[isolated_normal_count] = stream_29_.readByte();
                this.texturePrimaryColor[isolated_normal_count] = stream_29_.readByte();
                this.textureSecondaryColor[isolated_normal_count] = stream_29_.readByte();
            }
            if (var70 == 3) {
                this.textureTrianglePIndex[isolated_normal_count] = (short) stream_25_.readUnsignedShort();
                this.textureTriangleMIndex[isolated_normal_count] = (short) stream_25_.readUnsignedShort();
                this.textureTriangleNIndex[isolated_normal_count] = (short) stream_25_.readUnsignedShort();
                if (this.version < 15) {
                    this.particleDirectionX[isolated_normal_count] = stream_26_.readUnsignedShort();
                    if (this.version < 14) {
                        this.particleDirectionY[isolated_normal_count] = stream_26_.readUnsignedShort();
                    } else {
                        this.particleDirectionY[isolated_normal_count] = stream_26_.readIntV2();
                    }

                    this.particleDirectionZ[isolated_normal_count] = stream_26_.readUnsignedShort();
                } else {
                    this.particleDirectionX[isolated_normal_count] = stream_26_.readIntV2();
                    this.particleDirectionY[isolated_normal_count] = stream_26_.readIntV2();
                    this.particleDirectionZ[isolated_normal_count] = stream_26_.readIntV2();
                }

                this.particleLifespanX[isolated_normal_count] = (byte) stream_27_.readByte();
                this.particleLifespanY[isolated_normal_count] = (byte) stream_28_.readByte();
                this.particleLifespanZ[isolated_normal_count] = stream_29_.readByte();
            }
        }

        stream.offset = tex_tri_count;
        int y;
        int var68;
        int var69;
        if (has_surface_fx) {
            isolated_normal_count = stream.readUnsignedByte();
            if (isolated_normal_count != 0) {

                for (isolated_normal = 0; isolated_normal != isolated_normal_count; ++isolated_normal) {
                    var69 = stream.readUnsignedShort();
                    y = stream.readUnsignedShort();
                    byte z = model_priority == -1 ? this.trianglePriorities[y] : (byte) model_priority;
                    //this.surfaces[isolated_normal] = new Surface(var69, y, (byte)z);
                }
            }

            isolated_normal = stream.readUnsignedByte();
            if (isolated_normal != 0) {
                //this.surfaceSkins = new SurfaceSkin[isolated_normal];

                for (var69 = 0; var69 != isolated_normal; ++var69) {
                    y = stream.readUnsignedShort();
                    var68 = stream.readUnsignedShort();
                    //this.surfaceSkins[var69] = new SurfaceSkin(y, var68);
                }
            }
        }

        if (has_vertex_normals) {
            isolated_normal_count = stream.readUnsignedByte();
            if (isolated_normal_count != 0) {
                //this.isolatedVertexNormals = new VertexNormal[isolated_normal_count];

                for (isolated_normal = 0; isolated_normal != isolated_normal_count; ++isolated_normal) {
                    var69 = stream.readUnsignedShort();
                    y = stream.readUnsignedShort();
                    var68 = stream.readUnsignedByte();
                    byte divisor = (byte) stream.readByte();
                    //this.isolatedVertexNormals[isolated_normal] = new VertexNormal(var69, y, var68, (byte)divisor);
                }
            }
        }

    }

}
