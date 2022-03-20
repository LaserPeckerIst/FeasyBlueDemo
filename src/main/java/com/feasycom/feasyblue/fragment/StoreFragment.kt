package com.feasycom.feasyblue.fragment

import android.view.View
import com.feasycom.feasyblue.R
import kotlinx.android.synthetic.main.fragment_store.*

class StoreFragment: BaseFragment(), View.OnClickListener{

    override fun getLayoutId() = R.layout.fragment_store

    override fun initView() {
        toolbarTitle.text = getString(R.string.store)

        myWebView.onUrlChangeListener = {
            toolbarBack?.visibility = if (it){
                View.GONE
            }else {
                View.VISIBLE
            }
        }

        toolbarBack.setOnClickListener(this)
        toolbarRefresh.setOnClickListener(this)
    }

    override fun onClick(v: View?) {
        when(v?.id){
            R.id.toolbarBack -> {
                myWebView.goBack()
            }
            R.id.toolbarRefresh -> {
                myWebView.reload()
            }
        }
    }
}