package de.tyrannus.venomhack.modules.misc;

import de.tyrannus.venomhack.modules.Module;
import de.tyrannus.venomhack.settings.Setting;
import net.minecraft.item.EndCrystalItem;
import net.minecraft.item.Item;
import net.minecraft.item.Items;
import net.minecraft.item.PickaxeItem;
import net.minecraft.item.SwordItem;

public class NoMiningTrace extends Module {
   private final Setting<Boolean> filter = this.setting("filter", "Will only work when holding items in filter.", Boolean.valueOf(true));
   private final Setting<Boolean> pick = this.setting("pickaxe", "Will work when holding a pickaxe.", Boolean.valueOf(true), this.filter::get);
   private final Setting<Boolean> gap = this.setting("gap", "Will work when holding any kind of gap.", Boolean.valueOf(false), this.filter::get);
   private final Setting<Boolean> crystal = this.setting("crystal", "Will work when holding a crystal.", Boolean.valueOf(false), this.filter::get);
   private final Setting<Boolean> obsidian = this.setting("obsidian", "Will work when holding obsidian", Boolean.valueOf(false), this.filter::get);
   private final Setting<Boolean> sword = this.setting("sword", "Will work when holding a sword", Boolean.valueOf(false), this.filter::get);

   public NoMiningTrace() {
      super(Module.Categories.MISC, "no-mining-trace", "Allows you to mine through entities.");
   }

   public boolean noTrace() {
      if (!this.isActive()) {
         return false;
      } else if (!this.filter.get()) {
         return true;
      } else {
         Item item = mc.player.getMainHandStack().getItem();
         if (this.pick.get() && item instanceof PickaxeItem) {
            return true;
         } else if (!this.gap.get() || !item.equals(Items.GOLDEN_APPLE) && !item.equals(Items.ENCHANTED_GOLDEN_APPLE)) {
            if (this.crystal.get() && item instanceof EndCrystalItem) {
               return true;
            } else if (this.obsidian.get() && item.equals(Items.OBSIDIAN)) {
               return true;
            } else {
               return this.sword.get() && item instanceof SwordItem;
            }
         } else {
            return true;
         }
      }
   }
}
