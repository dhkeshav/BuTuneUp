package com.example.butuneup

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.app.AlertDialog
import android.content.DialogInterface
import android.Manifest
import android.content.ContentValues.TAG
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.provider.MediaStore
import android.provider.OpenableColumns
import android.text.InputType
import android.util.Log
import android.widget.*
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.StorageReference
import com.google.firebase.storage.UploadTask
import java.io.File



private val firebaseStorage = FirebaseStorage.getInstance()

class UploadMusicActivity : AppCompatActivity() {

    private lateinit var selectButton: Button
    private lateinit var uploadButton: Button
    private lateinit var selectedFileTextView: TextView
    private lateinit var progressBar: ProgressBar

    private var selectedFileUri: Uri? = null

    private lateinit var storageReference: StorageReference

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_upload_music)



        selectButton = findViewById(R.id.select_button)
        uploadButton = findViewById(R.id.upload_button)
        selectedFileTextView = findViewById(R.id.selected_file_textview)
        progressBar = findViewById(R.id.progress_bar)

        // Request permission to read external storage if not granted
        if (ContextCompat.checkSelfPermission(
                this,
                Manifest.permission.READ_EXTERNAL_STORAGE
            ) != PackageManager.PERMISSION_GRANTED
        ) {
            ActivityCompat.requestPermissions(
                this,
                arrayOf(Manifest.permission.READ_EXTERNAL_STORAGE),
                1
            )
        }

        storageReference = firebaseStorage.reference.child("uploads")

        selectButton.setOnClickListener {
            val intent = Intent(Intent.ACTION_GET_CONTENT)
            intent.type = "audio/mpeg"
            startActivityForResult(intent, 2)
        }

        fun getFileName(uri: Uri): String {
            var result: String? = null
            if (uri.scheme.equals("content")) {
                val cursor = contentResolver.query(uri, null, null, null, null)
                cursor.use {
                    if (it != null && it.moveToFirst()) {
                        val index = it.getColumnIndex(OpenableColumns.DISPLAY_NAME)
                        result = if (index != -1) it.getString(index) else "default_file_name"
                    }
                }
            }
            if (result == null) {
                result = uri.path
                val cut = result!!.lastIndexOf('/')
                if (cut != -1) {
                    result = result!!.substring(cut + 1)
                }
            }
            return result!!
        }



        uploadButton.setOnClickListener {
            selectedFileUri?.let { uri ->
                // Prompt the user to enter a custom name for the file
                val builder = AlertDialog.Builder(this)
                builder.setTitle("Enter name for the file")

                val input = EditText(this)
                input.inputType = InputType.TYPE_CLASS_TEXT
                builder.setView(input)

                builder.setPositiveButton("OK") { _, _ ->
                    val customName = input.text.toString().trim()
                    if (customName.isNotEmpty()) {
                        // Use the custom name for the file
                        val fileRef = storageReference.child(customName)
                        val uploadTask = fileRef.putFile(uri)

                        uploadTask.addOnProgressListener { taskSnapshot ->
                            val progress = (100.0 * taskSnapshot.bytesTransferred / taskSnapshot.totalByteCount).toInt()
                            progressBar.progress = progress
                        }

                        uploadTask.addOnSuccessListener {
                            // File upload successful
                            // Get the download URL of the uploaded file
                            fileRef.downloadUrl.addOnSuccessListener { downloadUrl ->
                                // Go to home screen after successful upload
                                val intent = Intent(this, HomeActivity::class.java)
                                intent.putExtra("fileName", customName)
                                intent.putExtra("downloadUrl", downloadUrl.toString())
                                startActivity(intent)
                                finish()
                            }
                        }.addOnFailureListener {
                            Toast.makeText(this, "Failed to upload file: ${it.message}", Toast.LENGTH_SHORT).show()
                            Log.e(TAG, "Failed to upload file", it)
                        }
                    } else {
                        Toast.makeText(this, "Please enter a valid name for the file", Toast.LENGTH_SHORT).show()
                    }
                }

                builder.setNegativeButton("Cancel") { dialog, _ ->
                    dialog.cancel()
                }

                builder.show()
            }
        }


    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        if (requestCode == 2 && resultCode == RESULT_OK) {
            selectedFileUri = data?.data
            selectedFileTextView.text = selectedFileUri?.path
        }
    }
}

