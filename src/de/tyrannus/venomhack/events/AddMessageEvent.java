package de.tyrannus.venomhack.events;

import net.minecraft.text.Text;

public class AddMessageEvent extends Cancellable {
   private static final AddMessageEvent INSTANCE = new AddMessageEvent();
   private Text message;
   private int ticks;

   public static AddMessageEvent get(Text message, int ticks) {
      INSTANCE.message = message;
      INSTANCE.ticks = ticks;
      INSTANCE.setCancelled(false);
      return INSTANCE;
   }

   public Text getMessage() {
      return this.message;
   }

   public int getTicks() {
      return this.ticks;
   }

   public void setMessage(Text message) {
      this.message = message;
   }
}
