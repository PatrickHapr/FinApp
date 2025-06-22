package com.example.fintech.data.dao

import android.content.ContentValues
import android.content.Context
import android.database.Cursor
import com.example.fintech.data.db.DBHelper
import com.example.fintech.data.model.Transaction

class TransactionDAO(context: Context) {
    private val dbHelper = DBHelper(context)

    fun insert(transaction: Transaction): Long {
        val db = dbHelper.writableDatabase
        val values = ContentValues().apply {
            put(DBHelper.COLUMN_TYPE, transaction.type)
            put(DBHelper.COLUMN_DESCRIPTION, transaction.description)
            put(DBHelper.COLUMN_VALUE, transaction.value)
        }
        val result = db.insert(DBHelper.TABLE_TRANSACTIONS, null, values)
        db.close()
        return result
    }

    fun getAllTransactions(): List<Transaction> {
        val transactions = mutableListOf<Transaction>()
        val db = dbHelper.readableDatabase
        val cursor: Cursor = db.query(
            DBHelper.TABLE_TRANSACTIONS,
            null, null, null, null, null,
            "${DBHelper.COLUMN_ID} DESC"
        )

        if (cursor.moveToFirst()) {
            do {
                val transaction = Transaction(
                    id = cursor.getInt(cursor.getColumnIndexOrThrow(DBHelper.COLUMN_ID)),
                    type = cursor.getString(cursor.getColumnIndexOrThrow(DBHelper.COLUMN_TYPE)),
                    description = cursor.getString(cursor.getColumnIndexOrThrow(DBHelper.COLUMN_DESCRIPTION)),
                    value = cursor.getDouble(cursor.getColumnIndexOrThrow(DBHelper.COLUMN_VALUE))
                )
                transactions.add(transaction)
            } while (cursor.moveToNext())
        }
        cursor.close()
        db.close()
        return transactions
    }

    fun getTransactionsByType(type: String): List<Transaction> {
        val transactions = mutableListOf<Transaction>()
        val db = dbHelper.readableDatabase
        val cursor: Cursor = db.query(
            DBHelper.TABLE_TRANSACTIONS,
            null,
            "${DBHelper.COLUMN_TYPE} = ?",
            arrayOf(type),
            null, null,
            "${DBHelper.COLUMN_ID} DESC"
        )

        if (cursor.moveToFirst()) {
            do {
                val transaction = Transaction(
                    id = cursor.getInt(cursor.getColumnIndexOrThrow(DBHelper.COLUMN_ID)),
                    type = cursor.getString(cursor.getColumnIndexOrThrow(DBHelper.COLUMN_TYPE)),
                    description = cursor.getString(cursor.getColumnIndexOrThrow(DBHelper.COLUMN_DESCRIPTION)),
                    value = cursor.getDouble(cursor.getColumnIndexOrThrow(DBHelper.COLUMN_VALUE))
                )
                transactions.add(transaction)
            } while (cursor.moveToNext())
        }
        cursor.close()
        db.close()
        return transactions
    }

    fun calculateBalance(): Double {
        val db = dbHelper.readableDatabase
        var balance = 0.0

        // Somar créditos
        val creditCursor = db.rawQuery(
            "SELECT SUM(${DBHelper.COLUMN_VALUE}) FROM ${DBHelper.TABLE_TRANSACTIONS} WHERE ${DBHelper.COLUMN_TYPE} = 'CREDITO'",
            null
        )
        if (creditCursor.moveToFirst()) {
            balance += creditCursor.getDouble(0)
        }
        creditCursor.close()

        // Subtrair débitos
        val debitCursor = db.rawQuery(
            "SELECT SUM(${DBHelper.COLUMN_VALUE}) FROM ${DBHelper.TABLE_TRANSACTIONS} WHERE ${DBHelper.COLUMN_TYPE} = 'DEBITO'",
            null
        )
        if (debitCursor.moveToFirst()) {
            balance -= debitCursor.getDouble(0)
        }
        debitCursor.close()
        db.close()

        return balance
    }
}