package de.tyrannus.venomhack.settings.settings;

import de.tyrannus.venomhack.Venomhack;
import de.tyrannus.venomhack.gui.ClickGuiScreen;
import de.tyrannus.venomhack.gui.DropDown;
import de.tyrannus.venomhack.gui.GuiElement;
import de.tyrannus.venomhack.modules.render.ClickGui;
import de.tyrannus.venomhack.settings.IChange;
import de.tyrannus.venomhack.settings.IVisible;
import de.tyrannus.venomhack.settings.Setting;
import de.tyrannus.venomhack.utils.MathUtil;
import net.minecraft.util.math.MathHelper;
import net.minecraft.client.util.math.MatrixStack;

public abstract class SliderSetting<T extends Number> extends Setting<T> {
   public final float sliderMin;
   public final float sliderMax;
   protected final int precision;
   protected int dropDownX;
   protected int maxWidth;

   public SliderSetting(
      String name, String description, T defaultValue, IChange<T> onChanged, IVisible visible, float sliderMin, float sliderMax, int precision
   ) {
      super(name, description, defaultValue, onChanged, visible);
      this.sliderMin = sliderMin;
      this.sliderMax = sliderMax;
      this.precision = precision;
   }

   public SliderSetting(String name, String description, T defaultValue, IChange<T> onChanged, IVisible visible, float sliderMin, float sliderMax) {
      this(name, description, defaultValue, onChanged, visible, sliderMin, sliderMax, 1);
   }

   @Override
   public boolean onMouseClick(ClickGuiScreen screen, DropDown dropDown, int y, int button, double mouseX, double mouseY) {
      if (button == 2) {
         this.reset();
         return true;
      } else if (button == 1) {
         return false;
      } else {
         this.dropDownX = dropDown.getX() + GuiElement.sidePadding();
         this.maxWidth = dropDown.getMaxWidth() - GuiElement.sidePadding() * 2;
         double newValue = sliderPercentage(mouseX, this.dropDownX, this.maxWidth) * (double)(this.sliderMax - this.sliderMin) + (double)this.sliderMin;
         if (this instanceof IntSetting intSetting) {
            intSetting.set(Integer.valueOf((int)Math.round(newValue)));
         } else if (this instanceof FloatSetting floatSetting) {
            floatSetting.set(Float.valueOf((float)MathUtil.round(newValue, this.precision)));
         }

         screen.setFocused(this);
         return true;
      }
   }

   @Override
   public void render(DropDown dropDown, MatrixStack matrices, int x, int y, double mouseX, double mouseY, ClickGui gui) {
      if (this.isVisible()) {
         double percentage = sliderPercentage(this.get().doubleValue(), (double)this.sliderMin, (double)this.sliderMax);
         int fillWidth = (int)((double)(dropDown.getMaxWidth() - 2) * percentage);
         if (percentage > 0.0) {
            ClickGuiScreen.drawBackground(
               matrices,
               false,
               ClickGuiScreen.isMouseOver(dropDown, mouseX, mouseY, y, this),
               dropDown.getX() + GuiElement.sidePadding(),
               y,
               dropDown.getX() + GuiElement.sidePadding() + fillWidth,
               y + GuiElement.getDefaultHeight(),
               true
            );
         }

         if (percentage < 1.0) {
            ClickGuiScreen.drawBackground(
               matrices,
               false,
               ClickGuiScreen.isMouseOver(dropDown, mouseX, mouseY, y, this),
               dropDown.getX() + GuiElement.sidePadding() + fillWidth,
               y,
               dropDown.getX() + dropDown.getMaxWidth() - GuiElement.sidePadding(),
               y + GuiElement.getDefaultHeight(),
               false
            );
         }

         String text = String.valueOf(this.get());
         if (this instanceof FloatSetting floatSetting) {
            text = MathUtil.format(MathUtil.round(floatSetting.get(), this.precision));
         }

         super.render(dropDown, matrices, x, y, mouseX, mouseY, gui);
         ClickGuiScreen.drawText(matrices, text, x + Venomhack.mc.textRenderer.getWidth(this.parsedName() + " "), y, gui.secondaryTextColor.get());
      }
   }

   public abstract void onDragged(double var1);

   public static double sliderPercentage(double mouseX, int dropDownX, int dropDownWidth) {
      return MathHelper.clamp((mouseX - (double)dropDownX) / (double)dropDownWidth, 0.0, 1.0);
   }

   public static double sliderPercentage(double value, double sliderMin, double sliderMax) {
      return MathHelper.clamp((value - sliderMin) / (sliderMax - sliderMin), 0.0, 1.0);
   }

   public int getPrecision() {
      return this.precision;
   }
}
