package de.tyrannus.venomhack.settings.settings;

import com.google.gson.JsonElement;
import de.tyrannus.venomhack.gui.ClickGuiScreen;
import de.tyrannus.venomhack.gui.DropDown;
import de.tyrannus.venomhack.settings.IChange;
import de.tyrannus.venomhack.settings.IVisible;
import de.tyrannus.venomhack.settings.Setting;

public class StringSetting extends Setting<String> {
   public StringSetting(String name, String description, String defaultValue, IChange<String> onChanged, IVisible visible) {
      super(name, description, defaultValue, onChanged, visible);
   }

   @Override
   public boolean parseValue(String value) {
      this.set(value);
      return true;
   }

   @Override
   public void load(JsonElement element) {
      this.set(element.getAsString());
   }

   @Override
   public Object storeSetting() {
      return this.get();
   }

   @Override
   public boolean onMouseClick(ClickGuiScreen screen, DropDown dropDown, int y, int button, double mouseX, double mouseY) {
      if (button == 2) {
         this.reset();
         return true;
      } else {
         return false;
      }
   }
}
