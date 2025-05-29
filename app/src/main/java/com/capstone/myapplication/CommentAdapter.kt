package com.capstone.myapplication;

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageButton
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView

data class Comment(
    val commentId: Int,
    val userId: Int,
    val content: String,
    val thumbsUp: Int,
    val createdAt: String
)


class CommentAdapter(
    private val context: Context,
    private val commentList: List<Comment>,
    private val onThumbsUpClick: (Comment) -> Unit // 추천 클릭 이벤트 핸들러
) : RecyclerView.Adapter<CommentAdapter.CommentViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CommentViewHolder {
        val view = LayoutInflater.from(context).inflate(R.layout.item_comment, parent, false)
        return CommentViewHolder(view)
    }

    override fun onBindViewHolder(holder: CommentViewHolder, position: Int) {
        val comment = commentList[position]
        holder.bind(comment)
    }

    override fun getItemCount(): Int = commentList.size

    inner class CommentViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val contentTextView: TextView = itemView.findViewById(R.id.comment_content)
        private val thumbsUpTextView: TextView = itemView.findViewById(R.id.comment_thumbs_up_text)
        private val thumbsUpButton: ImageButton = itemView.findViewById(R.id.comment_thumbs_up_button)

        fun bind(comment: Comment) {
            contentTextView.text = comment.content
            thumbsUpTextView.text = "추천: ${comment.thumbsUp}"

            // 추천 버튼 클릭 이벤트
            thumbsUpButton.setOnClickListener {
                onThumbsUpClick(comment)
            }
        }
    }
}
