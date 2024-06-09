package icengine.core;

import org.joml.Quaternionf;
import org.joml.Vector3f;

public class Body {
    public Quaternionf orientation;
    public Vector3f angularVelocity;
    public Vector3f angularAcceleration;

//    public Quaternionf calculateOrientation(float dt) {
//        angularVelocity.add(angularAcceleration.mul(dt));
//
//    }
}
