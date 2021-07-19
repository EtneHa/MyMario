package renderer;

import org.lwjgl.BufferUtils;
import org.lwjgl.opengl.GL;

import java.nio.Buffer;
import java.nio.ByteBuffer;
import java.nio.IntBuffer;

import static org.lwjgl.opengl.GL11.*;
import static org.lwjgl.stb.STBImage.*;

public class Texture {
    private String filepath;
    private int texID;

    public Texture(String filepath){
        this.filepath = filepath;

        // Generate texture on GPU
        this.texID = glGenTextures();
        glBindTexture(GL_TEXTURE_2D, texID); // Error here!!!!!!!!!!!!!!!!!!!

        // Set texture parameters
        // Repeat image in both directions
        glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_WRAP_S, GL_REPEAT);
        glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_WRAP_T, GL_REPEAT);
        // When stretching the image, pixelate
        glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_MIN_FILTER, GL_NEAREST);
        // When shrinking an image, pixelate
        glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_MAG_FILTER, GL_NEAREST);

        IntBuffer width = BufferUtils.createIntBuffer(1);
        IntBuffer height = BufferUtils.createIntBuffer(1);
        IntBuffer channels = BufferUtils.createIntBuffer(1);

        ByteBuffer image = stbi_load(filepath, width, height, channels, 0);

        if (image != null){
            if (channels.get(0) == 4){
                glTexImage2D(GL_TEXTURE_2D, 0, GL_RGBA, width.get(0), height.get(0),
                        0, GL_RGBA, GL_UNSIGNED_BYTE, image);
            }else if (channels.get(0) == 3){
                glTexImage2D(GL_TEXTURE_2D, 0, GL_RGB, width.get(0), height.get(0),
                        0, GL_RGB, GL_UNSIGNED_BYTE, image);
            }else {
                assert false: "Error: (Texture) Unknown channels filepath = '" + this.filepath + "' channels = '" + channels.get(0) + "'";
            }
        }else
            assert false: "Error: (Texture) Could not open filepath = '" + this.filepath + "'";

        stbi_set_flip_vertically_on_load(true);
        stbi_image_free(image);
    }

    public void bind(){
        glBindTexture(GL_TEXTURE_2D, this.texID);
    }

    public void unBind(){
        glBindTexture(GL_TEXTURE_2D, 0);
    }


}