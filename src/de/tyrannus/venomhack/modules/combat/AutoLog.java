package de.tyrannus.venomhack.modules.combat;

import de.tyrannus.venomhack.events.TickEvent;
import de.tyrannus.venomhack.modules.Module;
import de.tyrannus.venomhack.settings.Setting;
import de.tyrannus.venomhack.utils.inventory.InvUtils;
import de.tyrannus.venomhack.utils.players.PlayerUtils;
import meteordevelopment.orbit.EventHandler;
import net.minecraft.item.Items;
import net.minecraft.text.Text;
import net.minecraft.network.packet.s2c.play.DisconnectS2CPacket;

public class AutoLog extends Module {
   private final Setting<Integer> hpLimit = this.setting("health", "The health threshold at which to log.", Integer.valueOf(6), 1.0F, 36.0F);
   private final Setting<Boolean> totemLog = this.setting("totem-log", "Logs when you run out of totems.", Boolean.valueOf(false));
   private final Setting<Boolean> toggle = this.setting("toggle", "Toggles the module off after doing the disconnect.", Boolean.valueOf(true));

   public AutoLog() {
      super(Module.Categories.COMBAT, "auto-log", "Automatically disconnects when you fall below the set health.");
   }

   @EventHandler
   public void onTick(TickEvent.Pre event) {
      if (mc.interactionManager.getCurrentGameMode().isSurvivalLike()) {
         if (this.totemLog.get() && InvUtils.count(Items.TOTEM_OF_UNDYING) < 1) {
            mc.player.networkHandler.onDisconnect(new DisconnectS2CPacket(Text.literal("[AutoLog] No totems left.")));
            if (this.toggle.get()) {
               this.toggle(false);
            }
         } else {
            if (PlayerUtils.getTotalHealth() <= (float)this.hpLimit.get().intValue()) {
               mc.player.networkHandler.onDisconnect(new DisconnectS2CPacket(Text.literal("[AutoLog] Your health was below " + this.hpLimit.get() + ".")));
               if (this.toggle.get()) {
                  this.toggle(false);
               }
            }
         }
      }
   }
}
