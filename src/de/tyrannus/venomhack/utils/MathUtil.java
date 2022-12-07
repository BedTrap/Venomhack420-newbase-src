package de.tyrannus.venomhack.utils;

import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;
import java.util.Locale;

public class MathUtil {
   private static final DecimalFormat decimal_format = new DecimalFormat("0", DecimalFormatSymbols.getInstance(Locale.ENGLISH));

   public static String format(double number) {
      return decimal_format.format(number);
   }

   public static String format(float number) {
      return decimal_format.format((double)number);
   }

   public static double round(float value, int precision) {
      double scale = Math.pow(10.0, (double)precision);
      return (double)Math.round((double)value * scale) / scale;
   }

   public static double round(double value, int precision) {
      double scale = Math.pow(10.0, (double)precision);
      return (double)Math.round(value * scale) / scale;
   }

   public static int clamp8Bit(int num) {
      return (num & ~(num >> 31) | 255 - num >> 31) & 0xFF;
   }

   static {
      decimal_format.setMaximumFractionDigits(340);
   }
}
