package com.example.aimers.Services;

import android.app.Activity;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.appcompat.widget.Toolbar;

import com.example.aimers.Interfaces.MenuClickListner;
import com.example.aimers.R;


public class AppBar {

    Activity activity;
    Toolbar toolbar;
    ImageView backButton,humbergerButton;
    TextView appBarTv;
    public AppBar(Activity activity) {
        this.activity = activity;
    }

    public  void init(Toolbar toolbar,String text){
        this.toolbar=toolbar;
         backButton = (ImageView)toolbar.findViewById(R.id.appbar_BackButton);
         appBarTv=toolbar.findViewById(R.id.appBarText);
         humbergerButton=toolbar.findViewById(R.id.appbar_HmbergarButton);

        appBarTv.setText(""+text);
        backButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
               activity.finish();
            }
        });
    }
    public void setAppBarText(String text){
        appBarTv.setText(""+text);
    }
    public void showMenuButton(){
        if(backButton!=null){
            backButton.setVisibility(View.GONE);
        }
        if(humbergerButton!=null){
            humbergerButton.setVisibility(View.VISIBLE);
        }
    }

    public void setMenuButtonClickListner(MenuClickListner listner){

        humbergerButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                listner.onClicked(v);
            }
        });

    }
    public void hideBackButton(){
        if(backButton!=null){
            backButton.setVisibility(View.GONE);
        }
    }
}

