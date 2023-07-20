package com.example.dating.utils

import com.google.firebase.database.ktx.database
import com.google.firebase.ktx.Firebase

class FBRef {

    companion object{
        val database = Firebase.database
        val userInfoRef = database.getReference("userInfo")
        val userLikeRef = database.getReference("userLike")
    }

}