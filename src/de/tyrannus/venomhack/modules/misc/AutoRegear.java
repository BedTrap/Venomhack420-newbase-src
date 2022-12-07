package de.tyrannus.venomhack.modules.misc;

import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import de.tyrannus.venomhack.commands.Commands;
import de.tyrannus.venomhack.events.PacketEvent;
import de.tyrannus.venomhack.events.TickEvent;
import de.tyrannus.venomhack.modules.Module;
import de.tyrannus.venomhack.settings.Setting;
import de.tyrannus.venomhack.utils.inventory.InvUtils;
import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import meteordevelopment.orbit.EventHandler;
import net.minecraft.screen.ScreenHandler;
import net.minecraft.screen.slot.Slot;
import net.minecraft.item.Items;
import net.minecraft.text.Text;
import net.minecraft.network.packet.c2s.play.CloseHandledScreenC2SPacket;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.screen.ingame.GenericContainerScreen;
import net.minecraft.client.gui.screen.ingame.ShulkerBoxScreen;

public class AutoRegear extends Module {
   private final Setting<Boolean> shulkers = this.setting("shulkers", "Sort while you have a shulker box open.", Boolean.valueOf(true));
   private final Setting<Boolean> misc = this.setting("misc", "Sort while in other containers.", Boolean.valueOf(false));
   private final Setting<Integer> delay = this.setting("delay", "The delay between each item.", Integer.valueOf(4), 0.0F, 20.0F);
   private final List<String> kitItems = new ArrayList<>();
   String kitToRead;
   int delayLeft;
   int containerSlot;
   int slot;

   public AutoRegear() {
      super(Module.Categories.MISC, "auto-regear", "Automatically sorts your inventory.");
   }

   @EventHandler
   private void onTick(TickEvent.Pre event) {
      if (this.kitItems.isEmpty()) {
         this.getKitItems();
      }

      if (this.hasOpenScreen(mc.currentScreen)) {
         ScreenHandler handler = mc.player.currentScreenHandler;
         if (this.delayLeft > 0) {
            --this.delayLeft;
         } else if (this.containerSlot < handler.slots.size() - 36) {
            String containerItem = ((Slot)handler.slots.get(this.containerSlot)).getStack().getItem().toString();

            for(int i = 0; i < 36 && this.slot < handler.slots.size(); ++i) {
               if (i < 8) {
                  this.slot = handler.slots.size() - 9 + i;
               } else {
                  this.slot = handler.slots.size() - 36 + i - 9;
               }

               if (((Slot)mc.player.currentScreenHandler.slots.get(this.slot)).getStack().getItem().equals(Items.AIR)
                  && containerItem.equals(this.kitItems.get(i))) {
                  InvUtils.move(this.containerSlot, this.slot);
                  this.delayLeft = this.delay.get();
                  break;
               }
            }

            mc.player.sendMessage(Text.of("Checking slot " + this.containerSlot), true);
            ++this.containerSlot;
         }
      }
   }

   @EventHandler
   private void onSend(PacketEvent.Send event) {
      if (event.getPacket() instanceof CloseHandledScreenC2SPacket) {
         this.containerSlot = 0;
         this.slot = 0;
      }
   }

   private boolean hasOpenScreen(Screen screen) {
      if (this.shulkers.get() && screen instanceof ShulkerBoxScreen) {
         return true;
      } else {
         return this.misc.get() && screen instanceof GenericContainerScreen;
      }
   }

   private void getKitItems() {
      try {
         BufferedReader br = new BufferedReader(new FileReader("venomhack\\kits\\loadedKit.kit"));
         this.kitToRead = br.readLine();
         br.close();
      } catch (IOException var5) {
         this.toggleWithError("Couldn't find a loaded kit. Load one with " + Commands.PREFIX + "kit load <name>.");
         return;
      }

      try {
         Gson gson = new Gson();

         for(JsonElement item : (JsonArray)gson.fromJson(new FileReader("venomhack\\kits\\" + this.kitToRead + ".json"), JsonArray.class)) {
            this.kitItems.add(item.getAsString());
         }
      } catch (FileNotFoundException var6) {
         this.toggleWithError("Couldn't find your kit. Create one with .kit save <name>.");
      }
   }

   @Override
   public void onEnable() {
      this.delayLeft = this.delay.get();
      this.kitItems.clear();
   }

   @Override
   public void onDisable() {
      this.slot = 0;
   }
}
