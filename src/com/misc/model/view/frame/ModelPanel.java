package com.misc.model.view.frame;

import com.misc.model.view.render.Canvas;
import com.misc.model.view.render.Renderer;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.io.BufferedInputStream;
import java.io.DataInputStream;
import java.io.File;
import java.io.FileInputStream;

public class ModelPanel extends JPanel implements Runnable {
    public static boolean isOptimizedSelected = false;
    private static long gcDelay = 0L;
    public boolean loaded = false;
    private final int oriPosX = 120;
    private final int oriPosY = 100;
    private int posX = oriPosX;
    private int posY = oriPosY;
    private int axisX = 0;
    private int axisY = 0;
    private int axisZ = 500;
    private int screenX = -1;
    private int screenY = -1;
    private Canvas model;
    private Renderer renderer;

    public ModelPanel() {
        setBackground(new Color(0, 0, 0));
        addMouseWheelListener(new MouseWheelListener() {
            public void mouseWheelMoved(MouseWheelEvent e) {
                mouseWheelMovedCallback(e);
            }
        });
        addMouseListener(new MouseAdapter() {
            public void mouseReleased(MouseEvent e) {
                mouseReleasedCallback(e);
            }
        });
        addComponentListener(new ComponentAdapter() {
            @Override
            public void componentResized(ComponentEvent c) {
                componentResizedCallback(c);
            }
        });
        addMouseMotionListener(new MouseMotionAdapter() {
            public void mouseDragged(MouseEvent e) {
                mouseDraggedCallback(e);
            }
        });
        GroupLayout layout = new GroupLayout(this);
        setLayout(layout);
        layout.setHorizontalGroup(layout.createParallelGroup(GroupLayout.Alignment.LEADING).addGap(0, 536, 32767));
        layout.setVerticalGroup(layout.createParallelGroup(GroupLayout.Alignment.LEADING).addGap(0, 318, 32767));
    }

    @Override
    public void run() {
        Graphics g = null;
        while (true) {
            g = getGraphics();
            if (model != null && g != null) {
                try {
                    renderer.updateModel(model, posX, axisY, posY, axisX, axisZ);
                    renderer.updatePreview(0, 0, g);
                    renderer.clear();
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }


            try {
                Thread.sleep(10L);
            } catch (InterruptedException e) {
            }

            if (System.currentTimeMillis() - gcDelay > 60000L) {//garbage collect every minute
                System.gc();
                gcDelay = System.currentTimeMillis();
            }
        }
    }

    private byte[] getBytesFromFile(String fileName) {
        try {
            int length;
            byte[] bytes = new byte[length = (int) (new File(fileName)).length()];
            DataInputStream in;
            (in = new DataInputStream(new BufferedInputStream(new FileInputStream(fileName))))
                    .readFully(bytes, 0, length);
            in.close();
            return bytes;
        } catch (Exception e) {
            return null;
        }
    }

    public void loadModelFromFile(String fileName) {
        loadModelFromBytes(getBytesFromFile(fileName));
    }

    public void loadModelFromBytes(byte[] bytes) {
        Canvas model;
        if (bytes != null && bytes[bytes.length - 1] == -1 && bytes[bytes.length - 2] == -1) {
            if ((model = Canvas.loadModel(bytes)).isNewHeader) {
                model.downScaleModel(4);
            }
        } else {
            Canvas.initModel(bytes);
            model = Canvas.createEmptyModel();
        }
        model.method38(64, 768, -50, -10, -50, true);
        this.model = model;
    }

    public final void render() {
        renderer = new Renderer(getWidth(), getHeight(), this);
    }

    public void mouseWheelMovedCallback(MouseWheelEvent var1) {
        int rotation = var1.getWheelRotation();
        axisZ += rotation * 50;
    }

    public void mouseReleasedCallback(MouseEvent event) {
        screenY = screenX = -1;
        int mouseType = event.getButton();
        if (mouseType == 3) {
            posX = oriPosX;
            posY = oriPosY;
            axisX = 0;
            axisY = 0;
            axisZ = 500;
        }
    }

    public void componentResizedCallback(ComponentEvent c) {
        if (getWidth() > 0 && getHeight() > 0) {
            render();
        }
    }

    public void mouseDraggedCallback(MouseEvent event) {
        int x = event.getX();
        int y = event.getY();
        @SuppressWarnings("static-access")
        String button = event.getModifiersExText(event.getModifiersEx());
        int mouseType = Character.getNumericValue(button.charAt(button.length() - 1));
        if (mouseType == 1) {
            if (screenX != -1 || screenY != -1) {
                axisX += (screenX - x) * 3;
                if (axisX < 0) {
                    axisX += 2048;
                } else if (axisX >= 2048) {
                    axisX = 2048 - axisX;
                }

                axisY -= (screenY - y) * 3;
                if (axisY < 0) {
                    axisY += 2048;
                } else if (axisY >= 2048) {
                    axisY = 2048 - axisY;
                }
            }
        } else if (mouseType == 2) {
            if (screenX != -1 || screenY != -1) {
                posX += x - screenX;
                if (posX < 0) {
                    posX += 2048;
                } else if (posX >= 2048) {
                    posX = 2048 - posX;
                }

                posY -= screenY - y;
                if (posY < 0) {
                    posY += 2048;
                } else if (posY >= 2048) {
                    posY = 2048 - posY;
                }
            }
        }

        screenX = x;
        screenY = y;
    }
}
