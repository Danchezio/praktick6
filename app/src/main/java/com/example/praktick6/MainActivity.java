package com.example.praktick6;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.google.android.material.snackbar.Snackbar;

import java.math.BigDecimal;
import java.math.RoundingMode;

public class MainActivity extends AppCompatActivity {
    private static final String STATE_SUMMARY = "summary";
    private static final BigDecimal MAX_DISCOUNT = BigDecimal.valueOf(100);

    private View root;
    private EditText priceInput;
    private EditText discountInput;
    private TextView result;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        root = findViewById(R.id.main);
        priceInput = findViewById(R.id.et_price);
        discountInput = findViewById(R.id.et_discount);
        result = findViewById(R.id.tv_result);
        applyInsets(root);

        // Проверяем поля при потере фокуса и запускаем расчёт по кнопкам.
        priceInput.setOnFocusChangeListener((view, hasFocus) -> {
            if (!hasFocus) readAmount(priceInput);
        });
        discountInput.setOnFocusChangeListener((view, hasFocus) -> {
            if (!hasFocus) readAmount(discountInput);
        });
        findViewById(R.id.btn_calculate).setOnClickListener(view -> calculate());
        findViewById(R.id.btn_next).setOnClickListener(view -> openResult());

        if (savedInstanceState != null && savedInstanceState.containsKey(STATE_SUMMARY)) {
            result.setText(savedInstanceState.getCharSequence(STATE_SUMMARY));
            result.setVisibility(View.VISIBLE);
        }
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        if (result.getVisibility() == View.VISIBLE) {
            outState.putCharSequence(STATE_SUMMARY, result.getText());
        }
    }

    private void openResult() {
        Discount result = calculate();
        if (result == null) {
            return;
        }

        // Явный Intent передаёт новую цену и экономию как два значения double.
        Intent intent = new Intent(this, SecondActivity.class);
        intent.putExtra(SecondActivity.EXTRA_NEW_PRICE, result.newPrice);
        intent.putExtra(SecondActivity.EXTRA_SAVINGS, result.savings);
        startActivity(intent);
    }

    // Результат округляется до копеек. Экономия дополняет новую цену до исходной.
    private Discount calculate() {
        InputMethodManager keyboard = getSystemService(InputMethodManager.class);
        keyboard.hideSoftInputFromWindow(root.getWindowToken(), 0);
        BigDecimal price = readAmount(priceInput);
        BigDecimal discount = readAmount(discountInput);
        if (price == null || discount == null) {
            result.setVisibility(View.GONE);
            EditText invalidField = price == null ? priceInput : discountInput;
            invalidField.requestFocus();
            Snackbar.make(root, invalidField.getError(), Snackbar.LENGTH_LONG).show();
            return null;
        }

        price = price.setScale(2, RoundingMode.HALF_UP);
        BigDecimal newPrice = price.multiply(BigDecimal.ONE.subtract(discount.movePointLeft(2)))
                .setScale(2, RoundingMode.HALF_UP);
        Discount calculation = new Discount(newPrice.doubleValue(),
                price.subtract(newPrice).doubleValue());
        result.setText(getString(R.string.discount_summary,
                MoneyText.format(this, calculation.newPrice),
                MoneyText.format(this, calculation.savings)));
        result.setVisibility(View.VISIBLE);
        return calculation;
    }

    private BigDecimal readAmount(EditText field) {
        boolean isDiscount = field == discountInput;
        String text = field.getText().toString().trim().replace(',', '.');
        try {
            BigDecimal amount = new BigDecimal(text);
            if (amount.signum() >= 0 && (!isDiscount || amount.compareTo(MAX_DISCOUNT) <= 0)) {
                field.setError(null);
                return amount;
            }
        } catch (NumberFormatException ignored) {
            // Пустая строка и некорректное число обрабатываются одинаково.
        }
        field.setError(getString(isDiscount ? R.string.error_discount : R.string.error_price));
        return null;
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

    private static final class Discount {
        private final double newPrice;
        private final double savings;

        private Discount(double newPrice, double savings) {
            this.newPrice = newPrice;
            this.savings = savings;
        }
    }
}
