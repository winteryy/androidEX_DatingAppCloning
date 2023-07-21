package com.example.dating.message

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.ListView
import android.widget.Toast
import com.example.dating.R
import com.example.dating.auth.UserDataModel
import com.example.dating.utils.FBAuthUtils
import com.example.dating.utils.FBRef
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.ValueEventListener

class MyLikeListActivity : AppCompatActivity() {

    private val TAG = MyLikeListActivity::class.java.simpleName
    private val myUid = FBAuthUtils.getUid()
    private val likeUserUidList = mutableListOf<String>()
    private val likeUserList = mutableListOf<UserDataModel>()

    lateinit var listViewAdapter: ListViewAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_my_like_list)

        val userListView = findViewById<ListView>(R.id.userListView)
        listViewAdapter = ListViewAdapter(this, likeUserList)
        userListView.adapter = listViewAdapter

        userListView.setOnItemClickListener { parent, view, position, id ->
            checkMatching(likeUserList[position].uid.toString())
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
                        Toast.makeText(this@MyLikeListActivity, "매칭이 되었습니다.", Toast.LENGTH_SHORT).show()
                        check = true
                        break
                    }
                }
                if(!check){
                    Toast.makeText(this@MyLikeListActivity, "매칭이 되지 않았습니다.", Toast.LENGTH_SHORT).show()
                }
            }
            override fun onCancelled(databaseError: DatabaseError) {
                // Getting Post failed, log a message
                Log.w(TAG, "loadPost:onCancelled", databaseError.toException())
            }

        }
        FBRef.userLikeRef.child(otherUid).addValueEventListener(postListener)
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
}