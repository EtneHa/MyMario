package jade;

import renderer.Renderer;

import java.awt.event.KeyEvent;

public class LevelScene extends Scene{
    private Renderer renderer;

    public LevelScene(){
        System.out.println("Level Scene == 1 !!");

    }

    public void start(){

    }

    @Override
    public void init(){

    }

    @Override
    public void update(float dt) {
        System.out.println("level 2");
        Window.changeScene(0);
    }
}
