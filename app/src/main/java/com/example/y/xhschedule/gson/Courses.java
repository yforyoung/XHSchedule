package com.example.y.xhschedule.gson;

import com.google.gson.annotations.SerializedName;

import java.io.Serializable;


public class Courses implements Serializable{
    private String name;

    @SerializedName("week_index")
    private int week;

    @SerializedName("course_index")
    private int time[];

    @SerializedName("week_start")
    private int weekStart;

    @SerializedName("week_end")
    private int weekEnd;

    private String teacher;
    private String location;
    private String zhou;


    public Courses() {
    }

    public Courses(String name, String location) {
        this.name = name;
        this.location = location;
    }

    public String getZhou() {
        return zhou;
    }

    public void setZhou(String zhou) {
        this.zhou = zhou;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int getWeek() {
        return week;
    }

    public void setWeek(int week) {
        this.week = week;
    }

    public int[] getTime() {
        return time;
    }

    public void setTime(int[] time) {
        this.time = time;
    }

    public int getWeekStart() {
        return weekStart;
    }

    public void setWeekStart(int weekStart) {
        this.weekStart = weekStart;
    }

    public int getWeekEnd() {
        return weekEnd;
    }

    public void setWeekEnd(int weekEnd) {
        this.weekEnd = weekEnd;
    }

    public String getTeacher() {
        return teacher;
    }

    public void setTeacher(String teacher) {
        this.teacher = teacher;
    }

    public String getLocation() {
        return location;
    }

    public void setLocation(String location) {
        this.location = location;
    }
}
