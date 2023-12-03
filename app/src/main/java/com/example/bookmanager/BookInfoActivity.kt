package com.example.bookmanager

import android.database.sqlite.SQLiteDatabase
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Toast
import com.bumptech.glide.Glide
import com.example.bookmanager.databinding.ActivityBookInfoBinding


class BookInfoActivity : AppCompatActivity() {
    private lateinit var database: SQLiteDatabase

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        var binding = ActivityBookInfoBinding.inflate(getLayoutInflater())
        setContentView(binding.root)
        // DB
        database = DBHelper(this).readableDatabase
        // Extra Data
        val isbn = intent.getLongExtra("isbn", 0L)
        val thumbnail = intent.getStringExtra("thumbnail")
        val title = intent.getStringExtra("title")
        val author = intent.getStringExtra("author")
        val publisher = intent.getStringExtra("publisher")
        val description = intent.getStringExtra("description")

        // 뒤로 가기
        binding.btnBack.setOnClickListener{ finish() }
        // 등록
        binding.btnRegister.setOnClickListener{
            val sql = "INSERT OR IGNORE INTO BOOK VALUES(?, ?, ?, ?, ?, ?)"
            val vals = arrayOf(
                isbn,
                thumbnail,
                title,
                author,
                publisher,
                description
            )
            database.execSQL(sql, vals)
            Toast.makeText(this, "등록되었습니다.", Toast.LENGTH_SHORT)
        }

        // 렌더
        Glide.with(this)
            .load(thumbnail)
            .placeholder(R.drawable.book_icon)
            .fallback(R.drawable.book_icon)
            .error(R.drawable.book_icon)
            .into(binding.imgThumbnail)
        binding.txtIsbn.text = isbn.toString()
        binding.txtTitle.text = title
        binding.txtAuthor.text = author
        binding.txtPublisher.text = publisher
        binding.txtDescription.text = description
    }
}
