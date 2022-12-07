package de.tyrannus.venomhack.modules.movement;

import de.tyrannus.venomhack.events.TickEvent;
import de.tyrannus.venomhack.modules.Module;
import de.tyrannus.venomhack.settings.Setting;
import de.tyrannus.venomhack.utils.inventory.InvUtils;
import de.tyrannus.venomhack.utils.inventory.ItemPos;
import de.tyrannus.venomhack.utils.world.BlockUtils;
import de.tyrannus.venomhack.utils.world.SwitchMode;
import meteordevelopment.orbit.EventHandler;
import net.minecraft.item.BlockItem;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.hit.BlockHitResult;

public class Scaffold extends Module {
   private final Setting<SwitchMode> switchMode = this.setting("switch-mode", "How to switch to the blocks.", SwitchMode.OLD);
   private final Setting<Boolean> airPlace = this.setting("air-place", "Air places.", Boolean.valueOf(true));
   private final Setting<Boolean> strictDirections = this.setting("strict-directions", "Strict directions.", Boolean.valueOf(false));
   private final Setting<Boolean> rotate = this.setting("rotate", "Look where you are placing server side.", Boolean.valueOf(false));
   private final Setting<Boolean> swing = this.setting("swing", "Swing client side.", Boolean.valueOf(true));

   public Scaffold() {
      super(Module.Categories.MOVEMENT, "scaffold", "Places your held block below you.");
   }

   @EventHandler
   private void onTick(TickEvent.Pre event) {
      ItemPos block = InvUtils.findInHotbar(item -> item instanceof BlockItem);
      if (block.found()) {
         BlockPos pos = mc.player.getBlockPos().down();
         if (mc.world.getBlockState(pos).isAir()) {
            BlockHitResult hitResult = BlockUtils.getPlaceResult(pos, this.airPlace.get(), this.strictDirections.get());
            BlockUtils.justPlace(block, hitResult, this.swing.get(), this.rotate.get(), 100, this.switchMode.get());
         }
      }
   }
}
