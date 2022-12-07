package de.tyrannus.venomhack.modules.render.hud.elements;

import de.tyrannus.venomhack.Venomhack;
import de.tyrannus.venomhack.events.RenderEvent;
import de.tyrannus.venomhack.modules.render.hud.HudElement;
import de.tyrannus.venomhack.settings.Setting;
import de.tyrannus.venomhack.utils.TextUtils;
import java.awt.Color;
import meteordevelopment.orbit.EventHandler;
import net.minecraft.text.Text;
import net.minecraft.text.MutableText;

public class WaterMark extends HudElement {
   private final Setting<String> watermark = this.setting("Watermark", "", "Venomhack {version}");
   private final Setting<Boolean> rainbow = this.setting("rainbow", "Whether to enable rainbow.", Boolean.valueOf(true));
   private final Setting<Float> rainbowsaturation = this.setting(
      "rainbow-saturation", "change the saturation of the rainbow", 1.0F, this.rainbow::get, 0.0F, 1.0F, 1
   );
   private final Setting<Float> rainbowSpeed = this.setting("rainbow-speed", "Rainbow speed for the prefix.", 0.0042F, this.rainbow::get, 0.0F, 0.005F, 4);
   private final Setting<Float> rainbowWordSpread = this.setting(
      "rainbow-spread", "How fast to cycle through the rainbow.", 0.02F, this.rainbow::get, 0.0F, 0.005F, 3
   );
   private float rainbowHue1;
   private float rainbowHue2;

   public WaterMark() {
      super("WaterMark", "Displays the clients name .", 60, 40);
   }

   @EventHandler
   private void onRender(RenderEvent.Hud event) {
      this.rainbowHue1 += this.rainbowSpeed.get();
      if (this.rainbowHue1 > 1.0F) {
         --this.rainbowHue1;
      } else if (this.rainbowHue1 < -1.0F) {
         ++this.rainbowHue1;
      }

      this.rainbowHue2 = this.rainbowHue1;
      mc.textRenderer
         .draw(
            this.applyRgb(this.watermark.get().replace("{version}", Venomhack.VERSION.getFriendlyString())),
            (float)this.getX(),
            (float)this.getY(),
            getHud().primaryColor.get().getRGB(),
            getHud().textShadows.get(),
            event.getMatrices().peek().getPositionMatrix(),
            event.getImmediate(),
            false,
            0,
            15728880
         );
   }

   private MutableText applyRgb(String text) {
      if (!this.rainbow.get()) {
         return Text.literal(text);
      } else {
         MutableText prefix = Text.empty();

         for(int i = 0; i < text.length(); ++i) {
            prefix.append(TextUtils.coloredTxt(text.substring(i, i + 1), Color.HSBtoRGB(this.rainbowHue2, this.rainbowsaturation.get(), 1.0F)));
            this.rainbowHue2 -= this.rainbowWordSpread.get();
         }

         return prefix;
      }
   }

   @Override
   public int[] getBounds() {
      return new int[]{mc.textRenderer.getWidth(this.watermark.get().replace("{version}", Venomhack.VERSION.getFriendlyString())), 9};
   }
}
