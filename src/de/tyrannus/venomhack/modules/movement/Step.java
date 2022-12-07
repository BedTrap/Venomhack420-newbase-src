package de.tyrannus.venomhack.modules.movement;

import de.tyrannus.venomhack.events.PlayerMoveEvent;
import de.tyrannus.venomhack.modules.Module;
import de.tyrannus.venomhack.settings.Setting;
import meteordevelopment.orbit.EventHandler;
import net.minecraft.util.math.Box;

public class Step extends Module {
   private final Setting<Float> height = this.setting("height", "How many blocks you can step.", Float.valueOf(1.0F), this::changeHeight, 0.6F, 7.0F);
   private final Setting<Step.StepMode> mode = this.setting(
      "mode", "The mode to use for stepping. Note that NCP is experimental.", Step.StepMode.VANILLA, value -> this.changeHeight(this.height.get())
   );
   private final Setting<Boolean> sneak = this.setting("sneak", "Whether to allow step while sneaking or not.", Boolean.valueOf(false));

   public Step() {
      super(Module.Categories.MOVEMENT, "step", "Allows you walk up blocks as if they were stairs.");
   }

   @EventHandler
   private void onTick(PlayerMoveEvent.Pre event) {
      if (!this.sneak.get() && mc.player.isSneaking()) {
         mc.player.stepHeight = 0.6F;
      } else {
         this.changeHeight(this.height.get());
         if (mc.player.horizontalCollision && !mc.player.isHoldingOntoLadder() && mc.player.isOnGround() && this.mode.get() == Step.StepMode.NCP) {
            Box box = mc.player.getBoundingBox().offset(0.0, (double)(this.height.get() + 1.0F), 0.0);
            if (mc.world.isSpaceEmpty(box.expand(0.05, 1.0, 0.0)) || !mc.world.isSpaceEmpty(box.expand(0.0, 1.0, 0.05))) {
               mc.player.updatePosition(mc.player.getX(), mc.player.getY() + 1.0, mc.player.getZ());
            }
         }
      }
   }

   private void changeHeight(float value) {
      if (mc.player != null) {
         if (this.mode.get() == Step.StepMode.VANILLA) {
            mc.player.stepHeight = value;
         } else {
            mc.player.stepHeight = 0.6F;
         }
      }
   }

   @Override
   protected void onDisable() {
      this.changeHeight(0.6F);
   }

   @Override
   public String getArrayText() {
      return Float.toString(this.height.get());
   }

   private static enum StepMode {
      VANILLA,
      NCP;
   }
}
