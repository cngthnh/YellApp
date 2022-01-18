package com.yellion.yellapp.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.yellion.yellapp.R;
import com.yellion.yellapp.models.DashboardCard;

import java.util.List;

public class UsersAdapter extends RecyclerView.Adapter<UsersAdapter.UsersViewHolder>{

    private Context mContext = null;
    private List<String> mListUserName;

    public UsersAdapter(Context mContext) {
        this.mContext = mContext;
    }

    public void setData(List<String> mListUserName) {
        this.mListUserName = mListUserName;
    }

    @NonNull
    @Override
    public UsersViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_user, parent, false);
        return new UsersAdapter.UsersViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull UsersViewHolder holder, int position) {
        String userName = mListUserName.get(position);
        if(userName == null){
            return;
        }
        holder.userName.setText(userName);
    }

    @Override
    public int getItemCount() {
        if(mListUserName != null){
            return mListUserName.size();
        }
        return 0;
    }

    public class UsersViewHolder extends RecyclerView.ViewHolder{

        TextView userName;
        public UsersViewHolder(@NonNull View itemView) {
            super(itemView);
            userName = itemView.findViewById(R.id.name_user);
        }
    }
}
