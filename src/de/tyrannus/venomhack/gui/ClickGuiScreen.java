package de.tyrannus.venomhack.gui;

import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import de.tyrannus.venomhack.Venomhack;
import de.tyrannus.venomhack.modules.Module;
import de.tyrannus.venomhack.modules.Modules;
import de.tyrannus.venomhack.modules.render.ClickGui;
import de.tyrannus.venomhack.settings.Setting;
import de.tyrannus.venomhack.settings.settings.KeySetting;
import de.tyrannus.venomhack.settings.settings.SliderSetting;
import de.tyrannus.venomhack.utils.MathUtil;
import java.awt.Color;
import java.io.FileNotFoundException;
import java.io.FileReader;
import net.minecraft.client.sound.PositionedSoundInstance;
import net.minecraft.text.Text;
import net.minecraft.client.render.Tessellator;
import net.minecraft.client.gui.DrawableHelper;
import net.minecraft.sound.SoundEvents;
import net.minecraft.client.gui.Element;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.util.InputUtil.class_307;
import net.minecraft.Immediate;

public class ClickGuiScreen extends Screen {
   public static final DropDown[] DROP_DOWNS = new DropDown[Module.Categories.values().length];
   private static final ClickGui GUI = Modules.get(ClickGui.class);
   private double mouseDeltaX;
   private double mouseDeltaY;

   public ClickGuiScreen() {
      super(Text.literal("Venomhack"));
      if (DROP_DOWNS[0] == null) {
         load();
      }
   }

   private int getModuleHeight() {
      return GuiElement.getDefaultHeight();
   }

   public void render(MatrixStack matrices, int mouseX, int mouseY, float delta) {
      super.renderBackground(matrices);

      for(DropDown dropDown : DROP_DOWNS) {
         DrawableHelper.fill(
            matrices,
            dropDown.getX(),
            dropDown.getY(),
            dropDown.getX() + dropDown.getMaxWidth(),
            dropDown.getY() + dropDown.getHeight(),
            GUI.categoryColor.get().getRGB()
         );
         drawText(
            matrices,
            dropDown.name(),
            dropDown.getX() + 2 + (dropDown.getMaxWidth() - Venomhack.mc.textRenderer.getWidth(dropDown.name())) / 2,
            dropDown.getY(),
            GUI.textColor.get()
         );
         if (!dropDown.isClosed()) {
            int y = dropDown.getY() + dropDown.getHeight();
            int x = dropDown.getX() + 4;

            for(Module module : dropDown.getModules()) {
               drawBackground(matrices, true, isMouseOver(dropDown, (double)mouseX, (double)mouseY, y, module), dropDown, y, module.isActive());
               DrawableHelper.fill(matrices, dropDown.getX() + 1, y, dropDown.getX() + 2, y + this.getModuleHeight(), GUI.categoryColor.get().getRGB());
               DrawableHelper.fill(
                  matrices,
                  dropDown.getX() + dropDown.getMaxWidth() - 2,
                  y,
                  dropDown.getX() + dropDown.getMaxWidth() - 1,
                  y + this.getModuleHeight(),
                  GUI.categoryColor.get().getRGB()
               );
               drawText(matrices, module.originalParsedName(), x, y, module.isActive() ? GUI.activeModuleColor.get() : GUI.textColor.get());
               String indicator = GUI.indicator.get().get(module.isClosed());
               drawText(matrices, indicator, dropDown.getX() + dropDown.getMaxWidth() - Venomhack.mc.textRenderer.getWidth(indicator), y, GUI.textColor.get());
               y += this.getModuleHeight();
               if (!module.isClosed()) {
                  for(Setting<?> setting : module.SETTINGS) {
                     setting.render(dropDown, matrices, x, y, (double)mouseX, (double)mouseY, GUI);
                     y += setting.getHeight();
                  }

                  module.bind.render(dropDown, matrices, x, y, (double)mouseX, (double)mouseY, GUI);
                  y += module.bind.getHeight();
               }
            }

            DrawableHelper.fill(matrices, dropDown.getX() + 1, y, dropDown.getX() + dropDown.getMaxWidth() - 1, y + 1, GUI.categoryColor.get().getRGB());
         }
      }
   }

   public boolean mouseClicked(double mouseX, double mouseY, int button) {
      Element var7 = this.getFocused();
      if (var7 instanceof KeySetting setting) {
         setting.set(class_307.MOUSE.createFromCode(button));
         this.setFocused(null);
         return true;
      } else {
         for(DropDown dropDown : DROP_DOWNS) {
            int xLeft = dropDown.getX();
            int xRight = xLeft + dropDown.getMaxWidth();
            int yTop = dropDown.getY();
            int yBottom = yTop + dropDown.getHeight();
            if (!(mouseX < (double)xLeft) && !(mouseX > (double)xRight) && !(mouseY < (double)yTop)) {
               if (mouseY < (double)yBottom) {
                  if (button != 2) {
                     this.playSound();
                  }

                  if (button == 0) {
                     this.setFocused(dropDown);
                     this.mouseDeltaX = mouseX - (double)dropDown.getX();
                     this.mouseDeltaY = mouseY - (double)dropDown.getY();
                  } else if (button == 1) {
                     dropDown.setClosed(!dropDown.isClosed());
                  }

                  return true;
               }

               if (!dropDown.isClosed()) {
                  for(Module module : dropDown.getModules()) {
                     if (isMouseOverEntireElement(dropDown, mouseX, mouseY, yBottom, module)) {
                        if (button != 2) {
                           this.playSound();
                        }

                        if (button == 0) {
                           module.toggle(false);
                        } else if (button == 1) {
                           module.setOpen(module.isClosed());
                        }

                        return true;
                     }

                     yBottom += this.getModuleHeight();
                     if (!module.isClosed()) {
                        for(Setting<?> setting : module.SETTINGS) {
                           if (setting.isVisible()) {
                              if (isMouseOverEntireElement(dropDown, mouseX, mouseY, yBottom, setting)) {
                                 if (setting.onMouseClick(this, dropDown, yBottom, button, mouseX, mouseY)) {
                                    this.playSound();
                                    return true;
                                 }

                                 return false;
                              }

                              yBottom += setting.getHeight();
                           }
                        }

                        if (isMouseOverEntireElement(dropDown, mouseX, mouseY, yBottom, module.bind)) {
                           if (module.bind.onMouseClick(this, dropDown, yBottom, button, mouseX, mouseY)) {
                              this.playSound();
                              return true;
                           }

                           return false;
                        }

                        yBottom += module.bind.getHeight();
                     }
                  }
               }
            }
         }

         return false;
      }
   }

   public boolean mouseDragged(double mouseX, double mouseY, int button, double deltaX, double deltaY) {
      if (button != 0) {
         return false;
      } else {
         Element focusedElement = this.getFocused();
         if (focusedElement == null) {
            return false;
         } else {
            if (focusedElement instanceof DropDown dropDown) {
               dropDown.setXY((int)Math.round(mouseX - this.mouseDeltaX), (int)Math.round(mouseY - this.mouseDeltaY));
            } else if (focusedElement instanceof SliderSetting setting) {
               setting.onDragged(mouseX);
            }

            return true;
         }
      }
   }

   public boolean keyPressed(int keyCode, int scanCode, int modifiers) {
      Element var5 = this.getFocused();
      if (!(var5 instanceof KeySetting)) {
         return super.keyPressed(keyCode, scanCode, modifiers);
      } else {
         KeySetting setting = (KeySetting)var5;
         if (keyCode != 256 && keyCode != 261 && keyCode != 259) {
            setting.set(keyCode);
         } else {
            setting.reset();
         }

         this.setFocused(null);
         return true;
      }
   }

   public static boolean isMouseOverEntireElement(DropDown dropDown, double mouseX, double mouseY, int y, GuiElement element) {
      if (GUI.getScreen().getFocused() != null) {
         return element.equals(GUI.getScreen().getFocused());
      } else {
         return mouseX >= (double)(dropDown.getX() + 2)
            && mouseX < (double)(dropDown.getX() + dropDown.getMaxWidth() - 2)
            && mouseY >= (double)y
            && mouseY < (double)(y + element.getHeight());
      }
   }

   public static boolean isMouseOver(DropDown dropDown, double mouseX, double mouseY, int y, GuiElement element) {
      if (GUI.getScreen().getFocused() != null) {
         return element.equals(GUI.getScreen().getFocused());
      } else {
         return mouseX >= (double)(dropDown.getX() + 2)
            && mouseX < (double)(dropDown.getX() + dropDown.getMaxWidth() - 2)
            && mouseY >= (double)y
            && mouseY < (double)(y + GuiElement.getDefaultHeight());
      }
   }

   public static void drawBackground(MatrixStack matrices, boolean module, boolean highlight, DropDown dropDown, int y, boolean active) {
      drawBackground(
         matrices,
         module,
         highlight,
         dropDown.getX() + 2,
         y,
         dropDown.getX() + dropDown.getMaxWidth() - 2,
         y + GuiElement.getDefaultHeight(),
         !module && active
      );
   }

   public static void drawBackground(MatrixStack matrices, boolean module, boolean highlight, int x, int y, int x2, int y2, boolean active) {
      int argb = active ? GUI.activeColor.get().getRGB() : (module ? GUI.backgroundColor.get().getRGB() : GUI.settingBackgroundColor.get().getRGB());
      if (highlight) {
         argb = doubleAlpha(argb);
      }

      DrawableHelper.fill(matrices, x, y, x2, y2, argb);
   }

   public static void drawText(MatrixStack matrices, String text, int x, int y, Color color) {
      drawText(matrices, Text.literal(text), x, y, color);
   }

   public static void drawText(MatrixStack matrices, Text text, int x, int y, Color color) {
      y += GuiElement.textPadding();
      class_4598 immediate = VertexConsumerProvider.immediate(Tessellator.getInstance().getBuffer());
      Venomhack.mc
         .textRenderer
         .draw(text, (float)x, (float)y, color.getRGB(), GUI.textShadows.get(), matrices.peek().getPositionMatrix(), immediate, false, 0, 15728880);
      immediate.draw();
   }

   public static int doubleAlpha(int argb) {
      return MathUtil.clamp8Bit(argb >>> 23) << 24 | argb & 16777215;
   }

   public boolean mouseReleased(double mouseX, double mouseY, int button) {
      if (!(this.getFocused() instanceof KeySetting)) {
         this.setFocused(null);
      }

      return true;
   }

   public boolean mouseScrolled(double mouseX, double mouseY, double amount) {
      for(DropDown dropDown : DROP_DOWNS) {
         dropDown.setXY(dropDown.getX(), dropDown.getY() + (int)Math.round(amount * 4.0));
      }

      return true;
   }

   public boolean shouldPause() {
      return false;
   }

   public void close() {
      Venomhack.mc.setScreen(null);
      GUI.setActive(false);

      for(Module module : Modules.modules()) {
         module.setOpen(false);
      }
   }

   private void playSound() {
      if (GUI.playClickSound.get()) {
         Venomhack.mc.getSoundManager().play(PositionedSoundInstance.master(SoundEvents.UI_BUTTON_CLICK, 1.0F));
      }
   }

   private static void load() {
      buildGUI();

      try {
         JsonObject superObject = JsonParser.parseReader(new FileReader("venomhack//config.json")).getAsJsonObject();
         if (superObject.has("gui")) {
            for(DropDown dropDown : DROP_DOWNS) {
               JsonObject dropDownObject = superObject.getAsJsonObject("gui").getAsJsonObject(dropDown.name());
               if (dropDownObject != null) {
                  dropDown.setXY(dropDownObject.get("x").getAsInt(), dropDownObject.get("y").getAsInt());
               }
            }
         }
      } catch (FileNotFoundException var6) {
         Venomhack.LOGGER.info("Couldn't find config folder, skipping loading.");
      }
   }

   public static JsonObject save() {
      JsonObject dropDowns = new JsonObject();

      for(DropDown dropDown : DROP_DOWNS) {
         if (dropDown == null) {
            break;
         }

         JsonObject element = new JsonObject();
         element.addProperty("x", dropDown.getX());
         element.addProperty("y", dropDown.getY());
         dropDowns.add(dropDown.name(), element);
      }

      return dropDowns;
   }

   public static void buildGUI() {
      setMaxWidth();
      Module.Categories[] categories = Module.Categories.values();
      int x = 20;

      for(int i = 0; i < categories.length; ++i) {
         Module.Categories category = categories[i];
         DropDown dropdown = new DropDown(category, Modules.getCategoryModules(category));
         dropdown.setXY(x, 20);
         x += dropdown.getMaxWidth();
         DROP_DOWNS[i] = dropdown;
      }
   }

   public static void setMaxWidth() {
      if (Venomhack.mc.textRenderer != null) {
         DropDown.setBiggestWidth(0);

         for(Module.Categories category : Module.Categories.values()) {
            int width = Venomhack.mc.textRenderer.getWidth(category.toString());

            for(Module module : Modules.getCategoryModules(category)) {
               width = Math.max(width, Venomhack.mc.textRenderer.getWidth(module.originalParsedName()));
               if (GUI.settingWidth.get()) {
                  for(Setting<?> setting : module.SETTINGS) {
                     width = Math.max(width, Venomhack.mc.textRenderer.getWidth(setting.parsedName()));
                  }
               }
            }

            if (width > DropDown.getBiggestWidth()) {
               DropDown.setBiggestWidth(width);
            }
         }
      }
   }
}
