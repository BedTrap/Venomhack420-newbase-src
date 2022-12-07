package de.tyrannus.venomhack.settings;

import com.google.gson.JsonElement;
import de.tyrannus.venomhack.gui.ClickGuiScreen;
import de.tyrannus.venomhack.gui.DropDown;
import de.tyrannus.venomhack.gui.GuiElement;
import de.tyrannus.venomhack.modules.render.ClickGui;
import de.tyrannus.venomhack.utils.TextUtils;
import java.util.ArrayList;
import java.util.List;
import net.minecraft.client.gui.DrawableHelper;
import net.minecraft.client.util.math.MatrixStack;

public abstract class Setting<T> implements GuiElement {
   private T value;
   private final String name;
   private final String description;
   private final T defaultValue;
   protected final IChange<T> onChanged;
   private final IVisible visible;

   public Setting(String name, String description, T defaultValue, IChange<T> onChanged) {
      this(name, description, defaultValue, onChanged, null);
   }

   public Setting(String name, String description, T defaultValue, IChange<T> onChanged, IVisible visible) {
      this.name = TextUtils.inverseParse(name);
      this.description = description;
      this.defaultValue = defaultValue;
      this.value = defaultValue;
      this.onChanged = onChanged;
      this.visible = visible;
   }

   public String getName() {
      return this.name;
   }

   public String parsedName() {
      return TextUtils.parseName(this.name);
   }

   public String getDescription() {
      return this.description;
   }

   public T get() {
      return this.value;
   }

   public T getDefaultValue() {
      return this.defaultValue;
   }

   public void reset() {
      this.set(this.defaultValue);
   }

   public void set(T value) {
      this.value = value;
      if (this.onChanged != null) {
         this.onChanged.onChanged(value);
      }
   }

   public boolean isVisible() {
      return this.visible == null || this.visible.visible();
   }

   public abstract boolean parseValue(String var1);

   public abstract void load(JsonElement var1);

   public String storeValue() {
      return this.value.toString();
   }

   public abstract Object storeSetting();

   public boolean isDefaultValue() {
      return this.defaultValue == this.value || this.defaultValue.equals(this.value);
   }

   public List<String> getSuggestions() {
      return new ArrayList<>(0);
   }

   @Override
   public int getHeight() {
      return this.isVisible() ? GuiElement.getDefaultHeight() : 0;
   }

   @Override
   public abstract boolean onMouseClick(ClickGuiScreen var1, DropDown var2, int var3, int var4, double var5, double var7);

   @Override
   public void render(DropDown dropDown, MatrixStack matrices, int x, int y, double mouseX, double mouseY, ClickGui gui) {
      if (this.isVisible()) {
         DrawableHelper.fill(matrices, dropDown.getX() + 1, y, dropDown.getX() + 2, y + GuiElement.getDefaultHeight(), gui.categoryColor.get().getRGB());
         DrawableHelper.fill(
            matrices,
            dropDown.getX() + dropDown.getMaxWidth() - 2,
            y,
            dropDown.getX() + dropDown.getMaxWidth() - 1,
            y + GuiElement.getDefaultHeight(),
            gui.categoryColor.get().getRGB()
         );
         ClickGuiScreen.drawText(matrices, this.parsedName(), x, y, gui.textColor.get());
      }
   }
}
