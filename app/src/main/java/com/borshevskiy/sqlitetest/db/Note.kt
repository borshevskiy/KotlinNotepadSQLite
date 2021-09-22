package com.borshevskiy.sqlitetest.db

@Entity
class Note(title:String,desc:String,uri:String,id:Int,time:String) {
    var _title = title
    var _desc = desc
    var _uri = uri
    var _id = id
    var _time = time
}