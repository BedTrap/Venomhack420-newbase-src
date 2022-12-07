package de.tyrannus.venomhack.mixins;

import de.tyrannus.venomhack.modules.Modules;
import de.tyrannus.venomhack.modules.render.NoRender;
import net.fabricmc.loader.api.FabricLoader;
import net.minecraft.client.render.Camera;
import net.minecraft.client.render.BackgroundRenderer;
import net.minecraft.client.render.BackgroundRenderer.class_4596;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin({BackgroundRenderer.class})
public abstract class BackgroundRendererMixin {
   @Inject(
      method = {"applyFog"},
      at = {@At("HEAD")},
      cancellable = true
   )
   private static void onApplyFog(Camera camera, class_4596 fogType, float viewDistance, boolean thickFog, float tickDelta, CallbackInfo ci) {
      if (!FabricLoader.getInstance().isModLoaded("sodium")) {
         NoRender noRender = Modules.get(NoRender.class);
         if (noRender.isActive() && noRender.fog.get()) {
            ci.cancel();
         }
      }
   }
}
