package de.tyrannus.venomhack.settings.settings;

import com.google.gson.JsonElement;
import de.tyrannus.venomhack.gui.ClickGuiScreen;
import de.tyrannus.venomhack.gui.DropDown;
import de.tyrannus.venomhack.modules.render.ClickGui;
import de.tyrannus.venomhack.settings.IChange;
import de.tyrannus.venomhack.settings.IVisible;
import de.tyrannus.venomhack.settings.Setting;
import java.util.Arrays;
import java.util.List;
import net.minecraft.client.util.math.MatrixStack;

public class BoolSetting extends Setting<Boolean> {
   public BoolSetting(String name, String description, boolean defaultValue, IChange<Boolean> onChanged, IVisible visible) {
      super(name, description, defaultValue, onChanged, visible);
   }

   @Override
   public void render(DropDown dropDown, MatrixStack matrices, int x, int y, double mouseX, double mouseY, ClickGui gui) {
      if (this.isVisible()) {
         ClickGuiScreen.drawBackground(matrices, false, ClickGuiScreen.isMouseOver(dropDown, mouseX, mouseY, y, this), dropDown, y, this.get());
         super.render(dropDown, matrices, x, y, mouseX, mouseY, gui);
      }
   }

   @Override
   public boolean parseValue(String value) {
      if (value.equalsIgnoreCase("true")) {
         this.set(Boolean.valueOf(true));
      } else {
         if (!value.equalsIgnoreCase("false")) {
            return false;
         }

         this.set(Boolean.valueOf(false));
      }

      return true;
   }

   @Override
   public void load(JsonElement element) {
      this.set(Boolean.valueOf(element.getAsBoolean()));
   }

   @Override
   public Object storeSetting() {
      return this.get();
   }

   @Override
   public List<String> getSuggestions() {
      return Arrays.asList("true", "false");
   }

   @Override
   public boolean onMouseClick(ClickGuiScreen screen, DropDown dropDown, int y, int button, double mouseX, double mouseY) {
      if (button == 2) {
         this.reset();
         return true;
      } else if (button == 1) {
         return false;
      } else {
         this.set(Boolean.valueOf(!this.get()));
         return true;
      }
   }
}
