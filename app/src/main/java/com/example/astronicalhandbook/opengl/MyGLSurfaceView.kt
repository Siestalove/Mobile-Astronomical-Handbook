package com.example.astronicalhandbook.opengl

import android.content.Context
import android.opengl.GLSurfaceView
import android.util.AttributeSet
import android.view.MotionEvent

class MyGLSurfaceView(context: Context, attrs: AttributeSet? = null) : GLSurfaceView(context, attrs) {

    val renderer: MyGLRenderer
    private var lastTouchX = 0f

    init {
        setEGLContextClientVersion(2)

        renderer = MyGLRenderer(context)
        setRenderer(renderer)
        renderMode = RENDERMODE_CONTINUOUSLY
    }

    override fun onTouchEvent(event: MotionEvent): Boolean {
        when (event.action) {
            MotionEvent.ACTION_DOWN -> {
                lastTouchX = event.x
                return true
            }
            MotionEvent.ACTION_MOVE -> {
                val dx = event.x - lastTouchX
                lastTouchX = event.x
                queueEvent { renderer.rotateCameraBy(dx * 0.003f) }
                return true
            }
        }
        return super.onTouchEvent(event)
    }
}
