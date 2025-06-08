package com.capstone.myapplication

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.capstone.Review
import java.text.SimpleDateFormat
import java.util.Locale
import java.util.TimeZone

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

        // 날짜 포맷 변경
        val inputFormat = SimpleDateFormat("EEE, dd MMM yyyy HH:mm:ss z", Locale.ENGLISH)
        val outputFormat = SimpleDateFormat("yyyy/MM/dd", Locale.getDefault())

        val formattedDate = try {
            val date = inputFormat.parse(review.created_at)
            outputFormat.format(date)
        } catch (e: Exception) {
            review.created_at // 포맷 실패 시 원래 값 표시
        }
        holder.date.text = formattedDate

        holder.thumbsUp.text = review.thumbs_up?.toString() ?: "0"
        holder.comments.text = review.comments_count?.toString() ?: "0"

        if (review.review_id == 2) {
            holder.reviewImage.setImageResource(R.drawable.img_i9)
        } else {
            holder.reviewImage.setImageResource(R.drawable.ic_launcher_foreground)
        }
    }
    override fun getItemCount(): Int = reviews.size
}