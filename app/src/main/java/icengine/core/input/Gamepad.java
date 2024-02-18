package icengine.core.input;

import static org.lwjgl.glfw.GLFW.glfwGetGamepadState;
import org.lwjgl.glfw.GLFWGamepadState;

public class Gamepad {
    private GLFWGamepadState state;
    private int currentGamepad = 0;
    private static int GamepadCount = 0;
    private boolean present = false;

    public Gamepad() {
        this.state = GLFWGamepadState.create();
        currentGamepad = GamepadCount++;
    }

    public void gamepadPollState() {
        if (glfwGetGamepadState(currentGamepad, state)) {
            present = true;
        } else {
            present = false;
        }
    }

    public boolean isPresent() {
        return present;
    }

    public float getAxis(int axis) {
        return state.axes(axis);
    }

    public int getButton(int button) {
        return state.buttons(button);
    }

}
