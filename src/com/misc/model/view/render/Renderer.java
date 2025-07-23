package com.misc.model.view.render;

import com.misc.model.view.properties.PanelProperties;

import java.awt.*;
import java.awt.image.*;

public final class Renderer implements ImageObserver, ImageProducer {
    private final int width;
    private final int height;
    private final ColorModel colorModel = new DirectColorModel(32, 16711680, '\uff00', 255);
    private final Image generated3DModelImage;
    private final int[] screen;
    private final int[] pixels;
    private ImageConsumer imageProducer;

    public Renderer(int width, int height, Component component) {
        ModelCanvas.setLighting(0.8D);
        //ModelCanvas.method1();
        ModelCanvas.setDimensions(width, height);
        this.screen = ModelCanvas.screen;
        this.width = width;
        this.height = height;
        this.pixels = new int[width * height];
        this.generated3DModelImage = component.createImage(this);
        //this.generatePixels();
        component.prepareImage(this.generated3DModelImage, this);
        this.initScreenProperties();
    }

    private void initScreenProperties() {
        int[] pixels = this.pixels;
        int width = this.width;
        int height = this.height;
        PanelProperties.pixels = pixels;
        PanelProperties.width = width;
        PanelProperties.height = height;
        if (width > PanelProperties.width) {
            width = PanelProperties.width;
        }
        if (height > PanelProperties.height) {
            height = PanelProperties.height;
        }
        PanelProperties.width2 = width;
        PanelProperties.height2 = height;
        PanelProperties.field65 = PanelProperties.width2 - 1;
    }

    public void updatePreview(int var1, int var2, Graphics g) {
        this.generatePixels();
        g.drawImage(this.generated3DModelImage, 0, 0, this);
    }

    public synchronized void addConsumer(ImageConsumer c) {
        this.imageProducer = c;
        c.setDimensions(this.width, this.height);
        c.setProperties(null);
        c.setColorModel(this.colorModel);
        c.setHints(14);
    }

    public synchronized boolean isConsumer(ImageConsumer var1) {
        return this.imageProducer == var1;
    }

    public synchronized void removeConsumer(ImageConsumer var1) {
        if (this.imageProducer == var1) {
            this.imageProducer = null;
        }

    }

    public void startProduction(ImageConsumer var1) {
        this.addConsumer(var1);
    }

    public void requestTopDownLeftRightResend(ImageConsumer var1) {
        System.out.println("TDLR");
    }

    private synchronized void generatePixels() {
        this.imageProducer.setPixels(0, 0, this.width, this.height, this.colorModel, this.pixels, 0, this.width);
        this.imageProducer.imageComplete(2);
    }

    public boolean imageUpdate(Image arg0, int arg1, int arg2, int arg3, int arg4, int arg5) {
        return true;
    }

    public void updateModel(Canvas model, int x, int axisX, int y, int axisY, int axisZ) {
        this.initScreenProperties();
        ModelCanvas.screen = this.screen;
        if (axisY < 0) axisY = 0;
        if (axisX < 0) axisX = 0;
        int var7 = ModelCanvas.field16[axisX] * axisZ >> 16;
        axisZ = ModelCanvas.field17[axisX] * axisZ >> 16;
        if (model != null) {
            model.setViewPoint(0, axisY, 0, axisX, 0, var7, axisZ);
            ModelCanvas.screenPosX = x;
            ModelCanvas.screenPosY = y;
        } else {
            throw new NullPointerException("You cant render a null model");
        }
    }

    public void clear() {
        this.initScreenProperties();
        int screen = PanelProperties.width * PanelProperties.height;
        //set background color
        for (int i = 0; i < screen; ++i) {
            PanelProperties.pixels[i] = 31928;
        }

    }
}
