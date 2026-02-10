package com.example.astronicalhandbook.opengl

import android.content.Context
import android.opengl.GLES20
import android.opengl.GLSurfaceView
import android.opengl.Matrix
import javax.microedition.khronos.egl.EGLConfig
import javax.microedition.khronos.opengles.GL10
import com.example.astronicalhandbook.R

class MyGLRenderer(private val context: Context) : GLSurfaceView.Renderer {

    private lateinit var background: Square
    private lateinit var solarSystem: SolarSystem
    private var textureId: Int = 0
    private var lastFrameTimeNs = 0L


    private val vPMatrix = FloatArray(16)
    private val projectionMatrix = FloatArray(16)
    private val viewMatrix = FloatArray(16)

    private val camera = CameraController()
    private var selectedPlanetIndex = 0
    private val planets = SolarSystem.PlanetId.entries.toTypedArray()
    private val selectionCube = SelectionCube()


    override fun onSurfaceCreated(unused: GL10, config: EGLConfig) {
        GLES20.glClearColor(0.0f, 0.0f, 0.0f, 1.0f)
        GLES20.glEnable(GLES20.GL_DEPTH_TEST)
        GLES20.glEnable(GLES20.GL_CULL_FACE)
        GLES20.glCullFace(GLES20.GL_BACK)
        GLES20.glEnable(GLES20.GL_BLEND)
        GLES20.glBlendFunc(GLES20.GL_SRC_ALPHA, GLES20.GL_ONE_MINUS_SRC_ALPHA)

        background = Square()
        solarSystem = SolarSystem()
        textureId = ShaderUtils.loadTexture(context, R.drawable.galaxy_texture)
    }

    override fun onDrawFrame(unused: GL10) {
        GLES20.glClear(GLES20.GL_COLOR_BUFFER_BIT or GLES20.GL_DEPTH_BUFFER_BIT)

        val now = System.nanoTime()
        val deltaTime = (now - lastFrameTimeNs) * 1e-9f
        lastFrameTimeNs = now


        Matrix.setLookAtM(viewMatrix, 0, 0f, 3f, -5f, 0f, 0f, 0f, 0f, 1.0f, 0.0f)

        Matrix.multiplyMM(vPMatrix, 0, projectionMatrix, 0, viewMatrix, 0)

        GLES20.glDisable(GLES20.GL_DEPTH_TEST)
        GLES20.glEnable(GLES20.GL_BLEND)
        GLES20.glBlendFunc(GLES20.GL_ONE, GLES20.GL_ONE_MINUS_SRC_ALPHA)

        val selectedPlanet = planets[selectedPlanetIndex]
        val planetPos = solarSystem.getPlanetPosition(selectedPlanet)

        camera.update(planetPos, deltaTime)
        Matrix.multiplyMM(vPMatrix, 0, projectionMatrix, 0, camera.getViewMatrix(), 0)

        background.draw(textureId)

        GLES20.glDisable(GLES20.GL_BLEND)
        GLES20.glEnable(GLES20.GL_DEPTH_TEST)

        solarSystem.draw(vPMatrix, deltaTime)

        planetPos?.let {
            selectionCube.draw(vPMatrix, it, size = 0.15f)
        }
    }

    override fun onSurfaceChanged(unused: GL10, width: Int, height: Int) {
        GLES20.glViewport(0, 0, width, height)

        val ratio: Float = width.toFloat() / height.toFloat()

        Matrix.frustumM(projectionMatrix, 0, -ratio, ratio, -1f, 1f, 1f, 10f)
    }

    fun selectNext() {
        selectedPlanetIndex = (selectedPlanetIndex + 1) % planets.size
    }

    fun selectPrevious() {
        selectedPlanetIndex =
            (selectedPlanetIndex - 1 + planets.size) % planets.size
    }

    fun toggleFocus() {
        camera.followPlanet = !camera.followPlanet
    }

    fun getSelectedPlanetName(): String {
        return planets[selectedPlanetIndex].name
    }
}