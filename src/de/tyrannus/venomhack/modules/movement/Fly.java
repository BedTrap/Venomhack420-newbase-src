package de.tyrannus.venomhack.modules.movement;

import de.tyrannus.venomhack.events.PacketEvent;
import de.tyrannus.venomhack.modules.Module;
import de.tyrannus.venomhack.utils.Utils;
import meteordevelopment.orbit.EventHandler;
import net.minecraft.network.packet.c2s.play.UpdatePlayerAbilitiesC2SPacket;

public class Fly extends Module {
   public Fly() {
      super(Module.Categories.MOVEMENT, "fly", "Allows you to fly in survival mode.");
   }

   @EventHandler
   private void onPacketSend(PacketEvent.Send event) {
      if (event.getPacket() instanceof UpdatePlayerAbilitiesC2SPacket) {
         event.cancel();
      }
   }

   @Override
   protected void onEnable() {
      mc.player.getAbilities().flying = true;
   }

   @Override
   protected void onDisable() {
      if (!Utils.isNull() && mc.interactionManager.getCurrentGameMode().isSurvivalLike()) {
         mc.player.getAbilities().flying = false;
      }
   }
}
