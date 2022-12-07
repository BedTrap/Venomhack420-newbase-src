package de.tyrannus.venomhack.mixins;

import com.mojang.authlib.yggdrasil.YggdrasilUserApiService;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin({YggdrasilUserApiService.class})
public class YggdrasilUserApiServiceMixin {
   @Inject(
      method = {"fetchProperties"},
      at = {@At("HEAD")},
      cancellable = true,
      remap = false
   )
   private void stfu(CallbackInfo ci) {
      ci.cancel();
   }
}
