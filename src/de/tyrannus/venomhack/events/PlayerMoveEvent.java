package de.tyrannus.venomhack.events;

public class PlayerMoveEvent {
   public static class Post extends PlayerMoveEvent {
      private static final PlayerMoveEvent.Post INSTANCE = new PlayerMoveEvent.Post();

      private Post() {
      }

      public static PlayerMoveEvent.Post get() {
         return INSTANCE;
      }
   }

   public static class Pre extends PlayerMoveEvent {
      private static final PlayerMoveEvent.Pre INSTANCE = new PlayerMoveEvent.Pre();

      private Pre() {
      }

      public static PlayerMoveEvent.Pre get() {
         return INSTANCE;
      }
   }
}
