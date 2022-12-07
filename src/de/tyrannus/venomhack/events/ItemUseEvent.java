package de.tyrannus.venomhack.events;

import net.minecraft.item.ItemStack;

public class ItemUseEvent {
   ItemStack stack;

   public ItemStack getStack() {
      return this.stack;
   }

   public static class Start extends ItemUseEvent {
      private static final ItemUseEvent.Start INSTANCE = new ItemUseEvent.Start();

      public static ItemUseEvent.Start get(ItemStack stack) {
         INSTANCE.stack = stack;
         return INSTANCE;
      }
   }

   public static class Stop extends ItemUseEvent {
      private static final ItemUseEvent.Stop INSTANCE = new ItemUseEvent.Stop();

      public static ItemUseEvent.Stop get(ItemStack stack) {
         INSTANCE.stack = stack;
         return INSTANCE;
      }
   }
}
