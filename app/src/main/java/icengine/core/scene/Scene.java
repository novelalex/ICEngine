package icengine.core.scene;

public abstract class Scene {
    public Scene() {

    }
    
    public void init() {
        
    }
    public abstract void update(float dt);

    public abstract void render();

}
