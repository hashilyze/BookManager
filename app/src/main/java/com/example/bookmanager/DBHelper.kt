package com.example.bookmanager

import android.content.Context
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper

class DBHelper(context: Context?) : SQLiteOpenHelper(context, "BookManager.db", null, 1) {
    override fun onCreate(db: SQLiteDatabase) {
        var sql : String ="""
            CREATE TABLE IF NOT EXISTS Book(
                isbn INTEGER PRIMARY KEY,
                thumbnail TEXT,
                title TEXT NOT NULL,
                author TEXT NOT NULL default '',
                publisher TEXT NOT NULL default '',
                description TEXT NOT NULL default ''
            );
        """.trimIndent()

        db.execSQL(sql)
    }

    override fun onUpgrade(db: SQLiteDatabase, oldVersion: Int, newVersion: Int) {
//        val sql : String = "DROP TABLE if exists mytable"
//
//        db.execSQL(sql)
//        onCreate(db)
    }

}