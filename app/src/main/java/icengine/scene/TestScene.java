package icengine.scene;

import icengine.core.renderer.model.Loader;
import icengine.core.renderer.model.RawModel;
import icengine.core.renderer.model.Renderer;

public class TestScene extends Scene{

    private Loader loader;
    private Renderer renderer;
    private RawModel model;

    private float[] vertices = {
        -0.5f, 0.5f, 0f,    // TL
        -0.5f, -0.5f, 0f,   // BL
        0.5f, -0.5f, 0f,    // BR
        0.5f, 0.5f, 0f,     // TR
      };
    
    private int[] indices = {
        0, 1, 3,    // TL, BL, TR
        3, 1, 2     // TR, BL, BR
    };
    public TestScene() {
        
    }

    @Override
    public void init() {
        loader = new Loader();
        model = loader.loadToVAO(vertices, indices);
        renderer = new Renderer();
    }

    @Override
    public void deInit() {
        loader.cleanUp();
    }

    @Override
    public void update(float dt) {
        
    }

    @Override
    public void render() {
        renderer.prepare();
        renderer.render(model);
    }
    
}
