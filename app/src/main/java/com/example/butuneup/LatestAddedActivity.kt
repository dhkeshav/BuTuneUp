import android.media.MediaPlayer
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageButton
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.butuneup.R
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.StorageReference
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await
import java.io.IOException

class LatestAddedActivity : AppCompatActivity() {

    private lateinit var recyclerView: RecyclerView
    private lateinit var latestSongsList: MutableList<String>

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_latest_added)

        // Initialize RecyclerView and layout manager
        recyclerView = findViewById(R.id.recyclerviewer_latest_songs)
        recyclerView.layoutManager = LinearLayoutManager(this)

        // Load latest songs from Firebase Storage
        latestSongsList = mutableListOf()
        loadLatestSongs()
    }

    private fun loadLatestSongs() {
        val storage = FirebaseStorage.getInstance()
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

    companion object {
        const val TAG = "LatestAddedActivity"
    }
}
