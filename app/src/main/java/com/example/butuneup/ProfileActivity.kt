package com.example.butuneup

import android.content.Intent
import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.ImageButton
import android.widget.ImageView
import android.widget.TextView
import com.bumptech.glide.Glide
import com.bumptech.glide.load.engine.DiskCacheStrategy

class ProfileActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_profile)

        val bennettLogo = findViewById<ImageButton>(R.id.spotify_logo)
        bennettLogo.setOnClickListener {
            val url = "https://www.bennett.edu.in/admission/"
            val intent = Intent(Intent.ACTION_VIEW)
            intent.data = Uri.parse(url)
            startActivity(intent)
        }
        // Get references to the UI views
        // Retrieve the user data from the intent extras
        val email = intent.getStringExtra("email")
        val name = intent.getStringExtra("name")
        val photoUrl = intent.getStringExtra("photoUrl")

        // Get references to the UI views
        val profileImageView: ImageView = findViewById(R.id.profile_picture)
        val nameTextView: TextView = findViewById(R.id.display_name)
        val bioTextView: TextView = findViewById(R.id.bio)

        // Set the user data to the UI views
        nameTextView.text = name
        bioTextView.text = email
        if (!photoUrl.isNullOrEmpty()) {
            Glide.with(this)
                .load(photoUrl)
                .skipMemoryCache(true)
                .diskCacheStrategy(DiskCacheStrategy.NONE)
                .into(profileImageView)

        }

        Log.d("ProfileActivity", "Photo URL: $photoUrl")
    }
}
