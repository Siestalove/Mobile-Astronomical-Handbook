package com.example.astronicalhandbook.opengl

import android.opengl.GLES20
import java.nio.ByteBuffer
import java.nio.ByteOrder
import java.nio.FloatBuffer
import java.nio.ShortBuffer

class Square {
    private val vertexShaderCode =
        "uniform mat4 uMVPMatrix;" +
        "attribute vec4 vPosition;" +
        "attribute vec2 a_TexCoordinate;" +
        "varying vec2 v_TexCoordinate;" +
        "void main() {" +
        "  gl_Position = uMVPMatrix * vPosition;" +
        "  v_TexCoordinate = a_TexCoordinate;" +
        "}"

    private val fragmentShaderCode =
        "precision mediump float;" +
        "uniform sampler2D u_Texture;" +
        "varying vec2 v_TexCoordinate;" +
        "void main() {" +
        "  gl_FragColor = texture2D(u_Texture, v_TexCoordinate);" +
        "}"

    private var vertexBuffer: FloatBuffer
    private var drawListBuffer: ShortBuffer
    private var textureBuffer: FloatBuffer
    private var mProgram: Int

    private val COORDS_PER_VERTEX = 3
    private var squareCoords = floatArrayOf(
        -1.0f,  1.0f, 0.0f,   
        -1.0f, -1.0f, 0.0f,   
         1.0f, -1.0f, 0.0f,   
         1.0f,  1.0f, 0.0f    
    )

    private val drawOrder = shortArrayOf(0, 1, 2, 0, 2, 3) 

    private val vertexStride = COORDS_PER_VERTEX * 4 

    private val textureCoords = floatArrayOf(
        0.0f, 0.0f, 
        0.0f, 1.0f, 
        1.0f, 1.0f, 
        1.0f, 0.0f  
    )

    init {
        val bb = ByteBuffer.allocateDirect(squareCoords.size * 4)
        bb.order(ByteOrder.nativeOrder())
        vertexBuffer = bb.asFloatBuffer()
        vertexBuffer.put(squareCoords)
        vertexBuffer.position(0)

        val dlb = ByteBuffer.allocateDirect(drawOrder.size * 2)
        dlb.order(ByteOrder.nativeOrder())
        drawListBuffer = dlb.asShortBuffer()
        drawListBuffer.put(drawOrder)
        drawListBuffer.position(0)

        val tb = ByteBuffer.allocateDirect(textureCoords.size * 4)
        tb.order(ByteOrder.nativeOrder())
        textureBuffer = tb.asFloatBuffer()
        textureBuffer.put(textureCoords)
        textureBuffer.position(0)

        val vertexShader = ShaderUtils.loadShader(GLES20.GL_VERTEX_SHADER, vertexShaderCode)
        val fragmentShader = ShaderUtils.loadShader(GLES20.GL_FRAGMENT_SHADER, fragmentShaderCode)

        mProgram = GLES20.glCreateProgram().also {
            GLES20.glAttachShader(it, vertexShader)
            GLES20.glAttachShader(it, fragmentShader)
            GLES20.glLinkProgram(it)
        }
    }

    fun draw(textureId: Int, mvpMatrix: FloatArray) {
        GLES20.glUseProgram(mProgram)
        
        val mvpMatrixHandle = GLES20.glGetUniformLocation(mProgram, "uMVPMatrix")
        GLES20.glUniformMatrix4fv(mvpMatrixHandle, 1, false, mvpMatrix, 0)

        val positionHandle = GLES20.glGetAttribLocation(mProgram, "vPosition")
        GLES20.glEnableVertexAttribArray(positionHandle)
        GLES20.glVertexAttribPointer(
            positionHandle, COORDS_PER_VERTEX,
            GLES20.GL_FLOAT, false,
            vertexStride, vertexBuffer
        )

        val texCoordHandle = GLES20.glGetAttribLocation(mProgram, "a_TexCoordinate")
        GLES20.glEnableVertexAttribArray(texCoordHandle)
        GLES20.glVertexAttribPointer(
            texCoordHandle, 2,
            GLES20.GL_FLOAT, false,
            2 * 4, textureBuffer 
        )

        GLES20.glActiveTexture(GLES20.GL_TEXTURE0)
        GLES20.glBindTexture(GLES20.GL_TEXTURE_2D, textureId)
        val textureUniformHandle = GLES20.glGetUniformLocation(mProgram, "u_Texture")
        GLES20.glUniform1i(textureUniformHandle, 0)
        GLES20.glDrawElements(
            GLES20.GL_TRIANGLES, drawOrder.size,
            GLES20.GL_UNSIGNED_SHORT, drawListBuffer
        )

        GLES20.glDisableVertexAttribArray(positionHandle)
        GLES20.glDisableVertexAttribArray(texCoordHandle)
    }
}
