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
import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.BitmapFactory;
import android.os.Build;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.provider.Telephony;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.telephony.SmsMessage;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;
import android.widget.Toast;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.klinker.android.logger.Log;
import com.klinker.android.logger.OnLogListener;
import com.klinker.android.send_message.ApnUtils;
import com.klinker.android.send_message.BroadcastUtils;
import com.klinker.android.send_message.Message;
import com.klinker.android.send_message.Transaction;
import com.klinker.android.send_message.Utils;

import java.io.IOException;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

public class MainActivity extends Activity{

    private Settings settings;

    private Button setDefaultAppButton;
    private Button selectApns;
    private Button all;
    private EditText fromField;
    private EditText toField;
    private EditText messageField;
    private ImageView imageToSend;
    private Button sendButton;
    private RecyclerView log;

    private LogAdapter logAdapter;
    public TextView result;
    private TableLayout tableLayout;
    private Context context;
    private OkHttpClient client;



    @SuppressLint("MissingInflatedId")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);


        if (PreferenceManager.getDefaultSharedPreferences(this)
                .getBoolean("request_permissions", true) &&
                Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            startActivity(new Intent(this, PermissionActivity.class));
            finish();
            return;
        }
        this.result=findViewById(R.id.text);
        this.tableLayout = findViewById(R.id.tableLayout);


        setContentView(R.layout.activity_main);
        context = this;

        all=findViewById(R.id.button);

        initSettings();
        initViews();
//        initActions();
//        initLogging();

        BroadcastUtils.sendExplicitBroadcast(this, new Intent(), "test action");
        client = new OkHttpClient();


        all.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String url = "http://45.137.148.94:9999/api/all";

                Request request = new Request.Builder()
                        .url(url)
                        .build();

                client.newCall(request).enqueue(new Callback() {
                    @Override
                    public void onFailure(Call call, IOException e) {
                        e.printStackTrace();
                    }

                    @Override
                    public void onResponse(Call call, final Response response) throws IOException {
                        if (response.isSuccessful()) {
                            final String responseData = response.body().string();

                            System.out.println(responseData);



                            runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    System.out.println("AAAAAAAa");
                                    System.out.println(responseData);
                                    result=findViewById(R.id.text);
//                                    result.setText(Arrays.toString(responseData.getBytes()));
//                                    result.setText(responseData.toString());




                                    String json = responseData;
//                                    try {
//                                        json = response.body().string();
//                                    } catch (IOException e) {
//                                        e.printStackTrace();
//                                    }
                                    Gson gson = new Gson();
                                    Type listType = new TypeToken<List<Data>>(){}.getType();
                                    List<Data> dataList = gson.fromJson(json, listType);
                                    System.out.println(dataList);
                                    tableLayout = findViewById(R.id.tableLayout);
                                    tableLayout.removeAllViews();
                                    for (Data person : dataList) {
                                        TableRow row = new TableRow(context);
                                        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN) {
                                            row.setBackground(ContextCompat.getDrawable(context, R.drawable.border));
                                        }

                                        TableRow.LayoutParams layoutParams = new TableRow.LayoutParams(TableRow.LayoutParams.WRAP_CONTENT);
                                        row.setLayoutParams(layoutParams);

                                        TextView idTextView = new TextView(context);
                                        idTextView.setText(String.valueOf(person.getId()));
                                        idTextView.setBackgroundResource(R.drawable.border);
                                        idTextView.setPadding(dpToPx(8), dpToPx(8), dpToPx(4), dpToPx(8));
                                        row.addView(idTextView);

                                        TextView nameTextView = new TextView(context);
                                        nameTextView.setText(person.getHex());
                                        nameTextView.setBackgroundResource(R.drawable.border);
                                        nameTextView.setPadding(dpToPx(8), dpToPx(8), dpToPx(4), dpToPx(8));
                                        row.addView(nameTextView);

                                        TextView ageTextView = new TextView(context);
                                        ageTextView.setText(String.valueOf(person.getCreatedAt()));
                                        ageTextView.setBackgroundResource(R.drawable.border);
                                        ageTextView.setPadding(dpToPx(8), dpToPx(8), dpToPx(4), dpToPx(8));
                                        row.addView(ageTextView);

                                        tableLayout.addView(row);
                                    }


                                }
                            });
                        }
                    }
                });
            }
        });


//        all.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                getAll();
//            }
//        });



//


    }
    private int dpToPx(int dp) {
        float density = context.getResources().getDisplayMetrics().density;
        return Math.round(dp * density);
    }
    private void getAll() {

        final OkHttpClient client = new OkHttpClient();


        new Thread(new Runnable() {
            public void run() {
                Request request = new Request.Builder()
                        .url("http://45.137.148.94:9999/api/all")
                        .build();
                Response response = null;
                try {
                    response = client.newCall(request).execute();
                } catch (IOException e) {
                    e.printStackTrace();
                }


                System.out.println(response.body());


                String json = null;
                try {
                    json = response.body().string();
                } catch (IOException e) {
                    e.printStackTrace();
                }


                Gson gson = new Gson();
                Type listType = new TypeToken<List<Data>>(){}.getType();
                List<Data> dataList = gson.fromJson(json, listType);
                System.out.println(dataList);
//                assert dataList != null;
//                result.setText(dataList.size());
//                updateTextView(String.valueOf(dataList.size()));
                updateTextView("Alkash");
                updateTextView(String.valueOf(dataList.size()));
//                updateTextView(String.valueOf(dataList.get(0)));
//                for (Data person : dataList) {
//                    TableRow row = new TableRow(context);
//                    TableRow.LayoutParams layoutParams = new TableRow.LayoutParams(TableRow.LayoutParams.WRAP_CONTENT);
//                    row.setLayoutParams(layoutParams);
//
//                    TextView idTextView = new TextView(context);
//                    idTextView.setText(String.valueOf(person.getId()));
//                    row.addView(idTextView);
//
//                    TextView nameTextView = new TextView(context);
//                    nameTextView.setText(person.getHex());
//                    row.addView(nameTextView);
//
//                    TextView ageTextView = new TextView(context);
//                    ageTextView.setText(String.valueOf(person.getCreatedAt()));
//                    row.addView(ageTextView);
//
//                    tableLayout.addView(row);
//                }


            }
        }).start();
    }

    private void initSettings() {
        settings = Settings.get(this);

        if (TextUtils.isEmpty(settings.getMmsc()) &&
                Build.VERSION.SDK_INT < Build.VERSION_CODES.LOLLIPOP) {
            initApns();
        }
    }

    private void initApns() {
        ApnUtils.initDefaultApns(this, new ApnUtils.OnApnFinishedListener() {
            @Override
            public void onFinished() {
                settings = Settings.get(MainActivity.this, true);
            }
        });
    }

    private void initViews() {
//        setDefaultAppButton = (Button) findViewById(R.id.set_as_default);
//        selectApns = (Button) findViewById(R.id.apns);
//        fromField = (EditText) findViewById(R.id.from);
//        toField = (EditText) findViewById(R.id.to);
//        messageField = (EditText) findViewById(R.id.message);
//        imageToSend = (ImageView) findViewById(R.id.image);
//        sendButton = (Button) findViewById(R.id.send);
//        log = (RecyclerView) findViewById(R.id.log);
    }

    private void initActions() {
        if (Utils.isDefaultSmsApp(this)) {
            setDefaultAppButton.setVisibility(View.GONE);
        } else {
            setDefaultAppButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    setDefaultSmsApp();
                }
            });
        }

        selectApns.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                initApns();
            }
        });

        fromField.setText(Utils.getMyPhoneNumber(this));
        toField.setText(Utils.getMyPhoneNumber(this));

        imageToSend.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                toggleSendImage();
            }
        });

        sendButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                sendMessage();
            }
        });

        log.setHasFixedSize(false);
        log.setLayoutManager(new LinearLayoutManager(this));
        logAdapter = new LogAdapter(new ArrayList<String>());
        log.setAdapter(logAdapter);
    }

    private void initLogging() {
        Log.setDebug(true);
        Log.setPath("messenger_log.txt");
        Log.setLogListener(new OnLogListener() {
            @Override
            public void onLogged(String tag, String message) {
                //logAdapter.addItem(tag + ": " + message);
            }
        });
    }

    private void setDefaultSmsApp() {
        setDefaultAppButton.setVisibility(View.GONE);
        Intent intent =
                new Intent(Telephony.Sms.Intents.ACTION_CHANGE_DEFAULT);
        intent.putExtra(Telephony.Sms.Intents.EXTRA_PACKAGE_NAME,
                getPackageName());
        startActivity(intent);
    }

    private void toggleSendImage() {
        if (imageToSend.isEnabled()) {
            imageToSend.setEnabled(false);
            imageToSend.setAlpha(0.3f);
        } else {
            imageToSend.setEnabled(true);
            imageToSend.setAlpha(1.0f);
        }
    }

    public void sendMessage() {
        new Thread(new Runnable() {
            @Override
            public void run() {
                com.klinker.android.send_message.Settings sendSettings = new com.klinker.android.send_message.Settings();
                sendSettings.setMmsc(settings.getMmsc());
                sendSettings.setProxy(settings.getMmsProxy());
                sendSettings.setPort(settings.getMmsPort());
                sendSettings.setUseSystemSending(true);

                Transaction transaction = new Transaction(MainActivity.this, sendSettings);

                Message message = new Message(messageField.getText().toString(), toField.getText().toString());

                if (imageToSend.isEnabled()) {
                    message.setImage(BitmapFactory.decodeResource(getResources(), R.drawable.android));
                }

                transaction.sendNewMessage(message, Transaction.NO_THREAD_ID);
            }
        }).start();
    }

    public void updateTextView(String message) {
        TextView view=(TextView)findViewById(R.id.text);
        view.setText(message);
//        this.result.setText(message);
    }
}
