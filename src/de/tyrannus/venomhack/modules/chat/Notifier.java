package de.tyrannus.venomhack.modules.chat;

import de.tyrannus.venomhack.events.PlayerDeathEvent;
import de.tyrannus.venomhack.events.TotemPopEvent;
import de.tyrannus.venomhack.modules.Module;
import de.tyrannus.venomhack.settings.Setting;
import de.tyrannus.venomhack.utils.TextUtils;
import java.awt.Color;
import meteordevelopment.orbit.EventHandler;
import net.minecraft.util.Formatting;
import net.minecraft.text.Text;
import net.minecraft.text.MutableText;

public class Notifier extends Module {
   private final Setting<Boolean> totemPops = this.setting("totem-pops", "Notifies you about totem pops.", Boolean.valueOf(true));
   private final Setting<Color> popColor = this.setting("pop-color", "The pop colour in chat.", new Color(192, 192, 192, 255));

   public Notifier() {
      super(Module.Categories.CHAT, "notifier", "Notifies you of certain events.");
   }

   @EventHandler
   private void onPop(TotemPopEvent event) {
      if (this.totemPops.get()) {
         int pop = event.getPops();
         MutableText text = Text.literal("").formatted(Formatting.WHITE).append(event.getEntity().getDisplayName());
         text.append(" popped ").append(this.format(pop)).append(pop == 1 ? " totem." : " totems.");
         this.info(text);
      }
   }

   @EventHandler
   private void onDeath(PlayerDeathEvent event) {
      if (this.totemPops.get() && event.getPops() != 0) {
         int pop = event.getPops();
         MutableText text = Text.literal("").formatted(Formatting.WHITE).append(event.getPlayer().getDisplayName());
         text.append(" died after popping ").append(this.format(pop)).append(pop == 1 ? " totem." : " totems.");
         this.info(text);
      }
   }

   private Text format(int number) {
      return TextUtils.coloredTxt(String.valueOf(number), this.popColor.get());
   }
}
