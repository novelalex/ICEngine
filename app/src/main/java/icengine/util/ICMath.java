package icengine.util;

public class ICMath {
    public static float remapRangef(float value, float fromLow, float fromHigh, float toLow, float toHigh) {
        // First, normalize the value within the original range
        float normalizedValue = (value - fromLow) / (fromHigh - fromLow);
        
        // Then, scale it to the target range
        return toLow + normalizedValue * (toHigh - toLow);
    }
}
