package renderer;

import org.joml.*;
import org.lwjgl.BufferUtils;

import java.io.IOException;
import java.nio.FloatBuffer;
import java.nio.file.Files;
import java.nio.file.Paths;

import static org.lwjgl.opengl.GL20.*;

public class Shader {
    private String vertexShaderSrc, fragmentShaderSrc;
    private int shaderProgramID;
    private boolean beingUsed = false;

    private String filePath;

    public Shader(String filePath){
        this.filePath = filePath;
        try{
            String source = new String(Files.readAllBytes(Paths.get(this.filePath)));
            String[] splitStringShader = source.split("(#type)( )+([a-zA-Z]+)");

            int index = source.indexOf("#type") + 6;
            int temp = source.indexOf("\r\n", index);
            String firstPattern = source.substring(index, temp).trim();

            index = source.indexOf("#type", temp) + 6;
            temp = source.indexOf("\r\n", index);
            String secondPattern = source.substring(index, temp).trim();
            if (firstPattern.equals("vertex")){
                vertexShaderSrc = splitStringShader[1];
            }else if (firstPattern.equals("fragment")){
                vertexShaderSrc = splitStringShader[2];
            }else {
                throw new IOException("Unexpected token" + firstPattern + " ");
            }

            if (secondPattern.equals("vertex")){
                fragmentShaderSrc = splitStringShader[1];
            }else if (secondPattern.equals("fragment")){
                fragmentShaderSrc = splitStringShader[2];
            }else {
                throw new IOException("Unexpected token" + secondPattern + " ");
            }

        }catch (IOException ex){
            ex.printStackTrace();
            assert false: "ERROR: Could not open file for Shader " + this.filePath + "!!!";
        }
    }

    // Compile and linking Shader function
    public void compile(){
        int vertexID, fragmentID;

        // Step 1 load and compile vertex shader
        vertexID = glCreateShader(GL_VERTEX_SHADER);
        // Pass the shader source to the GPU
        glShaderSource(vertexID, this.vertexShaderSrc);
        glCompileShader(vertexID);

        // Check for the vertex shader
        int success = glGetShaderi(vertexID, GL_COMPILE_STATUS);
        if (success == GL_FALSE){
            int len = glGetShaderi(vertexID, GL_INFO_LOG_LENGTH);
            System.out.println("ERROR: "+ this.filePath +"\n\tVertex shader compilation failed");
            System.out.println(glGetShaderInfoLog(vertexID, len));
            assert false: "";
        }

        // Step 2 load an compile fragment shader
        fragmentID = glCreateShader(GL_FRAGMENT_SHADER);
        // Pass this shader to the GPU
        glShaderSource(fragmentID, this.fragmentShaderSrc);
        glCompileShader(fragmentID);

        // Check for the fragment shader
        success = glGetShaderi(fragmentID, GL_COMPILE_STATUS);
        if (success == GL_FALSE){
            int len = glGetShaderi(fragmentID, GL_INFO_LOG_LENGTH);
            System.out.println("ERROR \"+ this.filePath +\"\n\tFragment shader compilation failed");
            System.out.println(glGetShaderInfoLog(fragmentID, len));
            assert false: "";
        }

        // Step 3 linking shaders and check for errors
        this.shaderProgramID = glCreateProgram();
        glAttachShader(this.shaderProgramID, vertexID);
        glAttachShader(this.shaderProgramID, fragmentID);
        glLinkProgram(this.shaderProgramID);

        // Check for errors
        success = glGetProgrami(this.shaderProgramID, GL_LINK_STATUS);
        if (success == GL_FALSE){
            int len = glGetProgrami(this.shaderProgramID, GL_INFO_LOG_LENGTH);
            System.out.println("ERROR Cannot link Vertex shader and Fragment shader");
            System.out.println(glGetProgramInfoLog(this.shaderProgramID, len));
            assert false: "";
        }
    }

    // Run function
    public void use(){
        if (!beingUsed) {
            glUseProgram(shaderProgramID);
            beingUsed = true;
        }
    }

    // Destroy function
    public void detach(){
        glUseProgram(0);
        beingUsed = false;
    }

    public void uploadMat4f(String varName, Matrix4f mat4) {
        int varLocation = glGetUniformLocation(shaderProgramID, varName); // Error here!!!!!!!!!!!!!!!!!!!
        use();
        FloatBuffer matBuffer = BufferUtils.createFloatBuffer(16);
        mat4.get(matBuffer);
        glUniformMatrix4fv(varLocation, false, matBuffer);
    }
    public void uploadVec4f(String varName, Vector4f vec){
        int varLocation = glGetUniformLocation(shaderProgramID, varName);
        use();
        glUniform4f(shaderProgramID, vec.x, vec.y, vec.z, vec.w);
    }
    public void upLoadMat3f(String varName, Matrix3f mat3){
        int varLocation = glGetUniformLocation(shaderProgramID, varName);
        use();
        FloatBuffer matBuffer = BufferUtils.createFloatBuffer(9);
        mat3.get(matBuffer);
        glUniformMatrix3fv(varLocation, false, matBuffer);
    }
    public void uploadVec3f(String varName, Vector3f vec){
        int varLocation = glGetUniformLocation(shaderProgramID, varName);
        use();
        glUniform3f(varLocation, vec.x, vec.y, vec.z);
    }
    public void uploadMat2f(String varName, Matrix2f mat2){
        int varLocation = glGetUniformLocation(shaderProgramID, varName);
        use();
        FloatBuffer matBuffer = BufferUtils.createFloatBuffer(4);
        mat2.get(matBuffer);
        glUniformMatrix2fv(varLocation, false, matBuffer);
    }
    public void uploadVec2f(String varName, Vector2f vec){
        int varLocation = glGetUniformLocation(shaderProgramID, varName);
        use();
        glUniform2f(varLocation, vec.x, vec.y);
    }
    public void uploadFloat(String varName, float val){
        int varLocation = glGetUniformLocation(shaderProgramID, varName);
        use();
        glUniform1f(varLocation, val);
    }
    public void uploadInt(String varName, int val){
        int varLocation = glGetUniformLocation(shaderProgramID, varName);
        use();
        glUniform1i(varLocation, val);
    }
    public void uploadTexture(String varName, int texture){
        int varLocation = glGetUniformLocation(shaderProgramID, varName);
        use();
        glUniform1i(varLocation, texture);
    }

    public void uploadIntArray(String varName, int[] slots){
        int varLocation = glGetUniformLocation(shaderProgramID, varName);
        use();
        //glUniform2iv(varLocation,slots); // Error here!!!!!!!!!!
        glUniform1iv(varLocation,slots);
    }
}
