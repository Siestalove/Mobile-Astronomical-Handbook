package com.example.astronicalhandbook.opengl

import android.app.Activity
import android.os.Bundle

import android.widget.Button
import android.widget.TextView
import com.example.astronicalhandbook.R

class OpenGLActivity : Activity() {

    private lateinit var gLView: MyGLSurfaceView
    private lateinit var planetInfo: TextView


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_opengl)

        gLView = findViewById(R.id.gl_surface_view)

        findViewById<Button>(R.id.btn_back_to_news).setOnClickListener {
            finish()
        }

        planetInfo = findViewById(R.id.planet_info)

        findViewById<Button>(R.id.btn_left).setOnClickListener {
            gLView.queueEvent {
                gLView.renderer.selectPrevious()
                val name = gLView.renderer.getSelectedPlanetName()
                runOnUiThread { planetInfo.text = name }
            }
        }

        findViewById<Button>(R.id.btn_right).setOnClickListener {
            gLView.queueEvent {
                gLView.renderer.selectNext()
                val name = gLView.renderer.getSelectedPlanetName()
                runOnUiThread { planetInfo.text = name }
            }
        }

        findViewById<Button>(R.id.btn_focus).setOnClickListener {
            gLView.queueEvent {
                runOnUiThread {
                    gLView.renderer.toggleFocus()
                }
            }
        }

        findViewById<Button>(R.id.btn_info).setOnClickListener {
            gLView.queueEvent {
                val name = gLView.renderer.getSelectedPlanetName()
                runOnUiThread {
                    if (name == "MOON") {
                        val intent = android.content.Intent(this@OpenGLActivity, MoonPhongActivity::class.java)
                        startActivity(intent)
                    }
                }
            }
        }

    }
}