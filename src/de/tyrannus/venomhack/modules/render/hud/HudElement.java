package de.tyrannus.venomhack.modules.render.hud;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import de.tyrannus.venomhack.Venomhack;
import de.tyrannus.venomhack.events.RenderEvent;
import de.tyrannus.venomhack.modules.Modules;
import de.tyrannus.venomhack.settings.AbstractSettingHolder;
import de.tyrannus.venomhack.settings.Setting;
import java.awt.Color;
import net.minecraft.client.gui.Element;
import net.minecraft.client.gui.screen.ChatScreen;

public abstract class HudElement extends AbstractSettingHolder implements Element {
   private static Hud hud;
   protected static int WHITE = Color.WHITE.getRGB();
   protected static int RED = Color.RED.getRGB();
   protected int x;
   protected int y;

   public HudElement(String name, String description, int defaultX, int defaultY) {
      super(name, description);
      this.x = defaultX;
      this.y = defaultY;
   }

   protected void renderText(RenderEvent.Hud event, String leftText, String rightText) {
      this.renderText(event, leftText, rightText, getHud().primaryColor.get(), getHud().secondaryColor.get());
   }

   protected void renderText(RenderEvent.Hud event, String leftText, String rightText, Color leftColor, Color rightColor) {
      this.renderText(event, leftText, rightText, leftColor.getRGB(), rightColor.getRGB());
   }

   protected void renderText(RenderEvent.Hud event, String leftText, String rightText, int leftColor, int rightColor) {
      int leftLength = mc.textRenderer.getWidth(leftText);
      this.x = Math.max(1, Math.min(this.x, mc.getWindow().getScaledWidth() - leftLength - mc.textRenderer.getWidth(rightText)));
      this.y = Math.max(1, Math.min(this.y, mc.getWindow().getScaledHeight() - 9));
      int renderY = this.y;
      if (mc.currentScreen instanceof ChatScreen && renderY > mc.currentScreen.height - 28) {
         renderY -= 14;
      }

      mc.textRenderer
         .draw(
            leftText,
            (float)this.x,
            (float)renderY,
            leftColor,
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
            rightText,
            (float)(this.x + leftLength),
            (float)renderY,
            rightColor,
            getHud().textShadows.get(),
            event.getMatrices().peek().getPositionMatrix(),
            event.getImmediate(),
            false,
            0,
            15728880,
            mc.textRenderer.isRightToLeft()
         );
   }

   public abstract int[] getBounds();

   @Override
   public void toggle() {
      this.active = !this.active;
      if (this.active && Modules.isActive(Hud.class)) {
         Venomhack.EVENTS.subscribe(this);
      } else {
         Venomhack.EVENTS.unsubscribe(this);
      }
   }

   protected static Hud getHud() {
      if (hud == null) {
         hud = Modules.get(Hud.class);
      }

      return hud;
   }

   public int getX() {
      return this.x;
   }

   public int getY() {
      return this.y;
   }

   public void setX(int x) {
      this.x = x;
   }

   public void setY(int y) {
      this.y = y;
   }

   @Override
   public JsonObject packConfig() {
      JsonObject config = super.packConfig();
      config.addProperty("x", this.x);
      config.addProperty("y", this.y);
      return config;
   }

   @Override
   public void unpackConfig(JsonObject config) {
      if (config.has("active")) {
         this.active = config.get("active").getAsBoolean();
         if (this.active && getHud().isActive()) {
            Venomhack.EVENTS.subscribe(this);
         }
      }

      for(Setting<?> setting : this.SETTINGS) {
         JsonElement element = config.get(setting.getName());
         if (element != null) {
            setting.load(element);
         }
      }

      if (config.has("x")) {
         this.x = config.get("x").getAsInt();
      }

      if (config.has("y")) {
         this.y = config.get("y").getAsInt();
      }
   }
}
