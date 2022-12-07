package de.tyrannus.venomhack.modules.misc;

import de.tyrannus.venomhack.events.KeyEvent;
import de.tyrannus.venomhack.modules.Module;
import de.tyrannus.venomhack.settings.Setting;
import de.tyrannus.venomhack.utils.ChatUtils;
import de.tyrannus.venomhack.utils.inventory.InvUtils;
import de.tyrannus.venomhack.utils.inventory.ItemPos;
import meteordevelopment.orbit.EventHandler;
import net.minecraft.util.Hand;
import net.minecraft.screen.slot.SlotActionType;
import net.minecraft.item.Items;
import net.minecraft.network.packet.c2s.play.UpdateSelectedSlotC2SPacket;
import net.minecraft.network.packet.c2s.play.HandSwingC2SPacket;
import net.minecraft.network.packet.c2s.play.PlayerInteractItemC2SPacket;

public class MiddleClickPearl extends Module {
   private final Setting<Boolean> offhand = this.setting("offhand", "Throws the epearl from your offhand to not interrupt your eating.", Boolean.valueOf(true));

   public MiddleClickPearl() {
      super(Module.Categories.MISC, "middle-click-pearl", "Throws a pearl without needing to swap slots.");
   }

   @EventHandler
   private void onKey(KeyEvent.Post event) {
      if (mc.currentScreen == null) {
         if (event.isMouse() && event.getKey().getCode() == 2 && event.getAction() != 0) {
            ItemPos pearl = InvUtils.findInHotbar(Items.ENDER_PEARL);
            if (this.offhand.get()) {
               pearl = InvUtils.find(itemStack -> itemStack.getItem() == Items.ENDER_PEARL, true);
            }

            if (!pearl.found()) {
               ChatUtils.info("No ender pearls found!");
            } else {
               Hand hand = pearl.getHand();
               if (pearl.slot() != 1 && !pearl.isOffhand()) {
                  if (this.offhand.get()) {
                     hand = Hand.OFF_HAND;
                     if (pearl.slot() > 36) {
                        mc.interactionManager.clickSlot(mc.player.currentScreenHandler.syncId, pearl.slot() - 36, 40, SlotActionType.SWAP, mc.player);
                     } else {
                        mc.interactionManager.clickSlot(mc.player.currentScreenHandler.syncId, 45, pearl.slot(), SlotActionType.SWAP, mc.player);
                     }
                  } else {
                     sendPacket(new UpdateSelectedSlotC2SPacket(pearl.slot()));
                  }
               }

               sendPacket(new PlayerInteractItemC2SPacket(hand, 0));
               sendPacket(new HandSwingC2SPacket(hand));
               if (this.offhand.get()) {
                  if (pearl.slot() > 36) {
                     mc.interactionManager.clickSlot(mc.player.currentScreenHandler.syncId, pearl.slot() - 36, 40, SlotActionType.SWAP, mc.player);
                  } else {
                     mc.interactionManager.clickSlot(mc.player.currentScreenHandler.syncId, 45, pearl.slot(), SlotActionType.SWAP, mc.player);
                  }
               } else {
                  sendPacket(new UpdateSelectedSlotC2SPacket(mc.player.getInventory().selectedSlot));
               }
            }
         }
      }
   }
}
