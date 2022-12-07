package de.tyrannus.venomhack.utils;

import de.tyrannus.venomhack.Venomhack;
import de.tyrannus.venomhack.modules.Modules;
import de.tyrannus.venomhack.modules.chat.ChatControl;
import java.awt.Color;
import net.minecraft.text.Text;
import net.minecraft.text.MutableText;

public class ChatUtils {
   public static void sendMsg(String msg) {
      Venomhack.mc.inGameHud.getChatHud().addMessage(Text.literal(msg));
   }

   public static void sendMsg(Text text) {
      Venomhack.mc.inGameHud.getChatHud().addMessage(text);
   }

   public static void info(String msg) {
      Venomhack.mc.inGameHud.getChatHud().addMessage(prefixText().append(TextUtils.coloredTxt(msg, Color.WHITE)));
   }

   public static void info(Text text) {
      Venomhack.mc.inGameHud.getChatHud().addMessage(prefixText().append(text));
   }

   public static MutableText prefixText() {
      return Modules.get(ChatControl.class).getPrefix();
   }
}
