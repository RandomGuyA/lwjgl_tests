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

package episode_30;

import org.lwjgl.LWJGLException;
import org.lwjgl.input.Keyboard;
import org.lwjgl.input.Mouse;
import org.lwjgl.opengl.Display;
import org.lwjgl.opengl.DisplayMode;
import utility.*;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;

import static org.lwjgl.opengl.GL11.*;
import static org.lwjgl.opengl.GL15.*;
import static org.lwjgl.opengl.GL20.glDeleteProgram;
import static org.lwjgl.opengl.GL20.glUseProgram;

/**
 * Shows what different matrices do in OpenGL.
 *
 * @author Oskar Veerhoek
 */
public class MatricesDemo {

    private static EulerCamera camera;
    private static int shaderProgram;
    private static int vboVertexHandle;
    private static int vboNormalHandle;

    private static final LWJGLTimer timer = new LWJGLTimer();
    private static Model model;

    private static final String MODEL_LOCATION = "res/models/bunny.obj";
    private static final String VERTEX_SHADER_LOCATION = "res/shaders/vertex_phong_lighting.vs";
    private static final String FRAGMENT_SHADER_LOCATION = "res/shaders/vertex_phong_lighting.fs";

    public static void main(String[] args) {
        setUpDisplay();
        setUpVBOs();
        setUpCamera();
        setUpShaders();
        setUpStates();
        timer.initialize();
        while (!Display.isCloseRequested()) {
            render();
            checkInput();
            Display.update();
            Display.sync(60);
        }
        cleanUp();
        System.exit(0);
    }

    private static void checkInput() {
        timer.update();
        camera.processMouse(1, 80, -80);
        camera.processKeyboard((float) timer.getElapsedTime(), 1, 1, 1);
        while (Keyboard.next()) {
            if (Keyboard.getEventKeyState()) {
                switch (Keyboard.getEventKey()) {
                    case Keyboard.KEY_O:
                        camera.applyOrthographicMatrix();
                        break;
                    case Keyboard.KEY_P:
                        camera.applyPerspectiveMatrix();
                        break;
                }
            }
        }
        if (Mouse.isButtonDown(0)) {
            Mouse.setGrabbed(true);
        } else if (Mouse.isButtonDown(1)) {
            Mouse.setGrabbed(false);
        }
    }

    private static void cleanUp() {
        glDeleteProgram(shaderProgram);
        glDeleteBuffers(vboVertexHandle);
        glDeleteBuffers(vboNormalHandle);
        Display.destroy();
    }

    private static void render() {
        glClear(GL_COLOR_BUFFER_BIT | GL_DEPTH_BUFFER_BIT);
        glLoadIdentity();
        camera.applyTranslations();
        glUseProgram(shaderProgram);
        glLight(GL_LIGHT0, GL_POSITION, BufferTools.asFlippedFloatBuffer(camera.x(), camera.y(), camera.z(), 1));
        glBindBuffer(GL_ARRAY_BUFFER, vboVertexHandle);
        glVertexPointer(3, GL_FLOAT, 0, 0L);
        glBindBuffer(GL_ARRAY_BUFFER, vboNormalHandle);
        glNormalPointer(GL_FLOAT, 0, 0L);
        glEnableClientState(GL_VERTEX_ARRAY);
        glEnableClientState(GL_NORMAL_ARRAY);
        glColor3f(0.4f, 0.27f, 0.17f);
        glMaterialf(GL_FRONT, GL_SHININESS, 10f);
        glDrawArrays(GL_TRIANGLES, 0, model.getFaces().size() * 3);
        glDisableClientState(GL_VERTEX_ARRAY);
        glDisableClientState(GL_NORMAL_ARRAY);
        glBindBuffer(GL_ARRAY_BUFFER, 0);
        glUseProgram(0);
    }

    private static void setUpStates() {
        glShadeModel(GL_SMOOTH);
        glEnable(GL_DEPTH_TEST);
        glEnable(GL_LIGHTING);
        glEnable(GL_LIGHT0);
        glLightModel(GL_LIGHT_MODEL_AMBIENT, BufferTools.asFlippedFloatBuffer(new float[]{0.05f, 0.05f, 0.05f, 1f}));
        glLight(GL_LIGHT0, GL_POSITION, BufferTools.asFlippedFloatBuffer(new float[]{0, 0, 0, 1}));
        glEnable(GL_CULL_FACE);
        glCullFace(GL_BACK);
        glEnable(GL_COLOR_MATERIAL);
        glColorMaterial(GL_FRONT, GL_DIFFUSE);
    }

    private static void setUpVBOs() {
        int[] vbos;
        try {
            model = OBJLoader.loadModel(new File(MODEL_LOCATION));
            vbos = OBJLoader.createVBO(model);
            vboVertexHandle = vbos[0];
            vboNormalHandle = vbos[1];
        } catch (FileNotFoundException e) {
            e.printStackTrace();
            cleanUp();
            System.exit(1);
        } catch (IOException e) {
            e.printStackTrace();
            cleanUp();
            System.exit(1);
        }
    }

    private static void setUpShaders() {
        shaderProgram = ShaderLoader.loadShaderPair(VERTEX_SHADER_LOCATION, FRAGMENT_SHADER_LOCATION);
    }

    private static void setUpCamera() {
        camera = new EulerCamera((float) Display.getWidth() / (float) Display.getHeight(), -2.19f, 1.36f, 11.45f);
        camera.setFieldOfView(70);
        camera.applyOptimalStates();
        camera.applyPerspectiveMatrix();
    }

    private static void setUpDisplay() {
        try {
            Display.setDisplayMode(new DisplayMode(1280, 720));
            Display.setVSyncEnabled(true);
            Display.setTitle("Matrices Demo");
            Display.create();
        } catch (LWJGLException e) {
            System.err.println("The display wasn't initialized correctly. :(");
            cleanUp();
            System.exit(1);
        }
    }
}
