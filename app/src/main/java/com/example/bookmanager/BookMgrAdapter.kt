package com.example.bookmanager

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.BaseAdapter
import android.widget.ImageView
import android.widget.ProgressBar
import android.widget.TextView
import com.bumptech.glide.Glide

class BookMgrAdapter(val context: Context, val bookList: ArrayList<Book>) : BaseAdapter() {
    override fun getCount(): Int = bookList.size
    override fun getItem(position: Int): Any = bookList[position]
    override fun getItemId(position: Int): Long = bookList[position].isbn

    override fun getView(position: Int, convertView: View?, parent: ViewGroup?): View {
        val view: View = LayoutInflater.from(context).inflate(R.layout.book_mgr_item, null)

        val thumbnail = view.findViewById<ImageView>(R.id.img_thumbnail);
        val title = view.findViewById<TextView>(R.id.txt_title);
        val author = view.findViewById<TextView>(R.id.txt_author);
        val isbn = view.findViewById<TextView>(R.id.txt_isbn);
        val progress = view.findViewById<ProgressBar>(R.id.progressBar_page);

        val book = bookList[position]
        Glide.with(view)
            .load(book.thumbnail)
            .placeholder(R.drawable.book_icon)
            .fallback(R.drawable.book_icon)
            .error(R.drawable.book_icon)
            .into(thumbnail)
        title.text = book.title
        author.text = book.author
        isbn.text = book.isbn.toString()
        progress.progress = 30

        return view
    }
}