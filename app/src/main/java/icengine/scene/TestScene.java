package icengine.scene;

import org.joml.Matrix4f;
import org.joml.Vector3f;

import static org.lwjgl.glfw.GLFW.*;

import icengine.core.Camera;
import icengine.core.input.KeyListener;
import icengine.core.renderer.Shader;
import icengine.core.renderer.model.Loader;
import icengine.core.renderer.model.RawModel;
import icengine.core.renderer.model.Renderer;
import icengine.util.ICMath;
import static icengine.util.ICMath.*;


public class TestScene extends Scene{

    private Loader loader;
    private Renderer renderer;
    private RawModel model;
    private Camera camera;
    private Shader defaultShader;
    private Matrix4f modelMatrix = new Matrix4f();

    private float[] vertices = {
        -0.5f, 0.5f, 0f,    // TL
        -0.5f, -0.5f, 0f,   // BL
        0.5f, -0.5f, 0f,    // BR
        0.5f, 0.5f, 0f,     // TR
      };
    
    private int[] indices = {
        0, 1, 3,    // TL, BL, TR
        3, 1, 2     // TR, BL, BR
    };
    public TestScene() {
        
    }
    
    @Override
    public void init() {
        loader = new Loader();
        model = loader.loadToVAO(vertices, indices);
        renderer = new Renderer();
        defaultShader = new Shader("assets/shaders/default.vert", "assets/shaders/default.frag");
        defaultShader.compile();
        KeyListener.get();
        camera = new Camera(
            new Vector3f(0.0f, 0.0f, 5.0f),
            new Matrix4f().perspective(45.0f, (16.0f / 9.0f),  0.5f, 1000.0f));
        //modelMatrix.identity();
        defaultShader.bindAttribute(0, "inVertex");
    }

    @Override
    public void deInit() {
        loader.deInit();
        defaultShader.deInit();
    }

    @Override
    public void update(float dt) {
        float c_speed = 10.0f;

        if (KeyListener.isKeyPressed(GLFW_KEY_LEFT_SHIFT)) {
            c_speed *= 2.0f;
        }

        if (KeyListener.isKeyPressed(GLFW_KEY_W)) {
            camera.move(new Vector3f(ICMath.FORWARD), c_speed * dt);
        } else if (KeyListener.isKeyPressed(GLFW_KEY_S)) {
            camera.move(new Vector3f(ICMath.BACKWARD), c_speed * dt);
        } else if (KeyListener.isKeyPressed(GLFW_KEY_A)) {
            camera.move(new Vector3f(ICMath.LEFT), c_speed * dt);
        } else if (KeyListener.isKeyPressed(GLFW_KEY_D)) {
            camera.move(new Vector3f(ICMath.RIGHT), c_speed * dt);
        }

        modelMatrix.rotate((float)Math.toRadians(10.0f) * dt, new Vector3f(0.0f, 1.0f, 0.0f));
    }


    @Override
    public void render() {
        renderer.prepare();
        defaultShader.use();
        defaultShader.uploadMat4f("projection", camera.getProjectionMatrix());
        defaultShader.uploadMat4f("view", camera.getViewMatrix());
        defaultShader.uploadMat4f("model", modelMatrix);
        renderer.render(model);
        defaultShader.detach();
    }
    
}
