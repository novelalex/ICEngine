package icengine.core.input;

import static org.lwjgl.glfw.GLFW.GLFW_MOUSE_BUTTON_1;
import static org.lwjgl.opengl.GL11.GL_VIEWPORT;
import static org.lwjgl.opengl.GL11.glGetIntegerv;

import org.checkerframework.checker.units.qual.m;
import org.joml.Matrix4f;
import org.joml.Quaternionf;
import org.joml.Vector3f;
import org.lwjgl.opengl.GL;

import icengine.util.ICMath;

public class Trackball {
    private Quaternionf mouseRotationQuat;
    private Quaternionf prevQuat;
    private Matrix4f invNDC;
    private Vector3f beginV, endV;  
    private boolean mouseDown = false;

    public Trackball() {
        mouseRotationQuat = new Quaternionf();
        prevQuat = new Quaternionf();
        setWindowDimentions();
    }

    public void setWindowDimentions() {
        int viewport[] = new int[4];
        glGetIntegerv(GL_VIEWPORT, viewport);
        invNDC = ICMath.viewportNDC(viewport[2], viewport[3]).invert();
    }

    public void handleEvents() {
        if (MouseListener.mouseButtonDown(GLFW_MOUSE_BUTTON_1) && !MouseListener.isDragging()) {
            //System.out.println("Mouse down");
            onLeftMouseDown();
        } 
        if (!MouseListener.mouseButtonDown(GLFW_MOUSE_BUTTON_1)) {
            //System.out.println("Mouse not down");
            onLeftMouseUp();
        }
        if (MouseListener.isDragging()) {
            onMouseMove();
        } 
        if (WindowListener.hasSizeChanged()) {
            setWindowDimentions();
        }
    }   

    private Vector3f getMouseVector(double x, double y) {
        Vector3f mousePos = new Vector3f((float) x, (float) y, 0.0f);
        //System.out.println(mousePos.x + " " + mousePos.y + " " + mousePos.z);
        Vector3f v = new Vector3f();
        mousePos.mulDirection(invNDC, v);
        float xSquared = v.x * v.x;
        float ySquared = v.y * v.y;
        if (xSquared + ySquared <= 0.5f) { /// see reference (1.0f*1.0f / 2.0f) 1.0 is the radius of the sphere
            /// if it's the sphere
            v.z = (float) Math.sqrt(1.0f - (xSquared + ySquared));
        }
        else {
            /// else it's the hyperbolic sheet
            v.z = 0.5f / (float) Math.sqrt(xSquared + ySquared);
            v.normalize();
        }
        return v;
    }

    private void onLeftMouseDown() {
        beginV = getMouseVector(MouseListener.getX(), MouseListener.getY());
        //System.out.println(beginV.x + " " + beginV.y + " " + beginV.z);
        prevQuat = new Quaternionf(mouseRotationQuat);
        mouseDown = true;
    }

    private void onLeftMouseUp() {
        mouseDown = false;
    }

    private void onMouseMove() {
        if (mouseDown == false) {
            //System.out.println("Mouse not down");
            return;
        }

        endV = getMouseVector(MouseListener.getX(), MouseListener.getY());
        //System.out.println("BEGIN: " + beginV.x + " " + beginV.y + " " + beginV.z);
        //System.out.println("END: " + endV.x + " " + endV.y + " " + endV.z);
        float cosAngle = beginV.dot(endV);
        //System.out.println(cosAngle);
        float angle = (float) Math.acos(cosAngle);
        Vector3f axis = new Vector3f();
        beginV.cross(endV, axis);
        Quaternionf delta = new Quaternionf().fromAxisAngleRad(axis, angle);
        mouseRotationQuat = new Quaternionf(prevQuat).mul(delta);
    }

    public Quaternionf getQuat() {
        return new Quaternionf(mouseRotationQuat);
    }
}
