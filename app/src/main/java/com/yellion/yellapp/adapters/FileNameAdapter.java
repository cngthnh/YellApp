package com.yellion.yellapp.adapters;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.appcompat.widget.AppCompatImageButton;
import androidx.appcompat.widget.AppCompatTextView;
import androidx.lifecycle.MutableLiveData;
import androidx.recyclerview.widget.RecyclerView;

import com.yellion.yellapp.R;

import org.checkerframework.checker.units.qual.A;

import java.util.ArrayList;

public class FileNameAdapter extends RecyclerView.Adapter<FileNameAdapter.FileNameViewHolder>{

    ArrayList<String> fileNameList;
    Activity activity;
    MutableLiveData<Integer> countFileItem;

    public FileNameAdapter(Activity activity) {
        this.activity = activity;
        fileNameList = new ArrayList<>();
        countFileItem = new MutableLiveData<>();
        countFileItem.postValue(0);
    }

    public MutableLiveData<Integer> getSizeFileNameList () {
        return countFileItem;
    }

    public void addFileName(String filename) {
        fileNameList.add(filename);
        countFileItem.postValue(getItemCount());
        notifyItemInserted(fileNameList.size()-1);
    }

    public void removeFile(int position) {
        fileNameList.remove(position);
        countFileItem.postValue(getItemCount());
        notifyItemRemoved(position);
    }

    @NonNull
    @Override
    public FileNameViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_file,parent,false);
        return new FileNameViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull FileNameViewHolder holder, int position) {
        String fileName = fileNameList.get(position);
        if (fileName == null)
            return;
        holder.filename.setText(fileName);
        holder.deleteButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                removeFile(holder.getLayoutPosition());
            }
        });
    }

    @Override
    public int getItemCount() {
        if (fileNameList != null)
            return fileNameList.size();
        return 0;
    }

    public class FileNameViewHolder extends RecyclerView.ViewHolder {
        AppCompatTextView filename;
        AppCompatImageButton deleteButton;
        public FileNameViewHolder(@NonNull View itemView) {
            super(itemView);
            filename = itemView.findViewById(R.id.fileNameTextView);
            deleteButton = itemView.findViewById(R.id.deleteFileButton);
        }
    }
}