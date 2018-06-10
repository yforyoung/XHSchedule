package com.example.y.xhschedule;

import android.Manifest;
import android.annotation.SuppressLint;
import android.annotation.TargetApi;
import android.app.DatePickerDialog;
import android.content.ContentUris;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.DocumentsContract;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.design.widget.BottomNavigationView;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.FragmentManager;
import android.support.v4.content.ContextCompat;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AlertDialog;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.DatePicker;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.example.y.xhschedule.Util.Test;
import com.example.y.xhschedule.Util.Util;
import com.example.y.xhschedule.adapter.ViewPagerAdapter;
import com.example.y.xhschedule.beans.EventBean;
import com.example.y.xhschedule.beans.Student;

import org.greenrobot.eventbus.EventBus;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;


public class MainActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener, ViewPager.OnPageChangeListener, BottomNavigationView.OnNavigationItemSelectedListener {

    private TextView studentName;
    private TextView studentID;
    private RelativeLayout contentMain;
    private SharedPreferences.Editor weekEditor;
    private TextView toolbarTitle;
    private BottomNavigationView bottomNavigationView;
    private ViewPager viewPager;
    private int weekNow = 1;
    private int y, m, d;
    private String dateString;

    @SuppressLint("SetTextI18n")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        initView();
        setWeekNow();

        Util util = new Util();
        String temp = util.readJSON(this);
        Log.i("info", "onCreate: "+temp);
        if (temp.contains("500")){
            Toast.makeText(this,"教务系统错误",Toast.LENGTH_SHORT).show();
            Test.getInstance().student = new Student();
            Test.getInstance().isFail=1;
        }else{
            Student student = util.parseJSONWithGSON(temp);
            Test.getInstance().student = student;

            studentName.setText(student.getSname());
            studentID.setText(student.getSno());
        }



    }

    @SuppressLint("SetTextI18n")
    private void setWeekNow() {
        SharedPreferences pref = getSharedPreferences("weekNow", MODE_PRIVATE);
        dateString = pref.getString("date_start", "2018-03-01");
        getDate();
        @SuppressLint("SimpleDateFormat") SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
        try {
            Date d1 = sdf.parse(dateString);
            Date d2 = sdf.parse(y + "-" + (++m) + "-" + d);
            weekNow = ((int) ((d2.getTime() - d1.getTime()) / (1000 * 60 * 60 * 24)) / 7) + 1;
            Test.getInstance().weekNow = weekNow;
            toolbarTitle.setText("第" + weekNow + "周");
        } catch (ParseException e) {
            e.printStackTrace();
        }
    }

    @SuppressLint("CommitPrefEdits")
    private void initView() {

        @SuppressLint("CutPasteId") NavigationView head = findViewById(R.id.nav_view);
        studentID = head.getHeaderView(0).findViewById(R.id.student_id);
        studentName = head.getHeaderView(0).findViewById(R.id.student_name);
        contentMain = findViewById(R.id.content_main_linear);
        toolbarTitle = findViewById(R.id.toolbar_title);

        Toolbar toolbar = findViewById(R.id.toolbar);
        toolbar.setTitle("");
        setSupportActionBar(toolbar);

        DrawerLayout drawer = findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.addDrawerListener(toggle);
        toggle.syncState();

        @SuppressLint("CutPasteId") NavigationView navigationView = findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);
        weekEditor = getSharedPreferences("weekNow", MODE_PRIVATE).edit();

        if (readPicPath() != null) {
            displayImage(readPicPath());
        }

        bottomNavigationView = findViewById(R.id.bottom_nav_view);
        viewPager = findViewById(R.id.viewpager);

        FragmentManager fragmentManager = getSupportFragmentManager();
        ViewPagerAdapter viewPagerAdapter = new ViewPagerAdapter(fragmentManager);

        viewPager.setAdapter(viewPagerAdapter);
        viewPager.addOnPageChangeListener(this);
        bottomNavigationView.setOnNavigationItemSelectedListener(this);

    }


    @Override
    public void onBackPressed() {
        DrawerLayout drawer = findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    /*菜单点击事件*/
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        AlertDialog.Builder builder = new AlertDialog.Builder(MainActivity.this);
        final String[] weekC = {"1", "2", "3", "4", "5", "6", "7", "8", "9", "10", "11", "12", "13", "14", "15", "16", "17", "18", "19", "20"};
        builder.setItems(weekC, new DialogInterface.OnClickListener() {
            @SuppressLint("SetTextI18n")
            @Override
            public void onClick(DialogInterface dialog, int which) {
                weekNow = which + 1;
                toolbarTitle.setText("第" + weekNow + "周");
                EventBus.getDefault().post(new EventBean(weekNow));
            }
        });
        builder.show();

        return super.onOptionsItemSelected(item);
    }


    /*侧边栏点击事件*/
    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
        int id = item.getItemId();

        switch (id) {
            case R.id.background_choose:
                if (ContextCompat.checkSelfPermission(MainActivity.this,
                        Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
                    ActivityCompat.requestPermissions(MainActivity.this, new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, 1);
                } else {
                    openAlbum();
                }
                break;
            case R.id.reload:
                SharedPreferences.Editor preferences = getSharedPreferences("login", MODE_PRIVATE).edit();
                preferences.putString("user_id", "unload");
                preferences.apply();
                Intent intent = new Intent(getApplicationContext(), LoadActivity.class);
                startActivity(intent);
                finish();
                break;
            case R.id.exit:
                finish();
                break;
            case R.id.reset_bg:
                picSaved("");
                displayImage("");
                break;
            case R.id.today_course:
                viewPager.setCurrentItem(0);
                return true;

            case R.id.week_course:
                viewPager.setCurrentItem(1);
                return true;
            case R.id.date_start:
                new DatePickerDialog(MainActivity.this, 0, new DatePickerDialog.OnDateSetListener() {
                    @Override
                    public void onDateSet(DatePicker view, int year, int month, int dayOfMonth) {
                        weekEditor.putString("date_start", year + "-" + (++month) + "-" + dayOfMonth);       //mon从0开始
                        weekEditor.apply();
                        setWeekNow();
                        EventBus.getDefault().post(new EventBean(weekNow));
                    }
                }, Integer.parseInt(dateString.substring(0, 4)),
                        Integer.parseInt(dateString.substring(5, dateString.lastIndexOf("-")))-1,
                        Integer.parseInt(dateString.substring(dateString.lastIndexOf("-")+1, dateString.length())))
                        .show();
                return true;
            default:

        }
        DrawerLayout drawer = findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

    private void getDate() {
        Calendar calendar = Calendar.getInstance();
        y = calendar.get(Calendar.YEAR);
        m = calendar.get(Calendar.MONTH);
        d = calendar.get(Calendar.DAY_OF_MONTH);
    }

    private void openAlbum() {
        Intent intent = new Intent("android.intent.action.GET_CONTENT");
        intent.setType("image/*");
        startActivityForResult(intent, 2);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        switch (requestCode) {
            case 1:
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    openAlbum();
                } else {
                    Toast.makeText(this, "你拒绝了权限", Toast.LENGTH_SHORT).show();
                }
                break;
            default:
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        switch (requestCode) {
            case 2:
                if (resultCode == RESULT_OK) {
                    if (Build.VERSION.SDK_INT >= 19) {
                        handleImageOnKitKat(data);
                    } else
                        handleImageBeforeKitKat(data);
                }
        }
    }

    private void handleImageBeforeKitKat(Intent data) {
        Uri uri = data.getData();
        String imagePath = getImagePath(uri, null);
        displayImage(imagePath);
    }

    private void displayImage(String imagePath) {
        if (imagePath != null) {
            Bitmap bitmap = BitmapFactory.decodeFile(imagePath);
            BitmapDrawable bd = new BitmapDrawable(bitmap);
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN) {
                contentMain.setBackground(bd);
            }
            //设置背景
        } else {
            Toast.makeText(this, "获取图片失败", Toast.LENGTH_SHORT).show();
        }
    }

    @TargetApi(19)
    private void handleImageOnKitKat(Intent data) {
        String imagePath = null;
        Uri uri = data.getData();
        if (DocumentsContract.isDocumentUri(this, uri)) {
            String docId = DocumentsContract.getDocumentId(uri);
            assert uri != null;
            if ("com.android.providers.media.documents".equals(uri.getAuthority())) {
                String id = docId.split(":")[1];
                String selection = MediaStore.Images.Media._ID + "=" + id;
                imagePath = getImagePath(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, selection);
            } else if ("com.android..providers.downloads.documents".equals(uri.getAuthority())) {
                Uri contentUri = ContentUris.withAppendedId(Uri.parse("content://downloads//public_downloads"), Long.valueOf(docId));
                imagePath = getImagePath(contentUri, null);
            }
        } else {
            assert uri != null;
            if ("content".equalsIgnoreCase(uri.getScheme())) {
                imagePath = getImagePath(uri, null);
            } else if ("file".equalsIgnoreCase(uri.getScheme())) {
                imagePath = uri.getPath();
            }
        }
        if (picSaved(imagePath)) {
            displayImage(readPicPath());
        }

    }

    private String getImagePath(Uri uri, String selection) {
        String path = null;
        Cursor cursor = getContentResolver().query(uri, null, selection, null, null);
        if (cursor != null) {
            if (cursor.moveToFirst()) {
                path = cursor.getString(cursor.getColumnIndex(MediaStore.Images.Media.DATA));
            }
            cursor.close();
        }

        return path;
    }

    public boolean picSaved(String picPath) {
        SharedPreferences.Editor editor = getSharedPreferences("picPath", MODE_PRIVATE).edit();
        editor.putString("pic_path", picPath);
        editor.apply();
        return true;
    }

    public String readPicPath() {
        SharedPreferences preferences = getSharedPreferences("picPath", MODE_PRIVATE);
        return preferences.getString("pic_path", "");
    }


    @Override
    public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

    }

    @Override
    public void onPageSelected(int position) {
        switch (position) {
            case 0:
                bottomNavigationView.setSelectedItemId(R.id.today_course);
                break;
            case 1:
                bottomNavigationView.setSelectedItemId(R.id.week_course);
                break;
        }

    }

    @Override
    public void onPageScrollStateChanged(int state) {

    }
}
