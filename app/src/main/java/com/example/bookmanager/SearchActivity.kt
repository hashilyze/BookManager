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
    private lateinit var binding: ActivitySearchBinding
    private val urlFormat = "https://openapi.naver.com/v1/search/book_adv.json?display=100&start=1&d_titl=%s"
    private val clientId = "Sy7wWmpc1VDCpaeaxkDd"
    private val clientSecret = "tj4rG7jxJl"

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivitySearchBinding.inflate(getLayoutInflater())
        setContentView(binding.root)

        // 뒤로 가기 버튼
        binding.btnHome.setOnClickListener{ finish() }
        // 검색창
        binding.inputSearch.setOnQueryTextListener(object : SearchView.OnQueryTextListener{
            override fun onQueryTextSubmit(query: String?): Boolean {
                val url = urlFormat.format(query)
                val jsonRequest = object: JsonObjectRequest(
                    Method.GET,
                    url,
                    null,
                    { response ->
                        val bookList = arrayListOf<Book>().also{
                            val items = response.getJSONArray("items")
                            for(i in 0 until items.length()){
                                val item = items[i] as JSONObject

                                it.add(Book(
                                    item.getLong("isbn"),
                                    item.getString("image"),
                                    item.getString("title"),
                                    item.getString("author"),
                                    item.getString("publisher"),
                                    item.getString("description")
                                ))
                            }
                        }

                        binding.booklist.adapter = BookInfoAdapter(this@SearchActivity, bookList)
                        binding.booklist.onItemClickListener = AdapterView.OnItemClickListener {
                                parent, view, position, id ->
                            val selectedBook = parent.getItemAtPosition(position) as Book

                            var intent = Intent(this@SearchActivity, BookInfoActivity::class.java).also{
                                it.putExtra("mode", "Register")
                                it.putExtra("isbn", selectedBook.isbn)
                                it.putExtra("thumbnail", selectedBook.thumbnail)
                                it.putExtra("title", selectedBook.title)
                                it.putExtra("author", selectedBook.author)
                                it.putExtra("publisher", selectedBook.publisher)
                                it.putExtra("description", selectedBook.description)
                                it.putExtra("start_at", "")
                                it.putExtra("end_at", "")
                            }
                            startActivity(intent)
                        }
                    },
                    { error -> Log.d("test", "error... $error") }
                ){
                    override fun getHeaders(): MutableMap<String, String> {
                        val headers = HashMap<String, String>()
                        headers["X-Naver-Client-Id"] = clientId
                        headers["X-Naver-Client-Secret"] = clientSecret
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