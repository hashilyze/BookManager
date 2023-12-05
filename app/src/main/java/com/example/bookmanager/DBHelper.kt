package com.example.bookmanager

import android.content.Context
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper
import android.util.Log

class DBHelper(context: Context?) : SQLiteOpenHelper(context, "BookManager.db", null, 1) {
    override fun onCreate(db: SQLiteDatabase) {
        val bookSql : String ="""
            CREATE TABLE IF NOT EXISTS Book(
                isbn INTEGER PRIMARY KEY,
                thumbnail TEXT,
                title TEXT NOT NULL,
                author TEXT NOT NULL default '',
                publisher TEXT NOT NULL default '',
                description TEXT NOT NULL default '',
                start_at TEXT NOT NULL default(date('now','localtime')),
                end_at TEXT NOT NULL default ""
            );
        """.trimIndent()
        val memoSql : String ="""
            CREATE TABLE IF NOT EXISTS Memo(
                id INTEGER PRIMARY KEY AUTOINCREMENT,
                isbn INTEGER,
                title TEXT NOT NULL default '',
                contents TEXT NOT NULL default '',
                updated_at TEXT NOT NULL default(datetime('now','localtime')),
                FOREIGN KEY(isbn) REFERENCES Book(isbn) ON DELETE CASCADE ON UPDATE CASCADE
            );
        """.trimIndent()
        db.execSQL(bookSql)
        db.execSQL(memoSql)
        Log.d("BookManager", "Created Database")
    }

    override fun onUpgrade(db: SQLiteDatabase, oldVersion: Int, newVersion: Int) {
        val bookSql = "DROP TABLE IF EXISTS Book"
        val memoSql = "DROP TABLE IF EXISTS Memo"

        db.execSQL(bookSql)
        db.execSQL(memoSql)
        onCreate(db)
        Log.d("BookManager", "Updated Database")
    }

    override fun onOpen(db: SQLiteDatabase?) {
        super.onOpen(db)
        db?.execSQL("PRAGMA foreign_keys=ON")
    }

}