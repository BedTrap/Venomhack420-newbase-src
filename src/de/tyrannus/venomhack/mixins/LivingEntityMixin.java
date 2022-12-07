package de.tyrannus.venomhack.mixins;

import de.tyrannus.venomhack.modules.Modules;
import de.tyrannus.venomhack.modules.movement.NoSlow;
import net.minecraft.entity.LivingEntity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.ModifyVariable;

@Mixin({LivingEntity.class})
public abstract class LivingEntityMixin {
   @ModifyVariable(
      method = {"travel"},
      at = @At(
   value = "INVOKE",
   target = "Lnet/minecraft/entity/LivingEntity;onLanding()V",
   ordinal = 0
),
      ordinal = 0
   )
   private double onSlowFall(double value) {
      NoSlow noSlow = Modules.get(NoSlow.class);
      return noSlow.isActive() && noSlow.slowFalling.get() ? 0.08 : value;
   }
}
