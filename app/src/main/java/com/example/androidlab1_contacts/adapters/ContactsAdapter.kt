package com.example.androidlab1_contacts.adapters

import android.content.Context
import android.content.Intent
import android.drm.DrmStore.Action
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import androidx.core.content.ContextCompat.startActivity
import androidx.recyclerview.widget.RecyclerView
import com.example.androidlab1_contacts.CreateContactActivity
import com.example.androidlab1_contacts.R
import com.example.androidlab1_contacts.models.Contact
import com.example.androidlab1_contacts.storage.ContactsDB

class ContactsAdapter(var items: List<Contact>, var context: Context): RecyclerView.Adapter<ContactsAdapter.ContactViewHolder>(){
    class ContactViewHolder(view: View): RecyclerView.ViewHolder(view) {
        val image = view.findViewById<ImageView>(R.id.icon_view)
        val nameArea = view.findViewById<TextView>(R.id.name_area)
        val deleteButton = view.findViewById<Button>(R.id.delete_button)
        val selectButton = view.findViewById<LinearLayout>(R.id.card_item)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ContactViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.contact_item, parent, false)
        return ContactViewHolder(view)
    }

    override fun getItemCount(): Int {
        return  items.count()
    }

    override fun onBindViewHolder(holder: ContactViewHolder, position: Int) {
        if(items[position].icon != null) {
            holder.image.setImageBitmap(items[position].icon)
        }
        holder.nameArea.text = items[position].name

        holder.deleteButton.setOnClickListener {
            val db = ContactsDB(context, null)
            db.deleteContact(items[position].id)
            items = db.getContacts()
            notifyDataSetChanged()
        }

        holder.selectButton.setOnClickListener {
            val intent = Intent(context, CreateContactActivity::class.java).apply {
                putExtra("contact_id", items[position].id) // Pass item ID
            }
            context.startActivity(intent)
        }
    }
}