package com.example.astronicalhandbook.opengl

import android.opengl.GLES20
import android.opengl.Matrix
import kotlin.math.cos
import kotlin.math.sin

private const val SYSTEM_SCALE = 1.8f
private const val TWO_PI = (Math.PI * 2).toFloat()
private const val SUN_RADIUS = 0.12f * SYSTEM_SCALE

class SolarSystem {
    enum class PlanetId {
        SUN, MERCURY, VENUS, EARTH, MOON, MARS, JUPITER, SATURN, URANUS, NEPTUNE
    }
    private val planetPositions = mutableMapOf<PlanetId, FloatArray>()
    private val sphereMesh = SphereMesh(stacks = 16, slices = 16)

    private val modelMatrix = FloatArray(16)
    private val mvpMatrix = FloatArray(16)


    private data class Planet(
        val radius: Float,
        val orbitRadius: Float,
        val orbitSpeed: Float,
        val color: FloatArray
    ) {
        override fun equals(other: Any?): Boolean {
            if (this === other) return true
            if (javaClass != other?.javaClass) return false

            other as Planet

            if (radius != other.radius) return false
            if (orbitRadius != other.orbitRadius) return false
            if (orbitSpeed != other.orbitSpeed) return false
            if (!color.contentEquals(other.color)) return false

            return true
        }

        override fun hashCode(): Int {
            var result = radius.hashCode()
            result = 31 * result + orbitRadius.hashCode()
            result = 31 * result + orbitSpeed.hashCode()
            result = 31 * result + color.contentHashCode()
            return result
        }
    }

    private data class PlanetState(
        val planet: Planet,
        var angle: Float = 0f
    )

    private val sun = Planet(
        radius = 0.12f * SYSTEM_SCALE,
        orbitRadius = 0f,
        orbitSpeed = 0f,
        color = floatArrayOf(1.0f, 0.9f, 0.0f, 1.0f)
    )

    private val mercuryState = PlanetState(
        Planet(0.008f * SYSTEM_SCALE, 0.2f * SYSTEM_SCALE, 4.0f, floatArrayOf(0.6f, 0.6f, 0.6f, 1f))
    )

    private val venusState = PlanetState(
        Planet(0.02f * SYSTEM_SCALE, 0.35f * SYSTEM_SCALE, 1.6f, floatArrayOf(0.9f, 0.7f, 0.4f, 1f))
    )

    private val earthState = PlanetState(
        Planet(0.021f * SYSTEM_SCALE, 0.5f * SYSTEM_SCALE, 1.0f, floatArrayOf(0.2f, 0.5f, 1.0f, 1f))
    )

    private val moonState = PlanetState(
        Planet(0.006f * SYSTEM_SCALE, 0.05f * SYSTEM_SCALE, 3.0f, floatArrayOf(0.8f, 0.8f, 0.8f, 1f))
    )

    private val marsState = PlanetState(
        Planet(0.011f * SYSTEM_SCALE, 0.7f * SYSTEM_SCALE, 0.5f, floatArrayOf(0.9f, 0.3f, 0.1f, 1f))
    )

    private val jupiterState = PlanetState(
        Planet(0.07f * SYSTEM_SCALE, 1.0f * SYSTEM_SCALE, 0.08f, floatArrayOf(0.8f, 0.6f, 0.4f, 1f))
    )

    private val saturnState = PlanetState(
        Planet(0.06f * SYSTEM_SCALE, 1.4f * SYSTEM_SCALE, 0.03f, floatArrayOf(0.9f, 0.8f, 0.5f, 1f))
    )

    private val uranusState = PlanetState(
        Planet(0.035f * SYSTEM_SCALE, 1.8f * SYSTEM_SCALE, 0.01f, floatArrayOf(0.6f, 0.9f, 0.9f, 1f))
    )

    private val neptuneState = PlanetState(
        Planet(0.034f * SYSTEM_SCALE, 2.1f * SYSTEM_SCALE, 0.006f, floatArrayOf(0.3f, 0.5f, 1.0f, 1f))
    )

    fun draw(vpMatrix: FloatArray, deltaTime: Float) {
        deltaTime.coerceAtMost(0.05f)

        GLES20.glDisable(GLES20.GL_DEPTH_TEST)
        drawPlanet(PlanetId.SUN, sun, vpMatrix, 0f, 0f, 0f)
        GLES20.glEnable(GLES20.GL_DEPTH_TEST)

        drawOrbitingPlanet( PlanetId.MERCURY, mercuryState, vpMatrix, deltaTime)
        drawOrbitingPlanet(PlanetId.VENUS, venusState, vpMatrix, deltaTime)

        updateOrbit(earthState, deltaTime)
        val r = SUN_RADIUS + earthState.planet.orbitRadius
        val ex = r * cos(earthState.angle)
        val ez = r * sin(earthState.angle)

        drawPlanet(PlanetId.EARTH, earthState.planet, vpMatrix, ex, 0f, ez)

        updateOrbit(moonState, deltaTime)
        val mx = ex + moonState.planet.orbitRadius * cos(moonState.angle)
        val my = moonState.planet.orbitRadius * sin(moonState.angle)

        drawPlanet(PlanetId.MOON, moonState.planet, vpMatrix, mx, my, ez)

        drawOrbitingPlanet(PlanetId.MARS, marsState, vpMatrix, deltaTime)
        drawOrbitingPlanet(PlanetId.JUPITER, jupiterState, vpMatrix, deltaTime)
        drawOrbitingPlanet(PlanetId.SATURN, saturnState, vpMatrix, deltaTime)
        drawOrbitingPlanet(PlanetId.URANUS, uranusState, vpMatrix, deltaTime)
        drawOrbitingPlanet(PlanetId.NEPTUNE, neptuneState, vpMatrix, deltaTime)
    }

    private fun updateOrbit(state: PlanetState, deltaTime: Float) {
        state.angle += state.planet.orbitSpeed * deltaTime
        if (state.angle > TWO_PI) state.angle -= TWO_PI
    }

    private fun drawOrbitingPlanet(
        planetId: PlanetId,
        state: PlanetState,
        vpMatrix: FloatArray,
        deltaTime: Float
    ) {
        updateOrbit(state, deltaTime)

        val r = SUN_RADIUS + state.planet.orbitRadius
        val x = r * cos(state.angle)
        val z = r * sin(state.angle)

        drawPlanet(planetId, state.planet, vpMatrix, x, 0f, z)
    }

    private fun drawPlanet(
        currentPlanetId: PlanetId,
        planet: Planet,
        vpMatrix: FloatArray,
        x: Float,
        y: Float,
        z: Float
    ) {
        Matrix.setIdentityM(modelMatrix, 0)
        Matrix.translateM(modelMatrix, 0, x, y, z)
        Matrix.scaleM(modelMatrix, 0, planet.radius, planet.radius, planet.radius)

        Matrix.multiplyMM(mvpMatrix, 0, vpMatrix, 0, modelMatrix, 0)

        val emissive = currentPlanetId == PlanetId.SUN
        sphereMesh.draw(
            mvpMatrix = mvpMatrix,
            modelMatrix = modelMatrix,
            color = planet.color,
            emissive
        )
        planetPositions[currentPlanetId] = floatArrayOf(x, y, z)
    }

    fun getPlanetPosition(selectedPlanet: PlanetId): FloatArray? {
        return planetPositions[selectedPlanet]
    }
}
