package de.tyrannus.venomhack.modules.movement;

import de.tyrannus.venomhack.modules.Module;
import de.tyrannus.venomhack.settings.Setting;

public class Moses extends Module {
   public final Setting<Boolean> lava = this.setting("lava", "Applies to lava too.", Boolean.valueOf(true));

   public Moses() {
      super(Module.Categories.MOVEMENT, "moses", "Lets you walk through liquids as if it was air.");
   }
}
