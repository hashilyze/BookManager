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
import org.w3c.dom.Text

class BookInfoAdapter(val context: Context, val bookList: ArrayList<Book>) : BaseAdapter() {
    override fun getCount(): Int {
        return bookList.size
    }

    override fun getItem(position: Int): Any {
        return bookList[position]
    }

    override fun getItemId(position: Int): Long {
        return bookList[position].isbn
    }

    override fun getView(position: Int, convertView: View?, parent: ViewGroup?): View {
        val view: View = LayoutInflater.from(context).inflate(R.layout.book_info_item, null)

        val thumbnail = view.findViewById<ImageView>(R.id.img_thumbnail);
        val title = view.findViewById<TextView>(R.id.txt_title);
        val author = view.findViewById<TextView>(R.id.txt_author);
        val isbn = view.findViewById<TextView>(R.id.txt_isbn);
        val description = view.findViewById<TextView>(R.id.txt_description);

        val book = bookList[position]

        Glide.with(view)
            .load(book.thumbnail)
            .placeholder(R.drawable.book_icon)
            .fallback(R.drawable.book_icon)
            .error(R.drawable.book_icon)
            .into(thumbnail)
//        thumbnail.setImageResource(book.thumbnail)
        title.text = book.title
        author.text = book.author
        isbn.text = book.isbn.toString()
        description.text =book.description

        return view
    }
}