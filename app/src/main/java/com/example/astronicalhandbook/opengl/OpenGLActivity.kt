package com.example.astronicalhandbook.opengl

import android.app.Activity
import android.os.Bundle

import android.widget.Button
import android.widget.TextView
import com.example.astronicalhandbook.R

class OpenGLActivity : Activity() {

    private lateinit var gLView: MyGLSurfaceView
    private lateinit var planetInfo: TextView
    private lateinit var planetDesc: TextView
    private lateinit var planetImage: android.widget.ImageView
    private lateinit var cardPlanetInfo: androidx.cardview.widget.CardView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_opengl)

        gLView = findViewById(R.id.gl_surface_view)
        planetInfo = findViewById(R.id.planet_info)
        planetDesc = findViewById(R.id.planet_desc)
        planetImage = findViewById(R.id.planet_image)
        cardPlanetInfo = findViewById(R.id.card_planet_info)

        findViewById<Button>(R.id.btn_back_to_news).setOnClickListener {
            finish()
        }

        findViewById<Button>(R.id.btn_toggle_info).setOnClickListener {
            if (cardPlanetInfo.visibility == android.view.View.VISIBLE) {
                cardPlanetInfo.visibility = android.view.View.GONE
                (it as Button).text = getString(R.string.toggle_info_btn)
            } else {
                cardPlanetInfo.visibility = android.view.View.VISIBLE
                (it as Button).text = "Скрыть инфо"
            }
        }

        findViewById<Button>(R.id.btn_left).setOnClickListener {
            gLView.queueEvent {
                gLView.renderer.selectPrevious()
                val name = gLView.renderer.getSelectedPlanetName()
                runOnUiThread { updatePlanetInfoUI(name) }
            }
        }

        findViewById<Button>(R.id.btn_right).setOnClickListener {
            gLView.queueEvent {
                gLView.renderer.selectNext()
                val name = gLView.renderer.getSelectedPlanetName()
                runOnUiThread { updatePlanetInfoUI(name) }
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
        
        // Initial setup
        gLView.queueEvent {
            val name = gLView.renderer.getSelectedPlanetName()
            runOnUiThread { updatePlanetInfoUI(name) }
        }
    }

    private fun updatePlanetInfoUI(planetName: String) {
        val (nameRes, descRes, imageRes) = when (planetName) {
            "SUN" -> Triple(R.string.planet_name_sun, R.string.planet_desc_sun, R.drawable.planetdesc_sun)
            "MERCURY" -> Triple(R.string.planet_name_mercury, R.string.planet_desc_mercury, R.drawable.planetdesc_mercury)
            "VENUS" -> Triple(R.string.planet_name_venus, R.string.planet_desc_venus, R.drawable.planetdesc_venus)
            "EARTH" -> Triple(R.string.planet_name_earth, R.string.planet_desc_earth, R.drawable.planetdesc_earth)
            "MOON" -> Triple(R.string.planet_name_moon, R.string.planet_desc_moon, R.drawable.planetdesc_moon)
            "MARS" -> Triple(R.string.planet_name_mars, R.string.planet_desc_mars, R.drawable.planetdesc_mars)
            "JUPITER" -> Triple(R.string.planet_name_jupiter, R.string.planet_desc_jupiter, R.drawable.planetdesc_jupiter)
            "SATURN" -> Triple(R.string.planet_name_saturn, R.string.planet_desc_saturn, R.drawable.planetdesc_saturn)
            "URANUS" -> Triple(R.string.planet_name_uranus, R.string.planet_desc_uranus, R.drawable.planetdesc_uran)
            "NEPTUNE" -> Triple(R.string.planet_name_neptune, R.string.planet_desc_neptune, R.drawable.planetdesc_neptune)
            else -> Triple(R.string.planet_name_sun, R.string.planet_desc_sun, R.drawable.planetdesc_sun)
        }
        
        planetInfo.setText(nameRes)
        planetDesc.setText(descRes)
        planetImage.setImageResource(imageRes)
    }
}