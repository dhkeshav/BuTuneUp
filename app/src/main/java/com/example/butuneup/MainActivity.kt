package com.example.butuneup

import android.accounts.Account
import android.app.Activity
import android.app.Instrumentation.ActivityResult
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Button
import android.widget.ImageView
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import com.bumptech.glide.Glide
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInAccount
import com.google.android.gms.auth.api.signin.GoogleSignInClient
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.android.gms.tasks.Task
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.GoogleAuthProvider

class MainActivity : AppCompatActivity() {

    private lateinit var auth : FirebaseAuth
    private lateinit var googleSignInClient : GoogleSignInClient
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        val imageView: ImageView = findViewById(R.id.imageView)
        Glide.with(this).load(R.drawable.main_back).into(imageView)

        auth = FirebaseAuth.getInstance()

        if (auth.currentUser != null) {
            // user is already signed in
            val intent : Intent = Intent(this , NEWHOMEACTIVITY :: class.java)
            intent.putExtra("email" , auth.currentUser?.email)
            intent.putExtra("name" , auth.currentUser?.displayName)
            startActivity(intent)
            finish()
        } else {
            // user is not signed in, proceed with sign in process
            val gso = GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestIdToken(getString(R.string.default_web_client_id))
                .requestEmail()
                .requestProfile()
                .build()

            googleSignInClient = GoogleSignIn.getClient(this , gso)

            findViewById<Button>(R.id.gSignInButton).setOnClickListener {
                signInGoogle()
            }
        }




    }

    private fun signInGoogle() {
        val signInIntent = googleSignInClient.signInIntent
        launcher.launch(signInIntent)
    }

    private val launcher = registerForActivityResult(ActivityResultContracts.StartActivityForResult()){
            result ->
        if (result.resultCode == Activity.RESULT_OK){
            val task = GoogleSignIn.getSignedInAccountFromIntent(result.data)
            handleResults(task)
        }
    }

    private fun handleResults(task: Task<GoogleSignInAccount>) {
        if(task.isSuccessful){
            val account : GoogleSignInAccount? = task.result
            if(account != null ){
                updateUI(account)
            }
        }
        else{
            Toast.makeText(this , task.exception.toString() , Toast.LENGTH_SHORT).show()

        }
    }

    private fun updateUI(account: GoogleSignInAccount) {
        val credential = GoogleAuthProvider.getCredential(account.idToken, null)
        auth.signInWithCredential(credential).addOnCompleteListener { task ->
            if (task.isSuccessful) {
                val intent: Intent = Intent(this, NEWHOMEACTIVITY::class.java)
                intent.putExtra("email", account.email)
                intent.putExtra("name", account.displayName)
                intent.putExtra("photoUrl", account.photoUrl)
                // add this line to put the photo URL in the intent
                startActivity(intent)
            } else {
                Toast.makeText(this, task.exception.toString(), Toast.LENGTH_SHORT).show()
            }
        }
    }


}