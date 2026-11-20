package com.example.praktick6;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

public class SecondActivity extends AppCompatActivity {
    static final String EXTRA_NAME = "com.example.praktick6.TASK_NAME";
    static final String EXTRA_PRIORITY = "com.example.praktick6.TASK_PRIORITY";
    static final String EXTRA_URGENT = "com.example.praktick6.TASK_URGENT";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_second);
        MainActivity.applyInsets(findViewById(R.id.second));

        // Читаем данные карточки из Intent и показываем их в макете.
        Intent intent = getIntent();
        String name = intent.getStringExtra(EXTRA_NAME);
        int priority = intent.getIntExtra(EXTRA_PRIORITY, 0);
        boolean urgent = intent.getBooleanExtra(EXTRA_URGENT, false);
        if (name == null || priority < 1 || priority > 3) {
            finish();
            return;
        }

        TextView taskName = findViewById(R.id.tv_task_name);
        TextView taskPriority = findViewById(R.id.tv_task_priority);
        TextView taskUrgency = findViewById(R.id.tv_task_urgency);
        taskName.setText(name);
        taskPriority.setText(getString(R.string.priority_value, priority));
        taskPriority.setTextColor(getColor(MainActivity.priorityColor(priority)));
        taskUrgency.setText(urgent ? R.string.urgent_yes : R.string.urgent_no);
    }

    public void onBackClick(View view) {
        // Закрываем карточку и возвращаемся к ранее заполненной форме.
        finish();
    }
}
