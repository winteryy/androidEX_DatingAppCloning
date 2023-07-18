package com.example.dating.auth

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.Button
import androidx.databinding.DataBindingUtil
import com.example.dating.MainActivity
import com.example.dating.R
import com.example.dating.databinding.ActivityJoinBinding
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase

class JoinActivity : AppCompatActivity() {

    private val TAG = JoinActivity::class.java.simpleName

    private lateinit var binding : ActivityJoinBinding
    private lateinit var auth: FirebaseAuth

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = DataBindingUtil.setContentView(this, R.layout.activity_join)
        auth = Firebase.auth

        binding.joinBtn.setOnClickListener {
            val email = binding.emailArea.text.toString()
            val password = binding.pwdArea.text.toString()

            auth.createUserWithEmailAndPassword(email, password)
                .addOnCompleteListener(this) { task ->
                    if (task.isSuccessful) {
                        Log.d(TAG, "createUserWithEmail:success")
                        val intent = Intent(this, MainActivity::class.java)
                        startActivity(intent)

                    } else {

                        Log.w(TAG, "createUserWithEmail:failure", task.exception)

                    }
                }

        }

    }
}