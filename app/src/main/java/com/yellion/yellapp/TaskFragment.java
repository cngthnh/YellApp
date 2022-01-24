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

import androidx.fragment.app.FragmentActivity;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.google.android.material.bottomappbar.BottomAppBar;
import com.google.android.material.dialog.MaterialAlertDialogBuilder;
import com.yellion.yellapp.adapters.TaskAdapter;
import com.yellion.yellapp.databinding.FragmentTaskBinding;
import com.yellion.yellapp.models.YellTask;
import com.yellion.yellapp.viewmodels.YellTaskViewModel;

import java.text.DateFormat;
import java.text.ParseException;
import java.time.format.DateTimeFormatter;
import java.util.Date;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link TaskFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class TaskFragment extends Fragment {

    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "taskName";
    private static final String ARG_PARAM2 = "dashboardId";
    private static final String ARG_PARAM3 = "taskId";
    private static final String ARG_PARAM4 = "previousTaskName";
    private static final String ARG_PARAM5 = "parentId";

    YellTask currentYellTask;
    FragmentTaskBinding binding;
    YellTaskViewModel viewModel;
    TaskAdapter yellTaskAdapter;
    LoadingDialog loadingDialog;


    // TODO: Rename and change types of parameters
    private String previousTaskName;

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
    public static TaskFragment newInstance(String taskName, String dashBoardId, String taskId,
                                           String previousTaskName) {
        TaskFragment fragment = new TaskFragment();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, taskName);
        args.putString(ARG_PARAM2, dashBoardId);
        args.putString(ARG_PARAM3, taskId);
        args.putString(ARG_PARAM4, previousTaskName);
        fragment.setArguments(args);
        return fragment;
    }
    public static TaskFragment newInstance(String taskName, String dashBoardId, String taskId,
                                           String previousTaskName, String parentId) {
        TaskFragment fragment = new TaskFragment();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, taskName);
        args.putString(ARG_PARAM2, dashBoardId);
        args.putString(ARG_PARAM3, taskId);
        args.putString(ARG_PARAM4, previousTaskName);
        args.putString(ARG_PARAM5, parentId);
        fragment.setArguments(args);
        return fragment;
    }
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        loadingDialog = new LoadingDialog(getActivity());
        viewModel = new ViewModelProvider(this).get(YellTaskViewModel.class);
        currentYellTask = new YellTask();
        viewModel.init();
        viewModel.getYellTaskLiveData().observe(this, new Observer<YellTask>() {
            @RequiresApi(api = Build.VERSION_CODES.N)
            @Override
            public void onChanged(YellTask yellTask) {
                if (loadingDialog != null)
                    loadingDialog.dismissDialog();
                currentYellTask = yellTask;
                if (yellTask != null) {
                     if (currentYellTask.getEnd_time() != null)
                        binding.deadlineTask.setText(serverTime2MobileTime(currentYellTask.getEnd_time()));

                     if (currentYellTask.getPriority() != null) {
                        if (currentYellTask.getPriority() == 2)
                            binding.priorityTextView.setText("Thấp");
                        else if (currentYellTask.getPriority() == 0)
                            binding.priorityTextView.setText("Cao");
                        else
                            binding.priorityTextView.setText("Thường");
                    }

                    if (yellTask.getName() != null)
                        binding.taskName.setText(yellTask.getName());

                    if (yellTask.getContent() !=null )
                        binding.contentEditText.setText(yellTask.getContent());
                }


            }
        });
        viewModel.getTaskIdLiveData().observe(this, new Observer<String>() {
            @Override
            public void onChanged(String s) {
                currentYellTask.setTask_id(s);
            }
        });
        yellTaskAdapter = new TaskAdapter(getActivity());
        if (getArguments() != null) {
            currentYellTask.setName(getArguments().getString(ARG_PARAM1));
            currentYellTask.setDashboard_id(getArguments().getString(ARG_PARAM2));
            currentYellTask.setTask_id(getArguments().getString(ARG_PARAM3));
            previousTaskName = getArguments().getString(ARG_PARAM4);
            currentYellTask.setParent_id(getArguments().getString(ARG_PARAM5));
            if (currentYellTask.getTask_id() == null) {
                viewModel.addTask(currentYellTask);
            }
            else {
                viewModel.getTask(currentYellTask.getTask_id());
                loadingDialog.startLoadingDialog();
            }
        }
    }




    @RequiresApi(api = Build.VERSION_CODES.N)
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
    }

    private void setToolbarTaskListener() {
        AppCompatImageButton editNameTask = binding.editNameTask;
        AppCompatImageButton deleteTask = binding.deleteTask;
        AppCompatEditText taskName = binding.taskName;
        AppCompatImageButton taskIcon = binding.taskIcon;
        if (currentYellTask.getName() != null)
            taskName.setText(currentYellTask.getName());
        if (this.previousTaskName != null)
            binding.previousTask.setText(previousTaskName);
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
                    viewModel.editTask(currentYellTask);

                }
            }
        });
        taskName.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                if (s.toString().length()>40)
                    taskName.setError("Nhập quá 40 ký tự");
            }
        });
        deleteTask.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if ((String)taskIcon.getTag() == "true") {
                    taskName.setEnabled(false);
                    taskName.setText(currentName);
                    taskName.setError(null);

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

        binding.backButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(getActivity()!= null){
                    getActivity().getSupportFragmentManager().popBackStack();
                }
            }
        });
    }

    @RequiresApi(api = Build.VERSION_CODES.N)
    private void setConfigTaskListener() {
        AppCompatTextView deadlineTask = binding.deadlineTask;
        if (currentYellTask.getEnd_time() != null)
            deadlineTask.setText(serverTime2MobileTime(currentYellTask.getEnd_time()));
        DeadlineTimeDialog deadlineTimeDialog = new DeadlineTimeDialog();
        deadlineTimeDialog.getDateTimeLiveData().observe(getViewLifecycleOwner(), new Observer<String>() {
            @Override
            public void onChanged(String s) {
                deadlineTask.setText(s);
                currentYellTask.setEnd_time(mobileTime2ServerTime(s));
                viewModel.editTask(currentYellTask);
            }
        });
        deadlineTask.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                deadlineTimeDialog.show(getActivity().getSupportFragmentManager(),
                        "DeadlineTimeDialog");
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
                                currentYellTask.setPriority(which);
                                viewModel.editTask(currentYellTask);
                            }
                        });
                priorityDialog.show();
            }
        });
        AppCompatTextView status = binding.statusTextView;
        if (currentYellTask.getStatus() != null) {
            if (currentYellTask.getStatus() == 1)
                status.setText("Đã hoàn thành");
            else {
                status.setText("Chưa hoàn thành");
            }
        }
        status.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String [] statuses = new String[]{"Chưa hoàn thành","Đã hoàn thành"};
                MaterialAlertDialogBuilder statusDialog = new MaterialAlertDialogBuilder(getContext())
                        .setTitle("Trạng thái")
                        .setItems(statuses, new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                status.setText(statuses[which]);
                                currentYellTask.setStatus(which);
                                viewModel.editTask(currentYellTask);
                            }
                        });
                statusDialog.show();
            }
        });
        AppCompatEditText content = binding.contentEditText;
        if (currentYellTask.getContent() != null)
            content.setText(currentYellTask.getContent());
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
                    viewModel.editTask(currentYellTask);
                }
            }
        });
        editContentDiscard.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                content.setEnabled(false);
                content.setText(currentContent.toString());
                content.setError(null);
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

        content.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                if (s.toString().length()>200)
                    content.setError("Nhập quá 200 ký tự");
            }
        });


        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getActivity());
        binding.listTask.setAdapter(yellTaskAdapter);
        binding.listTask.setLayoutManager(linearLayoutManager);

        binding.addSubTask.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                YellTask yell = new YellTask("None","Công việc "+String.valueOf(yellTaskAdapter.getItemCount()+1));
                yellTaskAdapter.addYellTask(yell);
            }
        });
    }

    @RequiresApi(api = Build.VERSION_CODES.N)
    private String serverTime2MobileTime(String time) {
        SimpleDateFormat currentFormat = new SimpleDateFormat("HH:mm  dd/MM/YYYY");
        SimpleDateFormat isoFormat = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss");
        try {
            Date date = isoFormat.parse(time);
            return currentFormat.format(date);
        } catch (ParseException e) {
            Log.e("TimeParseError", "Time Parse Error");
            return null;
        }
    }

    @RequiresApi(api = Build.VERSION_CODES.N)
    private String mobileTime2ServerTime(String time) {
        SimpleDateFormat currentFormat = new SimpleDateFormat("HH:mm  dd/MM/YYYY");
        SimpleDateFormat isoFormat = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss");
        try {
            Date date = currentFormat.parse(time);
            return isoFormat.format(date);
        } catch (ParseException e) {
            Log.e("TimeParseError", "Time Parse Error");
            return null;
        }
    }

}