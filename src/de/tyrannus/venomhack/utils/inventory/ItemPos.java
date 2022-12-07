package de.tyrannus.venomhack.utils.inventory;

import de.tyrannus.venomhack.Venomhack;
import net.minecraft.util.Hand;
import net.minecraft.item.Item;

public record ItemPos(Item item, int slot) {
   public boolean found() {
      return this.slot != -1;
   }

   public boolean isOffhand() {
      return this.slot == 45;
   }

   public boolean isHotbar() {
      return this.slot < 9;
   }

   public Hand getHand() {
      return this.isOffhand() ? Hand.OFF_HAND : Hand.MAIN_HAND;
   }

   public boolean isSelected() {
      return this.slot == 45 || Venomhack.mc.player.getInventory().selectedSlot == this.slot;
   }
}
