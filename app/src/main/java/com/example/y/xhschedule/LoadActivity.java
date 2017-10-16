package com.example.y.xhschedule;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Handler;
import android.os.Message;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.webkit.CookieManager;
import android.webkit.CookieSyncManager;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;
import com.example.y.xhschedule.Util.Util;
import java.io.IOException;
import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.FormBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

public class LoadActivity extends AppCompatActivity {
    private final String TAG="info";

    private TextView userId;
    private TextView changePic;
    private TextView password;
    private TextView yzm;
    private WebView yzmPic;
    private Button login;

    private String sID="";
    private String sPasswd="";
    private String sYzm="";
    private String c;
    private ProgressDialog progressDialog;


    private SharedPreferences.Editor editor;
    private SharedPreferences pref;

    private Util util=new Util();

    private Handler handler=new Handler() {
        @Override
        public void handleMessage(Message msg) {
            progressDialog.hide();
            switch (msg.what){
                case 000:
                    Toast.makeText(LoadActivity.this, "DNS解析失败或者连接被拒绝", Toast.LENGTH_SHORT).show();
                    break;
                case 111:
                    Toast.makeText(LoadActivity.this, "连接超时", Toast.LENGTH_SHORT).show();
                    break;
                case 222:
                    Toast.makeText(LoadActivity.this, "验证码错误", Toast.LENGTH_SHORT).show();
                    break;
                case 333:
                    Toast.makeText(LoadActivity.this, "账号或密码错误", Toast.LENGTH_SHORT).show();
                    break;
                case 555:
                    Toast.makeText(LoadActivity.this, "验证码获取失败", Toast.LENGTH_SHORT).show();
                    break;
                case 666:
                    Toast.makeText(LoadActivity.this, "课表数据获取失败", Toast.LENGTH_SHORT).show();
                    break;
                default:
                    Toast.makeText(LoadActivity.this, "未知错误", Toast.LENGTH_SHORT).show();
                    break;
            }
        }
    };





    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.load_page);

        userId= (TextView) findViewById(R.id.id_input);
        password= (TextView) findViewById(R.id.password_input);
        login= (Button) findViewById(R.id.load_button);
        yzm= (TextView) findViewById(R.id.yzm_input);
        yzmPic= (WebView) findViewById(R.id.yzm_pic);
        changePic= (TextView) findViewById(R.id.change_pic);

        CookieSyncManager.createInstance(this);
        yzmPic.setWebViewClient(new MyWebViewClient());
        yzmPic.getSettings().setJavaScriptEnabled(true);
        yzmPic.setWebViewClient(new MyWebViewClient());
        CookieManager cookieManager = CookieManager.getInstance();
        cookieManager.setAcceptCookie(true);
        yzmPic.loadUrl("http://course.xhban.com:8000/login");

        editor=getSharedPreferences("login",MODE_PRIVATE).edit();
        pref=getSharedPreferences("login",MODE_PRIVATE);

        changePic.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                yzmPic.loadUrl("http://course.xhban.com:8000/login");
            }
        });

        login.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                login();


            }
        });

        if (!pref.getString("user_id","unload").equals("unload")){
            Intent intent=new Intent(getApplicationContext(),MainActivity.class);
            startActivity(intent);
            finish();
        }

    }
    public void login(){
        sID=userId.getText().toString();
        sPasswd=password.getText().toString();
        sYzm=yzm.getText().toString();
        c=getCookieShared();
        Log.i(TAG, "login: "+c);
        progressDialog=ProgressDialog.show(LoadActivity.this,"提示","登陆中",false,true);
        new Thread(new Runnable() {
            @Override
            public void run() {

                OkHttpClient client=new OkHttpClient();
                RequestBody requestBody=new FormBody.Builder()
                        .add("username",sID)
                        .add("password",sPasswd)
                        .add("checkcode",sYzm)
                        .build();
                Request request=new Request.Builder()
                        .url("http://course.xhban.com:8000/courses")
                        .addHeader("cookie",c)
                        .post(requestBody)
                        .build();
                client.newCall(request).enqueue(new Callback() {
                    @Override
                    public void onFailure(Call call, IOException e) {
                    }

                    @Override
                    public void onResponse(Call call, Response response) throws IOException {
                        String data=response.body().string();
                        Log.i("info", "onResponse: "+data);
                        if (data.length()<=6){
                            Message msg=new Message();
                            msg.what=Integer.parseInt(data);
                            handler.sendMessage(msg);

                        }else {
                            Log.i("info", "onResponse: load");
                            util.save(data,getApplicationContext());
                            editor.putString("user_id",sID);
                            editor.putString("password",sPasswd);
                            editor.apply();
                            Intent intent=new Intent(getApplicationContext(),MainActivity.class);
                            startActivity(intent);
                            finish();
                        }
                        /*Intent intent=new Intent(getApplicationContext(),MainActivity.class);
                        startActivity(intent);
                        finish();*/

                    }
                });
            }
        }).start();

    }

    private class MyWebViewClient extends WebViewClient {

        public void onPageFinished(WebView view, String url) {
            CookieManager cookieManager = CookieManager.getInstance();
            String CookieStr = cookieManager.getCookie(url);
            saveCookieShared(CookieStr);
            super.onPageFinished(view, url);
        }

    }



    public void saveCookieShared(String cookie){
        SharedPreferences.Editor spf=getSharedPreferences("cookie_save",Context.MODE_PRIVATE).edit();
        Log.i("info", "saveCookieShared: "+cookie);
        spf.putString("cookie",cookie);
        spf.apply();
    }

    public String getCookieShared(){
        SharedPreferences spf=getSharedPreferences("cookie_save",Context.MODE_PRIVATE);
        String c=spf.getString("cookie","");
        Log.i("info", "getCookieShared: "+c);
        return c;
    }
}
