package de.tyrannus.venomhack.modules.render.hud.elements;

import de.tyrannus.venomhack.events.RenderEvent;
import de.tyrannus.venomhack.modules.render.hud.HudElement;
import de.tyrannus.venomhack.settings.Setting;
import de.tyrannus.venomhack.utils.Utils;
import meteordevelopment.orbit.EventHandler;
import net.minecraft.item.ItemStack;

public class ArmourHud extends HudElement {
   private final Setting<Integer> distance = this.setting("distance", "The distance between each armour piece.", Integer.valueOf(18), 0.0F, 128.0F);

   public ArmourHud() {
      super("armour-hud", "Renders your armour and durability status.", 20, 20);
   }

   @EventHandler
   private void onRender(RenderEvent.Hud event) {
      if (!Utils.isNull()) {
         for(int i = 3; i >= 0; --i) {
            ItemStack stack = mc.player.getInventory().getArmorStack(i);
            int dist = this.distance.get() * (3 - i);
            mc.getItemRenderer().renderInGuiWithOverrides(stack, this.x + dist, this.y);
            mc.getItemRenderer().renderGuiItemOverlay(mc.textRenderer, stack, this.x + dist, this.y);
         }
      }
   }

   @Override
   public int[] getBounds() {
      return new int[]{4 * this.distance.get(), 18};
   }
}
