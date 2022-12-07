package de.tyrannus.venomhack.modules.render.hud.elements;

import de.tyrannus.venomhack.settings.Setting;
import java.util.Locale;
import net.minecraft.util.math.MathHelper;

public class DirectionHud extends TextHudElement {
   private final Setting<Boolean> direction = this.setting("direction", "Shows the cardinal direction you are facing.", Boolean.valueOf(true));
   private final Setting<Boolean> axis = this.setting("axis", "Shows the coordinate axis you are facing.", Boolean.valueOf(true));
   private final Setting<Boolean> angle = this.setting("yaw-pitch", "Shows your yaw and pitch values.", Boolean.valueOf(false));

   public DirectionHud() {
      super("direction-hud", "Shows the direction you are facing and the axis it is.", "", "direction", 20, 20);
   }

   @Override
   public String getRightText() {
      float yaw = MathHelper.wrapDegrees(mc.gameRenderer.getCamera().getYaw());
      StringBuilder builder = new StringBuilder();
      if ((double)yaw >= -22.5 && (double)yaw < 22.5) {
         if (this.direction.get()) {
            builder.append("South");
         }

         if (this.axis.get()) {
            builder.append(" [Z+]");
         }
      } else if ((double)yaw >= 22.5 && (double)yaw < 67.5) {
         if (this.direction.get()) {
            builder.append("South West");
         }

         if (this.axis.get()) {
            builder.append(" [X- Z+]");
         }
      } else if ((double)yaw >= 67.5 && (double)yaw < 112.5) {
         if (this.direction.get()) {
            builder.append("West");
         }

         if (this.axis.get()) {
            builder.append(" [X-]");
         }
      } else if ((double)yaw >= 112.5 && (double)yaw < 157.5) {
         if (this.direction.get()) {
            builder.append("North West");
         }

         if (this.axis.get()) {
            builder.append(" [X- Z-]");
         }
      } else if (!((double)yaw >= 157.5) && !((double)yaw < -157.5)) {
         if ((double)yaw >= -157.5 && (double)yaw < -112.5) {
            if (this.direction.get()) {
               builder.append("North East");
            }

            if (this.axis.get()) {
               builder.append(" [X+ Z-]");
            }
         } else if ((double)yaw >= -112.5 && (double)yaw < -67.5) {
            if (this.direction.get()) {
               builder.append("East");
            }

            if (this.axis.get()) {
               builder.append(" [X+]");
            }
         } else if ((double)yaw >= -67.5 && (double)yaw < -22.5) {
            if (this.direction.get()) {
               builder.append("South East");
            }

            if (this.axis.get()) {
               builder.append(" [X+ Z+]");
            }
         }
      } else {
         if (this.direction.get()) {
            builder.append("North");
         }

         if (this.axis.get()) {
            builder.append(" [Z-]");
         }
      }

      if (this.angle.get()) {
         builder.append(String.format(Locale.ROOT, " (%.1f %.1f)", yaw, MathHelper.wrapDegrees(mc.gameRenderer.getCamera().getPitch())));
      }

      if (builder.isEmpty()) {
         return "";
      } else {
         if (builder.charAt(0) == ' ') {
            builder.replace(0, 1, "");
         }

         return builder.toString();
      }
   }
}
