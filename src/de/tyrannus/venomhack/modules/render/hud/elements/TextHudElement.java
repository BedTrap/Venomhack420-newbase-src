package de.tyrannus.venomhack.modules.render.hud.elements;

import de.tyrannus.venomhack.events.RenderEvent;
import de.tyrannus.venomhack.modules.render.hud.HudElement;
import meteordevelopment.orbit.EventHandler;

public class TextHudElement extends HudElement {
   public final String leftText;
   public final String rightText;

   public TextHudElement(String name, String description, String leftText, String rightText, int defaultX, int defaultY) {
      super(name, description, defaultX, defaultY);
      this.leftText = leftText;
      this.rightText = rightText;
   }

   public String getLeftText() {
      return this.leftText;
   }

   public String getRightText() {
      return this.rightText;
   }

   @EventHandler
   private void onRender(RenderEvent.Hud event) {
      this.renderText(event, this.getLeftText(), this.getRightText());
   }

   @Override
   public int[] getBounds() {
      return new int[]{mc.textRenderer.getWidth(this.getLeftText() + " " + this.getRightText()), 9};
   }
}
