package de.tyrannus.venomhack.modules.render.hud.elements;

import de.tyrannus.venomhack.settings.Setting;

public class WelcomeHud extends TextHudElement {
   private final Setting<String> msg = this.setting("msg", "The message to display.", "Welcome {player}.");

   public WelcomeHud() {
      super("welcome-hud", "Displays a welcome message.", "welcome", "", 80, 0);
   }

   @Override
   public String getLeftText() {
      return this.msg.get().replaceAll("\\{player}", mc.getSession().getUsername());
   }
}
