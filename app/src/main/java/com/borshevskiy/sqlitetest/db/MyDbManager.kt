package com.borshevskiy.sqlitetest.db

import android.content.ContentValues
import android.content.Context
import android.database.sqlite.SQLiteDatabase
import android.provider.BaseColumns._ID
import com.borshevskiy.sqlitetest.db.MyDbNameClass.COLUMN_NAME_CONTENT
import com.borshevskiy.sqlitetest.db.MyDbNameClass.COLUMN_NAME_IMAGE_URI
import com.borshevskiy.sqlitetest.db.MyDbNameClass.COLUMN_NAME_TIME
import com.borshevskiy.sqlitetest.db.MyDbNameClass.COLUMN_NAME_TITLE
import com.borshevskiy.sqlitetest.db.MyDbNameClass.TABLE_NAME
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

class MyDbManager(context: Context) {

    val myDbHelper = MyDbHelper(context)
    var db: SQLiteDatabase? = null

    fun openDb() {
        db = myDbHelper.writableDatabase
    }

    suspend fun insertToDb(title:String,content:String,uri:String,time:String) = withContext(Dispatchers.IO) {
        val values = ContentValues().apply {
            put(COLUMN_NAME_TITLE, title)
            put(COLUMN_NAME_CONTENT, content)
            put(COLUMN_NAME_IMAGE_URI, uri)
            put(COLUMN_NAME_TIME, time)
        }
        db?.insert(TABLE_NAME,null,values)
    }

    suspend fun updateDb(title:String,content:String,uri:String, id: Int, time: String) = withContext(Dispatchers.IO) {
        val values = ContentValues().apply {
            put(COLUMN_NAME_TITLE, title)
            put(COLUMN_NAME_CONTENT, content)
            put(COLUMN_NAME_IMAGE_URI, uri)
            put(COLUMN_NAME_TIME, time)
        }
        db?.update(TABLE_NAME,values, "$_ID=$id",null)
    }

    suspend fun readDbData(searchText:String): ArrayList<Note> = withContext(Dispatchers.IO){
        val dataList = ArrayList<Note>()
        val selection = "${COLUMN_NAME_TITLE} like ?"
        val cursor = db?.query(TABLE_NAME,null,selection, arrayOf("%$searchText%"),null,null,null)

            while (cursor?.moveToNext()!!) {
                val title = cursor.getString(cursor.getColumnIndex(COLUMN_NAME_TITLE))
                val content = cursor.getString(cursor.getColumnIndex(COLUMN_NAME_CONTENT))
                val image = cursor.getString(cursor.getColumnIndex(COLUMN_NAME_IMAGE_URI))
                val id = cursor.getInt(cursor.getColumnIndex(_ID))
                val time = cursor.getString(cursor.getColumnIndex(COLUMN_NAME_TIME))
                dataList.add(Note(title,content,image,id,time))
            }
        cursor.close()
        return@withContext dataList
    }

    fun closeDb(){
        myDbHelper.close()
    }

    fun removeFromDb(id:Int) {
        db?.delete(TABLE_NAME, "$_ID=$id",null)
    }
}