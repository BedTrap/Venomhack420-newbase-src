package de.tyrannus.venomhack.modules.combat;

import de.tyrannus.venomhack.events.RenderEvent;
import de.tyrannus.venomhack.events.TickEvent;
import de.tyrannus.venomhack.modules.Module;
import de.tyrannus.venomhack.modules.combat.autocrystal.Origin;
import de.tyrannus.venomhack.settings.Setting;
import de.tyrannus.venomhack.utils.AntiCheatHelper;
import de.tyrannus.venomhack.utils.players.Friends;
import de.tyrannus.venomhack.utils.render.RenderMode;
import de.tyrannus.venomhack.utils.render.RenderUtils;
import java.awt.Color;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import meteordevelopment.orbit.EventHandler;
import net.minecraft.util.Hand;
import net.minecraft.entity.passive.PassiveEntity;
import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.passive.GolemEntity;
import net.minecraft.entity.mob.WaterCreatureEntity;
import net.minecraft.entity.mob.Monster;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.AxeItem;
import net.minecraft.item.Item;
import net.minecraft.item.SwordItem;

public class KillAura extends Module {
   private final Setting<KillAura.TickMode> tickMode = this.setting("event", "When to attack.", KillAura.TickMode.POST);
   private final Setting<Float> range = this.setting("range", "The hit range.", Float.valueOf(4.5F), 0.0F, 6.0F);
   private final Setting<Float> wallsRange = this.setting("walls-range", "How far to hit through walls.", Float.valueOf(4.5F), 0.0F, 6.0F);
   private final Setting<Origin> origin = this.setting("origin", "How to calculate ranges.", Origin.NCP);
   private final Setting<Boolean> hitboxes = this.setting(
      "hitboxes", "Whether to account for hitboxes in range calcs or not.", Boolean.valueOf(false), () -> this.origin.get() == Origin.NCP
   );
   private final Setting<Boolean> players = this.setting("players", "Whether or not to attack players.", Boolean.valueOf(true));
   private final Setting<Boolean> hostile = this.setting("hostiles", "Whether or not to attack hostile entities.", Boolean.valueOf(true));
   private final Setting<Boolean> passive = this.setting("passives", "Whether or not to attack passive entities.", Boolean.valueOf(false));
   private final Setting<KillAura.AttackMode> weapon = this.setting("active", "When to attack.", KillAura.AttackMode.BOTH);
   private final Setting<Integer> maxTargets = this.setting("max-targets", "How many entities to attack at once at most.", Integer.valueOf(1), 0.0F, 4.0F);
   private final Setting<Boolean> swing = this.setting("swing", "Whether to swing client side or not.", Boolean.valueOf(true));
   public final Setting<Boolean> render = this.setting("render", "Renders a box around the target.", Boolean.valueOf(false));
   private final Setting<RenderMode> renderMode = this.setting("render-mode", "How to r ender lines & sides.", RenderMode.LINES, this.render::get);
   private final Setting<Float> lineWidth = this.setting(
      "line-width", "The line width.", Float.valueOf(1.5F), () -> this.render.get() && this.renderMode.get().lines(), 0.0F, 3.0F
   );
   private final Setting<Color> lineColor = this.setting(
      "line-color", "The color of the outline.", Color.RED, () -> this.render.get() && this.renderMode.get().lines()
   );
   private final Setting<Color> sideColor = this.setting(
      "side-color", "The side color.", new Color(255, 0, 0, 100), () -> this.render.get() && this.renderMode.get().sides()
   );
   private final Setting<Boolean> chromaLines = this.setting(
      "chroma-lines", "Chroma Lines.", Boolean.valueOf(false), () -> this.render.get() && this.renderMode.get().lines()
   );
   private final Setting<Boolean> chromaSides = this.setting(
      "chroma-sides", "Chroma Sides.", Boolean.valueOf(false), () -> this.render.get() && this.renderMode.get().sides()
   );
   private final Setting<Float> chromaSpeed = this.setting(
      "chroma-speed", "How fast to cycle between colors.", Float.valueOf(3.0F), () -> this.chromaLines.get() || this.chromaSides.get()
   );
   private final Setting<Integer> chromaLinesAlpha = this.setting(
      "chroma-line-alpha",
      "The opacity of the chroma effect for lines.",
      Integer.valueOf(255),
      () -> this.render.get() && this.renderMode.get().lines() && this.chromaLines.get(),
      0.0F,
      255.0F
   );
   private final Setting<Integer> chromaSidesAlpha = this.setting(
      "chroma-side-alpha",
      "The opacity of the chroma effect for sides.",
      Integer.valueOf(100),
      () -> this.render.get() && this.renderMode.get().sides(),
      0.0F,
      255.0F
   );
   public final List<Entity> targets = new ArrayList();

   public KillAura() {
      super(Module.Categories.COMBAT, "kill-aura", "Bri'ish PVP");
   }

   @EventHandler
   private void onTick(TickEvent.Pre event) {
      if (this.tickMode.get() == KillAura.TickMode.PRE) {
         this.doKillAura();
      }
   }

   @EventHandler
   private void onTick(TickEvent.Post event) {
      if (this.tickMode.get() == KillAura.TickMode.POST) {
         this.doKillAura();
      }
   }

   @EventHandler
   private void onRender(RenderEvent.Flat event) {
      if (this.render.get() && !this.noWeapon()) {
         for(Entity target : this.targets) {
            Color lineCol = this.chromaLines.get()
               ? RenderUtils.getChromaColor((float)this.chromaLinesAlpha.get().intValue(), this.chromaSpeed.get())
               : this.lineColor.get();
            Color sideCol = this.chromaSides.get()
               ? RenderUtils.getChromaColor((float)this.chromaSidesAlpha.get().intValue(), this.chromaSpeed.get())
               : this.sideColor.get();
            RenderUtils.drawEntityBox(event, target, this.renderMode.get(), this.lineWidth.get(), lineCol, sideCol, this.chromaSides.get());
         }
      }
   }

   private void doKillAura() {
      this.targets.clear();
      if (mc.player != null && !this.noWeapon()) {
         for(Entity entity : mc.world.getEntities()) {
            if (entity != mc.player
               && entity instanceof LivingEntity livingEntity
               && !livingEntity.isRemoved()
               && !(livingEntity.getHealth() <= 0.0F)
               && (
                  livingEntity instanceof PlayerEntity player
                     ? this.players.get() && !Friends.isFriend(player)
                     : (
                        livingEntity instanceof Monster
                           ? this.hostile.get()
                           : !(livingEntity instanceof PassiveEntity) && !(livingEntity instanceof WaterCreatureEntity) && !(livingEntity instanceof GolemEntity)
                              || this.passive.get()
                     )
               )
               && !AntiCheatHelper.outOfHitRange(
                  entity, this.origin.get(), (double)this.range.get().floatValue(), (double)this.wallsRange.get().floatValue(), this.hitboxes.get()
               )) {
               this.targets.add(entity);
            }
         }

         if (!this.targets.isEmpty()) {
            this.targets.sort(Comparator.comparingDouble(e -> e.squaredDistanceTo(mc.player)));

            while(this.targets.size() > this.maxTargets.get()) {
               this.targets.remove(this.targets.size() - 1);
            }

            if (!(mc.player.getAttackCooldownProgress(0.5F) < 1.0F)) {
               for(Entity target : this.targets) {
                  mc.interactionManager.attackEntity(mc.player, target);
                  swing(Hand.MAIN_HAND, this.swing.get());
               }
            }
         }
      }
   }

   public boolean noWeapon() {
      Item item = mc.player.getMainHandStack().getItem();
      switch((KillAura.AttackMode)this.weapon.get()) {
         case SWORD:
            if (!(item instanceof SwordItem)) {
               return true;
            }
            break;
         case AXE:
            if (!(item instanceof AxeItem)) {
               return true;
            }
            break;
         case BOTH:
            if (!(item instanceof SwordItem) && !(item instanceof AxeItem)) {
               return true;
            }
         case ANY:
            break;
         default:
            throw new IncompatibleClassChangeError();
      }

      return false;
   }

   @Override
   public String getArrayText() {
      return this.targets.isEmpty() ? "None" : ((Entity)this.targets.get(0)).getEntityName();
   }

   @Override
   public void onDisable() {
   }

   protected static enum AttackMode {
      AXE,
      SWORD,
      BOTH,
      ANY;
   }

   protected static enum TickMode {
      PRE,
      POST;
   }
}
