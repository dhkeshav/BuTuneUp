package com.example.butuneup

import android.content.Intent
import android.os.Bundle
import android.content.Context
import android.view.MotionEvent
import android.view.View

import android.util.Log
import android.widget.Button
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.google.firebase.storage.FirebaseStorage
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.graphics.Rect
import android.media.AudioManager
import android.media.MediaPlayer
import android.view.*
import android.widget.ImageButton
import androidx.core.content.ContextCompat.getSystemService
import java.io.IOException
import java.util.*

private const val TAG = "HomeActivity"

class HomeActivity : AppCompatActivity() {
    private lateinit var recyclerView: RecyclerView
    private lateinit var scaleGestureDetector: ScaleGestureDetector
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_home)
        scaleGestureDetector = ScaleGestureDetector(this, ScaleListener())
        // Find the floating action buttons by their IDs
//        val historyFab = findViewById<FloatingActionButton>(R.id.floatingActionButton3)
//        val settingsFab = findViewById<FloatingActionButton>(R.id.floatingActionButton2)
//        val reminderFab = findViewById<FloatingActionButton>(R.id.floatingActionButton5)

        // Set click listeners to the floating action buttons
//        historyFab.setOnClickListener {
//            // Code to handle click on history floating action button
//        }
//        settingsFab.setOnClickListener {
//            // Code to handle click on settings floating action button
//        }
//        reminderFab.setOnClickListener {
//            // Code to handle click on reminder floating action button
//        }

        class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
            val songNameTextView: TextView = view.findViewById(R.id.song_name_textview)
            val playButton: ImageButton = view.findViewById(R.id.imageButton)
            var isPlaying: Boolean = false
            var mediaPlayer: MediaPlayer? = null

            init {
                playButton.setOnClickListener {
                    isPlaying = !isPlaying
                    if (isPlaying) {
                        playButton.setImageResource(R.drawable.ic_pause) // Set to pause button image
                        mediaPlayer = MediaPlayer()
                        try {
                            // Get the download URL of the audio file from Firebase Storage
                            val storageRef = FirebaseStorage.getInstance().getReference("uploads")
                            val audioRef = storageRef.child(songNameTextView.text.toString())
                            audioRef.downloadUrl.addOnSuccessListener { uri ->
                                mediaPlayer!!.setDataSource(this.itemView.context, uri)
                                mediaPlayer!!.prepare()
                                mediaPlayer!!.start()
                            }
                        } catch (e: IOException) {
                            Log.e(TAG, "Error playing audio", e)
                        }
                    } else {
                        playButton.setImageResource(R.drawable.ic_play_arrow) // Set to play button image
                        mediaPlayer?.release()
                        mediaPlayer = null
                    }
                }
            }
        }

        // Find the grid layout buttons by their IDs
        val playlistButton = findViewById<Button>(R.id.Playlist_Btn)
        val profileButton = findViewById<Button>(R.id.Profile_Btn)
        val uploadMusicButton = findViewById<Button>(R.id.Upload_Btn)


        // Set click listeners to the grid layout buttons
        playlistButton.setOnClickListener {
            // Code to handle click on playlist button
            // val intent = Intent(this, PlaylistActivity::class.java)
            // startActivity(intent)
        }
        profileButton.setOnClickListener {
            // Code to handle click on profile button
            // val intent = Intent(this, ProfileActivity::class.java)
            // startActivity(intent)
        }
        uploadMusicButton.setOnClickListener {
            // Code to handle click on upload music button
            val intent = Intent(this, UploadMusicActivity::class.java)
            startActivity(intent)
        }


        // Get the current hour of the day
        val hour = Calendar.getInstance().get(Calendar.HOUR_OF_DAY)

        // Set the greeting message based on the current time
        val greetingTextView = findViewById<TextView>(R.id.greeting_textview)
        when (hour) {
            in 6..11 -> greetingTextView.text = "Good morning"
            in 12..17 -> greetingTextView.text = "Good afternoon"
            else -> greetingTextView.text = "Good evening"
        }
        recyclerView = findViewById(R.id.latest_songs_recyclerview)
        recyclerView.layoutManager = LinearLayoutManager(this)
        recyclerView = findViewById(R.id.latest_songs_recyclerview)

        recyclerView = findViewById(R.id.latest_songs_recyclerview)

        // Set an empty adapter for the RecyclerView to avoid "No adapter attached" error
        recyclerView.adapter = SongAdapter(emptyList())



        // Retrieve the latest uploads from Firebase Storage
        val storageRef = FirebaseStorage.getInstance().getReference("uploads")
        storageRef.listAll().addOnSuccessListener { listResult ->
            val songs = mutableListOf<String>()
            listResult.items.forEach { item ->
                item.downloadUrl.addOnSuccessListener { url ->
                    Log.d(TAG, "Download URL for ${item.name}: $url")
                    songs.add(item.name)
                    recyclerView.adapter = SongAdapter(songs)
                }
            }
        }.addOnFailureListener { exception ->
            Log.e(TAG, "Error retrieving files", exception)
        }
    }
    private inner class ScaleListener : ScaleGestureDetector.SimpleOnScaleGestureListener() {
        override fun onScale(detector: ScaleGestureDetector): Boolean {
            val scaleFactor = detector.scaleFactor

            // Adjust the volume based on the scale factor
            val audioManager = getSystemService(Context.AUDIO_SERVICE) as AudioManager
            val currentVolume = audioManager.getStreamVolume(AudioManager.STREAM_MUSIC)
            val maxVolume = audioManager.getStreamMaxVolume(AudioManager.STREAM_MUSIC)
            var newVolume = currentVolume + (maxVolume * (scaleFactor - 1)).toInt()
            if (newVolume < 0) {
                newVolume = 0
            } else if (newVolume > maxVolume) {
                newVolume = maxVolume
            }
            audioManager.setStreamVolume(AudioManager.STREAM_MUSIC, newVolume, 0)

            return true
        }
    }
    override fun onTouchEvent(event: MotionEvent): Boolean {
        scaleGestureDetector.onTouchEvent(event)
        return true
    }

    inner class SongAdapter(private val songs: List<String>) :
        RecyclerView.Adapter<SongAdapter.ViewHolder>() {

        inner class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
            val songNameTextView: TextView = view.findViewById(R.id.song_name_textview)
            val playButton: ImageButton = view.findViewById(R.id.imageButton)
            var isPlaying: Boolean = false
            var mediaPlayer: MediaPlayer? = null

            init {
                playButton.setOnClickListener {
                    isPlaying = !isPlaying
                    if (isPlaying) {
                        playButton.setImageResource(R.drawable.ic_pause) // Set to pause button image
                        mediaPlayer = MediaPlayer()
                        try {
                            // Get the download URL of the audio file from Firebase Storage
                            val storageRef = FirebaseStorage.getInstance().getReference("uploads")
                            val audioRef = storageRef.child(songNameTextView.text.toString())
                            audioRef.downloadUrl.addOnSuccessListener { uri ->
                                mediaPlayer!!.setDataSource(this.itemView.context, uri)
                                mediaPlayer!!.prepare()
                                mediaPlayer!!.start()
                            }
                        } catch (e: IOException) {
                            Log.e(TAG, "Error playing audio", e)
                        }
                    } else {
                        playButton.setImageResource(R.drawable.ic_play_arrow) // Set to play button image
                        mediaPlayer?.release()
                        mediaPlayer = null
                    }
                }
            }
        }

        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
            val view = LayoutInflater.from(parent.context)
                .inflate(R.layout.song_item, parent, false)
            return ViewHolder(view)
        }

        override fun onBindViewHolder(holder: ViewHolder, position: Int) {
            holder.songNameTextView.text = songs[position]
        }

        override fun getItemCount() = songs.size
    }

}

