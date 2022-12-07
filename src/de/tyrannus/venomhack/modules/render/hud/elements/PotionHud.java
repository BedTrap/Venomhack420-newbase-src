package de.tyrannus.venomhack.modules.render.hud.elements;

import com.mojang.blaze3d.systems.RenderSystem;
import de.tyrannus.venomhack.events.RenderEvent;
import de.tyrannus.venomhack.gui.HudEditorScreen;
import de.tyrannus.venomhack.modules.render.hud.HudElement;
import de.tyrannus.venomhack.settings.Setting;
import java.awt.Color;
import java.util.ArrayList;
import java.util.Comparator;
import meteordevelopment.orbit.EventHandler;
import net.minecraft.client.texture.Sprite;
import net.minecraft.entity.effect.StatusEffectInstance;
import net.minecraft.client.gui.DrawableHelper;

public class PotionHud extends HudElement {
   private final Setting<Boolean> icon = this.setting("icon", "Shows the potion icon next to the effect.", Boolean.valueOf(false));
   private final Setting<Boolean> customColor = this.setting("custom-color", "Whether or not to have a custom text color.", Boolean.valueOf(false));
   private final Setting<Color> color = this.setting("text-color", "The text color.", new Color(0, 100, 200, 255), this.customColor::get);

   public PotionHud() {
      super("potion-hud", "Displays a list of your current potion effects.", 0, 200);
   }

   @EventHandler
   private void onRender(RenderEvent.Hud event) {
      if (!(mc.currentScreen instanceof HudEditorScreen) || mc.player != null && !mc.player.getStatusEffects().isEmpty()) {
         ArrayList<StatusEffectInstance> effects = this.getEffects();
         effects.sort(Comparator.comparingInt(e -> mc.textRenderer.getWidth(e.getEffectType().getName())));

         for(int i = 0; i < effects.size(); ++i) {
            StatusEffectInstance effect = (StatusEffectInstance)effects.get(i);
            int yPos = this.y - (i + 1) * 9;
            String name = effect.getEffectType().getName().getString();
            int width = mc.textRenderer.getWidth(name);
            int height = 9;
            int seconds = ((StatusEffectInstance)effects.get(i)).getDuration() / 20;
            int minutes = seconds / 60;
            seconds = seconds >= 60 ? seconds - minutes * 60 : seconds;
            String duration = (minutes >= 1 ? minutes : "0") + ":" + (seconds >= 10 ? seconds : "0" + seconds);
            int xOffset = 0;
            if (this.icon.get()) {
               Sprite sprite = mc.getStatusEffectSpriteManager().getSprite(effect.getEffectType());
               RenderSystem.setShaderTexture(0, sprite.getAtlas().getId());
               DrawableHelper.drawSprite(event.getMatrices(), this.x, yPos, 0, height, height, sprite);
               xOffset = height;
            }

            mc.textRenderer
               .draw(
                  name,
                  (float)(this.x + xOffset),
                  (float)yPos,
                  this.customColor.get() ? this.color.get().getRGB() : effect.getEffectType().getColor(),
                  getHud().textShadows.get(),
                  event.getMatrices().peek().getPositionMatrix(),
                  event.getImmediate(),
                  false,
                  0,
                  15728880,
                  mc.textRenderer.isRightToLeft()
               );
            mc.textRenderer
               .draw(
                  " " + duration,
                  (float)(this.x + xOffset + width + 1),
                  (float)yPos,
                  getHud().secondaryColor.get().getRGB(),
                  getHud().textShadows.get(),
                  event.getMatrices().peek().getPositionMatrix(),
                  event.getImmediate(),
                  false,
                  0,
                  15728880,
                  mc.textRenderer.isRightToLeft()
               );
         }
      } else {
         this.renderText(event, this.getParsedName(), "");
      }
   }

   private ArrayList<StatusEffectInstance> getEffects() {
      return new ArrayList(mc.player.getStatusEffects());
   }

   @Override
   public int[] getBounds() {
      if (mc.player != null && !mc.player.getStatusEffects().isEmpty()) {
         String longestName = ((StatusEffectInstance)mc.player
               .getStatusEffects()
               .stream()
               .sorted(Comparator.comparingInt(e -> mc.textRenderer.getWidth(e.getEffectType().getName())))
               .toList()
               .get(mc.player.getStatusEffects().size() - 1))
            .getEffectType()
            .getName()
            .getString();
         int width = mc.textRenderer.getWidth(longestName);
         int height = 9 * mc.player.getStatusEffects().size();
         return new int[]{width, height};
      } else {
         return new int[]{mc.textRenderer.getWidth(this.getParsedName()), 9};
      }
   }
}
