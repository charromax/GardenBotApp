/*
 * Copyright (c) 2021. Created by charr0max  -> manuelrg88@gmail.com
 */

package com.example.gardenbotapp.ui.home.sections.chart

import androidx.fragment.app.Fragment
import androidx.viewpager2.adapter.FragmentStateAdapter

class ChartsAdapter(val list: List<BaseChartFragment>, fragment: ChartFragment) :
    FragmentStateAdapter(fragment) {
    override fun getItemCount() = list.size

    override fun createFragment(position: Int): Fragment {
        return list[position]
    }

}