package test_examples;


import org.lwjgl.LWJGLException;
import org.lwjgl.input.Keyboard;
import org.lwjgl.input.Mouse;
import org.lwjgl.opengl.Display;
import org.lwjgl.util.glu.GLU;
import org.lwjgl.util.vector.Vector3f;
import utility.EulerCamera;

import static org.lwjgl.opengl.GL11.*;
import static org.lwjgl.opengl.GL20.glDeleteProgram;
import static org.lwjgl.opengl.GL20.glUseProgram;


public class SquareExample {

    private static final String WINDOW_TITLE = "Terrain!";
    private static final int[] WINDOW_DIMENSIONS = {1200, 650};
    private static final float ASPECT_RATIO = (float) WINDOW_DIMENSIONS[0] / (float) WINDOW_DIMENSIONS[1];
    private static final EulerCamera camera = new EulerCamera.Builder().setPosition(1080, 1080,
            1080).setRotation(90, 0, 0).setAspectRatio(ASPECT_RATIO).setFieldOfView(90).setFarClippingPane(2048.0f).build();
    private static final Camera cam = new Camera();
    /**
     * The shader program that will use the lookup texture and the height-map's vertex data to draw the terrain.
     */
    private static int shaderProgram;
    /**
     * The texture that will be used to find out which colours correspond to which heights.
     */
    private static int lookupTexture;
    /**
     * The display list that will contain the height-map's vertex data.
     */
    private static int heightmapDisplayList;
    /**
     * The points of the height. The first dimension represents the z-coordinate. The second dimension represents the
     * x-coordinate. The float value represents the height.
     */
    private static float[][] data;
    /**
     * Whether the terrain should vary in height or be displayed on a grid.
     */
    private static boolean flatten = false;

    private static void render() {
        // Clear the pixels on the screen and clear the contents of the depth buffer (3D contents of the scene)
        glClear(GL_COLOR_BUFFER_BIT | GL_DEPTH_BUFFER_BIT);
        // Reset any translations the camera made last frame update

        //cam.apply();


        int SIZE = 32;
        glBegin(GL_QUADS);
        for (int z = 0; z < SIZE; z++) {
            for (int x = 0; x < SIZE; x++) {

                glColor4f(0.5f, 0.6f, 0.6f, 1);
                glVertex4f((x*32)+2, 0, (z*32)+2, 1);

                glColor4f(0.5f, 0.6f, 0.6f, 1);
                glVertex4f((x*32)+2, 0, (z*32)+32, 1);

                glColor4f(0.5f, 0.6f, 0.6f, 1);
                glVertex4f((x*32)+32, 0, (z*32)+32, 1);

                glColor4f(0.5f, 0.6f, 0.6f, 1);
                glVertex4f((x*32)+32, 0, (z*32)+2, 1);
            }
        }
        glEnd();



    }

    private static void setUpCamera() {
  /*      glMatrixMode(GL_PROJECTION);
        glLoadIdentity();
        glOrtho(0, 1024, 1024, 0, 1, -1);
        glMatrixMode(GL_MODELVIEW);
*/
        glMatrixMode(GL_PROJECTION);
        glLoadIdentity();
        //glOrtho(0, 640, 480, 0, 1, -1);
        glOrtho(-1, 1, -1, 1, -1, 1);
        glMatrixMode(GL_MODELVIEW);

       /* cam.create();
        cam.setPos( new Vector3f(32, 1000, 32));
        cam.setRotation(new Vector3f(90, 0, 0));
        */
    }

    private static void input() {

        //cam.acceptInput(1.0f);

        /*
        while (Keyboard.next()) {
            if (Keyboard.getEventKeyState()) {
                if (Keyboard.isKeyDown(Keyboard.KEY_F)) {
                    flatten = !flatten;
                }
                if (Keyboard.getEventKey() == Keyboard.KEY_L) {
                    // Reload the shaders and the heightmap data.
                    glUseProgram(0);
                    glDeleteProgram(shaderProgram);
                    glBindTexture(GL_TEXTURE_2D, 0);
                    glDeleteTextures(lookupTexture);

                }
                if (Keyboard.getEventKey() == Keyboard.KEY_P) {
                    // Switch between normal mode, point mode, and wire-frame mode.
                    int polygonMode = glGetInteger(GL_POLYGON_MODE);
                    if (polygonMode == GL_LINE) {
                        glPolygonMode(GL_FRONT, GL_FILL);
                    } else if (polygonMode == GL_FILL) {
                        glPolygonMode(GL_FRONT, GL_POINT);
                    } else if (polygonMode == GL_POINT) {
                        glPolygonMode(GL_FRONT, GL_LINE);
                    }
                }
            }
        }
        if (Mouse.isButtonDown(0)) {
            Mouse.setGrabbed(true);
        } else if (Mouse.isButtonDown(1)) {
            Mouse.setGrabbed(false);
        }
        if (Mouse.isGrabbed()) {
            camera.processMouse(1, 80, -80);
        }
        camera.processKeyboard(16, 1);
        */
    }


    private static void cleanUp(boolean asCrash) {
        glUseProgram(0);
        glDeleteProgram(shaderProgram);
        glDeleteLists(heightmapDisplayList, 1);
        glBindTexture(GL_TEXTURE_2D, 0);
        glDeleteTextures(lookupTexture);
        System.err.println(GLU.gluErrorString(glGetError()));
        Display.destroy();
        System.exit(asCrash ? 1 : 0);
    }

    private static void setUpMatrices() {
        //camera.applyPerspectiveMatrix();
        //camera.applyOrthographicMatrix();
    }

    private static void setUpStates() {
        camera.applyOptimalStates();
        glPointSize(2);
        // Enable the sorting of shapes from far to near
        glEnable(GL_DEPTH_TEST);
        // Set the background to a blue sky colour
        glClearColor(0, 0.75f, 1, 1);
        // Remove the back (bottom) faces of shapes for performance
        glEnable(GL_CULL_FACE);
    }

    private static void update() {
        Display.update();
        Display.sync(60);
    }

    private static void enterGameLoop() {
        while (!Display.isCloseRequested()) {
            render();
            input();
            update();
        }
    }

    private static void setUpDisplay() {
        try {
            Display.setDisplayMode(new org.lwjgl.opengl.DisplayMode(WINDOW_DIMENSIONS[0], WINDOW_DIMENSIONS[1]));
            Display.setVSyncEnabled(true);
            Display.setTitle(WINDOW_TITLE);
            Display.create();
        } catch (LWJGLException e) {
            e.printStackTrace();
            cleanUp(true);
        }
    }

    public static void main(String[] args) {
        setUpDisplay();
        setUpStates();
        //setUpMatrices();
        setUpCamera();
        enterGameLoop();
        cleanUp(false);
    }
}
