package com.example.dating.message

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.widget.Button
import android.widget.EditText
import android.widget.ListView
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import com.example.dating.R
import com.example.dating.auth.UserDataModel
import com.example.dating.message.fcm.NotiModel
import com.example.dating.message.fcm.PushNotification
import com.example.dating.message.fcm.RetrofitInstance
import com.example.dating.utils.FBAuthUtils
import com.example.dating.utils.FBRef
import com.example.dating.utils.MyInfo
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.ValueEventListener
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import okhttp3.Dispatcher

class MyLikeListActivity : AppCompatActivity() {

    private val TAG = MyLikeListActivity::class.java.simpleName
    private val myUid = FBAuthUtils.getUid()
    private val likeUserUidList = mutableListOf<String>()
    private val likeUserList = mutableListOf<UserDataModel>()

    lateinit var listViewAdapter: ListViewAdapter
    lateinit var getterUid: String
    lateinit var getterToken: String

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_my_like_list)

        val userListView = findViewById<ListView>(R.id.userListView)
        listViewAdapter = ListViewAdapter(this, likeUserList)
        userListView.adapter = listViewAdapter

//        userListView.setOnItemClickListener { parent, view, position, id ->
//            checkMatching(likeUserList[position].uid.toString())
//
//        }

        userListView.setOnItemLongClickListener { parent, view, position, id ->
//            Toast.makeText(this, "test", Toast.LENGTH_SHORT).show()
            getterUid = likeUserList[position].uid.toString()
            getterToken = likeUserList[position].token.toString()
            checkMatching(getterUid)


            return@setOnItemLongClickListener(true)
        }

        getMyLikeList()

    }

    private fun checkMatching(otherUid: String){
        val postListener = object : ValueEventListener {
            override fun onDataChange(dataSnapshot: DataSnapshot) {
                var check = false
                for (dataModel in dataSnapshot.children){
                    val likeUserKey = dataModel.key.toString()

                    if(likeUserKey.equals(myUid)){
                        Toast.makeText(this@MyLikeListActivity, "매칭이 된 유저입니다.", Toast.LENGTH_SHORT).show()
                        check = true
                        showDialog()

                        break
                    }
                }
                if(!check){
                    Toast.makeText(this@MyLikeListActivity, "매칭이 되지 않은 유저입니다.", Toast.LENGTH_SHORT).show()
                }
            }
            override fun onCancelled(databaseError: DatabaseError) {
                // Getting Post failed, log a message
                Log.w(TAG, "loadPost:onCancelled", databaseError.toException())
            }

        }
        FBRef.userLikeRef.child(otherUid).addValueEventListener(postListener)
    }

    private fun testPush(notification : PushNotification) = CoroutineScope(Dispatchers.IO).launch {
        RetrofitInstance.api.postNotification(notification)
    }

    private fun getUserDataList(){

        val postListener = object : ValueEventListener {
            override fun onDataChange(dataSnapshot: DataSnapshot) {

                for (dataModel in dataSnapshot.children){
                    val user = dataModel.getValue(UserDataModel::class.java)

                    if(likeUserUidList.contains(user?.uid)){
                        likeUserList.add(user!!)
                    }
                }
                Log.d(TAG, likeUserList.toString())
                listViewAdapter.notifyDataSetChanged()
            }

            override fun onCancelled(databaseError: DatabaseError) {
                // Getting Post failed, log a message
                Log.w(TAG, "loadPost:onCancelled", databaseError.toException())
            }
        }
        FBRef.userInfoRef.addValueEventListener(postListener)

    }

    private fun getMyLikeList(){
        val postListener = object : ValueEventListener {
            override fun onDataChange(dataSnapshot: DataSnapshot) {
                for (dataModel in dataSnapshot.children){
                    likeUserUidList.add(dataModel.key.toString())
                }
                getUserDataList()
            }

            override fun onCancelled(databaseError: DatabaseError) {
                // Getting Post failed, log a message
                Log.w(TAG, "loadPost:onCancelled", databaseError.toException())
            }
        }
        FBRef.userLikeRef.child(myUid).addValueEventListener(postListener)
    }

    private fun showDialog(){
        val mDialogView = LayoutInflater.from(this).inflate(R.layout.custom_dialog, null)
        val mBuilder = AlertDialog.Builder(this)
            .setView(mDialogView)
            .setTitle("메세지 보내기")

        val mAlertDialog = mBuilder.show()

        val sendBtn = mAlertDialog.findViewById<Button>(R.id.sendBtn)
        val textArea = mAlertDialog.findViewById<EditText>(R.id.sendTextArea)

        sendBtn?.setOnClickListener {

            val msgText = textArea!!.text.toString()
            val msgModel = MsgModel(MyInfo.myNickname, msgText)

            FBRef.userMsgRef.child(getterUid).push().setValue(msgModel)

            val notiModel = NotiModel(MyInfo.myNickname, msgText)
            val pushModel = PushNotification(notiModel, getterToken)

            testPush(pushModel)

            mAlertDialog.dismiss()
        }
    }
}