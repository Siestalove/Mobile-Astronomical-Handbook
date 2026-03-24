package com.example.astronicalhandbook.opengl

import android.content.Context
import android.opengl.GLES20
import android.opengl.GLSurfaceView
import android.opengl.Matrix
import javax.microedition.khronos.egl.EGLConfig
import javax.microedition.khronos.opengles.GL10
import com.example.astronicalhandbook.R
import kotlin.math.PI
import kotlin.math.sin

class MyGLRenderer(private val context: Context) : GLSurfaceView.Renderer {

    private lateinit var background: Square
    private lateinit var solarSystem: SolarSystem
    lateinit var selectionCube: SelectionCube
    private lateinit var blackHole: BlackHoleSprite

    private var textureId: Int = 0
    private var lastFrameTimeNs = 0L
    private var bhTime = 0f
    private var screenW = 1
    private var screenH = 1


    private val vPMatrix = FloatArray(16)
    private val projectionMatrix = FloatArray(16)
    private val viewMatrix = FloatArray(16)

    private val camera = CameraController()
    private var selectedPlanetIndex = 0
    private val planets = SolarSystem.PlanetId.entries.toTypedArray()

    override fun onSurfaceCreated(unused: GL10, config: EGLConfig) {
        GLES20.glClearColor(0.0f, 0.0f, 0.0f, 1.0f)
        GLES20.glEnable(GLES20.GL_DEPTH_TEST)
        GLES20.glEnable(GLES20.GL_BLEND)
        GLES20.glBlendFunc(GLES20.GL_SRC_ALPHA, GLES20.GL_ONE_MINUS_SRC_ALPHA)
        GLES20.glEnable(GLES20.GL_CULL_FACE)
        GLES20.glCullFace(GLES20.GL_BACK)

        background = Square()
        solarSystem = SolarSystem(context)
        selectionCube = SelectionCube()
        blackHole = BlackHoleSprite(context)
        textureId = ShaderUtils.loadTexture(context, R.drawable.galaxy_texture)
    }

    override fun onDrawFrame(unused: GL10) {
        GLES20.glClear(GLES20.GL_COLOR_BUFFER_BIT or GLES20.GL_DEPTH_BUFFER_BIT)

        val now = System.nanoTime()
        var deltaTime = if (lastFrameTimeNs == 0L) 0f else (now - lastFrameTimeNs) * 1e-9f
        lastFrameTimeNs = now
        if (deltaTime > 0.1f) deltaTime = 0.05f

        val selectedPlanet = planets[selectedPlanetIndex]
        val planetPos = solarSystem.getPlanetPosition(selectedPlanet)

        camera.update(planetPos, deltaTime)
        Matrix.multiplyMM(vPMatrix, 0, projectionMatrix, 0, camera.getViewMatrix(), 0)

        GLES20.glDisable(GLES20.GL_DEPTH_TEST)
        GLES20.glEnable(GLES20.GL_BLEND)
        GLES20.glBlendFunc(GLES20.GL_ONE, GLES20.GL_ONE_MINUS_SRC_ALPHA)

        background.draw(textureId)
        GLES20.glEnable(GLES20.GL_BLEND)

        bhTime += deltaTime
        val bhProgress = (bhTime % 9f) / 9f
        val bhSize = screenW / 3f
        val bhCx = (-bhSize / 2f) + (screenW + bhSize) * bhProgress
        val bhCy = screenH / 2f + screenH * 0.20f * sin(bhProgress * 4.0 * PI).toFloat()
        blackHole.draw(bhCx, bhCy, bhSize, screenW, screenH)

        GLES20.glDisable(GLES20.GL_BLEND)
        GLES20.glEnable(GLES20.GL_DEPTH_TEST)
        solarSystem.draw(vPMatrix, deltaTime)

        val currentPlanetPos = solarSystem.getPlanetPosition(selectedPlanet)

        currentPlanetPos?.let {
            GLES20.glDepthMask(false)
            GLES20.glDisable(GLES20.GL_DEPTH_TEST)
            GLES20.glEnable(GLES20.GL_BLEND)
            GLES20.glBlendFunc(GLES20.GL_SRC_ALPHA, GLES20.GL_ONE_MINUS_SRC_ALPHA)

            val radius = solarSystem.getPlanetRadius(selectedPlanet)
            selectionCube.draw(
                vPMatrix,
                it,
                size = radius * 2.6f
            )
            GLES20.glDepthMask(true)
        }
    }

    override fun onSurfaceChanged(unused: GL10, width: Int, height: Int) {
        GLES20.glViewport(0, 0, width, height)
        screenW = width
        screenH = height

        val ratio: Float = width.toFloat() / height.toFloat()

        Matrix.frustumM(projectionMatrix, 0, -ratio, ratio, -1f, 1f, 1f, 100f)
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

    fun rotateCameraBy(deltaAngle: Float) {
        camera.rotateOrbitBy(deltaAngle)
    }

    fun getSelectedPlanetName(): String {
        return planets[selectedPlanetIndex].name
    }
}