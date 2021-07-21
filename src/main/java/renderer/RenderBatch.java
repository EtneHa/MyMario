package renderer;

import components.SpriteRenderer;
import jade.Window;
import org.joml.Vector2f;
import org.joml.Vector4f;
import org.lwjgl.system.macosx.LibSystem;
import util.AssetPool;

import java.util.ArrayList;
import java.util.List;

import static org.lwjgl.opengl.GL15.*;
import static org.lwjgl.opengl.GL20.*;
import static org.lwjgl.opengl.GL30.glBindVertexArray;
import static org.lwjgl.opengl.GL30.glGenVertexArrays;

public class RenderBatch implements Comparable<RenderBatch> {
    // Vertex
    // ========
    // Pos              Color                       texCoords       texID
    // float float      float float float float     float float     float
    private final int POS_SIZE = 2;
    private final int COLOR_SIZE = 4;
    private final int TEX_COORDS_SIZE = 2;
    private final int TEX_ID_SIZE = 1;

    private final int POS_OFFSET = 0;
    private final int COLOR_OFFSET = POS_OFFSET + POS_SIZE * Float.BYTES;
    private final int TEX_COORDS_OFFSET = COLOR_OFFSET + COLOR_SIZE * Float.BYTES; // Error here!!!!!!!!!!
    private final int TEX_ID_OFFSET = TEX_COORDS_OFFSET + TEX_COORDS_SIZE * Float.BYTES;// Error here!!!!!!!!!!

    private final int VERTEX_SIZE = 9;
    private final int VERTEX_SIZE_BYTES = VERTEX_SIZE * Float.BYTES;

    private List<Texture> textures;
    private SpriteRenderer[] sprites;
    private int numSprites;
    private boolean hasRoom;
    private float[] vertices;
    private int[] texSlots = {
            0, 1, 2, 3, 4, 5, 6, 7
    };

    private int vaoID, vboID;
    private int maxBatchSize;
    private Shader shader;

    private int zIndex;

    public RenderBatch(int maxBatchSize, int zIndex) {
        this.maxBatchSize = maxBatchSize;
        this.zIndex = zIndex;
        shader = AssetPool.getShader("assets/shaders/default.glsl");
        shader.compile();
        this.sprites = new SpriteRenderer[maxBatchSize];

        // 4 vertices quads
        vertices = new float[maxBatchSize * VERTEX_SIZE * 4];

        this.textures = new ArrayList<>();
        this.numSprites = 0;
        this.hasRoom = true;
    }

    public void start(){
        // Generate and bind a Vertex Array Object
        vaoID = glGenVertexArrays();
        glBindVertexArray(vaoID);

        // Allocate space for vertices
        vboID = glGenBuffers();
        glBindBuffer(GL_ARRAY_BUFFER, vboID);
        glBufferData(GL_ARRAY_BUFFER, (long) vertices.length * Float.BYTES, GL_DYNAMIC_DRAW);

        // Create and upload indices buffer
        int eboID = glGenBuffers();
        int[] indices = generateIndices();
        glBindBuffer(GL_ELEMENT_ARRAY_BUFFER, eboID);
        glBufferData(GL_ELEMENT_ARRAY_BUFFER, indices, GL_STATIC_DRAW);

        // Enable the buffer attribute pointers
        glVertexAttribPointer(0, POS_SIZE, GL_FLOAT, false, VERTEX_SIZE_BYTES, POS_OFFSET);
        glVertexAttribPointer(1, COLOR_SIZE, GL_FLOAT, false, VERTEX_SIZE_BYTES, COLOR_OFFSET);
        glVertexAttribPointer(2, TEX_COORDS_SIZE, GL_FLOAT, false,VERTEX_SIZE_BYTES, TEX_COORDS_OFFSET);
        glVertexAttribPointer(3, TEX_ID_SIZE, GL_FLOAT, false, VERTEX_SIZE_BYTES, TEX_ID_OFFSET);

        glEnableVertexAttribArray(0);
        glEnableVertexAttribArray(1);
        glEnableVertexAttribArray(2);
        glEnableVertexAttribArray(3);
    }

    public void addSprite(SpriteRenderer spr){
        // Get index and add renderObject
        int index = this.numSprites;
        this.sprites[index] = spr;
        this.numSprites++;

        if (spr.getTexture() != null){
            if (!textures.contains(spr.getTexture())){
                textures.add(spr.getTexture());
            }
        }

        // Add properties to local vertices array
        loadVertexProperties(index);

        if (this.numSprites >= this.maxBatchSize) {
            this.hasRoom = false;

        }
    }

    public void render(){
        boolean isRebuffer = false;
        for (int i = 0; i < numSprites; i++){
            if (sprites[i].isDirty()){
                loadVertexProperties(i);
                sprites[i].setClean();
                isRebuffer = true;
            }
        }
        if (isRebuffer) {
            // Rebuffer frame
            glBindBuffer(GL_ARRAY_BUFFER, vboID);
            glBufferSubData(GL_ARRAY_BUFFER, 0, vertices);
        }

        // Use shader
        shader.use();
        shader.uploadMat4f("uProjection", Window.getScene().getCamera().getProjectionMatrix());
        shader.uploadMat4f("uView", Window.getScene().getCamera().getViewMatrix());

        shader.uploadIntArray("uTextures", texSlots);
        for (int i = 0; i < textures.size(); i++){
            glActiveTexture(GL_TEXTURE0 + i + 1);
            textures.get(i).bind();
        }

        glBindVertexArray(vaoID);
        glEnableVertexAttribArray(0);
        glEnableVertexAttribArray(1);

        glDrawElements(GL_TRIANGLES, this.numSprites * 6, GL_UNSIGNED_INT, 0);

        glDisableVertexAttribArray(0);
        glDisableVertexAttribArray(1);
        glBindVertexArray(0);

        for (int i = 0; i < textures.size(); i++){
            textures.get(i).unBind();
        }

        shader.detach();
    }

    public void loadVertexProperties(int index){
        SpriteRenderer spr = this.sprites[index];

        // Find the offset within array (4 vertices per sprite)
        int offset =  index * 4 * VERTEX_SIZE;

        Vector4f color = spr.getColor();
        Vector2f[] texCoords = spr.getTexCoords();

        int texID = 0;
        // [0, tex, tex, tex, ...]
        if (spr.getTexture() != null) {
            for (int i = 0; i < textures.size(); i++) {
                if (textures.get(i) == spr.getTexture()) {
                    texID = i + 1;
                    break;
                }
            }
        }

        // Add vertice with the appropriate properties
        float xAdd = 1.0f;
        float yAdd = 1.0f;
        for (int i = 0; i < 4; i++){
            if (i == 1){
                yAdd = 0.0f;
            }else if (i == 2){
                xAdd = 0.0f;
            }else if (i == 3){
                yAdd = 1.0f;
            }

            // Load position
            vertices[offset] = spr.gameObject.transform.position.x + (xAdd * spr.gameObject.transform.scale.x);
            vertices[offset + 1] = spr.gameObject.transform.position.y + (yAdd * spr.gameObject.transform.scale.y);

            // Load color
            vertices[offset + 2] = color.x;
            vertices[offset + 3] = color.y;
            vertices[offset + 4] = color.z;
            vertices[offset + 5] = color.w;

            // Load texCoords
            vertices[offset + 6] = texCoords[i].x;
            vertices[offset + 7] = texCoords[i].y;

            // Load texID
            vertices[offset + 8] = (float) texID;

            offset += VERTEX_SIZE;
        }
    }

    public int[] generateIndices(){
        // 6 indices per quad (3 per triangle)
        int[] elements = new int[6 * this.maxBatchSize];
        for (int i = 0; i < this.maxBatchSize; i++){
            loadElementIndices(elements, i);
        }

        return elements;
    }

    private void loadElementIndices(int[] elements, int index){
        int offsetArrayIndex = 6 * index;
        int offset = 4 * index;

        // 3, 2, 0, 0, 2, 1         7, 6, 4, 4, 6, 5
        // Triangle 1
        elements[offsetArrayIndex + 0] = offset + 3;
        elements[offsetArrayIndex + 1] = offset + 2;
        elements[offsetArrayIndex + 2] = offset + 0;
        // Triangle 2
        elements[offsetArrayIndex + 3] = offset + 0;
        elements[offsetArrayIndex + 4] = offset + 2;
        elements[offsetArrayIndex + 5] = offset + 1;
    }

    public boolean hasRoom(){
        return this.hasRoom;
    }

    public boolean hasTexRoom(){
        return this.textures.size() < 8;
    }

    public boolean hasTexture(Texture tex){
        return this.textures.contains(tex);
    }

    public int zIndex(){
        return this.zIndex;
    }

    @Override
    public int compareTo(RenderBatch o) {
        return Integer.compare(this.zIndex, o.zIndex);
    }
}
