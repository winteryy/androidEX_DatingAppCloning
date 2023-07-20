package com.example.dating.auth

import android.app.Activity
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.drawable.BitmapDrawable
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.Button
import androidx.activity.result.ActivityResultCallback
import androidx.activity.result.contract.ActivityResultContract
import androidx.activity.result.contract.ActivityResultContracts
import androidx.databinding.DataBindingUtil
import com.example.dating.MainActivity
import com.example.dating.R
import com.example.dating.databinding.ActivityJoinBinding
import com.example.dating.utils.FBRef
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase
import com.google.firebase.storage.ktx.storage
import java.io.ByteArrayOutputStream

class JoinActivity : AppCompatActivity() {

    private val TAG = JoinActivity::class.java.simpleName

    private lateinit var binding : ActivityJoinBinding
    private lateinit var auth: FirebaseAuth

    private var nickname = ""
    private var gender = ""
    private var city = ""
    private var age = ""
    private var uid = ""

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = DataBindingUtil.setContentView(this, R.layout.activity_join)
        auth = Firebase.auth

        binding.joinBtn.setOnClickListener {
            val email = binding.emailArea.text.toString()
            val password = binding.pwdArea.text.toString()

            gender = binding.genderArea.text.toString()
            city = binding.cityArea.text.toString()
            age = binding.ageArea.text.toString()
            nickname = binding.nicknameArea.text.toString()

            auth.createUserWithEmailAndPassword(email, password)
                .addOnCompleteListener(this) { task ->
                    if (task.isSuccessful) {

                        val user = auth.currentUser
                        uid = user?.uid.toString()

                        val userModel = UserDataModel(uid, nickname, age, gender, city)

                        uploadImage(uid)

                        FBRef.userInfoRef.child(uid).setValue(userModel)

                        val intent = Intent(this, MainActivity::class.java)
                        intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
                        startActivity(intent)

                    } else {

                        Log.w(TAG, "createUserWithEmail:failure", task.exception)

                    }
                }
        }

        val getAction = registerForActivityResult(
            ActivityResultContracts.GetContent(),
            ActivityResultCallback {
                binding.imageArea.setImageURI(it)
            }
        )
        binding.imageArea.setOnClickListener {
            getAction.launch("image/*")

        }

    }

    private fun uploadImage(uid : String){

        val storage = Firebase.storage
        val storageRef = storage.reference.child(uid)

        binding.imageArea.isDrawingCacheEnabled = true
        binding.imageArea.buildDrawingCache()
        val bitmap = (binding.imageArea.drawable as BitmapDrawable).bitmap
        val baos = ByteArrayOutputStream()
        bitmap.compress(Bitmap.CompressFormat.JPEG, 100, baos)
        val data = baos.toByteArray()

        var uploadTask = storageRef.putBytes(data)
        uploadTask.addOnFailureListener {
            // Handle unsuccessful uploads
        }.addOnSuccessListener { taskSnapshot ->
            // taskSnapshot.metadata contains file metadata such as size, content-type, etc.
            // ...
        }
    }

}