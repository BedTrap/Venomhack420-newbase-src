package de.tyrannus.venomhack.modules.misc;

import de.tyrannus.venomhack.modules.Module;
import de.tyrannus.venomhack.settings.Setting;

public class Timer extends Module {
   private final Setting<Float> factor = this.setting("factor", "Multiplier for the base game speed.", Float.valueOf(1.0F));

   public Timer() {
      super(Module.Categories.MISC, "timer", "Speeds up or slows down your client side game.");
   }

   public float factor() {
      return this.isActive() ? this.factor.get() : 1.0F;
   }

   @Override
   public String getArrayText() {
      return String.valueOf(this.factor.get());
   }
}
