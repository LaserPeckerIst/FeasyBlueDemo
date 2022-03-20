package com.feasycom.feasyblue.dialog

import android.app.Dialog
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.view.*
import androidx.fragment.app.DialogFragment
import com.feasycom.feasyblue.R
import kotlinx.android.synthetic.main.dfu_name_fragment.*


class DfuNameDialogFragment: DialogFragment() {

    var onClickComplete: ((dialog:Dialog,name: String)-> Unit)? = null

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        /**
         * remove the system dialog title and border background
         */
        dialog?.let {
            it.window?.setBackgroundDrawable(ColorDrawable(0))
            it.requestWindowFeature(Window.FEATURE_NO_TITLE)
        }
        return inflater.inflate(R.layout.dfu_name_fragment, container, false)
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        dialog?.let {d ->
            d.setCancelable(false)
            d.setCanceledOnTouchOutside(false)
            ok.setOnClickListener {
                onClickComplete?.invoke(d, dfu_name.text.toString())
            }
            close.setOnClickListener {
                d.dismiss()
            }
        }


    }

    override fun onResume() {
        val params: ViewGroup.LayoutParams = dialog?.window!!.attributes
        /**
         * remove the system dialog title and border background
         */
        params.width = WindowManager.LayoutParams.MATCH_PARENT
        params.height = WindowManager.LayoutParams.WRAP_CONTENT
        dialog!!.window!!.attributes = params as WindowManager.LayoutParams
        super.onResume()

    }
}