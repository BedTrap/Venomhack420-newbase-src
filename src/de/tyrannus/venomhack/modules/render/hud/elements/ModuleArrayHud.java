package de.tyrannus.venomhack.modules.render.hud.elements;

import de.tyrannus.venomhack.events.RenderEvent;
import de.tyrannus.venomhack.modules.Module;
import de.tyrannus.venomhack.modules.Modules;
import de.tyrannus.venomhack.modules.render.hud.HudElement;
import de.tyrannus.venomhack.settings.Setting;
import java.awt.Color;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import meteordevelopment.orbit.EventHandler;

public class ModuleArrayHud extends HudElement {
   private final Setting<Boolean> rainbow = this.setting("rainbow", "Makes the array list use rainbow colors.", Boolean.valueOf(true));
   private final Setting<Float> rainbowSpeed = this.setting(
      "rainbow-speed", "How fast to cycle through the rainbow.", 0.0013F, this.rainbow::get, 0.0F, 0.005F, 4
   );
   private final Setting<Float> rainbowsaturation = this.setting(
      "rainbow-saturation", "change the saturation of the rainbow", 1.0F, this.rainbow::get, 0.0F, 1.0F, 1
   );
   private final Setting<Float> rainbowLineSpread = this.setting("rainbow-spread", "Rainbow spread per line.", 0.05F, this.rainbow::get, 0.0F, 0.2F, 2);
   private float rainbowHue1;

   public ModuleArrayHud() {
      super("array-hud", "Shows all active modules. Modules can be hidden from this list with ,drawn.", 0, 0);
   }

   @EventHandler
   private void onRender(RenderEvent.Hud event) {
      List<Module> modules = this.getDrawnModules();
      modules.sort(Comparator.comparingDouble(this::moduleTextWidth));
      this.rainbowHue1 += this.rainbowSpeed.get();
      if (this.rainbowHue1 > 1.0F) {
         --this.rainbowHue1;
      } else if (this.rainbowHue1 < -1.0F) {
         ++this.rainbowHue1;
      }

      float rainbowHue2 = this.rainbowHue1;
      int greatestWidth = mc.textRenderer.getWidth(modules.get(0).getParsedName());
      boolean left = this.getX() > mc.getWindow().getScaledWidth() / 2;
      boolean bottom = this.getY() > mc.getWindow().getScaledHeight() / 2;
      float yBase = (float)this.getY() + (bottom ? -0.5F : 0.5F) * (float)modules.size() * 9.0F;

      for(int i = 0; i < modules.size(); ++i) {
         Module module = modules.get(i);
         int width = -this.moduleTextWidth(module);
         int y = this.getY() + (bottom ? -i - 1 : i) * 9;
         int x = this.getX() + (left ? greatestWidth - width : 0);
         rainbowHue2 -= this.rainbowLineSpread.get();
         int c = Color.HSBtoRGB(rainbowHue2, this.rainbowsaturation.get(), 1.0F);
         mc.textRenderer
            .draw(
               module.getParsedName(),
               (float)x,
               (float)y,
               this.rainbow.get() ? c : module.category.color.getRGB(),
               getHud().textShadows.get(),
               event.getMatrices().peek().getPositionMatrix(),
               event.getImmediate(),
               false,
               0,
               15728880,
               mc.textRenderer.isRightToLeft()
            );
         int nameWidth = mc.textRenderer.getWidth(module.getParsedName());
         if (nameWidth < width) {
            int bracketWidth = mc.textRenderer.getWidth("[");
            mc.textRenderer
               .draw(
                  " [",
                  (float)(x + nameWidth),
                  (float)y,
                  Color.GRAY.getRGB(),
                  getHud().textShadows.get(),
                  event.getMatrices().peek().getPositionMatrix(),
                  event.getImmediate(),
                  false,
                  0,
                  15728880,
                  mc.textRenderer.isRightToLeft()
               );
            mc.textRenderer
               .draw(
                  module.getArrayText(),
                  (float)(x + nameWidth + 2 * bracketWidth),
                  (float)y,
                  getHud().secondaryColor.get().getRGB(),
                  getHud().textShadows.get(),
                  event.getMatrices().peek().getPositionMatrix(),
                  event.getImmediate(),
                  false,
                  0,
                  15728880,
                  mc.textRenderer.isRightToLeft()
               );
            mc.textRenderer
               .draw(
                  "]",
                  (float)(x + width - bracketWidth),
                  (float)y,
                  Color.GRAY.getRGB(),
                  getHud().textShadows.get(),
                  event.getMatrices().peek().getPositionMatrix(),
                  event.getImmediate(),
                  false,
                  0,
                  15728880,
                  mc.textRenderer.isRightToLeft()
               );
         }
      }
   }

   private List<Module> getDrawnModules() {
      ArrayList<Module> drawn = new ArrayList<>();

      for(Module module : Modules.modules()) {
         if (module.drawn && module.isActive()) {
            drawn.add(module);
         }
      }

      return drawn;
   }

   private int moduleTextWidth(Module module) {
      StringBuilder builder = new StringBuilder();
      builder.append(module.getParsedName());
      String arrayText = module.getArrayText();
      if (!arrayText.isEmpty()) {
         builder.append(" [");
         builder.append(arrayText);
         builder.append("]");
      }

      return (double)(-mc.textRenderer.getWidth(builder.toString()));
   }

   @Override
   public int[] getBounds() {
      List<Module> modules = this.getDrawnModules();
      modules.sort(Comparator.comparingDouble(this::moduleTextWidth));
      int width = mc.textRenderer.getWidth(modules.isEmpty() ? this.getParsedName() : modules.get(0).getParsedName());
      int height = Math.max(1, modules.size()) * 9;
      if (this.getY() > mc.getWindow().getScaledHeight() / 2) {
         height *= -1;
      }

      return new int[]{width, height};
   }
}
