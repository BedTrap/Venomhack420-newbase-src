package de.tyrannus.venomhack.modules.misc;

import de.tyrannus.venomhack.events.PacketEvent;
import de.tyrannus.venomhack.mixins.PlayerPositionLookS2CPacketAccessor;
import de.tyrannus.venomhack.modules.Module;
import meteordevelopment.orbit.EventHandler;
import net.minecraft.network.packet.s2c.play.PlayerPositionLookS2CPacket;

public class NoRotate extends Module {
   public NoRotate() {
      super(Module.Categories.MISC, "no rotate", "stops rotations");
   }

   @EventHandler
   private void onReceive(PacketEvent.Receive event) {
      if (mc.player != null && event.getPacket() instanceof PlayerPositionLookS2CPacket) {
         ((PlayerPositionLookS2CPacketAccessor)event.getPacket()).setPitch(mc.player.getPitch());
         ((PlayerPositionLookS2CPacketAccessor)event.getPacket()).setYaw(mc.player.getYaw());
      }
   }
}
