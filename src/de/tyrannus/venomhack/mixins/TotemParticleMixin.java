package de.tyrannus.venomhack.mixins;

import de.tyrannus.venomhack.modules.Modules;
import de.tyrannus.venomhack.modules.render.CustomPops;
import net.minecraft.client.particle.SpriteProvider;
import net.minecraft.client.world.ClientWorld;
import net.minecraft.client.particle.AnimatedParticle;
import net.minecraft.client.particle.TotemParticle;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin({TotemParticle.class})
public abstract class TotemParticleMixin extends AnimatedParticle {
   protected TotemParticleMixin(ClientWorld world, double x, double y, double z, SpriteProvider spriteProvider, float upwardsAcceleration) {
      super(world, x, y, z, spriteProvider, upwardsAcceleration);
   }

   @Inject(
      method = {"<init>"},
      at = {@At("TAIL")}
   )
   private void onPop(
      ClientWorld world, double x, double y, double z, double velocityX, double velocityY, double velocityZ, SpriteProvider spriteProvider, CallbackInfo ci
   ) {
      CustomPops customPops = Modules.get(CustomPops.class);
      if (customPops.isActive()) {
         this.scale(customPops.scale.get());
         int nextInt = this.random.nextInt(4);
         switch(nextInt) {
            case 0:
               this.setColor(customPops.color1.get().getRGB());
               break;
            case 1:
               this.setColor(customPops.color2.get().getRGB());
               break;
            case 2:
               this.setColor(customPops.color3.get().getRGB());
               break;
            case 3:
               this.setColor(customPops.color4.get().getRGB());
         }
      }
   }
}
