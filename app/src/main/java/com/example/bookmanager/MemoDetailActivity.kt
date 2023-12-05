package com.example.bookmanager

import android.database.sqlite.SQLiteDatabase
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.text.Editable
import android.util.Log
import android.view.View
import android.widget.Toast
import com.example.bookmanager.databinding.ActivityMemoDetailBinding

class MemoDetailActivity : AppCompatActivity() {
    private lateinit var binding: ActivityMemoDetailBinding
    private lateinit var database: SQLiteDatabase

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMemoDetailBinding.inflate(layoutInflater)
        setContentView(binding.root)

        database = DBHelper(this).writableDatabase // DB

        val action = intent.getStringExtra("action") ?: ""
        if(!"(read|write|edit)".toRegex().matches(action)){
            Log.e("BookManager", "Not Implemented MemoDetail's action")
            Toast.makeText(this, "잘못된 접근입니다.", Toast.LENGTH_SHORT)
            finish()
            return
        }

        val isbn = intent.getLongExtra("isbn", 0L)
        if(!"^[0-9]{13}$".toRegex().matches(isbn.toString())){
            Log.e("BookManager", "Invalid form of ISBN")
            Toast.makeText(this, "유효하지 않은 도서에 대한 접근입니다.", Toast.LENGTH_SHORT)
            finish()
            return
        }

        // === Common Interface ===
        binding.btnBack.setOnClickListener{ finish() }

        // === Display ===
        // 탭 선택
        if(action == "read"){
            openViewTab()
        }else if(action == "write" || action == "edit") {
            openEditTab()
        }
        // 내용 출력
        if(action == "read" || action == "edit") {
            val id = intent.getLongExtra("id", -1L)
            if(id == -1L){
                Log.e("BookManager", "Invalid memo")
                Toast.makeText(this, "존재하지 않는 메모입니다.", Toast.LENGTH_SHORT)
                finish()
                return
            }

            val fetchSql = """
                SELECT title, contents 
                FROM Memo
                WHERE id = ? 
            """.trimIndent()
            val fetchParams = arrayOf(id.toString())

            val cursor = database.rawQuery(fetchSql, fetchParams)
            if(!cursor.moveToNext()){
                Log.e("BookManager", "There is not book")
                Toast.makeText(this, "내 서재에 등록되지 않은 도서입니다.", Toast.LENGTH_SHORT)
                finish()
                return
            }

            binding.txtTitle.text = cursor.getString(0)
            binding.txtContents.text = cursor.getString(1)
        } else{
            binding.txtTitle.text = ""
            binding.txtContents.text = ""
        }
        binding.inputTitle.setText(binding.txtTitle.text)
        binding.inputContents.setText(binding.txtContents.text)

        // === Event Handling ===
        // 버튼 설정
        if(action == "read" || action == "edit"){
            val id = intent.getLongExtra("id", -1L)
            
            // 편집 모드 종료
            binding.btnCancel.setOnClickListener{ openViewTab() }
            // 편집 내용 저장
            binding.btnCommit.setOnClickListener{
                val updateSql = """
                    UPDATE Memo SET
                        title = ?,
                        contents = ?
                    WHERE id = ?
                """.trimIndent()
                val updateParams = arrayOf(
                    binding.inputTitle.text,
                    binding.inputContents.text,
                    id.toString()
                )

                database.execSQL(updateSql, updateParams)
                openViewTab()
                binding.txtTitle.text = binding.inputTitle.text
                binding.txtContents.text = binding.inputContents.text
            }
            // 편집 모드로 전환
            binding.btnEdit.setOnClickListener{
                openEditTab()
                binding.inputTitle.setText(binding.txtTitle.text)
                binding.inputContents.setText(binding.txtContents.text)
            }
            // 메모 삭제
            binding.btnDelete.setOnClickListener{
                val deleteSql = "DELETE FROM Memo Where id = ?"
                val deleteParams = arrayOf(id.toString())
                database.execSQL(deleteSql, deleteParams)
                finish()
            }
        }else if(action == "write") {
            // 메모 목록으로 돌아가기
            binding.btnCancel.setOnClickListener{ finish() }
            // 메모 등록
            binding.btnCommit.setOnClickListener {
                val writeSql = """
                    INSERT INTO MEMO(isbn, title, contents)
                    VALUES (?, ?, ?)
                """.trimIndent()
                val writeParams = arrayOf(
                    isbn.toString(),
                    binding.inputTitle.text,
                    binding.inputContents.text
                )
                database.execSQL(writeSql, writeParams)
                finish()
            }
        }

    }

    private fun openViewTab(){
        binding.menuView.visibility = View.VISIBLE
        binding.menuWrite.visibility = View.GONE

        binding.tabView.visibility = View.VISIBLE
        binding.tabEdit.visibility = View.GONE
    }
    private fun openEditTab(){
        binding.menuView.visibility = View.GONE
        binding.menuWrite.visibility = View.VISIBLE

        binding.tabView.visibility = View.GONE
        binding.tabEdit.visibility = View.VISIBLE

    }
}