package de.tyrannus.venomhack.modules.misc;

import de.tyrannus.venomhack.modules.Module;
import de.tyrannus.venomhack.settings.Setting;

public class Reach extends Module {
   public final Setting<Float> reach = this.setting("reach", "How far to reach at max.", Float.valueOf(5.0F));

   public Reach() {
      super(Module.Categories.MISC, "reach", "Allows to set your max reach distance to a custom value.");
   }

   @Override
   public String getArrayText() {
      return String.valueOf(this.reach.get());
   }
}
