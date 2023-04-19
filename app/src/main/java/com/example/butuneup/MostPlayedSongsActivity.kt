package com.example.butuneup

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView

class MostPlayedSongsActivity : AppCompatActivity() {
    private lateinit var recyclerView: RecyclerView
    private lateinit var mostPlayedSongsList: MutableList<Song>

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_most_played_songs)

        // Initialize RecyclerView and layout manager
        recyclerView = findViewById(R.id.most_played_recyclerview)
        recyclerView.layoutManager = LinearLayoutManager(this)

        // Load most played songs from database or API
        mostPlayedSongsList = loadMostPlayedSongs()

        // Create and set adapter for RecyclerView
        val adapter = MostPlayedSongsAdapter(mostPlayedSongsList)
        recyclerView.adapter = adapter
    }

    private fun loadMostPlayedSongs(): MutableList<Song> {
        // This is where you would fetch the most played songs from your database or API
        // For this example, we'll just create some dummy data
        val songs = mutableListOf<Song>()
        songs.add(Song("Song 1", "Artist 1", "Album 1", R.drawable.img))
        songs.add(Song("Song 2", "Artist 2", "Album 2", R.drawable.img))
        songs.add(Song("Song 3", "Artist 3", "Album 3", R.drawable.img))
        songs.add(Song("Song 4", "Artist 4", "Album 4", R.drawable.img))
        songs.add(Song("Song 5", "Artist 5", "Album 5", R.drawable.img))
        songs.add(Song("Song 6", "Artist 6", "Album 6", R.drawable.img))
        songs.add(Song("Song 7", "Artist 7", "Album 7", R.drawable.img))
        songs.add(Song("Song 8", "Artist 8", "Album 8", R.drawable.img))
        songs.add(Song("Song 9", "Artist 9", "Album 9", R.drawable.img))
        songs.add(Song("Song 10", "Artist 10", "Album 10", R.drawable.img))
        return songs
    }

    data class Song(val title: String, val artist: String, val album: String, val imageResource: Int)
}

class MostPlayedSongsAdapter(private val songsList: List<MostPlayedSongsActivity.Song>) : RecyclerView.Adapter<MostPlayedSongsAdapter.ViewHolder>() {

    class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val songTitle: TextView = itemView.findViewById(R.id.song_name_textview)

        val songImage: ImageView = itemView.findViewById(R.id.song_imageview)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.song_item, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val song = songsList[position]
        holder.songTitle.text = "Song ${position + 1}"

        holder.songImage.setImageResource(R.drawable.img)
    }

    override fun getItemCount(): Int {
        return songsList.size
    }
}
