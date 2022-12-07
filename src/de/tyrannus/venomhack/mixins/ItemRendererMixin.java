package de.tyrannus.venomhack.mixins;

import de.tyrannus.venomhack.Venomhack;
import de.tyrannus.venomhack.modules.Modules;
import de.tyrannus.venomhack.modules.render.ViewModel;
import net.minecraft.client.render.model.BakedModel;
import net.minecraft.util.math.Quaternion;
import net.minecraft.item.ItemStack;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.render.item.ItemRenderer;
import net.minecraft.client.render.model.json.ModelTransformation.class_811;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin({ItemRenderer.class})
public class ItemRendererMixin {
   @Inject(
      method = {"renderItem(Lnet/minecraft/item/ItemStack;Lnet/minecraft/client/render/model/json/ModelTransformation$Mode;ZLnet/minecraft/client/util/math/MatrixStack;Lnet/minecraft/client/render/VertexConsumerProvider;IILnet/minecraft/client/render/model/BakedModel;)V"},
      at = {@At(
   value = "INVOKE",
   target = "Lnet/minecraft/client/util/math/MatrixStack;translate(DDD)V"
)}
   )
   private void renderItem(
      ItemStack stack,
      class_811 renderMode,
      boolean leftHanded,
      MatrixStack matrices,
      VertexConsumerProvider vertexConsumers,
      int light,
      int overlay,
      BakedModel model,
      CallbackInfo ci
   ) {
      ViewModel module = Modules.get(ViewModel.class);
      if (module.isActive() && !stack.isEmpty() && Venomhack.mc.player != null) {
         if (renderMode == class_811.FIRST_PERSON_RIGHT_HAND) {
            if (module.eatFix.get() && this.playerIsEating()) {
               return;
            }

            matrices.translate(
               (double)module.posXMain.get().floatValue(), (double)module.posYMain.get().floatValue(), (double)module.posZMain.get().floatValue()
            );
            matrices.multiply(
               new Quaternion(
                  (float)module.rotationXMain.get().intValue(),
                  (float)module.rotationYMain.get().intValue(),
                  (float)module.rotationZMain.get().intValue(),
                  true
               )
            );
            matrices.scale(module.scaleXMain.get(), module.scaleYMain.get(), module.scaleZMain.get());
         } else if (renderMode == class_811.FIRST_PERSON_LEFT_HAND) {
            if (module.eatFix.get() && this.playerIsEating()) {
               return;
            }

            matrices.translate(
               (double)module.posXOff.get().floatValue(), (double)module.posYOff.get().floatValue(), (double)module.posZOff.get().floatValue()
            );
            matrices.multiply(
               new Quaternion(
                  (float)module.rotationXOff.get().intValue(), (float)module.rotationYOff.get().intValue(), (float)module.rotationZOff.get().intValue(), true
               )
            );
            matrices.scale(module.scaleXOff.get(), module.scaleYOff.get(), module.scaleZOff.get());
         }
      }
   }

   private boolean playerIsEating() {
      return Venomhack.mc.player.isHolding(ItemStack::isFood) && Venomhack.mc.options.useKey.isPressed();
   }
}
