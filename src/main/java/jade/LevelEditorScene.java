package jade;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import components.Rigidbody;
import components.Sprite;
import components.SpriteRenderer;
import components.SpriteSheet;
import imgui.ImGui;
import imgui.ImVec2;
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
    SpriteRenderer obj1Sprite;

    public LevelEditorScene() {

    }

    @Override
    public void init() {
        loadResources();
        this.camera = new Camera(new Vector2f(-250, 0));
        sprites = AssetPool.getSpriteSheet("assets/images/spritesheets/decorationsAndBlocks.png");
        if (levelLoaded) {
            this.activeGameObject = gameObjects.get(1);
            return;
        }

        GameObject obj1 = new GameObject("Object 1",
                new Transform(new Vector2f(200, 100), new Vector2f(256, 256)),
                2);
        SpriteRenderer spriteRenderer = new SpriteRenderer();
        Sprite sprite = new Sprite();
        sprite.setTexture(AssetPool.getTexture("assets/images/blendImage2.png"));
        spriteRenderer.setSprite(sprite);
        obj1.addComponent(spriteRenderer);
        obj1.addComponent(new Rigidbody());
        this.addGameObjectToScene(obj1);
        this.activeGameObject = obj1;

        GameObject obj2 = new GameObject("Object 2",
                new Transform(new Vector2f(400, 100), new Vector2f(256, 256)),
                1);
        SpriteRenderer spriteRenderer1 = new SpriteRenderer();
        Vector4f color = new Vector4f(1, 0,1,1);
        spriteRenderer1.setColor(color);
        obj2.addComponent(spriteRenderer1);
        this.addGameObjectToScene(obj2);


    }

    private void loadResources() {
        AssetPool.getShader("assets/shaders/default.glsl");

        AssetPool.addSpriteSheet("assets/images/spritesheet.png",
                new SpriteSheet(AssetPool.getTexture("assets/images/spritesheet.png"),
                        16, 16, 26, 0));

        AssetPool.addSpriteSheet("assets/images/spritesheets/decorationsAndBlocks.png",
                new SpriteSheet(AssetPool.getTexture("assets/images/spritesheets/decorationsAndBlocks.png"),
                        16,16,81,0));

        AssetPool.getTexture("assets/images/blendImage2.png");
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

        MouseListener.getOrthoX();

        for (GameObject go : this.gameObjects) {
            go.update(dt);
        }

        this.renderer.render();
    }

    @Override
    public void imgui(){
        ImGui.begin("Test Window");

        ImVec2 windowPos = new ImVec2();
        ImGui.getWindowPos(windowPos);
        ImVec2 windowSize = new ImVec2();
        ImGui.getWindowSize(windowSize);
        ImVec2 itemSpacing = new ImVec2();
        ImGui.getStyle().getItemSpacing(itemSpacing);

        float windowX2 = windowPos.x + windowSize.x;
        for (int i = 0; i <sprites.size(); i++){
            Sprite sprite = sprites.getSprite(i);
            float spriteWidth = sprite.getWidth() * 4;
            float spriteHeight = sprite.getHeight() * 4;
            int id = sprite.getTexId();
            Vector2f[] texCoords = sprite.getTexCoords();

            ImGui.pushID(i);
            if (ImGui.imageButton(id, spriteWidth, spriteHeight, texCoords[0].x, texCoords[0].y, texCoords[2].x, texCoords[2].y)){
                System.out.println("Button " + i + "clicked");
            }
            ImGui.popID();

            ImVec2 lastButtonPos = new ImVec2();
            ImGui.getItemRectMax(lastButtonPos);
            float lastButtonX2 = lastButtonPos.x;
            float nextButtonX2 = lastButtonX2 + itemSpacing.x + spriteWidth;

            if (i + 1 < sprites.size() && (nextButtonX2 < windowX2)){
                ImGui.sameLine();
            }
        }

        ImGui.end();
    }
}