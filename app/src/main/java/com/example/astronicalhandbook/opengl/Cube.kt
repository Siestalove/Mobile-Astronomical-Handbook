package com.example.astronicalhandbook.opengl

import android.opengl.GLES20
import java.nio.ByteBuffer
import java.nio.ByteOrder
import java.nio.FloatBuffer
import java.nio.ShortBuffer

class Cube {

    private val vertexBuffer: FloatBuffer
    private val indexBuffer: ShortBuffer
    private val indexCount: Int

    private val program: Int

    private val aPosition: Int
    private val uMVPMatrix: Int
    private val uColor: Int

    init {
        val vertices = floatArrayOf(
            // Front
            -0.5f, -0.5f,  0.5f,
            0.5f, -0.5f,  0.5f,
            0.5f,  0.5f,  0.5f,
            -0.5f,  0.5f,  0.5f,

            // Back
            -0.5f, -0.5f, -0.5f,
            0.5f, -0.5f, -0.5f,
            0.5f,  0.5f, -0.5f,
            -0.5f,  0.5f, -0.5f
        )

        val indices = shortArrayOf(
            // Front
            0, 1, 2, 0, 2, 3,
            // Right
            1, 5, 6, 1, 6, 2,
            // Back
            5, 4, 7, 5, 7, 6,
            // Left
            4, 0, 3, 4, 3, 7,
            // Top
            3, 2, 6, 3, 6, 7,
            // Bottom
            4, 5, 1, 4, 1, 0
        )

        indexCount = indices.size

        vertexBuffer = ByteBuffer.allocateDirect(vertices.size * 4)
            .order(ByteOrder.nativeOrder())
            .asFloatBuffer()
            .apply {
                put(vertices)
                position(0)
            }

        indexBuffer = ByteBuffer.allocateDirect(indices.size * 2)
            .order(ByteOrder.nativeOrder())
            .asShortBuffer()
            .apply {
                put(indices)
                position(0)
            }

        val vertexShader = ShaderUtils.loadShader(
            GLES20.GL_VERTEX_SHADER, VERTEX_SHADER
        )
        val fragmentShader = ShaderUtils.loadShader(
            GLES20.GL_FRAGMENT_SHADER, FRAGMENT_SHADER
        )

        program = GLES20.glCreateProgram().also {
            GLES20.glAttachShader(it, vertexShader)
            GLES20.glAttachShader(it, fragmentShader)
            GLES20.glLinkProgram(it)

            val linkStatus = IntArray(1)
            GLES20.glGetProgramiv(it, GLES20.GL_LINK_STATUS, linkStatus, 0)
            if (linkStatus[0] == 0) {
                val log = GLES20.glGetProgramInfoLog(it)
                GLES20.glDeleteProgram(it)
                throw RuntimeException("Failed to link cube program: $log")
            }
        }


        aPosition = GLES20.glGetAttribLocation(program, "aPosition")
        uMVPMatrix = GLES20.glGetUniformLocation(program, "uMVPMatrix")
        uColor = GLES20.glGetUniformLocation(program, "uColor")
    }

    fun draw(
        mvpMatrix: FloatArray,
        color: FloatArray
    ) {
        GLES20.glUseProgram(program)

        GLES20.glEnableVertexAttribArray(aPosition)
        GLES20.glVertexAttribPointer(
            aPosition,
            3,
            GLES20.GL_FLOAT,
            false,
            0,
            vertexBuffer
        )

        GLES20.glUniformMatrix4fv(uMVPMatrix, 1, false, mvpMatrix, 0)
        GLES20.glUniform4fv(uColor, 1, color, 0)

        GLES20.glDrawElements(
            GLES20.GL_TRIANGLES,
            indexCount,
            GLES20.GL_UNSIGNED_SHORT,
            indexBuffer
        )

        GLES20.glDisableVertexAttribArray(aPosition)
    }

    companion object {
        private const val VERTEX_SHADER = """
            uniform mat4 uMVPMatrix;
            attribute vec3 aPosition;

            void main() {
                gl_Position = uMVPMatrix * vec4(aPosition, 1.0);
            }
        """

        private const val FRAGMENT_SHADER = """
            precision mediump float;
            uniform vec4 uColor;

            void main() {
                gl_FragColor = uColor;
            }
        """
    }
}
