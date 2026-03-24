package com.example.astronicalhandbook.opengl

import android.content.Context
import android.opengl.GLES20
import com.example.astronicalhandbook.R
import java.nio.ByteBuffer
import java.nio.ByteOrder
import java.nio.FloatBuffer

class BlackHoleSprite(context: Context) {

    private val vertexBuffer: FloatBuffer
    private val texBuffer: FloatBuffer
    private val program: Int
    private val textureId: Int

    private val aPos: Int
    private val aTexCoord: Int
    private val uCenter: Int
    private val uHalfSize: Int
    private val uTexture: Int

    init {
        val verts = floatArrayOf(
            -1f, -1f,
             1f, -1f,
            -1f,  1f,
             1f,  1f
        )
        val tex = floatArrayOf(
            0f, 1f,
            1f, 1f,
            0f, 0f,
            1f, 0f
        )

        vertexBuffer = ByteBuffer.allocateDirect(verts.size * 4)
            .order(ByteOrder.nativeOrder()).asFloatBuffer()
            .apply { put(verts); position(0) }

        texBuffer = ByteBuffer.allocateDirect(tex.size * 4)
            .order(ByteOrder.nativeOrder()).asFloatBuffer()
            .apply { put(tex); position(0) }

        val vs = ShaderUtils.loadShader(GLES20.GL_VERTEX_SHADER, VERTEX_SHADER)
        val fs = ShaderUtils.loadShader(GLES20.GL_FRAGMENT_SHADER, FRAGMENT_SHADER)
        program = GLES20.glCreateProgram().also {
            GLES20.glAttachShader(it, vs)
            GLES20.glAttachShader(it, fs)
            GLES20.glLinkProgram(it)
        }

        aPos      = GLES20.glGetAttribLocation(program,  "aPos")
        aTexCoord = GLES20.glGetAttribLocation(program,  "aTexCoord")
        uCenter   = GLES20.glGetUniformLocation(program, "uCenter")
        uHalfSize = GLES20.glGetUniformLocation(program, "uHalfSize")
        uTexture  = GLES20.glGetUniformLocation(program, "uTexture")

        textureId = ShaderUtils.loadTexture(context, R.drawable.black_hole)
    }

    fun draw(cx: Float, cy: Float, sizePx: Float, screenW: Int, screenH: Int) {
        GLES20.glBlendFunc(GLES20.GL_ONE, GLES20.GL_ONE_MINUS_SRC_ALPHA)

        GLES20.glUseProgram(program)

        GLES20.glEnableVertexAttribArray(aPos)
        GLES20.glVertexAttribPointer(aPos, 2, GLES20.GL_FLOAT, false, 0, vertexBuffer)

        GLES20.glEnableVertexAttribArray(aTexCoord)
        GLES20.glVertexAttribPointer(aTexCoord, 2, GLES20.GL_FLOAT, false, 0, texBuffer)

        val ndcX = 2f * cx / screenW - 1f
        val ndcY = 1f - 2f * cy / screenH
        GLES20.glUniform2f(uCenter, ndcX, ndcY)
        GLES20.glUniform2f(uHalfSize, sizePx / screenW, sizePx / screenH)

        GLES20.glActiveTexture(GLES20.GL_TEXTURE0)
        GLES20.glBindTexture(GLES20.GL_TEXTURE_2D, textureId)
        GLES20.glUniform1i(uTexture, 0)

        GLES20.glDrawArrays(GLES20.GL_TRIANGLE_STRIP, 0, 4)

        GLES20.glDisableVertexAttribArray(aPos)
        GLES20.glDisableVertexAttribArray(aTexCoord)
    }

    companion object {
        private const val VERTEX_SHADER = """
attribute vec2 aPos;
attribute vec2 aTexCoord;
uniform vec2 uCenter;
uniform vec2 uHalfSize;
varying vec2 vTex;
void main() {
    gl_Position = vec4(uCenter + aPos * uHalfSize, 0.0, 1.0);
    vTex = aTexCoord;
}"""

        private const val FRAGMENT_SHADER = """
precision mediump float;
uniform sampler2D uTexture;
varying vec2 vTex;
void main() {
    vec4 color = texture2D(uTexture, vTex);
    gl_FragColor = vec4(color.rgb * color.a, color.a);
}"""
    }
}
