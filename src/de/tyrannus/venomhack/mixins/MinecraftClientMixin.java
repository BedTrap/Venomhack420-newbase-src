package de.tyrannus.venomhack.mixins;

import de.tyrannus.venomhack.Venomhack;
import de.tyrannus.venomhack.events.OpenScreenEvent;
import de.tyrannus.venomhack.modules.Modules;
import de.tyrannus.venomhack.modules.exploit.FastUse;
import de.tyrannus.venomhack.modules.exploit.MultiTask;
import de.tyrannus.venomhack.modules.movement.InventoryWalk;
import net.minecraft.util.Hand;
import net.minecraft.util.ActionResult;
import net.minecraft.entity.Entity;
import net.minecraft.item.ItemStack;
import net.minecraft.client.option.KeyBinding;
import net.minecraft.client.MinecraftClient;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.hit.EntityHitResult;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.network.ClientPlayerInteractionManager;
import net.minecraft.client.network.ClientPlayerEntity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.injection.At.Shift;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(
   value = {MinecraftClient.class},
   priority = 900
)
public abstract class MinecraftClientMixin {
   @Shadow
   private int itemUseCooldown;
   @Unique
   private int timesUsed = 0;

   @Shadow
   protected abstract void doItemUse();

   @Redirect(
      method = {"handleBlockBreaking"},
      at = @At(
   value = "INVOKE",
   target = "Lnet/minecraft/client/network/ClientPlayerEntity;isUsingItem()Z"
)
   )
   private boolean breakBlockCheck(ClientPlayerEntity player) {
      return Modules.isActive(MultiTask.class) ? false : player.isUsingItem();
   }

   @Redirect(
      method = {"doItemUse"},
      at = @At(
   value = "INVOKE",
   target = "Lnet/minecraft/client/network/ClientPlayerInteractionManager;isBreakingBlock()Z"
)
   )
   private boolean useItemBreakCheck(ClientPlayerInteractionManager cpim) {
      return Modules.isActive(MultiTask.class) ? false : cpim.isBreakingBlock();
   }

   @Redirect(
      method = {"handleInputEvents"},
      at = @At(
   value = "INVOKE",
   target = "Lnet/minecraft/client/network/ClientPlayerEntity;isUsingItem()Z",
   ordinal = 0
)
   )
   private boolean attackCheck(ClientPlayerEntity player) {
      return !Modules.isActive(MultiTask.class)
            || !Venomhack.mc.options.attackKey.isPressed()
               && !Venomhack.mc.options.useKey.isPressed()
               && !Venomhack.mc.options.pickItemKey.isPressed()
         ? player.isUsingItem()
         : false;
   }

   @Redirect(
      method = {"handleInputEvents"},
      at = @At(
   value = "INVOKE",
   target = "Lnet/minecraft/client/network/ClientPlayerEntity;isUsingItem()Z",
   ordinal = 1
)
   )
   private boolean attackCheck2(ClientPlayerEntity player) {
      MultiTask multiTask = Modules.get(MultiTask.class);
      return multiTask.isActive() && multiTask.old.get() && Venomhack.mc.options.useKey.isPressed() ? false : player.isUsingItem();
   }

   @Inject(
      method = {"doItemUse"},
      at = {@At(
   value = "FIELD",
   target = "Lnet/minecraft/client/MinecraftClient;itemUseCooldown:I",
   shift = Shift.AFTER
)}
   )
   private void onItemUse(CallbackInfo ci) {
      FastUse fastUse = Modules.get(FastUse.class);
      if (fastUse.isActive() && fastUse.isHolding()) {
         this.itemUseCooldown = fastUse.cooldown.get();
      }
   }

   @Inject(
      method = {"doItemUse"},
      at = {@At("HEAD")},
      cancellable = true
   )
   private void onDoItemUse(CallbackInfo ci) {
      FastUse fastUse = Modules.get(FastUse.class);
      if (fastUse.isActive() && fastUse.isHolding() && this.timesUsed < fastUse.multiplier.get() - 1) {
         ++this.timesUsed;
         this.doItemUse();
      } else if (this.timesUsed == fastUse.multiplier.get() - 1) {
         this.timesUsed = 0;
      }

      MultiTask multiTask = Modules.get(MultiTask.class);
      if (multiTask.isActive() && multiTask.old.get()) {
         if (!Venomhack.mc.interactionManager.isBreakingBlock()) {
            this.itemUseCooldown = fastUse.isActive() && fastUse.isHolding() ? fastUse.cooldown.get() : 4;
            if (!Venomhack.mc.player.isRiding()) {
               for(Hand hand : Hand.values()) {
                  ItemStack itemStack = Venomhack.mc.player.getStackInHand(hand);
                  if (Venomhack.mc.crosshairTarget != null) {
                     switch(Venomhack.mc.crosshairTarget.getType()) {
                        case ENTITY:
                           EntityHitResult entityHitResult = (EntityHitResult)Venomhack.mc.crosshairTarget;
                           Entity entity = entityHitResult.getEntity();
                           if (!Venomhack.mc.world.getWorldBorder().contains(entity.getBlockPos())) {
                              continue;
                           }

                           ActionResult actionResult = Venomhack.mc.interactionManager.interactEntityAtLocation(Venomhack.mc.player, entity, entityHitResult, hand);
                           if (!actionResult.isAccepted()) {
                              actionResult = Venomhack.mc.interactionManager.interactEntity(Venomhack.mc.player, entity, hand);
                           }

                           if (actionResult.isAccepted()) {
                              if (actionResult.shouldSwingHand()) {
                                 Venomhack.mc.player.swingHand(hand);
                              }
                              continue;
                           }
                           break;
                        case BLOCK:
                           BlockHitResult blockHitResult = (BlockHitResult)Venomhack.mc.crosshairTarget;
                           int i = itemStack.getCount();
                           ActionResult actionResult2 = Venomhack.mc.interactionManager.interactBlock(Venomhack.mc.player, hand, blockHitResult);
                           if (actionResult2.isAccepted()) {
                              if (actionResult2.shouldSwingHand()) {
                                 Venomhack.mc.player.swingHand(hand);
                                 if (!itemStack.isEmpty() && (itemStack.getCount() != i || Venomhack.mc.interactionManager.hasCreativeInventory())) {
                                    Venomhack.mc.gameRenderer.firstPersonRenderer.resetEquipProgress(hand);
                                 }
                              }
                              continue;
                           }

                           if (actionResult2 == ActionResult.FAIL) {
                              continue;
                           }
                     }
                  }

                  if (!itemStack.isEmpty()) {
                     ActionResult actionResult3 = Venomhack.mc.interactionManager.interactItem(Venomhack.mc.player, hand);
                     if (actionResult3.isAccepted()) {
                        if (actionResult3.shouldSwingHand()) {
                           Venomhack.mc.player.swingHand(hand);
                        }

                        Venomhack.mc.gameRenderer.firstPersonRenderer.resetEquipProgress(hand);
                     }
                  }
               }

               ci.cancel();
            }
         }
      }
   }

   @Redirect(
      method = {"setScreen"},
      at = @At(
   value = "INVOKE",
   target = "Lnet/minecraft/client/option/KeyBinding;unpressAll()V"
)
   )
   private void unPress() {
      if (Modules.get(InventoryWalk.class).allowWalk()) {
         for(KeyBinding keyBinding : KeyBinding.KEYS_BY_ID.values()) {
            if (!keyBinding.getCategory().equals("key.categories.movement")) {
               keyBinding.reset();
            }
         }
      } else {
         KeyBinding.unpressAll();
      }
   }

   @Inject(
      method = {"setScreen"},
      at = {@At("HEAD")},
      cancellable = true
   )
   private void onOpenScreen(Screen screen, CallbackInfo ci) {
      if (((OpenScreenEvent)Venomhack.EVENTS.post(OpenScreenEvent.get(screen))).isCancelled()) {
         ci.cancel();
      }
   }
}
