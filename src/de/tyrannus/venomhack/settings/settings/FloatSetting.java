package de.tyrannus.venomhack.settings.settings;

import com.google.gson.JsonElement;
import de.tyrannus.venomhack.settings.IChange;
import de.tyrannus.venomhack.settings.IVisible;
import de.tyrannus.venomhack.utils.MathUtil;

public class FloatSetting extends SliderSetting<Float> {
   public FloatSetting(
      String name, String description, Float defaultValue, IChange<Float> onChanged, IVisible visible, float sliderMin, float sliderMax, int precision
   ) {
      super(name, description, defaultValue, onChanged, visible, sliderMin, sliderMax, precision);
   }

   @Override
   public boolean parseValue(String value) {
      try {
         this.set(Float.valueOf(value));
         return true;
      } catch (NumberFormatException var3) {
         return false;
      }
   }

   @Override
   public void onDragged(double mouseX) {
      this.set(
         Float.valueOf(
            (float)MathUtil.round(
               sliderPercentage(mouseX, this.dropDownX, this.maxWidth) * (double)(this.sliderMax - this.sliderMin) + (double)this.sliderMin, this.precision
            )
         )
      );
   }

   @Override
   public void load(JsonElement element) {
      this.set(Float.valueOf(element.getAsFloat()));
   }

   @Override
   public Object storeSetting() {
      return this.get();
   }
}
