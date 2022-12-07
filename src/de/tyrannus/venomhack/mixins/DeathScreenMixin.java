package de.tyrannus.venomhack.mixins;

import de.tyrannus.venomhack.Venomhack;
import de.tyrannus.venomhack.events.TickEvent;
import net.minecraft.client.gui.screen.DeathScreen;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin({DeathScreen.class})
public class DeathScreenMixin {
   @Inject(
      method = {"tick"},
      at = {@At("HEAD")}
   )
   private void onTick(CallbackInfo ci) {
      Venomhack.EVENTS.post(TickEvent.Pre.get());
   }

   @Inject(
      method = {"tick"},
      at = {@At("RETURN")}
   )
   private void onTickReturn(CallbackInfo ci) {
      Venomhack.EVENTS.post(TickEvent.Post.get());
   }
}
