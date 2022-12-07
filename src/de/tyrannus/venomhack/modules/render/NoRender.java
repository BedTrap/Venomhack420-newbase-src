package de.tyrannus.venomhack.modules.render;

import de.tyrannus.venomhack.modules.Module;
import de.tyrannus.venomhack.settings.Setting;

public class NoRender extends Module {
   public final Setting<Boolean> damage = this.setting("no-hurt-cam", "Disables damage effect.", Boolean.valueOf(false));
   public final Setting<Boolean> totem = this.setting("totems", "Disables totem animation.", Boolean.valueOf(false));
   public final Setting<Boolean> fire = this.setting("fire", "Disables fire.", Boolean.valueOf(false));
   public final Setting<Boolean> fog = this.setting("fog", "Disables fog.", Boolean.valueOf(false));
   public final Setting<Boolean> weather = this.setting("weather", "Disables weather.", Boolean.valueOf(false));

   public NoRender() {
      super(Module.Categories.RENDER, "no-render", "Prevents certain aspects of the game from rendering.");
   }
}
