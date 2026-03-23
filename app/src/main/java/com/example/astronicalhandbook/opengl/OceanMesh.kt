package com.example.astronicalhandbook.opengl

import android.opengl.GLES20
import java.nio.ByteBuffer
import java.nio.ByteOrder
import java.nio.FloatBuffer
import java.nio.ShortBuffer
import kotlin.math.cos
import kotlin.math.sin

class OceanMesh(
    stacks: Int = 32,
    slices: Int = 32
) {

    private val vertexBuffer: FloatBuffer
    private val indexBuffer: ShortBuffer
    private val indexCount: Int

    private val program: Int

    private val aPosition: Int
    private val uMVPMatrix: Int
    private val uModelMatrix: Int
    private val uTime: Int

    init {
        val vertices = ArrayList<Float>()
        val indices = ArrayList<Short>()

        for (i in 0..stacks) {
            val phi = Math.PI * i / stacks
            val y = cos(phi).toFloat()
            val r = sin(phi).toFloat()

            for (j in 0..slices) {
                val theta = 2.0 * Math.PI * j / slices
                val x = (r * cos(theta)).toFloat()
                val z = (r * sin(theta)).toFloat()

                vertices.add(x)
                vertices.add(y)
                vertices.add(z)
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

        vertexBuffer = ByteBuffer.allocateDirect(vertices.size * 4)
            .order(ByteOrder.nativeOrder())
            .asFloatBuffer()
            .apply {
                put(vertices.toFloatArray())
                position(0)
            }

        indexBuffer = ByteBuffer.allocateDirect(indices.size * 2)
            .order(ByteOrder.nativeOrder())
            .asShortBuffer()
            .apply {
                put(indices.toShortArray())
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
        }

        aPosition = GLES20.glGetAttribLocation(program, "aPosition")
        uMVPMatrix = GLES20.glGetUniformLocation(program, "uMVPMatrix")
        uModelMatrix = GLES20.glGetUniformLocation(program, "uModelMatrix")
        uTime = GLES20.glGetUniformLocation(program, "uTime")
    }

    fun draw(
        mvpMatrix: FloatArray,
        modelMatrix: FloatArray,
        time: Float
    ) {
        GLES20.glUseProgram(program)

        GLES20.glEnableVertexAttribArray(aPosition)
        GLES20.glVertexAttribPointer(
            aPosition, 3, GLES20.GL_FLOAT, false, 0, vertexBuffer
        )

        GLES20.glUniformMatrix4fv(uMVPMatrix, 1, false, mvpMatrix, 0)
        GLES20.glUniformMatrix4fv(uModelMatrix, 1, false, modelMatrix, 0)
        GLES20.glUniform1f(uTime, time)

        GLES20.glDrawElements(
            GLES20.GL_TRIANGLES,
            indexCount,
            GLES20.GL_UNSIGNED_SHORT,
            indexBuffer
        )

        GLES20.glDisableVertexAttribArray(aPosition)
    }

    companion object {
        private const val VERTEX_SHADER = "" +
            "uniform mat4 uMVPMatrix;\n" +
            "uniform mat4 uModelMatrix;\n" +
            "attribute vec3 aPosition;\n" +
            "varying vec3 vWorldPos;\n" +
            "varying vec2 vUV;\n" +
            "void main() {\n" +
            "    vWorldPos = (uModelMatrix * vec4(aPosition, 1.0)).xyz;\n" +
            "    vUV = aPosition.xy * 0.5 + 0.5;\n" +
            "    gl_Position = uMVPMatrix * vec4(aPosition, 1.0);\n" +
            "}"

        private const val FRAGMENT_SHADER = "" +
            "precision mediump float;\n" +
            "uniform float uTime;\n" +
            "varying vec3 vWorldPos;\n" +
            "varying vec2 vUV;\n" +
            "float wave(vec2 p, float dirx, float diry, float freq, float speed) {\n" +
            "    return sin((p.x*dirx + p.y*diry) * freq + uTime * speed);\n" +
            "}\n" +
            "void main() {\n" +
            "    vec2 uv = vUV;\n" +
            "    float w1 = wave(uv, 1.0, 0.2, 18.0, 1.2);\n" +
            "    float w2 = wave(uv, 0.2, 1.0, 12.0, 0.9);\n" +
            "    float w3 = wave(uv, -0.8, 0.6, 9.0, 0.7);\n" +
            "    float w = (w1*0.5 + w2*0.35 + w3*0.25);\n" +
            "    uv += vec2(w * 0.015, w * 0.010);\n" +
            "    float h = 0.55 + 0.22 * wave(uv, 1.0, 0.0, 26.0, 1.4) + 0.18 * wave(uv, 0.0, 1.0, 19.0, 1.1) + 0.10 * wave(uv, 0.7, 0.7, 13.0, 0.8);\n" +
            "    vec3 deep = vec3(0.02, 0.18, 0.32);\n" +
            "    vec3 shallow = vec3(0.08, 0.45, 0.65);\n" +
            "    vec3 col = mix(deep, shallow, clamp(h, 0.0, 1.0));\n" +
            "    float foam = smoothstep(0.78, 0.92, h);\n" +
            "    col += foam * 0.20;\n" +
            "    gl_FragColor = vec4(col, 1.0);\n" +
            "}"
    }
}
