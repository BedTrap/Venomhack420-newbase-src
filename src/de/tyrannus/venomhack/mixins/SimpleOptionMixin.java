package de.tyrannus.venomhack.mixins;

import java.util.function.Consumer;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.option.SimpleOption;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin({SimpleOption.class})
public class SimpleOptionMixin<T> {
   @Shadow
   T value;
   @Shadow
   @Final
   private Consumer<T> changeCallback;

   @Inject(
      method = {"setValue"},
      at = {@At("HEAD")},
      cancellable = true
   )
   private void trole(T value, CallbackInfo ci) {
      if (MinecraftClient.getInstance().isRunning()) {
         if (!this.value.equals(value)) {
            this.value = value;
            this.changeCallback.accept(this.value);
         }
      } else {
         this.value = value;
      }

      ci.cancel();
   }
}
