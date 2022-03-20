package com.feasycom.feasyblue.widget;

import android.content.Context;
import android.content.res.TypedArray;
import android.text.InputFilter;
import android.util.AttributeSet;
import android.view.View;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.feasycom.feasyblue.R;
import com.feasycom.feasyblue.presenters.CommandPresenters;
import com.feasycom.feasyblue.utils.ToastUtil;


public class ForceAtCommandView extends LinearLayout {
    TextView label;
    EditText contextEdit;
    CheckBox checkFlag;
    String labelString;

    public ForceAtCommandView(final Context context, AttributeSet attrs) {
        super(context, attrs);
        View view = View.inflate(context, R.layout.force_at_command, this);
//        ButterKnife.bind(view);
        label = (TextView) view.findViewById(R.id.label);
        contextEdit = (EditText) view.findViewById(R.id.context);

        checkFlag = (CheckBox) view.findViewById(R.id.checkFlag);
        checkFlag.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (contextEdit.getText().toString().length() > 0) {
                    contextEdit.setEnabled(!isChecked);
                    if (isChecked){
                        CommandPresenters.INSTANCE.plus();
                    }else {
                        CommandPresenters.INSTANCE.minus();
                    }
                } else {
                    checkFlag.setChecked(false);
                    ToastUtil.show(getContext(),getResources().getString(R.string.none));
                }
            }
        });
        //引用资源文件 需要用TypedArray
        TypedArray ta = context.obtainStyledAttributes(attrs, R.styleable.forceAtCommandView);
        labelString = ta.getString(R.styleable.forceAtCommandView_label);
        label.setText(labelString);
    }

    public void setLabel(String label) {
        this.label.setText(label);
    }

    public void setInputType(int type){
        contextEdit.setInputType(type);
    }

    public void setMaxLength(int i){
        contextEdit.setFilters(new InputFilter[]{new InputFilter.LengthFilter(i)});

    }

    public void setContext(String context) {
        contextEdit.setText(context);
    }

    public String getCommandInfo(){
        if(checkFlag.isChecked()){
            if(getContext().getString(R.string.pin).equals(labelString)){
                return "AT+PIN="+contextEdit.getText().toString();
            }else if(getContext().getString(R.string.deviceName).equals(labelString)){
                return contextEdit.getText().toString();
            }else if(getContext().getString(R.string.baud).equals(labelString)){
                return "AT+BAUD="+contextEdit.getText().toString();
            }
        }
        return null;
    }
    public void setCommandInfo(String info){
        contextEdit.setText(info);
        checkFlag.setChecked(true);
        // CommandPresenters.INSTANCE.plus();
    }
}