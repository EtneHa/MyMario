package renderer;

import components.SpriteRenderer;
import jade.GameObject;
import jade.Window;
import org.lwjgl.system.CallbackI;
import org.lwjgl.system.windows.WNDCLASSEX;

import java.nio.channels.spi.SelectorProvider;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class Renderer {
    private final int MAX_BATCH_SIZE = 1000;
    private List<RenderBatch> batches;

    public Renderer(){
        this.batches = new ArrayList<>();
    }

    public void add(GameObject go){
        SpriteRenderer spr = go.getComponent(SpriteRenderer.class);
        if (spr != null){
            add(spr);
        }
    }

    public void add(SpriteRenderer spr){
        boolean added = false;
        for (RenderBatch batch : batches){
            if (batch.hasRoom() && (batch.zIndex() == spr.gameObject.zIndex())){
                Texture texture = spr.getTexture();
                if (texture == null || batch.hasTexRoom() || batch.hasTexture(spr.getTexture())) {
                    batch.addSprite(spr);
                    added = true;
                    break;
                }
            }
        }

        if (!added){
            RenderBatch newBatch = new RenderBatch(MAX_BATCH_SIZE, spr.gameObject.zIndex());
            newBatch.start();
            batches.add(newBatch);  // Add new Batch
            newBatch.addSprite(spr);
            Collections.sort(batches);
        }
    }

    public void render(){
        for (RenderBatch batch: batches
             ) {
            batch.render();
        }
    }
}
