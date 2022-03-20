package com.feasycom.feasyblue.widget

import android.content.Context
import android.util.AttributeSet
import android.view.View
import android.widget.CheckBox
import android.widget.EditText
import android.widget.LinearLayout
import android.widget.TextView
import com.feasycom.feasyblue.R
import com.feasycom.feasyblue.presenters.CommandPresenters
import com.feasycom.feasyblue.utils.ToastUtil

class CustomizeAtCommandView(context: Context?, attrs: AttributeSet?) :
    LinearLayout(context, attrs) {
    var label: TextView
    var parameterEdit: EditText
    var commandEdit: EditText
    var checkFlag: CheckBox
    val commandInfo: String
        get() = if (checkFlag.isChecked) {
            "AT+" + commandEdit.text.toString() + "=" + parameterEdit.text.toString()
        } else ""

    var customizeCommandCountChange: ((count: Int) -> Unit)? = null

    fun setCommandInfo(comand: String?, info: String?) {
        commandEdit.setText(comand)
        parameterEdit.setText(info)
        checkFlag.isChecked = true
    }



    init {
        val view = inflate(context, R.layout.customize_at_command, this)
        label = view.findViewById<View>(R.id.label) as TextView
        parameterEdit = view.findViewById<View>(R.id.parameter) as EditText
        commandEdit = view.findViewById<View>(R.id.command) as EditText

        checkFlag = view.findViewById<View>(R.id.checkFlag) as CheckBox
        checkFlag.setOnCheckedChangeListener { _, isChecked ->
            if (isChecked) {
                if (parameterEdit.text.toString().isNotEmpty() && commandEdit.text.toString()
                        .isNotEmpty()
                ) {
                    parameterEdit.isEnabled = !isChecked
                    commandEdit.isEnabled = !isChecked
                    CommandPresenters.plus()
                } else {
                    checkFlag.isChecked = false
                    ToastUtil.show(getContext(), resources.getString(R.string.none))
                }
            } else {
                parameterEdit.isEnabled = !isChecked
                commandEdit.isEnabled = !isChecked
                CommandPresenters.minus()
            }
        }
    }
}