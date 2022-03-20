package com.feasycom.feasyblue.widget;

import android.content.Context;
import android.util.AttributeSet;
import android.view.View;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.feasycom.feasyblue.R;
import com.feasycom.feasyblue.presenters.CommandPresenters;
import com.feasycom.feasyblue.utils.ToastUtil;


public class CustomizeAtCommandNoParamView extends LinearLayout{
    TextView label;
//    EditText parameterEdit;
    EditText commandEdit;
    CheckBox checkFlag;

    public CustomizeAtCommandNoParamView(Context context, AttributeSet attrs) {
        super(context, attrs);
        View view = View.inflate(context, R.layout.customize_at_command_no_param, this);
        label = (TextView) view.findViewById(R.id.label);
        commandEdit = (EditText) view.findViewById(R.id.command);
        checkFlag = (CheckBox) view.findViewById(R.id.checkFlag);
        checkFlag.setOnCheckedChangeListener((buttonView, isChecked) -> {
            if (isChecked) {
                if (commandEdit.getText().toString().length() > 0) {
                    commandEdit.setEnabled(false);
                    CommandPresenters.INSTANCE.plus();
                } else {
                    checkFlag.setChecked(false);
                    ToastUtil.show(getContext(), getResources().getString(R.string.none));
                }
            } else {
                commandEdit.setEnabled(true);
                CommandPresenters.INSTANCE.minus();
            }
        });
    }
    public String getCommandInfo(){
        if(checkFlag.isChecked()){
            return "AT+"+commandEdit.getText().toString()/*+"="+parameterEdit.getText().toString()*/;
        }
        return "";
    }
    public void setCommandInfo(String command){
        commandEdit.setText(command);
        checkFlag.setChecked(true);
    }


}