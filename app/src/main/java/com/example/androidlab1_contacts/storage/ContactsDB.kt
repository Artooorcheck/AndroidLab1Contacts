package com.example.androidlab1_contacts.storage

import android.content.ContentValues
import android.content.Context
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import com.example.androidlab1_contacts.models.Contact
import java.io.ByteArrayOutputStream

class ContactsDB(val context: Context, val factory: SQLiteDatabase.CursorFactory?):
    SQLiteOpenHelper(context, "contacts.app", factory, 1)  {

    override fun onCreate(db: SQLiteDatabase?) {
        val  query = "create table contacts (id integer primary key autoincrement not null, name text, email text, phone_number text, icon blob)"
        db!!.execSQL(query)
    }

    override fun onUpgrade(db: SQLiteDatabase?, oldVersion: Int, newVersion: Int) {
        db!!.execSQL("drop table if exists users")
        onCreate(db)
    }

    suspend fun addContact(contact: Contact){
        val values = ContentValues()
        values.put("name", contact.name)
        values.put("email", contact.email)
        values.put("phone_number", contact.phone)

        if(contact.icon != null) {
            values.put("icon", bitmapToByteArray(contact.icon!!))
        }

        val db = this.writableDatabase
        db.insert("contacts", null, values)
    }

    fun bitmapToByteArray(bitmap: Bitmap): ByteArray{
        ByteArrayOutputStream().use { stream ->
            bitmap.compress(Bitmap.CompressFormat.PNG, 100, stream)
            return stream.toByteArray()
        }
    }

    suspend fun updateContact(contact: Contact){
        val values = ContentValues()
        values.put("name", contact.name)
        values.put("email", contact.email)
        values.put("phone_number", contact.phone)

        if(contact.icon != null) {
            values.put("icon", bitmapToByteArray(contact.icon!!))
        }

        val db = this.writableDatabase
        db.update("contacts", values,"id = ?", arrayOf(contact.id.toString()))
    }

    fun getContact(id: Int): Contact? {
        val db = this.readableDatabase

        val result = db.rawQuery("select * from contacts where id = '$id'", null).use {
            if(it.moveToFirst()) {

                val blob = it.getBlob(it.getColumnIndexOrThrow("icon"))
                var icon: Bitmap? = null

                if(blob != null) {
                    icon = BitmapFactory.decodeByteArray(blob, 0, blob.size)
                }
                val contact = Contact(
                    it.getInt(it.getColumnIndexOrThrow("id")),
                    it.getString(it.getColumnIndexOrThrow("name")),
                    it.getString(it.getColumnIndexOrThrow("phone_number")),
                    it.getString(it.getColumnIndexOrThrow("email")),
                    icon)

                return contact;
            }
        }

        return null;
    }

    fun deleteContact(id: Int) {
        val db = this.writableDatabase
        db.delete("contacts", "id = ?", arrayOf(id.toString()))
    }

    fun getContacts(): List<Contact> {
        val contacts = mutableListOf<Contact>()
        val db = readableDatabase
        val cursor = db.rawQuery("select * from contacts", null)

        if (cursor.count > 0 && cursor.moveToFirst()) {
            do {
                val blob = cursor.getBlob(cursor.getColumnIndexOrThrow("icon"))
                var icon: Bitmap? = null

                if(blob != null) {
                    icon = BitmapFactory.decodeByteArray(blob, 0, blob.size)
                }
                contacts.add(Contact(
                    cursor.getInt(cursor.getColumnIndexOrThrow("id")),
                    cursor.getString(cursor.getColumnIndexOrThrow("name")),
                    cursor.getString(cursor.getColumnIndexOrThrow("phone_number")),
                    cursor.getString(cursor.getColumnIndexOrThrow("email")),
                    icon))
            } while (cursor.moveToNext())
        }

        cursor.close()
        return contacts
    }
}