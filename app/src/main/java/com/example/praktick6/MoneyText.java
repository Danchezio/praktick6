package com.example.praktick6;

import android.content.Context;
import android.content.res.Configuration;

import java.math.BigDecimal;
import java.text.NumberFormat;
import java.util.Locale;

final class MoneyText {
    private static final Locale RUSSIAN = Locale.forLanguageTag("ru-RU");

    private MoneyText() {}

    static String format(Context context, double amount) {
        NumberFormat formatter = NumberFormat.getNumberInstance(RUSSIAN);
        formatter.setMaximumFractionDigits(2);
        String number = formatter.format(amount);
        BigDecimal rubles = BigDecimal.valueOf(amount).stripTrailingZeros();
        if (rubles.scale() > 0) {
            return context.getString(R.string.rubles_fractional, number);
        }

        // Интерфейс на русском, поэтому и формы слова «рубль» выбираем по русским правилам.
        Configuration russian = new Configuration(context.getResources().getConfiguration());
        russian.setLocale(RUSSIAN);
        int quantity = rubles.remainder(BigDecimal.valueOf(100)).intValueExact();
        return context.createConfigurationContext(russian).getResources()
                .getQuantityString(R.plurals.rubles, quantity, number);
    }
}
