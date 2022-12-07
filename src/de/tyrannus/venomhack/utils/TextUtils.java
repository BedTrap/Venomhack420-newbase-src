package de.tyrannus.venomhack.utils;

import de.tyrannus.venomhack.Venomhack;
import java.awt.Color;
import java.util.List;
import net.minecraft.item.ItemStack;
import net.minecraft.text.Text;
import net.minecraft.text.Style;
import net.minecraft.text.MutableText;
import net.minecraft.text.TextColor;
import net.minecraft.text.OrderedText;

public class TextUtils extends Utils {
   public static MutableText parseText(OrderedText text) {
      MutableText parsed = Text.empty();
      text.accept((i, style, codePoint) -> {
         parsed.append(Text.literal(new String(Character.toChars(codePoint))).setStyle(style));
         return true;
      });
      return parsed;
   }

   public static String parseName(String name) {
      StringBuilder builder = new StringBuilder();
      boolean up = false;
      char[] chars = name.toCharArray();

      for(int i = 0; i < chars.length; ++i) {
         if (i != 0 && !up) {
            builder.append(switch(chars[i]) {
               case ' ', '-', '_' -> {
                  up = true;
                  yield ' ';
               }
               default -> chars[i];
            });
         } else {
            builder.append(Character.toUpperCase(chars[i]));
            up = false;
         }
      }

      return builder.toString();
   }

   public static String inverseParse(String name) {
      return name.toLowerCase().replaceAll(" ", "-");
   }

   public static String getGrammar(int number) {
      String digit = Integer.toString(number);
      int length = digit.length();
      if (length > 1 && digit.charAt(length - 2) == '1') {
         return "th";
      } else {
         return switch(digit.charAt(length - 1)) {
            case '1' -> "st";
            case '2' -> "nd";
            case '3' -> "rd";
            default -> "th";
         };
      }
   }

   public static MutableText coloredTxt(String text, Color color) {
      return Text.literal(text).setStyle(Style.EMPTY.withColor(TextColor.fromRgb(color.getRGB())));
   }

   public static MutableText coloredTxt(String text, int color) {
      return Text.literal(text).setStyle(Style.EMPTY.withColor(color));
   }

   public static String addBackSlashes(String string) {
      return string.isEmpty() ? "" : "\\" + string;
   }

   public static String getNewMessage(List<String> messages) {
      String msg = messages.get(Utils.RANDOM.nextInt(0, messages.size()));
      return msg.equals(Analyser.getLastMessage()) && messages.size() > 1 ? getNewMessage(messages) : msg;
   }

   public static void sendNewMessage(List<String> messages) {
      sendNewMessage(getNewMessage(messages));
   }

   public static void sendNewMessage(String message) {
      sendNewMessage(message, false);
   }

   public static void sendNewMessage(String message, boolean command) {
      if (command) {
         Venomhack.mc.player.sendCommand(message, null);
      } else {
         Venomhack.mc.player.sendChatMessage(message, null);
      }
   }

   public static float getDurabilityInPercent(ItemStack stack) {
      return 100.0F * (float)(stack.getMaxDamage() - stack.getDamage()) / (float)stack.getMaxDamage();
   }
}
