package com.example.bmicalc

import android.content.Context
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper
import android.provider.BaseColumns

object FeedReaderContract {
    // Table contents are grouped together in an anonymous object.
    object FeedEntry : BaseColumns {
        const val MEASUREMENTS_TABLE_NAME = "measurements"
        const val DATE = "date"
        const val MASS = "mass"
        const val HEIGHT = "height"
        const val SYSTEM = "system"
        const val VALUE = "value"
        const val NAME = "name"
    }
}

class DataStorage {
    val SQL_CREATE_ENTRIES =
            "CREATE TABLE ${FeedReaderContract.FeedEntry.MEASUREMENTS_TABLE_NAME} (" +
                    "${BaseColumns._ID} INTEGER PRIMARY KEY," +
                    "${FeedReaderContract.FeedEntry.DATE} TEXT," +
                    "${FeedReaderContract.FeedEntry.MASS} DOUBLE," +
                    "${FeedReaderContract.FeedEntry.HEIGHT} INTEGER," +
                    "${FeedReaderContract.FeedEntry.SYSTEM} INTEGER," +
                    "${FeedReaderContract.FeedEntry.VALUE} DOUBLE," +
                    "${FeedReaderContract.FeedEntry.NAME} TEXT )"

    val SQL_DELETE_ENTRIES = "DROP TABLE IF EXISTS ${FeedReaderContract.FeedEntry.MEASUREMENTS_TABLE_NAME}"
}

class FeedReaderDbHelper(context: Context) : SQLiteOpenHelper(context, DATABASE_NAME, null, DATABASE_VERSION) {
    val ds = DataStorage()
    override fun onCreate(db: SQLiteDatabase) {
        db.execSQL(ds.SQL_CREATE_ENTRIES)
    }
    override fun onUpgrade(db: SQLiteDatabase, oldVersion: Int, newVersion: Int) {
        db.execSQL(ds.SQL_DELETE_ENTRIES)
        onCreate(db)
    }
    override fun onDowngrade(db: SQLiteDatabase, oldVersion: Int, newVersion: Int) {
        onUpgrade(db, oldVersion, newVersion)
    }
    companion object {
        // If you change the database schema, you must increment the database version.
        const val DATABASE_VERSION = 1
        const val DATABASE_NAME = FeedReaderContract.FeedEntry.MEASUREMENTS_TABLE_NAME
    }
}

