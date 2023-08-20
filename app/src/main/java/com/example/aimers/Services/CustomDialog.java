package com.example.aimers.Services;

import android.app.Activity;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import androidx.appcompat.app.AlertDialog;

import com.example.aimers.Interfaces.CustomDialogClickListner;
import com.example.aimers.R;

public class CustomDialog {
    private Activity context;
    private Button okButton,cancelButton;
    private TextView titleTv;
    AlertDialog dialog;
    AlertDialog.Builder builder;
    public CustomDialog(Activity context) {
        this.context = context;
        builder=new AlertDialog.Builder(context);
        View view=context.getLayoutInflater().inflate(R.layout.custom_alert_dialog,null);
        builder.setView(view);
        titleTv=view.findViewById(R.id.customDialog_TitleTvId);

        okButton=view.findViewById(R.id.customDialog_OkButtonId);
        cancelButton=view.findViewById(R.id.customDialog_CancelButtonId);
        dialog=builder.create();
    }
    public void show(String message) {
        titleTv.setText(""+message);
        dialog.show();
    }
    public void onActionClick(CustomDialogClickListner listner){
        okButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(listner!=null){
                    listner.onPositiveButtonClicked(v,dialog);
                }

            }
        });
        cancelButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(listner!=null) {
                    listner.onNegativeButtonClicked(v, dialog);
                }
            }
        });
    }
}
