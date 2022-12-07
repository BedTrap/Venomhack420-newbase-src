package de.tyrannus.venomhack.modules.render.hud.elements;

import de.tyrannus.venomhack.events.PacketEvent;
import de.tyrannus.venomhack.events.TickEvent;
import de.tyrannus.venomhack.gui.HudEditorScreen;
import de.tyrannus.venomhack.utils.MathUtil;
import meteordevelopment.orbit.EventHandler;

public class LagNotifierHud extends TextHudElement {
   private int sinceLastPacketReceived;

   public LagNotifierHud() {
      super("lag-notifier-hud", "Says Sheeeeeiiiiittttt whenever the server lags.", "Sheeeeeiiiiittttt: ", "69", 40, 40);
   }

   @EventHandler
   private void onPacket(PacketEvent.Receive event) {
      this.sinceLastPacketReceived = 0;
   }

   @EventHandler
   private void onTick(TickEvent.Pre event) {
      ++this.sinceLastPacketReceived;
   }

   @Override
   public String getLeftText() {
      if (mc.currentScreen instanceof HudEditorScreen) {
         return this.getParsedName();
      } else {
         return this.sinceLastPacketReceived < 20 ? "" : this.leftText;
      }
   }

   @Override
   public String getRightText() {
      return this.sinceLastPacketReceived < 20 ? "" : String.valueOf(MathUtil.round((double)this.sinceLastPacketReceived * 0.05, 1));
   }
}
