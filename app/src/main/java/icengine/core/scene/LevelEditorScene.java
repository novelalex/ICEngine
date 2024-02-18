package icengine.core.scene;

import static org.lwjgl.opengl.GL45.*;

import java.nio.FloatBuffer;
import java.nio.IntBuffer;

import org.joml.Vector2f;
import org.lwjgl.BufferUtils;

import icengine.core.Window;
import icengine.core.input.MouseListener;

public class LevelEditorScene extends Scene {

    private String vertexShaderSrc = "#version 330 core\r\n" + //
                "\r\n" + //
                "layout (location=0) in vec3 aPos;\r\n" + //
                "layout (location=1) in vec4 aColor;\r\n" + //
                "\r\n" + //
                "out vec4 fColor;\r\n" + //
                "\r\n" + //
                "uniform vec3 mPos;\r\n" + //
                "\r\n" + //
                "void main() {\r\n" + //
                "    fColor = aColor;\r\n" + //
                "    gl_Position = vec4(aPos + mPos + 0.5, 1.0);\r\n" + //
                "}";
    private String fragmentShaderSrc = "#version 330 core\r\n" + //
                "\r\n" + //
                "in vec4 fColor;\r\n" + //
                "out vec4 color;\r\n" + //
                "\r\n" + //
                "uniform float mousePosX;\r\n" + //
                "\r\n" + //
                "void main() {\r\n" + //
                "    color = fColor * mousePosX;\r\n" + //
                "}\r\n" + //
                "";

    private int vertexID, fragmentID, shaderProgram;

    private float[] vertexArray = {
            -0.5f,  0.5f, 0.0f,      1.0f, 0.0f, 0.0f, 1.0f, // Bottom right
             0.5f, -0.5f, 0.0f,      0.0f, 1.0f, 0.0f, 1.0f, // Top left
             0.5f,  0.5f, 0.0f,      0.0f, 0.0f, 1.0f, 1.0f, // Top right
            -0.5f, -0.5f, 0.0f,      1.0f, 1.0f, 0.0f, 1.0f, // Bottom left
    };

    private int[] elementArray = {
            /*
             * x x
             * 
             * 
             * x x
             */
            2, 1, 0, // Top right triangle
            0, 1, 3 // Bottom left triangle

    };
    private int vaoID, vboID, eboID;

    public LevelEditorScene() {

    }

    @Override
    public void init() {
        vertexID = glCreateShader(GL_VERTEX_SHADER);
        glShaderSource(vertexID, vertexShaderSrc);
        glCompileShader(vertexID);
        int success = glGetShaderi(vertexID, GL_COMPILE_STATUS);
        if (success == GL_FALSE) {
            int len = glGetShaderi(vertexID, GL_INFO_LOG_LENGTH);
            System.out.println("Error: 'defaultshader.glsl'\n\tVertex shader compilation failed");
            System.out.println(glGetShaderInfoLog(vertexID, len));
            assert false : "";
        }

        fragmentID = glCreateShader(GL_FRAGMENT_SHADER);
        glShaderSource(fragmentID, fragmentShaderSrc);
        glCompileShader(fragmentID);
        success = glGetShaderi(fragmentID, GL_COMPILE_STATUS);
        if (success == GL_FALSE) {
            int len = glGetShaderi(fragmentID, GL_INFO_LOG_LENGTH);
            System.out.println("Error: 'defaultshader.glsl'\n\tFragment shader compilation failed");
            System.out.println(glGetShaderInfoLog(fragmentID, len));
            assert false : "";
        }

        shaderProgram = glCreateProgram();
        glAttachShader(shaderProgram, vertexID);
        glAttachShader(shaderProgram, fragmentID);
        glLinkProgram(shaderProgram);

        success = glGetProgrami(shaderProgram, GL_LINK_STATUS);
        if (success == GL_FALSE) {
            int len = glGetProgrami(shaderProgram, GL_INFO_LOG_LENGTH);
            System.out.println("Error: 'defaultshader.glsl'\n\tLinking shader failed");
            System.out.println(glGetProgramInfoLog(shaderProgram, len));
            assert false : "";
        }

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
        
    }

    @Override
    public void render() {
        glUseProgram(shaderProgram);
        glBindVertexArray(vaoID);
        glEnableVertexAttribArray(0);
        glEnableVertexAttribArray(1);
        Vector2f mPos = new Vector2f(
            MouseListener.getX() / Window.get().getSize().x,
            -MouseListener.getY() / Window.get().getSize().y);

        glUniform3f(0, mPos.x, mPos.y, 0.0f);

        glDrawElements(GL_TRIANGLES, elementArray.length, GL_UNSIGNED_INT, 0);

        glDisableVertexAttribArray(0);
        glDisableVertexAttribArray(1);
        glBindVertexArray(0);
        glUseProgram(0);
    }

}
