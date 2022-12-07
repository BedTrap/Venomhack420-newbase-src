package de.tyrannus.venomhack.settings.settings;

import de.tyrannus.venomhack.Venomhack;
import de.tyrannus.venomhack.gui.ClickGuiScreen;
import de.tyrannus.venomhack.gui.DropDown;
import de.tyrannus.venomhack.gui.GuiElement;
import de.tyrannus.venomhack.modules.render.ClickGui;
import de.tyrannus.venomhack.modules.render.hud.HudElement;
import de.tyrannus.venomhack.modules.render.hud.HudElements;
import de.tyrannus.venomhack.settings.Setting;
import net.minecraft.client.util.math.MatrixStack;

public class HudElementSetting extends BoolSetting {
   private final HudElement element;
   private boolean closed = true;

   public HudElementSetting(HudElement element) {
      super(element.getName(), element.description, true, null, null);
      this.element = element;
   }

   @Override
   public boolean onMouseClick(ClickGuiScreen screen, DropDown dropDown, int y, int button, double mouseX, double mouseY) {
      if (!this.closed && !ClickGuiScreen.isMouseOver(dropDown, mouseX, mouseY, y, this)) {
         y += GuiElement.getDefaultHeight();

         for(Setting<?> setting : this.element.SETTINGS) {
            if (ClickGuiScreen.isMouseOverEntireElement(dropDown, mouseX, mouseY, y, setting)) {
               return setting.onMouseClick(screen, dropDown, y, button, mouseX, mouseY);
            }

            y += setting.getHeight();
         }

         return false;
      } else if (button == 0) {
         this.set(!this.get());
         return true;
      } else if (button == 1) {
         if (this.element.SETTINGS.isEmpty()) {
            return false;
         } else {
            this.closed = !this.closed;
            return true;
         }
      } else {
         return false;
      }
   }

   @Override
   public void render(DropDown dropDown, MatrixStack matrices, int x, int y, double mouseX, double mouseY, ClickGui gui) {
      if (this.isVisible()) {
         super.render(dropDown, matrices, x, y, mouseX, mouseY, gui);
         if (!this.element.SETTINGS.isEmpty()) {
            String indicator = gui.indicator.get().get(this.closed);
            ClickGuiScreen.drawText(
               matrices, indicator, dropDown.getX() + dropDown.getMaxWidth() - Venomhack.mc.textRenderer.getWidth(indicator), y, gui.textColor.get()
            );
         }

         if (!this.closed) {
            y += GuiElement.getDefaultHeight();

            for(Setting<?> setting : this.element.SETTINGS) {
               setting.render(dropDown, matrices, x, y, mouseX, mouseY, gui);
               y += setting.getHeight();
            }
         }
      }
   }

   @Override
   public int getHeight() {
      if (!this.isVisible()) {
         return 0;
      } else {
         int height = super.getHeight();
         if (this.closed) {
            return height;
         } else {
            for(Setting<?> setting : this.element.SETTINGS) {
               height += setting.getHeight();
            }

            return height;
         }
      }
   }

   public void set(Boolean value) {
      super.set(value);
      if (HudElements.initialized) {
         this.element.toggle();
      }
   }

   public HudElement getElement() {
      return this.element;
   }

   public Boolean get() {
      return this.element.isActive();
   }
}
