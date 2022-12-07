package de.tyrannus.venomhack.modules.movement;

import de.tyrannus.venomhack.events.PacketEvent;
import de.tyrannus.venomhack.modules.Module;
import meteordevelopment.orbit.EventHandler;
import net.minecraft.network.Packet;
import net.minecraft.network.packet.c2s.play.PlayerMoveC2SPacket;

public class Nofall extends Module {
   public Nofall() {
      super(Module.Categories.MOVEMENT, "no-fall", "no fall damage L for them");
   }

   @EventHandler
   private void onSend(PacketEvent.Send event) {
      Packet var3 = event.getPacket();
      if (var3 instanceof PlayerMoveC2SPacket packet && !(mc.player.fallDistance < 3.0F)) {
         packet.onGround = true;
         return;
      }
   }
}
