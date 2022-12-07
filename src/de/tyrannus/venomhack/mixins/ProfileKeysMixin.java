package de.tyrannus.venomhack.mixins;

import java.util.Optional;
import java.util.concurrent.CompletableFuture;
import net.minecraft.util.Util;
import net.minecraft.network.encryption.PlayerKeyPair;
import net.minecraft.client.util.ProfileKeys;
import net.minecraft.client.util.ProfileKeys.class_7653;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin({ProfileKeys.class})
public class ProfileKeysMixin {
   @Inject(
      method = {"getKeyPair"},
      at = {@At("HEAD")},
      cancellable = true
   )
   private void stfu(Optional<PlayerKeyPair> optional2, CallbackInfoReturnable<CompletableFuture<Optional<class_7653>>> cir) {
      cir.setReturnValue(
         CompletableFuture.<Optional<PlayerKeyPair>>supplyAsync(() -> optional2, Util.getMainWorkerExecutor()).thenApply(optional -> optional.map(class_7653::new))
      );
   }
}
