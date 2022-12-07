package de.tyrannus.venomhack.modules.render.hud.elements;

import de.tyrannus.venomhack.events.RenderEvent;
import de.tyrannus.venomhack.modules.Modules;
import de.tyrannus.venomhack.modules.render.Freecam;
import de.tyrannus.venomhack.modules.render.hud.HudElement;
import de.tyrannus.venomhack.settings.Setting;
import de.tyrannus.venomhack.utils.TextUtils;
import de.tyrannus.venomhack.utils.Utils;
import de.tyrannus.venomhack.utils.world.WorldUtils;
import java.awt.Color;
import java.util.Locale;
import meteordevelopment.orbit.EventHandler;
import net.minecraft.world.World;
import net.minecraft.util.math.Vec3d;
import net.minecraft.text.Text;
import net.minecraft.text.MutableText;

public class CoordHud extends HudElement {
   private final Setting<Boolean> opposite = this.setting("opposite", "Shows coordinates of the opposite dimension.", Boolean.valueOf(true));
   private final Setting<Boolean> rainbow = this.setting("rainbow", "Whether to enable rainbow.", Boolean.valueOf(false));
   private final Setting<Float> rainbowSpeed = this.setting("rainbow-speed", "Rainbow speed for the prefix.", 0.0042F, this.rainbow::get, 0.0F, 0.005F, 4);
   private final Setting<Float> rainbowWordSpread = this.setting(
      "rainbow-spread", "How fast to cycle through the rainbow.", 0.02F, this.rainbow::get, 0.0F, 0.005F, 2
   );
   private final Setting<Float> rainbowsaturation = this.setting(
      "rainbow-saturation", "change the saturation of the rainbow", 1.0F, this.rainbow::get, 0.0F, 1.0F, 1
   );
   private float rainbowHue1;
   private float rainbowHue2;
   private int length;

   public CoordHud() {
      super("coord-hud", "Shows your current coordinates.", 40, 20);
   }

   @EventHandler
   private void onRender(RenderEvent.Hud event) {
      if (!Utils.isNull()) {
         Vec3d pos = Modules.isActive(Freecam.class) ? mc.gameRenderer.getCamera().getPos() : mc.player.getPos();
         String coordString = String.format(Locale.ROOT, "[%.1f %.1f %.1f]", pos.x, pos.y, pos.z);
         boolean overworld = WorldUtils.getWorld() == World.OVERWORLD;
         String oppositeString = "";
         if (this.opposite.get()) {
            if (overworld) {
               oppositeString = String.format(Locale.ROOT, " [%.1f %.1f %.1f]", pos.x / 8.0, pos.y, pos.z / 8.0);
            } else if (WorldUtils.getWorld() == World.NETHER) {
               oppositeString = String.format(Locale.ROOT, " [%.1f %.1f %.1f]", pos.x * 8.0, pos.y, pos.z * 8.0);
            }
         }

         this.length = mc.textRenderer.getWidth(coordString + oppositeString);
         if (this.rainbow.get()) {
            this.rainbowHue1 += this.rainbowSpeed.get();
            if (this.rainbowHue1 > 1.0F) {
               --this.rainbowHue1;
            } else if (this.rainbowHue1 < -1.0F) {
               ++this.rainbowHue1;
            }

            this.rainbowHue2 = this.rainbowHue1;
            mc.textRenderer
               .draw(
                  this.applyRgb(coordString + oppositeString),
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
         } else {
            this.renderText(event, coordString, oppositeString, overworld ? WHITE : RED, overworld ? RED : WHITE);
         }
      }
   }

   private MutableText applyRgb(String text) {
      MutableText prefix = Text.empty();

      for(int i = 0; i < text.length(); ++i) {
         prefix.append(TextUtils.coloredTxt(text.substring(i, i + 1), Color.HSBtoRGB(this.rainbowHue2, this.rainbowsaturation.get(), 1.0F)));
         this.rainbowHue2 -= this.rainbowWordSpread.get();
      }

      return prefix;
   }

   @Override
   public int[] getBounds() {
      return new int[]{this.length, 9};
   }
}
