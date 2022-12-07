package de.tyrannus.venomhack.mixins;

import de.tyrannus.venomhack.Venomhack;
import de.tyrannus.venomhack.modules.Modules;
import de.tyrannus.venomhack.modules.render.OldAnimations;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.util.math.MathHelper;
import net.minecraft.client.render.entity.model.BipedEntityModel;
import net.minecraft.client.model.ModelPart;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.At.Shift;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin({BipedEntityModel.class})
public class BipedEntityModelMixin {
   @Shadow
   public ModelPart leftArm;
   @Shadow
   public ModelPart rightArm;
   @Shadow
   public ModelPart leftLeg;
   @Shadow
   public ModelPart rightLeg;

   @Inject(
      method = {"setAngles"},
      at = {@At(
   value = "FIELD",
   target = "Lnet/minecraft/client/model/ModelPart;roll:F",
   ordinal = 1,
   shift = Shift.AFTER
)}
   )
   private void setAngles(LivingEntity livingEntity, float f, float g, float h, float i, float j, CallbackInfo ci) {
      OldAnimations oa = Modules.get(OldAnimations.class);
      if (oa.isActive()) {
         if (oa.arms.get()) {
            if (livingEntity instanceof PlayerEntity) {
               if (!oa.ignoreSelf.get() || livingEntity != Venomhack.mc.player) {
                  this.leftArm.pitch = MathHelper.cos(f * oa.leftArmPitch.get()) * 2.0F * g;
                  this.rightArm.pitch = MathHelper.cos(f * oa.rightArmPitch.get()) * 2.0F * g;
                  this.leftArm.roll = (MathHelper.cos(f * oa.leftArmRoll.get()) - 1.0F) * 1.0F * g;
                  this.rightArm.roll = (MathHelper.cos(f * oa.rightArmRoll.get()) + 1.0F) * 1.0F * g;
               }
            }
         }
      }
   }
}
