package de.tyrannus.venomhack.modules.render.hud.elements;

import net.minecraft.client.MinecraftClient;

public class FpsHud extends TextHudElement {
   public FpsHud() {
      super("fps-hud", "Displays your fps.", "FPS: ", "420", 40, 0);
   }

   @Override
   public String getRightText() {
      return String.valueOf(MinecraftClient.currentFps);
   }
}
