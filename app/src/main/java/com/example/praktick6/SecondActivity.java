package com.example.praktick6;

import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.example.praktick6.databinding.ActivitySecondBinding;

/** Второй экран: показывает исходную сумму и результат, принятые с первого экрана. */
public class SecondActivity extends AppCompatActivity {

    // Ключи extra объявлены здесь, чтобы оба экрана использовали одни и те же константы
    public static final String EXTRA_AMOUNT = "extra_amount";
    public static final String EXTRA_RESULT = "extra_result";

    private ActivitySecondBinding binding; // Класс привязки для activity_second.xml

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        binding = ActivitySecondBinding.inflate(getLayoutInflater());
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

        // Читаем данные из Intent; второй параметр — значение по умолчанию
        double amount = getIntent().getDoubleExtra(EXTRA_AMOUNT, 0.0);
        double result = getIntent().getDoubleExtra(EXTRA_RESULT, 0.0);

        // Показываем данные
        binding.tvSecondAmount.setText(getString(R.string.second_amount_format, amount));
        binding.tvSecondResult.setText(getString(R.string.second_result_format, result));

        // «Назад»: закрываем экран, пользователь возвращается на первый (данные в полях сохраняются)
        binding.btnBack.setOnClickListener(v -> finish());
    }
}
