package components;

import imgui.ImGui;
import jade.Component;
import jade.Transform;
import org.joml.Vector2f;
import org.joml.Vector4f;
import org.lwjgl.system.CallbackI;
import renderer.Texture;

public class SpriteRenderer extends Component {
    private Vector4f color = new Vector4f(1,0,0,1);
    private Sprite sprite = new Sprite();
    private transient Transform lastTransform;

    private boolean isDirty;

    public SpriteRenderer(){}

    /*
    public SpriteRenderer(Vector4f color){
        this.color = color;
        isDirty = true;
        this.sprite = new Sprite();
    }

    public SpriteRenderer(Sprite sprite){
        this.sprite = sprite;
        isDirty = true;
        this.color = new Vector4f(0,0,0,0);
    }
     */

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

    @Override
    public void imgui(){
        float[] imColor = {this.color.x, this.color.y, this.color.z, this.color.w};
        ImGui.text("Color Picker");
        if (ImGui.colorPicker4("Color Picker: ", imColor)) {
            this.color.set(imColor);
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
