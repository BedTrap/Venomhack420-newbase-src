package de.tyrannus.venomhack.mixins;

import de.tyrannus.venomhack.Venomhack;
import de.tyrannus.venomhack.modules.Modules;
import de.tyrannus.venomhack.modules.render.OldAnimations;
import de.tyrannus.venomhack.modules.render.ViewModel;
import net.minecraft.client.render.item.HeldItemRenderer;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.ModifyArg;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin({HeldItemRenderer.class})
public class HeldItemRendererMixin {
   @Shadow
   private float equipProgressMainHand;
   @Shadow
   private float equipProgressOffHand;
   @Shadow
   private float prevEquipProgressMainHand;
   @Shadow
   private float prevEquipProgressOffHand;

   @ModifyArg(
      method = {"renderItem(FLnet/minecraft/client/util/math/MatrixStack;Lnet/minecraft/client/render/VertexConsumerProvider$Immediate;Lnet/minecraft/client/network/ClientPlayerEntity;I)V"},
      at = @At(
   value = "INVOKE",
   target = "Lnet/minecraft/client/render/item/HeldItemRenderer;renderFirstPersonItem(Lnet/minecraft/client/network/AbstractClientPlayerEntity;FFLnet/minecraft/util/Hand;FLnet/minecraft/item/ItemStack;FLnet/minecraft/client/util/math/MatrixStack;Lnet/minecraft/client/render/VertexConsumerProvider;I)V",
   ordinal = 0
),
      index = 4
   )
   private float mhand(float swingProgress) {
      ViewModel module = Modules.get(ViewModel.class);
      return module.mainSwing.get() != 0.0F && module.isActive() && !Venomhack.mc.player.getMainHandStack().isEmpty()
         ? module.mainSwing.get()
         : swingProgress;
   }

   @ModifyArg(
      method = {"renderItem(FLnet/minecraft/client/util/math/MatrixStack;Lnet/minecraft/client/render/VertexConsumerProvider$Immediate;Lnet/minecraft/client/network/ClientPlayerEntity;I)V"},
      at = @At(
   value = "INVOKE",
   target = "Lnet/minecraft/client/render/item/HeldItemRenderer;renderFirstPersonItem(Lnet/minecraft/client/network/AbstractClientPlayerEntity;FFLnet/minecraft/util/Hand;FLnet/minecraft/item/ItemStack;FLnet/minecraft/client/util/math/MatrixStack;Lnet/minecraft/client/render/VertexConsumerProvider;I)V",
   ordinal = 1
),
      index = 4
   )
   private float ohand(float swingProgress) {
      ViewModel module = Modules.get(ViewModel.class);
      return module.offSwing.get() != 0.0F && module.isActive() && !Venomhack.mc.player.getOffHandStack().isEmpty()
         ? module.offSwing.get()
         : swingProgress;
   }

   @Inject(
      method = {"updateHeldItems"},
      at = {@At("HEAD")}
   )
   private void onUpdateHeldItemsHEAD(CallbackInfo ci) {
      OldAnimations oa = Modules.get(OldAnimations.class);
      if (oa.isActive()) {
         if (oa.handSwing.get()) {
            this.equipProgressMainHand = 0.0F;
            this.prevEquipProgressMainHand = 0.0F;
            this.equipProgressOffHand = 0.0F;
            this.prevEquipProgressOffHand = 0.0F;
         }
      }
   }

   @Inject(
      method = {"updateHeldItems"},
      at = {@At("RETURN")}
   )
   private void onUpdateHeldItemsRETURN(CallbackInfo ci) {
      OldAnimations oa = Modules.get(OldAnimations.class);
      if (oa.isActive()) {
         if (oa.handSwing.get()) {
            this.equipProgressMainHand = 1.0F;
            this.prevEquipProgressMainHand = 1.0F;
            this.equipProgressOffHand = 1.0F;
            this.prevEquipProgressOffHand = 1.0F;
         }
      }
   }
}
