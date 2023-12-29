package com.dicoding.picodiploma.loginwithanimation.view.adapter

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.core.app.ActivityOptionsCompat
import androidx.core.util.Pair
import androidx.paging.PagingDataAdapter
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.dicoding.picodiploma.loginwithanimation.data.response.ListStoryItem
import com.dicoding.picodiploma.loginwithanimation.databinding.ItemStoriesBinding
import com.dicoding.picodiploma.loginwithanimation.view.detail.DetailStoryActivity
import com.dicoding.picodiploma.loginwithanimation.view.utils.Utils.convertDate

class StoryAdapter(private val context: Context) :
    PagingDataAdapter<ListStoryItem, StoryAdapter.MyViewHolder>(
        DIFF_CALLBACK
    ) {


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyViewHolder {
        val binding = ItemStoriesBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return MyViewHolder(binding)
    }

    override fun onBindViewHolder(holder: MyViewHolder, position: Int) {
        val story = getItem(position)
        if (story != null) {
            holder.bind(story)
            holder.itemView.setOnClickListener {
                val moveStoryDataIntent =
                    Intent(holder.itemView.context, DetailStoryActivity::class.java)
                moveStoryDataIntent.putExtra(DetailStoryActivity.ID, story.id)

                val optionsCompat: ActivityOptionsCompat =
                    ActivityOptionsCompat.makeSceneTransitionAnimation(
                        holder.itemView.context as Activity,
                        Pair(holder.binding.itemPhoto, "storyImage")
                    )
                context.startActivity(moveStoryDataIntent, optionsCompat.toBundle())
            }
        }
    }

    class MyViewHolder(val binding: ItemStoriesBinding) : RecyclerView.ViewHolder(binding.root) {
        fun bind(story: ListStoryItem) {
            binding.itemDescription.text = story.description
            binding.itemName.text = "Oleh " + story.name
            binding.itemDate.text = story.createdAt.convertDate()
            Glide.with(binding.root.context)
                .load(story.photoUrl)
                .into(binding.itemPhoto)
        }

    }

    companion object {
        val DIFF_CALLBACK = object : DiffUtil.ItemCallback<ListStoryItem>() {
            override fun areItemsTheSame(oldItem: ListStoryItem, newItem: ListStoryItem): Boolean {
                return oldItem.id == newItem.id
            }

            override fun areContentsTheSame(
                oldItem: ListStoryItem,
                newItem: ListStoryItem
            ): Boolean {
                return oldItem == newItem
            }
        }
    }
}