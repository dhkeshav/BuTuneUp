package com.example.butuneup

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.google.firebase.storage.FirebaseStorage
import java.util.*

private const val TAG = "HomeActivity"

class HomeActivity : AppCompatActivity() {
    private lateinit var recyclerView: RecyclerView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_home)

        // Find the floating action buttons by their IDs
        val historyFab = findViewById<FloatingActionButton>(R.id.floatingActionButton3)
        val settingsFab = findViewById<FloatingActionButton>(R.id.floatingActionButton2)
        val reminderFab = findViewById<FloatingActionButton>(R.id.floatingActionButton5)

        // Set click listeners to the floating action buttons
        historyFab.setOnClickListener {
            // Code to handle click on history floating action button
        }
        settingsFab.setOnClickListener {
            // Code to handle click on settings floating action button
        }
        reminderFab.setOnClickListener {
            // Code to handle click on reminder floating action button
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

    private class SongAdapter(private val songs: List<String>) :
        RecyclerView.Adapter<SongAdapter.ViewHolder>() {

        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
            val view = LayoutInflater.from(parent.context)
                .inflate(R.layout.song_item, parent, false)
            return ViewHolder(view)
        }

        override fun onBindViewHolder(holder: ViewHolder, position: Int) {
            holder.songNameTextView.text = songs[position]
        }

        override fun getItemCount() = songs.size

        inner class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
            val songNameTextView: TextView = view.findViewById(R.id.song_name_textview)
        }
    }
}
