package de.tyrannus.venomhack.modules.render;

import de.tyrannus.venomhack.events.PlayerDeathEvent;
import de.tyrannus.venomhack.modules.Module;
import meteordevelopment.orbit.EventHandler;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.LightningEntity;
import net.minecraft.util.math.Vec3d;

public class KillFx extends Module {
   public KillFx() {
      super(Module.Categories.RENDER, "kill-effects", "Become thor.");
   }

   @EventHandler
   private void onDeath(PlayerDeathEvent event) {
      if (event.isTarget()) {
         Vec3d pos = event.getPlayer().getPos();
         LightningEntity bolt = new LightningEntity(EntityType.LIGHTNING_BOLT, mc.world);
         bolt.setPosition(pos);
         mc.world.addEntity(93, bolt);
      }
   }
}
