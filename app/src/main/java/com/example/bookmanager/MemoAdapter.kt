package com.example.bookmanager

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.BaseAdapter
import android.widget.ImageView
import android.widget.TextView
import kotlin.math.pow

class MemoAdapter(val context: Context, val memoList: ArrayList<Memo>) : BaseAdapter() {
    override fun getCount(): Int = memoList.size
    override fun getItem(position: Int): Any = memoList[position]
    override fun getItemId(position: Int): Long = memoList[position].id

    override fun getView(position: Int, convertView: View?, parent: ViewGroup?): View {
        val view: View = LayoutInflater.from(context).inflate(R.layout.memo_item, null)

        val title = view.findViewById<TextView>(R.id.txt_title)
        val contents = view.findViewById<TextView>(R.id.txt_contents)
        val updated_at = view.findViewById<TextView>(R.id.txt_updated_at)

        val memo = memoList[position]
        title.text = memo.title
        contents.text = memo.contents
        updated_at.text = memo.updated_at

        return view
    }
}