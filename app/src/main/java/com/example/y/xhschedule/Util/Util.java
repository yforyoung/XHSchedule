package com.example.y.xhschedule.Util;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.Message;

import com.example.y.xhschedule.beans.Course;
import com.example.y.xhschedule.beans.Student;
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
import java.util.HashMap;
import java.util.List;
import java.util.Map;


public class Util {
    public Util() {
    }

    /*存储网络数据--json格式*/
    public void save(String data, Context context) {
        FileOutputStream out;
        BufferedWriter writer = null;
        try {
            out = context.openFileOutput("scheduleData", Context.MODE_PRIVATE);
            writer = new BufferedWriter(new OutputStreamWriter(out));
            writer.write(data);
            SharedPreferences.Editor preferences = context.getSharedPreferences("saved", Context.MODE_PRIVATE).edit();
            preferences.putInt("saved", 1);
            preferences.apply();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            try {
                if (writer != null)
                    writer.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

    }


    /*读取本地文件数据*/
    public String readJSON(Context context) {
        FileInputStream inputStream;
        BufferedReader reader = null;
        StringBuilder stringBuilder = new StringBuilder();
        try {
            inputStream = context.openFileInput("scheduleData");
            reader = new BufferedReader(new InputStreamReader(inputStream));
            String line;
            while ((line = reader.readLine()) != null)
                stringBuilder.append(line);
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            if (reader != null) {
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
    public Student parseJSONWithGSON(InputStream inputStream) {
        Gson gson = new Gson();
        return gson.fromJson(new InputStreamReader(inputStream), Student.class);
    }


    public Student parseJSONWithGSON(String jsonData) {
        Gson gson = new Gson();
        return gson.fromJson(jsonData, Student.class);
    }


    /*加载课表数据*/
    public List<Course> loadData(int weekNow, Map<Integer, ArrayList<Course>> coursesHashMap) {
        Course[][] courses = new Course[5][7];
        Student student = Test.getInstance().student;
        List<Course> courseList = new ArrayList<>();
        Message message = new Message();
        message.what = 2;

        for (Course c : student.getCourses()) {
            int t = (c.getTime()[1] / 2);
            int w = c.getWeek();

            if (courses[t - 1][w - 1] == null && c.getWeekStart() <= weekNow && c.getWeekEnd() >= weekNow) {
                courses[t - 1][w - 1] = c;
            }
        }
        for (Course c : student.getCourses()) {
            int t = (c.getTime()[1] / 2);
            int w = c.getWeek();
            if (courses[t - 1][w - 1] == null) {
                if (weekNow < c.getWeekStart() || weekNow > c.getWeekEnd())
                    courses[t - 1][w - 1] = c;
            } else if (courses[t - 1][w - 1] != null) {
                ArrayList<Course> list;
                if (!c.equals(courses[t - 1][w - 1])) {
                    if (coursesHashMap.containsKey((t - 1) * 7 + w - 1)) {
                        list = coursesHashMap.get((t - 1) * 7 + w - 1);
                    } else {
                        list = new ArrayList<>();
                    }
                    list.add(c);
                    coursesHashMap.put((t - 1) * 7 + w - 1, list);
                }
            }
        }

        for (int i = 0; i < 5; i++) {
            for (int j = 0; j < 7; j++)
                if (courses[i][j] == null)
                    courseList.add(new Course("","",-1));
                else {
                    courseList.add(courses[i][j]);
                }
        }
        return courseList;
    }


}
