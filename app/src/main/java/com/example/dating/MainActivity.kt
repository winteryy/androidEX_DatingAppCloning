package com.example.dating

import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.Context
import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import com.bumptech.glide.Glide
import com.example.dating.auth.IntroActivity
import com.example.dating.auth.UserDataModel

import com.example.dating.databinding.ActivityMainBinding
import com.example.dating.setting.MyPageActivity
import com.example.dating.setting.SettingActivity
import com.example.dating.slider.CardStackAdapter
import com.example.dating.utils.FBAuthUtils
import com.example.dating.utils.FBRef
import com.example.dating.utils.MyInfo
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.ValueEventListener
import com.google.firebase.ktx.Firebase
import com.google.firebase.storage.ktx.storage
import com.yuyakaido.android.cardstackview.CardStackLayoutManager
import com.yuyakaido.android.cardstackview.CardStackListener
import com.yuyakaido.android.cardstackview.Direction
import java.util.jar.Manifest

class MainActivity : AppCompatActivity() {

    lateinit var cardStackAdapter: CardStackAdapter
    lateinit var manager : CardStackLayoutManager

    private val TAG = MainActivity::class.java.simpleName

    private lateinit var binding: ActivityMainBinding

    private val userDataList = mutableListOf<UserDataModel>()
    private var userCount = 0
    private val uid = FBAuthUtils.getUid()
    private lateinit var currentUserGender: String

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityMainBinding.inflate(layoutInflater)

//        getUserDataList()
        getMyUserData()

        manager = CardStackLayoutManager(baseContext, object: CardStackListener{
            override fun onCardDragging(direction: Direction?, ratio: Float) {
            }

            override fun onCardSwiped(direction: Direction?) {

                if(direction == Direction.Right){
                    userLikeOther(uid, userDataList[userCount].uid.toString())
                }else if(direction == Direction.Left){
                }

                userCount = userCount+1
//                userCount.inc()
                Log.d(TAG, userCount.toString())
                if(userCount==userDataList.size){
                    getUserDataList(currentUserGender)
                    userCount = 0
                    Toast.makeText(this@MainActivity, "유저갱신", Toast.LENGTH_SHORT).show()
                }

            }

            override fun onCardRewound() {
            }

            override fun onCardCanceled() {
            }

            override fun onCardAppeared(view: View?, position: Int) {
            }

            override fun onCardDisappeared(view: View?, position: Int) {
            }

        })

        binding.cardStackView.layoutManager = manager
        cardStackAdapter = CardStackAdapter(baseContext, userDataList)
        binding.cardStackView.adapter = cardStackAdapter

        binding.settingIcon.setOnClickListener {

            val intent = Intent(this, SettingActivity::class.java)
            startActivity(intent)
        }

        setContentView(binding.root)

    }

    private fun createNotificationChannel() {
        // Create the NotificationChannel, but only on API 26+ because
        // the NotificationChannel class is new and not in the support library
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val name = "name"
            val descriptionText = "description"
            val importance = NotificationManager.IMPORTANCE_DEFAULT
            val channel = NotificationChannel("Test_Channel", name, importance).apply {
                description = descriptionText
            }
            // Register the channel with the system
            val notificationManager: NotificationManager =
                getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
            notificationManager.createNotificationChannel(channel)
        }
    }

    private fun sendNotification(){
        var builder = NotificationCompat.Builder(this, "Test_Channel")
            .setSmallIcon(R.drawable.ic_launcher_background)
            .setContentTitle("매칭완료")
            .setContentText("매칭이 완료되었습니다")
            .setPriority(NotificationCompat.PRIORITY_DEFAULT)
        with(NotificationManagerCompat.from(this)) {
            // notificationId is a unique int for each notification that you must define
            notify(123, builder.build())
            Log.d(TAG, "check nofify")
        }
    }

    private fun getOtherUserLikeList(otherUid: String){
        val postListener = object : ValueEventListener {
            override fun onDataChange(dataSnapshot: DataSnapshot) {

                for (dataModel in dataSnapshot.children){
                    val likeUserKey = dataModel.key.toString()
                    if(likeUserKey.equals(uid)){
                        Toast.makeText(this@MainActivity, "매칭완료", Toast.LENGTH_SHORT).show()
                        createNotificationChannel()
                        sendNotification()
                    }
                }
            }

            override fun onCancelled(databaseError: DatabaseError) {
                // Getting Post failed, log a message
                Log.w(TAG, "loadPost:onCancelled", databaseError.toException())
            }
        }
        FBRef.userLikeRef.child(otherUid).addValueEventListener(postListener)
    }
    private fun userLikeOther(myUid: String, otherUid: String){
        FBRef.userLikeRef.child(myUid).child(otherUid).setValue("true")

        getOtherUserLikeList(otherUid)
    }

    private fun getMyUserData(){
        val postListener = object : ValueEventListener {
            override fun onDataChange(dataSnapshot: DataSnapshot) {
                val data = dataSnapshot.getValue(UserDataModel::class.java)
                currentUserGender = data?.gender.toString()
                MyInfo.myNickname = data?.nickname.toString()

                getUserDataList(currentUserGender)

            }

            override fun onCancelled(databaseError: DatabaseError) {
                // Getting Post failed, log a message
                Log.w(TAG, "loadPost:onCancelled", databaseError.toException())
            }
        }
        FBRef.userInfoRef.child(uid).addValueEventListener(postListener)
    }

    private fun getUserDataList(currentUserGender: String){

        val postListener = object : ValueEventListener {
            override fun onDataChange(dataSnapshot: DataSnapshot) {

                for (dataModel in dataSnapshot.children){
                    val user = dataModel.getValue(UserDataModel::class.java)
                    if(!user!!.gender.toString().equals(currentUserGender)){
                        userDataList.add(user!!)
                    }

                }
                cardStackAdapter.notifyDataSetChanged()
            }

            override fun onCancelled(databaseError: DatabaseError) {
                // Getting Post failed, log a message
                Log.w(TAG, "loadPost:onCancelled", databaseError.toException())
            }
        }
        FBRef.userInfoRef.addValueEventListener(postListener)

    }
}