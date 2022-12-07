package de.tyrannus.venomhack.modules.render.hud.elements;

import de.tyrannus.venomhack.events.RenderEvent;
import de.tyrannus.venomhack.modules.render.hud.HudElement;
import de.tyrannus.venomhack.settings.Setting;
import de.tyrannus.venomhack.utils.Utils;
import de.tyrannus.venomhack.utils.inventory.InvUtils;
import de.tyrannus.venomhack.utils.inventory.ItemPos;
import meteordevelopment.orbit.EventHandler;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;

public class ItemHud extends HudElement {
   private final Setting<Boolean> totems = this.setting("totems", "Renders totems on your screen.", Boolean.valueOf(true));

   public ItemHud() {
      super("item-hud", "Displays items on your screen.", 200, 200);
   }

   @EventHandler
   private void onRender(RenderEvent.Hud event) {
      if (!Utils.isNull()) {
         event.getImmediate().draw();
         if (this.totems.get()) {
            this.renderItem(Items.TOTEM_OF_UNDYING, this.x, this.y);
         }
      }
   }

   private void renderItem(Item item, int xPos, int yPos) {
      ItemPos result = InvUtils.find(item);
      int count = 0;
      if (result.found()) {
         count = InvUtils.count(item);
         ItemStack itemStack = new ItemStack(item, count);
         mc.getItemRenderer().renderInGuiWithOverrides(itemStack, xPos, yPos);
         mc.getItemRenderer().renderGuiItemOverlay(mc.textRenderer, itemStack, xPos, yPos, Integer.toString(count));
      }
   }

   @Override
   public int[] getBounds() {
      return new int[]{18, 18};
   }
}
