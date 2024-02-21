package icengine.core;


import org.lwjgl.Version;
import org.lwjgl.glfw.GLFWErrorCallback;
import org.lwjgl.opengl.GL;


import icengine.core.input.KeyListener;
import icengine.core.input.MouseListener;
import icengine.scene.*;
import icengine.util.Time;

import static org.lwjgl.glfw.Callbacks.glfwFreeCallbacks;
import static org.lwjgl.glfw.GLFW.*;
import static org.lwjgl.system.MemoryUtil.NULL;

import org.joml.Vector2f;


public class Window {
	private int width, height;
	private String title;
	private long glfwWindow;
	private float dt = -1;
	public  float r, g, b, a;

	private static Window window = null;
	private static Scene currentScene = null;
	
	private Window() {
		this.width = 1290;
		this.height = 720;
		this.title = "ICEngine (Internal Combustion Engine) v0.0.2";
		
		
	}
	
	public static void changeScene(int newScene) {
		if (currentScene != null) {
			currentScene.deInit();
		}
		switch (newScene) {
			case 0:
				currentScene = new TestScene();
				break;
			case 1:
				currentScene = new LevelScene();
				break;
			default:
				assert false : "Unknown scene \"" + newScene + "\"";
				break;
		}
		currentScene.init();
	}

	// Singleton - Combined with the static window, it ensures only one instance of window gets created 
	public static Window get() {
		if (Window.window == null) {
			Window.window = new Window();
		}
		return window;
	}

	public Vector2f getSize() {
		return new Vector2f(width, height);
	}
	
	
	@SuppressWarnings("null")
	public void run() {
		System.out.println("Hello LWJGL " + Version.getVersion() + "!");
       
		
		init();
		loop();

		if (currentScene != null) {
			currentScene.deInit();
		}

		glfwFreeCallbacks(glfwWindow);
		glfwDestroyWindow(glfwWindow);

		glfwTerminate();
		
		glfwSetErrorCallback(null).free();
	}
	
	public void init() {
		GLFWErrorCallback.createPrint(System.err).set();
		
		// Initialize GLFW
		if (!glfwInit()) {
			throw new IllegalStateException("Unable to initalize GLFW");
		}

		// Configure GLFW
		glfwDefaultWindowHints();
		glfwWindowHint(GLFW_VISIBLE, GLFW_FALSE);
		glfwWindowHint(GLFW_RESIZABLE, GLFW_TRUE);
		//glfwWindowHint(GLFW_MAXIMIZED, GLFW_TRUE);

		// Create window
		glfwWindow = glfwCreateWindow(this.width, this.height, this.title, NULL, NULL);
		if (glfwWindow == NULL) {
			throw new IllegalStateException("Failed to create GLFW window");
		}

		// Set up mouse and key callback
		glfwSetCursorPosCallback(glfwWindow, MouseListener::mousePosCallback);
		glfwSetMouseButtonCallback(glfwWindow, MouseListener::mouseButtonCallback);
		glfwSetScrollCallback(glfwWindow, MouseListener::mouseScrollCallback);
		glfwSetKeyCallback(glfwWindow, KeyListener::keyCallback);

		glfwMakeContextCurrent(glfwWindow);

		// Hides cursor
		//glfwSetInputMode(glfwWindow, GLFW_CURSOR, GLFW_CURSOR_HIDDEN);

		// Enable v-sync
		glfwSwapInterval(1);

		glfwShowWindow(glfwWindow);

		// OPENGL dies without this, plz use this
		GL.createCapabilities();

		changeScene(0);
	}

	public void loop() {
		float beginTime = Time.getTime();
		float endTime = Time.getTime();
		
		while (!glfwWindowShouldClose(glfwWindow)) {
			glfwPollEvents();

			if (dt >= 0) {
				currentScene.update(dt);
			}
			

			currentScene.render();
			 
			glfwSwapBuffers(glfwWindow);

			endTime = Time.getTime();
			dt = endTime - beginTime;
			beginTime = endTime;
		}
	}
}
