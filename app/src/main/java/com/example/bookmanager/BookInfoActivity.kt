package com.example.bookmanager

import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteException
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Toast
import com.bumptech.glide.Glide
import com.example.bookmanager.databinding.ActivityBookInfoBinding
import java.time.LocalDate
import java.time.Period


class BookInfoActivity : AppCompatActivity() {
    private lateinit var binding: ActivityBookInfoBinding
    private lateinit var database: SQLiteDatabase

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityBookInfoBinding.inflate(layoutInflater)
        setContentView(binding.root)
        // DB
        database = DBHelper(this).writableDatabase
        // Extra Data
        val mode = intent.getStringExtra("mode")
        val isbn = intent.getLongExtra("isbn", 0L)
        val thumbnail = intent.getStringExtra("thumbnail")
        val title = intent.getStringExtra("title")
        val author = intent.getStringExtra("author")
        val publisher = intent.getStringExtra("publisher")
        val description = intent.getStringExtra("description")

        // Common Interface
        // 뒤로 가기
        binding.btnBack.setOnClickListener{ finish() }
        // 책 정보 보기
        binding.btnInfo.setOnClickListener{
            binding.groupInfo.visibility = View.VISIBLE
            binding.groupMgr.visibility = View.GONE
        }
        // 독서 활동 보기
        binding.btnMgr.setOnClickListener{
            binding.groupInfo.visibility = View.GONE
            binding.groupMgr.visibility = View.VISIBLE
        }
        // 렌더
        Glide.with(this)
            .load(thumbnail)
            .placeholder(R.drawable.book_icon)
            .error(R.drawable.book_icon)
            .into(binding.imgThumbnail)
        binding.txtIsbn.text = isbn.toString()
        binding.txtTitle.text = title
        binding.txtAuthor.text = author
        binding.txtPublisher.text = publisher
        binding.txtDescription.text = description


        if(mode.equals("Register")){
            // 메뉴 선택
            binding.menuRegister.visibility = View.VISIBLE
            binding.menuManager.visibility = View.GONE
            // 독서 활동 비활성화
            binding.btnMgr.visibility = View.GONE


            // 등록하기
            binding.btnRegister.setOnClickListener{
                val sqlRegister = """
                INSERT OR IGNORE INTO Book(isbn, thumbnail, title, author, publisher, description) 
                VALUES 
                (?, ?, ?, ?, ?, ?);
                """.trimIndent()
                val sqlOverlapCheck = "SELECT isbn FROM Book WHERE isbn = ?"

                val vals = arrayOf(
                    isbn,
                    thumbnail,
                    title,
                    author,
                    publisher,
                    description
                )

                try{
                    val cursor = database.rawQuery(sqlOverlapCheck, arrayOf(isbn.toString()))
                    if(cursor.moveToNext()) {
                        Toast.makeText(this, "이미 등록된 책입니다.", Toast.LENGTH_SHORT).show()
                    } else{
                        database.execSQL(sqlRegister, vals)
                        Toast.makeText(this, "책이 등록되었습니다.", Toast.LENGTH_SHORT).show()
                    }
                }catch(error: SQLiteException){
                    Log.e("BookManager", error.message.toString())
                    Toast.makeText(this, "책을 등록할 수 없습니다.", Toast.LENGTH_SHORT).show()
                }
            }
        } else if(mode.equals("Manager")){
            // 메뉴 선택
            binding.menuRegister.visibility = View.GONE
            binding.menuManager.visibility = View.VISIBLE
            // 독서 활동 활성화
            binding.btnMgr.visibility = View.VISIBLE

            // 서재에서 제거하기
            binding.btnDelete.setOnClickListener{
                val sql = "DELETE FROM Book WHERE isbn = ?"
                val vals = arrayOf(isbn.toString())
                database.execSQL(sql, vals)
                finish()
            }

            // 독서 기간
            var reloadDays = {
                    start_at: String, end_at: String ->
                val last_at = if(end_at.equals("")){
                    LocalDate.now()
                } else{
                    LocalDate.parse(end_at)
                }
                val days = Period.between(last_at, LocalDate.parse(start_at)).days

                binding.txtDays.text = "${days}일"
                binding.txtStartAt.text = start_at
                binding.txtEndAt.text = end_at
            }
            val start_at = intent.getStringExtra("start_at") ?: ""
            val end_at = intent.getStringExtra("end_at") ?: ""
            reloadDays(start_at, end_at)

            // 독서 종료/재개
            binding.btnBreak.text = if(end_at.equals("")) {
                "독서 종료"
            } else{
                "독서 재개"
            }
            binding.btnBreak.setOnClickListener{
                val vals = arrayOf(isbn.toString())
                if(binding.btnBreak.text == "독서 종료"){
                    binding.btnBreak.text = "독서 재개"
                    val sql = "UPDATE Book SET end_at = date('now','localtime') WHERE isbn = ?"
                    database.execSQL(sql, vals)
                    reloadDays(start_at, LocalDate.now().toString())
                } else{
                    binding.btnBreak.text = "독서 종료"
                    val sql = "UPDATE Book SET end_at = '' WHERE isbn = ?"
                    database.execSQL(sql, vals)
                    reloadDays(start_at, "")
                }
            }
        }
    }
}
