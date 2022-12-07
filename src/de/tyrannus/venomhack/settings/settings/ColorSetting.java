package de.tyrannus.venomhack.settings.settings;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import de.tyrannus.venomhack.Venomhack;
import de.tyrannus.venomhack.gui.ClickGuiScreen;
import de.tyrannus.venomhack.gui.DropDown;
import de.tyrannus.venomhack.gui.GuiElement;
import de.tyrannus.venomhack.modules.render.ClickGui;
import de.tyrannus.venomhack.settings.IChange;
import de.tyrannus.venomhack.settings.IVisible;
import de.tyrannus.venomhack.settings.Setting;
import java.awt.Color;
import net.minecraft.client.gui.DrawableHelper;
import net.minecraft.client.util.math.MatrixStack;

public class ColorSetting extends Setting<Color> {
   private boolean closed = true;
   private final ColorSetting.ColorSlider[] settings = new ColorSetting.ColorSlider[]{
      new ColorSetting.ColorSlider("red", 2),
      new ColorSetting.ColorSlider("green", 1),
      new ColorSetting.ColorSlider("blue", 0),
      new ColorSetting.ColorSlider("alpha", 3)
   };

   public ColorSetting(String name, String description, Color defaultValue, IChange<Color> onChanged, IVisible visible) {
      super(name, description, defaultValue, onChanged, visible);
   }

   @Override
   public boolean onMouseClick(ClickGuiScreen screen, DropDown dropDown, int y, int button, double mouseX, double mouseY) {
      if (!this.closed && !ClickGuiScreen.isMouseOver(dropDown, mouseX, mouseY, y, this)) {
         y += GuiElement.getDefaultHeight();

         for(ColorSetting.ColorSlider setting : this.settings) {
            if (ClickGuiScreen.isMouseOver(dropDown, mouseX, mouseY, y, setting)) {
               return setting.onMouseClick(screen, dropDown, y, button, mouseX, mouseY);
            }

            y += setting.getHeight();
         }

         return false;
      } else if (button == 1) {
         this.closed = !this.closed;
         return true;
      } else if (button == 2) {
         this.reset();
         return true;
      } else {
         return false;
      }
   }

   @Override
   public void render(DropDown dropDown, MatrixStack matrices, int x, int y, double mouseX, double mouseY, ClickGui gui) {
      if (this.isVisible()) {
         super.render(dropDown, matrices, x, y, mouseX, mouseY, gui);
         int argb = gui.settingBackgroundColor.get().getRGB();
         String indicator = gui.indicator.get().get(true);
         int indicatorX = dropDown.getX() + dropDown.getMaxWidth() - Venomhack.mc.textRenderer.getWidth(indicator);
         if (indicator.isEmpty()) {
            indicatorX -= 3;
         }

         if (ClickGuiScreen.isMouseOver(dropDown, mouseX, mouseY, y, this)) {
            argb = ClickGuiScreen.doubleAlpha(argb);
         }

         DrawableHelper.fill(matrices, dropDown.getX() + 2, y, indicatorX + 1 - 9, y + GuiElement.getDefaultHeight(), argb);
         DrawableHelper.fill(
            matrices, indicatorX + 1, y, dropDown.getX() + dropDown.getMaxWidth() - GuiElement.sidePadding() - 1, y + GuiElement.getDefaultHeight(), argb
         );
         DrawableHelper.fill(matrices, indicatorX + 1 - 9, y, indicatorX + 1, y + GuiElement.getDefaultHeight(), this.get().getRGB());
         if (!this.closed) {
            indicator = gui.indicator.get().get(false);
            indicatorX = dropDown.getX() + dropDown.getMaxWidth() - Venomhack.mc.textRenderer.getWidth(indicator);
         }

         ClickGuiScreen.drawText(matrices, this.parsedName(), x, y, gui.textColor.get());
         ClickGuiScreen.drawText(matrices, indicator, indicatorX, y, gui.textColor.get());
         y += GuiElement.getDefaultHeight();

         for(ColorSetting.ColorSlider setting : this.settings) {
            setting.render(dropDown, matrices, x, y, mouseX, mouseY, gui);
            y += setting.getHeight();
         }
      }
   }

   @Override
   public int getHeight() {
      if (!this.isVisible()) {
         return 0;
      } else {
         int height = super.getHeight();

         for(ColorSetting.ColorSlider setting : this.settings) {
            height += setting.getHeight();
         }

         return height;
      }
   }

   @Override
   public boolean parseValue(String value) {
      String[] numbers = value.split(" ");
      switch(numbers.length) {
         case 3:
            this.set(new Color(Integer.parseInt(numbers[0]), Integer.parseInt(numbers[1]), Integer.parseInt(numbers[2])));
            break;
         case 4:
            this.set(new Color(Integer.parseInt(numbers[0]), Integer.parseInt(numbers[1]), Integer.parseInt(numbers[2]), Integer.parseInt(numbers[3])));
            break;
         default:
            return false;
      }

      return true;
   }

   @Override
   public void load(JsonElement element) {
      JsonArray array = element.getAsJsonArray();
      this.set(new Color(array.get(0).getAsInt(), array.get(1).getAsInt(), array.get(2).getAsInt(), array.get(3).getAsInt()));
   }

   @Override
   public Object storeSetting() {
      JsonArray array = new JsonArray();
      array.add(this.get().getRed());
      array.add(this.get().getGreen());
      array.add(this.get().getBlue());
      array.add(this.get().getAlpha());
      return array;
   }

   private class ColorSlider extends IntSetting {
      private final int aspect;

      public ColorSlider(String name, int aspect) {
         super(name, "The aspect " + name + " for this color.", ColorSetting.this.getDefaultValue().getRGB() >> aspect * 8 & 0xFF, null, null, 0.0F, 255.0F);
         this.aspect = aspect;
      }

      public Integer get() {
         return ColorSetting.this.get().getRGB() >> this.aspect * 8 & 0xFF;
      }

      public void set(Integer value) {
         int oldColor = ColorSetting.this.get().getRGB();
         int newC = value << this.aspect * 8 | oldColor & ~(255 << this.aspect * 8);
         ColorSetting.this.set(new Color(newC, true));
      }

      @Override
      public boolean isVisible() {
         return !ColorSetting.this.closed;
      }
   }
}
