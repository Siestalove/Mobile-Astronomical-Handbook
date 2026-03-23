package com.example.astronicalhandbook.opengl

import android.content.Context
import android.opengl.GLES20
import android.opengl.GLSurfaceView
import android.opengl.Matrix
import javax.microedition.khronos.egl.EGLConfig
import javax.microedition.khronos.opengles.GL10
import kotlin.math.sin
import com.example.astronicalhandbook.R

class MyGLRenderer(private val context: Context) : GLSurfaceView.Renderer {

    private lateinit var background: Square
    private lateinit var solarSystem: SolarSystem
    private lateinit var blackHoleMesh: SphereMesh
    private var textureId: Int = 0
    private var blackHoleTextureId: Int = 0
    private var lastFrameTimeNs = 0L
    private var bhX = -1.25f
    private var bhAngle = 0f
    private var bhTime = 0f


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
        blackHoleMesh = SphereMesh(stacks = 32, slices = 32)
        textureId = ShaderUtils.loadTexture(context, R.drawable.galaxy_texture)
        blackHoleTextureId = ShaderUtils.loadTexture(context, R.drawable.black_hole)
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

        val identityMatrix = FloatArray(16)
        Matrix.setIdentityM(identityMatrix, 0)
        background.draw(textureId, identityMatrix)

        GLES20.glDisable(GLES20.GL_BLEND)
        GLES20.glEnable(GLES20.GL_DEPTH_TEST)

        // Draw the black hole as a 2D Overlay (NDC) exactly like SanyaKOS
        GLES20.glEnable(GLES20.GL_BLEND)
        GLES20.glBlendFunc(GLES20.GL_SRC_ALPHA, GLES20.GL_ONE_MINUS_SRC_ALPHA)
        GLES20.glDepthMask(false)
        GLES20.glDisable(GLES20.GL_DEPTH_TEST)

        bhTime += deltaTime
        bhAngle = (bhAngle + 28f * deltaTime) % 360f
        val speedNow = 0.32f * (0.9f + 0.1f * sin(bhTime * 0.8f))
        bhX += speedNow * deltaTime
        if (bhX > 1.25f) bhX = -1.25f

        val y = 0.25f + 0.06f * sin(bhTime * 0.7f)
        val pulse = 1f + 0.06f * sin(bhTime * 1.6f)

        val blackHoleModel = FloatArray(16)
        Matrix.setIdentityM(blackHoleModel, 0)
        Matrix.translateM(blackHoleModel, 0, bhX, y, 0f)
        
        // 3D rotations so it looks like a spinning sphere
        Matrix.rotateM(blackHoleModel, 0, bhAngle, 0f, 0f, 1f)     // Spin around Z
        Matrix.rotateM(blackHoleModel, 0, bhAngle * 1.5f, 0f, 1f, 0f) // Spin around Y
        Matrix.rotateM(blackHoleModel, 0, bhAngle * 0.5f, 1f, 0f, 0f) // Spin around X

        Matrix.scaleM(blackHoleModel, 0, 0.30f * pulse, 0.30f * pulse, 0.30f * pulse) // Must scale Z as well for a perfect sphere!
        
        // Draw using SphereMesh instead of Square
        blackHoleMesh.draw(
            mvpMatrix = blackHoleModel, 
            modelMatrix = blackHoleModel, 
            textureId = blackHoleTextureId, 
            emissive = true
        )

        GLES20.glDepthMask(true)
        GLES20.glDisable(GLES20.GL_BLEND)

        solarSystem.draw(vPMatrix, deltaTime)

        val currentPlanetPos = solarSystem.getPlanetPosition(selectedPlanet)
    }

    override fun onSurfaceChanged(unused: GL10, width: Int, height: Int) {
        GLES20.glViewport(0, 0, width, height)

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

    fun getSelectedPlanetName(): String {
        return planets[selectedPlanetIndex].name
    }
}