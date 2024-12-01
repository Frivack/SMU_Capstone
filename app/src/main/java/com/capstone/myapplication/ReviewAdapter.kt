package com.capstone.myapplication

import android.database.DataSetObserver
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ListAdapter
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView

data class Review(
    val reviewId: Int,
    val title: String,
    val content: String,
    val date: String,
    val thumbsUp: Int,
    val comments: Int,
    val userId: Int
)

class ReviewAdapter(
    private val reviews: List<Review>,
    private val onReviewClick: (Review) -> Unit // 클릭 이벤트 처리 람다 함수
) : RecyclerView.Adapter<ReviewAdapter.ReviewViewHolder>() {

    inner class ReviewViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val title: TextView = itemView.findViewById(R.id.review_title)
        val content: TextView = itemView.findViewById(R.id.review_content)
        val date: TextView = itemView.findViewById(R.id.review_date)
        val thumbsUp: TextView = itemView.findViewById(R.id.review_thumbs_up_text)
        val comments: TextView = itemView.findViewById(R.id.review_chat_text)

        init {
            // 아이템 클릭 시 onReviewClick 람다 함수 호출
            itemView.setOnClickListener {
                onReviewClick(reviews[adapterPosition])
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
        holder.date.text = review.date
        holder.thumbsUp.text = review.thumbsUp.toString()
        holder.comments.text = review.comments.toString()
    }

    override fun getItemCount(): Int = reviews.size
}

