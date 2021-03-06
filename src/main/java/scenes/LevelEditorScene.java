package scenes;

import components.*;
import imgui.ImGui;
import imgui.ImVec2;
import jade.*;
import org.joml.Vector2f;
import org.joml.Vector4f;
import scenes.Scene;
import util.AssetPool;

public class LevelEditorScene extends Scene {
    private GameObject obj1;
    private SpriteSheet sprites;
    SpriteRenderer obj1Sprite;

    private MouseControls mouseControls = new MouseControls();

    public LevelEditorScene() {

    }

    @Override
    public void init() {
        loadResources();
        this.camera = new Camera(new Vector2f(-250, 0));
        sprites = AssetPool.getSpriteSheet("assets/images/spritesheets/decorationsAndBlocks.png");
        if (levelLoaded) {
            this.activeGameObject = gameObjects.get(1);
            this.activeGameObject.addComponent(new Rigidbody());
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
        mouseControls.update(dt);

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
                GameObject object = Prefabs.generateSpriteObject(sprite, spriteWidth, spriteHeight);
                mouseControls.pickupObject(object);
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