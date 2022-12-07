package de.tyrannus.venomhack.modules.render.hud.elements;

public class PingHud extends TextHudElement {
   public PingHud() {
      super("ping-hud", "Displays your ping.", "Ping: ", "0", 60, 0);
   }

   @Override
   public String getRightText() {
      return mc.getNetworkHandler() != null && mc.player != null && mc.getNetworkHandler().getPlayerListEntry(mc.player.getUuid()) != null
         ? String.valueOf(mc.getNetworkHandler().getPlayerListEntry(mc.player.getUuid()).getLatency())
         : "0";
   }
}
