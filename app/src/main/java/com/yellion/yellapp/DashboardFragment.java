package com.yellion.yellapp;

import static android.content.ContentValues.TAG;

import android.app.Dialog;
import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.text.Spannable;
import android.text.SpannableString;
import android.text.SpannableStringBuilder;
import android.text.method.LinkMovementMethod;
import android.text.style.ForegroundColorSpan;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;
import android.view.Window;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.widget.TextView;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.yellion.yellapp.adapters.UsersAdapter;
import com.yellion.yellapp.databinding.FragmentDashboardBinding;
import com.yellion.yellapp.models.DashboardCard;
import com.yellion.yellapp.utils.MySpannable;

import java.util.ArrayList;
import java.util.List;

public class DashboardFragment extends Fragment {
    FragmentDashboardBinding binding;
    DashboardCard dashboardCard;
    UsersAdapter usersAdapter = null;
    List<String> usernames;


    public DashboardFragment(DashboardCard dashboardCard) {
        this.dashboardCard = dashboardCard;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        binding = FragmentDashboardBinding.inflate(inflater, container, false );
        View view = binding.getRoot();
        binding.edtNameDb.setText(dashboardCard.getName());

        binding.backDashboard.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(getFragmentManager() != null){
                    getFragmentManager().popBackStack();
                }
            }
        });

        binding.deleteInDb.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                openDialogDeleteDashboard(dashboardCard);
            }
        });

        binding.editDb.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                binding.edtNameDb.setFocusableInTouchMode(true);
                binding.edtNameDb.setFocusable(true);

                binding.editDb.setVisibility(View.GONE);
                binding.deleteInDb.setVisibility(View.GONE);
                binding.backDashboard.setVisibility(View.GONE);
                binding.tvDescriptionDb.setVisibility(View.GONE);
                binding.completeEditDb.setVisibility(View.VISIBLE);
                binding.cancelEditDb.setVisibility(View.VISIBLE);
                binding.edtDescriptionDb.setVisibility(View.VISIBLE);

            }
        });

        binding.cancelEditDb.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                binding.edtNameDb.setText(dashboardCard.getName());
                binding.completeEditDb.setVisibility(View.GONE);
                binding.cancelEditDb.setVisibility(View.GONE);
                binding.edtDescriptionDb.setVisibility(View.GONE);
                binding.editDb.setVisibility(View.VISIBLE);
                binding.deleteInDb.setVisibility(View.VISIBLE);
                binding.backDashboard.setVisibility(View.VISIBLE);
                binding.tvDescriptionDb.setVisibility(View.VISIBLE);
                String dsc = binding.tvDescriptionDb.getTag().toString();
                binding.edtDescriptionDb.setText(dsc);
                try {
                    InputMethodManager inputManager = (InputMethodManager)getActivity().getSystemService(Context.INPUT_METHOD_SERVICE);
                    inputManager.hideSoftInputFromWindow(getActivity().getCurrentFocus().getWindowToken(),InputMethodManager.HIDE_NOT_ALWAYS);
                }
                catch (Exception ex){
                    Log.e(TAG, ex.toString());
                }

                binding.edtNameDb.setFocusable(false);
            }
        });

        binding.completeEditDb.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                binding.completeEditDb.setVisibility(View.GONE);
                binding.cancelEditDb.setVisibility(View.GONE);
                binding.edtDescriptionDb.setVisibility(View.GONE);
                binding.editDb.setVisibility(View.VISIBLE);
                binding.deleteInDb.setVisibility(View.VISIBLE);
                binding.backDashboard.setVisibility(View.VISIBLE);
                binding.tvDescriptionDb.setVisibility(View.VISIBLE);
                String dsc = binding.edtDescriptionDb.getText().toString();
                binding.tvDescriptionDb.setText(dsc);
                binding.tvDescriptionDb.setTag(null);
                makeTextViewResizable(binding.tvDescriptionDb, 3, "...Xem thêm", true);
                try {
                    InputMethodManager inputManager = (InputMethodManager)getActivity().getSystemService(Context.INPUT_METHOD_SERVICE);
                    inputManager.hideSoftInputFromWindow(getActivity().getCurrentFocus().getWindowToken(),InputMethodManager.HIDE_NOT_ALWAYS);
                }
                catch (Exception ex){
                    Log.e(TAG, ex.toString());
                }
                binding.edtNameDb.setFocusable(false);

            }
        });
        makeTextViewResizable(binding.tvDescriptionDb, 3, "...Xem thêm", true);

        LinearLayoutManager layoutManager = new LinearLayoutManager(getActivity(),  LinearLayoutManager.HORIZONTAL, false);
        binding.listUsers.setLayoutManager(layoutManager);
        usersAdapter = new UsersAdapter(getContext());
        usernames = new ArrayList<>();
        getListUserNamesFromServer();
        usersAdapter.setData(usernames);
        usersAdapter.notifyDataSetChanged();
        binding.listUsers.setVisibility(View.VISIBLE);
        binding.listUsers.setAdapter(usersAdapter);



        return view;
    }

    private void getListUserNamesFromServer() {
        usernames.add("BC");
        usernames.add("TH");
        usernames.add("TT");
    }

    private void openDialogDeleteDashboard(DashboardCard dashboardCard) {
        final Dialog dialog = new Dialog(getContext());
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setContentView(R.layout.dialog_delete_dashboard);

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

        TextView title = dialog.findViewById(R.id.title_delete_db);
        TextView deleteBt = dialog.findViewById(R.id.delete_db);
        TextView cancelDeleteBt = dialog.findViewById(R.id.cancel_delete_db);


        String elementS = "Bạn có chắc là muốn xoá bảng ";
        String s = elementS + dashboardCard.getName() + " không?";

        Spannable spannable = new SpannableString(s);
        spannable.setSpan(new ForegroundColorSpan(Color.rgb(255,152,0)), elementS.length(), elementS.length() + dashboardCard.getName().length(),Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);

        title.setText(spannable);

        deleteBt.setOnClickListener(view -> {
            if(getFragmentManager() != null){
                getFragmentManager().popBackStack();
            }
            dialog.dismiss();
        });

        cancelDeleteBt.setOnClickListener(view -> dialog.dismiss());

        dialog.show();
    }

    public static void makeTextViewResizable(final TextView tv, final int maxLine, final String expandText, final boolean viewMore) {

        if (tv.getTag() == null) {
            tv.setTag(tv.getText());
        }
        ViewTreeObserver vto = tv.getViewTreeObserver();
        vto.addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {

            //@SuppressWarnings("deprecation")
            @Override
            public void onGlobalLayout() {
                String text;
                int lineEndIndex;
                ViewTreeObserver obs = tv.getViewTreeObserver();
                obs.removeOnGlobalLayoutListener(this);

                if (maxLine == 0) {
                    lineEndIndex = tv.getLayout().getLineEnd(0);
                    text = tv.getText().subSequence(0, lineEndIndex - expandText.length() + 1) + " " + expandText;
                } else if (maxLine > 0 && tv.getLineCount() >= maxLine) {
                    lineEndIndex = tv.getLayout().getLineEnd(maxLine - 1);
                    text = tv.getText().subSequence(0, lineEndIndex - expandText.length() + 1) + " " + expandText;
                } else {
                    lineEndIndex = tv.getLayout().getLineEnd(tv.getLayout().getLineCount() - 1);
                    text = tv.getText().subSequence(0, lineEndIndex) + " " + expandText;
                }
                
                if(tv.getLineCount() > 3){
                    tv.setText(text);
                    tv.setMovementMethod(LinkMovementMethod.getInstance());

                    tv.setText(addClickablePartTextViewResizable(new SpannableString(tv.getText().toString()), tv, lineEndIndex, expandText,
                            viewMore), TextView.BufferType.SPANNABLE);
                }
            }
        });
    }

    private static SpannableStringBuilder addClickablePartTextViewResizable(SpannableString strSpanned, TextView tv,
                                                                            final int maxLine, final String spanableText, final boolean viewMore) {
        String str = strSpanned.toString();
        SpannableStringBuilder ssb = new SpannableStringBuilder(strSpanned);
        if (str.contains(spanableText)) {
            ssb.setSpan(new MySpannable(false) {

                @Override
                public void onClick(View widget) {
                    tv.setLayoutParams(tv.getLayoutParams());
                    tv.setText(tv.getTag().toString(), TextView.BufferType.SPANNABLE);
                    tv.invalidate();
                    if (viewMore) {
                        makeTextViewResizable(tv, -1, "...Thu gọn", false);
                    } else {
                        makeTextViewResizable(tv, 3, "...Xem thêm", true);
                    }

                }
            }, str.indexOf(spanableText), str.indexOf(spanableText) + spanableText.length(), 0);

        }
        return ssb;
    }
}