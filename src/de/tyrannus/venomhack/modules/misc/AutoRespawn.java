package de.tyrannus.venomhack.modules.misc;

import de.tyrannus.venomhack.Venomhack;
import de.tyrannus.venomhack.events.OpenScreenEvent;
import de.tyrannus.venomhack.events.PlayerDeathEvent;
import de.tyrannus.venomhack.events.TickEvent;
import de.tyrannus.venomhack.modules.Module;
import de.tyrannus.venomhack.settings.Setting;
import de.tyrannus.venomhack.utils.Analyser;
import meteordevelopment.orbit.EventHandler;
import net.minecraft.network.packet.c2s.play.ClientStatusC2SPacket;
import net.minecraft.client.gui.screen.DeathScreen;
import net.minecraft.network.packet.c2s.play.ClientStatusC2SPacket.class_2800;

public class AutoRespawn extends Module {
   private final Setting<Integer> delay = this.setting("delay", "How long to wait in seconds to perform the respawn.", Integer.valueOf(0));
   private final Setting<Boolean> kit = this.setting("auto-rekit", "yea", Boolean.valueOf(true));
   private final Setting<String> kitname = this.setting("kit-name", "", "cpvp", this.kit::get);
   int delayLeft = 0;

   public AutoRespawn() {
      super(Module.Categories.MISC, "auto-respawn", "Useless module because Venomhack users never die.");
   }

   @Override
   protected void onEnable() {
      this.delayLeft = 0;
   }

   @EventHandler
   public void onScreen(OpenScreenEvent event) {
      if (event.screen instanceof DeathScreen) {
         if (this.delay.get() == 0) {
            this.respawn();
            Analyser.setDead();
            Venomhack.EVENTS.post(PlayerDeathEvent.get(mc.player, Analyser.totemPops.removeInt(mc.player.getUuid()), false, false));
            event.cancel();
         } else {
            this.delayLeft = this.delay.get() * 20 + 20;
         }
      }
   }

   @EventHandler
   private void onTick(TickEvent.Pre event) {
      --this.delayLeft;
      if ((this.delayLeft == 0 || this.delay.get() == 0) && mc.player != null && mc.player.isDead()) {
         this.respawn();
      }
   }

   private void respawn() {
      sendPacket(new ClientStatusC2SPacket(class_2800.PERFORM_RESPAWN));
      if (this.kit.get()) {
         mc.player.sendCommand("kit " + (String)this.kitname.get());
      }
   }
}
