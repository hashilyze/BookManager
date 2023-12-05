package com.example.bookmanager

import android.content.Intent
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteException
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Toast
import com.bumptech.glide.Glide
import com.example.bookmanager.databinding.ActivityBookDetailBinding
import java.time.LocalDate
import java.time.Period


class BookDetailActivity : AppCompatActivity() {
    private lateinit var binding: ActivityBookDetailBinding
    private lateinit var database: SQLiteDatabase

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityBookDetailBinding.inflate(layoutInflater)
        setContentView(binding.root)
        database = DBHelper(this).writableDatabase // DB

        val action = intent.getStringExtra("action") ?: ""
        if(!"(Register|Manager)".toRegex().matches(action)){
            Log.e("BookManager", "Not Implemented BookDetail's action")
            Toast.makeText(this, "잘못된 접근입니다.", Toast.LENGTH_SHORT)
            finish()
            return
        }

        // 도서 정보
        val isbn = intent.getLongExtra("isbn", 0L)
        val thumbnail = intent.getStringExtra("thumbnail")
        val title = intent.getStringExtra("title")
        val author = intent.getStringExtra("author")
        val publisher = intent.getStringExtra("publisher")
        val description = intent.getStringExtra("description")

        // === Common Interface ===
        // 뒤로 가기
        binding.btnBack.setOnClickListener{ finish() }

        // === Context Menu ===
        // 책 정보 보기
        binding.btnInfo.setOnClickListener{ openBookInfoTab() }
        // 독서 활동 보기
        binding.btnMgr.setOnClickListener{ openBookMgrTab() }
        binding.btnMgr.visibility = if(action == "Register"){ View.GONE } else { View.VISIBLE }
        openBookInfoTab()

        // === Header Navigation ====
        if(action.equals("Register")){
            openBookRegisterMenu()
        } else{
            openBookManagerMenu()
        }

        // === 책 정보 표시 ===
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

        if(action.equals("Register")){
            // 등록하기
            binding.btnRegister.setOnClickListener{
                val registerSql = """
                INSERT OR IGNORE INTO Book(isbn, thumbnail, title, author, publisher, description) 
                VALUES (?, ?, ?, ?, ?, ?);
                """.trimIndent()
                val uniqueCheckSql = "SELECT isbn FROM Book WHERE isbn = ?"

                val registerParams = arrayOf(
                    isbn,
                    thumbnail,
                    title,
                    author,
                    publisher,
                    description
                )
                val uniqueCheckParams = arrayOf(isbn.toString())

                try{
                    val cursor = database.rawQuery(uniqueCheckSql, uniqueCheckParams)
                    if(cursor.moveToNext()) {
                        Toast.makeText(this, "이미 등록된 책입니다.", Toast.LENGTH_SHORT).show()
                    } else{
                        database.execSQL(registerSql, registerParams)
                        Toast.makeText(this, "책이 등록되었습니다.", Toast.LENGTH_SHORT).show()
                    }
                }catch(error: SQLiteException){
                    Log.e("BookManager", error.message.toString())
                    Toast.makeText(this, "책을 등록할 수 없습니다.", Toast.LENGTH_SHORT).show()
                }
            }
        } else if(action.equals("Manager")){
            // 서재에서 제거하기
            binding.btnDelete.setOnClickListener{
                val deleteSql = "DELETE FROM Book WHERE isbn = ?"
                val deleteParams = arrayOf(isbn.toString())
                database.execSQL(deleteSql, deleteParams)
                finish()
            }
            // 메모 목록 화면으로 이동
            binding.btnMemo.setOnClickListener{
                val intent = Intent(this, MemoListActivity::class.java)
                intent.putExtra("isbn", isbn)
                startActivity(intent)
            }

            // 독서 기간 관리
            var reloadDays = { start_at: String, end_at: String ->
                val last_at = if(end_at == ""){
                    LocalDate.now()
                } else{
                    LocalDate.parse(end_at)
                }
                val days = Period.between(LocalDate.parse(start_at), last_at).days

                binding.txtDays.text = "${days}일"
                binding.txtStartAt.text = start_at
                binding.txtEndAt.text = end_at
            }
            val start_at = intent.getStringExtra("start_at") ?: ""
            val end_at = intent.getStringExtra("end_at") ?: ""
            reloadDays(start_at, end_at)

            // 독서 종료/재개
            binding.btnBreak.text = if(end_at.equals("")) { "독서 종료" } else{ "독서 재개" }
            binding.btnBreak.setOnClickListener{
                val updateParams = arrayOf(isbn.toString())
                if(binding.btnBreak.text == "독서 종료"){
                    binding.btnBreak.text = "독서 재개"
                    val updateSql = "UPDATE Book SET end_at = date('now','localtime') WHERE isbn = ?"
                    database.execSQL(updateSql, updateParams)
                    reloadDays(start_at, LocalDate.now().toString())
                } else{
                    binding.btnBreak.text = "독서 종료"
                    val updateSql = "UPDATE Book SET end_at = '' WHERE isbn = ?"
                    database.execSQL(updateSql, updateParams)
                    reloadDays(start_at, "")
                }
            }

        }
    }

    fun openBookInfoTab(){
        binding.groupInfo.visibility = View.VISIBLE
        binding.groupMgr.visibility = View.GONE
    }
    fun openBookMgrTab(){
        binding.groupInfo.visibility = View.GONE
        binding.groupMgr.visibility = View.VISIBLE
    }

    fun openBookRegisterMenu(){
        binding.menuRegister.visibility = View.VISIBLE
        binding.menuManager.visibility = View.GONE
    }
    fun openBookManagerMenu(){
        binding.menuRegister.visibility = View.GONE
        binding.menuManager.visibility = View.VISIBLE
    }
}
