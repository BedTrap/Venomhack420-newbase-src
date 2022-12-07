package de.tyrannus.venomhack.mixins;

import de.tyrannus.venomhack.Venomhack;
import de.tyrannus.venomhack.modules.Modules;
import de.tyrannus.venomhack.modules.movement.Moses;
import de.tyrannus.venomhack.modules.movement.Velocity;
import de.tyrannus.venomhack.modules.render.Freecam;
import net.minecraft.entity.Entity;
import net.minecraft.util.math.Vec3d;
import net.minecraft.util.math.MathHelper;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin({Entity.class})
public abstract class EntityMixin {
   @Shadow
   public abstract Vec3d getRotationVector(float var1, float var2);

   @Inject(
      method = {"pushAwayFrom"},
      at = {@At("HEAD")},
      cancellable = true
   )
   private void pushAwayFrom(Entity entity, CallbackInfo ci) {
      Velocity velocity = Modules.get(Velocity.class);
      if (velocity.isActive() && velocity.entities.get()) {
         ci.cancel();
      }
   }

   @Inject(
      method = {"changeLookDirection"},
      at = {@At("HEAD")},
      cancellable = true
   )
   private void onChangeLook(double cursorDeltaX, double cursorDeltaY, CallbackInfo ci) {
      if (this == Venomhack.mc.player) {
         Freecam freecam = Modules.get(Freecam.class);
         if (freecam.isActive()) {
            float f = (float)cursorDeltaY * 0.15F;
            float g = (float)cursorDeltaX * 0.15F;
            Venomhack.mc
               .gameRenderer
               .getCamera()
               .setRotation(
                  Venomhack.mc.gameRenderer.getCamera().getYaw() + g,
                  MathHelper.clamp(Venomhack.mc.gameRenderer.getCamera().getPitch() + f, -90.0F, 90.0F)
               );
            ci.cancel();
         }
      }
   }

   @Inject(
      method = {"getCameraPosVec"},
      at = {@At("HEAD")},
      cancellable = true
   )
   private void getCameraPos(float tickDelta, CallbackInfoReturnable<Vec3d> cir) {
      if (Modules.get(Freecam.class).isActive()) {
         cir.setReturnValue(Venomhack.mc.gameRenderer.getCamera().getPos());
      }
   }

   @Inject(
      method = {"getRotationVec"},
      at = {@At("HEAD")},
      cancellable = true
   )
   private void getRotation(float tickDelta, CallbackInfoReturnable<Vec3d> cir) {
      if (Modules.get(Freecam.class).isActive()) {
         cir.setReturnValue(this.getRotationVector(Venomhack.mc.gameRenderer.getCamera().getPitch(), Venomhack.mc.gameRenderer.getCamera().getYaw()));
      }
   }

   @Inject(
      method = {"isTouchingWater"},
      at = {@At("HEAD")},
      cancellable = true
   )
   private void modifyTouchingWater(CallbackInfoReturnable<Boolean> cir) {
      if (Modules.isActive(Moses.class)) {
         cir.setReturnValue(false);
      }
   }

   @Inject(
      method = {"isInLava"},
      at = {@At("HEAD")},
      cancellable = true
   )
   private void modifyIsInLava(CallbackInfoReturnable<Boolean> cir) {
      Moses moses = Modules.get(Moses.class);
      if (moses.isActive() && moses.lava.get()) {
         cir.setReturnValue(false);
      }
   }
}
