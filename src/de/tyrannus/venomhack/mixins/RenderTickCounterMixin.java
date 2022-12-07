package de.tyrannus.venomhack.mixins;

import de.tyrannus.venomhack.modules.Modules;
import de.tyrannus.venomhack.modules.misc.Timer;
import net.minecraft.client.render.RenderTickCounter;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin({RenderTickCounter.class})
public class RenderTickCounterMixin {
   @Shadow
   public float lastFrameDuration;

   @Inject(
      method = {"beginRenderTick"},
      at = {@At(
   value = "FIELD",
   target = "Lnet/minecraft/client/render/RenderTickCounter;prevTimeMillis:J",
   ordinal = 1
)}
   )
   private void onTail(long timeMillis, CallbackInfoReturnable<Integer> cir) {
      this.lastFrameDuration *= Modules.get(Timer.class).factor();
   }
}
