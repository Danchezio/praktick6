package com.example.praktick6;

import android.content.Intent;
import android.os.Bundle;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

public class SecondActivity extends AppCompatActivity {
    static final String EXTRA_NEW_PRICE = "com.example.praktick6.NEW_PRICE";
    static final String EXTRA_SAVINGS = "com.example.praktick6.SAVINGS";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_second);
        MainActivity.applyInsets(findViewById(R.id.second));

        // Читаем суммы из Intent и выводим их в отдельных строках.
        Intent intent = getIntent();
        double newPrice = intent.getDoubleExtra(EXTRA_NEW_PRICE, Double.NaN);
        double savings = intent.getDoubleExtra(EXTRA_SAVINGS, Double.NaN);
        if (!Double.isFinite(newPrice) || !Double.isFinite(savings)) {
            finish();
            return;
        }

        TextView price = findViewById(R.id.tv_new_price);
        TextView saving = findViewById(R.id.tv_savings);
        price.setText(getString(R.string.new_price_value, MoneyText.format(this, newPrice)));
        saving.setText(getString(R.string.savings_value, MoneyText.format(this, savings)));
        findViewById(R.id.btn_back).setOnClickListener(view -> finish());
    }
}
