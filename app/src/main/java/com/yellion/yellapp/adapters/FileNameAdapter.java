package com.yellion.yellapp.adapters;

import android.app.Activity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.appcompat.widget.AppCompatImageButton;
import androidx.appcompat.widget.AppCompatTextView;
import androidx.recyclerview.widget.RecyclerView;

import com.yellion.yellapp.R;

import org.checkerframework.checker.units.qual.A;

import java.util.ArrayList;

public class FileNameAdapter extends RecyclerView.Adapter<FileNameAdapter.FileNameViewHolder>{

    ArrayList<String> fileNameList;
    Activity activity;

    public FileNameAdapter(Activity activity) {
        this.activity = activity;
        fileNameList = new ArrayList<>();
    }

    public String getSizeFileNameList () {
        return String.valueOf(fileNameList.size()) + " tệp";
    }

    public void addFileName(String filename) {
        fileNameList.add(filename);
        notifyItemInserted(fileNameList.size()-1);
    }

    public void deleteFile(int position) {
        fileNameList.remove(position);
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
