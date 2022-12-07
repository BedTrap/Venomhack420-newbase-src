package de.tyrannus.venomhack.settings.settings;

import com.google.gson.JsonElement;
import de.tyrannus.venomhack.Venomhack;
import de.tyrannus.venomhack.gui.ClickGuiScreen;
import de.tyrannus.venomhack.gui.DropDown;
import de.tyrannus.venomhack.modules.render.ClickGui;
import de.tyrannus.venomhack.settings.IChange;
import de.tyrannus.venomhack.settings.IVisible;
import de.tyrannus.venomhack.settings.Setting;
import de.tyrannus.venomhack.utils.TextUtils;
import java.util.ArrayList;
import java.util.List;
import net.minecraft.client.util.math.MatrixStack;

public class EnumSetting<T extends Enum<T>> extends Setting<T> {
   public EnumSetting(String name, String description, T defaultValue, IChange<T> onChanged, IVisible visible) {
      super(name, description, defaultValue, onChanged, visible);
   }

   @Override
   public boolean onMouseClick(ClickGuiScreen screen, DropDown dropDown, int y, int button, double mouseX, double mouseY) {
      if (button == 2) {
         this.reset();
         return true;
      } else {
         this.setNextEnumValue(button == 1);
         return true;
      }
   }

   @Override
   public void render(DropDown dropDown, MatrixStack matrices, int x, int y, double mouseX, double mouseY, ClickGui gui) {
      if (this.isVisible()) {
         ClickGuiScreen.drawBackground(matrices, false, ClickGuiScreen.isMouseOver(dropDown, mouseX, mouseY, y, this), dropDown, y, false);
         ClickGuiScreen.drawText(
            matrices,
            TextUtils.parseName(this.get().toString().toLowerCase()),
            x + Venomhack.mc.textRenderer.getWidth(this.parsedName() + " "),
            y,
            gui.secondaryTextColor.get()
         );
         super.render(dropDown, matrices, x, y, mouseX, mouseY, gui);
      }
   }

   @Override
   public boolean parseValue(String value) {
      for(T enumValue : (Enum[])this.getDefaultValue().getDeclaringClass().getEnumConstants()) {
         if (enumValue.toString().equalsIgnoreCase(value)) {
            this.set(enumValue);
            return true;
         }
      }

      return false;
   }

   public void setNextEnumValue(boolean back) {
      T[] enums = this.get().getDeclaringClass().getEnumConstants();

      for(int i = 0; i < enums.length; ++i) {
         T enumValue = enums[i];
         if (this.get() == enumValue) {
            if (back) {
               if (--i < 0) {
                  i = enums.length - 1;
               }

               this.set(enums[i]);
               return;
            }

            if (++i == enums.length) {
               i = 0;
            }

            this.set(enums[i]);
            return;
         }
      }
   }

   @Override
   public void load(JsonElement element) {
      this.parseValue(element.getAsString());
   }

   @Override
   public Object storeSetting() {
      return this.get().toString();
   }

   @Override
   public List<String> getSuggestions() {
      ArrayList<String> suggestions = new ArrayList<>();

      for(T enumValue : (Enum[])this.getDefaultValue().getDeclaringClass().getEnumConstants()) {
         suggestions.add(enumValue.toString());
      }

      return suggestions;
   }
}
