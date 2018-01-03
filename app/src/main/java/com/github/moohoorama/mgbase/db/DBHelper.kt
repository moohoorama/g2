package com.github.moohoorama.mgbase.db

import android.annotation.SuppressLint
import android.content.Context
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper
import android.util.Log

/**
 * Created by Yanoo on 2017. 12. 29
 */
class DBHelper(context: Context, name:String, factory:SQLiteDatabase.CursorFactory?, version:Int):SQLiteOpenHelper(context,name,factory,version){

    override fun onCreate(db: SQLiteDatabase?) {
        db?.execSQL("CREATE TABLE TETRIS_SCORE (id INTEGER PRIMARY KEY AUTOINCREMENT, name TEXT, score INTEGER, create_at DATE);")
    }

    override fun onUpgrade(db: SQLiteDatabase?, ov: Int, nv: Int) {
        if (ov == 1 && nv ==2) {
            db?.execSQL("CREATE INDEX TETRIS_SCORE_SCORE_IDX as TETRIS_SCORE(score)")
        }
    }

    fun insert(name:String, score:Int) {
        try {
            val db = writableDatabase
            db.execSQL("INSERT INTO TETRIS_SCORE values(null, '$name','$score',datetime('now','localtime'))")
            db.close()
        }catch (e:Exception) {
            e.printStackTrace()
            Log.i("db","$e")
        }
    }

//    data class Score

    @SuppressLint("Recycle")
    fun select() : String {
        val db= readableDatabase
        var result=""
        try {
            val cursor = db.rawQuery("SELECT * FROM TETRIS_SCORE ORDER BY score desc limit 5", null)
            while (cursor.moveToNext()) {
                result += "${cursor.getInt(0)} ${cursor.getString(1)} ${cursor.getInt(2)}  ${cursor.getString(3)}\n"
            }
        }catch (e:Exception) {
            e.printStackTrace()
            Log.i("db","$e")
        }
        Log.i("db result","$result")

        return result
    }
}
