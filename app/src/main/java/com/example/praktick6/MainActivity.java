package com.example.praktick6;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.google.android.material.snackbar.Snackbar;

public class MainActivity extends AppCompatActivity {
    private static final String STATE_SUMMARY = "summary";
    private static final String STATE_PRIORITY = "priority";

    private View root;
    private EditText taskName;
    private EditText priorityInput;
    private CheckBox urgent;
    private TextView result;
    private int resultPriority;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        root = findViewById(R.id.main);
        taskName = findViewById(R.id.et_task_name);
        priorityInput = findViewById(R.id.et_priority);
        urgent = findViewById(R.id.cb_urgent);
        result = findViewById(R.id.tv_result);
        applyInsets(root);

        // Нажатие на иллюстрацию подсказывает, как заполнить форму.
        findViewById(R.id.iv_task_help).setOnClickListener(view ->
                Snackbar.make(root, R.string.form_help, Snackbar.LENGTH_LONG).show());

        if (savedInstanceState != null && savedInstanceState.containsKey(STATE_SUMMARY)) {
            resultPriority = savedInstanceState.getInt(STATE_PRIORITY);
            result.setText(savedInstanceState.getCharSequence(STATE_SUMMARY));
            result.setTextColor(getColor(priorityColor(resultPriority)));
            result.setVisibility(View.VISIBLE);
        }
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        if (result.getVisibility() == View.VISIBLE) {
            outState.putCharSequence(STATE_SUMMARY, result.getText());
            outState.putInt(STATE_PRIORITY, resultPriority);
        }
    }

    public void onCalculateClick(View view) {
        readTask();
    }

    public void onNextClick(View view) {
        Task task = readTask();
        if (task == null) {
            return;
        }

        // Явный Intent передаёт три значения экрана результата.
        Intent intent = new Intent(this, SecondActivity.class);
        intent.putExtra(SecondActivity.EXTRA_NAME, task.name);
        intent.putExtra(SecondActivity.EXTRA_PRIORITY, task.priority);
        intent.putExtra(SecondActivity.EXTRA_URGENT, task.urgent);
        startActivity(intent);
    }

    // Кнопки используют одну и ту же проверку и расчёт отображаемого результата.
    private Task readTask() {
        hideKeyboard();
        result.setVisibility(View.GONE);

        String name = taskName.getText().toString().trim();
        if (name.isEmpty()) {
            showError(taskName, R.string.error_name);
            return null;
        }

        int priority;
        try {
            priority = Integer.parseInt(priorityInput.getText().toString().trim());
        } catch (NumberFormatException exception) {
            showError(priorityInput, R.string.error_priority);
            return null;
        }
        if (priority < 1 || priority > 3) {
            showError(priorityInput, R.string.error_priority);
            return null;
        }

        Task task = new Task(name, priority, urgent.isChecked());
        resultPriority = task.priority;
        result.setText(getString(R.string.task_summary, task.name, task.priority,
                getString(task.urgent ? R.string.urgent_yes : R.string.urgent_no)));
        result.setTextColor(getColor(priorityColor(task.priority)));
        result.setVisibility(View.VISIBLE);
        return task;
    }

    private void showError(EditText field, int message) {
        field.requestFocus();
        field.setError(getString(message));
        Snackbar.make(root, message, Snackbar.LENGTH_LONG).show();
    }

    private void hideKeyboard() {
        InputMethodManager keyboard = getSystemService(InputMethodManager.class);
        keyboard.hideSoftInputFromWindow(root.getWindowToken(), 0);
    }

    static int priorityColor(int priority) {
        switch (priority) {
            case 1: return R.color.priority_low;
            case 2: return R.color.priority_medium;
            default: return R.color.priority_high;
        }
    }

    // Учитываем системные панели и клавиатуру, чтобы форма оставалась доступной.
    static void applyInsets(View root) {
        ViewCompat.setOnApplyWindowInsetsListener(root, (view, insets) -> {
            Insets safe = insets.getInsets(
                    WindowInsetsCompat.Type.systemBars() | WindowInsetsCompat.Type.displayCutout()
                            | WindowInsetsCompat.Type.ime());
            view.setPadding(safe.left, safe.top, safe.right, safe.bottom);
            return insets;
        });
    }

    private static class Task {
        private final String name;
        private final int priority;
        private final boolean urgent;

        private Task(String name, int priority, boolean urgent) {
            this.name = name;
            this.priority = priority;
            this.urgent = urgent;
        }
    }
}
