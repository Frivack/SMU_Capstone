package com.capstone.myapplication

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.capstone.Review // 여기만 남겨!

class ReviewAdapter(
    private val reviews: List<Review>,
    private val onItemClick: (Review) -> Unit
) : RecyclerView.Adapter<ReviewAdapter.ReviewViewHolder>() {
    inner class ReviewViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val title: TextView = itemView.findViewById(R.id.review_title)
        val content: TextView = itemView.findViewById(R.id.review_content)
        val date: TextView = itemView.findViewById(R.id.review_date)
        val thumbsUp: TextView = itemView.findViewById(R.id.review_thumbs_up_text)
        val comments: TextView = itemView.findViewById(R.id.review_chat_text)
        val reviewImage: ImageView = itemView.findViewById(R.id.review_img)

        init {
            itemView.setOnClickListener {
                onItemClick(reviews[adapterPosition])
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ReviewViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_review, parent, false)
        return ReviewViewHolder(view)
    }

    override fun onBindViewHolder(holder: ReviewViewHolder, position: Int) {
        val review = reviews[position]
        holder.title.text = review.title
        holder.content.text = review.content
        holder.date.text = review.created_at        // ← 여기!! created_at으로
        holder.thumbsUp.text = review.thumbs_up?.toString() ?: "0"
        holder.comments.text = review.comments_count?.toString() ?: "0"

        if (review.review_id == 2) {                // ← 여기!! review_id로
            holder.reviewImage.setImageResource(R.drawable.img_i9)
        } else {
            holder.reviewImage.setImageResource(R.drawable.ic_launcher_foreground)
        }
    }

    override fun getItemCount(): Int = reviews.size
}
