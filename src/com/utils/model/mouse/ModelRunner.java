package com.utils.model.mouse;


import com.utils.model.Main;
import com.utils.model.render.Canvas;

public final class ModelRunner implements Runnable {
    public void run() {
        Main main;
        (main = new Main()).setTitle("");
        main.loadFiles();
        main.setVisible(true);
        main.render();
        (new Thread(String.valueOf(main))).start();
        Canvas.load();
    }
}
