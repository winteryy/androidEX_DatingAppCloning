package com.example.dating.message

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.BaseAdapter
import android.widget.TextView
import com.example.dating.R

class MsgAdapter(val context: Context, val items: MutableList<MsgModel>): BaseAdapter() {
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
        if(converView==null){
            converView = LayoutInflater.from(parent?.context).inflate(R.layout.list_view_item, parent, false)
        }

        val nickname = converView!!.findViewById<TextView>(R.id.listViewNicknameArea)
        val text = converView!!.findViewById<TextView>(R.id.nicknameArea)
        nickname.text = items[position].senderInfo
        text.text = items[position].sendText

        return converView!!

    }

}