package icengine.core.renderer;


import static org.lwjgl.opengl.GL45.*;

 
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;

public class Shader {
    private int vertexID, fragmentID, shaderProgramID;
    private String vertexSrc, fragmentSrc;
    private String vertexPath, fragmentPath;

    public Shader (String vertexPath, String fragmentPath) {
        this.vertexPath = vertexPath;
        this.fragmentPath = fragmentPath;

        try {
            vertexSrc = new String(Files.readAllBytes(Paths.get(this.vertexPath)));
        } catch(IOException e) {
            e.printStackTrace();
            assert false: "Error loading vertex shader: '" + this.vertexPath + "'";
        }

        try {
            fragmentSrc = new String(Files.readAllBytes(Paths.get(this.fragmentPath)));
        } catch(IOException e) {
            e.printStackTrace();
            assert false: "Error loading fragment shader: '" + this.fragmentPath + "'";
        }

        System.out.println("V:" + vertexSrc);
        System.out.println("F:" + fragmentSrc);
    }

    public void compile() {
        vertexID = glCreateShader(GL_VERTEX_SHADER);
        glShaderSource(vertexID, vertexSrc);
        glCompileShader(vertexID);
        int success = glGetShaderi(vertexID, GL_COMPILE_STATUS);
        if (success == GL_FALSE) {
            int len = glGetShaderi(vertexID, GL_INFO_LOG_LENGTH);
            System.out.println("Error: 'defaultshader.glsl'\n\tVertex shader compilation failed");
            System.out.println(glGetShaderInfoLog(vertexID, len));
            assert false : "";
        }

        fragmentID = glCreateShader(GL_FRAGMENT_SHADER);
        glShaderSource(fragmentID, fragmentSrc);
        glCompileShader(fragmentID);
        success = glGetShaderi(fragmentID, GL_COMPILE_STATUS);
        if (success == GL_FALSE) {
            int len = glGetShaderi(fragmentID, GL_INFO_LOG_LENGTH);
            System.out.println("Error: 'defaultshader.glsl'\n\tFragment shader compilation failed");
            System.out.println(glGetShaderInfoLog(fragmentID, len));
            assert false : "";
        }

        shaderProgramID = glCreateProgram();
        glAttachShader(shaderProgramID, vertexID);
        glAttachShader(shaderProgramID, fragmentID);
        glLinkProgram(shaderProgramID);

        success = glGetProgrami(shaderProgramID, GL_LINK_STATUS);
        if (success == GL_FALSE) {
            int len = glGetProgrami(shaderProgramID, GL_INFO_LOG_LENGTH);
            System.out.println("Error: 'defaultshader.glsl'\n\tLinking shader failed");
            System.out.println(glGetProgramInfoLog(shaderProgramID, len));
            assert false : "";
        }
    }

    public void use() {
        glUseProgram(shaderProgramID);

    }
    public void detach() {
        glUseProgram(0);
    }
}
