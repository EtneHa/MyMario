package jade;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class GameObject {
    private String name;
    private List<Component> components;
    public Transform transform;

    public GameObject(String name){
        this.name = name;
        components = new ArrayList<>();
        transform = new Transform();
    }
    public GameObject(String name, Transform transform){
        this.name = name;
        components = new ArrayList<>();
        this.transform = transform;
    }

    public <T extends Component> T getComponent(Class<T> tClass){
        for (Component c:
             components) {
            if (tClass.isAssignableFrom(c.getClass())){
                try {
                    return tClass.cast(c);
                }catch (ClassCastException exc){
                    exc.printStackTrace();
                    assert false:"Error: (GameObject) failed to get Component '" + tClass.getClass() + "'";
                }
            }
        }
        return null;
    }

    public <T extends Component> void removeComponent(Class<T> tClass){
        for (int i = 0; i < components.size(); i++){
            Component c = components.get(i);
            if (tClass.isAssignableFrom(c.getClass())){
                components.remove(c);
                return;
            }
        }
    }

    public void addComponent(Component c){
        this.components.add(c);
        c.gameObject = this;
    }

    public void update (float dt){
        for (int i = 0; i < components.size(); i++){
            components.get(i).update(dt);
        }
    }

    public void start(){
        for (int i = 0; i < components.size(); i++){
            components.get(i).start();
        }
    }
}
