package de.tyrannus.venomhack.mixins;

import de.tyrannus.venomhack.modules.Modules;
import de.tyrannus.venomhack.modules.misc.Reach;
import de.tyrannus.venomhack.modules.movement.AntiRubberband;
import net.minecraft.network.Packet;
import net.minecraft.client.network.ClientPlayNetworkHandler;
import net.minecraft.client.network.ClientPlayerInteractionManager;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(
   value = {ClientPlayerInteractionManager.class},
   priority = 900
)
public class ClientPlayerInteractionManagerMixin {
   @Inject(
      method = {"getReachDistance"},
      at = {@At("HEAD")},
      cancellable = true
   )
   private void getReach(CallbackInfoReturnable<Float> cir) {
      Reach reach = Modules.get(Reach.class);
      if (reach.isActive()) {
         cir.setReturnValue(reach.reach.get());
      }
   }

   @Redirect(
      method = {"interactItem"},
      at = @At(
   value = "INVOKE",
   target = "Lnet/minecraft/client/network/ClientPlayNetworkHandler;sendPacket(Lnet/minecraft/network/Packet;)V",
   ordinal = 0
)
   )
   private void onInteract(ClientPlayNetworkHandler networkHandler, Packet<?> packet) {
      if (!Modules.isActive(AntiRubberband.class)) {
         networkHandler.sendPacket(packet);
      }
   }
}
