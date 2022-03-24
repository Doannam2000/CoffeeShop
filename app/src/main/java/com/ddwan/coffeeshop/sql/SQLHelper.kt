package com.ddwan.coffeeshop.sql

import android.annotation.SuppressLint
import android.content.ContentValues
import android.content.Context
import android.database.Cursor
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper
import android.util.Log
import com.ddwan.coffeeshop.model.*
import java.text.SimpleDateFormat
import java.util.*

class SQLHelper(context: Context) : SQLiteOpenHelper(context, DB_NAME, null, DB_VERSION) {

    companion object {
        @SuppressLint("ConstantLocale")
        val sdf = SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault())
        private const val DB_NAME = "DDWAN_COFFEE"
        private const val DB_VERSION = 1

        //Food Category
        private const val TB_CATEGORY = "tbl_category"
        private const val FOOD_CATEGORY_ID = "category_id"
        private const val FOOD_CATEGORY_NAME = "category_name"

        //Food
        private const val TB_FOOD = "tbl_food"
        private const val FOOD_ID = "food_id"
        private const val FOOD_NAME = "food_name"
        private const val PRICE = "price"

        //Account
        private const val TB_ACCOUNT = "tbl_account"
        private const val ID = "id"
        private const val EMAIL = "email"
        private const val PASSWORD = "password"
        private const val NAME = "name"
        private const val ADDRESS = "address"
        private const val PHONE = "phone"
        private const val ROLE = "role"

        //Bill
        private const val TB_BILL = "tbl_bill"
        private const val BILL_ID = "bill_id"
        private const val DATE_CHECK_IN = "date_check_in"
        private const val DATE_CHECK_OUT = "date_check_out"

        //TABLE
        private const val TB_TABLE = "tbl_table"
        private const val TABLE_ID = "table_id"
        private const val TABLE_NAME = "table_name"
        private const val STATUS = "status"

        // BillInfo
        private const val TB_BILL_INFO = "tbl_bill_info"
        private const val BILL_INFO_ID = "bill_info_id"
        private const val COUNT = "count"

    }

    override fun onCreate(db: SQLiteDatabase?) {

        //create category
        db?.execSQL("Create table $TB_CATEGORY($FOOD_CATEGORY_ID INTEGER PRIMARY KEY AUTOINCREMENT," +
                "$FOOD_CATEGORY_NAME TEXT)")

        //create food
        db?.execSQL("Create table $TB_FOOD($FOOD_ID INTEGER PRIMARY KEY AUTOINCREMENT," +
                "$FOOD_NAME TEXT,$FOOD_CATEGORY_ID TEXT,$PRICE TEXT)")

        //create account
        db?.execSQL("Create table $TB_ACCOUNT($ID INTEGER PRIMARY KEY AUTOINCREMENT," +
                "$EMAIL TEXT,$PASSWORD TEXT,$NAME TEXT,$ADDRESS TEXT,$PHONE TEXT,$ROLE ROLE)")

        //create table
        db?.execSQL("Create table $TB_TABLE($TABLE_ID INTEGER PRIMARY KEY AUTOINCREMENT," +
                "$TABLE_NAME TEXT,$STATUS TEXT)")

        //create Bill
        db?.execSQL("Create table $TB_BILL($BILL_ID INTEGER PRIMARY KEY AUTOINCREMENT," +
                "$DATE_CHECK_IN TEXT,$DATE_CHECK_OUT TEXT,$TABLE_ID TEXT,$STATUS TEXT)")

        //create BillInfo
        db?.execSQL("Create table $TB_BILL_INFO($BILL_INFO_ID INTEGER PRIMARY KEY AUTOINCREMENT," +
                "$BILL_ID TEXT,$FOOD_ID TEXT,$PRICE TEXT,$COUNT TEXT)")

    }

    override fun onUpgrade(db: SQLiteDatabase?, p1: Int, p2: Int) {
        db!!.execSQL("Drop table if exists $TB_CATEGORY")
        db.execSQL("Drop table if exists $TB_BILL")
        db.execSQL("Drop table if exists $TB_ACCOUNT")
        db.execSQL("Drop table if exists $TB_BILL_INFO")
        db.execSQL("Drop table if exists $TB_FOOD")
        db.execSQL("Drop table if exists $TB_TABLE")
        onCreate(db)
    }

    //insert Food
    fun insertFood(food: Food): Long {
        val db = this.writableDatabase
        val contentValues = ContentValues()
        contentValues.put(FOOD_NAME, food.foodName)
        contentValues.put(FOOD_CATEGORY_ID, food.category)
        contentValues.put(PRICE, food.price)
        val success = db.insert(TB_FOOD, null, contentValues)
        db.close()
        return success
    }

    //insert category
    fun insertCategory(category: Category): Long {
        val db = this.writableDatabase
        val contentValues = ContentValues()
        contentValues.put(FOOD_CATEGORY_NAME, category.name)
        val success = db.insert(TB_CATEGORY, null, contentValues)
        db.close()
        return success
    }

    //insert Table
    fun insertTable(table: Table): Long {
        val db = this.writableDatabase
        val contentValues = ContentValues()
        contentValues.put(TABLE_NAME, table.tableName)
        contentValues.put(STATUS, table.status)
        val success = db.insert(TB_TABLE, null, contentValues)
        db.close()
        return success
    }

    // insert Account
    fun insertAccount(account: Account): Long {
        val db = this.writableDatabase
        val contentValues = ContentValues()
        contentValues.put(EMAIL, account.email)
        contentValues.put(PASSWORD, account.password)
        contentValues.put(NAME, account.name)
        contentValues.put(ADDRESS, account.address)
        contentValues.put(PHONE, account.phone)
        contentValues.put(ROLE, account.role)
        val success = db.insert(TB_ACCOUNT, null, contentValues)
        db.close()
        return success
    }

    //insert bill
    fun insertBill(bill: Bill): Long {
        val db = this.writableDatabase
        val contentValues = ContentValues()
        contentValues.put(DATE_CHECK_IN, sdf.format(bill.dateCheckIn))
        contentValues.put(DATE_CHECK_OUT, sdf.format(bill.dateCheckOut))
        contentValues.put(TABLE_ID, bill.tableId)
        contentValues.put(STATUS, bill.status)
        val success = db.insert(TB_BILL, null, contentValues)
        db.close()
        return success
    }

    //insert billInfo
    fun insertBillInfo(billInfo: BillInfo): Long {
        val db = this.writableDatabase
        val contentValues = ContentValues()
        contentValues.put(BILL_ID, billInfo.billId)
        contentValues.put(FOOD_ID, billInfo.foodId)
        contentValues.put(PRICE, billInfo.price)
        contentValues.put(COUNT, billInfo.count)
        val success = db.insert(TB_BILL_INFO, null, contentValues)
        db.close()
        return success
    }

    fun deleteDB(id: String, table: String): Int {
        val db = this.writableDatabase
        var success = db.delete(table, "$ID = ?", arrayOf(id))
        db.close()
        return success
    }

    @SuppressLint("Range", "Recycle")
    fun getAllFromFood(): ArrayList<Food> {
        val list = ArrayList<Food>()
        val query = "SELECT * FROM $TB_FOOD"
        val db = this.readableDatabase
        val cursor: Cursor?
        try {
            cursor = db.rawQuery(query, null)
        } catch (e: Exception) {
            db.execSQL(query)
            return list
        }
        if (cursor.moveToFirst()) {
            do {
                val foodId = cursor.getString(cursor.getColumnIndex(FOOD_ID)).toInt()
                val foodName = cursor.getString(cursor.getColumnIndex(FOOD_NAME))
                val category = cursor.getString(cursor.getColumnIndex(FOOD_CATEGORY_ID)).toInt()
                val price = cursor.getString(cursor.getColumnIndex(PRICE)).toInt()
                val food =Food(foodId,foodName,category,price)
                list.add(food)
            } while (cursor.moveToNext())
        }
        return list
    }

}