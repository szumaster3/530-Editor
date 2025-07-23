package com.misc.model.view.render;


import com.misc.model.view.properties.ModelProperties;
import com.misc.model.view.properties.PanelProperties;

public final class Canvas extends Field {
    private static final int[] field134;
    private static final int[] field135;
    private static final int[] field136;
    private static ModelProperties[] properties;
    private static final boolean[] field119;
    private static final boolean[] field120;
    private static final int[] field121;
    private static final int[] field122;
    private static final int[] field123;
    private static final int[] field124;
    private static final int[] field125;
    private static final int[] field126;
    private static final int[] field127;
    private static final int[][] field128;
    private static final int[] field129;
    private static final int[][] field130;
    private static final int[] field131;
    private static final int[] field132;
    private static final int[] field133;
    @SuppressWarnings("unused")
    private static final int[] field137;
    private static final int[] field138;
    private static final int[] field139;
    private static final int[] field140;
    private static final int[] field141;

    static {
        new Canvas();
        field119 = new boolean[4096];
        field120 = new boolean[4096];
        field121 = new int[4096];
        field122 = new int[4096];
        field123 = new int[4096];
        field124 = new int[4096];
        field125 = new int[4096];
        field126 = new int[4096];
        field127 = new int[1500];
        field128 = new int[1500][512];
        field129 = new int[12];
        field130 = new int[12][2000];
        field131 = new int[2000];
        field132 = new int[2000];
        field133 = new int[12];
        field134 = new int[10];
        field135 = new int[10];
        field136 = new int[10];
        field137 = new int[1000];
        field138 = ModelCanvas.field16;
        field139 = ModelCanvas.field17;
        field140 = ModelCanvas.faceLighting;
        field141 = ModelCanvas.field15;
    }

    public boolean isNewHeader = false;
    private int verticeCount;
    private int[] vertexX;
    private int[] vertexY;
    private int[] vertexZ;
    private int triangleCount;
    private int[] trianglePointsX;
    private int[] trianglePointsY;
    private int[] trianglePointsZ;
    private int[] field100;
    private int[] field101;
    private int[] field102;
    private int[] faceRenderType;
    private int[] trianglePriorities;
    private int[] faceAlphas;
    private int[] faceColor;
    private int texTriangleCount;
    private int[] textureTriangleNIndex;
    private int[] textureTriangleMIndex;
    private int[] textureTrianglePIndex;
    private int field111;
    private int field112;
    private int field113;
    private int field114;
    private int[] vertexSkins;
    private int[] triangleSkinValues;
    @SuppressWarnings("unused")
    private Fields[] field117;

    private Canvas() {
    }

    private Canvas(byte[] data) {
        Buffer buffer = new Buffer(data);
        Buffer buffer2 = new Buffer(data);
        Buffer buffer3 = new Buffer(data);
        Buffer buffer4 = new Buffer(data);
        Buffer buffer5 = new Buffer(data);
        Buffer buffer6 = new Buffer(data);
        Buffer buffer7 = new Buffer(data);
        buffer.offset = data.length - 23;
        int numVertices = buffer.readShortv2();
        int numTriangles = buffer.readShortv2();
        int numTexTriangles = buffer.readUnsignedByte();
        ModelProperties prop;
        (prop = properties[0] = new ModelProperties()).data = data;
        prop.numVertices = numVertices;
        prop.numTriangles = numTriangles;
        prop.numTexTriangles = numTexTriangles;
        int hash = buffer.readUnsignedByte();
        boolean has_fill_attr = (1 & hash) == 1;
        boolean has_surface_fx;
        boolean has_large_size = (8 & hash) == 8;
        if (!(has_surface_fx = has_large_size)) {
            Canvas model = this;
            Buffer buffer8 = new Buffer(data);
            Buffer buffer9 = new Buffer(data);
            Buffer buffer100 = new Buffer(data);
            Buffer buffer101 = new Buffer(data);
            Buffer buffer102 = new Buffer(data);
            Buffer buffer103 = new Buffer(data);
            Buffer buffer104 = new Buffer(data);
            buffer8.offset = data.length - 23;
            int numVertices2 = buffer8.readShortv2();
            int numTriangles2 = buffer8.readShortv2();
            int numTexTriangles2 = buffer8.readUnsignedByte();
            ModelProperties var96;
            (var96 = properties[0] = new ModelProperties()).data = data;
            var96.numVertices = numVertices2;
            var96.numTriangles = numTriangles2;
            var96.numTexTriangles = numTexTriangles2;
            int hash2 = buffer8.readUnsignedByte();
            boolean has_fill_attr2 = ~(1 & hash2) == -2;
            int model_priority2 = buffer8.readUnsignedByte();
            int has_alpha2 = buffer8.readUnsignedByte();
            int has_triangle_skin_types2 = buffer8.readUnsignedByte();
            int has_textures2 = buffer8.readUnsignedByte();
            int has_skins2 = buffer8.readUnsignedByte();
            int modelVerticesX2 = buffer8.readShortv2();
            int modelVerticesY2 = buffer8.readShortv2();
            int modelVerticesZ2 = buffer8.readShortv2();
            int modelVertexPoint2 = buffer8.readShortv2();
            int modelTextureCoordinates2 = buffer8.readShortv2();
            int var109 = 0;
            int var110 = 0;
            int var111 = 0;
            byte[] hasTexture = null;
            byte[] var114 = null;
            byte[] var115 = null;
            byte[] var116 = null;
            byte[] var117 = null;
            byte[] var118 = null;
            byte[] var119 = null;
            int[] var120 = null;
            int[] var121 = null;
            int[] var122 = null;
            short[] faceColor = null;
            int var125;
            if (numTexTriangles2 > 0) {
                var114 = new byte[numTexTriangles2];
                buffer8.offset = 0;

                for (var125 = 0; var125 < numTexTriangles2; ++var125) {
                    byte var126;
                    if ((var126 = var114[var125] = buffer8.readByte()) == 0) {
                        ++var109;
                    }

                    if (var126 > 0 && var126 <= 3) {
                        ++var110;
                    }

                    if (var126 == 2) {
                        ++var111;
                    }
                }
            }

            int var127 = var125 = numTexTriangles2 + numVertices2;
            if (hash2 == 1) {
                var125 += numTriangles2;
            }

            int var128 = var125;
            int var129 = var125 += numTriangles2;
            if (model_priority2 == 255) {
                var125 += numTriangles2;
            }

            int var130 = var125;
            if (has_triangle_skin_types2 == 1) {
                var125 += numTriangles2;
            }

            int var131 = var125;
            if (has_skins2 == 1) {
                var125 += numVertices2;
            }

            int var132 = var125;
            if (has_alpha2 == 1) {
                var125 += numTriangles2;
            }

            int var133 = var125;
            int var134 = var125 += modelVertexPoint2;
            if (has_textures2 == 1) {
                var125 += numTriangles2 << 1;
            }

            int var135 = var125;
            int var136 = var125 += modelTextureCoordinates2;
            int var137 = var125 += numTriangles2 << 1;
            int var138 = var125 += modelVerticesX2;
            int var139 = var125 += modelVerticesY2;
            int var140 = var125 += modelVerticesZ2;
            int var141 = var125 += var109 * 6;
            int var142 = var125 += var110 * 6;
            int var143 = var125 += var110 * 6;
            int var144 = var125 += var110;
            int var145 = var125 + var110;
            int[] vertexX = new int[numVertices2];
            int[] vertexY = new int[numVertices2];
            int[] vertexZ = new int[numVertices2];
            int[] trianglePointsX = new int[numTriangles2];
            int[] trianglePointsY = new int[numTriangles2];
            int[] trianglePointsZ = new int[numTriangles2];
            this.vertexSkins = new int[numVertices2];
            this.faceRenderType = new int[numTriangles2];
            this.trianglePriorities = new int[numTriangles2];
            this.faceAlphas = new int[numTriangles2];
            this.triangleSkinValues = new int[numTriangles2];
            if (has_skins2 == 1) {
                this.vertexSkins = new int[numVertices2];
            }

            if (has_fill_attr2) {
                this.faceRenderType = new int[numTriangles2];
            }

            if (model_priority2 == 255) {
                this.trianglePriorities = new int[numTriangles2];
            }

            if (has_alpha2 == 1) {
                this.faceAlphas = new int[numTriangles2];
            }

            if (has_triangle_skin_types2 == 1) {
                this.triangleSkinValues = new int[numTriangles2];
            }

            if (has_textures2 == 1) {
                faceColor = new short[numTriangles2];
            }

            if (has_textures2 == 1 && numTexTriangles2 > 0) {
                hasTexture = new byte[numTriangles2];
            }

            int[] faceColors = new int[numTriangles2];
            int[] textureTriangleNIndex = null;
            int[] textureTriangleMIndex = null;
            int[] textureTrianglePIndex = null;
            if (numTexTriangles2 > 0) {
                textureTriangleNIndex = new int[numTexTriangles2];
                textureTriangleMIndex = new int[numTexTriangles2];
                textureTrianglePIndex = new int[numTexTriangles2];
                if (var110 > 0) {
                    var120 = new int[var110];
                    var122 = new int[var110];
                    var121 = new int[var110];
                    var118 = new byte[var110];
                    var119 = new byte[var110];
                    var116 = new byte[var110];
                }

                if (var111 > 0) {
                    var117 = new byte[var111];
                    var115 = new byte[var111];
                }
            }

            buffer8.offset = numTexTriangles2;
            buffer9.offset = var137;
            buffer100.offset = var138;
            buffer101.offset = var139;
            buffer102.offset = var131;
            int var155 = 0;
            int var156 = 0;
            int var157 = 0;

            int var158;
            int var159;
            int var160;
            int var161;
            int var162;
            for (var158 = 0; var158 < numVertices2; ++var158) {
                var159 = buffer8.readUnsignedByte();
                var160 = 0;
                if ((var159 & 1) != 0) {
                    var160 = buffer9.readShortSmart();
                }

                var161 = 0;
                if ((var159 & 2) != 0) {
                    var161 = buffer100.readShortSmart();
                }

                var162 = 0;
                if ((var159 & 4) != 0) {
                    var162 = buffer101.readShortSmart();
                }

                vertexX[var158] = var155 + var160;
                vertexY[var158] = var156 + var161;
                vertexZ[var158] = var157 + var162;
                var155 = vertexX[var158];
                var156 = vertexY[var158];
                var157 = vertexZ[var158];
                if (model.vertexSkins != null) {
                    model.vertexSkins[var158] = buffer102.readUnsignedByte();
                }
            }

            buffer8.offset = var136;
            buffer9.offset = var127;
            buffer100.offset = var129;
            buffer101.offset = var132;
            buffer102.offset = var130;
            buffer103.offset = var134;
            buffer104.offset = var135;

            for (var158 = 0; var158 < numTriangles2; ++var158) {
                faceColors[var158] = buffer8.readShortv2();
                if (hash2 == 1) {
                    model.faceRenderType[var158] = buffer9.readByte();
                    if (model.faceRenderType[var158] == 2) {
                        faceColors[var158] = '\uffff';
                    }

                    model.faceRenderType[var158] = 0;
                }

                if (model_priority2 == 255) {
                    model.trianglePriorities[var158] = buffer100.readByte();
                }

                if (has_alpha2 == 1) {
                    model.faceAlphas[var158] = buffer101.readByte();
                    if (model.faceAlphas[var158] < 0) {
                        model.faceAlphas[var158] += 256;
                    }
                }

                if (has_triangle_skin_types2 == 1) {
                    model.triangleSkinValues[var158] = buffer102.readUnsignedByte();
                }

                if (has_textures2 == 1) {
                    faceColor[var158] = (short) (buffer103.readShortv2() - 1);
                }

                if (hasTexture != null) {
                    if (faceColor[var158] != -1) {
                        hasTexture[var158] = (byte) (buffer104.readUnsignedByte() - 1);
                    } else {
                        hasTexture[var158] = -1;
                    }
                }
            }

            buffer8.offset = var133;
            buffer9.offset = var128;
            var158 = 0;
            var159 = 0;
            var160 = 0;
            var161 = 0;

            int var163;
            for (var162 = 0; var162 < numTriangles2; ++var162) {
                if ((var163 = buffer9.readUnsignedByte()) == 1) {
                    var161 = var158 = buffer8.readShortSmart() + var161;
                    var161 = var159 = buffer8.readShortSmart() + var161;
                    var161 = var160 = buffer8.readShortSmart() + var161;
                    trianglePointsX[var162] = var158;
                    trianglePointsY[var162] = var159;
                    trianglePointsZ[var162] = var160;
                }

                if (var163 == 2) {
                    var159 = var160;
                    var161 = var160 = buffer8.readShortSmart() + var161;
                    trianglePointsX[var162] = var158;
                    trianglePointsY[var162] = var159;
                    trianglePointsZ[var162] = var160;
                }

                if (var163 == 3) {
                    var158 = var160;
                    var161 = var160 = buffer8.readShortSmart() + var161;
                    trianglePointsX[var162] = var158;
                    trianglePointsY[var162] = var159;
                    trianglePointsZ[var162] = var160;
                }

                if (var163 == 4) {
                    int var164 = var158;
                    var158 = var159;
                    var159 = var164;
                    var161 = var160 = buffer8.readShortSmart() + var161;
                    trianglePointsX[var162] = var158;
                    trianglePointsY[var162] = var164;
                    trianglePointsZ[var162] = var160;
                }
            }

            buffer8.offset = var140;
            buffer9.offset = var141;
            buffer100.offset = var142;
            buffer101.offset = var143;
            buffer102.offset = var144;
            buffer103.offset = var145;

            for (var162 = 0; var162 < numTexTriangles2; ++var162) {
                if ((var163 = var114[var162] & 255) == 0) {
                    textureTriangleNIndex[var162] = buffer8.readShortv2();
                    textureTriangleMIndex[var162] = buffer8.readShortv2();
                    textureTrianglePIndex[var162] = buffer8.readShortv2();
                }

                if (var163 == 1) {
                    textureTriangleNIndex[var162] = buffer9.readShortv2();
                    textureTriangleMIndex[var162] = buffer9.readShortv2();
                    textureTrianglePIndex[var162] = buffer9.readShortv2();
                    var120[var162] = buffer100.readShortv2();
                    var122[var162] = buffer100.readShortv2();
                    var121[var162] = buffer100.readShortv2();
                    var118[var162] = buffer101.readByte();
                    var119[var162] = buffer102.readByte();
                    var116[var162] = buffer103.readByte();
                }

                if (var163 == 2) {
                    textureTriangleNIndex[var162] = buffer9.readShortv2();
                    textureTriangleMIndex[var162] = buffer9.readShortv2();
                    textureTrianglePIndex[var162] = buffer9.readShortv2();
                    var120[var162] = buffer100.readShortv2();
                    var122[var162] = buffer100.readShortv2();
                    var121[var162] = buffer100.readShortv2();
                    var118[var162] = buffer101.readByte();
                    var119[var162] = buffer102.readByte();
                    var116[var162] = buffer103.readByte();
                    var117[var162] = buffer103.readByte();
                    var115[var162] = buffer103.readByte();
                }

                if (var163 == 3) {
                    textureTriangleNIndex[var162] = buffer9.readShortv2();
                    textureTriangleMIndex[var162] = buffer9.readShortv2();
                    textureTrianglePIndex[var162] = buffer9.readShortv2();
                    var120[var162] = buffer100.readShortv2();
                    var122[var162] = buffer100.readShortv2();
                    var121[var162] = buffer100.readShortv2();
                    var118[var162] = buffer101.readByte();
                    var119[var162] = buffer102.readByte();
                    var116[var162] = buffer103.readByte();
                }
            }

            if (model_priority2 != 255) {
                for (var162 = 0; var162 < numTriangles2; ++var162) {
                    model.trianglePriorities[var162] = model_priority2;
                }
            }

            model.faceColor = faceColors;
            model.verticeCount = numVertices2;
            model.triangleCount = numTriangles2;
            model.vertexX = vertexX;
            model.vertexY = vertexY;
            model.vertexZ = vertexZ;
            model.trianglePointsX = trianglePointsX;
            model.trianglePointsY = trianglePointsY;
            model.trianglePointsZ = trianglePointsZ;
        } else {
            int var166 = 0;
            if (has_surface_fx) {
                buffer.offset -= 7;
                var166 = buffer.readUnsignedByte();
                buffer.offset += 6;
            }

            if (var166 == 15) {
                this.isNewHeader = true;
            }

            int modelPriority = buffer.readUnsignedByte();
            int hasAlpha = buffer.readUnsignedByte();
            int hasTriangleSkinTypes = buffer.readUnsignedByte();
            int modelTexture = buffer.readUnsignedByte();
            int modelVertexSkins = buffer.readUnsignedByte();
            int modelVerticesX = buffer.readShortv2();
            int modelVerticesY = buffer.readShortv2();
            int modelVerticesZ = buffer.readShortv2();
            int modelVertexPoint = buffer.readShortv2();
            int modelTextureCoordinates = buffer.readShortv2();
            int textureAmount = 0;
            int var25 = 0;
            int var26 = 0;
            byte[] var27 = null;
            byte[] textureRenderTypes = null;
            byte[] var29 = null;
            byte[] var30 = null;
            byte[] var31 = null;
            byte[] var32 = null;
            byte[] var33 = null;
            int[] var34 = null;
            int[] var35 = null;
            int[] var36 = null;
            short[] var37 = null;
            int position;
            if (numTexTriangles > 0) {
                textureRenderTypes = new byte[numTexTriangles];
                buffer.offset = 0;

                for (position = 0; position < numTexTriangles; ++position) {
                    byte var39;
                    if ((var39 = textureRenderTypes[position] = buffer.readByte()) == 0) {
                        ++textureAmount;
                    }

                    if (var39 > 0 && var39 <= 3) {
                        ++var25;
                    }

                    if (var39 == 2) {
                        ++var26;
                    }
                }
            }

            int renderTypePos = position = numTexTriangles + numVertices;
            if (has_fill_attr) {
                position += numTriangles;
            }

            if (hash == 1) {
                position += numTriangles;
            }

            int var40 = position;
            int var41 = position += numTriangles;
            if (modelPriority == 255) {
                position += numTriangles;
            }

            int var42 = position;
            if (hasTriangleSkinTypes == 1) {
                position += numTriangles;
            }

            int var43 = position;
            if (modelVertexSkins == 1) {
                position += numVertices;
            }

            int var44 = position;
            if (hasAlpha == 1) {
                position += numTriangles;
            }

            int var45 = position;
            modelVertexPoint = position += modelVertexPoint;
            if (modelTexture == 1) {
                position += numTriangles << 1;
            }

            int textureCoordPos = position;
            modelTextureCoordinates = position += modelTextureCoordinates;
            int var47 = position += numTriangles << 1;
            int var48 = position += modelVerticesX;
            modelVerticesY = position += modelVerticesY;
            modelVerticesZ = position += modelVerticesZ;
            textureAmount = position += textureAmount * 6;
            position += var25 * 6;
            byte var169 = 6;
            if (var166 != 14) {
                if (var166 >= 15) {
                    var169 = 9;
                }
            } else {
                var169 = 7;
            }

            int var49 = position;
            int var50 = position += var169 * var25;
            int var51 = position += var25;
            int var64 = position + var25;
            int[] vertexX = new int[numVertices];
            int[] vertexY = new int[numVertices];
            int[] vertexZ = new int[numVertices];
            int[] trianglePointsX = new int[numTriangles];
            int[] trianglePointsY = new int[numTriangles];
            int[] trianglePointsZ = new int[numTriangles];
            this.vertexSkins = new int[numVertices];
            this.faceRenderType = new int[numTriangles];
            this.trianglePriorities = new int[numTriangles];
            this.faceAlphas = new int[numTriangles];
            this.triangleSkinValues = new int[numTriangles];
            if (modelVertexSkins == 1) {
                this.vertexSkins = new int[numVertices];
            }

            if (has_fill_attr) {
                this.faceRenderType = new int[numTriangles];
            }

            if (modelPriority == 255) {
                this.trianglePriorities = new int[numTriangles];
            }

            if (hasAlpha == 1) {
                this.faceAlphas = new int[numTriangles];
            }

            if (hasTriangleSkinTypes == 1) {
                this.triangleSkinValues = new int[numTriangles];
            }

            if (modelTexture == 1) {
                var37 = new short[numTriangles];
            }

            if (modelTexture == 1 && numTexTriangles > 0) {
                var27 = new byte[numTriangles];
            }

            int[] faceColor = new int[numTriangles];
            int[] var71 = null;
            int[] var72 = null;
            int[] var73 = null;
            if (numTexTriangles > 0) {
                var71 = new int[numTexTriangles];
                var72 = new int[numTexTriangles];
                var73 = new int[numTexTriangles];
                if (var25 > 0) {
                    var34 = new int[var25];
                    var36 = new int[var25];
                    var35 = new int[var25];
                    var32 = new byte[var25];
                    var33 = new byte[var25];
                    var30 = new byte[var25];
                }

                if (var26 > 0) {
                    var31 = new byte[var26];
                    var29 = new byte[var26];
                }
            }

            buffer.offset = numTexTriangles;
            buffer2.offset = var47;
            buffer3.offset = var48;
            buffer4.offset = modelVerticesY;
            buffer5.offset = var43;
            int var74 = 0;
            int var75 = 0;
            int var76 = 0;

            int var77;
            int var78;
            int var79;
            int var80;
            int var81;
            for (var77 = 0; var77 < numVertices; ++var77) {
                var78 = buffer.readUnsignedByte();
                var79 = 0;
                if ((var78 & 1) != 0) {
                    var79 = buffer2.readShortSmart();
                }

                var80 = 0;
                if ((var78 & 2) != 0) {
                    var80 = buffer3.readShortSmart();
                }

                var81 = 0;
                if ((var78 & 4) != 0) {
                    var81 = buffer4.readShortSmart();
                }

                vertexX[var77] = var74 + var79;
                vertexY[var77] = var75 + var80;
                vertexZ[var77] = var76 + var81;
                var74 = vertexX[var77];
                var75 = vertexY[var77];
                var76 = vertexZ[var77];
                if (this.vertexSkins != null) {
                    this.vertexSkins[var77] = buffer5.readUnsignedByte();
                }
            }

            buffer.offset = modelTextureCoordinates;
            buffer2.offset = renderTypePos;
            buffer3.offset = var41;
            buffer4.offset = var44;
            buffer5.offset = var42;
            buffer6.offset = modelVertexPoint;
            buffer7.offset = textureCoordPos;

            for (var77 = 0; var77 < numTriangles; ++var77) {
                faceColor[var77] = buffer.readShortv2();
                if (hash == 1) {
                    this.faceRenderType[var77] = buffer2.readByte();
                    if (this.faceRenderType[var77] == 2) {
                        faceColor[var77] = '\uffff';
                    }

                    this.faceRenderType[var77] = 0;
                }

                if (modelPriority == 255) {
                    this.trianglePriorities[var77] = buffer3.readByte();
                }

                if (hasAlpha == 1) {
                    this.faceAlphas[var77] = buffer4.readByte();
                    if (this.faceAlphas[var77] < 0) {
                        this.faceAlphas[var77] += 256;
                    }
                }

                if (hasTriangleSkinTypes == 1) {
                    this.triangleSkinValues[var77] = buffer5.readUnsignedByte();
                }

                if (modelTexture == 1) {
                    var37[var77] = (short) (buffer6.readShortv2() - 1);
                }

                if (var27 != null) {
                    if (var37[var77] != -1) {
                        var27[var77] = (byte) (buffer7.readUnsignedByte() - 1);
                    } else {
                        var27[var77] = -1;
                    }
                }
            }

            buffer.offset = var45;
            buffer2.offset = var40;
            var77 = 0;
            var78 = 0;
            var79 = 0;
            var80 = 0;

            int var82;
            for (var81 = 0; var81 < numTriangles; ++var81) {
                if ((var82 = buffer2.readUnsignedByte()) == 1) {
                    var80 = var77 = buffer.readShortSmart() + var80;
                    var80 = var78 = buffer.readShortSmart() + var80;
                    var80 = var79 = buffer.readShortSmart() + var80;
                    trianglePointsX[var81] = var77;
                    trianglePointsY[var81] = var78;
                    trianglePointsZ[var81] = var79;
                }

                if (var82 == 2) {
                    var78 = var79;
                    var80 = var79 = buffer.readShortSmart() + var80;
                    trianglePointsX[var81] = var77;
                    trianglePointsY[var81] = var78;
                    trianglePointsZ[var81] = var79;
                }

                if (var82 == 3) {
                    var77 = var79;
                    var80 = var79 = buffer.readShortSmart() + var80;
                    trianglePointsX[var81] = var77;
                    trianglePointsY[var81] = var78;
                    trianglePointsZ[var81] = var79;
                }

                if (var82 == 4) {
                    int var83 = var77;
                    var77 = var78;
                    var78 = var83;
                    var80 = var79 = buffer.readShortSmart() + var80;
                    trianglePointsX[var81] = var77;
                    trianglePointsY[var81] = var83;
                    trianglePointsZ[var81] = var79;
                }
            }

            buffer.offset = modelVerticesZ;
            buffer2.offset = textureAmount;
            buffer3.offset = var49;
            buffer4.offset = var50;
            buffer5.offset = var51;
            buffer6.offset = var64;

            for (var81 = 0; var81 < numTexTriangles; ++var81) {
                if ((var82 = textureRenderTypes[var81] & 255) == 0) {
                    var71[var81] = buffer.readShortv2();
                    var72[var81] = buffer.readShortv2();
                    var73[var81] = buffer.readShortv2();
                }

                if (var82 == 1) {
                    var71[var81] = buffer2.readShortv2();
                    var72[var81] = buffer2.readShortv2();
                    var73[var81] = buffer2.readShortv2();
                    if (var166 < 15) {
                        var34[var81] = buffer3.readShortv2();
                        if (var166 >= 14) {
                            var36[var81] = buffer3.readMid();
                        } else {
                            var36[var81] = buffer3.readShortv2();
                        }

                        var35[var81] = buffer3.readShortv2();
                    } else {
                        var34[var81] = buffer3.readMid();
                        var36[var81] = buffer3.readMid();
                        var35[var81] = buffer3.readMid();
                    }

                    var32[var81] = buffer4.readByte();
                    var33[var81] = buffer5.readByte();
                    var30[var81] = buffer6.readByte();
                }

                if (var82 == 2) {
                    var71[var81] = buffer2.readShortv2();
                    var72[var81] = buffer2.readShortv2();
                    var73[var81] = buffer2.readShortv2();
                    if (var166 >= 15) {
                        var34[var81] = buffer3.readMid();
                        var36[var81] = buffer3.readMid();
                        var35[var81] = buffer3.readMid();
                    } else {
                        var34[var81] = buffer3.readShortv2();
                        if (var166 < 14) {
                            var36[var81] = buffer3.readShortv2();
                        } else {
                            var36[var81] = buffer3.readMid();
                        }

                        var35[var81] = buffer3.readShortv2();
                    }

                    var32[var81] = buffer4.readByte();
                    var33[var81] = buffer5.readByte();
                    var30[var81] = buffer6.readByte();
                    var31[var81] = buffer6.readByte();
                    var29[var81] = buffer6.readByte();
                }

                if (var82 == 3) {
                    var71[var81] = buffer2.readShortv2();
                    var72[var81] = buffer2.readShortv2();
                    var73[var81] = buffer2.readShortv2();
                    if (var166 < 15) {
                        var34[var81] = buffer3.readShortv2();
                        if (var166 < 14) {
                            var36[var81] = buffer3.readShortv2();
                        } else {
                            var36[var81] = buffer3.readMid();
                        }

                        var35[var81] = buffer3.readShortv2();
                    } else {
                        var34[var81] = buffer3.readMid();
                        var36[var81] = buffer3.readMid();
                        var35[var81] = buffer3.readMid();
                    }

                    var32[var81] = buffer4.readByte();
                    var33[var81] = buffer5.readByte();
                    var30[var81] = buffer6.readByte();
                }
            }

            if (modelPriority != 255) {
                for (var81 = 0; var81 < numTriangles; ++var81) {
                    this.trianglePriorities[var81] = modelPriority;
                }
            }

            this.faceColor = faceColor;
            this.verticeCount = numVertices;
            this.triangleCount = numTriangles;
            this.vertexX = vertexX;
            this.vertexY = vertexY;
            this.vertexZ = vertexZ;
            this.trianglePointsX = trianglePointsX;
            this.trianglePointsY = trianglePointsY;
            this.trianglePointsZ = trianglePointsZ;
        }
    }

    private Canvas(int index) {
        ModelProperties prop = properties[index];
        this.verticeCount = prop.numVertices;
        this.triangleCount = prop.numTriangles;
        this.texTriangleCount = prop.numTexTriangles;
        this.vertexX = new int[this.verticeCount];
        this.vertexY = new int[this.verticeCount];
        this.vertexZ = new int[this.verticeCount];
        this.trianglePointsX = new int[this.triangleCount];
        this.trianglePointsY = new int[this.triangleCount];
        this.trianglePointsZ = new int[this.triangleCount];
        this.textureTriangleNIndex = new int[this.texTriangleCount];
        this.textureTriangleMIndex = new int[this.texTriangleCount];
        this.textureTrianglePIndex = new int[this.texTriangleCount];
        if (prop.field74 >= 0) {
            this.vertexSkins = new int[this.verticeCount];
        }

        if (prop.field78 >= 0) {
            this.faceRenderType = new int[this.triangleCount];
        }

        if (prop.field79 >= 0) {
            this.trianglePriorities = new int[this.triangleCount];
        }

        if (prop.field80 >= 0) {
            this.faceAlphas = new int[this.triangleCount];
        }

        if (prop.field81 >= 0) {
            this.triangleSkinValues = new int[this.triangleCount];
        }

        this.faceColor = new int[this.triangleCount];
        Buffer var2;
        (var2 = new Buffer(prop.data)).offset = 0;
        Buffer var3;
        (var3 = new Buffer(prop.data)).offset = prop.field71;
        Buffer var4;
        (var4 = new Buffer(prop.data)).offset = prop.field72;
        Buffer var5;
        (var5 = new Buffer(prop.data)).offset = prop.field73;
        Buffer var6;
        (var6 = new Buffer(prop.data)).offset = prop.field74;
        int var7 = 0;
        int var8 = 0;
        int var9 = 0;

        int var11;
        int var12;
        int var13;
        int var14a;
        for (int i = 0; i < this.verticeCount; ++i) {
            var11 = var2.readUnsignedByte();
            var12 = 0;
            if ((var11 & 1) != 0) {
                var12 = var3.readShortSmart();
            }

            var13 = 0;
            if ((var11 & 2) != 0) {
                var13 = var4.readShortSmart();
            }

            var14a = 0;
            if ((var11 & 4) != 0) {
                var14a = var5.readShortSmart();
            }

            this.vertexX[i] = var7 + var12;
            this.vertexY[i] = var8 + var13;
            this.vertexZ[i] = var9 + var14a;
            var7 = this.vertexX[i];
            var8 = this.vertexY[i];
            var9 = this.vertexZ[i];
            if (this.vertexSkins != null) {
                this.vertexSkins[i] = var6.readUnsignedByte();
            }
        }

        var2.offset = prop.field77;
        var3.offset = prop.field78;
        var4.offset = prop.field79;
        var5.offset = prop.field80;
        var6.offset = prop.field81;

        for (int i = 0; i < this.triangleCount; i++) {
            this.faceColor[i] = var2.readUnsignedShort();
            if (this.faceRenderType != null) {
                this.faceRenderType[i] = var3.readUnsignedByte();
            }

            if (this.trianglePriorities != null) {
                this.trianglePriorities[i] = var4.readUnsignedByte();
            }

            if (this.faceAlphas != null) {
                this.faceAlphas[i] = var5.readUnsignedByte();
            }

            if (this.triangleSkinValues != null) {
                this.triangleSkinValues[i] = var6.readUnsignedByte();
            }
        }

        var2.offset = prop.field75;
        var3.offset = prop.field76;
        int var10 = 0;
        var11 = 0;
        var12 = 0;
        var13 = 0;

        for (int i = 0; i < this.triangleCount; i++) {
            int var16;
            if ((var16 = var3.readUnsignedByte()) == 1) {
                var13 = var10 = var2.readShortSmart() + var13;
                var13 = var11 = var2.readShortSmart() + var13;
                var13 = var12 = var2.readShortSmart() + var13;
                this.trianglePointsX[i] = var10;
                this.trianglePointsY[i] = var11;
                this.trianglePointsZ[i] = var12;
            }

            if (var16 == 2) {
                var11 = var12;
                var13 = var12 = var2.readShortSmart() + var13;
                this.trianglePointsX[i] = var10;
                this.trianglePointsY[i] = var11;
                this.trianglePointsZ[i] = var12;
            }

            if (var16 == 3) {
                var10 = var12;
                var13 = var12 = var2.readShortSmart() + var13;
                this.trianglePointsX[i] = var10;
                this.trianglePointsY[i] = var11;
                this.trianglePointsZ[i] = var12;
            }

            if (var16 == 4) {
                var16 = var10;
                var10 = var11;
                var11 = var16;
                var13 = var12 = var2.readShortSmart() + var13;
                this.trianglePointsX[i] = var10;
                this.trianglePointsY[i] = var16;
                this.trianglePointsZ[i] = var12;
            }
        }

        var2.offset = prop.field82;

        for (int i = 0; i < this.texTriangleCount; i++) {
            this.textureTriangleNIndex[i] = var2.readUnsignedShort();
            this.textureTriangleMIndex[i] = var2.readUnsignedShort();
            this.textureTrianglePIndex[i] = var2.readUnsignedShort();
        }

    }

    public static void load() {
        properties = new ModelProperties[1];
    }

    public static void initModel(byte[] data) {//load empty model
        if (data == null) {
            ModelProperties prop = properties[0] = new ModelProperties();
            prop.numVertices = 0;
            prop.numTriangles = 0;
            prop.numTexTriangles = 0;
        } else {
            Buffer buffer = new Buffer(data);
            buffer.offset = data.length - 18;
            ModelProperties prop = properties[0] = new ModelProperties();
            prop.data = data;
            prop.numVertices = buffer.readUnsignedShort();
            prop.numTriangles = buffer.readUnsignedShort();
            prop.numTexTriangles = buffer.readUnsignedByte();
            int hash = buffer.readUnsignedByte();
            int var3 = buffer.readUnsignedByte();
            int var4 = buffer.readUnsignedByte();
            int var5 = buffer.readUnsignedByte();
            int var6 = buffer.readUnsignedByte();
            int var7 = buffer.readUnsignedShort();
            int var8 = buffer.readUnsignedShort();
            buffer.readUnsignedShort();
            int var11 = buffer.readUnsignedShort();

            prop.field70 = 0;
            int var9 = prop.numVertices;
            prop.field76 = var9;
            var9 += prop.numTriangles;
            prop.field79 = var9;
            if (var3 == 255) {
                var9 += prop.numTriangles;
            } else {
                prop.field79 = -var3 - 1;
            }

            prop.field81 = var9;
            if (var5 == 1) {
                var9 += prop.numTriangles;
            } else {
                prop.field81 = -1;
            }

            prop.field78 = var9;
            if (hash == 1) {
                var9 += prop.numTriangles;
            } else {
                prop.field78 = -1;
            }

            prop.field74 = var9;
            if (var6 == 1) {
                var9 += prop.numVertices;
            } else {
                prop.field74 = -1;
            }

            prop.field80 = var9;
            if (var4 == 1) {
                var9 += prop.numTriangles;
            } else {
                prop.field80 = -1;
            }

            prop.field75 = var9;
            var9 += var11;
            prop.field77 = var9;
            var9 += prop.numTriangles << 1;
            prop.field82 = var9;
            var9 += prop.numTexTriangles * 6;
            prop.field71 = var9;
            var9 += var7;
            prop.field72 = var9;
            var9 += var8;
            prop.field73 = var9;
        }
    }

    public static Canvas createEmptyModel() {
        return properties == null ? null : new Canvas(0);
    }

    public static Canvas loadModel(byte[] modelBytes) {
        return new Canvas(modelBytes);
    }

    private static int method39(int var0, int var1, int var2) {
        if ((var2 & 2) == 2) {
            if (var1 < 0) {
                var1 = 0;
            } else if (var1 > 127) {
                var1 = 127;
            }

            return var1 = 127 - var1;
        } else {
            if ((var1 = var1 * (var0 & 127) >> 7) < 2) {
                var1 = 2;
            } else if (var1 > 126) {
                var1 = 126;
            }

            return (var0 & 'ﾀ') + var1;
        }
    }

    public void downScaleModel(int scale) {
        for (int i = 0; i < this.verticeCount; ++i) {
            this.vertexX[i] /= scale;//default is 4
            this.vertexY[i] /= scale;
            this.vertexZ[i] /= scale;
        }
    }

    public void method38(int var1, int uselessInt, int var3, int var4, int var5, boolean uselessBool) {
        var1 = (int) Math.sqrt(5100.0D);
        var1 = var1 * 768 >> 8;
        if (this.field100 == null) {
            this.field100 = new int[this.triangleCount];
            this.field101 = new int[this.triangleCount];
            this.field102 = new int[this.triangleCount];
        }

        if (super.field89 == null) {
            super.field89 = new Fields[this.verticeCount];

            for (int i = 0; i < this.verticeCount; ++i) {
                super.field89[i] = new Fields();
            }
        }

        int var7;
        int var8;
        int var9;
        int var10;
        int var11;
        int var12;
        int var20;
        for (int i = 0; i < this.triangleCount; ++i) {
            var3 = this.trianglePointsX[i];
            var4 = this.trianglePointsY[i];
            var5 = this.trianglePointsZ[i];
            if (var5 < 0) var5 = 0;
            if (var4 < 0) var4 = 0;
            if (var3 < 0) var3 = 0;
            var20 = this.vertexX[var4] - this.vertexX[var3];
            var7 = this.vertexY[var4] - this.vertexY[var3];
            var8 = this.vertexZ[var4] - this.vertexZ[var3];
            var9 = this.vertexX[var5] - this.vertexX[var3];
            var10 = this.vertexY[var5] - this.vertexY[var3];
            var11 = this.vertexZ[var5] - this.vertexZ[var3];
            var12 = var7 * var11 - var10 * var8;
            var8 = var8 * var9 - var11 * var20;

            for (var20 = var20 * var10 - var9 * var7; var12 > 8192 || var8 > 8192 || var20 > 8192 || var12 < -8192 || var8 < -8192 || var20 < -8192; var20 >>= 1) {
                var12 >>= 1;
                var8 >>= 1;
            }

            if ((var7 = (int) Math.sqrt(var12 * var12 + var8 * var8 + var20 * var20)) <= 0) {
                var7 = 1;
            }

            var12 = (var12 << 8) / var7;
            var8 = (var8 << 8) / var7;
            var20 = (var20 << 8) / var7;
            if (this.faceRenderType != null && (this.faceRenderType[i] & 1) != 0) {
                var3 = 64 + (var12 * -50 + var8 * -10 + var20 * -50) / (var1 + var1 / 2);
                this.field100[i] = method39(this.faceColor[i], var3, this.faceRenderType[i]);
            } else {
                Fields var13;
                Fields var10000 = var13 = super.field89[var3];
                var10000.field83 += var12;
                var13.field84 += var8;
                var13.field85 += var20;
                ++var13.field86;
                var10000 = var13 = super.field89[var4];
                var10000.field83 += var12;
                var13.field84 += var8;
                var13.field85 += var20;
                ++var13.field86;
                var10000 = var13 = super.field89[var5];
                var10000.field83 += var12;
                var13.field84 += var8;
                var13.field85 += var20;
                ++var13.field86;
            }
        }

        byte var21 = -50;
        byte var19 = -10;
        byte var18 = -50;
        var3 = var1;
        byte var17 = 64;
        Canvas var14 = this;

        for (var7 = 0; var7 < var14.triangleCount; ++var7) {
            var8 = var14.trianglePointsX[var7];
            var9 = var14.trianglePointsY[var7];
            var10 = var14.trianglePointsZ[var7];
            if (var8 < 0) var8 = 0;
            if (var9 < 0) var9 = 0;
            if (var10 < 0) var10 = 0;
            if (var14.faceRenderType == null) {
                var11 = var14.faceColor[var7];
                Fields var15 = var14.field89[var8];
                var8 = var17 + (var18 * var15.field83 + var19 * var15.field84 + var21 * var15.field85) / (var3 * var15.field86);
                var14.field100[var7] = method39(var11, var8, 0);
                var15 = var14.field89[var9];
                var8 = var17 + (var18 * var15.field83 + var19 * var15.field84 + var21 * var15.field85) / (var3 * var15.field86);
                var14.field101[var7] = method39(var11, var8, 0);
                var15 = var14.field89[var10];
                var8 = var17 + (var18 * var15.field83 + var19 * var15.field84 + var21 * var15.field85) / (var3 * var15.field86);
                var14.field102[var7] = method39(var11, var8, 0);
            } else if ((var14.faceRenderType[var7] & 1) == 0) {
                var11 = var14.faceColor[var7];
                var12 = var14.faceRenderType[var7];
                Fields var16 = var14.field89[var8];
                var8 = var17 + (var18 * var16.field83 + var19 * var16.field84 + var21 * var16.field85) / (var3 * var16.field86);
                var14.field100[var7] = method39(var11, var8, var12);
                var16 = var14.field89[var9];
                var8 = var17 + (var18 * var16.field83 + var19 * var16.field84 + var21 * var16.field85) / (var3 * var16.field86);
                var14.field101[var7] = method39(var11, var8, var12);
                var16 = var14.field89[var10];
                var8 = var17 + (var18 * var16.field83 + var19 * var16.field84 + var21 * var16.field85) / (var3 * var16.field86);
                var14.field102[var7] = method39(var11, var8, var12);
            }
        }

        label74:
        {
            var14.field89 = null;
            var14.field117 = null;
            var14.vertexSkins = null;
            var14.triangleSkinValues = null;
            if (var14.faceRenderType != null) {
                for (var7 = 0; var7 < var14.triangleCount; ++var7) {
                    if ((var14.faceRenderType[var7] & 2) == 2) {
                        break label74;
                    }
                }
            }

            var14.faceColor = null;
        }

        var14 = this;
        super.field90 = 0;
        this.field111 = 0;
        this.field112 = 0;

        for (int i = 0; i < var14.verticeCount; ++i) {
            var3 = var14.vertexX[i];
            var4 = var14.vertexY[i];
            var5 = var14.vertexZ[i];
            if (-var4 > var14.field90) {
                var14.field90 = -var4;
            }

            if (var4 > var14.field112) {
                var14.field112 = var4;
            }

            if ((var20 = var3 * var3 + var5 * var5) > var14.field111) {
                var14.field111 = var20;
            }
        }

        var14.field111 = (int) (Math.sqrt(var14.field111) + 0.99D);
        var14.field114 = (int) (Math.sqrt(var14.field111 * var14.field111 + var14.field90 * var14.field90) + 0.99D);
        var14.field113 = var14.field114 + (int) (Math.sqrt(var14.field111 * var14.field111 + var14.field112 * var14.field112) + 0.99D);
    }

    public void setViewPoint(int var1, int axisY, int var3, int axisX, int var5, int var6, int axisZ) {
        var5 = ModelCanvas.screenPosX;
        int var8 = ModelCanvas.screenPosY;
        int var9 = field138[var1];
        int var10 = field139[var1];
        int var11 = field138[axisY];
        int var12 = field139[axisY];
        int var13 = field138[var3];
        int var14 = field139[var3];
        int var15 = field138[axisX];
        axisX = field139[axisX];
        int var16 = var6 * var15 + axisZ * axisX >> 16;

        for (int var17 = 0; var17 < this.verticeCount; ++var17) {
            int var18 = this.vertexX[var17];
            int var19 = this.vertexY[var17];
            int var20 = this.vertexZ[var17];
            int var21;
            if (var3 != 0) {
                var21 = var19 * var13 + var18 * var14 >> 16;
                var19 = var19 * var14 - var18 * var13 >> 16;
                var18 = var21;
            }

            if (var1 != 0) {
                var21 = var19 * var10 - var20 * var9 >> 16;
                var20 = var19 * var9 + var20 * var10 >> 16;
                var19 = var21;
            }

            if (axisY != 0) {
                var21 = var20 * var11 + var18 * var12 >> 16;
                var20 = var20 * var12 - var18 * var11 >> 16;
                var18 = var21;
            }

            var19 += var6;
            var20 += axisZ;
            var21 = var19 * axisX - var20 * var15 >> 16;
            var20 = var19 * var15 + var20 * axisX >> 16;
            field123[var17] = var20 - var16;
            if (var20 != 0) {
                field121[var17] = var5 + (var18 << 9) / var20;
                field122[var17] = var8 + (var21 << 9) / var20;
            }
            if (this.texTriangleCount > 0) {
                field124[var17] = var18;
                field125[var17] = var21;
                field126[var17] = var20;
            }
        }

        try {
            Canvas model = this;

            for (axisY = 0; axisY < model.field113; ++axisY) {
                field127[axisY] = 0;
            }

            for (axisY = 0; axisY < model.triangleCount; ++axisY) {
                if (model.faceRenderType == null || model.faceRenderType[axisY] != -1) {
                    var3 = model.trianglePointsX[axisY];
                    axisX = model.trianglePointsY[axisY];
                    var5 = model.trianglePointsZ[axisY];
                    var6 = field121[var3];
                    axisZ = field121[axisX];
                    var8 = field121[var5];
                    if ((var6 - axisZ) * (field122[var5] - field122[axisX]) - (field122[var3] - field122[axisX]) * (var8 - axisZ) > 0) {
                        field120[axisY] = false;
                        field119[axisY] = var6 < 0 || axisZ < 0 || var8 < 0 || var6 > PanelProperties.field65 || axisZ > PanelProperties.field65 || var8 > PanelProperties.field65;
                        var9 = (field123[var3] + field123[axisX] + field123[var5]) / 3 + model.field114;
                        field128[var9][field127[var9]++] = axisY;
                    }
                }
            }

            int[] var23;
            if (model.trianglePriorities != null) {
                for (axisY = 0; axisY < 12; ++axisY) {
                    field129[axisY] = 0;
                    field133[axisY] = 0;
                }

                for (axisY = model.field113 - 1; axisY >= 0; --axisY) {
                    if ((var3 = field127[axisY]) > 0) {
                        var23 = field128[axisY];

                        for (var5 = 0; var5 < var3; ++var5) {
                            var6 = var23[var5];
                            axisZ = model.trianglePriorities[var6];
                            var8 = field129[axisZ]++;
                            field130[axisZ][var8] = var6;
                            if (axisZ < 10) {
                                field133[axisZ] += axisY;
                            } else if (axisZ == 10) {
                                field131[var8] = axisY;
                            } else {
                                field132[var8] = axisY;
                            }
                        }
                    }
                }

                axisY = 0;
                if (field129[1] > 0 || field129[2] > 0) {
                    axisY = (field133[1] + field133[2]) / (field129[1] + field129[2]);
                }

                var3 = 0;
                if (field129[3] > 0 || field129[4] > 0) {
                    var3 = (field133[3] + field133[4]) / (field129[3] + field129[4]);
                }

                axisX = 0;
                if (field129[6] > 0 || field129[8] > 0) {
                    axisX = (field133[6] + field133[8]) / (field129[6] + field129[8]);
                }

                var5 = 0;
                var6 = field129[10];
                int[] var24 = field130[10];
                int[] var25 = field131;
                if (var6 == 0) {
                    var5 = 0;
                    var6 = field129[11];
                    var24 = field130[11];
                    var25 = field132;
                }

                if (var6 > 0) {
                    var9 = var25[0];
                } else {
                    var9 = -1000;
                }

                for (var10 = 0; var10 < 10; ++var10) {
                    while (var10 == 0 && var9 > axisY) {
                        model.method41(var24[var5++]);
                        if (var5 == var6 && var24 != field130[11]) {
                            var5 = 0;
                            var6 = field129[11];
                            var24 = field130[11];
                            var25 = field132;
                        }

                        if (var5 < var6) {
                            var9 = var25[var5];
                        } else {
                            var9 = -1000;
                        }
                    }

                    while (var10 == 3 && var9 > var3) {
                        model.method41(var24[var5++]);
                        if (var5 == var6 && var24 != field130[11]) {
                            var5 = 0;
                            var6 = field129[11];
                            var24 = field130[11];
                            var25 = field132;
                        }

                        if (var5 < var6) {
                            var9 = var25[var5];
                        } else {
                            var9 = -1000;
                        }
                    }

                    while (var10 == 5 && var9 > axisX) {
                        model.method41(var24[var5++]);
                        if (var5 == var6 && var24 != field130[11]) {
                            var5 = 0;
                            var6 = field129[11];
                            var24 = field130[11];
                            var25 = field132;
                        }

                        if (var5 < var6) {
                            var9 = var25[var5];
                        } else {
                            var9 = -1000;
                        }
                    }

                    var11 = field129[var10];
                    int[] var26 = field130[var10];

                    for (var13 = 0; var13 < var11; ++var13) {
                        model.method41(var26[var13]);
                    }
                }

                while (var9 != -1000) {
                    model.method41(var24[var5++]);
                    if (var5 == var6 && var24 != field130[11]) {
                        var5 = 0;
                        var24 = field130[11];
                        var6 = field129[11];
                        var25 = field132;
                    }

                    if (var5 < var6) {
                        var9 = var25[var5];
                    } else {
                        var9 = -1000;
                    }
                }

                return;
            }

            for (axisY = model.field113 - 1; axisY >= 0; --axisY) {
                if ((var3 = field127[axisY]) > 0) {
                    var23 = field128[axisY];

                    for (var5 = 0; var5 < var3; ++var5) {
                        model.method41(var23[var5]);
                    }
                }
            }
        } catch (Exception var27) {
        }

    }

    private void method41(int var1) {
        int var3;
        int var4;
        int var5;
        int var6;
        int var7;
        if (field120[var1]) {
            var3 = ModelCanvas.screenPosX;
            var4 = ModelCanvas.screenPosY;
            var5 = 0;
            var6 = this.trianglePointsX[var1];
            var7 = this.trianglePointsY[var1];
            int var8 = this.trianglePointsZ[var1];
            int var9 = field126[var6];
            int var10 = field126[var7];
            int var11 = field126[var8];
            int var12;
            int var13;
            int var14;
            int var15;
            if (var9 >= 50) {
                field134[0] = field121[var6];
                field135[0] = field122[var6];
                ++var5;
                field136[0] = this.field100[var1];
            } else {
                var12 = field124[var6];
                var13 = field125[var6];
                var14 = this.field100[var1];
                if (var11 >= 50) {
                    var15 = (50 - var9) * field141[var11 - var9];
                    field134[0] = var3 + (var12 + ((field124[var8] - var12) * var15 >> 16) << 9) / 50;
                    field135[0] = var4 + (var13 + ((field125[var8] - var13) * var15 >> 16) << 9) / 50;
                    ++var5;
                    field136[0] = var14 + ((this.field102[var1] - var14) * var15 >> 16);
                }

                if (var10 >= 50) {
                    var15 = (50 - var9) * field141[var10 - var9];
                    field134[var5] = var3 + (var12 + ((field124[var7] - var12) * var15 >> 16) << 9) / 50;
                    field135[var5] = var4 + (var13 + ((field125[var7] - var13) * var15 >> 16) << 9) / 50;
                    field136[var5++] = var14 + ((this.field101[var1] - var14) * var15 >> 16);
                }
            }

            if (var10 >= 50) {
                field134[var5] = field121[var7];
                field135[var5] = field122[var7];
                field136[var5++] = this.field101[var1];
            } else {
                var12 = field124[var7];
                var13 = field125[var7];
                var14 = this.field101[var1];
                if (var9 >= 50) {
                    var15 = (50 - var10) * field141[var9 - var10];
                    field134[var5] = var3 + (var12 + ((field124[var6] - var12) * var15 >> 16) << 9) / 50;
                    field135[var5] = var4 + (var13 + ((field125[var6] - var13) * var15 >> 16) << 9) / 50;
                    field136[var5++] = var14 + ((this.field100[var1] - var14) * var15 >> 16);
                }

                if (var11 >= 50) {
                    var15 = (50 - var10) * field141[var11 - var10];
                    field134[var5] = var3 + (var12 + ((field124[var8] - var12) * var15 >> 16) << 9) / 50;
                    field135[var5] = var4 + (var13 + ((field125[var8] - var13) * var15 >> 16) << 9) / 50;
                    field136[var5++] = var14 + ((this.field102[var1] - var14) * var15 >> 16);
                }
            }

            if (var11 >= 50) {
                field134[var5] = field121[var8];
                field135[var5] = field122[var8];
                field136[var5++] = this.field102[var1];
            } else {
                var12 = field124[var8];
                var13 = field125[var8];
                var14 = this.field102[var1];
                if (var10 >= 50) {
                    var15 = (50 - var11) * field141[var10 - var11];
                    field134[var5] = var3 + (var12 + ((field124[var7] - var12) * var15 >> 16) << 9) / 50;
                    field135[var5] = var4 + (var13 + ((field125[var7] - var13) * var15 >> 16) << 9) / 50;
                    field136[var5++] = var14 + ((this.field101[var1] - var14) * var15 >> 16);
                }

                if (var9 >= 50) {
                    var15 = (50 - var11) * field141[var9 - var11];
                    field134[var5] = var3 + (var12 + ((field124[var6] - var12) * var15 >> 16) << 9) / 50;
                    field135[var5] = var4 + (var13 + ((field125[var6] - var13) * var15 >> 16) << 9) / 50;
                    field136[var5++] = var14 + ((this.field100[var1] - var14) * var15 >> 16);
                }
            }

            var12 = field134[0];
            var13 = field134[1];
            var14 = field134[2];
            var15 = field135[0];
            var3 = field135[1];
            var4 = field135[2];
            if ((var12 - var13) * (var4 - var3) - (var15 - var3) * (var14 - var13) > 0) {
                ModelCanvas.field8 = false;
                if (var5 == 3) {
                    if (var12 < 0 || var13 < 0 || var14 < 0 || var12 > PanelProperties.field65 || var13 > PanelProperties.field65 || var14 > PanelProperties.field65) {
                        ModelCanvas.field8 = true;
                    }

                    if (this.faceRenderType == null) {
                        var6 = 0;
                    } else {
                        var6 = this.faceRenderType[var1] & 3;
                    }

                    if (var6 == 0) {
                        ModelCanvas.fillFace(var15, var3, var4, var12, var13, var14, field136[0], field136[1], field136[2]);
                    } else if (var6 == 1) {
                        ModelCanvas.method5(var15, var3, var4, var12, var13, var14, field140[this.field100[var1]]);
                    } else if (var6 == 2) {
                        var6 = this.faceRenderType[var1] >> 2;
                        var7 = this.textureTriangleNIndex[var6];
                        var8 = this.textureTriangleMIndex[var6];
                        var6 = this.textureTrianglePIndex[var6];
                        ModelCanvas.method7(var15, var3, var4, var12, var13, var14, field136[0], field136[1], field136[2], field124[var7], field124[var8], field124[var6], field125[var7], field125[var8], field125[var6], field126[var7], field126[var8], field126[var6], this.faceColor[var1]);
                    } else if (var6 == 3) {
                        var6 = this.faceRenderType[var1] >> 2;
                        var7 = this.textureTriangleNIndex[var6];
                        var8 = this.textureTriangleMIndex[var6];
                        var6 = this.textureTrianglePIndex[var6];
                        ModelCanvas.method7(var15, var3, var4, var12, var13, var14, this.field100[var1], this.field100[var1], this.field100[var1], field124[var7], field124[var8], field124[var6], field125[var7], field125[var8], field125[var6], field126[var7], field126[var8], field126[var6], this.faceColor[var1]);
                    }
                }

                if (var5 == 4) {
                    if (var12 < 0 || var13 < 0 || var14 < 0 || var12 > PanelProperties.field65 || var13 > PanelProperties.field65 || var14 > PanelProperties.field65 || field134[3] < 0 || field134[3] > PanelProperties.field65) {
                        ModelCanvas.field8 = true;
                    }

                    if (this.faceRenderType == null) {
                        var6 = 0;
                    } else {
                        var6 = this.faceRenderType[var1] & 3;
                    }

                    if (var6 == 0) {
                        ModelCanvas.fillFace(var15, var3, var4, var12, var13, var14, field136[0], field136[1], field136[2]);
                        ModelCanvas.fillFace(var15, var4, field135[3], var12, var14, field134[3], field136[0], field136[2], field136[3]);
                        return;
                    }

                    if (var6 == 1) {
                        var6 = field140[this.field100[var1]];
                        ModelCanvas.method5(var15, var3, var4, var12, var13, var14, var6);
                        ModelCanvas.method5(var15, var4, field135[3], var12, var14, field134[3], var6);
                        return;
                    }

                    if (var6 == 2) {
                        var6 = this.faceRenderType[var1] >> 2;
                        var7 = this.textureTriangleNIndex[var6];
                        var8 = this.textureTriangleMIndex[var6];
                        var6 = this.textureTrianglePIndex[var6];
                        ModelCanvas.method7(var15, var3, var4, var12, var13, var14, field136[0], field136[1], field136[2], field124[var7], field124[var8], field124[var6], field125[var7], field125[var8], field125[var6], field126[var7], field126[var8], field126[var6], this.faceColor[var1]);
                        ModelCanvas.method7(var15, var4, field135[3], var12, var14, field134[3], field136[0], field136[2], field136[3], field124[var7], field124[var8], field124[var6], field125[var7], field125[var8], field125[var6], field126[var7], field126[var8], field126[var6], this.faceColor[var1]);
                        return;
                    }

                    if (var6 == 3) {
                        var6 = this.faceRenderType[var1] >> 2;
                        var7 = this.textureTriangleNIndex[var6];
                        var8 = this.textureTriangleMIndex[var6];
                        var6 = this.textureTrianglePIndex[var6];
                        ModelCanvas.method7(var15, var3, var4, var12, var13, var14, this.field100[var1], this.field100[var1], this.field100[var1], field124[var7], field124[var8], field124[var6], field125[var7], field125[var8], field125[var6], field126[var7], field126[var8], field126[var6], this.faceColor[var1]);
                        ModelCanvas.method7(var15, var4, field135[3], var12, var14, field134[3], this.field100[var1], this.field100[var1], this.field100[var1], field124[var7], field124[var8], field124[var6], field125[var7], field125[var8], field125[var6], field126[var7], field126[var8], field126[var6], this.faceColor[var1]);
                    }
                }
            }

        } else {
            int var2 = this.trianglePointsX[var1];
            var3 = this.trianglePointsY[var1];
            var4 = this.trianglePointsZ[var1];
            ModelCanvas.field8 = field119[var1];
            if (this.faceAlphas == null) {
                ModelCanvas.field11 = 0;
            } else {
                ModelCanvas.field11 = this.faceAlphas[var1];
            }

            if (this.faceRenderType == null) {
                var5 = 0;
            } else {
                var5 = this.faceRenderType[var1] & 3;
            }

            if (var5 == 0) {
                ModelCanvas.fillFace(field122[var2], field122[var3], field122[var4], field121[var2], field121[var3], field121[var4], this.field100[var1], this.field101[var1], this.field102[var1]);
            } else if (var5 == 1) {
                ModelCanvas.method5(field122[var2], field122[var3], field122[var4], field121[var2], field121[var3], field121[var4], field140[this.field100[var1]]);
            } else if (var5 == 2) {
                var5 = this.faceRenderType[var1] >> 2;
                var6 = this.textureTriangleNIndex[var5];
                var7 = this.textureTriangleMIndex[var5];
                var5 = this.textureTrianglePIndex[var5];
                ModelCanvas.method7(field122[var2], field122[var3], field122[var4], field121[var2], field121[var3], field121[var4], this.field100[var1], this.field101[var1], this.field102[var1], field124[var6], field124[var7], field124[var5], field125[var6], field125[var7], field125[var5], field126[var6], field126[var7], field126[var5], this.faceColor[var1]);
            } else {
                if (var5 == 3) {
                    var5 = this.faceRenderType[var1] >> 2;
                    var6 = this.textureTriangleNIndex[var5];
                    var7 = this.textureTriangleMIndex[var5];
                    var5 = this.textureTrianglePIndex[var5];
                    ModelCanvas.method7(field122[var2], field122[var3], field122[var4], field121[var2], field121[var3], field121[var4], this.field100[var1], this.field100[var1], this.field100[var1], field124[var6], field124[var7], field124[var5], field125[var6], field125[var7], field125[var5], field126[var6], field126[var7], field126[var5], this.faceColor[var1]);
                }

            }
        }
    }
}
