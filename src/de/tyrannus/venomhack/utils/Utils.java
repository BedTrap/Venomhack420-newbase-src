package de.tyrannus.venomhack.utils;

import de.tyrannus.venomhack.Venomhack;
import it.unimi.dsi.fastutil.objects.ObjectIterator;
import java.util.List;
import java.util.Random;
import net.minecraft.entity.Entity;
import net.minecraft.util.math.Vec3i;
import net.minecraft.util.math.Vec3d;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.util.InputUtil;
import net.minecraft.client.input.KeyboardInput;
import net.minecraft.util.math.BlockPos.class_2339;
import net.minecraft.client.util.InputUtil.class_306;
import net.minecraft.client.util.InputUtil.class_307;

public class Utils {
   public static final Random RANDOM = new Random();
   protected static final class_2339 mutable = new class_2339();

   public static class_306 keyFromString(String input) {
      try {
         return InputUtil.fromTranslationKey(input);
      } catch (IllegalArgumentException var5) {
         ObjectIterator var1 = class_307.KEYSYM.map.values().iterator();

         while(var1.hasNext()) {
            class_306 key = (class_306)var1.next();
            String translationKey = key.getTranslationKey();
            String[] array = translationKey.split("\\.");
            if (array[array.length - 2].equals("right")) {
               if (input.equals("r" + array[array.length - 1])) {
                  return key;
               }
            } else if (array[array.length - 2].equals("left")) {
               if (input.equals("l" + array[array.length - 1])) {
                  return key;
               }
            } else if (array[array.length - 2].equals("mouse")) {
               if (input.equals("m" + array[array.length - 1])) {
                  return key;
               }
            } else if (input.equals(array[array.length - 1])) {
               return key;
            }
         }

         return null;
      }
   }

   public static void smartAdd(List<Vec3i> list, Vec3i vec) {
      if (!list.contains(vec)) {
         list.add(vec);
      }
   }

   public static boolean isKeyPressed(int code) {
      return InputUtil.isKeyPressed(MinecraftClient.getInstance().getWindow().getHandle(), code);
   }

   public static Vec3d transformInput(float yaw, boolean forward, boolean back, boolean left, boolean right, boolean up, boolean down, float speed) {
      int y = up ? 1 : 0;
      if (down) {
         --y;
      }

      return Entity.movementInputToVelocity(
         new Vec3d((double)KeyboardInput.getMovementMultiplier(left, right), (double)y, (double)KeyboardInput.getMovementMultiplier(forward, back)), speed, yaw
      );
   }

   public static boolean isNull() {
      return Venomhack.mc.world == null || Venomhack.mc.player == null;
   }
}
