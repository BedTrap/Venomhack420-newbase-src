package de.tyrannus.venomhack.settings.settings;

import com.google.gson.JsonElement;
import de.tyrannus.venomhack.settings.IChange;
import de.tyrannus.venomhack.settings.IVisible;
import java.util.Arrays;
import java.util.List;

public class StringListSetting extends ListSetting<String> {
   public StringListSetting(String name, String description, IChange<List<String>> onChanged, IVisible visible, String... defaultValue) {
      super(name, description, onChanged, visible, defaultValue);
   }

   public String transform(JsonElement element) {
      return element.getAsString();
   }

   @Override
   public boolean parseValue(String value) {
      this.set(Arrays.asList(value.split(";")));
      return true;
   }
}
