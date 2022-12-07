package de.tyrannus.venomhack.modules.misc;

import de.tyrannus.venomhack.events.TickEvent;
import de.tyrannus.venomhack.mixins.EntityAccessor;
import de.tyrannus.venomhack.modules.Module;
import meteordevelopment.orbit.EventHandler;

public class Portals extends Module {
   public Portals() {
      super(Module.Categories.MISC, "portals", "Allows GUI's to still be accessible in portals.");
   }

   @EventHandler
   private void onTick(TickEvent.Post event) {
      ((EntityAccessor)mc.player).setInNetherPortal(false);
   }
}
