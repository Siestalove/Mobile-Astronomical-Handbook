package com.example.astronicalhandbook.opengl

import android.opengl.Matrix

class SelectionCube {

    private val cubeMesh = Cube()
    private var creationTimeMs: Long = 0
    private val fadeOutDurationMs = 1500L

    fun draw(
        vpMatrix: FloatArray,
        position: FloatArray,
        size: Float
    ) {
        if (creationTimeMs == 0L) {
            creationTimeMs = System.currentTimeMillis()
        }

        val elapsedMs = System.currentTimeMillis() - creationTimeMs
        val alpha = (1.0f - (elapsedMs / fadeOutDurationMs.toFloat())).coerceIn(0f, 1f)

        val model = FloatArray(16)
        val mvp = FloatArray(16)

        Matrix.setIdentityM(model, 0)
        Matrix.translateM(model, 0, position[0], position[1], position[2])
        Matrix.scaleM(model, 0, size, size, size)

        Matrix.multiplyMM(mvp, 0, vpMatrix, 0, model, 0)

        cubeMesh.draw(
            mvp,
            color = floatArrayOf(0.2f, 0.6f, 1.0f, 0.3f * alpha)
        )
    }

    fun reset() {
        creationTimeMs = 0
    }
}
