package icengine.core.renderer;

import static org.lwjgl.opengl.GL11.*;
import static org.lwjgl.opengl.GL12.*;
import static org.lwjgl.opengl.GL13.*;

import java.nio.ByteBuffer;
import java.nio.IntBuffer;

import org.joml.Matrix4f;
import org.joml.Quaternionf;
import org.joml.Vector3f;
import org.lwjgl.BufferUtils;
import org.lwjgl.stb.STBImage;

import icengine.core.Camera;

public class Skybox {

    private int textureID;

    private static String[] files = new String[6];

    private static int[] cubeMap = {
        GL_TEXTURE_CUBE_MAP_POSITIVE_X, // right
        GL_TEXTURE_CUBE_MAP_NEGATIVE_X, // left
        GL_TEXTURE_CUBE_MAP_POSITIVE_Y, // top
        GL_TEXTURE_CUBE_MAP_NEGATIVE_Y, // bottom
        GL_TEXTURE_CUBE_MAP_POSITIVE_Z, // back
        GL_TEXTURE_CUBE_MAP_NEGATIVE_Z // front
    };

    private Mesh cube;
    private Shader shader;

    private Quaternionf o; // Orientation
    private Vector3f p; // Position
    private Quaternionf trkBll;

    public void init(String right_, String left_, String top_, String bottom_, String back_, String front_) {
        cube = new Mesh("assets/meshes/Cube.obj");

        textureID = glGenTextures();
        glBindTexture(GL_TEXTURE_CUBE_MAP, textureID);

        files[0] = right_;
        files[1] = left_;
        files[2] = top_;
        files[3] = bottom_;
        files[4] = back_;
        files[5] = front_;

        o = new Quaternionf(1.0f, 0.0f, 0.0f, 0.0f);
        p = new Vector3f(0.0f, 0.0f, 0.0f);
    }

    public boolean onCreate() {
        cube = new Mesh("assets/meshes/Cube.obj");

        shader = new Shader("assets/shaders/skybox.vert", "assets/shaders/skybox.frag");
        shader.bindAttribute(0, "inVertex");
        shader.compile();

        return loadImages();
    }

    public Matrix4f v() {
        return new Matrix4f().rotation(trkBll).translate(p).rotate(o).scale(2.0f);
    }

    public Quaternionf setTrackball(Quaternionf _t) {
        trkBll = _t;
        return trkBll;
    }

    public float setZoom(float _z, float maxZoom, float minZoom) {
        float zoom = _z / 100;
        p.z = zoom;
        if (p.z < maxZoom) p.z = maxZoom;
        if (p.z > minZoom) p.z = minZoom;
        return zoom;
    }

    public void render(Camera _cam) {
        glDepthMask(false);
        glCullFace(GL_NONE);
        shader.use();
        glBindTexture(GL_TEXTURE_CUBE_MAP, textureID);
        
        shader.uploadTexture("skyboxTexture", 0);
        glActiveTexture(GL_TEXTURE0);
        shader.uploadMat4f("projectionMatrix", _cam.getProjectionMatrix());
        shader.uploadMat4f("viewMatrix", v());

        cube.render();
        glDepthMask(true);
        glCullFace(GL_BACK);
        shader.detach();
    }

    public boolean loadImages() {
        for (int i = 0; i < 6; i++) {
            ByteBuffer imageBuffer;
            IntBuffer widthBuffer = BufferUtils.createIntBuffer(1);
            IntBuffer heightBuffer = BufferUtils.createIntBuffer(1);
            IntBuffer channelsBuffer = BufferUtils.createIntBuffer(1);
    
            // Load image using STB library
            imageBuffer = STBImage.stbi_load(files[i], widthBuffer, heightBuffer, channelsBuffer, 0);
            if (imageBuffer == null) {
                System.err.println("Failed to load image: " + files[i]);
                return false;
            }
    
            int width = 0;
            int height = 0;
            if (widthBuffer.hasRemaining() && heightBuffer.hasRemaining()) {
                width = widthBuffer.get();
                height = heightBuffer.get();
            } else {
                System.err.println("Failed to get image dimensions for: " + files[i]);
                STBImage.stbi_image_free(imageBuffer);
                return false;
            }
    
            // Upload texture to OpenGL
            glTexImage2D(cubeMap[i], 0, GL_RGBA, width, height, 0, GL_RGBA, GL_UNSIGNED_BYTE, imageBuffer);
    
            // Free image buffer
            STBImage.stbi_image_free(imageBuffer);
        }
    
        // Set texture parameters
        glTexParameteri(GL_TEXTURE_CUBE_MAP, GL_TEXTURE_MAG_FILTER, GL_LINEAR);
        glTexParameteri(GL_TEXTURE_CUBE_MAP, GL_TEXTURE_MIN_FILTER, GL_LINEAR);
        glTexParameteri(GL_TEXTURE_CUBE_MAP, GL_TEXTURE_WRAP_S, GL_CLAMP_TO_EDGE);
        glTexParameteri(GL_TEXTURE_CUBE_MAP, GL_TEXTURE_WRAP_T, GL_CLAMP_TO_EDGE);
        glTexParameteri(GL_TEXTURE_CUBE_MAP, GL_TEXTURE_WRAP_R, GL_CLAMP_TO_EDGE);
    
        return true;
    }
}
