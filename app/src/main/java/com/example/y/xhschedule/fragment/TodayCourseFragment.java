package com.example.y.xhschedule.fragment;

import android.support.v4.app.Fragment;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.y.xhschedule.R;
import com.example.y.xhschedule.Util.Test;
import com.example.y.xhschedule.adapter.TodayCourseAdapter;
import com.example.y.xhschedule.beans.Course;
import com.example.y.xhschedule.beans.EventBean;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

import static org.greenrobot.eventbus.EventBus.TAG;

public class TodayCourseFragment extends Fragment {
    private TodayCourseAdapter adapter;
    private List<Course> courseList;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.today_course_fragment, container, false);
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EventBus.getDefault().register(this);
    }


    @Subscribe
    public void onEvent(EventBean event) {
        Log.i(TAG, "onEvent: ");
        changeData();
    }

    private void changeData() {
        courseList.clear();

        List<Course> cList = Test.getInstance().student.getCourses();
        Calendar calendar = Calendar.getInstance();
        int today = calendar.get(Calendar.DAY_OF_WEEK);
        int weekNow = Test.getInstance().weekNow;
        for (Course c : cList) {
            if (weekNow <= c.getWeekEnd() && weekNow >= c.getWeekStart()) {
                if (c.getWeek() == today - 1)
                    courseList.add(c);
                else if (c.getWeek() == 7 && today == 1)
                    courseList.add(c);
            }
        }
        adapter.notifyDataSetChanged();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        EventBus.getDefault().unregister(this);
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        RecyclerView recyclerView = getActivity().findViewById(R.id.today_course_list);
        recyclerView.setLayoutManager(new LinearLayoutManager(getActivity(), LinearLayoutManager.VERTICAL, false));
        courseList = new ArrayList<>();
        adapter = new TodayCourseAdapter(courseList);
        recyclerView.setAdapter(adapter);
        if (Test.getInstance().isFail == 0)
            changeData();
    }
}
