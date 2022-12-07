package de.tyrannus.venomhack.gui;

import de.tyrannus.venomhack.Venomhack;
import de.tyrannus.venomhack.modules.Module;
import de.tyrannus.venomhack.modules.Modules;
import de.tyrannus.venomhack.modules.render.ClickGui;
import java.util.List;
import net.minecraft.client.util.math.MatrixStack;

public class DropDown implements GuiElement {
   private static int biggestWidth = 0;
   private static final ClickGui GUI = Modules.get(ClickGui.class);
   private boolean closed;
   private int x;
   private int y;
   private final Module.Categories category;
   private final List<Module> modules;

   public DropDown(Module.Categories category, List<Module> modules) {
      this.category = category;
      this.modules = modules;
      this.closed = false;
   }

   public int getMaxWidth() {
      return biggestWidth + GuiElement.textPadding() + Venomhack.mc.textRenderer.getWidth(GUI.indicator.get().get(true));
   }

   public static int getBiggestWidth() {
      return biggestWidth;
   }

   public static void setBiggestWidth(int biggestWidth) {
      DropDown.biggestWidth = biggestWidth;
   }

   public boolean isClosed() {
      return this.closed;
   }

   public int getX() {
      return this.x;
   }

   public int getY() {
      return this.y;
   }

   public Module.Categories getCategory() {
      return this.category;
   }

   public List<Module> getModules() {
      return this.modules;
   }

   public String name() {
      return this.category.toString();
   }

   public int color() {
      return this.category.color.getRGB();
   }

   public void setClosed(boolean closed) {
      this.closed = closed;
   }

   public void setXY(int x, int y) {
      this.x = x;
      this.y = y;
   }

   @Override
   public boolean onMouseClick(ClickGuiScreen screen, DropDown dropDown, int y, int button, double mouseX, double mouseY) {
      return false;
   }

   @Override
   public void render(DropDown dropDown, MatrixStack matrices, int x, int y, double mouseX, double mouseY, ClickGui gui) {
   }
}
