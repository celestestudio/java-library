package com.celeste.library.shared.util.formatter;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.jetbrains.annotations.NotNull;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public final class RomanNumberFormatter {

  private static final String[] symbols = { "M", "CM", "D", "CD", "C", "XC", "L", "XL", "X", "IX", "V", "IV", "I" };
  private static final int[] numbers = { 1000, 900, 500, 400, 100, 90, 50, 40, 10, 9, 5, 4, 1 };

  public static String format(int number) {
    for (int i = 0; i < numbers.length; i++) {
      if (number >= numbers[i]) {
        return symbols[i] + format(number - numbers[i]);
      }
    }

    return "";
  }

  public static int unFormat(@NotNull final String number) {
    for (int i = 0; i < symbols.length; i++) {
      if (!number.startsWith(symbols[i])) continue;

      return numbers[i] + unFormat(number.replaceFirst(symbols[i], ""));
    }

    return 0;
  }

}