package icengine.core.renderer;

import static org.lwjgl.opengl.GL11.GL_BACK;
import static org.lwjgl.opengl.GL11.GL_CULL_FACE;
import static org.lwjgl.opengl.GL11.GL_DEPTH_TEST;
import static org.lwjgl.opengl.GL11.GL_LINEAR;
import static org.lwjgl.opengl.GL11.GL_RGB;
import static org.lwjgl.opengl.GL11.GL_RGBA;
import static org.lwjgl.opengl.GL11.GL_TEXTURE_2D;
import static org.lwjgl.opengl.GL11.GL_TEXTURE_MAG_FILTER;
import static org.lwjgl.opengl.GL11.GL_TEXTURE_MIN_FILTER;
import static org.lwjgl.opengl.GL11.GL_TEXTURE_WRAP_S;
import static org.lwjgl.opengl.GL11.GL_TEXTURE_WRAP_T;
import static org.lwjgl.opengl.GL11.GL_UNSIGNED_BYTE;
import static org.lwjgl.opengl.GL11.glBindTexture;
import static org.lwjgl.opengl.GL11.glCullFace;
import static org.lwjgl.opengl.GL11.glDeleteTextures;
import static org.lwjgl.opengl.GL11.glDepthMask;
import static org.lwjgl.opengl.GL11.glDisable;
import static org.lwjgl.opengl.GL11.glEnable;
import static org.lwjgl.opengl.GL11.glGenTextures;
import static org.lwjgl.opengl.GL11.glTexImage2D;
import static org.lwjgl.opengl.GL11.glTexParameteri;
import static org.lwjgl.opengl.GL12.GL_CLAMP_TO_EDGE;
import static org.lwjgl.opengl.GL12.GL_TEXTURE_WRAP_R;
import static org.lwjgl.opengl.GL13.GL_TEXTURE0;
import static org.lwjgl.opengl.GL13.GL_TEXTURE_CUBE_MAP;
import static org.lwjgl.opengl.GL13.GL_TEXTURE_CUBE_MAP_NEGATIVE_X;
import static org.lwjgl.opengl.GL13.GL_TEXTURE_CUBE_MAP_NEGATIVE_Y;
import static org.lwjgl.opengl.GL13.GL_TEXTURE_CUBE_MAP_NEGATIVE_Z;
import static org.lwjgl.opengl.GL13.GL_TEXTURE_CUBE_MAP_POSITIVE_X;
import static org.lwjgl.opengl.GL13.GL_TEXTURE_CUBE_MAP_POSITIVE_Y;
import static org.lwjgl.opengl.GL13.GL_TEXTURE_CUBE_MAP_POSITIVE_Z;
import static org.lwjgl.opengl.GL13.glActiveTexture;
import static org.lwjgl.opengl.GL45.*;
import static org.lwjgl.stb.STBImage.stbi_image_free;
import static org.lwjgl.stb.STBImage.stbi_load;

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

    public void init(String right_, String left_, String top_, String bottom_, String back_, String front_) {
        cube = new Mesh("assets/meshes/Cube.obj");

        textureID = glGenTextures();
        
        
        files[0] = right_;
        files[1] = left_;
        files[2] = top_;
        files[3] = bottom_;
        files[4] = back_;
        files[5] = front_;

        o = new Quaternionf();
        p = new Vector3f(0.0f, 0.0f, 0.0f);

        shader = new Shader("assets/shaders/skybox.vert", "assets/shaders/skybox.frag");
        shader.bindAttribute(0, "inVertex");
        shader.compile();
        loadImages();
    }

    public void deInit() {
        glDeleteTextures(textureID);
        cube.deInit();
        shader.deInit();
    }
    

    public Matrix4f v() {
        // System.out.println(trkBll);
        // System.out.println(p);
        // System.out.println(o);
        return new Matrix4f().translate(p).rotate(o).scale(1f);
    }

    public void setViewOrientation(Quaternionf o) {
        System.out.println(o);
        this.o = o;
    }

    public float setZoom(float _z, float maxZoom, float minZoom) {
        float zoom = _z / 100;
        p.z = zoom;
        if (p.z < maxZoom) p.z = maxZoom;
        if (p.z > minZoom) p.z = minZoom;
        return zoom;
    }

    public void render(Camera _cam) {
        glDisable(GL_DEPTH_TEST);
        glDisable(GL_CULL_FACE);
        shader.use();
        shader.uploadTexture("skyboxTexture", 0);
        shader.uploadMat4f("projectionMatrix", _cam.getProjectionMatrix());
        shader.uploadMat4f("viewMatrix", v());
        glBindTexture(GL_TEXTURE_2D, textureID);
        cube.render();
        glBindTexture(GL_TEXTURE_2D, 0);
        glEnable(GL_DEPTH_TEST);
        glEnable(GL_CULL_FACE);
        shader.detach();
    }

    public void loadImages() {
        for (int i = 0; i < 6; i++) {
            IntBuffer width = BufferUtils.createIntBuffer(1);
            IntBuffer height = BufferUtils.createIntBuffer(1);
            IntBuffer channels = BufferUtils.createIntBuffer(1);
            ByteBuffer image = stbi_load(files[i], width, height, channels, 0);
            int mode = (channels.get(0) == 4) ? GL_RGBA : GL_RGB;
            if (image != null) {
                glTexImage2D(cubeMap[i], 0, mode, width.get(), height.get(), 0, mode, GL_UNSIGNED_BYTE, image);
                stbi_image_free(image);
            } else {
                assert false : "Error: Texture failed to load at path: " + files[i];
            }
        }
    
        // Set texture parameters
        glTexParameteri(GL_TEXTURE_CUBE_MAP, GL_TEXTURE_MAG_FILTER, GL_LINEAR);
        glTexParameteri(GL_TEXTURE_CUBE_MAP, GL_TEXTURE_MIN_FILTER, GL_LINEAR);
        glTexParameteri(GL_TEXTURE_CUBE_MAP, GL_TEXTURE_WRAP_S, GL_CLAMP_TO_EDGE);
        glTexParameteri(GL_TEXTURE_CUBE_MAP, GL_TEXTURE_WRAP_T, GL_CLAMP_TO_EDGE);
        glTexParameteri(GL_TEXTURE_CUBE_MAP, GL_TEXTURE_WRAP_R, GL_CLAMP_TO_EDGE);
    }
}
