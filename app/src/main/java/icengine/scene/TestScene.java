package icengine.scene;

import org.joml.Matrix4f;
import org.joml.Vector3f;

import static org.lwjgl.glfw.GLFW.*;
import static org.lwjgl.opengl.GL11.GL_COLOR_BUFFER_BIT;
import static org.lwjgl.opengl.GL11.glClear;
import static org.lwjgl.opengl.GL11.glClearColor;
import static org.lwjgl.opengl.GL45.*;

import icengine.core.Camera;
import icengine.core.input.KeyListener;
import icengine.core.renderer.Mesh;
import icengine.core.renderer.Shader;
import icengine.core.renderer.Texture;
import icengine.util.ICMath;


public class TestScene extends Scene{
    private Mesh squareMesh;
    private Camera camera;
    private Shader defaultShader;
    private Texture texture = new Texture("assets/textures/real.png");
    private Matrix4f modelMatrix = new Matrix4f().rotate((float)Math.toRadians(180), new Vector3f(ICMath.X_AXIS));

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

    private float[] uvCoords = {
        0, 1,
        0, 0,
        1, 0,
        1, 1
    };
    public TestScene() {
        
    }
    
    @Override
    public void init() {
        squareMesh = new Mesh(vertices, indices, uvCoords);
        defaultShader = new Shader("assets/shaders/default.vert", "assets/shaders/default.frag");
        defaultShader.compile();
        defaultShader.bindAttribute(0, "inVertex");
        defaultShader.bindAttribute(1, "inTexCoord");

        KeyListener.get();
        camera = new Camera(
            new Vector3f(0.0f, 0.0f, 5.0f),
            new Matrix4f().perspective(45.0f, (16.0f / 9.0f),  0.5f, 1000.0f));
        //modelMatrix.identity();
    }

    @Override
    public void deInit() {
        squareMesh.deInit();
        texture.deInit();
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

        modelMatrix.rotate((float)Math.toRadians(10.0f) * dt, new Vector3f(ICMath.Y_AXIS));
    }


    @Override
    public void render() {
        glClearColor(0, 0, 0, 1);
        glClear(GL_COLOR_BUFFER_BIT);
        
        defaultShader.use();
        
        defaultShader.uploadTexture("tex", 0);
        glActiveTexture(GL_TEXTURE0);
        texture.bind();

        defaultShader.uploadMat4f("projection", camera.getProjectionMatrix());
        defaultShader.uploadMat4f("view", camera.getViewMatrix());
        defaultShader.uploadMat4f("model", modelMatrix);
        squareMesh.render();
        texture.unbind();
        defaultShader.detach();
    }
    
}
