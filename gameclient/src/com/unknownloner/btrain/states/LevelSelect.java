package com.unknownloner.btrain.states;

import com.unknownloner.btrain.gl.Texture;
import org.lwjgl.opengl.Display;

import java.io.IOException;

import static org.lwjgl.opengl.GL20.glUniform2f;
import static org.lwjgl.opengl.GL20.glUniformMatrix3;

public class LevelSelect extends GameState {

    Texture texture;

    int select = 1;

    public LevelSelect() throws IOException {
        texture = Texture.load("/textures/space.jpg");
    }

    public void init(){

    }

    public void tick(){

    }

    public void draw(){
        batch.begin();
        glUniform2f(shader.uniformLoc("u_screen_size"), Display.getWidth(), Display.getHeight());

        modelBuf.clear();
        model.store(modelBuf);
        modelBuf.flip();
        glUniformMatrix3(shader.uniformLoc("u_model"), false, modelBuf);
        batch.setColor(1.0f, 1.0f, 1.0f, 1.0f);
        batch.drawTexture(texture, 0, 0, Display.getWidth(), Display.getHeight());

        batch.end();
    }
}