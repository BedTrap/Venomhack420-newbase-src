package de.tyrannus.venomhack.settings;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import de.tyrannus.venomhack.Venomhack;
import de.tyrannus.venomhack.modules.Module;
import de.tyrannus.venomhack.settings.settings.BoolSetting;
import de.tyrannus.venomhack.settings.settings.ColorSetting;
import de.tyrannus.venomhack.settings.settings.EnumSetting;
import de.tyrannus.venomhack.settings.settings.FloatSetting;
import de.tyrannus.venomhack.settings.settings.IntSetting;
import de.tyrannus.venomhack.settings.settings.ItemListSetting;
import de.tyrannus.venomhack.settings.settings.KeySetting;
import de.tyrannus.venomhack.settings.settings.ListSetting;
import de.tyrannus.venomhack.settings.settings.StringListSetting;
import de.tyrannus.venomhack.settings.settings.StringSetting;
import de.tyrannus.venomhack.utils.TextUtils;
import java.awt.Color;
import java.util.ArrayList;
import java.util.List;
import net.minecraft.item.Item;
import net.minecraft.client.MinecraftClient;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public abstract class AbstractSettingHolder {
   public final List<Setting<?>> SETTINGS = new ArrayList<>();
   protected boolean active;
   protected final String name;
   public final String description;
   protected static final MinecraftClient mc = MinecraftClient.getInstance();

   public AbstractSettingHolder(String name, String description) {
      this.name = TextUtils.inverseParse(name);
      this.description = description;
   }

   public boolean isActive() {
      return this.active;
   }

   public void setActive(boolean active) {
      if (this.active ^ active) {
         this.toggle();
      }
   }

   public String getParsedName() {
      return TextUtils.parseName(this.name);
   }

   public String getName() {
      return this.name;
   }

   public abstract void toggle();

   public JsonObject packConfig() {
      JsonObject config = new JsonObject();
      config.addProperty("active", this.active);

      for(Setting<?> setting : this.SETTINGS) {
         if (setting instanceof BoolSetting) {
            config.addProperty(setting.getName(), setting.storeSetting());
         } else if (setting instanceof IntSetting || setting instanceof KeySetting) {
            config.addProperty(setting.getName(), setting.storeSetting());
         } else if (setting instanceof FloatSetting) {
            config.addProperty(setting.getName(), setting.storeSetting());
         } else if (!(setting instanceof ListSetting) && !(setting instanceof ColorSetting)) {
            config.addProperty(setting.getName(), (String)setting.storeSetting());
         } else {
            config.add(setting.getName(), (JsonArray)setting.storeSetting());
         }
      }

      return config;
   }

   public void unpackConfig(JsonObject config) {
      if (config.has("active")) {
         this.active = config.get("active").getAsBoolean();
         if (this.active) {
            Venomhack.EVENTS.subscribe(this);
         }
      }

      for(Setting<?> setting : this.SETTINGS) {
         JsonElement element = config.get(setting.getName());
         if (element != null) {
            setting.load(element);
         }
      }
   }

   @Nullable
   public Setting<?> getSetting(String setting) {
      for(Setting<?> set : this.SETTINGS) {
         if (set.getName().equalsIgnoreCase(setting)) {
            return set;
         }
      }

      return null;
   }

   private <T> Setting<T> add(Setting<?> setting) {
      this.SETTINGS.add(setting);
      return setting;
   }

   protected final <T> Setting<T> setting(String name, String description, T defaultValue) {
      return this.setting(name, description, defaultValue, null, null);
   }

   protected final <T> Setting<T> setting(String name, String description, T defaultValue, IChange<T> onChanged) {
      return this.setting(name, description, defaultValue, onChanged, null);
   }

   protected final <T> Setting<T> setting(String name, String description, T defaultValue, IVisible visible) {
      return this.setting(name, description, defaultValue, null, visible);
   }

   @NotNull
   protected final <T> Setting<T> setting(String name, String description, T defaultValue, IChange<T> onChanged, IVisible visible) {
      if (defaultValue instanceof Boolean) {
         return this.add(new BoolSetting(name, description, (Boolean)defaultValue, onChanged, visible));
      } else if (defaultValue instanceof Integer) {
         return this.add(new IntSetting(name, description, (Integer)defaultValue, onChanged, visible, 0.0F, 10.0F));
      } else if (defaultValue instanceof Float) {
         return this.add(new FloatSetting(name, description, (Float)defaultValue, onChanged, visible, 0.0F, 10.0F, 1));
      } else if (defaultValue instanceof Enum) {
         return this.add(new EnumSetting(name, description, (T)((Enum)defaultValue), onChanged, visible));
      } else if (defaultValue instanceof String) {
         return this.add(new StringSetting(name, description, (String)defaultValue, onChanged, visible));
      } else if (defaultValue instanceof Color) {
         return this.add(new ColorSetting(name, description, (Color)defaultValue, onChanged, visible));
      } else {
         return defaultValue instanceof Keybind ? this.add(new KeySetting(name, description, (Keybind)defaultValue, onChanged, visible)) : null;
      }
   }

   protected final <T> Setting<T> setting(String name, String description, T defaultValue, float sliderMin, float sliderMax) {
      return this.setting(name, description, defaultValue, null, null, sliderMin, sliderMax);
   }

   protected final <T> Setting<T> setting(String name, String description, T defaultValue, IChange<T> onChanged, float sliderMin, float sliderMax) {
      return this.setting(name, description, defaultValue, onChanged, null, sliderMin, sliderMax);
   }

   protected final <T> Setting<T> setting(String name, String description, T defaultValue, IVisible visible, float sliderMin, float sliderMax) {
      return this.setting(name, description, defaultValue, null, visible, sliderMin, sliderMax);
   }

   protected final <T> Setting<T> setting(
      String name, String description, T defaultValue, IChange<T> onChanged, IVisible visible, float sliderMin, float sliderMax
   ) {
      return defaultValue instanceof Integer
         ? this.add(new IntSetting(name, description, (Integer)defaultValue, onChanged, visible, sliderMin, sliderMax))
         : this.add(new FloatSetting(name, description, (Float)defaultValue, onChanged, visible, sliderMin, sliderMax, 1));
   }

   protected final Setting<Float> setting(
      String name, String description, float defaultValue, IChange<Float> onChanged, IVisible visible, float sliderMin, float sliderMax, int precision
   ) {
      return this.add(new FloatSetting(name, description, defaultValue, onChanged, visible, sliderMin, sliderMax, precision));
   }

   protected final Setting<Float> setting(
      String name, String description, float defaultValue, IVisible visible, float sliderMin, float sliderMax, int precision
   ) {
      return this.add(new FloatSetting(name, description, defaultValue, null, visible, sliderMin, sliderMax, precision));
   }

   protected final Setting<Float> setting(String name, String description, float defaultValue, float sliderMin, float sliderMax, int precision) {
      return this.add(new FloatSetting(name, description, defaultValue, null, null, sliderMin, sliderMax, precision));
   }

   @SafeVarargs
   protected final <T> Setting<List<T>> listSetting(String name, String description, @NotNull T... defaultValues) {
      return this.listSetting(name, description, null, null, defaultValues);
   }

   @SafeVarargs
   @NotNull
   protected final <T> Setting<List<T>> listSetting(String name, String description, IChange<T> onChanged, IVisible visible, @NotNull T... defaultValues) {
      if (defaultValues[0] instanceof String) {
         return this.add(new StringListSetting(name, description, onChanged, visible, (String[])defaultValues));
      } else {
         return defaultValues[0] instanceof Item
            ? this.add(new ItemListSetting(name, description, onChanged, visible, (Item[])defaultValues))
            : null;
      }
   }

   @Override
   public int hashCode() {
      return this.name.hashCode() + this.description.hashCode() + this.SETTINGS.hashCode();
   }

   @Override
   public boolean equals(Object o) {
      if (this == o) {
         return true;
      } else if (!(o instanceof Module)) {
         return false;
      } else {
         Module module = (Module)o;
         return o.getClass() == this.getClass() && module.name.equals(this.name);
      }
   }
}
