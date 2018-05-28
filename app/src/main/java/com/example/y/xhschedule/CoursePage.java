package com.example.y.xhschedule;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.TextView;

import com.example.y.xhschedule.beans.Course;

public class CoursePage extends AppCompatActivity {

    @SuppressLint("SetTextI18n")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.course_page);
        Toolbar toolbar=findViewById(R.id.toolbar);
        TextView title=findViewById(R.id.toolbar_title);
        title.setText("课程信息");
        toolbar.setTitle("");
        setSupportActionBar(toolbar);
        toolbar.setNavigationIcon(R.drawable.ic_arrow_back);
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });


        TextView courseName = findViewById(R.id.course_name);
        TextView courseTeacher = findViewById(R.id.course_teacher);
        TextView courseTime = findViewById(R.id.course_time);
        TextView courseLoc = findViewById(R.id.course_loc);
        TextView courseWeek = findViewById(R.id.course_week);
        TextView courseZhou = findViewById(R.id.course_zhou);

        Intent intent=getIntent();
        Course course = (Course) intent.getSerializableExtra("course");
        courseName.setText(course.getName());
        courseTime.setText("第"+(1+ course.getTime()[1]-1)/2+"节课");
        courseTeacher.setText(course.getTeacher());
        courseLoc.setText(course.getLocation());
        courseWeek.setText(course.getWeekStart()+" 到 "+ course.getWeekEnd()+"周");
        if (course.getZhou().equals("")){
            courseZhou.setVisibility(View.INVISIBLE);
        }


    }
}
