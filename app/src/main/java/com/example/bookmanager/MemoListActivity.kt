package com.example.bookmanager

import android.content.Intent
import android.database.sqlite.SQLiteDatabase
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.AdapterView
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import com.example.bookmanager.databinding.ActivityMemoListBinding

class MemoListActivity : AppCompatActivity() {
    private lateinit var binding: ActivityMemoListBinding
    private lateinit var database: SQLiteDatabase
    private lateinit var reloadLauncher: ActivityResultLauncher<Intent>

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMemoListBinding.inflate(layoutInflater)
        setContentView(binding.root)

        database = DBHelper(this).readableDatabase // DB
        reloadLauncher = registerForActivityResult( // Intent Launcher
            ActivityResultContracts.StartActivityForResult()){
            loadLibrary()
        }

        // 뒤로가기
        binding.btnBack.setOnClickListener{ finish() }
        // 메모 편집 화면으로 이동
        binding.btnMemoAdd.setOnClickListener{}
        loadLibrary()
    }

    private fun loadLibrary(){
        val memoList = arrayListOf<Memo>().also{
            val sql = """
                SELECT id, isbn, title, contents, updated_at 
                FROM Memo
                WHERE isbn = ?
                """.trimIndent()
            val vals = arrayOf(intent.getLongExtra("isbn", 0L).toString())

            val cursor = database.rawQuery(sql, vals)
            while(cursor.moveToNext()){
                it.add(Memo(
                    cursor.getLong(0),      // id
                    cursor.getLong(1),      // isbn
                    cursor.getString(2),    // title
                    cursor.getString(3),    // contents
                    cursor.getString(4),    // updated_at
                ))
            }
        }

        binding.memolist.adapter = MemoAdapter(this, memoList)
        binding.memolist.onItemClickListener = AdapterView.OnItemClickListener{
                parent, view, position, id ->
            val selectedMemo = parent.getItemAtPosition(position) as Memo
            var intent = Intent(this@MemoListActivity, null).also{
                it.putExtra("id", selectedMemo.id)
                it.putExtra("isbn", selectedMemo.isbn)
                it.putExtra("title", selectedMemo.title)
                it.putExtra("contents", selectedMemo.contents)
                it.putExtra("updated_at", selectedMemo.updated_at)
            }
            reloadLauncher.launch(intent)
        }
    }
}