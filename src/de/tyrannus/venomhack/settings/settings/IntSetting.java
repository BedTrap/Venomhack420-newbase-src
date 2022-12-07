package de.tyrannus.venomhack.settings.settings;

import com.google.gson.JsonElement;
import de.tyrannus.venomhack.settings.IChange;
import de.tyrannus.venomhack.settings.IVisible;

public class IntSetting extends SliderSetting<Integer> {
   public IntSetting(String name, String description, Integer defaultValue, IChange<Integer> onChanged, IVisible visible, float sliderMin, float sliderMax) {
      super(name, description, defaultValue, onChanged, visible, sliderMin, sliderMax);
   }

   @Override
   public boolean parseValue(String value) {
      try {
         this.set(Integer.valueOf(value));
         return true;
      } catch (NumberFormatException var3) {
         return false;
      }
   }

   @Override
   public void onDragged(double mouseX) {
      this.set(
         Integer.valueOf(
            (int)Math.round(sliderPercentage(mouseX, this.dropDownX, this.maxWidth) * (double)(this.sliderMax - this.sliderMin) + (double)this.sliderMin)
         )
      );
   }

   @Override
   public void load(JsonElement element) {
      this.set(Integer.valueOf(element.getAsInt()));
   }

   @Override
   public Object storeSetting() {
      return this.get();
   }
}
