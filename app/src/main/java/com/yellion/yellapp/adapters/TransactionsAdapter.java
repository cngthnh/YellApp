package com.yellion.yellapp.adapters;

import android.annotation.SuppressLint;
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
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

import com.chauthai.swipereveallayout.SwipeRevealLayout;
import com.chauthai.swipereveallayout.ViewBinderHelper;
import com.squareup.moshi.Moshi;
import com.yellion.yellapp.R;
import com.yellion.yellapp.models.InfoMessage;
import com.yellion.yellapp.models.TransactionCard;
import com.yellion.yellapp.utils.ApiService;
import com.yellion.yellapp.utils.Client;
import com.yellion.yellapp.utils.SessionManager;

import java.util.List;

import okhttp3.MediaType;
import okhttp3.RequestBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class TransactionsAdapter extends RecyclerView.Adapter<TransactionsAdapter.TransactionsViewHolder>{

    private Context mContext = null;
    private List<TransactionCard> mListTransaction;
    private ViewBinderHelper viewBinderHelper = new ViewBinderHelper();
    SessionManager sessionManager;
    ApiService service;
    Moshi moshi = new Moshi.Builder().build();
    public TransactionsAdapter(Context mContext) {
        this.mContext = mContext;
    }

    public void setData(List<TransactionCard> mListTransaction) {
        this.mListTransaction = mListTransaction;
    }

    @NonNull
    @Override
    public TransactionsViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_transition, parent, false);
        return new TransactionsViewHolder(view);
    }

    @SuppressLint("SetTextI18n")
    @Override
    public void onBindViewHolder(@NonNull TransactionsViewHolder holder, int position) {
        TransactionCard transactionCard = mListTransaction.get(position);
        if(transactionCard == null){
            return;
        }
        holder.nameTransaction.setText(transactionCard.budget_id);
        holder.amount.setText(String.format("%d",transactionCard.amount));
        holder.purpose.setText(transactionCard.purpose);
        holder.time.setText(transactionCard.time);

        viewBinderHelper.bind(holder.swipeRevealLayout, String.valueOf(1));
        holder.deleteLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                openDialogDeleteTransaction(holder, transactionCard);
            }
        });

    }

    private void openDialogDeleteTransaction(TransactionsViewHolder holder, TransactionCard transactionCard) {
        final Dialog dialog = new Dialog(mContext);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setContentView(R.layout.dialog_delete_transaction);

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

        TextView title = dialog.findViewById(R.id.title_delete_ts);
        TextView deleteTs = dialog.findViewById(R.id.delete_ts);
        TextView cancelDeleteTs = dialog.findViewById(R.id.cancel_delete_ts);


        String elementS = "Bạn có chắc là muốn xoá giao dịch";
        String s = elementS + transactionCard.getBudget_id() + " không?";

        Spannable spannable = new SpannableString(s);
        spannable.setSpan(new ForegroundColorSpan(Color.rgb(255,152,0)), elementS.length(), elementS.length() + transactionCard.getBudget_id().length(),Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);

        title.setText(spannable);

        deleteTs.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                deleteTransactionFromServer(transactionCard);
                mListTransaction.remove(holder.getAdapterPosition());
                notifyItemRemoved(holder.getAdapterPosition());
                dialog.dismiss();
            }
        });

        cancelDeleteTs.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dialog.dismiss();
            }
        });

        dialog.show();
    }

    private void deleteTransactionFromServer(TransactionCard transactionCard) {
        service = Client.createServiceWithAuth(ApiService.class, sessionManager);
        Call<InfoMessage> call;

        String json = moshi.adapter(TransactionCard.class).toJson(transactionCard);
        RequestBody requestBody = RequestBody.create(MediaType.parse("text/plain"), json);

        call = service.deleteTransaction(requestBody);
        call.enqueue(new Callback<InfoMessage>() {
            @Override
            public void onResponse(Call<InfoMessage> call, Response<InfoMessage> response) {
                Log.w("YellDeleteTransaction", "onResponse: " + response);
            }

            @Override
            public void onFailure(Call<InfoMessage> call, Throwable t) {
                Log.w("YellDeleteTransaction", "onFailure: " + t.getMessage() );
            }
        });
    }

    @Override
    public int getItemCount() {
        if(mListTransaction != null){
            return mListTransaction.size();
        }
        return 0;
    }

    public class TransactionsViewHolder extends RecyclerView.ViewHolder{
        private TextView nameTransaction;
        private TextView amount;
        private TextView time;
        private TextView purpose;
        private SwipeRevealLayout swipeRevealLayout;
        private CardView deleteLayout;
        private CardView itemLayout;

        public TransactionsViewHolder(@NonNull View itemView) {
            super(itemView);
            nameTransaction = itemView.findViewById((R.id.name_ts_item));
            amount = itemView.findViewById(R.id.amount_ts);
            swipeRevealLayout = itemView.findViewById(R.id.swipe_ts);
            deleteLayout = itemView.findViewById(R.id.delete_item_transaction);
            itemLayout = itemView.findViewById(R.id.item_layout);
            time=itemView.findViewById(R.id.time_ts);
            purpose=itemView.findViewById(R.id.purpose_ts);
        }
    }
}