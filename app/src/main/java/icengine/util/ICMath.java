package icengine.util;

public class ICMath {


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
}
