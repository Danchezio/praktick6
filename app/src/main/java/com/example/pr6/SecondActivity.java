package com.example.pr6;

import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

public class SecondActivity extends AppCompatActivity {

    public static final String EXTRA_NAME = "extra_name";
    public static final String EXTRA_GUESTS = "extra_guests";
    public static final String EXTRA_WINDOW = "extra_window";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_second);

        TextView tvSecondDetails = findViewById(R.id.tvSecondDetails);

        String name = getIntent().getStringExtra(EXTRA_NAME);
        if (name == null) {
            name = "";
        }
        int guests = getIntent().getIntExtra(EXTRA_GUESTS, 1);
        boolean isWindow = getIntent().getBooleanExtra(EXTRA_WINDOW, false);

        String windowText = getString(isWindow ? R.string.option_yes : R.string.option_no);
        tvSecondDetails.setText(getString(R.string.second_data_format, name, guests, windowText));
    }

    public void onBackClick(View view) {
        finish();
    }
}