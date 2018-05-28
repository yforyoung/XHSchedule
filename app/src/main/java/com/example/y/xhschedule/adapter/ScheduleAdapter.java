package com.example.y.xhschedule.adapter;

import android.graphics.Color;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.example.y.xhschedule.R;
import com.example.y.xhschedule.beans.Course;
import java.util.ArrayList;
import java.util.List;


public class ScheduleAdapter extends RecyclerView.Adapter<ScheduleAdapter.ViewHolder> implements View.OnClickListener {
    private List<Course> courseList;
    private int week;

    private List<String> name = new ArrayList<>();

    private OnItemClickListener mOnItemClickListener = null;

    static class ViewHolder extends RecyclerView.ViewHolder {

        TextView sName;
        TextView sLocation;

        ViewHolder(View itemView) {
            super(itemView);
            sName = itemView.findViewById(R.id.s_name);
            sLocation = itemView.findViewById(R.id.s_location);
        }
    }

    public interface OnItemClickListener {
        void onItemClick(View view, int position);
    }


    public ScheduleAdapter(List<Course> list, int week) {
        courseList = list;
        this.week = week;
    }



    public void setWeek(int number) {
        this.week = number;
        notifyDataSetChanged();
    }


    @Override
    public void onClick(View view) {
        if (mOnItemClickListener != null) {
            mOnItemClickListener.onItemClick(view, (int) view.getTag());
        }
    }

    public void setmOnItemClickListener(OnItemClickListener listener) {
        this.mOnItemClickListener = listener;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.recycler_view_item, parent, false);
        ViewHolder holder = new ViewHolder(view);
        view.setOnClickListener(this);
        return holder;
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        Course course = courseList.get(position);

        holder.sName.setText(course.getName());
        holder.sLocation.setText(course.getLocation());
        holder.itemView.setTag(position);

        if (course.getWeekStart() <= week && week <= course.getWeekEnd()) {
            String zhou = course.getZhou();
            if (!name.contains(course.getName())) {
                name.add(course.getName());
            }
            if (zhou.equals(""))
                randomBackground(holder.itemView,name.indexOf(course.getName())%4);
            else if (zhou.contains("双周") && week % 2 == 0)
                randomBackground(holder.itemView,name.indexOf(course.getName())%4);
            else if (zhou.contains("单周") && (week + 1) % 2 == 0)
                randomBackground(holder.itemView,name.indexOf(course.getName())%4);
            else
                holder.itemView.setBackgroundResource(R.drawable.bg_course_single);
        } else {
            holder.itemView.setBackgroundColor(Color.parseColor("#00000000"));
        }
    }

    private void randomBackground(View view,int i) {
        switch (i) {
            case 1:
                view.setBackgroundResource(R.drawable.bg_course_1);
                break;
            case 2:
                view.setBackgroundResource(R.drawable.bg_course_2);
                break;
            case 0:
                view.setBackgroundResource(R.drawable.bg_course_3);
                break;
            case 3:
                view.setBackgroundResource(R.drawable.bg_course_4);
                break;

        }
    }

    @Override
    public int getItemCount() {
        return courseList.size();
    }
}
