// MainActivity.java
package com.example.todolist;

import android.app.TimePickerDialog;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TimePicker;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.util.ArrayList;
import java.util.Calendar;

public class MainActivity extends AppCompatActivity {

    private ArrayList<String> taskList = new ArrayList<>();
    private ArrayAdapter<String> taskAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_main);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        ListView taskListView = findViewById(R.id.taskListView);
        taskAdapter = new ArrayAdapter<>(this, R.layout.list_item_task, R.id.taskText, taskList);
        taskListView.setAdapter(taskAdapter);

        FloatingActionButton addTaskButton = findViewById(R.id.addTaskButton);
        addTaskButton.setOnClickListener(view -> showAddTaskDialog());

        taskListView.setOnItemLongClickListener((adapterView, view, position, id) -> {
            showEditDeleteDialog(position);
            return false;
        });
    }

    private void showAddTaskDialog() {
        final EditText taskEditText = new EditText(this);

        AlertDialog dialog = new AlertDialog.Builder(this)
                .setTitle("Add New Task")
                .setView(taskEditText)
                .setPositiveButton("Next", (dialogInterface, i) -> {
                    String task = taskEditText.getText().toString();
                    if (!task.isEmpty()) {
                        showTimePickerDialog(task, -1); // -1 indicates new task
                    }
                })
                .setNegativeButton("Cancel", null)
                .create();
        dialog.show();
    }

    private void showTimePickerDialog(String task, int position) {
        Calendar currentTime = Calendar.getInstance();
        int hour = currentTime.get(Calendar.HOUR_OF_DAY);
        int minute = currentTime.get(Calendar.MINUTE);

        TimePickerDialog timePickerDialog = new TimePickerDialog(this, (TimePicker timePicker, int selectedHour, int selectedMinute) -> {
            String amPm = selectedHour >= 12 ? "PM" : "AM";
            int hourIn12 = selectedHour % 12;
            if (hourIn12 == 0) hourIn12 = 12;

            String formattedTime = String.format("Time: %02d:%02d %s", hourIn12, selectedMinute, amPm);
            String taskWithTime = task + " (" + formattedTime + ")";

            if (position == -1) { // New task
                taskList.add(taskWithTime);
            } else { // Edit existing task
                taskList.set(position, taskWithTime);
            }
            taskAdapter.notifyDataSetChanged();
        }, hour, minute, false); // Use 12-hour format
        timePickerDialog.show();
    }

    private void showEditDeleteDialog(int position) {
        String task = taskList.get(position);

        AlertDialog dialog = new AlertDialog.Builder(this)
                .setTitle("Edit or Delete Task")
                .setMessage("Choose an Option For: " + task)
                .setPositiveButton("Edit", (dialogInterface, i) -> showEditTaskDialog(position))
                .setNegativeButton("Delete", (dialogInterface, i) -> deleteTask(position))
                .create();
        dialog.show();
    }

    private void showEditTaskDialog(int position) {
        final EditText taskEditText = new EditText(this);
        taskEditText.setText(taskList.get(position).split(" \\(")[0]); // Set text without time

        AlertDialog dialog = new AlertDialog.Builder(this)
                .setTitle("Edit Task")
                .setView(taskEditText)
                .setPositiveButton("Next", (dialogInterface, i) -> {
                    String updatedTask = taskEditText.getText().toString();
                    if (!updatedTask.isEmpty()) {
                        showTimePickerDialog(updatedTask, position);
                    }
                })
                .setNegativeButton("Cancel", null)
                .create();
        dialog.show();
    }

    private void deleteTask(int position) {
        taskList.remove(position);
        taskAdapter.notifyDataSetChanged();
    }
}
