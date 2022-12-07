package de.tyrannus.venomhack.events;

public class SendMessageEvent extends Cancellable {
   private static final SendMessageEvent INSTANCE = new SendMessageEvent();
   private String message;
   private boolean command;

   public static SendMessageEvent get(String message, boolean command) {
      INSTANCE.message = message;
      INSTANCE.command = command;
      INSTANCE.setCancelled(false);
      return INSTANCE;
   }

   public boolean isCommand() {
      return this.command;
   }

   public String getMessage() {
      return this.message;
   }

   public void setMessage(String message) {
      this.message = message;
   }
}
