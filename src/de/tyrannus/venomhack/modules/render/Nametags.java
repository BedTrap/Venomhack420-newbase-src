package de.tyrannus.venomhack.modules.render;

import de.tyrannus.venomhack.events.RenderEvent;
import de.tyrannus.venomhack.modules.Module;
import de.tyrannus.venomhack.settings.Setting;
import de.tyrannus.venomhack.utils.render.RenderUtils;
import java.awt.Color;
import meteordevelopment.orbit.EventHandler;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.PlayerEntity;

public class Nametags extends Module {
   private final Setting<Boolean> self = this.setting("self", "Shows nametags for yourself.", Boolean.valueOf(false));
   private final Setting<Color> txtColor = this.setting("text-color", "The text color.", new Color(255, 255, 255, 255));
   private final Setting<Color> sideColor = this.setting("background-color", "The background color.", new Color(0, 0, 0, 60));

   public Nametags() {
      super(Module.Categories.RENDER, "name-tags", "Applies more renderings to player nametags.");
   }

   @EventHandler
   private void onRender(RenderEvent.Flat event) {
      for(Entity entity : mc.world.getEntities()) {
         if (entity instanceof PlayerEntity player && (player != mc.player || this.self.get() && mc.gameRenderer.getCamera().isThirdPerson())) {
            RenderUtils.drawEntityTag(event, player, this.txtColor.get(), this.sideColor.get());
         }
      }
   }
}
