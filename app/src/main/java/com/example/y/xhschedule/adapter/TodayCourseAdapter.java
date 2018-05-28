package com.example.y.xhschedule.adapter;

import android.annotation.SuppressLint;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.example.y.xhschedule.R;
import com.example.y.xhschedule.beans.Course;

import java.util.List;

public class TodayCourseAdapter extends RecyclerView.Adapter<TodayCourseAdapter.ViewHolder> implements View.OnClickListener {
    private List<Course> courseList;
    private OnItemClickListener onItemClickListener;

    public TodayCourseAdapter(List<Course> courseList) {
        this.courseList = courseList;
    }

    public void setOnItemClickListener(OnItemClickListener onItemClickListener) {
        this.onItemClickListener = onItemClickListener;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.today_course_item, parent, false);
        view.setOnClickListener(this);
        return new ViewHolder(view);
    }

    @SuppressLint("SetTextI18n")
    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        Course course = courseList.get(position);
        holder.courseName.setText(course.getName());
        holder.courseTime.setText(course.getTime()[0] + "," + course.getTime()[1]);
        holder.courseLocation.setText(course.getLocation());
        holder.itemView.setTag(position);
    }

    @Override
    public int getItemCount() {
        return courseList.size();
    }

    @Override
    public void onClick(View view) {
        if (onItemClickListener != null)
            onItemClickListener.onItemClick(view, (int) view.getTag());
    }

    class ViewHolder extends RecyclerView.ViewHolder {
        TextView courseName, courseTime, courseLocation;

        ViewHolder(View itemView) {
            super(itemView);
            courseName = itemView.findViewById(R.id.today_course_name);
            courseTime = itemView.findViewById(R.id.today_course_time);
            courseLocation = itemView.findViewById(R.id.today_course_location);
        }
    }

    public interface OnItemClickListener {
        void onItemClick(View v, int tag);
    }

}
