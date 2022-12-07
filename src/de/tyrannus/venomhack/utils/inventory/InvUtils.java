package de.tyrannus.venomhack.utils.inventory;

import de.tyrannus.venomhack.Venomhack;
import it.unimi.dsi.fastutil.ints.Int2ObjectArrayMap;
import java.util.function.Predicate;
import net.minecraft.screen.ScreenHandler;
import net.minecraft.screen.slot.SlotActionType;
import net.minecraft.screen.slot.Slot;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.network.packet.c2s.play.ClickSlotC2SPacket;

public class InvUtils {
   public static ItemPos findInHotbar(Item... items) {
      return findInHotbar(item -> {
         for(Item item1 : items) {
            if (item1 == item) {
               return true;
            }
         }

         return false;
      });
   }

   public static ItemPos findInHotbar(Predicate<Item> predicate) {
      return findInHotbar(predicate, true);
   }

   public static ItemPos findInHotbar(Predicate<Item> predicate, boolean offhand) {
      Item oItem = Venomhack.mc.player.getOffHandStack().getItem();
      if (offhand && predicate.test(oItem)) {
         return new ItemPos(oItem, 45);
      } else {
         for(int slot = 0; slot < 9; ++slot) {
            Item item = Venomhack.mc.player.getInventory().getStack(slot).getItem();
            if (predicate.test(item)) {
               return new ItemPos(item, slot);
            }
         }

         return new ItemPos(null, -1);
      }
   }

   public static ItemPos find(Item... items) {
      return find(stack -> {
         for(Item item : items) {
            if (item == stack.getItem()) {
               return true;
            }
         }

         return false;
      });
   }

   public static ItemPos find(Predicate<ItemStack> predicate) {
      return find(predicate, false);
   }

   public static ItemPos find(Predicate<ItemStack> predicate, boolean reverseOrder) {
      if (reverseOrder) {
         for(int slot = 35; slot >= 0; --slot) {
            ItemStack stack = Venomhack.mc.player.getInventory().getStack(slot);
            if (predicate.test(stack)) {
               return new ItemPos(stack.getItem(), slot);
            }
         }
      } else {
         for(int slot = 0; slot < 36; ++slot) {
            ItemStack stack = Venomhack.mc.player.getInventory().getStack(slot);
            if (predicate.test(stack)) {
               return new ItemPos(stack.getItem(), slot);
            }
         }
      }

      ItemStack offhandStack = Venomhack.mc.player.getOffHandStack();
      if (predicate.test(offhandStack)) {
         return new ItemPos(offhandStack.getItem(), 45);
      } else {
         ItemStack cursorStack = Venomhack.mc.player.currentScreenHandler.getCursorStack();
         if (predicate.test(cursorStack)) {
            return new ItemPos(cursorStack.getItem(), 36);
         } else {
            for(int slot = 0; slot < 4; ++slot) {
               ItemStack itemStack = Venomhack.mc.player.playerScreenHandler.getCraftingInput().getStack(slot);
               if (predicate.test(itemStack)) {
                  return new ItemPos(itemStack.getItem(), 37 + slot);
               }
            }

            return new ItemPos(null, -1);
         }
      }
   }

   public static int count(Item item) {
      return count((Predicate<Item>)(item1 -> item1 == item));
   }

   public static int count(Predicate<Item> predicate) {
      int count = 0;

      for(int i = 0; i < 36; ++i) {
         ItemStack stack = Venomhack.mc.player.getInventory().getStack(i);
         if (predicate.test(stack.getItem())) {
            count += stack.getCount();
         }
      }

      ItemStack offhandStack = Venomhack.mc.player.getOffHandStack();
      if (predicate.test(offhandStack.getItem())) {
         count += offhandStack.getCount();
      }

      ItemStack cursorStack = Venomhack.mc.player.currentScreenHandler.getCursorStack();
      if (predicate.test(cursorStack.getItem())) {
         count += cursorStack.getCount();
      }

      for(int i = 0; i < 4; ++i) {
         ItemStack stack = Venomhack.mc.player.playerScreenHandler.getCraftingInput().getStack(i);
         if (predicate.test(stack.getItem())) {
            count += stack.getCount();
         }
      }

      return count;
   }

   public static int countEmptySlots() {
      int empty = 0;

      for(int i = 0; i < 36; ++i) {
         if (Venomhack.mc.player.getInventory().getStack(i).isEmpty()) {
            ++empty;
         }
      }

      return empty;
   }

   public static void move(int from, int to) {
      Venomhack.mc.interactionManager.clickSlot(Venomhack.mc.player.currentScreenHandler.syncId, from, 0, SlotActionType.PICKUP, Venomhack.mc.player);
      Venomhack.mc.interactionManager.clickSlot(Venomhack.mc.player.currentScreenHandler.syncId, to, 0, SlotActionType.PICKUP, Venomhack.mc.player);
   }

   public static void clickSlotPacket(int fromIndex, int toIndex, SlotActionType type) {
      ScreenHandler sh = Venomhack.mc.player.currentScreenHandler;
      Slot slot = sh.getSlot(fromIndex);
      Int2ObjectArrayMap<ItemStack> stack = new Int2ObjectArrayMap();
      stack.put(fromIndex, slot.getStack());
      Venomhack.mc
         .player
         .networkHandler
         .sendPacket(new ClickSlotC2SPacket(sh.syncId, sh.getRevision(), slot.id, toIndex, type, sh.getSlot(fromIndex).getStack(), stack));
   }
}
