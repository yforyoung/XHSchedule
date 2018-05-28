package com.example.y.xhschedule.beans;

import com.google.gson.annotations.SerializedName;

import java.util.List;

/**
 * Created by yforyoung on 2017/9/14.
 */

public class Student {
    private String sno;
    private String sname;
    private String college;
    private String profession;

    @SerializedName("class")
    private String sClass;

    private List<Course> courses;


    public String getSno()
    {
        return sno;
    }

    public void setSno(String sno) {
        this.sno = sno;
    }

    public String getSname() {
        return sname;
    }

    public void setSname(String sname) {
        this.sname = sname;
    }

    public String getCollege() {
        return college;
    }

    public void setCollege(String college) {
        this.college = college;
    }

    public String getProfession() {
        return profession;
    }

    public void setProfession(String profession) {
        this.profession = profession;
    }

    public String getsClass() {
        return sClass;
    }

    public void setsClass(String sClass) {
        this.sClass = sClass;
    }

    public List<Course> getCourses() {
        return courses;
    }

    public void setCourses(List<Course> courses) {
        this.courses = courses;
    }
}
