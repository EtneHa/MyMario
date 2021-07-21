package jade;

import org.lwjgl.Version;
import org.lwjgl.glfw.GLFWErrorCallback;
import org.lwjgl.opengl.GL;

import static org.lwjgl.glfw.GLFW.*;
import static org.lwjgl.opengl.GL11.*;
import static org.lwjgl.system.MemoryUtil.NULL;

public class Window {
    int width, height;
    String title;
    private long glfwWindow;
    private ImGuiLayer imGuiLayer;

    public float r,b,g,a;
    private boolean fadeToBlack = false;

    private static Window window = null;

    private static Scene currentScene;

    private Window(){
        this.width = 1920;
        this.height = 1080;
        this.title = "Mario";
        r = 1.0f;
        b = 1.0f;
        g = 1.0f;
        a = 1.0f;
    }

    public static Scene getScene() {
        return currentScene;
    }

    public static void changeScene(int newScene){
        switch (newScene){
            case 0:{
                currentScene = new LevelEditorScene();
                currentScene.init();
                currentScene.start();
                break;
            }
            case 1:{
                currentScene = new LevelScene();
                currentScene.init();
                currentScene.start();
                break;
            }
            default:{
                assert false: "Unknown scene '" + newScene + "'";
                break;
            }
        }
    }


    public static Window get(){
        if (Window.window == null){
            Window.window = new Window();
        }
        return Window.window;
    }

    public void run(){
        System.out.println("Hello LWJGL " + Version.getVersion() + " !");

        init();
        loop();

        // Free the window callbacks and destroy the window
        glfwFreeCalbacks(window);
        glfwDestroyWindow(this.glfwWindow);

        // Terminate GLFW and the free the error callback
        glfwTerminate();
        glfwSetErrorCallback(null).set();
    }

    private void glfwFreeCalbacks(Window window) {
    }

    private void init(){
        // Setup an error callback
        GLFWErrorCallback.createPrint(System.err).set();

        // Initialize GLFW
        if (!glfwInit()){
            throw new IllegalStateException("Unable to initialize glfw.");
        }

        // Configure GLFW
        glfwDefaultWindowHints();
        glfwWindowHint(GLFW_VISIBLE, GLFW_FALSE);
        glfwWindowHint(GLFW_RESIZABLE, GLFW_TRUE);
        glfwWindowHint(GLFW_MAXIMIZED, GLFW_TRUE);

        // Create the window
        glfwWindow = glfwCreateWindow(this.width, this.height, this.title, NULL, NULL);
        if (glfwWindow == NULL){
            throw new IllegalStateException("Failed to create the GLFW window");
        }

        // Setup callback here
        glfwSetMouseButtonCallback(glfwWindow, MouseListener::mouseButtonCallback);
        glfwSetCursorPosCallback(glfwWindow, MouseListener::mousePosCallback);
        glfwSetScrollCallback(glfwWindow, MouseListener::mouseScrollCallback);
        glfwSetKeyCallback(glfwWindow, KeyListener::keyCallback);
        glfwSetWindowSizeCallback(glfwWindow, (w, newWidth, newHeight) ->{
            Window.setWidth(newWidth);
            Window.setHeight(newHeight);
        });

        // Make the OpenGL context current
        glfwMakeContextCurrent(glfwWindow);

        // Enable v-sync
        glfwSwapInterval(1);

        // Make the window visible
        glfwShowWindow(glfwWindow);

        GL.createCapabilities();

        // Add Blend func
        glEnable(GL_BLEND);
        glBlendFunc(GL_ONE, GL_ONE_MINUS_SRC_ALPHA);

        // Create ImGui Layer
        this.imGuiLayer = new ImGuiLayer(glfwWindow);
        this.imGuiLayer.initImGui();


        Window.changeScene(0);
    }

    private void loop(){
        float beginTime = (float)glfwGetTime();
        float endTime = (float)glfwGetTime();
        float dt = -1.0f;

        currentScene.load();
        while (!glfwWindowShouldClose(glfwWindow)){
            // Poll Events
            glfwPollEvents();

            glClearColor(r, b, g, a);
            glClear(GL_COLOR_BUFFER_BIT);

            if (dt >=0 ){
                currentScene.update(dt);
            }

            if (KeyListener.isKeyPressed(GLFW_KEY_SPACE)){
                fadeToBlack = true;
            }

            this.imGuiLayer.update(dt, currentScene);


            //if (MouseListener.)

            glfwSwapBuffers(glfwWindow);

            endTime = (float)glfwGetTime();
            dt = endTime - beginTime;
            beginTime = endTime;
        }
        currentScene.saveExit();
    }

    public static int getHeight(){
        return get().height;
    }

    public static int getWidth(){
        return get().width;
    }

    public static void setWidth( int newWidth){
        get().width = newWidth;
    }

    public static void setHeight(int newHeight){
        get().height = newHeight;
    }
}
