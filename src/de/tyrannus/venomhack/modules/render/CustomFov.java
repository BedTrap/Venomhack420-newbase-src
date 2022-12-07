package de.tyrannus.venomhack.modules.render;

import de.tyrannus.venomhack.modules.Module;
import de.tyrannus.venomhack.settings.Setting;

public class CustomFov extends Module {
   private final Setting<Integer> fov = this.setting("fov", "The Fov to use.", Integer.valueOf(130), this::change, 0.0F, 180.0F);
   private int previousFov = 70;

   public CustomFov() {
      super(Module.Categories.RENDER, "custom-fov", "Allows you to increase your fov beyond vanilla limits.");
   }

   @Override
   protected void onEnable() {
      this.previousFov = mc.options.getFov().getValue();
      mc.options.getFov().setValue(this.fov.get());
   }

   private void change(int value) {
      if (this.isActive() && mc.options != null) {
         mc.options.getFov().setValue(value);
      }
   }

   @Override
   public String getArrayText() {
      return String.valueOf(this.fov.get());
   }

   @Override
   public void onDisable() {
      mc.options.getFov().setValue(this.previousFov);
   }
}
