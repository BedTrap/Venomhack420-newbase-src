package de.tyrannus.venomhack.modules.render;

import com.google.gson.JsonObject;
import de.tyrannus.venomhack.gui.ClickGuiScreen;
import de.tyrannus.venomhack.modules.Module;
import de.tyrannus.venomhack.settings.Setting;
import java.awt.Color;

public class ClickGui extends Module {
   private final Setting<ClickGui.Presets> preset = this.setting("preset", "What gui to use", ClickGui.Presets.DEFAULT, this::applyPreset);
   public final Setting<ClickGui.Indicators> indicator = this.setting(
      "indicator", "How to indicate if a module is opened.", ClickGui.Indicators.PLUS, this::change
   );
   public final Setting<Boolean> textShadows = this.setting("text-shadows", "Whether to draw shadows behind text or not.", Boolean.valueOf(true));
   public final Setting<Boolean> settingWidth = this.setting(
      "setting-width", "Accounts for the longest setting name when building gui.", Boolean.valueOf(true), v -> ClickGuiScreen.setMaxWidth()
   );
   public final Setting<Boolean> playClickSound = this.setting("click-sound", "Plays a click sound when you interact with the gui.", Boolean.valueOf(true));
   public final Setting<Color> categoryColor = this.setting(
      "category-color", "Color the category names get rendered in.", new Color(255, 0, 0, 180), this::change
   );
   public final Setting<Color> backgroundColor = this.setting(
      "background-color", "The color of the background the settings get rendered on.", new Color(75, 75, 75, 100), this::change
   );
   public final Setting<Color> settingBackgroundColor = this.setting(
      "setting-background", "The color of the background the settings get rendered on.", new Color(37, 37, 37, 100), this::change
   );
   public final Setting<Color> activeModuleColor = this.setting(
      "active-module-color", "Color of the background the active modules get.", new Color(255, 0, 0, 255), this::change
   );
   public final Setting<Color> activeColor = this.setting("active-color", "Color of the background the active get.", new Color(255, 0, 0, 100), this::change);
   public final Setting<Color> textColor = this.setting("text-color", "The text color for settings and categories.", Color.WHITE, this::change);
   public final Setting<Color> secondaryTextColor = this.setting("text-color-2", "The secondary text color for settings.", Color.LIGHT_GRAY, this::change);
   private ClickGuiScreen screen;
   private boolean changeToCustom = true;

   public ClickGui() {
      super(Module.Categories.RENDER, "click-gui", "Opens a gui to configure most parts about the client.");
      this.bind.set(345);
      this.silent = true;
   }

   private void change(Object ignored) {
      if (this.changeToCustom) {
         this.preset.set(ClickGui.Presets.CUSTOM);
      }
   }

   private void applyPreset(ClickGui.Presets preset) {
      if (ClickGuiScreen.DROP_DOWNS[0] != null) {
         boolean changeBack = this.changeToCustom;
         this.changeToCustom = false;
         switch(preset) {
            case PANCAKE:
               this.indicator.set(ClickGui.Indicators.DOT);
               this.categoryColor.set(new Color(110, 104, 255, 255));
               this.backgroundColor.set(new Color(0, 0, 0, 140));
               this.settingBackgroundColor.set(new Color(0, 0, 0, 140));
               this.activeModuleColor.set(new Color(110, 104, 255, 255));
               this.activeColor.set(new Color(110, 104, 255, 255));
               this.textColor.set(new Color(255, 255, 255, 255));
               this.secondaryTextColor.set(new Color(192, 192, 192, 255));
               ClickGuiScreen.DROP_DOWNS[0].setXY(20, 20);
               ClickGuiScreen.DROP_DOWNS[1].setXY(175, 20);
               ClickGuiScreen.DROP_DOWNS[2].setXY(330, 20);
               ClickGuiScreen.DROP_DOWNS[3].setXY(485, 20);
               ClickGuiScreen.DROP_DOWNS[4].setXY(640, 20);
               ClickGuiScreen.DROP_DOWNS[5].setXY(795, 20);
            case CUSTOM:
               break;
            default:
               this.indicator.reset();
               this.categoryColor.reset();
               this.backgroundColor.reset();
               this.settingBackgroundColor.reset();
               this.activeModuleColor.reset();
               this.activeColor.reset();
               this.textColor.reset();
               this.secondaryTextColor.reset();
               ClickGuiScreen.buildGUI();
         }

         if (changeBack) {
            this.changeToCustom = true;
         }
      }
   }

   public ClickGuiScreen getScreen() {
      return this.screen;
   }

   @Override
   public void onEnable() {
      this.screen = new ClickGuiScreen();
      mc.setScreen(this.screen);
   }

   @Override
   public void onDisable() {
      if (mc.currentScreen instanceof ClickGuiScreen) {
         mc.setScreen(null);
      }
   }

   @Override
   public JsonObject packConfig() {
      this.active = false;
      return super.packConfig();
   }

   @Override
   public void unpackConfig(JsonObject config) {
      this.changeToCustom = false;
      super.unpackConfig(config);
      this.changeToCustom = true;
   }

   public static enum Indicators {
      NONE("", ""),
      DOT(" ... ", "⋮ "),
      PLUS(" + ", " - "),
      ARROWS(" ⫸ ", " ⩔ ");

      private final String open;
      private final String closed;

      private Indicators(String open, String closed) {
         this.open = open;
         this.closed = closed;
      }

      public String get(boolean closed) {
         return closed ? this.open : this.closed;
      }
   }

   public static enum Presets {
      DEFAULT,
      PANCAKE,
      CUSTOM;
   }
}
