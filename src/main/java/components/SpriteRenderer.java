package components;

import jade.Component;
import jade.Transform;
import org.joml.Vector2f;
import org.joml.Vector4f;
import org.lwjgl.system.CallbackI;
import renderer.Texture;

public class SpriteRenderer extends Component {
    private Vector4f color;
    private Sprite sprite;
    private Transform lastTransform;

    private boolean isDirty;

    public SpriteRenderer(Vector4f color){
        this.color = color;
        this.sprite = new Sprite(null);
    }
    public SpriteRenderer(Sprite sprite){
        this.sprite = sprite;
        this.color = new Vector4f(0,0,0,0);
    }

    public void start(){
        this.lastTransform = gameObject.transform.copy();

    }

    @Override
    public void update(float dt) {
        if (!lastTransform.equals(gameObject.transform)){
            this.gameObject.transform.copy(this.lastTransform);
            isDirty = true;
        }
    }

    public Texture getTexture(){
        return sprite.getTexture();
    }

    public Vector2f[] getTextCoords(){
        return sprite.getTexCoords();
    }

    public Vector4f getColor() {
        return color;
    }

    public void setSprite(Sprite sprite){
        this.sprite = sprite;
        isDirty = true;
    }

    public void setColor(Vector4f color){
        if (!this.color.equals(color)) {
            this.color.set(color);
            isDirty = true;
        }
    }

    public boolean isDirty(){
        return this.isDirty;
    }

    public void setClean(){
        this.isDirty = false;
    }
}
