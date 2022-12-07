package de.tyrannus.venomhack.modules.movement;

import de.tyrannus.venomhack.events.TickEvent;
import de.tyrannus.venomhack.modules.Module;
import de.tyrannus.venomhack.settings.Setting;
import meteordevelopment.orbit.EventHandler;

public class Sprint extends Module {
   public final Setting<Boolean> multiDirectional = this.setting("multi-directional", "Sprints in all directions.", Boolean.valueOf(true));
   private final Setting<Boolean> liquids = this.setting("liquids-check", "Won't make you sprint when submerged in liquids.", Boolean.valueOf(false));
   private final Setting<Boolean> foodCheck = this.setting("hunger-check", "Whether or not to check hunger level.", Boolean.valueOf(true));

   public Sprint() {
      super(Module.Categories.MOVEMENT, "sprint", "Makes you sprint all the time.");
   }

   @EventHandler
   public void onTick(TickEvent.Pre event) {
      if (this.canSprint()) {
         if (mc.player.sidewaysSpeed != 0.0F || mc.player.forwardSpeed != 0.0F) {
            mc.player.setSprinting(true);
         }
      }
   }

   public boolean canSprint() {
      return this.active
         && (!this.liquids.get() || !mc.player.isSubmergedInWater() && !mc.player.isInLava())
         && (!this.foodCheck.get() || mc.player.getHungerManager().getFoodLevel() > 6);
   }
}
