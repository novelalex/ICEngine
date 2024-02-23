package icengine.core.renderer;

import java.nio.FloatBuffer;
import java.nio.IntBuffer;
import java.util.ArrayList;
import java.util.List;

import org.lwjgl.BufferUtils;
import org.lwjgl.assimp.AIFace;
import org.lwjgl.assimp.AIMesh;
import org.lwjgl.assimp.AIScene;
import org.lwjgl.assimp.Assimp;

import static org.lwjgl.opengl.GL11.GL_FLOAT;
import static org.lwjgl.opengl.GL11.GL_TRIANGLES;
import static org.lwjgl.opengl.GL11.GL_UNSIGNED_INT;
import static org.lwjgl.opengl.GL11.glDrawElements;
import static org.lwjgl.opengl.GL15.GL_ARRAY_BUFFER;
import static org.lwjgl.opengl.GL15.GL_ELEMENT_ARRAY_BUFFER;
import static org.lwjgl.opengl.GL15.GL_STATIC_DRAW;
import static org.lwjgl.opengl.GL15.glBindBuffer;
import static org.lwjgl.opengl.GL15.glBufferData;
import static org.lwjgl.opengl.GL15.glDeleteBuffers;
import static org.lwjgl.opengl.GL15.glGenBuffers;
import static org.lwjgl.opengl.GL20.glDisableVertexAttribArray;
import static org.lwjgl.opengl.GL20.glEnableVertexAttribArray;
import static org.lwjgl.opengl.GL20.glVertexAttribPointer;
import static org.lwjgl.opengl.GL30.glBindVertexArray;
import static org.lwjgl.opengl.GL30.glDeleteVertexArrays;
import static org.lwjgl.opengl.GL30.glGenVertexArrays;

public class Mesh {
    private int vaoID;
    private int vertexCount;

    private List<Integer> vbos = new ArrayList<Integer>();

    public int getVaoID() {
        return vaoID;
    }

    public int getVertexCount() {
        return vertexCount;
    }

    private void loadRawMesh(float[] positions, int [] indices, float[] uvCoords, float[] normals) {
        vaoID = createVAO();
        vertexCount = indices.length;
        bindIndicesBuffer(indices);
        // TODO: Put everything in a single VBO like Scott does
        storeDataInAttributeList(0, 3, positions);
        storeDataInAttributeList(1, 3, normals);
        storeDataInAttributeList(2, 2, uvCoords);
        unbindVAO();
    }   

    public Mesh(String fileName) {
        AIScene scene = Assimp.aiImportFile(fileName,
			Assimp.aiProcess_Triangulate |	
			Assimp.aiProcess_GenSmoothNormals |	
			Assimp.aiProcess_FlipUVs |	
			Assimp.aiProcess_CalcTangentSpace |	
			Assimp.aiProcess_LimitBoneWeights	
		);

        if (scene == null) {
            throw new IllegalStateException("Error loading model: " + fileName);
        }

        int numMeshes = scene.mNumMeshes();
        if (numMeshes == 0) {
            throw new IllegalStateException("No meshes found in model: " + fileName);
        }

        AIMesh mesh = AIMesh.create(scene.mMeshes().get(0));

        int sizeOfVec3 = 3;
        int sizeOfVec2 = 2;
        float vertexArray[] = new float[mesh.mNumVertices() * sizeOfVec3];
        float uvArray[] = new float[mesh.mNumVertices() * sizeOfVec2];
        int indicesArray[] = new int[mesh.mNumFaces() * 3];
        float normalsArray[] = new float[mesh.mNumVertices() * sizeOfVec3];
        
        for (int i = 0; i < mesh.mNumVertices(); i++) {
            vertexArray[i * sizeOfVec3] = mesh.mVertices().get(i).x();
            vertexArray[i * sizeOfVec3 + 1] = mesh.mVertices().get(i).y();
            vertexArray[i * sizeOfVec3 + 2] = mesh.mVertices().get(i).z();
            uvArray[i * sizeOfVec2] = mesh.mTextureCoords(0).get(i).x();
            uvArray[i * sizeOfVec2 + 1] = mesh.mTextureCoords(0).get(i).y();
            normalsArray[i * sizeOfVec3] = mesh.mNormals().get(i).x();
            normalsArray[i * sizeOfVec3 + 1] = mesh.mNormals().get(i).y();
            normalsArray[i * sizeOfVec3 + 2] = mesh.mNormals().get(i).z();
        }

        IntBuffer indices = BufferUtils.createIntBuffer(mesh.mNumFaces() * mesh.mFaces().get(0).mNumIndices());
		
		for(int f = 0; f < mesh.mNumFaces(); f++)
		{
			AIFace face = mesh.mFaces().get(f);
			for(int ind = 0; ind < face.mNumIndices(); ind++)
				indices.put(face.mIndices().get(ind));
		}

        indices.flip();
        indices.get(indicesArray);

        loadRawMesh(vertexArray, indicesArray, uvArray, normalsArray);
		
    }

    public Mesh(float[] positions, int [] indices, float[] uvCoords, float[] normals) {
        loadRawMesh(positions, indices, uvCoords, normals);
    }

    private int createVAO() {
        int vaoID = glGenVertexArrays();
        glBindVertexArray(vaoID);
        return vaoID;
    }

    private void storeDataInAttributeList(int attributeNumber, int size, float[] data) {
        int vboID = glGenBuffers();
        vbos.add(vboID);
        glBindBuffer(GL_ARRAY_BUFFER, vboID);
        FloatBuffer buffer = storeDataInFloatBuffer(data);
        glBufferData(GL_ARRAY_BUFFER, buffer, GL_STATIC_DRAW);
        glVertexAttribPointer(attributeNumber, size, GL_FLOAT, false, 0, 0);
        glBindBuffer(GL_ARRAY_BUFFER, 0);
    }

    private void unbindVAO() {
        glBindVertexArray(0);
    }

    private void bindIndicesBuffer(int[] indices) {
        int vboID = glGenBuffers();
        vbos.add(vboID);
        glBindBuffer(GL_ELEMENT_ARRAY_BUFFER, vboID);
        IntBuffer buffer = storeDataInIntBuffer(indices);
        glBufferData(GL_ELEMENT_ARRAY_BUFFER, buffer, GL_STATIC_DRAW);
        glBindBuffer(GL_ARRAY_BUFFER, 0);
    }

    private IntBuffer storeDataInIntBuffer(int[] data) {
        IntBuffer buffer = BufferUtils.createIntBuffer(data.length);
        buffer.put(data);
        buffer.flip();
        return buffer;
    }

    private FloatBuffer storeDataInFloatBuffer(float[] data) {
        FloatBuffer buffer = BufferUtils.createFloatBuffer(data.length);
        buffer.put(data);
        buffer.flip();
        return buffer;
    }

    public void render() {
        glBindVertexArray(vaoID);
        glEnableVertexAttribArray(0);
        glEnableVertexAttribArray(1);
        glEnableVertexAttribArray(2);
        glDrawElements(GL_TRIANGLES, vertexCount, GL_UNSIGNED_INT, 0);
        glDisableVertexAttribArray(0);
        glDisableVertexAttribArray(1);
        glDisableVertexAttribArray(2);
        glBindVertexArray(0);
    } 

    public void deInit() {
        for (int vbo: vbos) {
            glDeleteBuffers(vbo);
        }
        glDeleteVertexArrays(vaoID);
    }
    
}
