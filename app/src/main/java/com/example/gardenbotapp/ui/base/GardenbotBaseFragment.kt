/*
 * Copyright (c) 2021. Created by charr0max  -> manuelrg88@gmail.com
 */

package com.example.gardenbotapp.ui.base

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.viewbinding.ViewBinding
import com.example.gardenbotapp.ui.MainActivity

abstract class GardenbotBaseFragment<VBinding : ViewBinding, VM : ViewModel> : Fragment() {
    open var useSharedViewModel: Boolean = false
    protected lateinit var viewModel: VM
    protected abstract fun getViewModelClass(): Class<VM>

    protected lateinit var binding: VBinding
    protected abstract fun getViewBinding(): VBinding

    private fun init() {
        binding = getViewBinding()
        viewModel = if (useSharedViewModel) {
            ViewModelProvider(requireActivity()).get(
                getViewModelClass()
            )
        } else {
            ViewModelProvider(this).get(getViewModelClass())
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        init()
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setUpActionBar()
        setUpUI()
        observeLiveData()
        setClickListeners()
    }

    open fun setUpActionBar() {
        (activity as? MainActivity)?.supportActionBar?.setDisplayHomeAsUpEnabled(true)
        (activity as? MainActivity)?.supportActionBar?.setHomeButtonEnabled(true)
    }

    /**
     * override to setup fragment initial state
     */
    open fun setUpUI() {
        //unimplemented in base
    }

    /**
     * override to observe viewmodel livedata
     */
    open fun observeLiveData() {
        //unimplemented in base
    }

    /**
     * override to set UI click listeners
     */
    open fun setClickListeners() {
        //unimplemented in base
    }

}