package com.example.astronicalhandbook.opengl

import android.content.Context
import android.opengl.Matrix
import com.example.astronicalhandbook.R

class SolarSystem(context: Context) {

    private val sun: Sphere
    private val mercury: Sphere
    private val venus: Sphere
    private val earth: Sphere
    private val moon: Sphere
    private val mars: Sphere
    private val jupiter: Sphere
    private val saturn: Sphere
    private val uranus: Sphere
    private val neptune: Sphere

    private val modelMatrix = FloatArray(16)
    private val tempMatrix = FloatArray(16)

    init {
        val sunTexture = ShaderUtils.loadTexture(context, R.drawable.sun_texture)
        val mercuryTexture = ShaderUtils.loadTexture(context, R.drawable.mercury_texture)
        val venusTexture = ShaderUtils.loadTexture(context, R.drawable.venus_texture)
        val earthTexture = ShaderUtils.loadTexture(context, R.drawable.earth_texture)
        val moonTexture = ShaderUtils.loadTexture(context, R.drawable.moon_texture)
        val marsTexture = ShaderUtils.loadTexture(context, R.drawable.mars_texture)
        val jupiterTexture = ShaderUtils.loadTexture(context, R.drawable.jupiter_texture)
        val saturnTexture = ShaderUtils.loadTexture(context, R.drawable.saturn_texture)
        val uranusTexture = ShaderUtils.loadTexture(context, R.drawable.uranus_texture)
        val neptuneTexture = ShaderUtils.loadTexture(context, R.drawable.neptune_texture)

        sun = Sphere(radius = 0.12f, textureId = sunTexture)
        
        mercury = Sphere(radius = 0.008f, textureId = mercuryTexture)
        venus = Sphere(radius = 0.02f, textureId = venusTexture)
        earth = Sphere(radius = 0.021f, textureId = earthTexture)
        moon = Sphere(radius = 0.006f, textureId = moonTexture)
        mars = Sphere(radius = 0.011f, textureId = marsTexture)
        jupiter = Sphere(radius = 0.07f, textureId = jupiterTexture)
        saturn = Sphere(radius = 0.06f, textureId = saturnTexture)
        uranus = Sphere(radius = 0.035f, textureId = uranusTexture)
        neptune = Sphere(radius = 0.034f, textureId = neptuneTexture)
    }

    fun draw(vpMatrix: FloatArray, time: Long) {
        val timeSeconds = time / 1000f

        Matrix.setIdentityM(modelMatrix, 0)
        Matrix.multiplyMM(tempMatrix, 0, vpMatrix, 0, modelMatrix, 0)
        sun.draw(tempMatrix)

        drawPlanet(mercury, vpMatrix, 0.2f, timeSeconds * 4.0f)
        drawPlanet(venus, vpMatrix, 0.35f, timeSeconds * 1.6f)

        val earthAngle = timeSeconds * 1.0f
        val earthX = 0.5f * kotlin.math.cos(earthAngle)
        val earthZ = 0.5f * kotlin.math.sin(earthAngle)

        Matrix.setIdentityM(modelMatrix, 0)
        Matrix.translateM(modelMatrix, 0, earthX, 0f, earthZ)
        Matrix.multiplyMM(tempMatrix, 0, vpMatrix, 0, modelMatrix, 0)
        earth.draw(tempMatrix)

        val moonAngle = timeSeconds * 3.0f
        val moonOrbitRadius = 0.05f
        val moonY = moonOrbitRadius * kotlin.math.cos(moonAngle)
        val moonZ = moonOrbitRadius * kotlin.math.sin(moonAngle)

        Matrix.setIdentityM(modelMatrix, 0)
        Matrix.translateM(modelMatrix, 0, earthX, moonY, earthZ + moonZ)
        Matrix.multiplyMM(tempMatrix, 0, vpMatrix, 0, modelMatrix, 0)
        moon.draw(tempMatrix)

        drawPlanet(mars, vpMatrix, 0.7f, timeSeconds * 0.5f)
        drawPlanet(jupiter, vpMatrix, 1.0f, timeSeconds * 0.08f)
        drawPlanet(saturn, vpMatrix, 1.4f, timeSeconds * 0.03f)
        drawPlanet(uranus, vpMatrix, 1.8f, timeSeconds * 0.01f)
        drawPlanet(neptune, vpMatrix, 2.1f, timeSeconds * 0.006f)
    }

    private fun drawPlanet(planet: Sphere, vpMatrix: FloatArray, orbitRadius: Float, angle: Float) {
        val x = orbitRadius * kotlin.math.cos(angle)
        val z = orbitRadius * kotlin.math.sin(angle)

        Matrix.setIdentityM(modelMatrix, 0)
        Matrix.translateM(modelMatrix, 0, x, 0f, z)
        Matrix.multiplyMM(tempMatrix, 0, vpMatrix, 0, modelMatrix, 0)
        planet.draw(tempMatrix)
    }
}
