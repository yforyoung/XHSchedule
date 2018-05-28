package com.example.y.xhschedule.fragment;

import android.annotation.SuppressLint;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.StaggeredGridLayoutManager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.example.y.xhschedule.CoursePage;
import com.example.y.xhschedule.R;
import com.example.y.xhschedule.adapter.ScheduleAdapter;
import com.example.y.xhschedule.Util.Test;
import com.example.y.xhschedule.Util.Util;
import com.example.y.xhschedule.beans.Course;
import com.example.y.xhschedule.beans.EventBean;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;

import static org.greenrobot.eventbus.EventBus.TAG;

public class WeekCourseFragment extends Fragment {
    private List<Course> courseList = new ArrayList<>();
    private HashMap<Integer, ArrayList<Course>> coursesHashMap;
    private Util util = new Util();
    private ScheduleAdapter adapter;
    private int weekNow = 0;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.week_course_fragment, container, false);
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EventBus.getDefault().register(this);
    }


    @Subscribe
    public void onEvent(EventBean event) {
        Log.i(TAG, "onEvent: ");
        weekNow=event.getMgs();
        initSchedule();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        EventBus.getDefault().unregister(this);
    }

    @SuppressLint("UseSparseArrays")
    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        weekNow = Test.getInstance().weekNow;
        RecyclerView recyclerView = getActivity().findViewById(R.id.schedule_recycler_view);
        StaggeredGridLayoutManager layoutManager = new StaggeredGridLayoutManager(7, StaggeredGridLayoutManager.VERTICAL);
        recyclerView.setLayoutManager(layoutManager);
        adapter = new ScheduleAdapter(courseList, weekNow);
        recyclerView.setAdapter(adapter);
        adapter.setmOnItemClickListener(new ScheduleAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(View view, final int position) {
                showCourse(position);
            }
        });

        TextView sun = getActivity().findViewById(R.id.sunday);
        TextView mon = getActivity().findViewById(R.id.monday);
        TextView tues = getActivity().findViewById(R.id.tuesday);
        TextView wed = getActivity().findViewById(R.id.wednesday);
        TextView thur = getActivity().findViewById(R.id.thursday);
        TextView fri = getActivity().findViewById(R.id.friday);
        TextView satu = getActivity().findViewById(R.id.saturday);

        coursesHashMap = new HashMap<>();

        Calendar calendar = Calendar.getInstance();
        int today = calendar.get(Calendar.DAY_OF_WEEK);
        switch (today) {
            case 1:
                changeBackground(sun);
                break;
            case 2:
                changeBackground(mon);
                break;
            case 3:
                changeBackground(tues);
                break;
            case 4:
                changeBackground(wed);
                break;
            case 5:
                changeBackground(thur);
                break;
            case 6:
                changeBackground(fri);
                break;
            case 7:
                changeBackground(satu);
                break;

        }
        initSchedule();
    }

    private void showCourse(int position) {

        final Course temp = courseList.get(position);
        final ArrayList<Course> list = coursesHashMap.get(position);

        if (temp!=null) {
            if (coursesHashMap.get(position) == null) {
                Intent intent = new Intent(getActivity(), CoursePage.class);
                intent.putExtra("course", temp);
                startActivity(intent);
            } else {
                final int size = list.size();
                String[] cont = new String[list.size() + 1];
                if (temp.getWeekStart() <= weekNow && weekNow <= temp.getWeekEnd()) {
                    String zhou;
                    zhou = temp.getZhou();
                    if (zhou.equals(""))
                        cont[0] = temp.getName() + "（今日有课）";
                    else if (zhou.contains("双周") && weekNow % 2 == 0)
                        cont[0] = temp.getName() + "（今日有课）";
                    else if (zhou.contains("单周") && (weekNow + 1) % 2 == 0) {
                        cont[0] = temp.getName() + "（今日有课）";
                    } else
                        cont[0] = temp.getName();
                } else {
                    cont[0] = temp.getName();
                }

                int i = 0;
                for (Course c : list) {
                    if (c.getWeekStart() <= weekNow && weekNow <= c.getWeekEnd()) {
                        String zhou;
                        zhou = c.getZhou();
                        if (zhou.equals(""))
                            cont[++i] = c.getName() + "（今日有课）";
                        else if (zhou.contains("双周") && weekNow % 2 == 0)
                            cont[++i] = c.getName() + "（今日有课）";
                        else if (zhou.contains("单周") && (weekNow + 1) % 2 == 0) {
                            cont[++i] = c.getName() + "（今日有课）";
                        } else
                            cont[++i] = c.getName();
                    } else {
                        cont[++i] = c.getName();
                    }

                }

                AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
                builder.setItems(cont, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        Intent intent = new Intent(getActivity(), CoursePage.class);
                        switch (which) {
                            case 0:
                                intent.putExtra("course", temp);
                                break;
                            case 1:
                                intent.putExtra("course", list.get(0));
                                break;
                            case 2:
                                intent.putExtra("course", list.get(1));
                                break;
                            case 3:
                                intent.putExtra("course", list.get(2));
                                break;
                            default:
                                intent.putExtra("course", list.get(size - 1));
                                break;
                        }
                        startActivity(intent);
                    }
                });
                builder.show();
            }
        }

    }

    private void changeBackground(View view) {
        view.setBackgroundColor(Color.parseColor("#9BF2AAAA"));
    }


    public void initSchedule() {
        adapter.setWeek(weekNow);

        if (!coursesHashMap.isEmpty())
            coursesHashMap.clear();

        courseList.clear();

        courseList.addAll(util.loadData(Test.getInstance().weekNow, coursesHashMap));

        adapter.notifyDataSetChanged();

    }
}
