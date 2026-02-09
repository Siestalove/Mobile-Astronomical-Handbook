package com.example.astronicalhandbook.opengl

import android.opengl.Matrix

class SolarSystem {

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
        sun = Sphere(radius = 0.12f, color = floatArrayOf(1.0f, 0.9f, 0.0f, 1.0f))
        
        mercury = Sphere(radius = 0.008f, color = floatArrayOf(0.6f, 0.6f, 0.6f, 1.0f))
        venus = Sphere(radius = 0.02f, color = floatArrayOf(0.9f, 0.7f, 0.4f, 1.0f))
        earth = Sphere(radius = 0.021f, color = floatArrayOf(0.2f, 0.5f, 1.0f, 1.0f))
        moon = Sphere(radius = 0.006f, color = floatArrayOf(0.8f, 0.8f, 0.8f, 1.0f))
        mars = Sphere(radius = 0.011f, color = floatArrayOf(0.9f, 0.3f, 0.1f, 1.0f))
        jupiter = Sphere(radius = 0.07f, color = floatArrayOf(0.8f, 0.6f, 0.4f, 1.0f))
        saturn = Sphere(radius = 0.06f, color = floatArrayOf(0.9f, 0.8f, 0.5f, 1.0f))
        uranus = Sphere(radius = 0.035f, color = floatArrayOf(0.6f, 0.9f, 0.9f, 1.0f))
        neptune = Sphere(radius = 0.034f, color = floatArrayOf(0.3f, 0.5f, 1.0f, 1.0f))
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
        val moonX = moonOrbitRadius * kotlin.math.cos(moonAngle)
        val moonY = moonOrbitRadius * kotlin.math.sin(moonAngle)

        Matrix.setIdentityM(modelMatrix, 0)
        Matrix.translateM(modelMatrix, 0, earthX + moonX, moonY, earthZ)
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
