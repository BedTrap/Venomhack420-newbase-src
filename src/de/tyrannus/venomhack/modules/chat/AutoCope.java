package de.tyrannus.venomhack.modules.chat;

import de.tyrannus.venomhack.events.PacketEvent;
import de.tyrannus.venomhack.modules.Module;
import de.tyrannus.venomhack.settings.Setting;
import de.tyrannus.venomhack.utils.TextUtils;
import java.util.List;
import meteordevelopment.orbit.EventHandler;
import net.minecraft.network.Packet;
import net.minecraft.network.packet.s2c.play.DeathMessageS2CPacket;

public class AutoCope extends Module {
   private final Setting<List<String>> messages = this.listSetting("messages", "The messages to send after you died.", new String[]{"Lag?", "I got 0 ticked"});

   public AutoCope() {
      super(Module.Categories.CHAT, "auto-cope", "Automatically sends a cope message in chat when you die.");
   }

   @EventHandler(
      priority = 200
   )
   private void onOpenScreenEvent(PacketEvent.Receive event) {
      Packet var3 = event.getPacket();
      if (var3 instanceof DeathMessageS2CPacket packet) {
         if (packet.getEntityId() == mc.player.getId()) {
            TextUtils.sendNewMessage(this.messages.get());
         }
      }
   }
}
