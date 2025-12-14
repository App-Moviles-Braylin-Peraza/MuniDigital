package com.example.munidigital

import android.os.Bundle
import android.widget.ImageView
import androidx.appcompat.app.AppCompatActivity
import com.bumptech.glide.Glide

class ImageViewerActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_image_viewer)

        val imageUrl = intent.getStringExtra("IMAGE_URL")
        val imageView = findViewById<ImageView>(R.id.fullscreen_image)

        Glide.with(this)
            .load(imageUrl)
            .into(imageView)

        // Cerrar al tocar la imagen
        imageView.setOnClickListener {
            finish()
        }
    }
}