package com.example.astronicalhandbook.opengl

import android.app.Activity
import android.os.Bundle
import android.widget.Button
import android.opengl.GLSurfaceView
import com.example.astronicalhandbook.R

class MoonPhongActivity : Activity() {
    private lateinit var gLView: GLSurfaceView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_moon_phong)

        gLView = findViewById(R.id.moon_phong_surface_view)
        gLView.setEGLContextClientVersion(2)
        val renderer = MoonPhongRenderer(this)
        gLView.setRenderer(renderer)
        gLView.renderMode = GLSurfaceView.RENDERMODE_CONTINUOUSLY

        findViewById<Button>(R.id.btn_back).setOnClickListener {
            finish()
        }
    }

    override fun onResume() {
        super.onResume()
        gLView.onResume()
    }

    override fun onPause() {
        super.onPause()
        gLView.onPause()
    }
}
