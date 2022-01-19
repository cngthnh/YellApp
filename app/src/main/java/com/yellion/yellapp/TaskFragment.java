package com.yellion.yellapp;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.widget.AppCompatEditText;
import androidx.appcompat.widget.AppCompatImageButton;
import androidx.appcompat.widget.AppCompatTextView;
import androidx.fragment.app.Fragment;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.Toast;

import com.google.android.material.bottomappbar.BottomAppBar;
import com.google.android.material.dialog.MaterialAlertDialogBuilder;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.yellion.yellapp.databinding.FragmentTaskBinding;
import com.yellion.yellapp.models.ErrorMessage;
import com.yellion.yellapp.models.Task;
import com.yellion.yellapp.models.UserAccount;
import com.yellion.yellapp.utils.ApiService;
import com.yellion.yellapp.utils.Client;
import com.yellion.yellapp.utils.SessionManager;

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
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";
    SessionManager sessionManager;
    ApiService service;
    String taskId;
    private FragmentTaskBinding binding;

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    public TaskFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment TaskFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static TaskFragment newInstance(String param1, String param2) {
        TaskFragment fragment = new TaskFragment();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mParam1 = getArguments().getString(ARG_PARAM1);
            mParam2 = getArguments().getString(ARG_PARAM2);
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
    private void getDataFromServer() {
        service = Client.createServiceWithAuth(ApiService.class, sessionManager);
        Call<Task> call;
        call = service.getTask(taskId);
        call.enqueue(new Callback<Task>() {
            @Override
            public void onResponse(Call<Task> call, Response<Task> response) {
                if (response.isSuccessful()) {
                    // TODO: Binding data for field
                } else {
                    ErrorMessage apiError = ErrorMessage.convertErrors(response.errorBody());
                    Toast.makeText(getActivity(), apiError.getMessage(), Toast.LENGTH_LONG).show();
                }
            }

            @Override
            public void onFailure(Call<Task> call, Throwable t) {
                Log.w("YellTaskFragment", "onFailure: " + t.getMessage() );
            }
        });
    }
}