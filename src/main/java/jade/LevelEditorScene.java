package jade;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import components.Sprite;
import components.SpriteRenderer;
import components.SpriteSheet;
import imgui.ImGui;
import org.joml.Vector2d;
import org.joml.Vector2f;
import org.joml.Vector4f;
import org.lwjgl.system.CallbackI;
import renderer.Texture;
import util.AssetPool;

import javax.security.auth.kerberos.KerberosTicket;

import static org.lwjgl.glfw.GLFW.*;

public class LevelEditorScene extends Scene {

    private GameObject obj1;
    private SpriteSheet sprites;

    public LevelEditorScene() {

    }

    @Override
    public void init() {
        loadResources();

        this.sprites = AssetPool.getSpriteSheet("assets/images/spritesheet.png");

        this.camera = new Camera(new Vector2f(-250, 0));
        GameObject obj2 = new GameObject("Object 2",
                new Transform(new Vector2f(400, 100), new Vector2f(256, 256)),
                2);
        SpriteRenderer spriteRenderer = new SpriteRenderer();
        Sprite sprite = new Sprite();
        sprite.setTexture(AssetPool.getTexture("assets/images/blendImage2.png"));
        spriteRenderer.setSprite(sprite);
        obj2.addComponent(spriteRenderer);
        this.addGameObjectToScene(obj2);


        this.obj1 = new GameObject("Object 1",
                new Transform(new Vector2f(200, 100), new Vector2f(256, 256)),
                2);
        SpriteRenderer spriteRenderer1 = new SpriteRenderer();
        Vector4f color = new Vector4f(1, 0,1,1);
        spriteRenderer1.setColor(color);
        this.obj1.addComponent(spriteRenderer1);
        this.addGameObjectToScene(obj1);
        this.activeGameObject = obj1;

        Gson gson = new GsonBuilder().setPrettyPrinting().create();
        System.out.println(gson.toJson(sprite));
        System.out.println(gson.toJson(spriteRenderer1));
    }

    private void loadResources() {
        AssetPool.getShader("assets/shaders/default.glsl");

        AssetPool.addSpriteSheet("assets/images/spritesheet.png",
                new SpriteSheet(AssetPool.getTexture("assets/images/spritesheet.png"),
                        16, 16, 26, 0));

        AssetPool.addSpriteSheet("assets/images/1.jpg",
                new SpriteSheet(AssetPool.getTexture("assets/images/1.jpg"),
                        10, 10, 26, 0));
    }

    @Override
    public void update(float dt) {
        /*
        //obj1.getComponent(SpriteRenderer.class).setSprite(sprites.getSprite(0));
        if (KeyListener.isKeyPressed(GLFW_KEY_RIGHT)) {
            if (lastKey != GLFW_KEY_RIGHT) {
                lastKey = GLFW_KEY_RIGHT;
                spriteIndex = 0;
            }
            obj1.transform.position.set(new Vector2f(obj1.transform.position.x + dt * 50f, obj1.transform.position.y));
            if (!KeyListener.isKeyPressed(GLFW_KEY_UP)) {
                if (spriteFlipTimeLeft <= 0) {
                    spriteFlipTimeLeft = spriteFlipTime;
                    spriteIndex++;
                    if (spriteIndex > 3) {
                        spriteIndex = 1;
                    }
                    obj1.getComponent(SpriteRenderer.class).setSprite(sprites.getSprite(spriteIndex));
                } else {
                    spriteFlipTimeLeft -= dt;
                }
            }
        } else if (KeyListener.isKeyPressed(GLFW_KEY_LEFT)) {
            obj1.transform.position.set(new Vector2f(obj1.transform.position.x - dt * 50f, obj1.transform.position.y));
        }
        if (KeyListener.isKeyPressed(GLFW_KEY_UP)) {
            if (lastKey != GLFW_KEY_UP) {
                lastKey = GLFW_KEY_UP;
                spriteIndex = 9;
            }
            obj1.transform.position.set(new Vector2f(obj1.transform.position.x, obj1.transform.position.y + dt * 50f));
            if(spriteFlipTimeLeft <= 0){
                spriteFlipTimeLeft = spriteFlipTime;
                spriteIndex++;
                if (spriteIndex > 13){
                    spriteIndex = 9;
                }
                obj1.getComponent(SpriteRenderer.class).setSprite(sprites.getSprite(spriteIndex));
            }else {
                spriteFlipTimeLeft -= dt;
            }
        } else if (KeyListener.isKeyPressed(GLFW_KEY_DOWN)) {
            obj1.transform.position.set(new Vector2f(obj1.transform.position.x, obj1.transform.position.y - dt * 50f));
        }*/

        for (GameObject go : this.gameObjects) {
            go.update(dt);
        }

        this.renderer.render();
    }

    @Override
    public void imgui(){
        ImGui.begin("Test Window");
        ImGui.text("This is some text");
        ImGui.end();
    }
}