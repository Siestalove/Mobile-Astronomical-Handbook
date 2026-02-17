package com.example.astronicalhandbook.opengl

import android.opengl.Matrix
import kotlin.math.sqrt

class CameraController {
    private val viewMatrix = FloatArray(16)
    private var eyeX = 0f
    private var eyeY = 10f
    private var eyeZ = -16f

    private var targetX = 0f
    private var targetY = 0f
    private var targetZ = 0f

    var followPlanet: Boolean = false

    fun update(target: FloatArray?, deltaTime: Float) {
        if (followPlanet && target != null) {
            val followDistance = 2.6f
            val dx = eyeX - target[0]
            val dz = eyeZ - target[2]
            val len = sqrt(dx * dx + dz * dz).coerceAtLeast(0.001f)

            val desiredX = target[0] + (dx / len) * followDistance
            val desiredZ = target[2] + (dz / len) * followDistance

            eyeX += (desiredX - eyeX) * deltaTime * 2f
            eyeZ += (desiredZ - eyeZ) * deltaTime * 2f
            eyeY += (1.0f - eyeY) * deltaTime * 2f
            targetX += (target[0] - targetX) * deltaTime * 5f
            targetZ += (target[2] - targetZ) * deltaTime * 5f
        } else {
            val defaultEyeX = 0f
            val defaultEyeY = 10f
            val defaultEyeZ = -16f
            val defaultTargetX = 0f
            val defaultTargetY = 0f
            val defaultTargetZ = 0f

            eyeX += (defaultEyeX - eyeX) * deltaTime * 2f
            eyeY += (defaultEyeY - eyeY) * deltaTime * 2f
            eyeZ += (defaultEyeZ - eyeZ) * deltaTime * 2f
            targetX += (defaultTargetX - targetX) * deltaTime * 5f
            targetY += (defaultTargetY - targetY) * deltaTime * 5f
            targetZ += (defaultTargetZ - targetZ) * deltaTime * 5f
        }

        Matrix.setLookAtM(
            viewMatrix, 0,
            eyeX, eyeY, eyeZ,
            targetX, targetY, targetZ,
            0f, 1f, 0f
        )
    }

    fun getViewMatrix(): FloatArray = viewMatrix
}
