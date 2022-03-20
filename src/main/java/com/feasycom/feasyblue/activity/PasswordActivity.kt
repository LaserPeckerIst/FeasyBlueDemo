package com.feasycom.feasyblue.activity

import android.content.Context
import android.content.Intent
import android.text.Editable
import android.text.TextWatcher
import android.view.KeyEvent
import com.feasycom.feasyblue.R
import kotlinx.android.synthetic.main.activity_password.*

class PasswordActivity: BaseActivity() {

    override fun initView() {
        setSupportActionBar(toolbar)
        supportActionBar?.let {
            it.setDisplayShowTitleEnabled(false);
            it.setDisplayHomeAsUpEnabled(true)
        }
        toolbarTitle.text = getString(R.string.parameterDefining)
        toolbar.setNavigationOnClickListener {
            MainActivity.activityStart(this, 1)
            finish()
        }

        passwordEditView.editText?.addTextChangedListener(object: TextWatcher{
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {

            }

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                if(s?.length == 8){
                    if (s.toString() == "20138888"){
                        ParameterModificationActivity.activityStart(this@PasswordActivity)
                        finish()
                    }else {
                        passwordEditView.error = "密码错误"
                    }
                }
            }

            override fun afterTextChanged(s: Editable?) {
                if (s?.length != 8){
                    passwordEditView.error = ""
                }
            }

        })

    }

    override fun getLayout() = R.layout.activity_password

    override fun onKeyDown(keyCode: Int, event: KeyEvent?): Boolean {
        return if ((keyCode == KeyEvent.KEYCODE_BACK)) {
            MainActivity.activityStart(this, 1)
            finish()
            false;
        }else {
            super.onKeyDown(keyCode, event);
        }
    }


    companion object{
        private const val TAG = "PasswordActivity"

        fun activityStart(context: Context){
            val intent = Intent(context, PasswordActivity::class.java)
            context.startActivity(intent)
        }
    }
}