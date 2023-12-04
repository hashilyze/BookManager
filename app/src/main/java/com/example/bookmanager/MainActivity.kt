package com.example.bookmanager

import android.content.Intent
import android.database.sqlite.SQLiteDatabase
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.AdapterView
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import com.example.bookmanager.databinding.ActivityMainBinding

class MainActivity : AppCompatActivity() {
    private lateinit var binding: ActivityMainBinding
    private lateinit var database: SQLiteDatabase
    private lateinit var reloadLauncher: ActivityResultLauncher<Intent>

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        database = DBHelper(this).readableDatabase // DB
        reloadLauncher = registerForActivityResult( // Intent Launcher
            ActivityResultContracts.StartActivityForResult()){
            loadLibrary()
        }

        // 검색 화면으로 이동
        binding.btnSearch.setOnClickListener{
            val intent = Intent(this, SearchActivity::class.java)
            reloadLauncher.launch(intent)
        }
        // 내 서재 보기
        loadLibrary()
    }

    private fun loadLibrary(){
        val bookList = arrayListOf<Book>().also{
            val sql = """
                SELECT isbn, thumbnail, title, 
                    author, publisher, description, 
                    start_at, end_at
                FROM BOOK
                """.trimIndent()
            val cursor = database.rawQuery(sql, null)
            while(cursor.moveToNext()){
                it.add(Book(
                    cursor.getLong(0),      // isbn
                    cursor.getString(1),    // thumbnail
                    cursor.getString(2),    // title
                    cursor.getString(3),    // author
                    cursor.getString(4),    // publisher
                    cursor.getString(5),    // description
                    cursor.getString(6),    // start_at
                    cursor.getString(7)     // end_at
                ))
            }
        }


        binding.booklist.adapter = BookMgrAdapter(this, bookList)
        binding.booklist.onItemClickListener = AdapterView.OnItemClickListener {
                parent, view, position, id ->
            val selectedBook = parent.getItemAtPosition(position) as Book

            var intent = Intent(this@MainActivity, BookInfoActivity::class.java).also{
                it.putExtra("mode", "Manager")
                it.putExtra("isbn", selectedBook.isbn)
                it.putExtra("thumbnail", selectedBook.thumbnail)
                it.putExtra("title", selectedBook.title)
                it.putExtra("author", selectedBook.author)
                it.putExtra("publisher", selectedBook.publisher)
                it.putExtra("description", selectedBook.description)
                it.putExtra("start_at", selectedBook.start_at)
                it.putExtra("end_at", selectedBook.end_at)
            }
            reloadLauncher.launch(intent)
        }
    }
}