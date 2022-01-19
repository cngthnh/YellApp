package com.yellion.yellapp;

import android.content.Context;
import android.content.DialogInterface;
import android.graphics.drawable.Drawable;
import android.icu.text.SimpleDateFormat;
import android.os.Build;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.appcompat.widget.AppCompatEditText;
import androidx.appcompat.widget.AppCompatImageButton;
import androidx.appcompat.widget.AppCompatTextView;
import androidx.fragment.app.Fragment;

import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.Switch;
import android.widget.Toast;

import com.google.android.material.bottomappbar.BottomAppBar;
import com.google.android.material.dialog.MaterialAlertDialogBuilder;
import com.yellion.yellapp.databinding.FragmentTaskBinding;
import com.yellion.yellapp.models.ErrorMessage;
import com.yellion.yellapp.models.InfoMessage;
import com.squareup.moshi.Moshi;
import com.yellion.yellapp.models.YellTask;
import com.yellion.yellapp.utils.ApiService;
import com.yellion.yellapp.utils.Client;
import com.yellion.yellapp.utils.SessionManager;

import java.text.ParseException;
import java.util.Date;

import okhttp3.MediaType;
import okhttp3.RequestBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link TaskFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class TaskFragment extends Fragment {

    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "taskId";
    private static final String ARG_PARAM2 = "dashboardId";
    SessionManager sessionManager;
    ApiService service;
    Moshi moshi = new Moshi.Builder().build();
    YellTask currentYellTask;
    private FragmentTaskBinding binding;

    // TODO: Rename and change types of parameters
    private String taskId;
    private String dashBoardId;

    public TaskFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param taskId Parameter 1.
     * @param dashBoardId Parameter 2.
     * @return A new instance of fragment TaskFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static TaskFragment newInstance(String taskId, String dashBoardId) {
        TaskFragment fragment = new TaskFragment();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, taskId);
        args.putString(ARG_PARAM2, dashBoardId);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            taskId = getArguments().getString(ARG_PARAM1);
            dashBoardId = getArguments().getString(ARG_PARAM2);
            currentYellTask = new YellTask(dashBoardId,taskId);
        }
    }




    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        binding = FragmentTaskBinding.inflate(inflater, container, false);
        View view = binding.getRoot();
        setToolbarTaskListener();
        setConfigTaskListener();
        return view;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        BottomAppBar navBar = getActivity().findViewById(R.id.mainAppBar);
        navBar.setVisibility(View.GONE);
    }

    private void setToolbarTaskListener() {
        AppCompatImageButton editNameTask = binding.editNameTask;
        AppCompatImageButton deleteTask = binding.deleteTask;
        AppCompatEditText taskName = binding.taskName;
        AppCompatImageButton taskIcon = binding.taskIcon;
        taskIcon.setClickable(false);
        taskIcon.setTag("false");
        StringBuffer currentName = new StringBuffer();
        final Drawable[] currentDrawable = new Drawable[1];
        editNameTask.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if ((String)taskIcon.getTag() == "false") {
                    taskName.setEnabled(true);
                    taskName.requestFocusFromTouch();
                    InputMethodManager imm = (InputMethodManager)
                            getContext().getSystemService(Context.INPUT_METHOD_SERVICE);
                    imm.showSoftInput(taskName, InputMethodManager.SHOW_IMPLICIT);
                    taskName.setSelection(taskName.getText().length());

                    taskIcon.setClickable(true);
                    taskIcon.setTag("true");
                    currentDrawable[0] = taskIcon.getDrawable();

                    editNameTask.setImageResource(R.drawable.ic_done);
                    editNameTask.setColorFilter(getResources().getColor(R.color.green));

                    deleteTask.setImageResource(R.drawable.ic_close);
                    deleteTask.setColorFilter(getResources().getColor(R.color.red));
                    currentName.append(taskName.getText());
                }
                else {
                    taskName.setEnabled(false);
                    currentName.delete(0,currentName.length());

                    taskIcon.setClickable(false);
                    taskIcon.setTag("false");

                    editNameTask.setImageResource(R.drawable.ic_edit_mode);
                    editNameTask.setColorFilter(null);

                    deleteTask.setImageResource(R.drawable.ic_delete);
                    deleteTask.setColorFilter(null);

                    currentYellTask.setName(taskName.getText().toString());
                }
            }
        });
        deleteTask.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if ((String)taskIcon.getTag() == "true") {
                    taskName.setEnabled(false);
                    taskName.setText(currentName);

                    taskIcon.setClickable(false);
                    taskIcon.setTag("false");
                    taskIcon.setImageDrawable(currentDrawable[0]);

                    editNameTask.setImageResource(R.drawable.ic_edit_mode);
                    editNameTask.setColorFilter(null);

                    deleteTask.setImageResource(R.drawable.ic_delete);
                    deleteTask.setColorFilter(null);

                    currentName.delete(0,currentName.length());
                }
                else {
                    MaterialAlertDialogBuilder confirmDelete = new MaterialAlertDialogBuilder(getContext())
                            .setTitle("Xoá công việc")
                            .setMessage("Bạn có chắc chắn muốn xoá công việc này?")
                            .setPositiveButton("Đồng ý", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {

                                }
                            })
                            .setNegativeButton("Huỷ", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {

                                }
                            });
                    confirmDelete.show();
                }
            }
        });
        taskIcon.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                BottomSheetIconPicker bottomSheetIconPicker = new BottomSheetIconPicker();
                bottomSheetIconPicker.show(getActivity().getSupportFragmentManager(),"Icon Picker");
            }
        });
    }

    private void setConfigTaskListener() {
        AppCompatTextView deadlineTask = binding.deadlineTask;
        deadlineTask.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                DeadlineTimeDialog deadlineTimeDialog = new DeadlineTimeDialog();
                deadlineTimeDialog.show(getActivity().getSupportFragmentManager(),
                        "DeadlineTimeDialog");
            }
        });
        deadlineTask.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @RequiresApi(api = Build.VERSION_CODES.N)
            @Override
            public void afterTextChanged(Editable s) {
                String temp = s.toString();
                SimpleDateFormat currentFormat = new SimpleDateFormat("HH:mm  dd/MM/YYYY");
                SimpleDateFormat isoFormat = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss'Z'");
                try {
                    Date date = currentFormat.parse(temp);
                    currentYellTask.setEnd_time(isoFormat.format(date));
                } catch (ParseException e) {
                    Log.e("TimeParseError","Time Parse Error");
                }
            }
        });
        AppCompatTextView priority = binding.priorityTextView;
        priority.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String [] priorities = new String[]{"Cao","Thường","Thấp"};
                MaterialAlertDialogBuilder priorityDialog = new MaterialAlertDialogBuilder(getContext())
                        .setTitle("Độ ưu tiên")
                        .setItems(priorities, new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                priority.setText(priorities[which]);
                            }
                        });
                priorityDialog.show();
            }
        });
        priority.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                String temp = s.toString();
                Integer value;
                if (temp == "Cao")
                    value = 2;
                else if (temp == "Thường")
                    value = 1;
                else
                    value = 0;
                currentYellTask.setPriority(value);
            }
        });

        AppCompatTextView status = binding.statusTextView;
        status.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String [] statuses = new String[]{"Đã hoàn thành","Chưa hoàn thành"};
                MaterialAlertDialogBuilder statusDialog = new MaterialAlertDialogBuilder(getContext())
                        .setTitle("Trạng thái")
                        .setItems(statuses, new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                status.setText(statuses[which]);
                            }
                        });
                statusDialog.show();
            }
        });
        status.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                String temp = s.toString();
                Integer value;
                if (temp == "Đã hoàn thành")
                    value = 1;
                else
                    value = 0;
                currentYellTask.setStatus(value);
            }
        });

        AppCompatEditText content = binding.contentEditText;
        AppCompatImageButton editContent = binding.editContent;
        AppCompatImageButton editContentDiscard = binding.editContentDiscard;
        StringBuffer currentContent = new StringBuffer();
        editContent.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (editContentDiscard.getVisibility() == View.GONE) {
                    content.setEnabled(true);
                    content.requestFocusFromTouch();
                    InputMethodManager imm = (InputMethodManager)
                            getContext().getSystemService(Context.INPUT_METHOD_SERVICE);
                    imm.showSoftInput(content, InputMethodManager.SHOW_IMPLICIT);
                    content.setSelection(content.getText().length());

                    editContent.setImageResource(R.drawable.ic_done);
                    editContent.setColorFilter(getResources().getColor(R.color.green));

                    editContentDiscard.setVisibility(View.VISIBLE);
                    currentContent.append(content.getText());
                }
                else {
                    content.setEnabled(false);
                    currentContent.delete(0,currentContent.length());

                    editContent.setColorFilter(null);
                    editContent.setImageResource(R.drawable.ic_edit_mode);

                    editContentDiscard.setVisibility(View.GONE);

                    currentYellTask.setContent(content.getText().toString());
                }
            }
        });
        editContentDiscard.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                content.setEnabled(false);
                content.setText(currentContent.toString());
                currentContent.delete(0,currentContent.length());


                editContent.setColorFilter(null);
                editContent.setImageResource(R.drawable.ic_edit_mode);

                editContentDiscard.setVisibility(View.GONE);
            }
        });

        AppCompatTextView chooseFileTextView = binding.chooseFileTextView;
        chooseFileTextView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                BottomSheetFilePicker filePicker = new BottomSheetFilePicker();
                filePicker.show(getActivity().getSupportFragmentManager(),"File Picker");
            }
        });


    }
    private void getTaskFromServer() {
        service = Client.createServiceWithAuth(ApiService.class, sessionManager);
        Call<YellTask> call;
        call = service.getTask(taskId);
        call.enqueue(new Callback<YellTask>() {
            @Override
            public void onResponse(Call<YellTask> call, Response<YellTask> response) {
                if (response.isSuccessful()) {
                    // TODO: Binding data for field
                } else {
                    ErrorMessage apiError = ErrorMessage.convertErrors(response.errorBody());
                    Toast.makeText(getActivity(), apiError.getMessage(), Toast.LENGTH_LONG).show();
                }
            }

            @Override
            public void onFailure(Call<YellTask> call, Throwable t) {
                Log.w("YellTaskFragment", "onFailure: " + t.getMessage() );
            }
        });
    }
    private void addTaskToServer() {
        service = Client.createServiceWithAuth(ApiService.class, sessionManager);
        Call<InfoMessage> call;
        RequestBody requestBody = taskToJson();
        call = service.addTask(requestBody);
        call.enqueue(new Callback<InfoMessage>() {
            @Override
            public void onResponse(Call<InfoMessage> call, Response<InfoMessage> response) {

                Log.w("YellTaskCreate", "onResponse: " + response);

                if (response.isSuccessful()) {
                    // TODO
                }
                else {
                    if (response.code() == 401) {
                        ErrorMessage apiError = ErrorMessage.convertErrors(response.errorBody());
                        Toast.makeText(getContext(), apiError.getMessage(), Toast.LENGTH_LONG).show();
                    }
                    // TODO
                }

            }

            @Override
            public void onFailure(Call<InfoMessage> call, Throwable t) {
                Toast.makeText(getContext(), "Lỗi khi kết nối với server", Toast.LENGTH_LONG).show();
                // TODO:
            }
        });
    }
    private RequestBody taskToJson() {
        String jsonYellTask = moshi.adapter(YellTask.class).toJson(currentYellTask);
        RequestBody requestBody = RequestBody.create(MediaType.parse("text/plain"), jsonYellTask);
        return requestBody;
    }
}