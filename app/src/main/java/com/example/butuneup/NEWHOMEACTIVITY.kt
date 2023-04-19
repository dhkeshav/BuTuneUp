package com.example.butuneup

import LatestAddedActivity
import android.content.Intent
import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.ImageButton
import android.widget.ImageView
import com.bumptech.glide.Glide
import java.util.*

class NEWHOMEACTIVITY : AppCompatActivity() {
    private lateinit var bennettLogo: ImageButton
    private lateinit var adUrl: String
    private val imageIds = arrayOf(R.drawable.img_4, R.drawable.img_5, R.drawable.img_6, R.drawable.img_7, R.drawable.img_8)
    private val adUrls = arrayOf("https://applications.bennett.edu.in/bu?utm_source=ridge&utm_medium=googlesearch&utm_campaign=brand-p1&gclid=CjwKCAjw586hBhBrEiwAQYEnHX26FirSjkOYYgFSXB-_Qb8bAxll6H069qt-SC5xKal3E9z_LBdnpBoCAT4QAvD_BwE",
        "https://www.amazon.in/Boat-Rockerz-550-Headphone-Aesthetics/dp/B0856HNLDK/ref=sr_1_1_sspa?crid=1KFEDQF30JWAJ&keywords=headphones%2Bwireless&qid=1681191782&sprefix=head%2Caps%2C364&sr=8-1-spons&sp_csd=d2lkZ2V0TmFtZT1zcF9hdGY&th=1",
        "https://www.amazon.in/boAt-Stone-620-Portable-Multi-Compatibility/dp/B09GFRV7L5/ref=sr_1_3?crid=2XNZD1J6LEMQV&keywords=speaker&qid=1681191888&sprefix=speaker%2Caps%2C239&sr=8-3",
        "https://www.amazon.in/Boult-Audio-Resistant-Assistant-Bluetooth/dp/B09WJ7R8BP/ref=sr_1_2_sspa?crid=CEQTPA3XESBR&keywords=eardopes&qid=1681192608&sprefix=eardope%2Caps%2C334&sr=8-2-spons&sp_csd=d2lkZ2V0TmFtZT1zcF9hdGY&th=1",
        "https://www.example.com/ad5")
    private var currentImageIndex = 0

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_newhomeactivity)
        val imageButton4: ImageButton = findViewById(R.id.favourites_btn)
        Glide.with(this).load(R.drawable.fav).into(imageButton4)
        val imageView: ImageView = findViewById(R.id.imageView)
        Glide.with(this).load(R.drawable.sound_on).into(imageView)

        bennettLogo = findViewById(R.id.bennett_logo)
        // Get references to the buttons
        val mostPlayedButton = findViewById<ImageButton>(R.id.most_played_btn)
        val uploadButton = findViewById<ImageButton>(R.id.upload_btn)
        val latestAddedButton = findViewById<ImageButton>(R.id.latest_added_btn)
        val favouritesButton = findViewById<ImageButton>(R.id.favourites_btn)

        // Set up click listeners for each button
        mostPlayedButton.setOnClickListener {
            // Replace MainActivity with the activity you want to launch
            val intent = Intent(this, MostPlayedSongsActivity::class.java)
            startActivity(intent)
        }

        uploadButton.setOnClickListener {
            // Replace UploadActivity with the activity you want to launch
            val intent = Intent(this, UploadMusicActivity::class.java)
            startActivity(intent)
        }

        latestAddedButton.setOnClickListener {
            // Replace LatestAddedActivity with the activity you want to launch
            val intent = Intent(this,HomeActivity::class.java)
            startActivity(intent)
        }

        favouritesButton.setOnClickListener {
            // Replace FavouritesActivity with the activity you want to launch
//            val intent = Intent(this, FavouritesActivity::class.java)
//            startActivity(intent)
        }

        // Start the timer to change the image every 5 seconds
        Timer().scheduleAtFixedRate(object : TimerTask() {
            override fun run() {
                runOnUiThread {
                    // Update the image in the ImageButton
                    bennettLogo.setImageResource(imageIds[currentImageIndex])

                    // Get the corresponding URL and store it in adUrl
                    adUrl = adUrls[currentImageIndex]

                    // Increment the image index
                    currentImageIndex++
                    if (currentImageIndex == imageIds.size) {
                        currentImageIndex = 0
                    }
                }
            }
        }, 0, 5000)

        // Set an OnClickListener on the ImageButton to launch the URL when clicked
        bennettLogo.setOnClickListener {
            val intent = Intent(Intent.ACTION_VIEW, Uri.parse(adUrl))
            startActivity(intent)
        }
    }
}
