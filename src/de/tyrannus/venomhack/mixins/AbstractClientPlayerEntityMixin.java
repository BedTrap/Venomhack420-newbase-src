package de.tyrannus.venomhack.mixins;

import de.tyrannus.venomhack.modules.Modules;
import de.tyrannus.venomhack.modules.render.UserCapes;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityType;
import net.minecraft.world.World;
import net.minecraft.util.Identifier;
import net.minecraft.client.network.AbstractClientPlayerEntity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin({AbstractClientPlayerEntity.class})
public abstract class AbstractClientPlayerEntityMixin extends Entity {
   public AbstractClientPlayerEntityMixin(EntityType<?> type, World world) {
      super(type, world);
   }

   @Inject(
      method = {"getCapeTexture"},
      at = {@At("HEAD")},
      cancellable = true
   )
   private void onGetCapeTexture(CallbackInfoReturnable<Identifier> info) {
      if (Modules.isActive(UserCapes.class)) {
         String thisUuid = this.getUuid().toString();

         for(String uuid : UserCapes.USER_UUIDS) {
            if (thisUuid.equals(uuid)) {
               info.setReturnValue(UserCapes.TEXTURE);
               return;
            }
         }
      }
   }
}
