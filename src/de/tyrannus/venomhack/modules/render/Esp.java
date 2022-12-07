package de.tyrannus.venomhack.modules.render;

import de.tyrannus.venomhack.events.RenderEvent;
import de.tyrannus.venomhack.modules.Module;
import de.tyrannus.venomhack.modules.Modules;
import de.tyrannus.venomhack.modules.combat.KillAura;
import de.tyrannus.venomhack.settings.Setting;
import de.tyrannus.venomhack.utils.render.RenderMode;
import de.tyrannus.venomhack.utils.render.RenderUtils;
import java.awt.Color;
import meteordevelopment.orbit.EventHandler;
import net.minecraft.entity.passive.PassiveEntity;
import net.minecraft.entity.Entity;
import net.minecraft.entity.decoration.EndCrystalEntity;
import net.minecraft.entity.ItemEntity;
import net.minecraft.entity.mob.Monster;
import net.minecraft.entity.player.PlayerEntity;

public class Esp extends Module {
   private final Setting<Integer> cull = this.setting("cull-distance", "Culling distance.", Integer.valueOf(40), 0.0F, 256.0F);
   private final Setting<Boolean> players = this.setting("players", "Adds esp to players.", Boolean.valueOf(false));
   private final Setting<Boolean> self = this.setting("self", "Renders esp for you too.", Boolean.valueOf(false), this.players::get);
   private final Setting<Boolean> hostile = this.setting("hostile", "Adds esp to monsters.", Boolean.valueOf(false));
   private final Setting<Boolean> passive = this.setting("passive", "Adds esp to passive mobs.", Boolean.valueOf(false));
   private final Setting<Boolean> crystals = this.setting("crystals", "Adds esp to crystals.", Boolean.valueOf(false));
   private final Setting<Boolean> items = this.setting("items", "Adds esp to items.", Boolean.valueOf(false));
   private final Setting<RenderMode> mode = this.setting("render-mode", "How to render lines & sides.", RenderMode.BOTH);
   private final Setting<Boolean> chromaLines = this.setting("chroma-lines", "Chroma Lines.", Boolean.valueOf(false), () -> this.mode.get().lines());
   private final Setting<Boolean> chromaSides = this.setting("chroma-sides", "Chroma Sides.", Boolean.valueOf(false), () -> this.mode.get().sides());
   private final Setting<Float> chromaSpeed = this.setting(
      "chroma-speed", "How fast to cycle between colors.", Float.valueOf(3.0F), () -> this.chromaLines.get() || this.chromaSides.get()
   );
   private final Setting<Integer> chromaLinesAlpha = this.setting(
      "chroma-line-alpha",
      "The opacity of the chroma effect for lines.",
      Integer.valueOf(255),
      () -> this.mode.get().lines() && this.chromaLines.get(),
      0.0F,
      255.0F
   );
   private final Setting<Integer> chromaSidesAlpha = this.setting(
      "chroma-side-alpha",
      "The opacity of the chroma effect for sides.",
      Integer.valueOf(100),
      () -> this.mode.get().sides() && this.chromaSides.get(),
      0.0F,
      255.0F
   );
   private final Setting<Float> linesWidth = this.setting("line-width", "Width of the rendered lines.", Float.valueOf(1.5F), () -> this.mode.get().lines());
   private final Setting<Color> linesColor = this.setting("line-color", "The line color.", Color.RED, () -> this.mode.get().lines() && !this.chromaLines.get());
   private final Setting<Color> sidesColor = this.setting(
      "side-color", "The side color.", new Color(255, 0, 0, 100), () -> this.mode.get().sides() && !this.chromaSides.get()
   );

   public Esp() {
      super(Module.Categories.RENDER, "esp", "Cool renders around entities.");
   }

   @EventHandler
   private void onRender(RenderEvent.Flat event) {
      for(Entity entity : mc.world.getEntities()) {
         if (!(mc.player.squaredDistanceTo(entity) > (double)(this.cull.get() * this.cull.get()))) {
            KillAura aura = Modules.get(KillAura.class);
            if ((!aura.isActive() || !aura.render.get() || aura.noWeapon() || !aura.targets.contains(entity))
               && (
                  entity instanceof PlayerEntity
                     ? this.players.get() && (entity != mc.player || this.self.get() && mc.gameRenderer.getCamera().isThirdPerson())
                     : (
                        entity instanceof Monster
                           ? this.hostile.get()
                           : (
                              entity instanceof PassiveEntity
                                 ? this.passive.get()
                                 : (entity instanceof EndCrystalEntity ? this.crystals.get() : entity instanceof ItemEntity && this.items.get())
                           )
                     )
               )) {
               Color lineCol = this.chromaLines.get() ? this.getChromaColor((float)this.chromaLinesAlpha.get().intValue()) : this.linesColor.get();
               Color sideCol = this.chromaSides.get() ? this.getChromaColor((float)this.chromaSidesAlpha.get().intValue()) : this.sidesColor.get();
               RenderUtils.drawEntityBox(event, entity, this.mode.get(), this.linesWidth.get(), lineCol, sideCol, this.chromaSides.get());
            }
         }
      }
   }

   private Color getChromaColor(float alpha) {
      float speed = 10000.0F / this.chromaSpeed.get();
      Color hsb = Color.getHSBColor((float)(System.currentTimeMillis() % (long)((int)speed)) / speed, 1.0F, 1.0F);
      return new Color((float)hsb.getRed() / 255.0F, (float)hsb.getGreen() / 255.0F, (float)hsb.getBlue() / 255.0F, alpha / 255.0F);
   }
}
