package com.feasycom.feasyblue.activity

import android.content.Context
import android.content.Intent
import android.text.InputType.TYPE_CLASS_NUMBER
import android.util.Log
import android.view.KeyEvent
import android.view.View
import com.feasycom.feasyblue.R
import com.feasycom.feasyblue.interfaces.ICommandCallback
import com.feasycom.feasyblue.presenters.CommandPresenters
import com.feasycom.feasyblue.utils.ToastUtil
import com.feasycom.feasyblue.utils.getStrSet
import com.feasycom.feasyblue.utils.putStrSet
import kotlinx.android.synthetic.main.activity_parameter_modification.*
import kotlinx.android.synthetic.main.activity_password.toolbar
import kotlinx.android.synthetic.main.activity_password.toolbarTitle
import java.util.*

class ParameterModificationActivity: BaseActivity(), View.OnClickListener, ICommandCallback {
    private lateinit var commandSet: HashSet<String>
    private lateinit var commandNoParamSet: HashSet<String>

    override fun initView() {
        initToolbar()
        toolbarButton.text = getString(R.string.begin)
        toolbarButton.setOnClickListener(this)
        customizeCommandButton.setOnClickListener(this)
        CommandPresenters.mICommandCallback = this
        initData()

        commandName.setMaxLength(29)
        with(commandPin) {
            setMaxLength(6)
            setInputType(TYPE_CLASS_NUMBER)
        }
        with(commandBaud) {
            setInputType(TYPE_CLASS_NUMBER)
            setMaxLength(6)
        }
    }

    private  fun initToolbar() {
        setSupportActionBar(toolbar)
        supportActionBar?.let {
            it.setDisplayShowTitleEnabled(false)
            it.setDisplayHomeAsUpEnabled(true)
        }
        toolbarTitle.text = getString(R.string.parameterDefining)
        toolbar.setNavigationOnClickListener {
            saveCommand()
            MainActivity.activityStart(this, 1)
            finish()
        }
    }

    private fun initData(){

        commandSet = getStrSet("commandSet",HashSet<String>())
        Log.e("TAG", "initData: " + commandSet )

        commandNoParamSet = getStrSet("commandNoParamSet",HashSet<String>())

        if(commandSet.isNotEmpty()){
            for (command in commandSet) {
                when {
                    command.contains("AT+NAME") -> {
                        commandName.commandInfo =
                            command.substring(command.indexOf("=") + 1, command.length)
                    }
                    command.contains("AT+PIN") -> {
                        commandPin.commandInfo =
                            command.substring(command.indexOf("=") + 1, command.length)

                    }
                    command.contains("AT+BAUD") -> {
                        commandBaud.commandInfo =
                            command.substring(command.indexOf("=") + 1, command.length)
                    }
                    command.contains("=") -> {
                        val comm = command.substring(command.indexOf("+") + 1, command.indexOf("="))
                        val param = command.substring(command.indexOf("=") + 1, command.length)

                        Log.e("TAG", "initData: comm   -> ${comm}    param  -> ${param}" )
                        when {
                            customizeCommand1.commandInfo.isBlank() -> {
                                customizeCommand1.setCommandInfo(comm, param)
                            }
                            customizeCommand2.commandInfo.isBlank() -> {
                                customizeCommand2.setCommandInfo(comm, param)
                            }
                            customizeCommand3.commandInfo.isBlank() -> {
                                customizeCommand3.setCommandInfo(comm, param)
                            }
                            customizeCommand4.commandInfo.isBlank() -> {
                                customizeCommand4.setCommandInfo(comm, param)
                            }
                            customizeCommand5.commandInfo.isBlank() -> {
                                customizeCommand5.setCommandInfo(comm, param)
                            }
                            customizeCommand6.commandInfo.isBlank() -> {
                                customizeCommand6.setCommandInfo(comm, param)
                            }
                            customizeCommand7.commandInfo.isBlank() -> {
                                customizeCommand7.setCommandInfo(comm, param)
                            }
                            customizeCommand8.commandInfo.isBlank() -> {
                                customizeCommand8.setCommandInfo(comm, param)
                            }
                            customizeCommand9.commandInfo.isBlank() -> {
                                customizeCommand9.setCommandInfo(comm, param)
                            }
                        }
                    }
                }
            }
        }

        if (commandNoParamSet.isNotEmpty()) {
            for (param in commandNoParamSet) {
                val comm = param.substring(param.indexOf("+") + 1, param.length)
                when {
                    customizeCommandNoParam1.commandInfo.isBlank() -> {
                        customizeCommandNoParam1.commandInfo = comm
                    }
                    customizeCommandNoParam2.commandInfo.isBlank() -> {
                        customizeCommandNoParam2.commandInfo = comm
                    }
                    customizeCommandNoParam3.commandInfo.isBlank() -> {
                        customizeCommandNoParam3.commandInfo = comm
                    }
                    customizeCommandNoParam4.commandInfo.isBlank() -> {
                        customizeCommandNoParam4.commandInfo = comm
                    }
                    customizeCommandNoParam5.commandInfo.isBlank() -> {
                        customizeCommandNoParam5.commandInfo = comm
                    }
                    customizeCommandNoParam6.commandInfo.isBlank() -> {
                        customizeCommandNoParam6.commandInfo = comm
                    }
                    customizeCommandNoParam7.commandInfo.isBlank() -> {
                        customizeCommandNoParam7.commandInfo = comm
                    }
                }
            }
        }
    }

    override fun getLayout() = R.layout.activity_parameter_modification

    companion object{
        private const val TAG = "ParameterModificationActivity"
        fun activityStart(context: Context){
            val intent = Intent(context, ParameterModificationActivity::class.java)
            context.startActivity(intent)
        }
    }

    override fun onClick(v: View?) {
        when(v?.id){
            R.id.toolbarButton -> {
                saveCommand()
                ParameterModificationDeviceListActivity.activityStart(this)
            }
            R.id.customizeCommandButton -> {
                if (customizeCommandButton.text.toString() == getString(R.string.customizeCommand)) {
                    customizeCommandButton.setText(R.string.customizeCommandHid)
                    customizeCommand1.visibility = View.INVISIBLE
                    customizeCommand2.visibility = View.INVISIBLE
                    customizeCommand3.visibility = View.INVISIBLE
                    customizeCommand4.visibility = View.INVISIBLE
                    customizeCommand5.visibility = View.INVISIBLE
                    customizeCommand6.visibility = View.INVISIBLE
                    customizeCommand7.visibility = View.INVISIBLE
                    customizeCommand8.visibility = View.INVISIBLE
                    customizeCommand9.visibility = View.INVISIBLE
                    customizeCommandNoParam1.visibility = View.INVISIBLE
                    customizeCommandNoParam2.visibility = View.INVISIBLE
                    customizeCommandNoParam3.visibility = View.INVISIBLE
                    customizeCommandNoParam4.visibility = View.INVISIBLE
                    customizeCommandNoParam5.visibility = View.INVISIBLE
                    customizeCommandNoParam6.visibility = View.INVISIBLE
                    customizeCommandNoParam7.visibility = View.INVISIBLE
                } else {
                    customizeCommandButton.text = getString(R.string.customizeCommand)
                    customizeCommand1.visibility = View.VISIBLE
                    customizeCommand2.visibility = View.VISIBLE
                    customizeCommand3.visibility = View.VISIBLE
                    customizeCommand4.visibility = View.VISIBLE
                    customizeCommand5.visibility = View.VISIBLE
                    customizeCommand6.visibility = View.VISIBLE
                    customizeCommand7.visibility = View.VISIBLE
                    customizeCommand8.visibility = View.VISIBLE
                    customizeCommand9.visibility = View.VISIBLE
                    customizeCommandNoParam1.visibility = View.VISIBLE
                    customizeCommandNoParam2.visibility = View.VISIBLE
                    customizeCommandNoParam3.visibility = View.VISIBLE
                    customizeCommandNoParam4.visibility = View.VISIBLE
                    customizeCommandNoParam5.visibility = View.VISIBLE
                    customizeCommandNoParam6.visibility = View.VISIBLE
                    customizeCommandNoParam7.visibility = View.VISIBLE
                }
            }
        }
    }

    private fun saveCommand() {
        commandSet.clear()
        commandNoParamSet.clear()
        if (!commandSet.contains("AT+VER")) {
            commandSet.add("AT+VER")
        }
        if (commandName.commandInfo != null) {
            commandSet.add("AT+NAME=" + commandName.commandInfo)
        }
        if (commandPin.commandInfo != null) {
            commandSet.add(commandPin.commandInfo)
        }
        if (commandBaud.commandInfo != null) {
            commandSet.add(commandBaud.commandInfo)
        }
        if (customizeCommand1.commandInfo.isNotBlank()) {
            Log.e("TAG", "saveCommand: 添加" )
            commandSet.add(customizeCommand1.commandInfo)
        }
        if (customizeCommand2.commandInfo.isNotBlank()) {
            commandSet.add(customizeCommand2.commandInfo)
        }
        if (customizeCommand3.commandInfo.isNotBlank()) {
            commandSet.add(customizeCommand3.commandInfo)
        }
        if (customizeCommand4.commandInfo.isNotBlank()) {
            commandSet.add(customizeCommand4.commandInfo)
        }
        if (customizeCommand5.commandInfo.isNotBlank()) {
            commandSet.add(customizeCommand5.commandInfo)
        }
        if (customizeCommand6.commandInfo.isNotBlank()) {
            commandSet.add(customizeCommand6.commandInfo)
        }
        if (customizeCommand7.commandInfo.isNotBlank()) {
            commandSet.add(customizeCommand7.commandInfo)
        }
        if (customizeCommand8.commandInfo.isNotBlank()) {
            commandSet.add(customizeCommand8.commandInfo)
        }
        if (customizeCommand9.commandInfo.isNotBlank()) {
            commandSet.add(customizeCommand9.commandInfo)
        }

        if (customizeCommandNoParam1.commandInfo.isNotBlank()) {
            commandNoParamSet.add(customizeCommandNoParam1.commandInfo)
        }
        if (customizeCommandNoParam2.commandInfo.isNotBlank()) {
            commandNoParamSet.add(customizeCommandNoParam2.commandInfo)
        }
        if (customizeCommandNoParam3.commandInfo.isNotBlank()) {
            commandNoParamSet.add(customizeCommandNoParam3.commandInfo)
        }
        if (customizeCommandNoParam4.commandInfo.isNotBlank()) {
            commandNoParamSet.add(customizeCommandNoParam4.commandInfo)
        }
        if (customizeCommandNoParam5.commandInfo.isNotBlank()) {
            commandNoParamSet.add(customizeCommandNoParam5.commandInfo)
        }
        if (customizeCommandNoParam6.commandInfo.isNotBlank()) {
            commandNoParamSet.add(customizeCommandNoParam6.commandInfo)
        }
        if (customizeCommandNoParam7.commandInfo.isNotBlank()) {
            commandNoParamSet.add(customizeCommandNoParam7.commandInfo)
        }
        if (commandSet.size == 0 && commandNoParamSet.size == 0) {
            ToastUtil.show(this, getString(R.string.commandNull))
        }
        Log.e("TAG", "saveCommand: " + commandSet )
        putStrSet("commandSet", commandSet)
        putStrSet("commandNoParamSet", commandNoParamSet)
    }

    override fun onKeyDown(keyCode: Int, event: KeyEvent?): Boolean {
        return if ((keyCode == KeyEvent.KEYCODE_BACK)) {
            saveCommand()
            MainActivity.activityStart(this, 1)
            finish()
            false
        }else {
            super.onKeyDown(keyCode, event)
        }
    }

    override fun update(i: Int) {
        Log.e("TAG", "数据  -> " + i )
        toolbarButton.isEnabled = i != 0
        customizeSelectCount.text = i.toString()
    }
}