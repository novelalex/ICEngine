package icengine.core.input;


import org.joml.Vector2f;

import icengine.core.Window;

public class WindowListener {
    private static WindowListener instance;
    private int width, height;
    private boolean isResized = false;

    private WindowListener() {
        Vector2f windowSize = Window.get().getSize();
        this.width = (int) windowSize.x;
        this.height = (int) windowSize.y;
    }

    public static WindowListener get() {
        if (WindowListener.instance == null) {
            WindowListener.instance = new WindowListener();
        }
        return WindowListener.instance;
    }

    public static void windowResizeCallback(long w, int width_, int height_) {
        get().width = width_;
        get().height = height_;
        get().isResized = true;
    }

    public static boolean hasSizeChanged() {
        return get().isResized;
    }

    public static void resetSizeChangedFlag() {
        get().isResized = false;
    }
}
