package com.example.dating.setting

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import androidx.core.content.res.FontResourcesParserCompat.FamilyResourceEntry
import androidx.databinding.DataBindingUtil
import com.bumptech.glide.Glide
import com.example.dating.R
import com.example.dating.auth.UserDataModel
import com.example.dating.databinding.ActivityMyPageBinding
import com.example.dating.utils.FBAuthUtils
import com.example.dating.utils.FBRef
import com.google.android.gms.tasks.OnCompleteListener
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.ValueEventListener
import com.google.firebase.ktx.Firebase
import com.google.firebase.storage.ktx.storage

class MyPageActivity : AppCompatActivity() {

    private val TAG = MyPageActivity::class.java.simpleName

    private lateinit var binding: ActivityMyPageBinding

    private val uid = FBAuthUtils.getUid()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = DataBindingUtil.setContentView(this, R.layout.activity_my_page)
        getMyData()
    }

    private fun getMyData(){

        val postListener = object : ValueEventListener {
            override fun onDataChange(dataSnapshot: DataSnapshot) {
                val data = dataSnapshot.getValue(UserDataModel::class.java)

                binding.myUid.text = uid
                binding.myNickname.text = data?.nickname
                binding.myAge.text = data?.age
                binding.myCity.text = data?.city
                binding.myGender.text = data?.gender

                val storageRef = Firebase.storage.reference.child(data?.uid.toString())
                storageRef.downloadUrl.addOnCompleteListener(OnCompleteListener {
                    if(it.isSuccessful){
                        Glide.with(baseContext)
                            .load(it.result)
                            .into(binding.myImage)
                    }
                })

            }

            override fun onCancelled(databaseError: DatabaseError) {
                // Getting Post failed, log a message
                Log.w(TAG, "loadPost:onCancelled", databaseError.toException())
            }
        }
        FBRef.userInfoRef.child(uid).addValueEventListener(postListener)
    }
}