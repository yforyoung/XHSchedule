package com.example.y.xhschedule;

import android.graphics.Color;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.example.y.xhschedule.gson.Courses;

import java.util.List;



public class ScheduleAdapter extends RecyclerView.Adapter<ScheduleAdapter.ViewHolder> implements View.OnClickListener{
    private List<Courses> coursesList;
    private int week;

    private OnItemClickListener mOnItemClickListener=null;

    static class ViewHolder extends RecyclerView.ViewHolder{

        TextView sName;
        TextView sLocation;
        public ViewHolder(View itemView) {
            super(itemView);
            sName= (TextView) itemView.findViewById(R.id.s_name);
            sLocation= (TextView) itemView.findViewById(R.id.s_location);
        }
    }

    public static interface OnItemClickListener{
        void onItemClick(View view,int position);
    }



    public ScheduleAdapter(List<Courses> list,int week){
        coursesList =list;
        this.week=week;
    }

    public void setWeek(int number)
    {
        this.week=number;
        notifyDataSetChanged();
    }



    @Override
    public void onClick(View view){
        if (mOnItemClickListener!=null){
            mOnItemClickListener.onItemClick(view, (int)view.getTag());
        }
    }

    public void setmOnItemClickListener(OnItemClickListener listener){
        this.mOnItemClickListener=listener;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view= LayoutInflater.from(parent.getContext()).inflate(R.layout.recycler_view_item,parent,false);
        ViewHolder holder=new ViewHolder(view);
        view.setOnClickListener(this);
        return holder;
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        Courses courses = coursesList.get(position);
        holder.sName.setText(courses.getName());
        holder.sLocation.setText(courses.getLocation());
        holder.itemView.setTag(position);

        if (courses.getWeekStart()<=week&&week<=courses.getWeekEnd()){
            holder.itemView.setBackgroundResource(R.drawable.bg_course);
        }else
        {
            holder.itemView.setBackgroundResource(R.drawable.diliver);
        }
    }

    @Override
    public int getItemCount() {
        return coursesList.size();
    }
}
