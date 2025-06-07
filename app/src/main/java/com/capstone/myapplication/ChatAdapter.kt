package com.capstone.myapplication

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView

class ChatAdapter(private val chatMessages: MutableList<ChatMessage>) :
    RecyclerView.Adapter<ChatAdapter.MessageViewHolder>() {

    private val VIEW_TYPE_USER = 1
    private val VIEW_TYPE_BOT = 2

    override fun getItemViewType(position: Int): Int {
        return if (chatMessages[position].isUser) VIEW_TYPE_USER else VIEW_TYPE_BOT
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MessageViewHolder {
        val layout = if (viewType == VIEW_TYPE_USER) R.layout.item_chat_user else R.layout.item_chat_bot
        val view = LayoutInflater.from(parent.context).inflate(layout, parent, false)
        return MessageViewHolder(view)
    }

    override fun onBindViewHolder(holder: MessageViewHolder, position: Int) {
        val message = chatMessages[position]
        holder.bind(message)
    }

    override fun getItemCount(): Int = chatMessages.size

    fun addMessage(message: ChatMessage) {
        chatMessages.add(message)
        notifyItemInserted(chatMessages.size - 1)
    }

    class MessageViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val messageText: TextView = itemView.findViewById(R.id.text_view_message)

        fun bind(chatMessage: ChatMessage) {
            messageText.text = chatMessage.message
        }
    }

    fun updateMessages(newMessages: List<ChatMessage>) {
        val oldSize = chatMessages.size
        chatMessages.clear()
        notifyItemRangeRemoved(0, oldSize) // 기존 아이템이 모두 지워졌다고 알림

        chatMessages.addAll(newMessages)
        notifyItemRangeInserted(0, newMessages.size) // 새로운 아이템이 모두 추가되었다고 알림
    }
}