package de.tyrannus.venomhack.settings;

import de.tyrannus.venomhack.utils.Utils;
import net.minecraft.client.util.InputUtil;
import net.minecraft.client.util.InputUtil.class_306;

public class Keybind {
   public static final Keybind UNBOUND = new Keybind(InputUtil.UNKNOWN_KEY);
   public static final Keybind BINDING = new Keybind(282);
   private final class_306 key;

   public Keybind(class_306 key) {
      this.key = key;
   }

   public Keybind(int keycode) {
      this.key = InputUtil.fromKeyCode(keycode, keycode);
   }

   public boolean isPressed() {
      return Utils.isKeyPressed(this.key.getCode());
   }

   public int getCode() {
      return this.key.getCode();
   }

   public class_306 getKey() {
      return this.key;
   }
}
