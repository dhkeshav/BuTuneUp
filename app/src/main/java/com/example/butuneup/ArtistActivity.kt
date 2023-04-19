package com.example.butuneup

import android.content.Context
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.service.controls.ControlsProviderService.TAG
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.auth.GoogleAuthProvider
import com.google.firebase.auth.ktx.auth
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase

class ArtistActivity : AppCompatActivity() {
    private lateinit var userList: MutableList<User>
    private lateinit var userAdapter: UserAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_artist)

        userList = mutableListOf()
        userAdapter = UserAdapter(userList)

        val recyclerView = findViewById<RecyclerView>(R.id.user_list)
        recyclerView.layoutManager = LinearLayoutManager(this)
        recyclerView.adapter = userAdapter

        // Fetch all users from Firebase who signed in with Google and add them to the userList
        val db = Firebase.firestore
        val providerId = GoogleAuthProvider.PROVIDER_ID // Replace this with your provider ID if necessary
        db.collection("users")
            .whereEqualTo("signInProvider", providerId)
            .get()
            .addOnSuccessListener { documents ->
                for (document in documents) {
                    val user = document.toObject(User::class.java)
                    userList.add(user)
                }
                userAdapter.notifyDataSetChanged()
            }
            .addOnFailureListener { exception ->
                Log.w(TAG, "Error getting users", exception)
            }
    }

}

class UserAdapter(private val userList: List<User>) : RecyclerView.Adapter<UserAdapter.UserViewHolder>() {

    private val followedUserList: MutableList<User> = mutableListOf()

    class UserViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val profilePicImageView: ImageView = itemView.findViewById(R.id.profile_pic)
        val userNameTextView: TextView = itemView.findViewById(R.id.user_name)
        val followButton: Button = itemView.findViewById(R.id.follow_button)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): UserViewHolder {
        val itemView = LayoutInflater.from(parent.context).inflate(R.layout.user_item, parent, false)
        return UserViewHolder(itemView)
    }

    override fun onBindViewHolder(holder: UserViewHolder, position: Int) {
        val currentUser = userList[position]

        // Set the profile picture and user name
        holder.profilePicImageView.setImageResource(R.drawable._avatar_shape)
        holder.userNameTextView.text = currentUser.name

        // Set the follow button text and click listener
        holder.followButton.apply {
            text = if (currentUser.isFollowing) "Unfollow" else "Follow"
            setTextColor(if (currentUser.isFollowing) context.getColor(R.color.background) else context.getColor(R.color.white))
            setBackgroundColor(if (currentUser.isFollowing) context.getColor(R.color.white) else context.getColor(R.color.background))

            setOnClickListener {
                if (currentUser.isFollowing) {
                    unfollowUser(currentUser, holder.itemView)
                    currentUser.isFollowing = false
                    text = "Follow" // set the follow button text to "Follow" after unfollowing
                } else {
                    followUser(currentUser, holder.itemView)
                    currentUser.isFollowing = true
                    text = "Unfollow" // set the follow button text to "Unfollow" after following
                }
                setTextColor(if (currentUser.isFollowing) context.getColor(R.color.background) else context.getColor(R.color.white))
                setBackgroundColor(if (currentUser.isFollowing) context.getColor(R.color.white) else context.getColor(R.color.background))
            }
        }

        // Set the following state based on shared preferences
        val sharedPrefs = holder.itemView.context.getSharedPreferences("UserPreferences", Context.MODE_PRIVATE)
        val isFollowing = sharedPrefs.getBoolean(currentUser.name, false)
        currentUser.isFollowing = isFollowing
    }


    override fun getItemCount() = userList.size

    private fun isFollowing(user: User): Boolean {
        return followedUserList.contains(user)
    }

    private fun followUser(user: User, itemView: View) {
        // Follow the user
        user.isFollowing = true

        // Store following state in shared preferences
        val sharedPrefs = itemView.context.getSharedPreferences("MY_PREFS", Context.MODE_PRIVATE)
        sharedPrefs.edit().putBoolean(user.name, true).apply()
    }

    private fun unfollowUser(user: User, itemView: View) {
        // Unfollow the user
        user.isFollowing = false

        // Store following state in shared preferences
        val sharedPrefs = itemView.context.getSharedPreferences("MY_PREFS", Context.MODE_PRIVATE)
        sharedPrefs.edit().putBoolean(user.name, false).apply()
    }


}

data class User(val name: String, val profilePicResId: Int, var isFollowing: Boolean = false)


