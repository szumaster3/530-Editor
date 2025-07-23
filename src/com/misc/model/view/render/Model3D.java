package com.misc.model.view.render;

import com.cache.util.Utils;
import com.jogamp.opengl.GL;
import com.jogamp.opengl.GL2;

public class Model3D extends Model {

    private int width, height, depth;

    public Model3D(byte[] arg0) {
        super(arg0);
    }

    public void render(float x, float y, float z, float rx, float ry, float rz, float sx, float sy, float sz, GL2 gl) {
        gl.glLoadIdentity();
        gl.glTranslatef(x, y, z);
        gl.glRotatef(rx, 1.0F, 0.0F, 0.0F);
        gl.glRotatef(ry, 0.0F, 1.0F, 0.0F);
        gl.glRotatef(rz, 0.0F, 0.0F, 1.0F);
        gl.glScalef(sx, sy, sz);
        /*short[] tri_x = this.aShortArray1514;
        short[] tri_y = this.aShortArray1513;
        short[] tri_z = this.aShortArray1541;
        short[] tex_map_x = this.texTrianglesPoint3;//textureTrianglePIndex
        short[] tex_map_y = this.texTrianglesPoint2;//textureTriangleMIndex
        short[] tex_map_z = this.texTrianglesPoint1;//textureTriangleNIndex
        short[] textures = this.modelTextures;
        int[] vertices_x = this.vertexX;
        int[] vertices_y = this.vertexY;
        int[] vertices_z = this.vertexZ;
        short[] colors = this.modelColors;*/

        short[] tri_x = this.triangleViewspaceX;
        short[] tri_y = this.triangleViewspaceY;
        short[] tri_z = this.triangleViewspaceZ;
        short[] tex_map_x = this.textureTrianglePIndex;
        short[] tex_map_y = this.textureTriangleMIndex;
        short[] tex_map_z = this.textureTriangleNIndex;
        short[] textures = this.faceTexture;
        int[] vertices_x = this.verticesX;
        int[] vertices_y = this.verticesY;
        int[] vertices_z = this.verticesZ;
        short[] colors = this.faceColors;
        float model_scale = this.version < 13 ? 1.0F : 4.0F;
        /*if (textures != null) {//textureCoords
            gl.glEnable(GL.GL_DEPTH_TEST);
            gl.glEnable(GL.GL_TEXTURE_2D);
            gl.glTexParameterf(GL.GL_TEXTURE_2D, GL.GL_TEXTURE_MIN_FILTER, (int)GL.GL_NEAREST);
            gl.glTexParameterf(GL.GL_TEXTURE_2D, GL.GL_TEXTURE_MAG_FILTER, (int)GL.GL_NEAREST);
        }*/
        //TODO MAKE 2 LOOPS AND RENDER ARGB TRIANGLES LAST
        //System.out.println(numTexTriangles + " " + vertices_x.length + " " + vertices_y.length + " " + vertices_z.length + " " + tri_x.length + " " + tri_y.length + " " + tri_z.length);
        //System.out.println(texTrianglesPoint1.length + " " + texTrianglesPoint2.length + " " + texTrianglesPoint3.length + " ");
        for (int triangle = 0; triangle < numTriangles; triangle++) {
            if (faceAlpha != null && faceAlpha[triangle] == -1) {//faceAlpha : aByteArray1505
                continue;
            }
            int point_a = tri_x[triangle];
            int point_b = tri_y[triangle];
            int point_c = tri_z[triangle];
            int color = Utils.getHsl2rgb()[colors[triangle] & 0xffff];
            //  Console.WriteLine("Triangle " + triangle + ", HSL: " + (colors[triangle] & 0xffff) + ", RGB: " + color);
            int triangle_type;
            if (faceRenderType == null) {//faceRenderType : aByteArray1493
                triangle_type = 0;
            } else {
                triangle_type = faceRenderType[triangle] & 3;
            }
            //9631 firecape

            if (textures != null && textures[triangle] != -1) {


                int id = textures[triangle] & 0xffff;//a lot of Ids are WRONG
                //Console.WriteLine(id);

                if (id != -1) {
                    //System.out.println("Texture ID: " + id);
                    //int i = LoadTexture(id, Form1.loc + @"\textures\" + id + ".png", 1, true, false);

                    //gl.glBindTexture(TextureTarget.Texture2D, i);
                }
            }

            /*if (Form1.enableParticles)
            {
                // int particleCol = this.texturePrimaryColor[triangle];
                //this.textureSecondaryColor = new int[particle_color];
            }*/
            //boolean textured = false;
            boolean textured = textures != null;
            //bool textured = Form1.enableModelTextures & textures != null;
            gl.glBegin(GL.GL_TRIANGLES);
            byte r = ((byte) (color >> 16));
            byte g = ((byte) (color >> 8));
            byte b = ((byte) color);
            byte alpha = (byte) (faceAlpha == null ? 0xff : ~faceAlpha[triangle]);
            /*if (r < 0)
            	r = (byte)(r * -1);
            if (g < 0)
            	g = (byte)(g * -1);
            if (b < 0)
            	b = (byte)(b * -1);*/
            //gl.glColor4ub(r, g, b, );
            gl.glColor4ub(r, g, b, alpha);//(byte)(faceAlpha == null ? 0xff : ~faceAlpha[triangle])
            switch (triangle_type) {
                case 0://shaded triangle
                case 1://flat triangle
                    if (textured) {
                        //gl.glTexCoord2(textureMap.textureCoordU[triangle].X, textureMap.textureCoordV[triangle].X);
                        //gl.glTexCoord2s(tex_map_x[point_a], tex_map_x[point_a]);
                    }
                    gl.glVertex3f((float) vertices_x[point_a] / model_scale, (float) vertices_y[point_a] / model_scale, (float) vertices_z[point_a] / model_scale);
                    if (textured) {
                        //gl.glTexCoord2(textureMap.textureCoordU[triangle].Y, textureMap.textureCoordV[triangle].Y);
                        //gl.glTexCoord2s(tex_map_y[point_b], tex_map_y[point_b]);
                    }
                    gl.glVertex3f((float) vertices_x[point_b] / model_scale, (float) vertices_y[point_b] / model_scale, (float) vertices_z[point_b] / model_scale);
                    if (textured) {
                        //gl.glTexCoord2(textureMap.textureCoordU[triangle].Z, textureMap.textureCoordV[triangle].Z);
                        //gl.glTexCoord2s(tex_map_z[point_c], tex_map_z[point_c]);
                    }
                    gl.glVertex3f((float) vertices_x[point_c] / model_scale, (float) vertices_y[point_c] / model_scale, (float) vertices_z[point_c] / model_scale);
                    break;
                case 2://textured?
                case 3://textured?
                    int ptr = faceRenderType[triangle] >> 2;
                    int tex_point_a = textureTrianglePIndex[ptr];//textureTrianglePIndex : texTrianglesPoint3
                    int tex_point_b = textureTriangleMIndex[ptr];//textureTriangleMIndex
                    int tex_point_c = textureTriangleNIndex[ptr];//textureTriangleNIndex

                    try {
                        // gl.glTexCoord2(textureMap.textureCoordU[triangle].X, textureMap.textureCoordV[triangle].X);
                        gl.glTexCoord2s(tex_map_x[tex_point_a], tex_map_x[tex_point_a]);
                        gl.glVertex3f((float) vertices_x[tex_point_a] / model_scale, (float) vertices_y[tex_point_a] / model_scale, (float) vertices_z[tex_point_a] / model_scale);
                        //gl.glTexCoord2(textureMap.textureCoordU[triangle].Y, textureMap.textureCoordV[triangle].Y);
                        gl.glTexCoord2s(tex_map_y[tex_point_b], tex_map_y[tex_point_b]);
                        gl.glVertex3f((float) vertices_x[tex_point_b] / model_scale, (float) vertices_y[tex_point_b] / model_scale, (float) vertices_z[tex_point_b] / model_scale);
                        //gl.glTexCoord2(textureMap.textureCoordU[triangle].Z, textureMap.textureCoordV[triangle].Z);
                        gl.glTexCoord2s(tex_map_z[tex_point_c], tex_map_z[tex_point_c]);
                        gl.glVertex3f((float) vertices_x[tex_point_c] / model_scale, (float) vertices_y[tex_point_c] / model_scale, (float) vertices_z[tex_point_c] / model_scale);
                    } catch (Exception e) {
                        //System.out.println("A: " + tex_point_a + " : " + tex_map_x.length + ", B: " + tex_point_b + " : " + tex_map_y.length + ", C: " + tex_point_c + " : " + tex_map_z.length);
                    }
                    break;
            }
            gl.glEnd();
        }
        if (textures == null) {
            gl.glDisable(GL.GL_TEXTURE_2D);
        }
    }

    public void calcDimms(boolean force) {
        if (!force && width >= 0 && height >= 0 && depth >= 0) return;

        if (numTriangles == 0) {
            width = 0;
            height = 0;
            depth = 0;
            return;
        }
        short[] tri_x = this.triangleViewspaceX;
        short[] tri_y = this.triangleViewspaceY;
        short[] tri_z = this.triangleViewspaceZ;
        int[] vertices_x = this.verticesX;
        int[] vertices_y = this.verticesY;
        int[] vertices_z = this.verticesZ;
        int minX = 0x7fffffff;
        int maxX = -0x7fffffff;
        int minY = 0x7fffffff;
        int maxY = -0x7fffffff;
        int minZ = 0x7fffffff;
        int maxZ = -0x7fffffff;
        for (int i = 0; i != numTriangles; ++i) {
            int t = tri_x[i] & 0xffff;
            if (maxX < vertices_x[t]) maxX = vertices_x[t];

            if (minX > vertices_x[t]) minX = vertices_x[t];

            if (maxY < vertices_y[t]) maxY = vertices_y[t];

            if (minY > vertices_y[t]) minY = vertices_y[t];

            if (maxZ < vertices_z[t]) maxZ = vertices_z[t];

            if (minZ > vertices_z[t]) minZ = vertices_z[t];

            t = tri_y[i] & 0xffff;
            if (maxX < vertices_x[t]) maxX = vertices_x[t];

            if (minX > vertices_x[t]) minY = vertices_x[t];

            if (maxY < vertices_y[t]) maxY = vertices_y[t];

            if (minY > vertices_y[t]) minY = vertices_y[t];

            if (maxZ < vertices_z[t]) maxZ = vertices_z[t];

            if (minZ > vertices_z[t]) minZ = vertices_z[t];

            t = tri_z[i] & 0xffff;
            if (maxX < vertices_x[t]) maxX = vertices_x[t];

            if (minX > vertices_x[t]) minX = vertices_x[t];

            if (maxY < vertices_y[t]) maxY = vertices_y[t];

            if (minY > vertices_y[t]) minY = vertices_y[t];

            if (maxZ < vertices_z[t]) maxZ = vertices_z[t];

            if (minZ > vertices_z[t]) minZ = vertices_z[t];

        }

        width = maxX - minX;
        height = maxY - minY;
        depth = maxZ - minZ;
    }

    public void resetDimms() {
        width = height = depth = -1;
    }

    public float getWidth() {
        return (float) width;
    }

    public float getHeight() {
        return (float) height;
    }

    public float getDepth() {
        return (float) depth;
    }
}
