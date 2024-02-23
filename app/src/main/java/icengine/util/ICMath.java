package icengine.util;

import org.joml.Matrix4f;
import org.joml.Vector3f;

public class ICMath {

    public static final float SMOL = 1.0e-7f;

    // Constant direction vectors to make life easier
    public static final float[] UP = {0.0f, 1.0f, 0.0f};
    public static final float[] DOWN = {0.0f, -1.0f, 0.0f};
    public static final float[] LEFT = {-1.0f, 0.0f, 0.0f};
    public static final float[] RIGHT = {1.0f, 0.0f, 0.0f};
    public static final float[] FORWARD = {0.0f, 0.0f, -1.0f};
    public static final float[] BACKWARD = {0.0f, 0.0f, 1.0f};

    public static final float[] X_AXIS = {1.0f, 0.0f, 0.0f};
    public static final float[] Y_AXIS = {0.0f, 1.0f, 0.0f};
    public static final float[] Z_AXIS = {0.0f, 0.0f, 1.0f};
    

    public static float remapRangef(float value, float fromLow, float fromHigh, float toLow, float toHigh) {
        float normalizedValue = (value - fromLow) / (fromHigh - fromLow);
        return toLow + normalizedValue * (toHigh - toLow);
    }


    // this is just scotts code
    public static Matrix4f viewportNDC(int width, int height) {
        float minZ = -1.0f;
        float maxZ = 1.0f;
    
        Matrix4f m = new Matrix4f()
        .scale(1.0f, -1.0f, 1.0f)
        .scale((float)width/ 2.0f, (float)height / 2.0f, maxZ - minZ)
        .translate(new Vector3f((float)width/ 2.0f, (float)height/ 2.0f, minZ));
        //m = m3.mul(m2.mul(m1));
        return m;
    }
}
