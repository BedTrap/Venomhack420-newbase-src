package de.tyrannus.venomhack.mixins;

import de.tyrannus.venomhack.Venomhack;
import de.tyrannus.venomhack.modules.Modules;
import de.tyrannus.venomhack.modules.render.Freecam;
import net.minecraft.entity.Entity;
import net.minecraft.world.BlockView;
import net.minecraft.util.math.Vec3d;
import net.minecraft.client.render.Camera;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin({Camera.class})
public abstract class CameraMixin {
   @Shadow
   private boolean thirdPerson;

   @Shadow
   protected void setPos(Vec3d pos) {
   }

   @Shadow
   public abstract float getPitch();

   @Shadow
   public abstract float getYaw();

   @Inject(
      method = {"update"},
      at = {@At("HEAD")},
      cancellable = true
   )
   private void onUpdate(BlockView area, Entity player, boolean thirdPerson, boolean inverseView, float tickDelta, CallbackInfo ci) {
      if (Venomhack.mc.player != null && Venomhack.mc.world != null) {
         Freecam freecam = Modules.get(Freecam.class);
         if (freecam.isActive()) {
            ci.cancel();
            this.thirdPerson = true;
            this.setPos(freecam.getPos());
         }
      }
   }
}
