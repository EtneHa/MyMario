package components;

import jade.Component;
import org.joml.Vector2f;
import org.joml.Vector4f;
import renderer.Texture;

public class SpriteRenderer extends Component {
    private Vector4f color;
    private Vector2f position;
    private Vector2f[] textCoords;
    private Texture texture;

    public SpriteRenderer(Vector4f color){
        this.color = color;
        this.texture = null;
    }
    public SpriteRenderer(Texture texture){
        this.texture = texture;
        this.color = new Vector4f(0,0,0,0);
    }

    public void start(){

    }

    public Texture getTexture(){
        return this.texture;
    }

    public Vector2f[] getTextCoords(){
        Vector2f[] texCoords = {
                new Vector2f(1,1),
                new Vector2f(1,0),
                new Vector2f(0,0),
                new Vector2f(0,1)
        };

        return texCoords;
    }

    public Vector4f getColor() {
        return color;
    }

    @Override
    public void update(float dt) {

    }
}