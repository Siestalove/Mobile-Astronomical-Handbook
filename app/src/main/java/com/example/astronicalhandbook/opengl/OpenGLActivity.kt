package com.example.astronicalhandbook.opengl

import android.app.Activity
import android.os.Bundle

import android.widget.Button
import com.example.astronicalhandbook.R

class OpenGLActivity : Activity() {

    private lateinit var gLView: MyGLSurfaceView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_opengl)

        gLView = findViewById(com.example.astronicalhandbook.R.id.gl_surface_view)
        
        findViewById<Button>(R.id.btn_back_to_news).setOnClickListener {
            finish()
        }
    }
}
