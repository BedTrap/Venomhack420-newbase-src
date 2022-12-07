package de.tyrannus.venomhack.events;

public class TickEvent {
   private TickEvent() {
   }

   public static class Post extends TickEvent {
      protected static final TickEvent.Post INSTANCE = new TickEvent.Post();

      private Post() {
      }

      public static TickEvent.Post get() {
         return INSTANCE;
      }
   }

   public static class Pre extends TickEvent {
      protected static final TickEvent.Pre INSTANCE = new TickEvent.Pre();

      private Pre() {
      }

      public static TickEvent.Pre get() {
         return INSTANCE;
      }
   }
}
