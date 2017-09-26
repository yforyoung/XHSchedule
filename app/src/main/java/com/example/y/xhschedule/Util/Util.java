package com.example.y.xhschedule.Util;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Message;
import android.util.Log;
import android.widget.ImageView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.bumptech.glide.signature.StringSignature;
import com.example.y.xhschedule.LoadActivity;
import com.example.y.xhschedule.gson.Courses;
import com.example.y.xhschedule.gson.Student;
import com.google.gson.Gson;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.FormBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

/**
 * Created by yforyoung on 2017/9/25.
 */

public class Util {
    public Util() {
    }



    /*存储网络数据--json格式*/
    public void save(String data, Context context){
        FileOutputStream out=null;
        BufferedWriter writer=null;
        try {
            out=context.openFileOutput("scheduleData", Context.MODE_PRIVATE);
            writer=new BufferedWriter(new OutputStreamWriter(out));
            writer.write(data);
            SharedPreferences.Editor preferences=context.getSharedPreferences("saved",Context.MODE_PRIVATE).edit();
            preferences.putInt("saved",1);
            preferences.apply();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }finally {
            try{
                if (writer!=null)
                    writer.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

    }





    /*读取本地文件数据*/
    public String readJSON(Context context){
        FileInputStream inputStream=null;
        BufferedReader reader=null;
        StringBuilder stringBuilder=new StringBuilder();
        try {
            inputStream=context.openFileInput("scheduleData");
            reader=new BufferedReader(new InputStreamReader(inputStream));
            String line="";
            while((line=reader.readLine())!=null)
                stringBuilder.append(line);
        } catch (IOException e) {
            e.printStackTrace();
        }finally {
            if (reader!=null){
                try {
                    reader.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
        return stringBuilder.toString();
    }



/*处理json返回Student类*/
    public Student parseJSONWithGSON(InputStream inputStream)
    {
        Gson gson=new Gson();
        Student student=gson.fromJson(new InputStreamReader(inputStream),Student.class);
        return student;
    }


    private Student parseJSONWithGSON(String jsonData) {
        Gson gson=new Gson();
        Student student=gson.fromJson(jsonData,Student.class);
        return student;
    }


    /*加载课表数据*/
    public void loadData(Student student, Courses [][]courses, int weekNow, List<Courses> coursesList, Map<Integer,ArrayList<Courses>> coursesHashMap)
    {
        Message message=new Message();
        message.what=2;

        for(Courses c:student.getCourses()){
            int t=(c.getTime()[1]/2);
            int w=c.getWeek();
            if (courses[t-1][w-1]==null&&c.getWeekStart()<=weekNow&&c.getWeekEnd()>=weekNow){
                courses[t-1][w-1]=c;
            }
        }
        for (Courses c: student.getCourses()){
            int t=(c.getTime()[1]/2);
            int w=c.getWeek();
            if (courses[t-1][w-1]==null){
                courses[t-1][w-1]=c;
            }else if (courses[t-1][w-1]!=null&&courses[t-1][w-1]!=c){
             if (coursesHashMap.get((t-1)*7+w-1)!=null){
                 coursesHashMap.get((t-1)*7+w-1).add(c);
             }else{
                ArrayList<Courses> list=new ArrayList<>();
                list.add(c);
                coursesHashMap.put((t-1)*7+w-1,list);
             }
            }
        }

        for(int i=0;i<5;i++){
            for(int j=0;j<7;j++)
                if(courses[i][j]==null)
                    coursesList.add(new Courses("  ","  "));
                else{
                    coursesList.add(courses[i][j]);
                }
        }
    }



    /*获取网络图片*/
    public void getPic(Context context, ImageView imageView){
        Glide.with(context)
                .load("http://courses.ngrok.cc/flask/login")
                .signature(new StringSignature(String.valueOf(System.currentTimeMillis())))
                .into(imageView);
    }
}
