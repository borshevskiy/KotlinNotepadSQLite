package com.borshevskiy.sqlitetest.db

import android.content.Context
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper
import com.borshevskiy.sqlitetest.db.MyDbNameClass.CREATE_TABLE
import com.borshevskiy.sqlitetest.db.MyDbNameClass.DATABASE_NAME
import com.borshevskiy.sqlitetest.db.MyDbNameClass.DATABASE_VERSION
import com.borshevskiy.sqlitetest.db.MyDbNameClass.SQL_DELETE_TABLE

class MyDbHelper(context: Context):SQLiteOpenHelper(context, DATABASE_NAME, null, DATABASE_VERSION) {

    override fun onCreate(db: SQLiteDatabase?) {
        db?.execSQL(CREATE_TABLE)
    }

    override fun onUpgrade(db: SQLiteDatabase?, oldVersion: Int, newVersion: Int) {
        db?.execSQL(SQL_DELETE_TABLE)
        onCreate(db)
    }
}