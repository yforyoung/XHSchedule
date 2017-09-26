package com.example.y.xhschedule;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.TextView;

import com.example.y.xhschedule.gson.Courses;

public class CoursePage extends AppCompatActivity {
    private TextView courseName,courseTime,courseTeacher,courseLoc,courseWeek,courseZhou;
    private Courses courses;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.course_page);
        courseName= (TextView) findViewById(R.id.course_name);
        courseTeacher= (TextView) findViewById(R.id.course_teacher);
        courseTime= (TextView) findViewById(R.id.course_time);
        courseLoc= (TextView) findViewById(R.id.course_loc);
        courseWeek= (TextView) findViewById(R.id.course_week);
        courseZhou= (TextView) findViewById(R.id.course_zhou);

        Intent intent=getIntent();
        courses= (Courses) intent.getSerializableExtra("course");
        courseName.setText(courses.getName().toString());
        courseTime.setText("第"+(courses.getTime()[1]-1)/2+"节课");
        courseTeacher.setText(courses.getTeacher().toString());
        courseLoc.setText(courses.getLocation().toString());
        courseWeek.setText(courses.getWeekStart()+" 到 "+courses.getWeekEnd()+"周");
       // courseZhou.setText("单双周："+courses.getZhou()[0]);


    }
}
