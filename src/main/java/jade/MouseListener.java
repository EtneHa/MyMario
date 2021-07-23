package jade;

import org.joml.Vector4f;
import org.lwjgl.glfw.GLFW;
import org.lwjgl.system.CallbackI;

import static org.lwjgl.glfw.GLFW.*;

public class MouseListener {
    private static MouseListener instance;
    private double scrollX, scrollY;
    private double xPos, yPos, lastX, lastY;
    private boolean mouseButtonPressed[] = new boolean[9];
    private boolean isDragging;

    private MouseListener(){
        this.scrollX = 0.0;
        this.scrollY = 0.0;
        this.xPos = 0.0;
        this.yPos = 0.0;
        this.lastX = 0.0;
        this.lastY = 0.0;
    }

    public static MouseListener get(){
        if (MouseListener.instance == null)
            MouseListener.instance = new MouseListener();

        return MouseListener.instance;
    }

    public static void mousePosCallback(long window, double xpos, double ypos){
        get().lastX = get().xPos;
        get().lastY = get().yPos;
        get().xPos = xpos;
        get().yPos = ypos;
        get().isDragging = get().mouseButtonPressed[0] || get().mouseButtonPressed[1] || get().mouseButtonPressed[2];
    }

    public static void mouseButtonCallback(long Window, int button, int act, int mods) {
        if (act == GLFW_PRESS) {
            if (button < get().mouseButtonPressed.length)
                get().mouseButtonPressed[button] = true;
        } else if (act == GLFW_RELEASE) {
            get().mouseButtonPressed[button] = false;
            get().isDragging = false;
        }
    }

    public static void mouseScrollCallback(long Window, double xOffset, double yOffset){
        get().scrollY = yOffset;
        get().scrollX = xOffset;
    }

    public static void endFrame(long window){
        get().scrollX = 0;
        get().scrollY = 0;
        get().lastX = get().xPos;
        get().lastY = get().yPos;
        get().xPos = 0;
        get().yPos = 0;
    }


    public static MouseListener getInstance() {
        return instance;
    }

    public double getScrollX() {
        return scrollX;
    }

    public double getScrollY() {
        return scrollY;
    }

    public double getxPos() {
        return xPos;
    }

    public double getyPos() {
        return yPos;
    }

    public static float getOrthoX(){
        float currentX = (float)get().getxPos();

        currentX = (currentX / (float) Window.getWidth())  * 2.0f - 1.0f;
        Vector4f tmp = new Vector4f(currentX, 0,0,1);
        tmp.mul(Window.getScene().getCamera().getInverseProjectionMatrix()).mul(Window.getScene().getCamera().getInverseViewMatrix());
        currentX = tmp.x;
        return currentX;
    }

    public static float getOrthoY(){
        float currentY = Window.getHeight() - (float)get().getyPos();

        currentY = (currentY / (float) Window.getHeight()) * 2 - 1.0f;
        Vector4f tmp = new Vector4f(0, currentY,0,1);
        tmp.mul(Window.getScene().getCamera().getInverseProjectionMatrix()).mul(Window.getScene().getCamera().getInverseViewMatrix());
        currentY = tmp.y;
        return currentY;
    }

    public double getLastX() {
        return lastX;
    }

    public double getLastY() {
        return lastY;
    }

    public boolean[] getMouseButtonPressed() {
        return mouseButtonPressed;
    }

    public boolean isDragging() {
        return isDragging;
    }

    public static boolean mouseButtonDown(int button){
        if (button < get().mouseButtonPressed.length){
            return get().mouseButtonPressed[button];
        }else
            return false;
    }
}
