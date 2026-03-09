package com.example.astronicalhandbook.opengl

import android.content.Context
import android.opengl.GLES20
import android.opengl.GLSurfaceView
import android.opengl.Matrix
import javax.microedition.khronos.egl.EGLConfig
import javax.microedition.khronos.opengles.GL10
import com.example.astronicalhandbook.R

class MoonPhongRenderer(private val context: Context) : GLSurfaceView.Renderer {
    private lateinit var moonMesh: MoonPhongMesh
    private var moonTextureId: Int = 0

    private val vPMatrix = FloatArray(16)
    private val projectionMatrix = FloatArray(16)
    private val viewMatrix = FloatArray(16)
    private val modelMatrix = FloatArray(16)
    private val mvpMatrix = FloatArray(16)

    private var angle = 0f
    private var lastTime = System.nanoTime()

    override fun onSurfaceCreated(unused: GL10, config: EGLConfig) {
        GLES20.glClearColor(0.0f, 0.0f, 0.0f, 1.0f)
        GLES20.glEnable(GLES20.GL_DEPTH_TEST)

        moonMesh = MoonPhongMesh(stacks = 64, slices = 64)
        moonTextureId = ShaderUtils.loadTexture(context, R.drawable.moon_texture)
    }

    override fun onDrawFrame(unused: GL10) {
        GLES20.glClear(GLES20.GL_COLOR_BUFFER_BIT or GLES20.GL_DEPTH_BUFFER_BIT)
        
        val now = System.nanoTime()
        val dt = (now - lastTime) * 1e-9f
        lastTime = now
        angle += dt * 20f

        Matrix.setLookAtM(viewMatrix, 0, 0f, 0f, -5f, 0f, 0f, 0f, 0f, 1f, 0f)
        Matrix.multiplyMM(vPMatrix, 0, projectionMatrix, 0, viewMatrix, 0)
        
        Matrix.setIdentityM(modelMatrix, 0)
        Matrix.rotateM(modelMatrix, 0, angle, 0f, 1f, 0f)

        Matrix.multiplyMM(mvpMatrix, 0, vPMatrix, 0, modelMatrix, 0)

        val lightPos = floatArrayOf(2f, 2f, -2f)
        val viewPos = floatArrayOf(0f, 0f, -5f)

        moonMesh.draw(
            mvpMatrix = mvpMatrix,
            modelMatrix = modelMatrix,
            textureId = moonTextureId,
            lightPos = lightPos,
            viewPos = viewPos
        )
    }

    override fun onSurfaceChanged(unused: GL10, width: Int, height: Int) {
        GLES20.glViewport(0, 0, width, height)
        val ratio: Float = width.toFloat() / height.toFloat()
        Matrix.frustumM(projectionMatrix, 0, -ratio, ratio, -1f, 1f, 2f, 10f)
    }
}
