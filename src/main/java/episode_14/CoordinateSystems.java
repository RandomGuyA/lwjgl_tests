/*
 * Copyright (c) 2013, Oskar Veerhoek
 * All rights reserved.
 *
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions are met:
 *
 * 1. Redistributions of source code must retain the above copyright notice, this
 *    list of conditions and the following disclaimer.
 * 2. Redistributions in binary form must reproduce the above copyright notice,
 *    this list of conditions and the following disclaimer in the documentation
 *    and/or other materials provided with the distribution.
 *
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS" AND
 * ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE IMPLIED
 * WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE
 * DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT OWNER OR CONTRIBUTORS BE LIABLE FOR
 * ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES
 * (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES;
 * LOSS OF USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND
 * ON ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT
 * (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF THIS
 * SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 *
 * The views and conclusions contained in the software and documentation are those
 * of the authors and should not be interpreted as representing official policies,
 * either expressed or implied, of the FreeBSD Project.
 */

package episode_14;

import org.lwjgl.LWJGLException;
import org.lwjgl.input.Keyboard;
import org.lwjgl.input.Mouse;
import org.lwjgl.opengl.Display;
import org.lwjgl.opengl.DisplayMode;
import org.newdawn.slick.opengl.Texture;
import org.newdawn.slick.opengl.TextureLoader;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;

import static org.jbox2d.common.MathUtils.clamp;
import static org.lwjgl.opengl.GL11.*;

/**
 * Explains the use of glOrtho
 *
 * @author Oskar
 */
public class CoordinateSystems {

    private static Texture texture;
    private static BufferedImage terrain;

    public static void main(String[] args) {

        try {
            Display.setDisplayMode(new DisplayMode(1200, 900));
            Display.setTitle("Coordinate Systems");
            Display.create();
        } catch (LWJGLException e) {
            e.printStackTrace();
            Display.destroy();
            System.exit(1);
        }

        try {
            texture = TextureLoader.getTexture("PNG", new FileInputStream(new File("res/grass_2_32.png")));
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }

        terrain = loadImage();


        // Initialization code OpenGL
        glMatrixMode(GL_PROJECTION);
        glLoadIdentity();
        glOrtho(0, 1200, 900, 0, 1, -1);
        // glOrtho(-1, 1, -1, 1, -1, 1);
        glMatrixMode(GL_MODELVIEW);
        float translate_x = 0;
        float translate_y = 0;
        float delta = 12.0f;
        float scale = 0.05f;
        float scaleDelta = 0.5f;

        while (!Display.isCloseRequested()) {
            // Render

            //glClearColor(0, 0, 0, 0); // add this
            glClear(GL_COLOR_BUFFER_BIT);


           // glBindTexture(GL_TEXTURE_2D, texture.getTextureID());

            // Put another matrix, a clone of the current one, on the matrix stack.
            glPushMatrix();
            glTranslatef(translate_x, translate_y, 0);
            glScaled(scale, scale, 1);


            int dWheel = Mouse.getDWheel();
            if (dWheel < 0) {
                scale += 0.05f * dWheel;
                scale = clamp(scale, 0.000125f, 4f); //clamp(x,min,max)

            } else if (dWheel > 0){

                scale += 0.01f * dWheel;
                scale = clamp(scale, 0.000125f, 4f); //clamp(x,min,max)

            }


            // Push the screen to the left or to the right, depending on translate_x.
            if (Keyboard.isKeyDown(Keyboard.KEY_W)) {
                translate_y+=delta;
            }
            if (Keyboard.isKeyDown(Keyboard.KEY_S)) {
                translate_y-=delta;
            }
            if (Keyboard.isKeyDown(Keyboard.KEY_A)) {
                translate_x+=delta;
            }
            if (Keyboard.isKeyDown(Keyboard.KEY_D)) {
                translate_x-=delta;
            }

            DrawImage();


            glPopMatrix();

            Display.update();
            Display.sync(60);
        }

        Display.destroy();
        System.exit(0);
    }

    static void DrawImage() {

        glEnable(GL_TEXTURE_2D);
        glTexEnvf(GL_TEXTURE_ENV, GL_TEXTURE_ENV_MODE,  GL_MODULATE);
        glBindTexture(GL_TEXTURE_2D, texture.getTextureID());

        glBegin(GL_QUADS);

        int SIZE = 32;
        int space = 0;

        for (int y = 0; y < terrain.getHeight()-1; y++) {
            for (int x = 0; x < terrain.getWidth()-1; x++) {



                setColor(x,y);

                glTexCoord2f(0, 0);
                glVertex2f((x * SIZE) + space, (y * SIZE) + space); // Upper-left

                setColor(x,y+1);
                // glColor4f(0f, 0.9f, 0.6f, 1);
                glTexCoord2f(0, 1);
                glVertex2f((x * SIZE) + space, (y * SIZE) + SIZE); // Upper-right
                setColor(x+1,y+1);
                //glColor4f(0f, 0.9f, 0.6f, 1);
                glTexCoord2f(1, 1);
                glVertex2f((x * SIZE) + SIZE, (y * SIZE) + SIZE); // Bottom-right
                setColor(x+1,y);
                //glColor4f(0f, 0.9f, 0.6f, 1);
                glTexCoord2f(1, 0);
                glVertex2f((x * SIZE) + SIZE, (y * SIZE) + space); // Bottom-left

            }
        }

        glEnd();

        glDisable(GL_TEXTURE_2D);
        //glPopMatrix();
        /*
        glMatrixMode(GL_PROJECTION);
        glPopMatrix();
        glMatrixMode(GL_MODELVIEW);
        */
    }

    public static void setColor(int x,int y){

        int rgb = terrain.getRGB(x,y);
        int red = (rgb >> 16) & 0xFF;
        int green = (rgb >> 8) & 0xFF;
        int blue = rgb & 0xFF;

        float r = (1.0f/255)*red;
        float g = (1.0f/255)*green;
        float b = (1.0f/255)*blue;

        glColor4f(r, g, b, 1);
    }

    public static BufferedImage loadImage() {

        BufferedImage img = null;
        try {
            img = ImageIO.read(new File("res/terrain_test.png"));
        } catch (IOException e) {
            System.out.println("failed to load asset");
        }

        return img;
    }


}
