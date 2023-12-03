package com.example.bookmanager

import android.content.Intent
import android.database.sqlite.SQLiteDatabase
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.AdapterView
import androidx.activity.result.contract.ActivityResultContracts
import com.example.bookmanager.databinding.ActivityMainBinding

class MainActivity : AppCompatActivity() {
    private lateinit var binding: ActivityMainBinding
    private lateinit var database: SQLiteDatabase


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(getLayoutInflater())
        setContentView(binding.root)
        // DB
        database = DBHelper(this).readableDatabase

        // 검색 하기
        val requestLauncher = registerForActivityResult(
            ActivityResultContracts.StartActivityForResult()){
            loadLibrary()
        }
        binding.btnSearch.setOnClickListener{
            val intent = Intent(this, SearchActivity::class.java)
            requestLauncher.launch(intent)
        }
        // 내 서재 보기
        loadLibrary()
    }

    fun loadLibrary(){
        val sql = "SELECT * FROM BOOK"
        val cursor = database.rawQuery(sql, null)

        val bookList = arrayListOf<Book>()
        while(cursor.moveToNext()){
            val isbn = cursor.getLong(0)
            val thumbnail = cursor.getString(1)
            val title = cursor.getString(2)
            val author = cursor.getString(3)
            val publisher = cursor.getString(4)
            val description = cursor.getString(5)

            bookList.add(Book(
                isbn,
                thumbnail,
                title,
                author,
                publisher,
                description
            ))
        }
        binding.booklist.adapter = BookMgrAdapter(this, bookList)
        binding.booklist.onItemClickListener = AdapterView.OnItemClickListener {
                parent, view, position, id ->
            val selectedBook = parent.getItemAtPosition(position) as Book
            Log.d("Test", selectedBook.title)
        }
    }
}