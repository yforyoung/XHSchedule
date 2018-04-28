package com.example.y.xhschedule;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

import com.example.y.xhschedule.gson.Courses;

public class CoursePage extends AppCompatActivity {

    @SuppressLint("SetTextI18n")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.course_page);
        TextView courseName = findViewById(R.id.course_name);
        TextView courseTeacher = findViewById(R.id.course_teacher);
        TextView courseTime = findViewById(R.id.course_time);
        TextView courseLoc = findViewById(R.id.course_loc);
        TextView courseWeek = findViewById(R.id.course_week);
        TextView courseZhou = findViewById(R.id.course_zhou);

        Intent intent=getIntent();
        Courses courses = (Courses) intent.getSerializableExtra("course");
        courseName.setText(courses.getName());
        courseTime.setText("第"+(1+ courses.getTime()[1]-1)/2+"节课");
        courseTeacher.setText(courses.getTeacher());
        courseLoc.setText(courses.getLocation());
        courseWeek.setText(courses.getWeekStart()+" 到 "+ courses.getWeekEnd()+"周");
        if (courses.getZhou().equals("")){
            courseZhou.setVisibility(View.INVISIBLE);
        }


    }
}
