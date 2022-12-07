package de.tyrannus.venomhack.modules.render.hud.elements;

import com.mojang.blaze3d.systems.RenderSystem;
import de.tyrannus.venomhack.events.RenderEvent;
import de.tyrannus.venomhack.modules.render.hud.HudElement;
import de.tyrannus.venomhack.settings.Setting;
import java.awt.Color;
import meteordevelopment.orbit.EventHandler;
import net.minecraft.util.Identifier;
import net.minecraft.client.gui.DrawableHelper;

public class LogoHud extends HudElement {
   private final Setting<Boolean> rainbow = this.setting("rainbow", "Whether to cycle through the rainbow colors.", Boolean.valueOf(true));
   private final Setting<Float> rainbowSpeed = this.setting(
      "rainbow-speed", "How fast to cycle through the rainbow.", 0.0035F, this.rainbow::get, 0.0F, 0.01F, 4
   );
   private final Setting<Integer> width = this.setting("width", "How wide the logo appears on the screen.", Integer.valueOf(64), 0.0F, 128.0F);
   private final Setting<Integer> height = this.setting("height", "How high the logo appears on the screen.", Integer.valueOf(64), 0.0F, 128.0F);
   private static final Identifier TEXTURE = new Identifier("venomhack", "icon.png");
   private float rainbowHue1;

   public LogoHud() {
      super("logo-hud", "Displays the Venomhack logo.", 0, 0);
   }

   @EventHandler
   private void onRender(RenderEvent.Hud event) {
      event.getImmediate().draw();
      this.rainbowHue1 = (float)((double)this.rainbowHue1 + (double)this.rainbowSpeed.get().floatValue() * 0.229960365);
      if (this.rainbowHue1 > 1.0F) {
         --this.rainbowHue1;
      } else if (this.rainbowHue1 < -1.0F) {
         ++this.rainbowHue1;
      }

      Color rgb = Color.getHSBColor(this.rainbowHue1, 1.0F, 1.0F);
      RenderSystem.setShaderTexture(0, TEXTURE);
      if (this.rainbow.get()) {
         RenderSystem.setShaderColor((float)rgb.getRed() / 255.0F, (float)rgb.getGreen() / 255.0F, (float)rgb.getBlue() / 255.0F, 1.0F);
      }

      DrawableHelper.drawTexture(
         event.getMatrices(),
         this.x,
         this.y,
         (float)(39 * this.width.get()) / 200.0F,
         0.0F,
         this.width.get(),
         this.height.get(),
         this.width.get(),
         this.height.get()
      );
      RenderSystem.setShaderColor(1.0F, 1.0F, 1.0F, 1.0F);
   }

   @Override
   public int[] getBounds() {
      return new int[]{this.width.get(), this.height.get()};
   }
}
