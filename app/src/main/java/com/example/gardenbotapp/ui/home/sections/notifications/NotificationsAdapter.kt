/*
 * Copyright (c) 2021. Created by charr0max  -> manuelrg88@gmail.com
 */

package com.example.gardenbotapp.ui.home.sections.notifications

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.example.gardenbotapp.R
import com.example.gardenbotapp.data.remote.model.Notification
import com.example.gardenbotapp.databinding.ListItemNotificationBinding
import com.example.gardenbotapp.util.formatTo
import com.example.gardenbotapp.util.toDate

class NotificationsAdapter :
    ListAdapter<Notification, NotificationsAdapter.ItemViewholder>(DiffCallback()) {


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ItemViewholder {
        return ItemViewholder(
            LayoutInflater.from(parent.context)
                .inflate(R.layout.list_item_notification, parent, false)
        )
    }

    override fun onBindViewHolder(holder: ItemViewholder, position: Int) {
        holder.bind(getItem(position))
    }

    class ItemViewholder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val binding = ListItemNotificationBinding.bind(itemView)
        fun bind(item: Notification) = with(itemView) {
            binding.notificationImage.setImageResource(item.image)
            binding.notificationMessage.text = item.message ?: ""
            binding.notificationDate.text =
                item.createdAt?.toDate()?.formatTo("EEEE 'a las' HH:mm a") ?: ""
        }
    }
}

class DiffCallback : DiffUtil.ItemCallback<Notification>() {
    override fun areItemsTheSame(oldItem: Notification, newItem: Notification): Boolean {
        return oldItem.id == newItem.id
    }

    override fun areContentsTheSame(oldItem: Notification, newItem: Notification): Boolean {
        return oldItem == newItem
    }
}