package de.tyrannus.venomhack.modules.render;

import de.tyrannus.venomhack.modules.Module;
import de.tyrannus.venomhack.settings.Setting;
import java.awt.Color;

public class CustomPops extends Module {
   public final Setting<Float> scale = this.setting("scale", "The particle scale.", Float.valueOf(1.0F), 0.0F, 3.0F);
   public final Setting<Color> color1 = this.setting("color-1", "Color 1.", new Color(255, 0, 0, 255));
   public final Setting<Color> color2 = this.setting("color-2", "Color 2.", new Color(255, 0, 0, 200));
   public final Setting<Color> color3 = this.setting("color-3", "Color 3.", new Color(255, 0, 0, 150));
   public final Setting<Color> color4 = this.setting("color-4", "Color 4.", new Color(255, 0, 0, 100));

   public CustomPops() {
      super(Module.Categories.RENDER, "custom-pops", "Adjusts totem pop particles.");
   }
}
