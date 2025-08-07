package com.utils.model.render;

import cache.alex.util.Utils;
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




        for (int triangle = 0; triangle < numTriangles; triangle++) {
            if (faceAlpha != null && faceAlpha[triangle] == -1) {
                continue;
            }
            int point_a = tri_x[triangle];
            int point_b = tri_y[triangle];
            int point_c = tri_z[triangle];
            int color = Utils.getHsl2rgb()[colors[triangle] & 0xffff];

            int triangle_type;
            if (faceRenderType == null) {
                triangle_type = 0;
            } else {
                triangle_type = faceRenderType[triangle] & 3;
            }


            if (textures != null && textures[triangle] != -1) {


                int id = textures[triangle] & 0xffff;


                if (id != -1) {




                }
            }



            boolean textured = textures != null;

            gl.glBegin(GL.GL_TRIANGLES);
            byte r = ((byte) (color >> 16));
            byte g = ((byte) (color >> 8));
            byte b = ((byte) color);
            byte alpha = (byte) (faceAlpha == null ? 0xff : ~faceAlpha[triangle]);


            gl.glColor4ub(r, g, b, alpha);
            switch (triangle_type) {
                case 0:
                case 1:
                    if (textured) {


                    }
                    gl.glVertex3f((float) vertices_x[point_a] / model_scale, (float) vertices_y[point_a] / model_scale, (float) vertices_z[point_a] / model_scale);
                    if (textured) {


                    }
                    gl.glVertex3f((float) vertices_x[point_b] / model_scale, (float) vertices_y[point_b] / model_scale, (float) vertices_z[point_b] / model_scale);
                    if (textured) {


                    }
                    gl.glVertex3f((float) vertices_x[point_c] / model_scale, (float) vertices_y[point_c] / model_scale, (float) vertices_z[point_c] / model_scale);
                    break;
                case 2:
                case 3:
                    int ptr = faceRenderType[triangle] >> 2;
                    int tex_point_a = textureTrianglePIndex[ptr];
                    int tex_point_b = textureTriangleMIndex[ptr];
                    int tex_point_c = textureTriangleNIndex[ptr];

                    try {

                        gl.glTexCoord2s(tex_map_x[tex_point_a], tex_map_x[tex_point_a]);
                        gl.glVertex3f((float) vertices_x[tex_point_a] / model_scale, (float) vertices_y[tex_point_a] / model_scale, (float) vertices_z[tex_point_a] / model_scale);

                        gl.glTexCoord2s(tex_map_y[tex_point_b], tex_map_y[tex_point_b]);
                        gl.glVertex3f((float) vertices_x[tex_point_b] / model_scale, (float) vertices_y[tex_point_b] / model_scale, (float) vertices_z[tex_point_b] / model_scale);

                        gl.glTexCoord2s(tex_map_z[tex_point_c], tex_map_z[tex_point_c]);
                        gl.glVertex3f((float) vertices_x[tex_point_c] / model_scale, (float) vertices_y[tex_point_c] / model_scale, (float) vertices_z[tex_point_c] / model_scale);
                    } catch (Exception e) {

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
