package com.example.contact

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageButton
import android.widget.ImageView
import android.widget.RelativeLayout
import android.widget.TextView
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView


public class ContactListRecyclerViewAdapter(item:List<ContactsModel>, ctx: Context):RecyclerView.Adapter<ContactListRecyclerViewAdapter.ViewHolder>() {

    private var list:List<ContactsModel> = item
    private var context: Context = ctx

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int):ViewHolder {
        return ViewHolder(LayoutInflater.from(context).inflate(R.layout.phonebook,parent,false))
    }

    override fun onBindViewHolder(holder: ContactListRecyclerViewAdapter.ViewHolder, position: Int) {
        holder.name.text = list[position].name
        holder.number.text=list[position].number

        holder.call.setOnClickListener{
            val i=Intent(Intent.ACTION_CALL)
            i.data= Uri.parse("tel:"+list[position].number)
            context.startActivity(i)
        }
        holder.sms.setOnClickListener {
            val j=Intent(Intent.ACTION_VIEW)
            j.data=Uri.fromParts("sms",list[position].number,null)
            context.startActivity(j)
        }
    }


    override fun getItemCount(): Int {
        return list.size
    }
    inner class  ViewHolder(v: View):RecyclerView.ViewHolder(v){
        val name: TextView = v.findViewById(R.id.phoneName)
        val number:TextView=v.findViewById(R.id.phonebooknumber)
        val call:ImageButton=v.findViewById(R.id.call)
        val sms:ImageButton=v.findViewById(R.id.sms)

    }


}