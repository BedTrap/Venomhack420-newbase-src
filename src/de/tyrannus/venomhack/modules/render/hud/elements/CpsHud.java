package de.tyrannus.venomhack.modules.render.hud.elements;

import de.tyrannus.venomhack.events.TickEvent;
import de.tyrannus.venomhack.utils.inventory.PerSecondCounter;
import meteordevelopment.orbit.EventHandler;
import net.minecraft.item.Items;

public class CpsHud extends TextHudElement {
   private final PerSecondCounter cps = new PerSecondCounter();

   public CpsHud() {
      super("cps-hud", "Shows how many crystals per second you get.", "CPS: ", "20", 20, 0);
   }

   @EventHandler
   private void onTick(TickEvent.Pre event) {
      this.cps.increment(Items.END_CRYSTAL);
   }

   @Override
   public String getRightText() {
      return String.valueOf(Math.max(0, this.cps.get()));
   }
}
