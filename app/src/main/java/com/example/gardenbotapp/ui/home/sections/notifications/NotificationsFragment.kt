/*
 * Copyright (c) 2021. Created by charr0max  -> manuelrg88@gmail.com
 */

package com.example.gardenbotapp.ui.home.sections.notifications

import androidx.recyclerview.widget.LinearLayoutManager
import com.example.gardenbotapp.data.remote.model.Notification
import com.example.gardenbotapp.databinding.FragmentNotificationsBinding
import com.example.gardenbotapp.ui.MainActivity
import com.example.gardenbotapp.ui.base.GardenbotBaseFragment
import com.example.gardenbotapp.util.VerticalSpaceItemDecoration
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class NotificationsFragment :
    GardenbotBaseFragment<FragmentNotificationsBinding, NotificationsViewModel>() {
    override fun getViewModelClass() = NotificationsViewModel::class.java
    override fun getViewBinding() = FragmentNotificationsBinding.inflate(layoutInflater)
    private lateinit var adapter: NotificationsAdapter
    private val list = arrayListOf<Notification>()


    override fun observeLiveData() {
        viewModel.notificationSub.observe(viewLifecycleOwner, {
            list.add(it)
            adapter.submitList(list)
            adapter.notifyItemInserted(list.size - 1)
        })
    }

    override fun setUpUI() {
        list.addAll((activity as MainActivity).notificationsList)
        adapter = NotificationsAdapter()
        binding.notificationList.layoutManager =
            LinearLayoutManager(context, LinearLayoutManager.VERTICAL, false)
        binding.notificationList.addItemDecoration(VerticalSpaceItemDecoration(8))
        binding.notificationList.adapter = adapter
        adapter.submitList(list)
        binding.notificationList.scheduleLayoutAnimation()
    }

}