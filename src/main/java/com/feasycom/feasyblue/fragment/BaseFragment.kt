package com.feasycom.feasyblue.fragment

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment

abstract class BaseFragment: Fragment() {

    private var rooView: View? = null

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        rooView?.let {
            return it
        }?: let {
            rooView = inflater.inflate(getLayoutId(), container, false)
            return rooView
        }
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        initView()
    }



    abstract fun getLayoutId(): Int

    abstract fun initView()
}