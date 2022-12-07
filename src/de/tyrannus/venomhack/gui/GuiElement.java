package de.tyrannus.venomhack.gui;

import de.tyrannus.venomhack.Venomhack;
import de.tyrannus.venomhack.modules.render.ClickGui;
import net.minecraft.client.gui.Element;
import net.minecraft.client.util.math.MatrixStack;

public interface GuiElement extends Element {
   static int getDefaultHeight() {
      return Venomhack.mc.textRenderer == null ? 0 : 9 + 6;
   }

   static int textPadding() {
      return 4;
   }

   static int sidePadding() {
      return 1;
   }

   default int getHeight() {
      return getDefaultHeight();
   }

   boolean onMouseClick(ClickGuiScreen var1, DropDown var2, int var3, int var4, double var5, double var7);

   void render(DropDown var1, MatrixStack var2, int var3, int var4, double var5, double var7, ClickGui var9);
}
