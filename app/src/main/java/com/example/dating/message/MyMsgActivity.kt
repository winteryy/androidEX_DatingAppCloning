package com.example.dating.message

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.ListView
import com.example.dating.R
import com.example.dating.auth.UserDataModel
import com.example.dating.utils.FBAuthUtils
import com.example.dating.utils.FBRef
import com.example.dating.utils.MyInfo
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.ValueEventListener

class MyMsgActivity : AppCompatActivity() {

    private val TAG = MyMsgActivity::class.java.simpleName

    lateinit var listViewAdapter: MsgAdapter
    val msgList = mutableListOf<MsgModel>()

    override fun onCreate(savedInstanceState: Bundle?) {

        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_my_msg)

        val listView = findViewById<ListView>(R.id.msgListView)
        listViewAdapter = MsgAdapter(this, msgList)
        listView.adapter = listViewAdapter

        getMyMsg()

    }

    private fun getMyMsg(){
        val postListener = object : ValueEventListener {
            override fun onDataChange(dataSnapshot: DataSnapshot) {
                msgList.clear()

                for (dataModel in dataSnapshot.children){
                    val msg = dataModel.getValue(MsgModel::class.java)
                    msgList.add(msg!!)
                }
                msgList.reverse()
                listViewAdapter.notifyDataSetChanged()

            }

            override fun onCancelled(databaseError: DatabaseError) {
                // Getting Post failed, log a message
                Log.w(TAG, "loadPost:onCancelled", databaseError.toException())
            }
        }
        FBRef.userMsgRef.child(FBAuthUtils.getUid()).addValueEventListener(postListener)
    }
}