package de.tyrannus.venomhack.modules.chat;

import de.tyrannus.venomhack.events.PlayerListChangeEvent;
import de.tyrannus.venomhack.events.TickEvent;
import de.tyrannus.venomhack.modules.Module;
import de.tyrannus.venomhack.settings.Setting;
import de.tyrannus.venomhack.utils.Analyser;
import de.tyrannus.venomhack.utils.ChatUtils;
import de.tyrannus.venomhack.utils.TextUtils;
import meteordevelopment.orbit.EventHandler;

public class LogDetection extends Module {
   private final Setting<Integer> delay = this.setting("delay", "Minimum ticks between sending messages.", Integer.valueOf(5));
   private final Setting<String> message = this.setting(
      "message", "The message to be send to make fun of your victims.", "LMAO {player} just logged. Venomhack owns me and all!"
   );
   private int delayLeft;

   public LogDetection() {
      super(Module.Categories.CHAT, "log-detection", "Sends a message when someone combat logs.");
   }

   @Override
   protected void onEnable() {
      this.delayLeft = 0;
   }

   @EventHandler
   private void onLeave(PlayerListChangeEvent.Leave event) {
      if (this.delayLeft <= 0) {
         if (Analyser.isTarget(event.getPlayer())) {
            if (this.message.get().isEmpty()) {
               ChatUtils.info("You have no message set.");
               return;
            }

            TextUtils.sendNewMessage(this.message.get().replace("{player}", event.getPlayer().getProfile().getName()));
            this.delayLeft = this.delay.get();
         }
      }
   }

   @EventHandler
   private void onPostTick(TickEvent.Post event) {
      --this.delayLeft;
   }
}
