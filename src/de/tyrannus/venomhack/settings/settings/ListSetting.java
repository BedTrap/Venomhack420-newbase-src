package de.tyrannus.venomhack.settings.settings;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import de.tyrannus.venomhack.gui.ClickGuiScreen;
import de.tyrannus.venomhack.gui.DropDown;
import de.tyrannus.venomhack.settings.IChange;
import de.tyrannus.venomhack.settings.IVisible;
import de.tyrannus.venomhack.settings.Setting;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

public abstract class ListSetting<T> extends Setting<List<T>> {
   @SafeVarargs
   public ListSetting(String name, String description, IChange<List<T>> onChanged, IVisible visible, T... defaultValue) {
      super(name, description, new ArrayList<>(List.of(defaultValue)), onChanged, visible);
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

   @Override
   public String storeValue() {
      StringBuilder builder = new StringBuilder();

      for(int i = 0; i < this.get().size(); ++i) {
         builder.append(this.get().get(i));
         if (i < this.get().size() - 1) {
            builder.append(";");
         }
      }

      return builder.toString();
   }

   @Override
   public void load(JsonElement element) {
      LinkedList<T> list = new LinkedList<>();

      for(JsonElement value : element.getAsJsonArray()) {
         list.add(this.transform(value));
      }

      this.set((T)list);
   }

   public abstract T transform(JsonElement var1);

   @Override
   public Object storeSetting() {
      JsonArray array = new JsonArray();

      for(T val : this.get()) {
         array.add(val.toString());
      }

      return array;
   }
}
