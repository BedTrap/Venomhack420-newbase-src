package de.tyrannus.venomhack.modules;

import com.google.gson.JsonObject;
import de.tyrannus.venomhack.modules.chat.ArmorMessage;
import de.tyrannus.venomhack.modules.chat.AutoCope;
import de.tyrannus.venomhack.modules.chat.ChatControl;
import de.tyrannus.venomhack.modules.chat.Greeter;
import de.tyrannus.venomhack.modules.chat.LogDetection;
import de.tyrannus.venomhack.modules.chat.Notifier;
import de.tyrannus.venomhack.modules.chat.PopCrash;
import de.tyrannus.venomhack.modules.combat.AutoLog;
import de.tyrannus.venomhack.modules.combat.Burrow;
import de.tyrannus.venomhack.modules.combat.Criticals;
import de.tyrannus.venomhack.modules.combat.FunnyCrystal;
import de.tyrannus.venomhack.modules.combat.KillAura;
import de.tyrannus.venomhack.modules.combat.Quiver;
import de.tyrannus.venomhack.modules.combat.SelfTrap;
import de.tyrannus.venomhack.modules.combat.Surround;
import de.tyrannus.venomhack.modules.exploit.AirPlace;
import de.tyrannus.venomhack.modules.exploit.AntiHunger;
import de.tyrannus.venomhack.modules.exploit.ChorusControl;
import de.tyrannus.venomhack.modules.exploit.FastUse;
import de.tyrannus.venomhack.modules.exploit.MultiTask;
import de.tyrannus.venomhack.modules.exploit.NoServerPack;
import de.tyrannus.venomhack.modules.exploit.XCarry;
import de.tyrannus.venomhack.modules.misc.AutoCrafter;
import de.tyrannus.venomhack.modules.misc.AutoRegear;
import de.tyrannus.venomhack.modules.misc.AutoRespawn;
import de.tyrannus.venomhack.modules.misc.AutoXP;
import de.tyrannus.venomhack.modules.misc.EGapFinder;
import de.tyrannus.venomhack.modules.misc.FakePlayer;
import de.tyrannus.venomhack.modules.misc.MiddleClickFriend;
import de.tyrannus.venomhack.modules.misc.MiddleClickPearl;
import de.tyrannus.venomhack.modules.misc.NoMiningTrace;
import de.tyrannus.venomhack.modules.misc.NoRotate;
import de.tyrannus.venomhack.modules.misc.PacketLogger;
import de.tyrannus.venomhack.modules.misc.PacketPlace;
import de.tyrannus.venomhack.modules.misc.PingSpoof;
import de.tyrannus.venomhack.modules.misc.Portals;
import de.tyrannus.venomhack.modules.misc.Reach;
import de.tyrannus.venomhack.modules.misc.Timer;
import de.tyrannus.venomhack.modules.movement.AntiRubberband;
import de.tyrannus.venomhack.modules.movement.Fly;
import de.tyrannus.venomhack.modules.movement.InventoryWalk;
import de.tyrannus.venomhack.modules.movement.Moses;
import de.tyrannus.venomhack.modules.movement.NoSlow;
import de.tyrannus.venomhack.modules.movement.Nofall;
import de.tyrannus.venomhack.modules.movement.Scaffold;
import de.tyrannus.venomhack.modules.movement.Sprint;
import de.tyrannus.venomhack.modules.movement.Step;
import de.tyrannus.venomhack.modules.movement.Velocity;
import de.tyrannus.venomhack.modules.render.BlockOutline;
import de.tyrannus.venomhack.modules.render.ClickGui;
import de.tyrannus.venomhack.modules.render.CustomFov;
import de.tyrannus.venomhack.modules.render.CustomPops;
import de.tyrannus.venomhack.modules.render.CustomTime;
import de.tyrannus.venomhack.modules.render.Esp;
import de.tyrannus.venomhack.modules.render.Freecam;
import de.tyrannus.venomhack.modules.render.FullBright;
import de.tyrannus.venomhack.modules.render.KillFx;
import de.tyrannus.venomhack.modules.render.LogoutSpots;
import de.tyrannus.venomhack.modules.render.Nametags;
import de.tyrannus.venomhack.modules.render.NoRender;
import de.tyrannus.venomhack.modules.render.OldAnimations;
import de.tyrannus.venomhack.modules.render.ShaderOverlay;
import de.tyrannus.venomhack.modules.render.UserCapes;
import de.tyrannus.venomhack.modules.render.ViewModel;
import de.tyrannus.venomhack.modules.render.hud.Hud;
import java.util.ArrayList;
import java.util.List;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class Modules {
   private static final Module[] MODULES = new Module[]{
      new ArmorMessage(),
      new AutoCope(),
      new ChatControl(),
      new Greeter(),
      new LogDetection(),
      new Notifier(),
      new PopCrash(),
      new AutoLog(),
      new AutoXP(),
      new Burrow(),
      new Criticals(),
      new FunnyCrystal(),
      new KillAura(),
      new Quiver(),
      new SelfTrap(),
      new Surround(),
      new AirPlace(),
      new AntiHunger(),
      new ChorusControl(),
      new FastUse(),
      new MultiTask(),
      new NoServerPack(),
      new XCarry(),
      new AutoCrafter(),
      new AutoRegear(),
      new AutoRespawn(),
      new EGapFinder(),
      new FakePlayer(),
      new MiddleClickFriend(),
      new MiddleClickPearl(),
      new NoMiningTrace(),
      new NoRotate(),
      new PacketLogger(),
      new PacketPlace(),
      new PingSpoof(),
      new Portals(),
      new Reach(),
      new Timer(),
      new AntiRubberband(),
      new Fly(),
      new InventoryWalk(),
      new Moses(),
      new Nofall(),
      new NoSlow(),
      new Scaffold(),
      new Sprint(),
      new Step(),
      new Velocity(),
      new BlockOutline(),
      new ClickGui(),
      new CustomFov(),
      new CustomPops(),
      new CustomTime(),
      new Esp(),
      new Freecam(),
      new FullBright(),
      new Hud(),
      new KillFx(),
      new LogoutSpots(),
      new Nametags(),
      new NoRender(),
      new OldAnimations(),
      new ShaderOverlay(),
      new UserCapes(),
      new ViewModel()
   };

   @Nullable
   public static Module get(String name) {
      for(Module module : MODULES) {
         if (module.getName().equalsIgnoreCase(name)) {
            return module;
         }
      }

      return null;
   }

   @NotNull
   public static <T extends Module> T get(Class<T> klass) {
      for(Module module : MODULES) {
         if (module.getClass() == klass) {
            return klass.cast(module);
         }
      }

      return null;
   }

   public static <T extends Module> boolean isActive(Class<T> klass) {
      for(Module module : MODULES) {
         if (module.getClass() == klass) {
            return module.isActive();
         }
      }

      return false;
   }

   public static List<Module> getCategoryModules(Module.Categories category) {
      ArrayList<Module> modules = new ArrayList<>();

      for(Module module : MODULES) {
         if (module.category == category) {
            modules.add(module);
         }
      }

      return modules;
   }

   public static Module[] modules() {
      return MODULES;
   }

   public static JsonObject save() {
      JsonObject modules = new JsonObject();

      for(Module module : MODULES) {
         modules.add(module.getName(), module.packConfig());
      }

      return modules;
   }

   public static void loadModuleConfigs(JsonObject modules) {
      for(Module module : MODULES) {
         if (modules.has(module.getName())) {
            module.unpackConfig(modules.getAsJsonObject(module.getName()));
         }
      }
   }
}
