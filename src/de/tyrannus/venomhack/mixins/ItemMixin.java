package de.tyrannus.venomhack.mixins;

import de.tyrannus.venomhack.Venomhack;
import de.tyrannus.venomhack.events.ItemUseEvent;
import net.minecraft.util.Hand;
import net.minecraft.util.ActionResult;
import net.minecraft.util.TypedActionResult;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.world.World;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin({Item.class})
public class ItemMixin {
   @Inject(
      method = {"finishUsing"},
      at = {@At("HEAD")}
   )
   private void onStopUsingItem(ItemStack stack, World world, LivingEntity user, CallbackInfoReturnable<ItemStack> cir) {
      if (user == Venomhack.mc.player) {
         Venomhack.EVENTS.post(ItemUseEvent.Stop.get(stack));
      }
   }

   @Inject(
      method = {"use"},
      at = {@At("RETURN")}
   )
   private void onUse(World world, PlayerEntity user, Hand hand, CallbackInfoReturnable<TypedActionResult<ItemStack>> cir) {
      if (user == Venomhack.mc.player && ((TypedActionResult)cir.getReturnValue()).getResult() != ActionResult.FAIL) {
         Venomhack.EVENTS.post(ItemUseEvent.Start.get((ItemStack)((TypedActionResult)cir.getReturnValue()).getValue()));
      }
   }
}
