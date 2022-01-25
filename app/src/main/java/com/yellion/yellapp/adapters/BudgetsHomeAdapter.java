package com.yellion.yellapp.adapters;

import android.app.Dialog;
import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.text.Spannable;
import android.text.SpannableString;
import android.text.style.ForegroundColorSpan;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

import com.chauthai.swipereveallayout.SwipeRevealLayout;
import com.chauthai.swipereveallayout.ViewBinderHelper;
import com.squareup.moshi.Moshi;
import com.yellion.yellapp.BudgetsFragment;
import com.yellion.yellapp.DashboardFragment;
import com.yellion.yellapp.R;
import com.yellion.yellapp.models.BudgetCard;
import com.yellion.yellapp.models.DashboardCard;
import com.yellion.yellapp.models.InfoMessage;
import com.yellion.yellapp.models.UserAccount;
import com.yellion.yellapp.utils.ApiService;
import com.yellion.yellapp.utils.Client;
import com.yellion.yellapp.utils.SessionManager;

import java.util.List;

import okhttp3.MediaType;
import okhttp3.RequestBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class BudgetsHomeAdapter extends RecyclerView.Adapter<BudgetsHomeAdapter.BudgetsHomeViewHolder>{

    private Context mContext = null;
    private List<BudgetCard> mListBudget;
    SessionManager sessionManager;

    public BudgetsHomeAdapter(Context mContext, SessionManager sessionManager) {
        this.mContext = mContext;
        this.sessionManager = sessionManager;
    }

    public void setData(List<BudgetCard> mListBudget) {
        this.mListBudget = mListBudget;
    }

    @NonNull
    @Override
    public BudgetsHomeViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_budget_home, parent, false);
        return new BudgetsHomeViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull BudgetsHomeViewHolder holder, int position) {
        BudgetCard budgetCard = mListBudget.get(position);
        if(budgetCard == null){
            return;
        }
        holder.nameBudget.setText(budgetCard.getName());
        String s = String.valueOf(budgetCard.getBalance())+" vnđ";
        holder.label.setText(s);
        holder.itemLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                AppCompatActivity activity = (AppCompatActivity) view.getContext();
                BudgetsFragment budgetFragment = new BudgetsFragment(budgetCard, sessionManager);
                activity.getSupportFragmentManager().beginTransaction()
                        .replace(R.id.fragmentContainer,budgetFragment).addToBackStack(null).commit();
            }
        });
    }

    @Override
    public int getItemCount() {
        if(mListBudget != null){
            if(mListBudget.size() > 5)
                return 5;
            return mListBudget.size();
        }
        return 0;
    }

    public class BudgetsHomeViewHolder extends RecyclerView.ViewHolder{
        private TextView nameBudget;
        private TextView label;
        private CardView itemLayout;

        public BudgetsHomeViewHolder(@NonNull View itemView) {
            super(itemView);
            nameBudget = itemView.findViewById((R.id.name_bg_home_item));
            label = itemView.findViewById(R.id.label_bg_home);
            itemLayout = itemView.findViewById(R.id.item_budget_home);
        }
    }
}
