package com.example.dating.utils

import com.google.firebase.auth.FirebaseAuth

class FBAuthUtils {

    companion object{

        private lateinit var auth: FirebaseAuth
        fun getUid(): String{
            auth = FirebaseAuth.getInstance()
            return auth.currentUser?.uid.toString()
        }
    }

}