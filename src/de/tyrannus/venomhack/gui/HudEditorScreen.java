package de.tyrannus.venomhack.gui;

import de.tyrannus.venomhack.Venomhack;
import de.tyrannus.venomhack.events.RenderEvent;
import de.tyrannus.venomhack.modules.render.hud.HudElement;
import de.tyrannus.venomhack.modules.render.hud.HudElements;
import java.awt.Color;
import net.minecraft.text.Text;
import net.minecraft.client.render.Tessellator;
import net.minecraft.client.gui.DrawableHelper;
import net.minecraft.client.gui.Element;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.Immediate;
import org.jetbrains.annotations.Nullable;

public class HudEditorScreen extends Screen {
   private double dragOffsetX;
   private double dragOffsetY;
   private final int active = new Color(192, 192, 192, 150).getRGB();
   private final int inactive = new Color(255, 0, 0, 150).getRGB();
   private boolean hasDragged = false;

   public HudEditorScreen() {
      super(Text.literal("Hud Editor"));

      for(HudElement element : HudElements.ELEMENTS) {
         Venomhack.EVENTS.subscribe(element);
      }
   }

   public void render(MatrixStack matrices, int mouseX, int mouseY, float delta) {
      this.renderBackground(matrices);

      for(HudElement element : HudElements.ELEMENTS) {
         int[] bounds = element.getBounds();
         DrawableHelper.fill(
            matrices, element.getX(), element.getY(), element.getX() + bounds[0], element.getY() + bounds[1], element.isActive() ? this.active : this.inactive
         );
      }

      class_4598 immediate = VertexConsumerProvider.immediate(Tessellator.getInstance().getBuffer());
      Venomhack.EVENTS.post(RenderEvent.Hud.get(matrices, immediate, delta));
      immediate.draw();
   }

   public boolean mouseClicked(double mouseX, double mouseY, int button) {
      HudElement element = this.findElementUnderMouse(mouseX, mouseY);
      if (element == null) {
         return false;
      } else if (button == 0) {
         this.setFocused(element);
         this.dragOffsetX = mouseX - (double)element.getX();
         this.dragOffsetY = mouseY - (double)element.getY();
         return true;
      } else {
         return false;
      }
   }

   public boolean mouseDragged(double mouseX, double mouseY, int button, double deltaX, double deltaY) {
      Element var11 = this.getFocused();
      if (var11 instanceof HudElement element && button == 0) {
         element.setX((int)(mouseX - this.dragOffsetX));
         element.setY((int)(mouseY - this.dragOffsetY));
         this.hasDragged = true;
         return true;
      }

      return false;
   }

   public boolean mouseReleased(double mouseX, double mouseY, int button) {
      Element element = (Element)(this.getFocused() == null ? this.findElementUnderMouse(mouseX, mouseY) : this.getFocused());
      if (element instanceof HudElement hudElement) {
         if (button == 0) {
            if (!this.hasDragged) {
               hudElement.toggle();
            }

            this.hasDragged = false;
            this.setFocused(null);
            return true;
         } else {
            return false;
         }
      } else {
         return false;
      }
   }

   @Nullable
   private HudElement findElementUnderMouse(double mouseX, double mouseY) {
      for(HudElement element : HudElements.ELEMENTS) {
         int x = element.getX();
         int y = element.getY();
         int[] bounds = element.getBounds();
         if (this.isInBetween(mouseX, (double)x, (double)(x + bounds[0])) && this.isInBetween(mouseY, (double)y, (double)(y + bounds[1]))) {
            return element;
         }
      }

      return null;
   }

   private boolean isInBetween(double mouse, double bound1, double bound2) {
      if (bound1 < mouse && mouse <= bound2) {
         return true;
      } else {
         return bound2 < mouse && mouse <= bound1;
      }
   }

   public void close() {
      super.close();

      for(HudElement element : HudElements.ELEMENTS) {
         Venomhack.EVENTS.unsubscribe(element);
      }
   }

   public boolean shouldPause() {
      return false;
   }
}
