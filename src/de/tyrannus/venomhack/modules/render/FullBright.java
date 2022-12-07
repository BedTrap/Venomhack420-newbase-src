package de.tyrannus.venomhack.modules.render;

import de.tyrannus.venomhack.modules.Module;

public class FullBright extends Module {
   private double previousGamma = 1.0;

   public FullBright() {
      super(Module.Categories.RENDER, "fullbright", "Allows you to see in darkness.");
   }

   @Override
   protected void onEnable() {
      this.previousGamma = mc.options.getGamma().getValue();
      mc.options.getGamma().setValue(16.0);
   }

   @Override
   protected void onDisable() {
      mc.options.getGamma().setValue(this.previousGamma);
   }
}
