package com.company;

import org.lwjgl.BufferUtils;
import org.lwjgl.glfw.*;
import org.lwjgl.opengl.GL;
import org.lwjgl.system.MemoryStack;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.nio.IntBuffer;
import java.util.Arrays;

import static org.lwjgl.glfw.Callbacks.glfwFreeCallbacks;
import static org.lwjgl.glfw.GLFW.*;
import static org.lwjgl.opengl.GL11.*;
import static org.lwjgl.opengl.GL20.*;
import static org.lwjgl.system.MemoryStack.stackPush;
import static org.lwjgl.system.MemoryUtil.*;

public class Window {
    private int width, height;
    private String title;
    private long window;
    private float mouseWheelVelocity;
    private float minX = -2;
    private float domain = 3;
    private float minY = -1;
    private float range = 2;
    private float limit = 100;
    private float rMod = 1;
    private float gMod = 1;
    private float bMod = 1;
    private float br = 0;
    private boolean isDown = false;
    private boolean shift = false;
    private double lastX = 1000000.0;
    private double lastY = 1000000.0;
    private double shiftX = 0;
    private double shiftY = 0;
    private float zoom = 1;
    private float gradient = 0.007f;
    private int theme2 = 0;//0 for false 1 for true
    private int theme3 = 0;//0 for false 1 for true
    private int julia = 0;
    private int inv = 0;
    private int rainbow = 0;
    private int trip = 0;
    private float cx = 0;
    private float cy = 0;

    public Window(int width, int height, String title){
        this.width = width;
        this.height = height;
        this.title = title;
    }

    public void create(){
        if(!GLFW.glfwInit()){
            System.err.println("Error: Couldn't initialize GLFW");
            System.exit(-1);
        }
        // Configure GLFW
        glfwWindowHint(GLFW_VISIBLE, GLFW_FALSE); // the window will stay hidden after creation
        glfwWindowHint(GLFW_RESIZABLE, GLFW_TRUE); // the window will be resizable

        window = GLFW.glfwCreateWindow(width, height, title, glfwGetPrimaryMonitor(), NULL);

        if(window == 0){
            System.err.println("Error: Window couldn't be created.");
            System.exit(-1);
        }

        run();

    }

    public boolean closed() {
        return GLFW.glfwWindowShouldClose(window);
    }

    public void scroll(float dy){
        limit += dy;
        zoom = Math.signum(dy);
    }

    public void run() {
        init();
        loop();

        // Free the window callbacks and destroy the window
        destroy();
    }

    public void destroy(){
        glfwFreeCallbacks(window);
        glfwDestroyWindow(window);

        // Terminate GLFW and free the error callback
        glfwTerminate();
        glfwSetErrorCallback(null).free();
    }

    private void init() {
        // Setup an error callback. The default implementation
        // will print the error message in System.err.
        GLFWErrorCallback.createPrint(System.err).set();

        // Initialize GLFW. Most GLFW functions will not work before doing this.
        if ( !glfwInit() )
            throw new IllegalStateException("Unable to initialize GLFW");

        if ( window == NULL )
            throw new RuntimeException("Failed to create the GLFW window");

        // Setup a key callback. It will be called every time a key is pressed, repeated or released.
        glfwSetKeyCallback(window, (window, key, scancode, action, mods) -> {
            if ( key == GLFW_KEY_ESCAPE && action == GLFW_RELEASE )
                glfwSetWindowShouldClose(window, true); // We will detect this in the rendering loop
            if ( key == GLFW_KEY_A && action == GLFW_PRESS){
                theme3 = 0;
                if(theme2 == 0) theme2 = 1;
                else theme2 = 0;
            }
            if ( key == GLFW_KEY_S && action == GLFW_PRESS){
                theme2 = 0;
                if(theme3 == 0) theme3 = 1;
                else theme3 = 0;
            }

            if ( key == GLFW_KEY_ENTER && action == GLFW_RELEASE ) {
                theme2 = 0;
                theme3 = 0;
                minX = -2;
                domain = 3;
                minY = -1;
                range = 2;
                limit = 100;
                rMod = 1;
                gMod = 1;
                bMod = 1;
                br = 0;
            }
            if ( key == GLFW_KEY_LEFT_SHIFT){
                if(action == GLFW_PRESS) {
                    shift = true;
                } else if(action == GLFW_RELEASE){
                    shift = false;
                }
            }
            if ( key == GLFW_KEY_I && action == GLFW_PRESS){
                if(inv == 0) inv =1;
                else inv = 0;
            }
            if ( key == GLFW_KEY_TAB && action == GLFW_PRESS){
                if(julia == 0) julia =1;
                else julia = 0;
            }
            if ( key == GLFW_KEY_R && action == GLFW_PRESS){
                if(rainbow == 0) rainbow = 1;
                else rainbow = 0;
            }
            if ( key == GLFW_KEY_T && action == GLFW_PRESS){
                if(trip == 0) trip = 1;
                else trip = 0;
            }
            if ( key == GLFW_KEY_LEFT_ALT && action == GLFW_PRESS) {
                br -= 0.1;
            }
            if ( key == GLFW_KEY_RIGHT_ALT && action == GLFW_PRESS) {
                br += 0.1;
            }
            if ( key == GLFW_KEY_Z && action == GLFW_REPEAT){
                rMod -= gradient;
            }
            if ( key == GLFW_KEY_X && action == GLFW_REPEAT){
                rMod += gradient;
            }
            if ( key == GLFW_KEY_C && action == GLFW_REPEAT){
                gMod -= gradient;
            }
            if ( key == GLFW_KEY_V && action == GLFW_REPEAT){
                gMod += gradient;
            }
            if ( key == GLFW_KEY_B && action == GLFW_REPEAT){
                bMod -= gradient;
            }
            if ( key == GLFW_KEY_N && action == GLFW_REPEAT){
                bMod += gradient;
            }
            if ( key == GLFW_KEY_1 && action == GLFW_REPEAT){
                limit -= 10;
            }
            if ( key == GLFW_KEY_2 && action == GLFW_REPEAT){
                limit += 10;
            }
        });
        GLFW.glfwSetMouseButtonCallback(window, new GLFWMouseButtonCallback() {
            @Override
            public void invoke(long window, int button, int action, int mods) {
                if (button == GLFW_MOUSE_BUTTON_LEFT){
                    if(action == GLFW_PRESS){
                        isDown = true;
                    } else
                    if(action == GLFW_RELEASE){
                        lastX = 1000000.0;
                        lastY = 1000000.0;
                        isDown = false;
                    }
                }
            }
        });

        GLFW.glfwSetCursorPosCallback(window, new GLFWCursorPosCallback() {
            @Override
            public void invoke(long window, double xpos, double ypos) {
                if(isDown){
                    if(lastX == 1000000.0 && lastY == 1000000.0){
                        lastX = xpos;
                        lastY = ypos;
                    } else{
                        shiftX = lastX - xpos;
                        shiftY = lastY - ypos;
                        lastX = xpos;
                        lastY = ypos;
                        minX+=shiftX/1920*domain;
                        minY-=shiftY/1080*range;
                    }
                }
                if(shift){
                    cx = (float)xpos;
                    cy = (float)ypos;
                }
            }
        });

        GLFW.glfwSetScrollCallback(window, new GLFWScrollCallback() {
            @Override public void invoke (long win, double dx, double dy) {
                mouseWheelVelocity = (float) dy;
                scroll(mouseWheelVelocity);
                minX += 0.05 * domain * zoom;
                minY += 0.05 * range  * zoom;
                domain -= 0.1 * domain * zoom;
                range  -= 0.1 * range  * zoom;
            }
        });

        // Get the thread stack and push a new frame
        try ( MemoryStack stack = stackPush() ) {
            IntBuffer pWidth = stack.mallocInt(1); // int*
            IntBuffer pHeight = stack.mallocInt(1); // int*

            // Get the window size passed to glfwCreateWindow
            glfwGetWindowSize(window, pWidth, pHeight);

            // Get the resolution of the primary monitor
            GLFWVidMode vidmode = glfwGetVideoMode(glfwGetPrimaryMonitor());

            // Center the window
            glfwSetWindowPos(
                    window,
                    (vidmode.width() - pWidth.get(0)) / 2,
                    (vidmode.height() - pHeight.get(0)) / 2
            );
        } // the stack frame is popped automatically

        // Make the OpenGL context current
        glfwMakeContextCurrent(window);
        // Enable v-sync
        glfwSwapInterval(1);

        // Make the window visible
        glfwShowWindow(window);
    }

    private void loop() {
        // This line is critical for LWJGL's interoperation with GLFW's
        // OpenGL context, or any context that is managed externally.
        // LWJGL detects the context that is current in the current thread,
        // creates the GLCapabilities instance and makes the OpenGL
        // bindings available for use.
        GL.createCapabilities();

        // Set the clear color
        glClearColor(0.0f, 0.0f, 0.0f, 0.0f);

        // Poll for window events. The key callback above will only be
        // invoked during this call.
        int shaderProgram = glCreateProgram();
        int vertexShader = glCreateShader(GL_VERTEX_SHADER);
        int fragmentShader = glCreateShader(GL_FRAGMENT_SHADER);
        StringBuilder vertexShaderSource = new StringBuilder();
        StringBuilder fragmentShaderSource = new StringBuilder();
        try{
            String filePath = new File("").getAbsolutePath();
            BufferedReader reader = new BufferedReader(new FileReader(filePath + "\\src\\com\\company\\shader.vert"));
            String line;
            while((line = reader.readLine()) != null){
                vertexShaderSource.append(line).append('\n');
            }
            reader.close();
        }catch(IOException e){
            System.err.println("VertexShader wasn't loaded properly.");
            destroy();
            System.exit(1);
        }
        try{
            String filePath = new File("").getAbsolutePath();
            BufferedReader reader = new BufferedReader(new FileReader(filePath + "\\src\\com\\company\\shader.frag"));
            String line;
            while((line = reader.readLine()) != null){
                fragmentShaderSource.append(line).append('\n');
            }
            reader.close();
        }catch(IOException e){
            System.err.println("FragmentShader wasn't loaded properly.");
            destroy();
            System.exit(1);
        }
        glShaderSource(vertexShader, vertexShaderSource);
        glCompileShader(vertexShader);
        if(glGetShaderi(vertexShader, GL_COMPILE_STATUS) == GL_FALSE){
            System.err.println("Vertex shader wasn't able to be compiled correctly.");
        }
        glShaderSource(fragmentShader, fragmentShaderSource);
        glCompileShader(fragmentShader);
        if(glGetShaderi(fragmentShader, GL_COMPILE_STATUS) == GL_FALSE){
            System.err.println("Fragment shader wasn't able to be compiled correctly.");
        }
        glAttachShader(shaderProgram, vertexShader);
        glAttachShader(shaderProgram, fragmentShader);
        glLinkProgram(shaderProgram);
        glValidateProgram(shaderProgram);
        // Run the rendering loop until the user has attempted to close
        // the window or has pressed the ESCAPE key.
        while ( !closed() ) {
            glClear(GL_COLOR_BUFFER_BIT | GL_DEPTH_BUFFER_BIT); // clear the framebuffer

            glUseProgram(shaderProgram);
            IntBuffer w = BufferUtils.createIntBuffer(1);
            IntBuffer h = BufferUtils.createIntBuffer(1);
            glfwGetWindowSize(window, w, h);
            float dims[] = {w.get(0), h.get(0)};
            float cxy[] = {cx, cy};
            System.out.println(Arrays.toString(cxy));

            int view   = glGetUniformLocation(shaderProgram, "viewportDimensions");
            int minXL  = glGetUniformLocation(shaderProgram, "minX");
            int maxXL  = glGetUniformLocation(shaderProgram, "maxX");
            int minYL  = glGetUniformLocation(shaderProgram, "minY");
            int maxYL  = glGetUniformLocation(shaderProgram, "maxY");
            int limitL = glGetUniformLocation(shaderProgram, "limit");
            int _rMod   = glGetUniformLocation(shaderProgram, "rMod");
            int _gMod  = glGetUniformLocation(shaderProgram, "gMod");
            int _bMod  = glGetUniformLocation(shaderProgram, "bMod");
            int brL     = glGetUniformLocation(shaderProgram, "br");
            int _theme2 = glGetUniformLocation(shaderProgram, "theme2");
            int _theme3 = glGetUniformLocation(shaderProgram, "theme3");
            int _inv = glGetUniformLocation(shaderProgram, "inv");
            int _cxy = glGetUniformLocation(shaderProgram, "cxy");
            int _julia = glGetUniformLocation(shaderProgram, "julia");
            int _rainbow = glGetUniformLocation(shaderProgram, "rainbow");
            int _trip = glGetUniformLocation(shaderProgram, "trip");

            glUniform2fv(view, dims);
            glUniform2fv(_cxy, cxy);
            glUniform1f(minXL, minX);
            glUniform1f(maxXL, minX+domain);
            glUniform1f(minYL, minY);
            glUniform1f(maxYL, minY+range);
            glUniform1f(limitL, limit);
            glUniform1f(_rMod, rMod);
            glUniform1f(_gMod, gMod);
            glUniform1f(_bMod, bMod);
            glUniform1f(brL, br);
            glUniform1i(_theme2, theme2);
            glUniform1i(_theme3, theme3);
            glUniform1i(_inv, inv);
            glUniform1i(_julia, julia);
            glUniform1i(_rainbow, rainbow);
            glUniform1i(_trip, trip);

            glBegin(GL_POLYGON);

            glVertex2f(-1f, -1f);
            glVertex2f(1f, -1f);
            glVertex2f(1f, 1f);
            glVertex2f(-1f, 1f);

            glEnd();
            glUseProgram(0);

            glfwSwapBuffers(window); // swap the color buffers

            glfwPollEvents();
        }
        glDeleteProgram(shaderProgram);
        glDeleteShader(vertexShader);
        glDeleteShader(fragmentShader);
        destroy();
        System.exit(0);
    }
}
