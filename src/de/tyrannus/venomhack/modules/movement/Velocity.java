package de.tyrannus.venomhack.modules.movement;

import de.tyrannus.venomhack.events.PacketEvent;
import de.tyrannus.venomhack.mixins.EntityVelocityUpdateS2CPacketAccessor;
import de.tyrannus.venomhack.mixins.ExplosionS2CPacketAccessor;
import de.tyrannus.venomhack.modules.Module;
import de.tyrannus.venomhack.settings.Setting;
import meteordevelopment.orbit.EventHandler;
import net.minecraft.network.Packet;
import net.minecraft.network.packet.s2c.play.ExplosionS2CPacket;
import net.minecraft.network.packet.s2c.play.EntityVelocityUpdateS2CPacket;

public class Velocity extends Module {
   private final Setting<Float> amountX = this.setting("x", "How much velocity should be taken on the X axis.", Float.valueOf(0.0F), 0.0F, 1.0F);
   private final Setting<Float> amountY = this.setting("y", "How much velocity should be taken on the Y axis.", Float.valueOf(0.0F), 0.0F, 1.0F);
   private final Setting<Float> amountZ = this.setting("z", "How much velocity should be taken on the Z axis.", Float.valueOf(0.0F), 0.0F, 1.0F);
   public final Setting<Boolean> entities = this.setting("entities", "Whether or not to disable entity push.", Boolean.valueOf(true));
   public final Setting<Boolean> blocks = this.setting("blocks", "Whether or not to disable block push.", Boolean.valueOf(true));

   public Velocity() {
      super(Module.Categories.MOVEMENT, "Velocity", "Modifies knockback values.");
   }

   @EventHandler
   public void readPacket(PacketEvent.Receive event) {
      if (mc.player != null && mc.world != null) {
         Packet velX = event.getPacket();
         if (velX instanceof EntityVelocityUpdateS2CPacket packet) {
            if (packet.getId() == mc.player.getId()) {
               double velXx = ((double)packet.getVelocityX() / 8000.0 - mc.player.getVelocity().x) * (double)this.amountX.get().floatValue();
               double velY = ((double)packet.getVelocityY() / 8000.0 - mc.player.getVelocity().y) * (double)this.amountY.get().floatValue();
               double velZ = ((double)packet.getVelocityZ() / 8000.0 - mc.player.getVelocity().z) * (double)this.amountZ.get().floatValue();
               ((EntityVelocityUpdateS2CPacketAccessor)packet).setX((int)(velXx * 8000.0 + mc.player.getVelocity().x * 8000.0));
               ((EntityVelocityUpdateS2CPacketAccessor)packet).setY((int)(velY * 8000.0 + mc.player.getVelocity().y * 8000.0));
               ((EntityVelocityUpdateS2CPacketAccessor)packet).setZ((int)(velZ * 8000.0 + mc.player.getVelocity().z * 8000.0));
            }
         } else {
            velX = event.getPacket();
            if (velX instanceof ExplosionS2CPacket packet) {
               ((ExplosionS2CPacketAccessor)packet).setPlayerVelocityX(packet.getPlayerVelocityX() * this.amountX.get());
               ((ExplosionS2CPacketAccessor)packet).setPlayerVelocityY(packet.getPlayerVelocityY() * this.amountY.get());
               ((ExplosionS2CPacketAccessor)packet).setPlayerVelocityZ(packet.getPlayerVelocityZ() * this.amountZ.get());
            }
         }
      }
   }
}
