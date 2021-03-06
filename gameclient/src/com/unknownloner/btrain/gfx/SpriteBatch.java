package com.unknownloner.btrain.gfx;

import com.unknownloner.btrain.Util;
import com.unknownloner.btrain.gl.Shader;
import com.unknownloner.btrain.gl.Texture;
import org.lwjgl.BufferUtils;

import java.io.IOException;
import java.nio.FloatBuffer;

import static org.lwjgl.opengl.GL11.*;
import static org.lwjgl.opengl.GL20.*;
import static org.lwjgl.opengl.GL15.*;
import static org.lwjgl.opengl.GL30.*;

public class SpriteBatch {

    private Texture curTex;
    private Shader curShader;

    private int glBuf;
    private FloatBuffer verts = BufferUtils.createFloatBuffer(65536);

    private boolean isBatching = false;

    private float r, g, b, a;

    public SpriteBatch() {
        glBuf = glGenBuffers();
        r = 1f;
        g = 1f;
        b = 1f;
        a = 1f;
        try {
            curShader = new Shader(Util.readText("/shaders/sprites.vert"), Util.readText("/shaders/sprites.frag"), "Spritebatch Shader");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void setTexture(Texture tex) {
        if (!isBatching) {
            curTex = tex;
            return;
        }

        if (tex != curTex) {
            flush();
        }
        curTex = tex;
        tex.bind();
    }

    public void setShader(Shader shader) {
        if (!isBatching) {
            curShader = shader;
            return;
        }

        if (curShader != shader) {
            flush();
        }
        curShader = shader;
        shader.use();
    }

    public void setColor(float r, float g, float b, float a) {
        if (!isBatching) {
            this.r = r;
            this.g = g;
            this.b = b;
            this.a = a;
            return;
        }
        if (r != this.r || g != this.g || b != this.b || a != this.a) {
            flush();
            this.r = r;
            this.g = g;
            this.b = b;
            this.a = a;
            glUniform4f(curShader.uniformLoc("u_color"), r, g, b, a);
        }
    }

    public Texture getTexture() {
        return curTex;
    }

    public Shader getShader() {
        return curShader;
    }

    public float getRed() {
        return r;
    }

    public float getGreen() {
        return g;
    }

    public float getBlue() {
        return b;
    }

    public float getAlpha() {
        return a;
    }

    public void begin() {
        if (isBatching) {
            throw new RuntimeException("Tried to start sprite batching while sprite batching");
        }
        isBatching = true;
        if (curTex != null)
            curTex.bind();
        curShader.use();
        glBindBuffer(GL_ARRAY_BUFFER, glBuf);
        glUniform4f(curShader.uniformLoc("u_color"), r, g, b, a);

        glEnableVertexAttribArray(0);
        glEnableVertexAttribArray(1);
    }

    public void end() {
        if (!isBatching) {
            throw new RuntimeException("Ended sprite batching but was not sprite batching");
        }
        flush();
        isBatching = false;

        glDisableVertexAttribArray(0);
        glDisableVertexAttribArray(1);
    }

    public void flush() {
        verts.flip();
        if (verts.limit() == 0) {
            verts.clear();
            return;
        }
        glBufferData(GL_ARRAY_BUFFER, verts, GL_STREAM_DRAW);

        glVertexAttribPointer(0, 2, GL_FLOAT, false, 4 * 4, 0);
        glVertexAttribPointer(1, 2, GL_FLOAT, false, 4 * 4, 2 * 4);

        glDrawArrays(GL_TRIANGLES, 0, verts.limit() / 4);
        verts.clear();
    }

    //This stuff is the interesting part
    public void addVert(double x, double y, double u, double v) {
        verts.put((float)x).put((float)y).put((float)u).put((float)v);
    }

    /**
     * Draws a texture at (x,y)
     * @param tex
     * @param x
     * @param y
     */
    public void drawTexture(Texture tex, double x, double y) {
        drawTexture(tex, x, y, tex.width, tex.height);
    }

    /**
     * Draws a texture at (x,y) scaled to the given size
     * @param tex
     * @param x
     * @param y
     * @param w
     * @param h
     */
    public void drawTexture(Texture tex, double x, double y, double w, double h) {
        setTexture(tex);
        addVert(x + w, y + h, 1.0, 0.0);
        addVert(x + 0, y + h, 0.0, 0.0);
        addVert(x + 0, y + 0, 0.0, 1.0);

        addVert(x + 0, y + 0, 0.0, 1.0);
        addVert(x + w, y + 0, 1.0, 1.0);
        addVert(x + w, y + h, 1.0, 0.0);
    }

    /**
     * Draws a texture at (x,y) with size of width,height.
     * @param tex
     * @param x
     * @param y
     * @param w
     * @param h
     * @param u0 U coordinate of top left corner
     * @param v0 V coordinate of top left corner
     * @param u1 U coordinate of bottom right corner
     * @param v1 V coordinate of bottom right corner
     */
    public void drawTextureUV(Texture tex, double x, double y, double w, double h, double u0, double v0, double u1, double v1) {
        setTexture(tex);
        addVert(x + w, y + h, u1, v0);
        addVert(x + 0, y + h, u0, v0);
        addVert(x + 0, y + 0, u0, v1);

        addVert(x + 0, y + 0, u0, v1);
        addVert(x + w, y + 0, u1, v1);
        addVert(x + w, y + h, u1, v0);
    }

    public void drawTextureRegion(Texture tex, double x, double y, int tx, int ty, int tw, int th) {
        drawTextureRegion(tex, x, y, tw, th, tx, ty, tw, th);
    }

    public void drawTextureRegion(Texture tex, double x, double y, double w, double h, int tx, int ty, int tw, int th) {
        double u0 = tx / (double)tex.width;
        double v0 = ty / (double)tex.height;
        double u1 = u0 + tw / (double)tex.width;
        double v1 = v0 + th / (double)tex.height;
        drawTextureUV(tex, x, y, w, h, u0, v0, u1, v1);
    }

    public void drawChar(char c, double x, double y, int scale) {
        int tx = (c % 16) * 6;
        int ty = (c / 16) * 8;
        drawTextureRegion(fontTex, x, y, 6 * scale, 8 * scale, tx, ty, 6, 8);
    }

    public void drawString(String s, double x, double y, int scale) {
        int len = s.length();
        for (int i = 0; i < len; i++) {
            drawChar(s.charAt(i), x + i * 6 * scale, y, scale);
        }
    }

    //Load font texture
    private static Texture fontTex;
    static {
        try {
            fontTex = Texture.load("/textures/font.png");
            glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_MAG_FILTER, GL_NEAREST);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }


}
