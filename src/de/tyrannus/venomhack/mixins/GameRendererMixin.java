package de.tyrannus.venomhack.mixins;

import de.tyrannus.venomhack.modules.Modules;
import de.tyrannus.venomhack.modules.misc.NoMiningTrace;
import de.tyrannus.venomhack.modules.render.NoRender;
import net.minecraft.item.ItemStack;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.client.render.GameRenderer;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.At.Shift;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin({GameRenderer.class})
public class GameRendererMixin {
   @Inject(
      method = {"updateTargetedEntity"},
      at = {@At(
   value = "FIELD",
   target = "Lnet/minecraft/client/MinecraftClient;crosshairTarget:Lnet/minecraft/util/hit/HitResult;",
   ordinal = 5,
   shift = Shift.BEFORE
)},
      cancellable = true
   )
   private void onUpdateCrosshair(float tickDelta, CallbackInfo ci) {
      if (Modules.get(NoMiningTrace.class).noTrace()) {
         ci.cancel();
      }
   }

   @Inject(
      method = {"bobViewWhenHurt"},
      at = {@At("HEAD")},
      cancellable = true
   )
   private void onBobViewWhenHurt(MatrixStack matrices, float tickDelta, CallbackInfo ci) {
      NoRender noRender = Modules.get(NoRender.class);
      if (noRender.isActive() && noRender.damage.get()) {
         ci.cancel();
      }
   }

   @Inject(
      method = {"showFloatingItem"},
      at = {@At("HEAD")},
      cancellable = true
   )
   private void onShowFloatingItem(ItemStack floatingItem, CallbackInfo ci) {
      NoRender noRender = Modules.get(NoRender.class);
      if (noRender.isActive() && noRender.totem.get()) {
         ci.cancel();
      }
   }
}
