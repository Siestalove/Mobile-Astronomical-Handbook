package com.example.astronicalhandbook.opengl

import android.opengl.GLES20
import java.nio.ByteBuffer
import java.nio.ByteOrder
import java.nio.FloatBuffer
import java.nio.ShortBuffer
import kotlin.math.cos
import kotlin.math.sin

class Sphere(
    private val radius: Float = 0.5f,
    private val stacks: Int = 20,
    private val slices: Int = 20,
    private val color: FloatArray = floatArrayOf(1f, 1f, 1f, 1f),
    private val textureId: Int
) {
    private val vertexShaderCode = """
        uniform mat4 uMVPMatrix;
        attribute vec4 vPosition;
        attribute vec2 aTexCoordinate;
        varying vec2 vTexCoordinate;
        void main() {
            gl_Position = uMVPMatrix * vPosition;
            vTexCoordinate = aTexCoordinate;
        }
    """.trimIndent()

    private val fragmentShaderCode = """
        precision mediump float;
        uniform vec4 uColor;
        uniform sampler2D uTexture;
        varying vec2 vTexCoordinate;
        void main() {
            gl_FragColor = texture2D(uTexture, vTexCoordinate); 
        }
    """.trimIndent()

    private var vertexBuffer: FloatBuffer
    private var indexBuffer: ShortBuffer
    private var mProgram: Int
    private var indexCount: Int = 0

    init {
        val vertices = mutableListOf<Float>()
        val indices = mutableListOf<Short>()
        
        // Vertices (x, y, z, u, v)
        // stride = 5 * 4 bytes
        
        for (i in 0..stacks) {
            val phi = Math.PI * i / stacks
            for (j in 0..slices) {
                val theta = 2 * Math.PI * j / slices
                val x = (radius * sin(phi) * cos(theta)).toFloat()
                val y = (radius * cos(phi)).toFloat()
                val z = (radius * sin(phi) * sin(theta)).toFloat()
                
                val u = 1.0f - (j.toFloat() / slices)
                val v = i.toFloat() / stacks
                
                vertices.add(x)
                vertices.add(y)
                vertices.add(z)
                vertices.add(u)
                vertices.add(v)
            }
        }

        for (i in 0 until stacks) {
            for (j in 0 until slices) {
                val first = (i * (slices + 1) + j).toShort()
                val second = (first + slices + 1).toShort()
                indices.add(first)
                indices.add(second)
                indices.add((first + 1).toShort())
                indices.add(second)
                indices.add((second + 1).toShort())
                indices.add((first + 1).toShort())
            }
        }

        indexCount = indices.size

        val vb = ByteBuffer.allocateDirect(vertices.size * 4)
        vb.order(ByteOrder.nativeOrder())
        vertexBuffer = vb.asFloatBuffer()
        vertexBuffer.put(vertices.toFloatArray())
        vertexBuffer.position(0)

        val ib = ByteBuffer.allocateDirect(indices.size * 2)
        ib.order(ByteOrder.nativeOrder())
        indexBuffer = ib.asShortBuffer()
        indexBuffer.put(indices.toShortArray())
        indexBuffer.position(0)

        val vertexShader = ShaderUtils.loadShader(GLES20.GL_VERTEX_SHADER, vertexShaderCode)
        val fragmentShader = ShaderUtils.loadShader(GLES20.GL_FRAGMENT_SHADER, fragmentShaderCode)

        mProgram = GLES20.glCreateProgram().also {
            GLES20.glAttachShader(it, vertexShader)
            GLES20.glAttachShader(it, fragmentShader)
            GLES20.glLinkProgram(it)
        }
    }

    fun draw(mvpMatrix: FloatArray) {
        GLES20.glUseProgram(mProgram)

        val positionHandle = GLES20.glGetAttribLocation(mProgram, "vPosition")
        GLES20.glEnableVertexAttribArray(positionHandle)
        // Stride is 5 * 4 = 20 bytes
        vertexBuffer.position(0)
        GLES20.glVertexAttribPointer(positionHandle, 3, GLES20.GL_FLOAT, false, 20, vertexBuffer)

        val texCoordHandle = GLES20.glGetAttribLocation(mProgram, "aTexCoordinate")
        GLES20.glEnableVertexAttribArray(texCoordHandle)
        vertexBuffer.position(3)
        GLES20.glVertexAttribPointer(texCoordHandle, 2, GLES20.GL_FLOAT, false, 20, vertexBuffer)
        
        val textureHandle = GLES20.glGetUniformLocation(mProgram, "uTexture")
        GLES20.glActiveTexture(GLES20.GL_TEXTURE0)
        GLES20.glBindTexture(GLES20.GL_TEXTURE_2D, textureId)
        GLES20.glUniform1i(textureHandle, 0)
        
        // Keeping uColor content for tinting if needed, but currently ignoring it or mixing?
        // Let's just use texture for now as per request.
        // val colorHandle = GLES20.glGetUniformLocation(mProgram, "uColor")
        // GLES20.glUniform4fv(colorHandle, 1, color, 0)

        val matrixHandle = GLES20.glGetUniformLocation(mProgram, "uMVPMatrix")
        GLES20.glUniformMatrix4fv(matrixHandle, 1, false, mvpMatrix, 0)

        GLES20.glDrawElements(GLES20.GL_TRIANGLES, indexCount, GLES20.GL_UNSIGNED_SHORT, indexBuffer)

        GLES20.glDisableVertexAttribArray(positionHandle)
        GLES20.glDisableVertexAttribArray(texCoordHandle)
    }
}
