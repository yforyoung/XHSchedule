package com.example.y.xhschedule;

import android.Manifest;
import android.annotation.SuppressLint;
import android.annotation.TargetApi;
import android.content.ContentUris;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.drawable.BitmapDrawable;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.provider.DocumentsContract;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.StaggeredGridLayoutManager;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;

import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.example.y.xhschedule.Util.Util;
import com.example.y.xhschedule.gson.Courses;
import com.example.y.xhschedule.gson.Student;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;

public class MainActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener {

    private List<Courses> coursesList =new ArrayList<>();
    private Courses courses[][]=new Courses[5][7];
   private HashMap<Integer, ArrayList<Courses>> coursesHashMap;
    private Util util=new Util();
    private TextView studentName;
    private TextView studentID;
    private ScheduleAdapter adapter;
    private LinearLayout contentMain;
    private SharedPreferences.Editor weekEditor;
    private TextView toolbarTitle;

    private int weekNow=1;
    private String stuName;
    private String stuID;


    @SuppressLint("HandlerLeak")
    private Handler handler=new Handler(){
        public void handleMessage(Message msg){
            switch (msg.what){
                case 1:
                    studentName.setText(stuName);
                    studentID.setText(stuID);
                    adapter.notifyDataSetChanged();

                    adapter.setmOnItemClickListener(new ScheduleAdapter.OnItemClickListener() {
                        @Override
                        public void onItemClick(View view, final int position) {

                            final Courses temp=coursesList.get(position);
                            final ArrayList<Courses> list=coursesHashMap.get(position);

                            if (!temp.getName().equals("  ")){
                                if(coursesHashMap.get(position)==null){
                                    Intent intent=new Intent(getApplicationContext(),CoursePage.class);
                                    intent.putExtra("course",temp);
                                    startActivity(intent);
                                }else {
                                    final int size=list.size();
                                    String []cont=new String[list.size()+1];
                                    if (temp.getWeekStart()<=weekNow&&weekNow<=temp.getWeekEnd()){
                                        String zhou;
                                        zhou=temp.getZhou();
                                        if (zhou.equals(""))
                                            cont[0]=temp.getName()+"（今日有课）";
                                        else if (zhou.contains("双周")&&weekNow%2==0)
                                            cont[0]=temp.getName()+"（今日有课）";
                                        else if (zhou.contains("单周")&&(weekNow+1)%2==0) {
                                            cont[0]=temp.getName()+"（今日有课）";
                                        }
                                        else
                                            cont[0]=temp.getName();
                                    }else{
                                        cont[0]=temp.getName();
                                    }

                                    int i=0;
                                    for (Courses c:list){
                                        if (c.getWeekStart()<=weekNow&&weekNow<=c.getWeekEnd()){
                                            String zhou;
                                            zhou=c.getZhou();
                                            if (zhou.equals(""))
                                                cont[++i]=c.getName()+"（今日有课）";
                                            else if (zhou.contains("双周")&&weekNow%2==0)
                                                cont[++i]=c.getName()+"（今日有课）";
                                            else if (zhou.contains("单周")&&(weekNow+1)%2==0) {
                                                cont[++i]=c.getName()+"（今日有课）";
                                            }
                                            else
                                                cont[++i]=c.getName();
                                        }else {
                                            cont[++i]=c.getName();
                                        }

                                    }

                                    AlertDialog.Builder builder=new AlertDialog.Builder(MainActivity.this);
                                    builder.setItems(cont, new DialogInterface.OnClickListener() {
                                        @Override
                                        public void onClick(DialogInterface dialog, int which) {
                                            Intent intent=new Intent(getApplicationContext(),CoursePage.class);
                                            switch (which){
                                                case 0:
                                                    intent.putExtra("course",temp);
                                                    break;
                                                case 1:
                                                    intent.putExtra("course",list.get(0));
                                                    break;
                                                case 2:
                                                    intent.putExtra("course",list.get(1));
                                                    break;
                                                case 3:
                                                    intent.putExtra("course",list.get(2));
                                                    break;
                                                default:
                                                    intent.putExtra("course",list.get(size-1));
                                                    break;
                                            }
                                            startActivity(intent);
                                        }
                                    });
                                    builder.show();
                                }
                            }
                        }
                    });
                    break;
                default:
            }
        }
    };




    @SuppressLint({"UseSparseArrays", "CommitPrefEdits", "SetTextI18n"})
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        @SuppressLint("CutPasteId") NavigationView head= findViewById(R.id.nav_view);
        studentID= head.getHeaderView(0).findViewById(R.id.student_id);
        studentName= head.getHeaderView(0).findViewById(R.id.student_name);
        contentMain= findViewById(R.id.content_main_linear);
        toolbarTitle= findViewById(R.id.toolbar_title);
        TextView sun = findViewById(R.id.sunday);
        TextView mon = findViewById(R.id.monday);
        TextView tues = findViewById(R.id.tuesday);
        TextView wed = findViewById(R.id.wednesday);
        TextView thur = findViewById(R.id.thursday);
        TextView fri = findViewById(R.id.friday);
        TextView satu = findViewById(R.id.saturday);


        Calendar calendar=Calendar.getInstance();
        int today=calendar.get(Calendar.DAY_OF_WEEK);
        switch (today){
            case 1:
                sun.setBackgroundColor(Color.parseColor("#9BF2AAAA"));
                break;
            case 2:
                mon.setBackgroundColor(Color.parseColor("#9BF2AAAA"));
                break;
            case 3:
                tues.setBackgroundColor(Color.parseColor("#9BF2AAAA"));
                break;
            case 4:
                wed.setBackgroundColor(Color.parseColor("#9BF2AAAA"));
                break;
            case 5:
                thur.setBackgroundColor(Color.parseColor("#9BF2AAAA"));
                break;
            case 6:
                fri.setBackgroundColor(Color.parseColor("#9BF2AAAA"));
                break;
            case 7:
                satu.setBackgroundColor(Color.parseColor("#9BF2AAAA"));
                break;

        }


        coursesHashMap=new HashMap<>();

        Toolbar toolbar = findViewById(R.id.toolbar);
        toolbar.setTitle("");
        setSupportActionBar(toolbar);

        DrawerLayout drawer = findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.setDrawerListener(toggle);
        toggle.syncState();

        @SuppressLint("CutPasteId") NavigationView navigationView = findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);
        weekEditor=getSharedPreferences("weekNow",MODE_PRIVATE).edit();

        if(readPicPath()!=null){
            displayImage(readPicPath());
        }


        SharedPreferences pref=getSharedPreferences("weekNow",MODE_PRIVATE);
        weekNow=pref.getInt("week_now",1);
        toolbarTitle.setText("第"+weekNow+"周");
        RecyclerView recyclerView = findViewById(R.id.schedule_recycler_view);
        StaggeredGridLayoutManager layoutManager = new StaggeredGridLayoutManager(7, StaggeredGridLayoutManager.VERTICAL);
        recyclerView.setLayoutManager(layoutManager);
        adapter=new ScheduleAdapter(coursesList,weekNow);
        recyclerView.setAdapter(adapter);

        initSchedule();
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
        AlertDialog.Builder builder=new AlertDialog.Builder(MainActivity.this);
        final String []weekC={"1","2","3","4","5","6","7","8","9","10","11","12","13","14","15","16","17","18","19","20"};
        builder.setItems(weekC, new DialogInterface.OnClickListener() {
            @SuppressLint("SetTextI18n")
            @Override
            public void onClick(DialogInterface dialog, int which) {
                weekNow=which+1;
                toolbarTitle.setText("第"+weekNow+"周");
                weekEditor.putInt("week_now",weekNow);
                weekEditor.apply();
                adapter.setWeek(weekNow);
                initSchedule();
                adapter.notifyDataSetChanged();
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

        switch (id){
            case R.id.background_choose:
                if (ContextCompat.checkSelfPermission(MainActivity.this,
                        Manifest.permission.WRITE_EXTERNAL_STORAGE)!= PackageManager.PERMISSION_GRANTED){
                    ActivityCompat.requestPermissions(MainActivity.this,new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE},1);
                }else {
                    openAlbum();
                }
                break;
            case R.id.reload:
                SharedPreferences.Editor preferences=getSharedPreferences("login",MODE_PRIVATE).edit();
                preferences.putString("user_id","unload");
                preferences.apply();
                Intent intent=new Intent(getApplicationContext(),LoadActivity.class);
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
            default:
        }
        DrawerLayout drawer = findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

    private void openAlbum() {
        Intent intent=new Intent("android.intent.action.GET_CONTENT");
        intent.setType("image/*");
        startActivityForResult(intent,2);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        switch (requestCode){
            case 1:
                if(grantResults.length>0 && grantResults[0]==PackageManager.PERMISSION_GRANTED){
                    openAlbum();
                }else {
                    Toast.makeText(this, "你拒绝了权限", Toast.LENGTH_SHORT).show();
                }
                break;
            default:
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        switch (requestCode){
            case 2:
                if (resultCode==RESULT_OK){
                    if (Build.VERSION.SDK_INT>=19){
                        handleImageOnKitKat(data);
                    }else
                        handleImageBeforeKitKat(data);
                }
        }
    }

    private void handleImageBeforeKitKat(Intent data) {
        Uri uri=data.getData();
        String imagePath=getImagePath(uri,null);
        displayImage(imagePath);
    }

    private void displayImage(String imagePath) {
        if (imagePath!=null){
            Bitmap bitmap= BitmapFactory.decodeFile(imagePath);
            BitmapDrawable bd= new BitmapDrawable(bitmap);
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN) {
                contentMain.setBackground(bd);
            }
            //设置背景
        }else {
            Toast.makeText(this, "获取图片失败", Toast.LENGTH_SHORT).show();
        }
    }

    @TargetApi(19)
    private void handleImageOnKitKat(Intent data) {
        String imagePath=null;
        Uri uri=data.getData();
        if (DocumentsContract.isDocumentUri(this,uri)){
            String docId=DocumentsContract.getDocumentId(uri);
            assert uri != null;
            if ("com.android.providers.media.documents".equals(uri.getAuthority())){
                String id=docId.split(":")[1];
                String selection=MediaStore.Images.Media._ID+"="+id;
                imagePath=getImagePath(MediaStore.Images.Media.EXTERNAL_CONTENT_URI,selection);
            }else if("com.android..providers.downloads.documents".equals(uri.getAuthority())){
                Uri contentUri= ContentUris.withAppendedId(Uri.parse("content://downloads//public_downloads"),Long.valueOf(docId));
                imagePath=getImagePath(contentUri,null);
            }
        }else {
            assert uri != null;
            if("content".equalsIgnoreCase(uri.getScheme())){
                imagePath=getImagePath(uri,null);
            }else if ("file".equalsIgnoreCase(uri.getScheme())){
                imagePath=uri.getPath();
            }
        }
        if(picSaved(imagePath)){
            displayImage(readPicPath());
        }

    }

    private String getImagePath(Uri uri, String selection) {
        String path=null;
        Cursor cursor=getContentResolver().query(uri,null,selection,null,null);
        if (cursor!=null){
            if (cursor.moveToFirst()) {
                path = cursor.getString(cursor.getColumnIndex(MediaStore.Images.Media.DATA));
            }
            cursor.close();
        }

        return path;
    }

    public boolean picSaved(String picPath){
        SharedPreferences.Editor editor=getSharedPreferences("picPath",MODE_PRIVATE).edit();
        editor.putString("pic_path",picPath);
        editor.apply();
        return true;
    }

    public String readPicPath(){
        SharedPreferences preferences=getSharedPreferences("picPath",MODE_PRIVATE);
        return preferences.getString("pic_path","");
    }




    public void initSchedule(){
        String temp=util.readJSON(getApplicationContext());
        if (temp.contains("college")){
            for(int i=0;i<5;i++){
                for (int j=0;j<7;j++)
                    courses[i][j]=null;
            }
            if (!coursesHashMap.isEmpty())
                coursesHashMap.clear();

            coursesList.clear();
            Student student = util.parseJSONWithGSON(temp);
            util.loadData(student,courses,weekNow,coursesList,coursesHashMap);
            stuName= student.getSname();
            stuID= student.getSno();

            Message message=new Message();
            message.what=1;
            handler.sendMessage(message);
        }else {
            Toast.makeText(this, "信息获取失败！", Toast.LENGTH_SHORT).show();
        }
    }

}
