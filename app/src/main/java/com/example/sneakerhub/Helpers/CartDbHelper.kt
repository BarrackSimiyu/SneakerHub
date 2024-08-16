package com.example.sneakerhub.helpers

import android.annotation.SuppressLint
import android.content.ContentValues
import android.content.Context
import android.database.Cursor
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper
import android.util.Log

class CartDbHelper(context: Context) : SQLiteOpenHelper(context, DATABASE_NAME, null, DATABASE_VERSION) {

    companion object {
        private const val DATABASE_NAME = "cart1.db"
        private const val DATABASE_VERSION = 1 // Increment this if schema changes
        private const val TABLE_NAME = "cart"
        const val COLUMN_SHOE_ID = "shoe_id"
        const val COLUMN_NAME = "name"
        const val COLUMN_PRICE = "price"
        const val COLUMN_CATEGORY_ID = "category_id"
        const val COLUMN_DESCRIPTION = "description"
        const val COLUMN_QUANTITY = "quantity"
        const val COLUMN_IMAGE_URL = "image_url"
    }

    override fun onCreate(db: SQLiteDatabase) {
        val createTableQuery = """
            CREATE TABLE $TABLE_NAME (
                $COLUMN_SHOE_ID TEXT PRIMARY KEY,
                $COLUMN_NAME TEXT,
                $COLUMN_PRICE REAL,
                $COLUMN_CATEGORY_ID TEXT,
                $COLUMN_DESCRIPTION TEXT,
                $COLUMN_QUANTITY INTEGER,
                $COLUMN_IMAGE_URL TEXT
            )
        """.trimIndent()
        db.execSQL(createTableQuery)
    }

    override fun onUpgrade(db: SQLiteDatabase, oldVersion: Int, newVersion: Int) {
        if (oldVersion < newVersion) {
            db.execSQL("DROP TABLE IF EXISTS $TABLE_NAME")
            onCreate(db)
        }
    }

    fun getAllItems(): Cursor {
        val db = this.readableDatabase
        return db.query(TABLE_NAME, null, null, null, null, null, null)
    }

    fun addItem(
        shoeId: String,
        shoeName: String,
        shoePrice: Double,
        categoryId: String,
        description: String,
        quantity: Int,
        photoUrl: String
    ): Boolean {
        val db = this.writableDatabase
        val values = ContentValues().apply {
            put(COLUMN_SHOE_ID, shoeId)
            put(COLUMN_NAME, shoeName)
            put(COLUMN_PRICE, shoePrice)
            put(COLUMN_CATEGORY_ID, categoryId)
            put(COLUMN_DESCRIPTION, description)
            put(COLUMN_QUANTITY, quantity)
            put(COLUMN_IMAGE_URL, photoUrl)
        }

        val cursor = db.query(
            TABLE_NAME,
            arrayOf(COLUMN_SHOE_ID),
            "$COLUMN_SHOE_ID = ?",
            arrayOf(shoeId),
            null, null, null
        )

        return if (cursor.moveToFirst()) {
            cursor.close()
            val result = db.update(TABLE_NAME, values, "$COLUMN_SHOE_ID = ?", arrayOf(shoeId))
            result > 0
        } else {
            cursor.close()
            db.insert(TABLE_NAME, null, values) != -1L
        }
    }

    fun deleteItem(shoeId: String): Boolean {
        val db = this.writableDatabase
        val result = db.delete(TABLE_NAME, "$COLUMN_SHOE_ID = ?", arrayOf(shoeId))
        return result > 0
    }

    fun clearCart(): Boolean {
        val db = this.writableDatabase
        val result = db.delete(TABLE_NAME, null, null)
        return result > 0
    }

    @SuppressLint("Range")
    fun totalCost(): Double {
        val db = this.readableDatabase
        val query = "SELECT SUM(${COLUMN_PRICE} * ${COLUMN_QUANTITY}) AS total FROM $TABLE_NAME"
        Log.d("CartDbHelper", "Executing totalCost query: $query")
        val cursor = db.rawQuery(query, null)
        var total = 0.0
        cursor.use {
            if (it.moveToFirst()) {
                total = it.getDouble(it.getColumnIndex("total"))
            }
            Log.d("CartDbHelper", "Total cost query result: $total")
        }
        return total
    }

    @SuppressLint("Range")
    fun getNumItems(): Int {
        val db = this.readableDatabase
        val query = "SELECT SUM(CAST(${COLUMN_QUANTITY} AS INTEGER)) AS total_items FROM $TABLE_NAME"
        Log.d("CartDbHelper", "Executing getNumItems query: $query")
        val cursor = db.rawQuery(query, null)
        val itemCount = cursor.use {
            if (it.moveToFirst()) {
                it.getInt(it.getColumnIndex("total_items"))
            } else {
                0
            }
        }
        return itemCount
    }

    fun clearCartById(shoeId: String): Boolean {
        val db = this.writableDatabase
        val result = db.delete(TABLE_NAME, "$COLUMN_SHOE_ID = ?", arrayOf(shoeId))
        return result > 0
    }
}
