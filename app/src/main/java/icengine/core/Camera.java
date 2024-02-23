package icengine.core;

import org.joml.Matrix4f;
import org.joml.Quaternionf;
import org.joml.Vector3f;

public class Camera {
    private Matrix4f projectionMatrix, viewMatrix;
    private Vector3f position;
    public Vector3f forward = new Vector3f(0, 0, -1);
    public Quaternionf orientation = new Quaternionf();

    public Camera(Vector3f position, Matrix4f proj) {
        // Hide the fact that the camera is actually at the origin and the world is moved around it
        this.position = position.negate();
        this.projectionMatrix = new Matrix4f();
        this.viewMatrix = new Matrix4f();
        adjustProjection(proj);
    }

    public void adjustProjection(Matrix4f proj) {
        projectionMatrix.set(proj);
            // .identity()
            // //.ortho(0.0f, 32.0f * 40.0f, 0.0f, 32.0f * 21.0f, 0.0f, 100.0f);
            // .perspective(45.0f, (16.0f / 9.0f),  0.5f, 1000.0f);
    }

    public Matrix4f getViewMatrix() {
        viewMatrix
            .identity()
            .translate(position)
            .rotate(orientation);
        return viewMatrix;
    }

    public Matrix4f getProjectionMatrix() {
        return projectionMatrix;
    }

    public Vector3f getForward() {
        forward.set(0, 0, -1);
        orientation.transform(forward);
        forward.normalize();
        return forward;
    }

    public void setPosition(Vector3f position) {
        this.position = position.negate();
    }

    public Vector3f getPosition() {
        return position.negate();
    }

    public void setOrientation(Quaternionf orientation) {
        this.orientation = orientation;
    }

    public void move(Vector3f direction, float amount) {
        // Hide the fact that the camera is actually at the origin and the world is moved around it
        position.add(((Vector3f)direction).negate().mul(amount));
    }

    
}
