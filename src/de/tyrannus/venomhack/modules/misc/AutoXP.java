package de.tyrannus.venomhack.modules.misc;

import de.tyrannus.venomhack.events.TickEvent;
import de.tyrannus.venomhack.modules.Module;
import de.tyrannus.venomhack.settings.Setting;
import de.tyrannus.venomhack.utils.inventory.InvUtils;
import de.tyrannus.venomhack.utils.inventory.ItemPos;
import meteordevelopment.orbit.EventHandler;
import net.minecraft.util.Hand;
import net.minecraft.screen.slot.SlotActionType;
import net.minecraft.item.ExperienceBottleItem;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.enchantment.EnchantmentHelper;
import net.minecraft.enchantment.Enchantments;
import net.minecraft.text.Text;
import net.minecraft.network.packet.c2s.play.UpdateSelectedSlotC2SPacket;
import net.minecraft.network.packet.c2s.play.PlayerInteractItemC2SPacket;
import net.minecraft.network.packet.c2s.play.PlayerMoveC2SPacket.class_2831;

public class AutoXP extends Module {
   private final Setting<Integer> packetsPerTick = this.setting("packets-per-tick", "May cause desync on stricter servers.", Integer.valueOf(1), 1.0F, 10.0F);
   private final Setting<AutoXP.SwitchMode> mode = this.setting(
      "switch-mode", "How to switch your items. Silent currently doesn't work.", AutoXP.SwitchMode.OFFHAND
   );
   private final Setting<Boolean> rotate = this.setting(
      "rotate", "Makes you face down serverside so that the xp always land at your feet.", Boolean.valueOf(true)
   );

   public AutoXP() {
      super(Module.Categories.MISC, "auto-xp", "Mends your armor by throwing experience bottles.");
   }

   @EventHandler
   private void onTick(TickEvent.Pre event) {
      ItemPos xp = InvUtils.findInHotbar(Items.EXPERIENCE_BOTTLE);
      if (this.mode.get() == AutoXP.SwitchMode.OFFHAND) {
         xp = InvUtils.find(item -> item.getItem() instanceof ExperienceBottleItem, true);
      }

      if (!xp.found()) {
         this.toggleWithError(28137, Text.of("No XP found - disabling."));
      } else {
         int repaired = 0;
         int repairable = 0;

         for(int i = 0; i < 4; ++i) {
            ItemStack stack = mc.player.getInventory().getArmorStack(i);
            if (stack.getItem() != null && !stack.isEmpty()) {
               if (EnchantmentHelper.getLevel(Enchantments.MENDING, stack) > 0) {
                  ++repairable;
               }

               if (!stack.isDamaged()) {
                  ++repaired;
               }
            }
         }

         if (repairable == 0) {
            this.toggleWithError(85236, Text.of("No mendable pieces found - disabling."));
         } else if (repaired >= repairable) {
            this.toggleWithError(87253, Text.of("Armour is repaired - disabling."));
         } else {
            if (!xp.isSelected() && !xp.isOffhand() && !this.xpInOffHand()) {
               if (this.mode.get() == AutoXP.SwitchMode.OFFHAND) {
                  mc.interactionManager.clickSlot(mc.player.currentScreenHandler.syncId, 45, xp.slot(), SlotActionType.SWAP, mc.player);
               } else {
                  sendPacket(new UpdateSelectedSlotC2SPacket(xp.slot()));
               }
            }

            if (this.rotate.get()) {
               sendPacket(new class_2831(mc.player.getYaw(), 90.0F, mc.player.isOnGround()));
            }

            Hand hand = xp.getHand();
            if (this.mode.get() == AutoXP.SwitchMode.OFFHAND) {
               hand = Hand.OFF_HAND;
            }

            for(int i = 0; i < this.packetsPerTick.get(); ++i) {
               mc.player.networkHandler.sendPacket(new PlayerInteractItemC2SPacket(hand, 0));
            }

            swing(hand, false);
            if (this.mode.get() == AutoXP.SwitchMode.OFFHAND) {
               mc.interactionManager.clickSlot(mc.player.currentScreenHandler.syncId, 45, xp.slot(), SlotActionType.SWAP, mc.player);
            } else {
               sendPacket(new UpdateSelectedSlotC2SPacket(mc.player.getInventory().selectedSlot));
            }
         }
      }
   }

   private boolean xpInOffHand() {
      return mc.player.getOffHandStack().getItem() == Items.EXPERIENCE_BOTTLE;
   }

   private static enum SwitchMode {
      NORMAL,
      OFFHAND;
   }
}
