package com.example.dating.message

import android.content.Context
import android.text.Layout
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.BaseAdapter
import android.widget.TextView
import com.example.dating.R
import com.example.dating.auth.UserDataModel

class ListViewAdapter(val context: Context, val items: MutableList<UserDataModel>): BaseAdapter() {
    override fun getCount(): Int {
        return items.size
    }

    override fun getItem(position: Int): Any {
        return items[position]
    }

    override fun getItemId(position: Int): Long {
        return position.toLong()
    }

    override fun getView(position: Int, convertView: View?, parent: ViewGroup?): View {
        var converView = convertView
        if(converView == null){
            converView = LayoutInflater.from(parent?.context).inflate(R.layout.list_view_item, parent, false)
        }
        converView?.findViewById<TextView>(R.id.nicknameArea)?.text = items[position].nickname


        return converView!!
    }
}