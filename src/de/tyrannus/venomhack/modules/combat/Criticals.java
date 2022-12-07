package de.tyrannus.venomhack.modules.combat;

import de.tyrannus.venomhack.events.PacketEvent;
import de.tyrannus.venomhack.modules.Module;
import de.tyrannus.venomhack.settings.Setting;
import de.tyrannus.venomhack.utils.Utils;
import meteordevelopment.orbit.EventHandler;
import net.minecraft.entity.effect.StatusEffects;
import net.minecraft.network.Packet;
import net.minecraft.network.packet.c2s.play.PlayerInteractEntityC2SPacket;
import net.minecraft.network.packet.c2s.play.ClientCommandC2SPacket;
import net.minecraft.network.packet.c2s.play.PlayerInteractEntityC2SPacket.class_5907;
import net.minecraft.network.packet.c2s.play.PlayerMoveC2SPacket.class_2829;
import net.minecraft.network.packet.c2s.play.ClientCommandC2SPacket.class_2849;

public class Criticals extends Module {
   private final Setting<Boolean> ncpBypass = this.setting("bypass", "Sends a specific sequence of packets to bypass ncp.", Boolean.valueOf(false));
   private final Setting<Boolean> disableSprint = this.setting("disable-sprint", "Disables and reenables sprinting to always crit.", Boolean.valueOf(false));
   private boolean wasSprinting = false;

   public Criticals() {
      super(Module.Categories.COMBAT, "criticals", "Lands critical hits on every hit.");
   }

   @EventHandler
   private void onSend(PacketEvent.Send event) {
      if (!Utils.isNull()) {
         Packet var3 = event.getPacket();
         if (!(var3 instanceof PlayerInteractEntityC2SPacket packet) || packet.type.getType() != class_5907.ATTACK) {
            return;
         }

         if (!((double)mc.player.getAttackCooldownProgress(0.5F) <= 0.9)) {
            if (!mc.player.isClimbing()
               && !mc.player.isTouchingWater()
               && !mc.player.hasStatusEffect(StatusEffects.BLINDNESS)
               && !mc.player.hasVehicle()) {
               this.wasSprinting = this.disableSprint.get() && mc.player.isSprinting();
               if (this.wasSprinting) {
                  sendPacket(new ClientCommandC2SPacket(mc.player, class_2849.STOP_SPRINTING));
               }

               if (!this.isVanillaCrit()) {
                  if (this.ncpBypass.get()) {
                     this.spoofPlayerPos(0.11);
                     this.spoofPlayerPos(0.1100013579);
                     this.spoofPlayerPos(1.3579E-6);
                  } else {
                     this.spoofPlayerPos(0.0625);
                     this.spoofPlayerPos(0.0);
                  }
               }
            }
         }
      }
   }

   @EventHandler
   private void onSent(PacketEvent.Sent event) {
      if (event.getPacket() instanceof PlayerInteractEntityC2SPacket && this.wasSprinting) {
         sendPacket(new ClientCommandC2SPacket(mc.player, class_2849.START_SPRINTING));
         this.wasSprinting = false;
      }
   }

   private void spoofPlayerPos(double up) {
      sendPacket(new class_2829(mc.player.getX(), mc.player.getY() + up, mc.player.getZ(), false));
   }

   private boolean isVanillaCrit() {
      return mc.player.fallDistance > 0.0F && !mc.player.isOnGround();
   }
}
