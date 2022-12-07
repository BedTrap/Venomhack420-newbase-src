package de.tyrannus.venomhack.modules.movement;

import de.tyrannus.venomhack.events.TickEvent;
import de.tyrannus.venomhack.modules.Module;
import de.tyrannus.venomhack.settings.Setting;
import meteordevelopment.orbit.EventHandler;
import net.minecraft.entity.effect.StatusEffects;

public class NoSlow extends Module {
   public final Setting<Boolean> items = this.setting("items", "Makes you go normal speed when eating.", Boolean.valueOf(true));
   public final Setting<Boolean> webs = this.setting("webs", "Removes web slowdown.", Boolean.valueOf(false));
   public final Setting<Boolean> slowFalling = this.setting("slow-falling", "Makes you fall at normal speed with slow falling.", Boolean.valueOf(true));

   public NoSlow() {
      super(Module.Categories.MOVEMENT, "no-slow", "Makes you go faster under certain circumstances.");
   }

   @EventHandler
   private void onTick(TickEvent.Pre event) {
      if (this.items.get()) {
         boolean canSprint = (float)mc.player.getHungerManager().getFoodLevel() > 6.0F || mc.player.getAbilities().allowFlying;
         if ((mc.player.isOnGround() || mc.player.isSubmergedInWater())
            && !mc.player.isSneaking()
            && !mc.player.isSprinting()
            && canSprint
            && !mc.player.hasStatusEffect(StatusEffects.BLINDNESS)
            && mc.options.sprintKey.isPressed()) {
            mc.player.setSprinting(true);
         }

         if (!mc.player.isSprinting()
            && (!mc.player.isTouchingWater() || mc.player.isSubmergedInWater())
            && mc.player.forwardSpeed > 0.0F
            && canSprint
            && !mc.player.hasStatusEffect(StatusEffects.BLINDNESS)
            && mc.options.sprintKey.isPressed()) {
            mc.player.setSprinting(true);
         }
      }
   }
}
