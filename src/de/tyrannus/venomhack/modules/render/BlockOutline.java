package de.tyrannus.venomhack.modules.render;

import de.tyrannus.venomhack.events.RenderEvent;
import de.tyrannus.venomhack.modules.Module;
import de.tyrannus.venomhack.settings.Setting;
import de.tyrannus.venomhack.utils.render.RenderMode;
import de.tyrannus.venomhack.utils.render.RenderUtils;
import java.awt.Color;
import meteordevelopment.orbit.EventHandler;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.hit.HitResult;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.hit.HitResult.class_240;

public class BlockOutline extends Module {
   public final Setting<Boolean> vanilla = this.setting("vanilla", "Only changes vanillas outline color.", Boolean.valueOf(false));
   private final Setting<RenderMode> mode = this.setting("render-mode", "How the outline will be rendered.", RenderMode.BOTH, () -> !this.vanilla.get());
   private final Setting<Float> lineWidth = this.setting("line-width", "The line width.", Float.valueOf(1.5F), () -> this.mode.get().lines(), 0.0F, 5.0F);
   public final Setting<Color> lineColor = this.setting(
      "line-color", "The line color.", new Color(255, 255, 255, 255), () -> this.mode.get().lines() || this.vanilla.get()
   );
   private final Setting<Color> sideColor = this.setting(
      "side-color", "The side color.", new Color(255, 255, 255, 80), () -> this.mode.get().sides() && !this.vanilla.get()
   );

   public BlockOutline() {
      super(Module.Categories.RENDER, "block-outline", "Allows you to customise selected block rendering.");
   }

   @EventHandler
   private void onRender(RenderEvent.Flat event) {
      if (!this.vanilla.get()) {
         HitResult block = mc.crosshairTarget;
         if (block instanceof BlockHitResult result && result.getType() != class_240.MISS) {
            BlockPos blockx = result.getBlockPos();
            RenderUtils.drawBlock(this.mode.get(), blockx, this.lineWidth.get(), this.lineColor.get(), this.sideColor.get(), false);
            return;
         }
      }
   }
}
