package com.example.bookmanager

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.AdapterView
import android.widget.Toast
import androidx.appcompat.widget.SearchView
import com.android.volley.Request
import com.android.volley.toolbox.JsonObjectRequest
import com.android.volley.toolbox.Volley
import com.example.bookmanager.databinding.ActivitySearchBinding
import org.json.JSONObject

class SearchActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        var binding = ActivitySearchBinding.inflate(getLayoutInflater())
        setContentView(binding.root)

        // 뒤로 가기 버튼
        binding.btnHome.setOnClickListener{
            Log.d("test", "뒤로 가기")
            finish()
        }
        // 검색창
        binding.inputSearch.setOnQueryTextListener(object : SearchView.OnQueryTextListener{
            override fun onQueryTextSubmit(query: String?): Boolean {
                val url = "https://openapi.naver.com/v1/search/book_adv.json?display=100&start=1&d_titl=${query}"
                val jsonRequest = object: JsonObjectRequest(
                    Request.Method.GET,
                    url,
                    null,
                    { response ->
                        val bookList = arrayListOf<Book>()
                        val items = response.getJSONArray("items")
                        for(i in 0 until items.length()){
                            val item = items[i] as JSONObject

                            bookList.add(Book(
                                item.getLong("isbn"),
                                item.getString("image"),
                                item.getString("title"),
                                item.getString("author"),
                                item.getString("publisher"),
                                item.getString("description")
                            ))
                        }

                        binding.booklist.adapter = BookInfoAdapter(this@SearchActivity, bookList)
                        binding.booklist.onItemClickListener = AdapterView.OnItemClickListener {
                                parent, view, position, id ->
                            val selectedBook = parent.getItemAtPosition(position) as Book
                            var intent = Intent(this@SearchActivity, BookInfoActivity::class.java)
                            intent.putExtra("isbn", selectedBook.isbn)
                            intent.putExtra("thumbnail", selectedBook.thumbnail)
                            intent.putExtra("title", selectedBook.title)
                            intent.putExtra("author", selectedBook.author)
                            intent.putExtra("publisher", selectedBook.publisher)
                            intent.putExtra("description", selectedBook.description)
                            startActivity(intent)
                        }
                    },
                    { error -> Log.d("test", "error... $error") }
                ){
                    override fun getHeaders(): MutableMap<String, String> {
                        val headers = HashMap<String, String>()
                        headers["X-Naver-Client-Id"] = "Sy7wWmpc1VDCpaeaxkDd"
                        headers["X-Naver-Client-Secret"] = "tj4rG7jxJl"
                        return headers
                    }
                }
                val queue = Volley.newRequestQueue(this@SearchActivity)
                queue.add(jsonRequest)
                return true
            }

            override fun onQueryTextChange(newText: String?): Boolean {
                return true
            }
        })
    }
}