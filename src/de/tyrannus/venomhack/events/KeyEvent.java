package de.tyrannus.venomhack.events;

import net.minecraft.client.option.KeyBinding;
import net.minecraft.client.util.InputUtil.class_306;

public class KeyEvent {
   protected class_306 key;
   protected int action;
   protected boolean mouse;

   private KeyEvent() {
   }

   public class_306 getKey() {
      return this.key;
   }

   public int getAction() {
      return this.action;
   }

   public boolean isMouse() {
      return this.mouse;
   }

   public boolean matchesBind(KeyBinding bind) {
      return bind.matchesMouse(this.key.getCode()) || bind.matchesKey(this.key.getCode(), this.key.getCode());
   }

   public static final class Post extends KeyEvent {
      private static final KeyEvent.Post INSTANCE = new KeyEvent.Post();

      private Post() {
      }

      public static KeyEvent.Post get(class_306 key, int action, boolean mouse) {
         INSTANCE.key = key;
         INSTANCE.action = action;
         INSTANCE.mouse = mouse;
         return INSTANCE;
      }
   }

   public static final class Pre extends KeyEvent {
      private static final KeyEvent.Pre INSTANCE = new KeyEvent.Pre();

      private Pre() {
      }

      public static KeyEvent.Pre get(class_306 key, int action, boolean mouse) {
         INSTANCE.key = key;
         INSTANCE.action = action;
         INSTANCE.mouse = mouse;
         return INSTANCE;
      }
   }
}
