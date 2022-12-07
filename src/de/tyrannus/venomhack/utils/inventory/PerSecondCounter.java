package de.tyrannus.venomhack.utils.inventory;

import net.minecraft.item.Item;

public class PerSecondCounter {
   private final int[] counts = new int[20];

   public void increment(Item item) {
      for(int i = this.counts.length - 1; i >= 0; --i) {
         if (i == 0) {
            this.counts[0] = InvUtils.count(item);
         } else {
            this.counts[i] = this.counts[i - 1];
         }
      }
   }

   public int get() {
      return this.counts[19] - this.counts[0];
   }
}
