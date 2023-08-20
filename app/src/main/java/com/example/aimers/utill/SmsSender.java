package com.example.aimers.utill;

import android.app.Activity;
import android.os.Handler;
import android.os.Looper;
import android.telephony.SmsManager;
import android.widget.Toast;

import com.example.aimers.Views.Admin.SmsManagerActivity;
import com.example.aimers.Views.Admin.StudentMessageManagerAcitivity;

import java.util.ArrayList;

public class SmsSender {

        public static void sendSMSInBackground(Activity activity,final String phoneNumber, final String message,String name) {
            Thread thread = new Thread(new Runnable() {
                @Override
                public void run() {
                    sendSmsMessage(activity,phoneNumber, message,name);
                }
            });
            thread.start();
        }
        private static void sendSmsMessage(Activity activity,String phoneNumber, String message,String name) {
            try {
                SmsManager smsManager = SmsManager.getDefault();
                ArrayList<String> parts = smsManager.divideMessage(message);
                smsManager.sendMultipartTextMessage(phoneNumber, null, parts, null, null);

                Handler handler = new Handler(Looper.getMainLooper());
                handler.post(new Runnable() {
                    @Override
                    public void run() {
                        Toast.makeText(activity, "SMS sent to " + name, Toast.LENGTH_SHORT).show();
                    }
                });

            }catch (Exception e) {
                Handler handler = new Handler(Looper.getMainLooper());
                handler.post(new Runnable() {
                    @Override
                    public void run() {
                        Toast.makeText(activity, "SMS failed to " + name, Toast.LENGTH_SHORT).show();
                    }
                });
                    e.printStackTrace();
                }
        }
}
