package de.tyrannus.venomhack.settings.settings;

import com.google.gson.JsonElement;
import de.tyrannus.venomhack.Venomhack;
import de.tyrannus.venomhack.gui.ClickGuiScreen;
import de.tyrannus.venomhack.gui.DropDown;
import de.tyrannus.venomhack.modules.render.ClickGui;
import de.tyrannus.venomhack.settings.IChange;
import de.tyrannus.venomhack.settings.IVisible;
import de.tyrannus.venomhack.settings.Keybind;
import de.tyrannus.venomhack.settings.Setting;
import de.tyrannus.venomhack.utils.Utils;
import net.minecraft.client.util.InputUtil;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.client.util.InputUtil.class_306;
import net.minecraft.client.util.InputUtil.class_307;

public class KeySetting extends Setting<Keybind> {
   public KeySetting(String name, String description, Keybind defaultValue, IChange<Keybind> onChanged, IVisible visible) {
      super(name, description, defaultValue, onChanged, visible);
   }

   @Override
   public boolean onMouseClick(ClickGuiScreen screen, DropDown dropDown, int y, int button, double mouseX, double mouseY) {
      if (button == 2) {
         this.reset();
         return true;
      } else if (button != 0) {
         return false;
      } else {
         screen.setFocused(this);
         this.set(Keybind.BINDING);
         return true;
      }
   }

   @Override
   public void render(DropDown dropDown, MatrixStack matrices, int x, int y, double mouseX, double mouseY, ClickGui gui) {
      if (this.isVisible()) {
         ClickGuiScreen.drawBackground(matrices, false, ClickGuiScreen.isMouseOver(dropDown, mouseX, mouseY, y, this), dropDown, y, false);
         super.render(dropDown, matrices, x, y, mouseX, mouseY, gui);
         if (this.get() == Keybind.BINDING || this.get().getKey().getCategory() != class_307.MOUSE && this.get().getCode() <= 0) {
            ClickGuiScreen.drawText(
               matrices,
               this.get() == Keybind.BINDING ? "Listening..." : "None",
               x + Venomhack.mc.textRenderer.getWidth("Bind "),
               y,
               gui.secondaryTextColor.get()
            );
         } else {
            ClickGuiScreen.drawText(
               matrices, this.get().getKey().getLocalizedText(), x + Venomhack.mc.textRenderer.getWidth("Bind "), y, gui.secondaryTextColor.get()
            );
         }
      }
   }

   public void set(int keycode) {
      this.set(InputUtil.fromKeyCode(keycode, 0));
   }

   public void set(String translationKey) {
      this.set(InputUtil.fromTranslationKey(translationKey));
   }

   public void set(class_306 key) {
      this.set(new Keybind(key));
   }

   public boolean equalsKey(class_306 key) {
      return key.equals(this.getKey());
   }

   public boolean isPressed() {
      return Utils.isKeyPressed(this.getCode());
   }

   public int getCode() {
      return this.get().getCode();
   }

   public class_306 getKey() {
      return this.get().getKey();
   }

   @Override
   public boolean parseValue(String value) {
      return false;
   }

   @Override
   public void load(JsonElement element) {
      this.set(element.getAsInt());
   }

   @Override
   public Object storeSetting() {
      return this.get().getCode();
   }
}
