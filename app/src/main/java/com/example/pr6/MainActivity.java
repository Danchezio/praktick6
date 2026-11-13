package com.example.pr6;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.google.android.material.snackbar.Snackbar;

public class MainActivity extends AppCompatActivity {

    private View mainRoot;
    private EditText etName;
    private EditText etGuests;
    private CheckBox cbWindow;
    private TextView tvResult;
    private ImageView ivIllustration;

    private static class BookingData {
        final String name;
        final int guests;
        final boolean isWindow;

        BookingData(String name, int guests, boolean isWindow) {
            this.name = name;
            this.guests = guests;
            this.isWindow = isWindow;
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // Находим элементы по идентификаторам
        mainRoot = findViewById(R.id.mainRoot);
        etName = findViewById(R.id.etName);
        etGuests = findViewById(R.id.etGuests);
        cbWindow = findViewById(R.id.cbWindow);
        tvResult = findViewById(R.id.tvResult);
        ivIllustration = findViewById(R.id.ivIllustration);

        ivIllustration.setOnClickListener(v ->
                Snackbar.make(mainRoot, getString(R.string.hint_image_click), Snackbar.LENGTH_SHORT).show()
        );
    }

    private BookingData validateAndGetBookingData() {
        String name = etName.getText().toString().trim();
        String guestsText = etGuests.getText().toString().trim();

        if (TextUtils.isEmpty(name)) {
            showError(getString(R.string.err_empty_name));
            return null;
        }

        if (TextUtils.isEmpty(guestsText)) {
            showError(getString(R.string.err_empty_guests));
            return null;
        }

        int guests;
        try {
            guests = Integer.parseInt(guestsText);
        } catch (NumberFormatException e) {
            showError(getString(R.string.err_not_number));
            return null;
        }

        if (guests < 1 || guests > 10) {
            showError(getString(R.string.err_guests_range));
            return null;
        }

        boolean isWindow = cbWindow.isChecked();
        return new BookingData(name, guests, isWindow);
    }

    private void showError(String message) {
        Snackbar.make(mainRoot, message, Snackbar.LENGTH_LONG).show();
    }

    public void onCalculateClick(View view) {
        BookingData data = validateAndGetBookingData();
        if (data == null) {
            return;
        }

        String windowText = getString(data.isWindow ? R.string.option_yes : R.string.option_no);
        String formattedResult = getString(R.string.result_format, data.name, data.guests, windowText);
        tvResult.setText(formattedResult);
    }

    public void onNextClick(View view) {
        BookingData data = validateAndGetBookingData();
        if (data == null) {
            return;
        }

        // Создаём явный Intent и передаём имя, гостей и флаг столика у окна
        Intent intent = new Intent(MainActivity.this, SecondActivity.class);
        intent.putExtra(SecondActivity.EXTRA_NAME, data.name);
        intent.putExtra(SecondActivity.EXTRA_GUESTS, data.guests);
        intent.putExtra(SecondActivity.EXTRA_WINDOW, data.isWindow);
        startActivity(intent);
    }
}