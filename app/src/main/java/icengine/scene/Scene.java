package icengine.scene;

public abstract class Scene {

    

    public Scene() {

    }
    
    public abstract void init();

    public abstract void deInit();

    public abstract void handleEvents();
    
    public abstract void update(float dt);

    public abstract void render();

}
