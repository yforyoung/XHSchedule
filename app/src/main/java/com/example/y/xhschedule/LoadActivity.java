package com.example.y.xhschedule;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.bumptech.glide.signature.StringSignature;
import com.example.y.xhschedule.Util.Util;

import java.io.BufferedWriter;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStreamWriter;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.FormBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

public class LoadActivity extends AppCompatActivity {
    private TextView userId;
    private TextView password;
    private TextView yzm;
    private ImageView yzmPic;
    private Button login;

    private String sID="";
    private String sPasswd="";
    private String sYzm="";
    private int checked=0;

    private SharedPreferences.Editor editor;
    private SharedPreferences pref;

    private Util util=new Util();




    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.load_page);

        userId= (TextView) findViewById(R.id.id_input);
        password= (TextView) findViewById(R.id.password_input);
        login= (Button) findViewById(R.id.load_button);
        yzm= (TextView) findViewById(R.id.yzm_input);
        yzmPic= (ImageView) findViewById(R.id.yzm_pic);

        sID=userId.getText().toString();
        sPasswd=password.getText().toString();
        sYzm=yzm.getText().toString();

        editor=getSharedPreferences("login",MODE_PRIVATE).edit();
        pref=getSharedPreferences("login",MODE_PRIVATE);

        yzmPic.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                util.getPic(getApplicationContext(),yzmPic);
            }
        });

        login.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                login();
            }
        });

     /*   if (!pref.getString("user_id","unload").equals("unload")){
            Intent intent=new Intent(getApplicationContext(),MainActivity.class);
            startActivity(intent);
            finish();
        }*/

    }
    public void login(){

        new Thread(new Runnable() {
            @Override
            public void run() {
                Log.i("info", "run: kaiqi");
                OkHttpClient client=new OkHttpClient();
                RequestBody requestBody=new FormBody.Builder()
                        .add("username",sID)
                        .add("password",sPasswd)
                        .add("checkcode",sYzm)
                        .build();
                Request request=new Request.Builder()
                        .url("http://courses.ngrok.cc/flask/courses")
                        .post(requestBody)
                        .build();
                client.newCall(request).enqueue(new Callback() {
                    @Override
                    public void onFailure(Call call, IOException e) {
                        Toast.makeText(LoadActivity.this, "密码或验证码错误！", Toast.LENGTH_SHORT).show();
                        editor.putString("user_id","unload");
                        editor.putString("password","unload");
                        editor.apply();
                        util.getPic(getApplicationContext(),yzmPic);
                    }

                    @Override
                    public void onResponse(Call call, Response response) throws IOException {

                        String jsonData=response.body().string();
                        Log.i("info", "onResponse: "+jsonData);
                        Toast.makeText(LoadActivity.this, "load", Toast.LENGTH_SHORT).show();
                        util.save(jsonData,getApplicationContext());
                        editor.putString("user_id",sID);
                        editor.putString("password",sPasswd);
                        editor.apply();
                   /*     Intent intent=new Intent(getApplicationContext(),MainActivity.class);
                        startActivity(intent);
                        finish();*/

                    }
                });
            }
        }).start();

    }




}
