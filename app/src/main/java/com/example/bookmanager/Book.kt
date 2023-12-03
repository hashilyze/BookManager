package com.example.bookmanager

class Book(val isbn: Long,
        val thumbnail: String,
        val title: String,
        val author: String = "",
        val publisher: String = "",
        val description: String = "",
)