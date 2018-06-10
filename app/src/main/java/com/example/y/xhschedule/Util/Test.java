package com.example.y.xhschedule.Util;

import com.example.y.xhschedule.beans.Student;

public class Test {
    private static Test test;
    public int weekNow;
    public int login;
    public Student student;
    public int isFail=0;


    public static Test getInstance(){
        if (test==null)
            test=new Test();
        return test;
    }
}
