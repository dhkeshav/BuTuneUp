import android.content.Intent
import android.media.MediaPlayer
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.butuneup.R
import com.google.firebase.ktx.Firebase
import com.google.firebase.storage.ktx.storage

class SongsActivity : AppCompatActivity() {
    private val storageRef = Firebase.storage.reference
    private lateinit var mediaPlayer: MediaPlayer

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_songs)

        // Get the list of songs from the intent
        val songs = intent?.getStringArrayListExtra("songs")

        // Initialize the media player
        mediaPlayer = MediaPlayer()

    }

    private inner class SongAdapter(private val songs: List<String>) :
        RecyclerView.Adapter<SongAdapter.ViewHolder>() {

        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
            val view = LayoutInflater.from(parent.context)
                .inflate(R.layout.song_item, parent, false)
            return ViewHolder(view)
        }

        override fun onBindViewHolder(holder: ViewHolder, position: Int) {
            holder.songNameTextView.text = songs[position]

            // Set up the play button
            holder.playButton.setOnClickListener {
                val songRef = storageRef.child(songs[position])
                songRef.downloadUrl.addOnSuccessListener { uri ->
                    mediaPlayer.apply {
                        reset()
                        setDataSource(uri.toString())
                        prepareAsync()
                        setOnPreparedListener {
                            start()
                        }
                    }
                }
            }

            // Set up the add to playlist button
            holder.addToPlaylistButton.setOnClickListener {
                val selectedSong = songs[position]
                val intent = Intent().apply {
                    putExtra("selected_song", selectedSong)
                }
                setResult(RESULT_OK, intent)
                finish()
            }
        }

        override fun getItemCount() = songs.size

        inner class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
            val songNameTextView: TextView = view.findViewById(R.id.title_textview)
            val playButton: Button = view.findViewById(R.id.play_button)
            val addToPlaylistButton: Button = view.findViewById(R.id.add_to_playlist_button)
        }

    }

    override fun onDestroy() {
        super.onDestroy()
        mediaPlayer.release()
    }
}
