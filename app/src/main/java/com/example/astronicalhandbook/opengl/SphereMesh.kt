package com.example.astronicalhandbook.opengl

import android.opengl.GLES20
import java.nio.ByteBuffer
import java.nio.ByteOrder
import java.nio.FloatBuffer
import java.nio.ShortBuffer
import kotlin.math.cos
import kotlin.math.sin

class SphereMesh(
    stacks: Int = 16,
    slices: Int = 16
) {

    private val vertexBuffer: FloatBuffer
    private val normalBuffer: FloatBuffer
    private val texCoordBuffer: FloatBuffer
    private val indexBuffer: ShortBuffer
    private val indexCount: Int

    private val program: Int

    private val aPosition: Int
    private val aNormal: Int
    private val aTexCoordinate: Int
    private val uMVPMatrix: Int
    private val uModelMatrix: Int
    private val uTexture: Int
    private val uLightPos: Int
    private val uEmissive: Int


    init {
        val vertices = ArrayList<Float>()
        val normals = ArrayList<Float>()
        val texCoords = ArrayList<Float>()
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

                normals.add(x)
                normals.add(y)
                normals.add(z)

                val u = j.toFloat() / slices
                val v = i.toFloat() / stacks
                texCoords.add(u)
                texCoords.add(v)
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

        normalBuffer = ByteBuffer.allocateDirect(normals.size * 4)
            .order(ByteOrder.nativeOrder())
            .asFloatBuffer()
            .apply {
                put(normals.toFloatArray())
                position(0)
            }

        texCoordBuffer = ByteBuffer.allocateDirect(texCoords.size * 4)
            .order(ByteOrder.nativeOrder())
            .asFloatBuffer()
            .apply {
                put(texCoords.toFloatArray())
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
        aNormal = GLES20.glGetAttribLocation(program, "aNormal")
        aTexCoordinate = GLES20.glGetAttribLocation(program, "aTexCoordinate")
        uMVPMatrix = GLES20.glGetUniformLocation(program, "uMVPMatrix")
        uModelMatrix = GLES20.glGetUniformLocation(program, "uModelMatrix")
        uTexture = GLES20.glGetUniformLocation(program, "uTexture")
        uLightPos = GLES20.glGetUniformLocation(program, "uLightPos")
        uEmissive = GLES20.glGetUniformLocation(program, "uEmissive")
    }

    fun draw(
        mvpMatrix: FloatArray,
        modelMatrix: FloatArray,
        textureId: Int,
        emissive: Boolean
    ) {
        GLES20.glUseProgram(program)

        GLES20.glEnableVertexAttribArray(aPosition)
        GLES20.glVertexAttribPointer(
            aPosition, 3, GLES20.GL_FLOAT, false, 0, vertexBuffer
        )

        GLES20.glEnableVertexAttribArray(aNormal)
        GLES20.glVertexAttribPointer(
            aNormal, 3, GLES20.GL_FLOAT, false, 0, normalBuffer
        )

        GLES20.glEnableVertexAttribArray(aTexCoordinate)
        GLES20.glVertexAttribPointer(
            aTexCoordinate, 2, GLES20.GL_FLOAT, false, 0, texCoordBuffer
        )

        GLES20.glUniformMatrix4fv(uMVPMatrix, 1, false, mvpMatrix, 0)
        GLES20.glUniformMatrix4fv(uModelMatrix, 1, false, modelMatrix, 0)

        GLES20.glActiveTexture(GLES20.GL_TEXTURE0)
        GLES20.glBindTexture(GLES20.GL_TEXTURE_2D, textureId)
        GLES20.glUniform1i(uTexture, 0)

        GLES20.glUniform3f(uLightPos, 0f, 1f, 0f)
        GLES20.glUniform1f(uEmissive, if (emissive) 1f else 0f)

        GLES20.glDrawElements(
            GLES20.GL_TRIANGLES,
            indexCount,
            GLES20.GL_UNSIGNED_SHORT,
            indexBuffer
        )

        GLES20.glDisableVertexAttribArray(aPosition)
        GLES20.glDisableVertexAttribArray(aNormal)
        GLES20.glDisableVertexAttribArray(aTexCoordinate)
    }

    companion object {
        private const val VERTEX_SHADER = """
            uniform mat4 uMVPMatrix;
            uniform mat4 uModelMatrix;
            
            attribute vec3 aPosition;
            attribute vec3 aNormal;
            attribute vec2 aTexCoordinate;
            
            varying vec3 vNormal;
            varying vec3 vWorldPos;
            varying vec2 vTexCoordinate;

            void main() {
                vec4 worldPos = uModelMatrix * vec4(aPosition, 1.0);
                gl_Position = uMVPMatrix * vec4(aPosition, 1.0);
                vWorldPos = worldPos.xyz;
                vNormal = mat3(uModelMatrix) * aNormal;
                vTexCoordinate = aTexCoordinate;
            }
        """
        private const val FRAGMENT_SHADER = """
            precision mediump float;
            
            uniform sampler2D uTexture;
            uniform vec3 uLightPos;
            uniform float uEmissive;
            
            varying vec3 vNormal;
            varying vec3 vWorldPos;
            varying vec2 vTexCoordinate;
            
            void main() {
                vec3 texColor = texture2D(uTexture, vTexCoordinate).rgb;
            
                vec3 color;
                if (uEmissive > 0.5) {
                    color = texColor;
                } else {
                    vec3 lightDir = normalize(uLightPos - vWorldPos);
                    float diff = max(dot(normalize(vNormal), lightDir), 0.0);
                    float ambient = 0.25;
                    color = texColor * (ambient + diff);
                }
            
                gl_FragColor = vec4(color, 1.0);
            }
        """

    }
}
