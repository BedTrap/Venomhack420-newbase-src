package de.tyrannus.venomhack.modules.render.hud;

import de.tyrannus.venomhack.modules.render.hud.elements.ArmourHud;
import de.tyrannus.venomhack.modules.render.hud.elements.CoordHud;
import de.tyrannus.venomhack.modules.render.hud.elements.CpsHud;
import de.tyrannus.venomhack.modules.render.hud.elements.DaBaby;
import de.tyrannus.venomhack.modules.render.hud.elements.DirectionHud;
import de.tyrannus.venomhack.modules.render.hud.elements.FpsHud;
import de.tyrannus.venomhack.modules.render.hud.elements.ItemHud;
import de.tyrannus.venomhack.modules.render.hud.elements.LagNotifierHud;
import de.tyrannus.venomhack.modules.render.hud.elements.LogoHud;
import de.tyrannus.venomhack.modules.render.hud.elements.ModuleArrayHud;
import de.tyrannus.venomhack.modules.render.hud.elements.PingHud;
import de.tyrannus.venomhack.modules.render.hud.elements.PotionHud;
import de.tyrannus.venomhack.modules.render.hud.elements.TpsHud;
import de.tyrannus.venomhack.modules.render.hud.elements.WaterMark;
import de.tyrannus.venomhack.modules.render.hud.elements.WelcomeHud;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class HudElements {
   public static final HudElement[] ELEMENTS = new HudElement[]{
      new ArmourHud(),
      new CoordHud(),
      new CpsHud(),
      new DaBaby(),
      new DirectionHud(),
      new FpsHud(),
      new ItemHud(),
      new LagNotifierHud(),
      new LogoHud(),
      new ModuleArrayHud(),
      new PingHud(),
      new PotionHud(),
      new TpsHud(),
      new WaterMark(),
      new WelcomeHud()
   };
   public static boolean initialized = false;

   public static <T extends HudElement> boolean isActive(Class<T> klass) {
      for(HudElement element : ELEMENTS) {
         if (klass.isAssignableFrom(element.getClass())) {
            return element.isActive();
         }
      }

      return false;
   }

   @NotNull
   public static <T extends HudElement> T get(Class<T> klass) {
      for(HudElement element : ELEMENTS) {
         if (klass.isAssignableFrom(element.getClass())) {
            return klass.cast(element);
         }
      }

      return null;
   }

   @Nullable
   public static HudElement get(String hudElement) {
      for(HudElement element : ELEMENTS) {
         if (element.getName().equalsIgnoreCase(hudElement)) {
            return element;
         }
      }

      return null;
   }
}
