package com.mqtt

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView

class MessageAdapter(var content:ArrayList<Message>):RecyclerView.Adapter<MessageAdapter.ViewHolder>()
{

    // Method to update the content and update the adapter
    fun updateContent(content:ArrayList<Message>) {
        this.content = content
        notifyItemInserted(0)
    }

    class ViewHolder(itemView: View):RecyclerView.ViewHolder(itemView)
    {
        var tvMsg: TextView = itemView.findViewById(R.id.item_msg)
        var tvTopic: TextView = itemView.findViewById(R.id.item_topic)
        var tvTime: TextView = itemView.findViewById(R.id.item_time)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_messages,parent,false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        var temp = content[position]
        holder.tvMsg.text = temp.msg
        holder.tvTopic.text = temp.topic
        holder.tvTime.text = temp.time
    }

    override fun getItemCount(): Int {
        return content.size

    }
}
