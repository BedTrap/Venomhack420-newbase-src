package de.tyrannus.venomhack;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import de.tyrannus.venomhack.commands.Commands;
import de.tyrannus.venomhack.gui.ClickGuiScreen;
import de.tyrannus.venomhack.modules.Modules;
import de.tyrannus.venomhack.modules.render.hud.HudElement;
import de.tyrannus.venomhack.modules.render.hud.HudElements;
import de.tyrannus.venomhack.utils.Analyser;
import de.tyrannus.venomhack.utils.players.Friends;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.lang.invoke.MethodHandles;
import java.lang.invoke.MethodHandles.Lookup;
import java.nio.file.Files;
import java.nio.file.Paths;
import meteordevelopment.orbit.EventBus;
import meteordevelopment.orbit.IEventBus;
import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.loader.api.FabricLoader;
import net.fabricmc.loader.api.ModContainer;
import net.fabricmc.loader.api.Version;
import net.fabricmc.loader.api.metadata.ModMetadata;
import net.minecraft.client.MinecraftClient;
import org.jetbrains.annotations.Nullable;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class Venomhack implements ClientModInitializer {
   public static final String MOD_ID = "vhack";
   public static final ModMetadata MOD_META = ((ModContainer)FabricLoader.getInstance().getModContainer("vhack").orElseThrow()).getMetadata();
   public static final Version VERSION = MOD_META.getVersion();
   public static final MinecraftClient mc = MinecraftClient.getInstance();
   public static final Logger LOGGER = LoggerFactory.getLogger("modid");
   public static final IEventBus EVENTS = new EventBus();

   public void onInitializeClient() {
      LOGGER.info("Initialising Venomhack.");
      EVENTS.registerLambdaFactory("de.tyrannus.venomhack", (lookupInMethod, klass) -> (Lookup)lookupInMethod.invoke(null, klass, MethodHandles.lookup()));
      Commands.init();
      loadConfig(null);
      Friends.load();
      EVENTS.subscribe(Analyser.class);
      Runtime.getRuntime().addShutdownHook(new Thread(() -> save(null)));
   }

   public static void save(@Nullable String name) {
      Gson gson = new GsonBuilder().setPrettyPrinting().create();
      JsonObject configObject = new JsonObject();
      configObject.addProperty("prefix", Commands.PREFIX);
      configObject.add("gui", ClickGuiScreen.save());
      configObject.add("modules", Modules.save());
      JsonObject hudObject = new JsonObject();

      for(HudElement element : HudElements.ELEMENTS) {
         hudObject.add(element.getName(), element.packConfig());
      }

      JsonArray friendsObject = new JsonArray();

      for(String friend : Friends.FRIENDS) {
         friendsObject.add(friend);
      }

      try {
         Files.createDirectories(Paths.get("venomhack"));
         Files.createDirectories(Paths.get("venomhack//kits"));
         FileWriter configWriter;
         if (name != null) {
            Files.createDirectories(Paths.get("venomhack//profiles"));
            configWriter = new FileWriter("venomhack//profiles//" + name + ".json");
         } else {
            configWriter = new FileWriter("venomhack//config.json");
         }

         FileWriter friendWriter = new FileWriter("venomhack//friends.json");
         FileWriter hudWriter = new FileWriter("venomhack//hud.json");
         gson.toJson(configObject, configWriter);
         gson.toJson(hudObject, hudWriter);
         gson.toJson(friendsObject, friendWriter);
         configWriter.close();
         hudWriter.close();
         friendWriter.close();
      } catch (IOException var8) {
         var8.printStackTrace();
      }
   }

   public static void loadConfig(@Nullable String name) {
      try {
         JsonObject superObject;
         if (name != null) {
            superObject = JsonParser.parseReader(new FileReader("venomhack//profiles//" + name + ".json")).getAsJsonObject();
         } else {
            superObject = JsonParser.parseReader(new FileReader("venomhack//config.json")).getAsJsonObject();
         }

         if (superObject.has("prefix")) {
            Commands.PREFIX = superObject.get("prefix").getAsString().charAt(0);
         }

         if (superObject.has("modules")) {
            Modules.loadModuleConfigs(superObject.getAsJsonObject("modules"));
         }
      } catch (IOException var2) {
         var2.printStackTrace();
      }
   }
}
