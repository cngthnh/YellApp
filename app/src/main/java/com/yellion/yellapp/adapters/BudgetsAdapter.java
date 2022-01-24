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
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

import com.chauthai.swipereveallayout.SwipeRevealLayout;
import com.chauthai.swipereveallayout.ViewBinderHelper;
import com.squareup.moshi.Moshi;
import com.yellion.yellapp.BudgetsFragment;
import com.yellion.yellapp.R;
import com.yellion.yellapp.models.BudgetCard;
import com.yellion.yellapp.models.InfoMessage;
import com.yellion.yellapp.utils.ApiService;
import com.yellion.yellapp.utils.Client;
import com.yellion.yellapp.utils.SessionManager;

import java.util.List;

import okhttp3.MediaType;
import okhttp3.RequestBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class BudgetsAdapter extends RecyclerView.Adapter<BudgetsAdapter.BudgetsViewHolder>{

    private Context mContext = null;
    private List<BudgetCard> mListBudget;
    private ViewBinderHelper viewBinderHelper = new ViewBinderHelper();
    SessionManager sessionManager;
    ApiService service;
    Moshi moshi = new Moshi.Builder().build();

    public BudgetsAdapter(Context mContext, SessionManager sessionManager) {
        this.mContext = mContext;
        this.sessionManager = sessionManager;
    }

    public void setData(List<BudgetCard> mListBudget) {
        this.mListBudget = mListBudget;
    }

    @NonNull
    @Override
    public BudgetsViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_budget, parent, false);
        return new BudgetsViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull BudgetsViewHolder holder, int position) {
        BudgetCard budgetCard = mListBudget.get(position);
        if(budgetCard == null){
            return;
        }
        holder.budget_id.setText(budgetCard.name);
        holder.threshold.setText(budgetCard.threshold.toString());
        viewBinderHelper.bind(holder.swipeRevealLayout, String.valueOf(1));
        holder.deleteLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                openDialogDeleteBudget(holder, budgetCard);
            }
        });

        holder.itemLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                AppCompatActivity activity = (AppCompatActivity) view.getContext();
                BudgetsFragment budgetsFragment = new BudgetsFragment(budgetCard, sessionManager);
                activity.getSupportFragmentManager().beginTransaction().replace(R.id.list_budgets,budgetsFragment).addToBackStack(null).commit();
            }
        });



    }

    private void openDialogDeleteBudget(BudgetsViewHolder holder, BudgetCard budgetCard) {
        final Dialog dialog = new Dialog(mContext);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setContentView(R.layout.dialog_delete_budget);

        Window window = dialog.getWindow();
        if(window == null){
            return;
        }

        window.setLayout(WindowManager.LayoutParams.MATCH_PARENT, WindowManager.LayoutParams.WRAP_CONTENT);
        window.setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));

        WindowManager.LayoutParams windowAttributes = window.getAttributes();
        windowAttributes.gravity = Gravity.CENTER;
        window.setAttributes(windowAttributes);

        dialog.setCancelable(true);

        TextView title = dialog.findViewById(R.id.title_delete_bg);
        TextView deleteBt = dialog.findViewById(R.id.delete_bg);
        TextView cancelDeleteBt = dialog.findViewById(R.id.cancel_delete_bg);


        String elementS = "Bạn có chắc là muốn xoá sổ tay ";
        String s = elementS + budgetCard.getName() + " không?";

        Spannable spannable = new SpannableString(s);
        spannable.setSpan(new ForegroundColorSpan(Color.rgb(255,152,0)), elementS.length(), elementS.length() + budgetCard.getName().length(),Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);

        title.setText(spannable);

        deleteBt.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                deleteBudgetFromServer(budgetCard);
                mListBudget.remove(holder.getAdapterPosition());
                notifyItemRemoved(holder.getAdapterPosition());
                dialog.dismiss();
            }
        });

        cancelDeleteBt.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dialog.dismiss();
            }
        });

        dialog.show();
    }

    private void deleteBudgetFromServer(BudgetCard budgetCard) {
        service = Client.createServiceWithAuth(ApiService.class, sessionManager);
        Call<InfoMessage> call;

        String json = moshi.adapter(BudgetCard.class).toJson(budgetCard);
        RequestBody requestBody = RequestBody.create(MediaType.parse("text/plain"), json);

        call = service.deleteBudgets(requestBody);

        call.enqueue(new Callback<InfoMessage>() {
            @Override
            public void onResponse(Call<InfoMessage> call, Response<InfoMessage> response) {
                Log.w("YellDeleteBudget", "onResponse: " + response);
            }

            @Override
            public void onFailure(Call<InfoMessage> call, Throwable t) {
                Log.w("YellDeleteBudget", "onFailure: " + t.getMessage() );
            }
        });
    }

    @Override
    public int getItemCount() {
        if(mListBudget != null){
            return mListBudget.size();
        }
        return 0;
    }

    public class BudgetsViewHolder extends RecyclerView.ViewHolder{
        private TextView budget_id;
        private TextView threshold;
        private SwipeRevealLayout swipeRevealLayout;
        private CardView deleteLayout;
        private CardView itemLayout;

        public BudgetsViewHolder(@NonNull View itemView) {
            super(itemView);
            budget_id = itemView.findViewById((R.id.name_bg_item));
            swipeRevealLayout = itemView.findViewById(R.id.swipe);
            threshold=itemView.findViewById(R.id.threshold);
            deleteLayout = itemView.findViewById(R.id.delete_item_budget);
            itemLayout = itemView.findViewById(R.id.item_layout);
        }
    }
}
