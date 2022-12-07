package de.tyrannus.venomhack.modules;

import com.google.gson.JsonObject;
import de.tyrannus.venomhack.Venomhack;
import de.tyrannus.venomhack.commands.Commands;
import de.tyrannus.venomhack.commands.commands.ModuleCommand;
import de.tyrannus.venomhack.gui.ClickGuiScreen;
import de.tyrannus.venomhack.gui.DropDown;
import de.tyrannus.venomhack.gui.GuiElement;
import de.tyrannus.venomhack.modules.render.ClickGui;
import de.tyrannus.venomhack.settings.AbstractSettingHolder;
import de.tyrannus.venomhack.settings.Keybind;
import de.tyrannus.venomhack.settings.settings.KeySetting;
import de.tyrannus.venomhack.utils.ChatUtils;
import de.tyrannus.venomhack.utils.TextUtils;
import java.awt.Color;
import net.minecraft.util.Hand;
import net.minecraft.text.Text;
import net.minecraft.network.Packet;
import net.minecraft.network.packet.c2s.play.HandSwingC2SPacket;
import net.minecraft.client.util.InputUtil;
import net.minecraft.client.util.math.MatrixStack;

public abstract class Module extends AbstractSettingHolder implements GuiElement {
   public final Module.Categories category;
   private String spoofName;
   public final KeySetting bind = new KeySetting("bind", "The module's bind.", Keybind.UNBOUND, null, null);
   public boolean toggleOnRelease;
   public boolean silent;
   public boolean drawn;
   private boolean open = false;

   public Module(Module.Categories category, String name, String description) {
      super(name, description);
      this.category = category;
      this.spoofName = "";
      this.drawn = true;
      this.toggleOnRelease = false;
      this.silent = false;
      Commands.COMMANDS.add(new ModuleCommand(this));
   }

   protected void onEnable() {
   }

   protected void onDisable() {
   }

   public void rename(String newName) {
      this.spoofName = newName;
   }

   protected static void sendPacket(Packet<?> packet) {
      if (mc.player != null && mc.player.networkHandler != null) {
         mc.player.networkHandler.sendPacket(packet);
      }
   }

   public static void swing(Hand hand, boolean clientSide) {
      if (mc.player != null && mc.player.networkHandler != null) {
         if (clientSide) {
            mc.player.swingHand(hand);
         } else {
            mc.player.networkHandler.sendPacket(new HandSwingC2SPacket(hand));
         }
      }
   }

   @Override
   public void toggle() {
      this.toggle(true);
   }

   public void toggle(boolean sendMsg) {
      this.active = !this.active;
      if (!this.silent && sendMsg) {
         ChatUtils.info(
            TextUtils.coloredTxt("Toggled " + this.getParsedName(), Color.WHITE)
               .append(this.isActive() ? TextUtils.coloredTxt(" ON", Color.GREEN) : TextUtils.coloredTxt(" OFF", Color.RED))
         );
      }

      if (this.active) {
         this.onEnable();
         Venomhack.EVENTS.subscribe(this);
      } else {
         Venomhack.EVENTS.unsubscribe(this);
         this.onDisable();
      }
   }

   protected void toggleWithError(int id, Text text) {
      this.toggle(false);
      ChatUtils.info(text);
   }

   protected void toggleWithError(String text) {
      this.toggle(false);
      ChatUtils.info(text);
   }

   protected void info(String message) {
      ChatUtils.info(message);
   }

   protected void info(Text text) {
      ChatUtils.info(text);
   }

   protected void info(int id, Text text) {
      ChatUtils.info(text);
   }

   public String getArrayText() {
      return "";
   }

   @Override
   public String getParsedName() {
      return TextUtils.parseName(this.spoofName.isEmpty() ? this.name : this.spoofName);
   }

   public String originalParsedName() {
      return TextUtils.parseName(this.name);
   }

   public boolean isClosed() {
      return !this.open;
   }

   public void setOpen(boolean open) {
      this.open = open;
   }

   @Override
   public void render(DropDown dropDown, MatrixStack matrices, int x, int y, double mouseX, double mouseY, ClickGui gui) {
   }

   @Override
   public boolean onMouseClick(ClickGuiScreen screen, DropDown dropDown, int y, int button, double mouseX, double mouseY) {
      if (button == 0) {
         this.toggle(false);
      } else if (button == 1) {
         this.open = !this.open;
      }

      return false;
   }

   @Override
   public JsonObject packConfig() {
      JsonObject config = super.packConfig();
      config.addProperty("bind", this.bind.get().getKey().getTranslationKey());
      config.addProperty("toggleOnRelease", this.toggleOnRelease);
      config.addProperty("silent", this.silent);
      config.addProperty("spoof-name", this.spoofName);
      config.addProperty("drawn", this.drawn);
      return config;
   }

   @Override
   public void unpackConfig(JsonObject config) {
      super.unpackConfig(config);
      if (config.has("bind")) {
         this.bind.set(InputUtil.fromTranslationKey(config.get("bind").getAsString()));
      }

      if (config.has("toggleOnRelease")) {
         this.toggleOnRelease = config.get("toggleOnRelease").getAsBoolean();
      }

      if (config.has("silent")) {
         this.silent = config.get("silent").getAsBoolean();
      }

      if (config.has("spoof-name")) {
         this.spoofName = config.get("spoof-name").getAsString();
      }

      if (config.has("drawn")) {
         this.drawn = config.get("drawn").getAsBoolean();
      }
   }

   public static enum Categories {
      EXPLOIT(new Color(255, 200, 0)),
      COMBAT(new Color(255, 0, 0)),
      MOVEMENT(new Color(0, 255, 255)),
      RENDER(new Color(255, 0, 255)),
      MISC(new Color(0, 255, 0)),
      CHAT(new Color(64, 64, 64));

      public final Color color;

      private Categories(Color color) {
         float[] hsb = Color.RGBtoHSB(color.getRed(), color.getGreen(), color.getBlue(), new float[3]);
         int intColor = Color.HSBtoRGB(hsb[0], 0.35F, hsb[2]);
         this.color = new Color(intColor);
      }
   }
}
