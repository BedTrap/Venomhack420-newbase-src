package de.tyrannus.venomhack.modules.render.hud;

import com.google.gson.JsonElement;
import com.google.gson.JsonIOException;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.mojang.blaze3d.systems.RenderSystem;
import de.tyrannus.venomhack.Venomhack;
import de.tyrannus.venomhack.gui.ClickGuiScreen;
import de.tyrannus.venomhack.gui.HudEditorScreen;
import de.tyrannus.venomhack.modules.Module;
import de.tyrannus.venomhack.settings.Setting;
import de.tyrannus.venomhack.settings.settings.HudElementSetting;
import java.awt.Color;
import java.io.FileNotFoundException;
import java.io.FileReader;
import net.minecraft.client.gui.screen.Screen;

public class Hud extends Module {
   public final Setting<Boolean> textShadows = this.setting("text-shadows", "Whether to draw shadows behind text or not.", Boolean.valueOf(true));
   public final Setting<Color> primaryColor = this.setting("primary-color", "The color for the left part of hud elements.", new Color(255, 0, 0));
   public final Setting<Color> secondaryColor = this.setting("secondary-color", "The color for the right part of hud elements.", new Color(255, 255, 255));
   private final Setting<Boolean> hudEditor = this.setting("editor", "Opens the hud editor", Boolean.valueOf(false), value -> {
      if (value) {
         Screen patt1232$temp = mc.currentScreen;
         if (patt1232$temp instanceof ClickGuiScreen screen) {
            screen.close();
         }

         RenderSystem.recordRenderCall(() -> mc.setScreen(new HudEditorScreen()));
         this.hudEditor.set(false);
      }
   });

   public Hud() {
      super(Module.Categories.RENDER, "hud", "Renders various elements on your screen.");

      for(HudElement element : HudElements.ELEMENTS) {
         this.SETTINGS.add(new HudElementSetting(element));
      }

      HudElements.initialized = true;
   }

   @Override
   protected void onEnable() {
      for(HudElement element : HudElements.ELEMENTS) {
         if (element.isActive()) {
            Venomhack.EVENTS.subscribe(element);
         }
      }
   }

   @Override
   protected void onDisable() {
      for(HudElement element : HudElements.ELEMENTS) {
         Venomhack.EVENTS.unsubscribe(element);
      }
   }

   @Override
   public void unpackConfig(JsonObject config) {
      super.unpackConfig(config);
      this.onDisable();

      try {
         JsonElement reader = JsonParser.parseReader(new FileReader("venomhack//hud.json"));
         if (reader == null) {
            return;
         }

         JsonObject hudConfig = reader.getAsJsonObject();

         for(HudElement element : HudElements.ELEMENTS) {
            if (hudConfig.has(element.getName())) {
               element.unpackConfig(hudConfig.get(element.getName()).getAsJsonObject());
            }
         }
      } catch (FileNotFoundException | JsonIOException var8) {
         var8.printStackTrace();
      }
   }
}
