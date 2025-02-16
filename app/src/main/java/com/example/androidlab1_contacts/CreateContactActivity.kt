package com.example.androidlab1_contacts

import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.net.Uri
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.ImageButton
import androidx.activity.enableEdgeToEdge
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.example.androidlab1_contacts.models.Contact
import com.example.androidlab1_contacts.storage.ContactsDB
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking

class CreateContactActivity : AppCompatActivity() {

    val contact: Contact = Contact(-1,"","","",null)

    val getContent = registerForActivityResult(ActivityResultContracts.GetContent()) { uri ->
        val iconButton = findViewById<ImageButton>(R.id.icon_button)
        contact.icon = uriToBitmap(uri)
        iconButton.setImageBitmap(contact.icon)
    }

    fun uriToBitmap(uri: Uri?): Bitmap? {
        return try {
            val inputStream = contentResolver.openInputStream(uri as Uri)
            Bitmap.createScaledBitmap(BitmapFactory.decodeStream(inputStream), 120, 120, false)
        } catch (e: Exception) {
            e.printStackTrace()
            null
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_create_contact)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.card)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        updateContactView()

        val iconButton = findViewById<ImageButton>(R.id.icon_button)
        val nameArea = findViewById<EditText>(R.id.card_name_area)
        val phoneArea = findViewById<EditText>(R.id.card_phone_area)
        val emailArea = findViewById<EditText>(R.id.card_email_area)
        val saveButton = findViewById<Button>(R.id.save_button)

        if(contact.icon != null) {
            iconButton.setImageBitmap(contact.icon)
        }
        nameArea.setText(contact.name)
        phoneArea.setText(contact.phone)
        emailArea.setText(contact.email)

        iconButton.setOnClickListener{
            getContent.launch("image/*")
        }

        saveButton.setOnClickListener{
            contact.name = nameArea.text.toString()
            contact.phone = phoneArea.text.toString()
            contact.email = emailArea.text.toString()
            updateContacts(this)
            finish()
        }
    }

    fun updateContacts(context: Context) = runBlocking {
        val db = ContactsDB(context, null)
        if(contact.id >= 0) {
            launch{ db.updateContact(contact) }
        } else {
            launch { db.addContact(contact) }
        }
    }

    fun updateContactView() {
        val id = intent.getIntExtra("contact_id", -1)

        if(id >= 0) {
            val db = ContactsDB(this, null)
            val res = db.getContact(id)
            if(res != null) {
                contact.id = res.id
                contact.name = res.name
                contact.email = res.email
                contact.phone = res.phone
                contact.icon = res.icon
            }
        }
    }
}