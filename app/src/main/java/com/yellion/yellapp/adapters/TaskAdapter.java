package com.yellion.yellapp.adapters;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatEditText;
import androidx.appcompat.widget.AppCompatTextView;
import androidx.cardview.widget.CardView;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;
import androidx.recyclerview.widget.RecyclerView;

import com.yellion.yellapp.LoadingDialog;
import com.yellion.yellapp.R;
import com.yellion.yellapp.TaskFragment;
import com.yellion.yellapp.models.YellTask;

import java.util.ArrayList;

public class TaskAdapter extends RecyclerView.Adapter<TaskAdapter.TaskViewHolder> {

    ArrayList<YellTask> yellTaskArrayList;
    FragmentActivity activity;
    String parentName;

    public TaskAdapter(FragmentActivity activity) {
        this.activity = activity;
        yellTaskArrayList = new ArrayList<>();
    }

    public void setParentName(String parentName) {
        this.parentName = parentName;
    }

    public void addYellTask(YellTask yellTask) {
        yellTaskArrayList.add(yellTask);
        notifyItemInserted(yellTaskArrayList.size()-1);
    }

    public void removeYellTask(int position) {
        yellTaskArrayList.remove(position);
        notifyItemRemoved(position);
    }

    @NonNull
    @Override
    public TaskViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_task,parent,false);
        return new TaskViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull TaskViewHolder holder, int position) {
        YellTask yellTask = yellTaskArrayList.get(position);
        String taskName = yellTask.getName();
        int status = yellTask.getStatus();
        if (yellTask == null)
            return;
        holder.taskName.setText(taskName);
        if (status == 1)
        {
            holder.taskLabel.setText("Đã hoàn thành");
            holder.taskLabel.setBackgroundResource(R.drawable.frame_cover_item_task_green);
        }
        else
        {
            holder.taskLabel.setText("Chưa hoàn thành");
            holder.taskLabel.setBackgroundResource(R.drawable.frame_cover_item_task_yellow);
        }
        holder.deleteTaskItem.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                removeYellTask(holder.getLayoutPosition());
            }
        });
        holder.taskName.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                TaskFragment fragment = TaskFragment.newInstance(yellTask.getName(),
                        yellTask.getDashboard_id(), yellTask.getTask_id(), parentName);
                activity.getSupportFragmentManager().beginTransaction()
                        .setCustomAnimations(android.R.anim.slide_in_left, android.R.anim.slide_out_right,
                                android.R.anim.slide_in_left, android.R.anim.cycle_interpolator)
                        .replace(R.id.fragmentContainer, fragment)
                        .addToBackStack(null)
                        .commit();
            }
        });
    }

    @Override
    public int getItemCount() {
        if (yellTaskArrayList != null)
            return yellTaskArrayList.size();
        return 0;
    }

    public class TaskViewHolder extends RecyclerView.ViewHolder {
        AppCompatTextView taskName;
        AppCompatTextView taskLabel;
        CardView deleteTaskItem;
        public TaskViewHolder(@NonNull View itemView) {
            super(itemView);
            taskName = itemView.findViewById(R.id.taskNameItem);
            taskLabel = itemView.findViewById(R.id.taskLabelItem);
            deleteTaskItem = itemView.findViewById(R.id.deleteTaskItem);
        }
    }
}
