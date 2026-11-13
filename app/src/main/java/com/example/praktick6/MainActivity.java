package com.example.praktick6;

import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.widget.EditText;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.example.praktick6.databinding.ActivityMainBinding;
import com.google.android.material.snackbar.Snackbar;

import java.math.BigDecimal;
import java.math.RoundingMode;

/** Первый экран: ввод суммы и курса, расчёт и переход на второй экран. */
public class MainActivity extends AppCompatActivity {

    private ActivityMainBinding binding; // Класс привязки, сгенерированный по activity_main.xml

    private boolean hasResult = false;   // Был ли выполнен успешный расчёт
    private double lastAmount = 0.0;     // Исходная сумма последнего расчёта
    private double lastResult = 0.0;     // Результат последнего расчёта

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // Раздуваем макет через ViewBinding и показываем корневой View
        binding = ActivityMainBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        // Отступы под системные панели (на новых Android экран рисуется во весь размер)
        final int padL = binding.getRoot().getPaddingLeft();   // Исходные отступы из XML запоминаем один раз
        final int padT = binding.getRoot().getPaddingTop();
        final int padR = binding.getRoot().getPaddingRight();
        final int padB = binding.getRoot().getPaddingBottom();
        ViewCompat.setOnApplyWindowInsetsListener(binding.getRoot(), (view, windowInsets) -> {
            Insets bars = windowInsets.getInsets(WindowInsetsCompat.Type.systemBars());
            view.setPadding(padL + bars.left, padT + bars.top, padR + bars.right, padB + bars.bottom);
            return WindowInsetsCompat.CONSUMED;
        });

        // Дополнительное событие: TextWatcher на оба обязательных поля
        InputWatcher watcher = new InputWatcher();
        binding.etAmount.addTextChangedListener(watcher);
        binding.etRate.addTextChangedListener(watcher);
        updateCalcButtonState(); // Начальное состояние кнопки «Рассчитать»

        // Основные события: клики по кнопкам
        binding.btnCalc.setOnClickListener(v -> onCalcClick());
        binding.btnNext.setOnClickListener(v -> onNextClick());
    }

    /** Следит за изменением текста: блокирует кнопку при пустых полях и сбрасывает старый результат. */
    private class InputWatcher implements TextWatcher {
        @Override
        public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            // Не используется
        }

        @Override
        public void onTextChanged(CharSequence s, int start, int before, int count) {
            // Не используется
        }

        @Override
        public void afterTextChanged(Editable s) {
            updateCalcButtonState();
            // Данные изменились — прежний результат больше не актуален
            hasResult = false;
            binding.tvResult.setText("");
        }
    }

    /** Кнопка «Рассчитать» доступна, только если оба поля непустые. */
    private void updateCalcButtonState() {
        boolean filled = !isBlank(binding.etAmount) && !isBlank(binding.etRate);
        binding.btnCalc.setEnabled(filled);
    }

    private boolean isBlank(EditText editText) {
        return editText.getText().toString().trim().isEmpty();
    }

    /** Нажатие «Рассчитать»: проверяем ввод, считаем и показываем результат на этом же экране. */
    private void onCalcClick() {
        // Проверка суммы
        Double amount = parseNumber(binding.etAmount.getText().toString());
        if (amount == null) {
            showError(binding.etAmount, getString(isBlank(binding.etAmount)
                    ? R.string.error_amount_empty : R.string.error_amount_invalid));
            return;
        }
        if (amount <= 0) {
            showError(binding.etAmount, getString(R.string.error_amount_positive));
            return;
        }

        // Проверка курса
        Double rate = parseNumber(binding.etRate.getText().toString());
        if (rate == null) {
            showError(binding.etRate, getString(isBlank(binding.etRate)
                    ? R.string.error_rate_empty : R.string.error_rate_invalid));
            return;
        }
        if (rate <= 0) {
            showError(binding.etRate, getString(R.string.error_rate_positive));
            return;
        }

        // Расчёт и вывод результата
        double result = convert(amount, rate);
        lastAmount = amount;
        lastResult = result;
        hasResult = true;
        binding.tvResult.setText(getString(R.string.result_format, amount, result));
    }

    /** Нажатие «Далее»: открываем второй экран и передаём исходную сумму и результат. */
    private void onNextClick() {
        if (!hasResult) {
            // Нечего передавать — просим сначала выполнить расчёт
            Snackbar.make(binding.getRoot(), R.string.error_calc_first, Snackbar.LENGTH_SHORT).show();
            return;
        }
        // Явный Intent: откуда — MainActivity.this, куда — SecondActivity
        Intent intent = new Intent(MainActivity.this, SecondActivity.class);
        intent.putExtra(SecondActivity.EXTRA_AMOUNT, lastAmount);
        intent.putExtra(SecondActivity.EXTRA_RESULT, lastResult);
        startActivity(intent);
    }

    /** Единственная функция расчёта: сумма / курс с округлением до 2 знаков. */
    private double convert(double amount, double rate) {
        return BigDecimal.valueOf(amount)
                .divide(BigDecimal.valueOf(rate), 2, RoundingMode.HALF_UP)
                .doubleValue();
    }

    /** Разбирает число из строки (принимает и запятую, и точку); при ошибке возвращает null. */
    private Double parseNumber(String text) {
        String normalized = text.trim().replace(',', '.');
        if (normalized.isEmpty()) {
            return null;
        }
        try {
            double value = Double.parseDouble(normalized);
            if (Double.isNaN(value) || Double.isInfinite(value)) {
                return null;
            }
            return value;
        } catch (NumberFormatException e) {
            return null; // Нечисловой ввод — без вылета
        }
    }

    /** Показывает ошибку и в самом поле, и в Snackbar. */
    private void showError(EditText field, String message) {
        field.setError(message);
        field.requestFocus();
        Snackbar.make(binding.getRoot(), message, Snackbar.LENGTH_LONG).show();
    }
}
