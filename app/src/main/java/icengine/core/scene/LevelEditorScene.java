package icengine.core.scene;

import static org.lwjgl.opengl.GL45.*;
import static org.lwjgl.glfw.GLFW.*;
import static org.lwjgl.opengl.GL11.GL_COLOR_BUFFER_BIT;
import static org.lwjgl.opengl.GL11.GL_FLOAT;
import static org.lwjgl.opengl.GL11.GL_TRIANGLES;
import static org.lwjgl.opengl.GL11.GL_UNSIGNED_INT;
import static org.lwjgl.opengl.GL11.glClear;
import static org.lwjgl.opengl.GL11.glClearColor;
import static org.lwjgl.opengl.GL11.glDrawElements;
import static org.lwjgl.opengl.GL15.GL_ARRAY_BUFFER;
import static org.lwjgl.opengl.GL15.GL_ELEMENT_ARRAY_BUFFER;
import static org.lwjgl.opengl.GL15.GL_STATIC_DRAW;
import static org.lwjgl.opengl.GL15.glBindBuffer;
import static org.lwjgl.opengl.GL15.glBufferData;
import static org.lwjgl.opengl.GL15.glGenBuffers;
import static org.lwjgl.opengl.GL20.glDisableVertexAttribArray;
import static org.lwjgl.opengl.GL20.glEnableVertexAttribArray;
import static org.lwjgl.opengl.GL20.glVertexAttribPointer;
import static org.lwjgl.opengl.GL30.glBindVertexArray;
import static org.lwjgl.opengl.GL30.glGenVertexArrays;

import java.nio.FloatBuffer;
import java.nio.IntBuffer;

import org.joml.Vector2f;
import org.lwjgl.BufferUtils;

import icengine.core.Camera;
import icengine.core.input.KeyListener;
import icengine.core.renderer.Shader;

public class LevelEditorScene extends Scene {

    private Shader defaultShader;
    private Camera camera;
    private float[] vertexArray = {
            -50.0f,  50.0f, 0.0f,      1.0f, 0.0f, 0.0f, 1.0f, // Bottom right
             50.0f, -50.0f, 0.0f,      0.0f, 1.0f, 0.0f, 1.0f, // Top left
             50.0f,  50.0f, 0.0f,      0.0f, 0.0f, 1.0f, 1.0f, // Top right
            -50.0f, -50.0f, 0.0f,      1.0f, 1.0f, 0.0f, 1.0f, // Bottom left
    };

    private int[] elementArray = {
            2, 1, 0, // Top right triangle
            0, 1, 3 // Bottom left triangle

    };
    private int vaoID, vboID, eboID;

    public LevelEditorScene() {
        
    }

    @Override
    public void init() {
        defaultShader = new Shader("assets/shaders/default.vert", "assets/shaders/default.frag");
        defaultShader.compile();
        KeyListener.get();

        camera = new Camera(new Vector2f(0.0f, 0.0f));
        
        vaoID = glGenVertexArrays();
        glBindVertexArray(vaoID);

        FloatBuffer vertexBuffer = BufferUtils.createFloatBuffer(vertexArray.length);
        vertexBuffer.put(vertexArray).flip();

        vboID = glGenBuffers();
        glBindBuffer(GL_ARRAY_BUFFER, vboID);
        glBufferData(GL_ARRAY_BUFFER, vertexBuffer, GL_STATIC_DRAW);

        IntBuffer elementBuffer = BufferUtils.createIntBuffer(elementArray.length);
        elementBuffer.put(elementArray).flip();

        eboID = glGenBuffers();
        glBindBuffer(GL_ELEMENT_ARRAY_BUFFER, eboID);
        glBufferData(GL_ELEMENT_ARRAY_BUFFER, elementBuffer, GL_STATIC_DRAW);

        int positionsSize = 3;
        int colorSize = 4;
        int floatSizeBytes = 4;
        int vertexSizeBytes = (positionsSize + colorSize) * floatSizeBytes;
        glVertexAttribPointer(0, positionsSize, GL_FLOAT, false, vertexSizeBytes, 0);
        glEnableVertexAttribArray(0);

        glVertexAttribPointer(1, colorSize, GL_FLOAT, false, vertexSizeBytes, positionsSize * floatSizeBytes);
        glEnableVertexAttribArray(1);

    }

    @Override
    public void update(float dt) {
        float c_speed = 500.0f;
        if (KeyListener.isKeyPressed(GLFW_KEY_W)) {
            camera.position.y -= c_speed * dt;
        } else if (KeyListener.isKeyPressed(GLFW_KEY_S)) {
            camera.position.y += c_speed * dt;
        } else if (KeyListener.isKeyPressed(GLFW_KEY_A)) {
            camera.position.x += c_speed * dt;
        } else if (KeyListener.isKeyPressed(GLFW_KEY_D)) {
            camera.position.x -= c_speed * dt;
        }
    }

    @Override
    public void render() {
        glClearColor(0.0f, 0.0f, 0.0f, 1.0f);        
		glClear(GL_COLOR_BUFFER_BIT);

        defaultShader.use();
        defaultShader.uploadMat4f("projection", camera.getProjectionMatrix());
        defaultShader.uploadMat4f("view", camera.getViewMatrix());

        glBindVertexArray(vaoID);
        glEnableVertexAttribArray(0);
        glEnableVertexAttribArray(1);

        glDrawElements(GL_TRIANGLES, elementArray.length, GL_UNSIGNED_INT, 0);

        glDisableVertexAttribArray(0);
        glDisableVertexAttribArray(1);
        glBindVertexArray(0);
        defaultShader.detach();
    }

}
