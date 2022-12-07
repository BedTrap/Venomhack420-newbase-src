package de.tyrannus.venomhack.modules.combat;

import de.tyrannus.venomhack.events.TickEvent;
import de.tyrannus.venomhack.modules.Module;
import de.tyrannus.venomhack.settings.Setting;
import de.tyrannus.venomhack.utils.inventory.InvUtils;
import de.tyrannus.venomhack.utils.inventory.ItemPos;
import meteordevelopment.orbit.EventHandler;
import net.minecraft.item.Items;
import net.minecraft.item.TippedArrowItem;
import net.minecraft.network.packet.c2s.play.PlayerMoveC2SPacket.class_2831;

public class Quiver extends Module {
   private final Setting<Integer> charge = this.setting("delay", "The charge time in ticks.", Integer.valueOf(3), 0.0F, 20.0F);
   private final Setting<Boolean> swapBack = this.setting("swap-back", "Swaps back to the previous selected slot after quivering.", Boolean.valueOf(true));
   private int prevSlot;
   private int delayLeft;
   private int bowSlot;

   public Quiver() {
      super(Module.Categories.COMBAT, "quiver", "Shoots yourself with an effect arrow.");
   }

   @EventHandler
   private void onTick(TickEvent.Pre event) {
      ItemPos bow = InvUtils.findInHotbar(Items.BOW);
      ItemPos arrows = InvUtils.find(item -> item.getItem() instanceof TippedArrowItem);
      if (bow.found() && arrows.found()) {
         mc.player.getInventory().selectedSlot = this.bowSlot;
         mc.getNetworkHandler().sendPacket(new class_2831(mc.player.getYaw(), -90.0F, mc.player.isOnGround()));
         mc.options.useKey.setPressed(true);
         if (this.delayLeft > 0) {
            --this.delayLeft;
         } else {
            this.delayLeft = this.charge.get();
            mc.interactionManager.stopUsingItem(mc.player);
            this.toggle(false);
         }
      } else {
         this.toggleWithError("Couldn't find all the required items, disabling.");
      }
   }

   @Override
   public void onEnable() {
      ItemPos bow = InvUtils.findInHotbar(Items.BOW);
      this.bowSlot = bow.slot();
      mc.options.useKey.setPressed(false);
      mc.interactionManager.stopUsingItem(mc.player);
      this.prevSlot = mc.player.getInventory().selectedSlot;
      this.delayLeft = this.charge.get();
   }

   @Override
   public void onDisable() {
      mc.options.useKey.setPressed(false);
      if (this.swapBack.get()) {
         mc.player.getInventory().selectedSlot = this.prevSlot;
      }
   }
}
