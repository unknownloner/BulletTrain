package com.unknownloner.btrain;

import com.unknownloner.btrain.gfx.LevelRenderer;
import com.unknownloner.btrain.logic.Level;
import org.lwjgl.LWJGLException;
import org.lwjgl.input.Keyboard;
import org.lwjgl.opengl.Display;
import org.lwjgl.opengl.DisplayMode;
import org.lwjgl.opengl.PixelFormat;

import java.io.File;
import static org.lwjgl.opengl.GL11.*;

public class BulletTrain {

    static boolean isFullscreen;

    public static void main(String[] args) throws Exception {
        for (String str : args) {
            if (str.equalsIgnoreCase("--fullscreen")) {
                isFullscreen = true;
            }
        }
        initDisplay();
        Level level = new Level("TestLevel", "/textures/space.jpg");
        LevelRenderer renderer = new LevelRenderer(level);

        while (!Display.isCloseRequested()) {
            if (Keyboard.isKeyDown(Keyboard.KEY_ESCAPE)) {
                break;
            }


            glClearColor(0.0f, 0.0f, 0.0f, 1.0f);
            glClear(GL_COLOR_BUFFER_BIT | GL_DEPTH_BUFFER_BIT);
            renderer.render();
            Display.update();
            Display.sync(120);
        }
        Display.destroy();
    }

    public static void initDisplay() throws LWJGLException {
        System.setProperty("org.lwjgl.librarypath", nativesPath());
        if (isFullscreen) {
            Display.setFullscreen(true);
        } else {
            Display.setDisplayMode(new DisplayMode(854, 480));
        }
        Display.setTitle("Bullet Train");
        Display.create(new PixelFormat().withDepthBits(24));
    }

    public static String nativesPath() {
        String os = System.getProperty("os.name").toLowerCase();
        String suffix = "";
        if (os.contains("win")) {
            suffix = "windows";
        } else if (os.contains("mac")) {
            suffix = "macosx";
        } else {
            suffix = "linux";
        }
        return System.getProperty("user.dir") + File.separator + "native" + File.separator + suffix;
    }

}
