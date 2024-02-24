package icengine.scene;

import static org.lwjgl.glfw.GLFW.*;
import static org.lwjgl.opengl.GL11.*;
import static org.lwjgl.opengl.GL13.*;

import org.joml.Matrix4f;
import org.joml.Vector3f;

import icengine.core.Camera;
import icengine.core.input.KeyListener;
import icengine.core.input.Trackball;
import icengine.core.renderer.Mesh;
import icengine.core.renderer.Shader;
import icengine.core.renderer.Skybox;
import icengine.core.renderer.Texture;
import icengine.util.ICMath;


public class TestScene extends Scene {
    private Mesh mesh;
    private Camera camera;
    private Shader defaultShader;
    private Texture texture;
    private Matrix4f modelMatrix = new Matrix4f();
    private Trackball trackball;
    private boolean drawInWireMode = false;
    private Skybox skybox;
    public TestScene() {

    }

    @Override
    public void init() {
        mesh = new Mesh("assets/meshes/Skull.obj");
        texture = new Texture("assets/textures/skull_texture.jpg");
        defaultShader = new Shader("assets/shaders/default.vert", "assets/shaders/default.frag");
        defaultShader.compile();
        defaultShader.bindAttribute(0, "inVertex");
        defaultShader.bindAttribute(1, "inTexCoord");

        skybox=new Skybox();
        skybox.init("assets/textures/skybox/posx.jpg", "assets/textures/skybox/negx.jpg", "assets/textures/skybox/posy.jpg", "assets/textures/skybox/negy.jpg", "assets/textures/skybox/posz.jpg", "assets/textures/skybox/negz.jpg");

        trackball = new Trackball();

        KeyListener.get();
        camera = new Camera(
                new Vector3f(0.0f, 0.0f, 5.0f),
                new Matrix4f().perspective(45.0f, (16.0f / 9.0f), 0.5f, 100.0f));
        modelMatrix.identity();
    }

    @Override
    public void deInit() {
        mesh.deInit();
        texture.deInit();
        defaultShader.deInit();
    }
    
    @Override
    public void handleEvents() {
        trackball.handleEvents();
        if (KeyListener.isKeyPressed(GLFW_KEY_Q)) {
            drawInWireMode = !drawInWireMode;
        }
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

        modelMatrix
            .identity()
            .rotate(trackball.getQuat());
            //.rotate((float) Math.toRadians(10.0f) * dt, new Vector3f(ICMath.Y_AXIS));

        //camera.setOrientation(trackball.getQuat());
        
    }

    @Override
    public void render() {
        glEnable(GL_DEPTH_TEST);
        if (drawInWireMode) {
		    glDisable(GL_DEPTH_TEST);
	    }
        glClearColor(0, 0, 0, 1);
        glClear(GL_COLOR_BUFFER_BIT | GL_DEPTH_BUFFER_BIT);

        if(drawInWireMode){
		    glPolygonMode(GL_FRONT_AND_BACK, GL_LINE);
	    }else{
		    glPolygonMode(GL_FRONT_AND_BACK, GL_FILL);
	    }
        skybox.render(camera);
        defaultShader.use();

        defaultShader.uploadTexture("tex", 0);
        glActiveTexture(GL_TEXTURE0);
        texture.bind();

        defaultShader.uploadMat4f("projection", camera.getProjectionMatrix());
        defaultShader.uploadMat4f("view", camera.getViewMatrix());
        defaultShader.uploadMat4f("model", modelMatrix);
        mesh.render();
        texture.unbind();
        defaultShader.detach();
    }


}
