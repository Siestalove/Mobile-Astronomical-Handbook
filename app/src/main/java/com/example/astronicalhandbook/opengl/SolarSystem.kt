package com.example.astronicalhandbook.opengl

import android.content.Context
import android.opengl.GLES20
import android.opengl.Matrix
import com.example.astronicalhandbook.R
import com.example.astronicalhandbook.opengl.SolarSystem.Planet
import kotlin.math.cos
import kotlin.math.sin
import kotlin.random.Random

private const val SYSTEM_SCALE = 20f
private const val SPEED_SCALAR = 0.18f
private const val TWO_PI = (Math.PI * 2).toFloat()
private const val SUN_RADIUS = 0.006f * SYSTEM_SCALE
private const val RADIUS_SCALE = 0.5f

class SolarSystem(context: Context) {
    enum class PlanetId {
        SUN, MERCURY, VENUS, EARTH, MOON, MARS, JUPITER, SATURN, URANUS, NEPTUNE
    }

    private val planetPositions = mutableMapOf<PlanetId, FloatArray>()
    private val sphereMesh = SphereMesh(stacks = 16, slices = 16)

    private val modelMatrix = FloatArray(16)
    private val mvpMatrix = FloatArray(16)
    private var elapsedTime: Float = 0f



    private data class Planet(
        val radius: Float,
        val orbitRadius: Float,
        val orbitSpeed: Float,
        val textureId: Int
    )

    private data class PlanetState(
        val planet: Planet,
        var angle: Float = Random.nextFloat() * 360f,
        var innerAngle: Float = 0f
    )

    private val sunTexture = ShaderUtils.loadTexture(context, R.drawable.sun_texture)
    private val mercuryTexture = ShaderUtils.loadTexture(context, R.drawable.mercury_texture)
    private val venusTexture = ShaderUtils.loadTexture(context, R.drawable.venus_texture)
    private val earthTexture = ShaderUtils.loadTexture(context, R.drawable.earth_texture)
    private val moonTexture = ShaderUtils.loadTexture(context, R.drawable.moon_texture)
    private val marsTexture = ShaderUtils.loadTexture(context, R.drawable.mars_texture)
    private val jupiterTexture = ShaderUtils.loadTexture(context, R.drawable.jupiter_texture)
    private val saturnTexture = ShaderUtils.loadTexture(context, R.drawable.saturn_texture)
    private val uranusTexture = ShaderUtils.loadTexture(context, R.drawable.uranus_texture)
    private val neptuneTexture = ShaderUtils.loadTexture(context, R.drawable.neptune_texture)



    private val sunState = PlanetState(
        Planet(radius = 0.08f * SYSTEM_SCALE, orbitRadius = 0f, orbitSpeed = 0f, textureId = sunTexture)
    )
    private val mercuryState = PlanetState(
        Planet(0.008f * SYSTEM_SCALE, 0.2f * SYSTEM_SCALE * RADIUS_SCALE, 4.0f * SPEED_SCALAR, mercuryTexture)
    )

    private val venusState = PlanetState(
        Planet(0.02f * SYSTEM_SCALE, 0.35f * SYSTEM_SCALE * RADIUS_SCALE, 1.6f * SPEED_SCALAR, venusTexture)
    )

    private val earthState = PlanetState(
        Planet(0.021f * SYSTEM_SCALE, 0.5f * SYSTEM_SCALE * RADIUS_SCALE, 1.0f * SPEED_SCALAR, earthTexture)
    )

    private val moonState = PlanetState(
        Planet(0.006f * SYSTEM_SCALE, 0.05f * SYSTEM_SCALE * RADIUS_SCALE, 3.0f * SPEED_SCALAR, moonTexture)
    )

    private val marsState = PlanetState(
        Planet(0.011f * SYSTEM_SCALE, 0.7f * SYSTEM_SCALE * RADIUS_SCALE, 0.8f * SPEED_SCALAR, marsTexture)
    )

    private val jupiterState = PlanetState(
        Planet(0.07f * SYSTEM_SCALE, 1.0f * SYSTEM_SCALE * RADIUS_SCALE, 0.6f * SPEED_SCALAR, jupiterTexture)
    )

    private val saturnState = PlanetState(
        Planet(0.06f * SYSTEM_SCALE, 1.4f * SYSTEM_SCALE * RADIUS_SCALE, 0.3f * SPEED_SCALAR, saturnTexture)
    )

    private val uranusState = PlanetState(
        Planet(0.035f * SYSTEM_SCALE, 1.8f * SYSTEM_SCALE * RADIUS_SCALE, 0.15f * SPEED_SCALAR, uranusTexture)
    )

    private val neptuneState = PlanetState(
        Planet(0.034f * SYSTEM_SCALE, 1.9f * SYSTEM_SCALE * RADIUS_SCALE, 0.5f * SPEED_SCALAR, neptuneTexture)
    )

    fun draw(vpMatrix: FloatArray, deltaTime: Float) {
        deltaTime.coerceAtMost(0.05f)
        elapsedTime += deltaTime

        drawOrbitingPlanet( PlanetId.MERCURY, mercuryState, vpMatrix, deltaTime, elapsedTime)
        drawOrbitingPlanet(PlanetId.VENUS, venusState, vpMatrix, deltaTime, elapsedTime)

        updateOrbit(earthState, deltaTime)
        val r = SUN_RADIUS + earthState.planet.orbitRadius
        val ex = r * cos(earthState.angle)
        val ez = r * sin(earthState.angle)

        drawPlanet(PlanetId.EARTH, earthState, vpMatrix, ex, 0f, ez, deltaTime, elapsedTime)

        updateOrbit(moonState, deltaTime)
        val mx = ex + moonState.planet.orbitRadius * cos(moonState.angle)
        val my = moonState.planet.orbitRadius * sin(moonState.angle)

        drawPlanet(PlanetId.MOON, moonState, vpMatrix, mx, my, ez, deltaTime, elapsedTime)

        drawOrbitingPlanet(PlanetId.MARS, marsState, vpMatrix, deltaTime, elapsedTime)
        drawOrbitingPlanet(PlanetId.JUPITER, jupiterState, vpMatrix, deltaTime, elapsedTime)
        drawOrbitingPlanet(PlanetId.SATURN, saturnState, vpMatrix, deltaTime, elapsedTime)
        drawOrbitingPlanet(PlanetId.URANUS, uranusState, vpMatrix, deltaTime, elapsedTime)
        drawOrbitingPlanet(PlanetId.NEPTUNE, neptuneState, vpMatrix, deltaTime, elapsedTime)

        GLES20.glDisable(GLES20.GL_DEPTH_TEST)
        drawPlanet(PlanetId.SUN, sunState, vpMatrix, 0f, 0f, 0f, deltaTime, elapsedTime)
        GLES20.glEnable(GLES20.GL_DEPTH_TEST)
    }

    private fun updateOrbit(state: PlanetState, deltaTime: Float) {
        state.angle += state.planet.orbitSpeed * deltaTime
        if (state.angle > TWO_PI) state.angle -= TWO_PI
    }

    private fun drawOrbitingPlanet(
        planetId: PlanetId,
        state: PlanetState,
        vpMatrix: FloatArray,
        deltaTime: Float,
        elapsedTime: Float
    ) {
        updateOrbit(state, deltaTime)

        val r = SUN_RADIUS + state.planet.orbitRadius
        val x = r * cos(state.angle)
        val z = r * sin(state.angle)

        drawPlanet(planetId, state, vpMatrix, x, 0f, z, deltaTime, elapsedTime)
    }

    private fun drawPlanet(
        currentPlanetId: PlanetId,
        state: PlanetState,
        vpMatrix: FloatArray,
        x: Float,
        y: Float,
        z: Float,
        deltaTime: Float,
        elapsedTime: Float
    ) {
        Matrix.setIdentityM(modelMatrix, 0)
        Matrix.translateM(modelMatrix, 0, x, y, z)
        state.let {
            it.innerAngle += 0.05f * deltaTime * 360f
            Matrix.rotateM(modelMatrix, 0, it.innerAngle, 0f, 1f, 1f)
        }

        Matrix.scaleM(modelMatrix, 0, state.planet.radius, state.planet.radius, state.planet.radius)

        Matrix.multiplyMM(mvpMatrix, 0, vpMatrix, 0, modelMatrix, 0)

        val emissive = currentPlanetId == PlanetId.SUN || currentPlanetId == PlanetId.NEPTUNE
        val water = currentPlanetId == PlanetId.NEPTUNE
        sphereMesh.draw(
            mvpMatrix = mvpMatrix,
            modelMatrix = modelMatrix,
            textureId = state.planet.textureId,
            emissive,
            water,
            elapsedTime
        )
        planetPositions[currentPlanetId] = floatArrayOf(x, y, z)
    }

    fun getPlanetPosition(selectedPlanet: PlanetId): FloatArray? {
        return planetPositions[selectedPlanet]
    }

    fun getPlanetRadius(id: PlanetId): Float {
        return when (id) {
            PlanetId.SUN -> sunState.planet.radius
            PlanetId.MERCURY -> mercuryState.planet.radius
            PlanetId.VENUS -> venusState.planet.radius
            PlanetId.EARTH -> earthState.planet.radius
            PlanetId.MOON -> moonState.planet.radius
            PlanetId.MARS -> marsState.planet.radius
            PlanetId.JUPITER -> jupiterState.planet.radius
            PlanetId.SATURN -> saturnState.planet.radius
            PlanetId.URANUS -> uranusState.planet.radius
            PlanetId.NEPTUNE -> neptuneState.planet.radius
        }
    }
}
