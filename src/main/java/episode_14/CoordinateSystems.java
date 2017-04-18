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
            Display.setDisplayMode(new DisplayMode(1368, 979));
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
        glOrtho(0, 1368, 979, 0, 1, -1);
        // glOrtho(-1, 1, -1, 1, -1, 1);
        glMatrixMode(GL_MODELVIEW);

        while (!Display.isCloseRequested()) {
            // Render

            glClearColor(0, 0, 0, 0); // add this
            glClear(GL_COLOR_BUFFER_BIT | GL_DEPTH_BUFFER_BIT);


           // glBindTexture(GL_TEXTURE_2D, texture.getTextureID());

            // Put another matrix, a clone of the current one, on the matrix stack.
            glPushMatrix();

            // Push the screen to the left or to the right, depending on translate_x.
            glTranslatef(0, 0, 0);

            // Do some OpenGL rendering (code from SimpleOGLRenderer.java).
            /*
            glBegin(GL_QUADS);

            int SIZE = 32;
            int space = 4;

            for (int y = 0; y < SIZE; y++) {
                for (int x = 0; x < SIZE; x++) {

                    glEnable(GL_TEXTURE_2D);
                    glTexEnvf(GL_TEXTURE_ENV, GL_TEXTURE_ENV_MODE,  GL_MODULATE);
                    glBindTexture(GL_TEXTURE_2D, texture.getTextureID());

                    glColor4f(0f, 0.9f, 0.2f, 1);

                    glTexCoord2f((x * SIZE) + space, (y * SIZE) + space);
                    glVertex2f((x * SIZE) + space, (y * SIZE) + space); // Upper-left

                   // glColor4f(0f, 0.9f, 0.6f, 1);
                    glTexCoord2f((x * SIZE) + space, (y * SIZE) + SIZE);
                    glVertex2f((x * SIZE) + space, (y * SIZE) + SIZE); // Upper-right

                    //glColor4f(0f, 0.9f, 0.6f, 1);
                    glTexCoord2f((x * SIZE) + SIZE, (y * SIZE) + SIZE);
                    glVertex2f((x * SIZE) + SIZE, (y * SIZE) + SIZE); // Bottom-right

                    //glColor4f(0f, 0.9f, 0.6f, 1);
                    glTexCoord2f((x * SIZE) + SIZE, (y * SIZE) + space);
                    glVertex2f((x * SIZE) + SIZE, (y * SIZE) + space); // Bottom-left

                }
            }
            glEnd();
            glFlush();
            // Dispose of the translations on the matrix.
            glPopMatrix();
            */
            DrawImage();

            Display.update();
            Display.sync(60);
        }

        Display.destroy();
        System.exit(0);
    }

    static void DrawImage() {

        glMatrixMode(GL_PROJECTION);
        glPushMatrix();
        glLoadIdentity();
        glOrtho(0.0, 1368, 0.0, 979, -1.0, 1.0);
        glMatrixMode(GL_MODELVIEW);
        glPushMatrix();


        glLoadIdentity();
        glDisable(GL_LIGHTING);


        glColor3f(1,1,1);
        glEnable(GL_TEXTURE_2D);
        glBindTexture(GL_TEXTURE_2D, texture.getTextureID());

        glColor3f(0,0.8f,0.2f);
        // Draw a textured quad
        glBegin(GL_QUADS);

        int SIZE = 32;
        int space = 0;

        for (int y = 0; y < terrain.getHeight(); y++) {
            for (int x = 0; x < terrain.getWidth(); x++) {

                glEnable(GL_TEXTURE_2D);
                glTexEnvf(GL_TEXTURE_ENV, GL_TEXTURE_ENV_MODE,  GL_MODULATE);
                glBindTexture(GL_TEXTURE_2D, texture.getTextureID());

                int rgb = terrain.getRGB(x,y);
                int red = (rgb >> 16) & 0xFF;
                int green = (rgb >> 8) & 0xFF;
                int blue = rgb & 0xFF;

                float r = (1.0f/255)*red;
                float g = (1.0f/255)*green;
                float b = (1.0f/255)*blue;

                glColor4f(r, g, b, 1);

                glTexCoord2f(0, 0);
                glVertex2f((x * SIZE) + space, (y * SIZE) + space); // Upper-left

                // glColor4f(0f, 0.9f, 0.6f, 1);
                glTexCoord2f(0, 1);
                glVertex2f((x * SIZE) + space, (y * SIZE) + SIZE); // Upper-right

                //glColor4f(0f, 0.9f, 0.6f, 1);
                glTexCoord2f(1, 1);
                glVertex2f((x * SIZE) + SIZE, (y * SIZE) + SIZE); // Bottom-right

                //glColor4f(0f, 0.9f, 0.6f, 1);
                glTexCoord2f(1, 0);
                glVertex2f((x * SIZE) + SIZE, (y * SIZE) + space); // Bottom-left

            }
        }

        glEnd();


        glDisable(GL_TEXTURE_2D);
        glPopMatrix();


        glMatrixMode(GL_PROJECTION);
        glPopMatrix();

        glMatrixMode(GL_MODELVIEW);

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
