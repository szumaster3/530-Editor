package cache.openrs.cache.sprite;

import cache.openrs.util.ByteBufferUtils;

import java.awt.image.BufferedImage;
import java.io.ByteArrayOutputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.util.ArrayList;
import java.util.List;


public final class Sprite {


    public static final int FLAG_VERTICAL = 0x01;


    public static final int FLAG_ALPHA = 0x02;


    public static Sprite decode(ByteBuffer buffer) {

        buffer.position(buffer.limit() - 2);
        int size = buffer.getShort() & 0xFFFF;


        int[] offsetsX = new int[size];
        int[] offsetsY = new int[size];
        int[] subWidths = new int[size];
        int[] subHeights = new int[size];


        buffer.position(buffer.limit() - size * 8 - 7);
        int width = buffer.getShort() & 0xFFFF;
        int height = buffer.getShort() & 0xFFFF;
        int[] palette = new int[(buffer.get() & 0xFF) + 1];


        Sprite set = new Sprite(width, height, size);


        for (int i = 0; i < size; i++) {
            offsetsX[i] = buffer.getShort() & 0xFFFF;
        }
        for (int i = 0; i < size; i++) {
            offsetsY[i] = buffer.getShort() & 0xFFFF;
        }
        for (int i = 0; i < size; i++) {
            subWidths[i] = buffer.getShort() & 0xFFFF;
        }
        for (int i = 0; i < size; i++) {
            subHeights[i] = buffer.getShort() & 0xFFFF;
        }


        buffer.position(buffer.limit() - size * 8 - 7 - (palette.length - 1) * 3);
        palette[0] = 0;
        for (int index = 1; index < palette.length; index++) {
            palette[index] = ByteBufferUtils.getTriByte(buffer);
            if (palette[index] == 0)
                palette[index] = 1;
        }


        buffer.position(0);
        for (int id = 0; id < size; id++) {

            int subWidth = subWidths[id], subHeight = subHeights[id];
            int offsetX = offsetsX[id], offsetY = offsetsY[id];


            BufferedImage image = set.frames[id] = new BufferedImage(width, height, BufferedImage.TYPE_INT_ARGB);


            int[][] indices = new int[subWidth][subHeight];


            int flags = buffer.get() & 0xFF;


            if (image != null) {

                if ((flags & FLAG_VERTICAL) != 0) {
                    for (int x = 0; x < subWidth; x++) {
                        for (int y = 0; y < subHeight; y++) {
                            indices[x][y] = buffer.get() & 0xFF;
                        }
                    }
                } else {
                    for (int y = 0; y < subHeight; y++) {
                        for (int x = 0; x < subWidth; x++) {
                            indices[x][y] = buffer.get() & 0xFF;
                        }
                    }
                }


                if ((flags & FLAG_ALPHA) != 0) {
                    if ((flags & FLAG_VERTICAL) != 0) {
                        for (int x = 0; x < subWidth; x++) {
                            for (int y = 0; y < subHeight; y++) {
                                int alpha = buffer.get() & 0xFF;
                                image.setRGB(x + offsetX, y + offsetY, alpha << 24 | palette[indices[x][y]]);
                            }
                        }
                    } else {
                        for (int y = 0; y < subHeight; y++) {
                            for (int x = 0; x < subWidth; x++) {
                                int alpha = buffer.get() & 0xFF;
                                image.setRGB(x + offsetX, y + offsetY, alpha << 24 | palette[indices[x][y]]);
                            }
                        }
                    }
                } else {
                    for (int x = 0; x < subWidth; x++) {
                        for (int y = 0; y < subHeight; y++) {
                            int index = indices[x][y];
                            if (index == 0) {
                                image.setRGB(x + offsetX, y + offsetY, 0);
                            } else {
                                image.setRGB(x + offsetX, y + offsetY, 0xFF000000 | palette[index]);
                            }
                        }
                    }
                }
            }
        }
        return set;
    }


    private final int width;


    private final int height;


    private final BufferedImage[] frames;


    public Sprite(int width, int height) {
        this(width, height, 1);
    }


    public Sprite(int width, int height, int size) {
        if (size < 1)
            throw new IllegalArgumentException();

        this.width = width;
        this.height = height;
        this.frames = new BufferedImage[size];
    }


    public int getWidth() {
        return width;
    }


    public int getHeight() {
        return height;
    }


    public int size() {
        return frames.length;
    }


    public BufferedImage getFrame(int id) {
        return frames[id];
    }


    public void setFrame(int id, BufferedImage frame) {
        if (frame.getWidth() != width || frame.getHeight() != height)
            throw new IllegalArgumentException("The frame's dimensions do not match with the sprite's dimensions.");

        frames[id] = frame;
    }


    public ByteBuffer encode() throws IOException {
        ByteArrayOutputStream bout = new ByteArrayOutputStream();
        DataOutputStream os = new DataOutputStream(bout);
        try {

            List<Integer> palette = new ArrayList<Integer>();
            palette.add(0);


            for (BufferedImage image : frames) {

                if (image.getWidth() != width || image.getHeight() != height)
                    throw new IOException("All frames must be the same size.");


                int flags = FLAG_VERTICAL;
                for (int x = 0; x < width; x++) {
                    for (int y = 0; y < height; y++) {

                        int argb = image.getRGB(x, y);
                        int alpha = (argb >> 24) & 0xFF;
                        int rgb = argb & 0xFFFFFF;
                        if (rgb == 0)
                            rgb = 1;


                        if (alpha != 0 && alpha != 255)
                            flags |= FLAG_ALPHA;


                        if (!palette.contains(rgb)) {
                            if (palette.size() >= 256)
                                throw new IOException("Too many colours in this sprite!");
                            palette.add(rgb);
                        }
                    }
                }


                os.write(flags);
                for (int x = 0; x < width; x++) {
                    for (int y = 0; y < height; y++) {
                        int argb = image.getRGB(x, y);
                        int alpha = (argb >> 24) & 0xFF;
                        int rgb = argb & 0xFFFFFF;
                        if (rgb == 0)
                            rgb = 1;

                        if ((flags & FLAG_ALPHA) == 0 && alpha == 0) {
                            os.write(0);
                        } else {
                            os.write(palette.indexOf(rgb));
                        }
                    }
                }


                if ((flags & FLAG_ALPHA) != 0) {
                    for (int x = 0; x < width; x++) {
                        for (int y = 0; y < height; y++) {
                            int argb = image.getRGB(x, y);
                            int alpha = (argb >> 24) & 0xFF;
                            os.write(alpha);
                        }
                    }
                }
            }


            for (int i = 1; i < palette.size(); i++) {
                int rgb = palette.get(i);
                os.write((byte) (rgb >> 16));
                os.write((byte) (rgb >> 8));
                os.write((byte) rgb);
            }


            os.writeShort(width);
            os.writeShort(height);
            os.write(palette.size() - 1);


            for (int i = 0; i < frames.length; i++) {
                os.writeShort(0);
                os.writeShort(0);
                os.writeShort(width);
                os.writeShort(height);
            }


            os.writeShort(frames.length);


            byte[] bytes = bout.toByteArray();
            return ByteBuffer.wrap(bytes);
        } finally {
            os.close();
        }
    }

}