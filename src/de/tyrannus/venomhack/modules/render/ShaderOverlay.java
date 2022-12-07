package de.tyrannus.venomhack.modules.render;

import de.tyrannus.venomhack.modules.Module;
import de.tyrannus.venomhack.settings.Setting;
import net.minecraft.util.Identifier;

public class ShaderOverlay extends Module {
   private final Setting<ShaderOverlay.Mobs> mob = this.setting("shader", "The shader to overlay.", ShaderOverlay.Mobs.CREEPER, this::onChanged);

   public ShaderOverlay() {
      super(Module.Categories.RENDER, "shader-overlay", "Renders overlays of some shaders.");
   }

   private void onChanged(ShaderOverlay.Mobs mob) {
      if (this.isActive() && mc.gameRenderer != null) {
         mc.gameRenderer.loadShader(mob.identifier);
      }
   }

   @Override
   public void onEnable() {
      this.onChanged(this.mob.get());
   }

   @Override
   public void onDisable() {
      if (mc.gameRenderer.getShader() != null) {
         mc.gameRenderer.getShader().close();
      }
   }

   @Override
   public String getArrayText() {
      return this.mob.get().toString();
   }

   private static enum Mobs {
      CREEPER(new Identifier("shaders/post/creeper.json")),
      SPIDER(new Identifier("shaders/post/spider.json")),
      ENDER_MAN(new Identifier("shaders/post/invert.json")),
      PHOSPHOR(new Identifier("shaders/post/phosphor.json")),
      PENCIL(new Identifier("shaders/post/pencil.json")),
      NOTCH(new Identifier("shaders/post/notch.json"));

      public final Identifier identifier;

      private Mobs(Identifier identifier) {
         this.identifier = identifier;
      }
   }
}
