package de.tyrannus.venomhack.modules.chat;

import de.tyrannus.venomhack.events.AddMessageEvent;
import de.tyrannus.venomhack.events.RenderEvent;
import de.tyrannus.venomhack.events.SendMessageEvent;
import de.tyrannus.venomhack.modules.Module;
import de.tyrannus.venomhack.settings.Setting;
import de.tyrannus.venomhack.utils.TextUtils;
import de.tyrannus.venomhack.utils.players.Fonts;
import de.tyrannus.venomhack.utils.players.Friends;
import java.awt.Color;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Random;
import meteordevelopment.orbit.EventHandler;
import net.minecraft.text.Text;
import net.minecraft.client.gui.screen.ChatScreen;
import net.minecraft.text.MutableText;
import net.minecraft.Visible;

public class ChatControl extends Module {
   private final Setting<Boolean> clear = this.setting("clear-chat", "Makes the chat background transparent.", Boolean.valueOf(false), this::handleClear);
   private final Setting<Boolean> selfHighlight = this.setting(
      "highlight-self", "Highlights your name in a special color. Compatible with name protect.", Boolean.valueOf(true)
   );
   private final Setting<Color> sHighlightColor = this.setting(
      "self-highlight-color", "Color to highlight your name with.", new Color(120, 0, 255), this.selfHighlight::get
   );
   private final Setting<Boolean> friendHighlight = this.setting("highlight-friends", "Colors your friends names.", Boolean.valueOf(false));
   private final Setting<Color> fHighlightColor = this.setting(
      "friend-highlight-color", "Color to highlight your name with.", new Color(0, 255, 180), this.friendHighlight::get
   );
   private final Setting<Boolean> timestamps = this.setting(
      "timestamps", "Adds client side time stamps to the beginning of chat messages.", Boolean.valueOf(false)
   );
   private final Setting<ChatControl.TimeFormat> format = this.setting(
      "format", "What time format to use.", ChatControl.TimeFormat.TWENTY_FOUR_HOUR, this.timestamps::get
   );
   private final Setting<Boolean> timestampsSeconds = this.setting(
      "include-seconds", "Whether to add seconds to the timestamps or not.", Boolean.valueOf(false), this.timestamps::get
   );
   private final Setting<Color> timestampsColor = this.setting("timestamp-color", "Color of the timestamps.", new Color(170, 170, 170), this.timestamps::get);
   private final Setting<Boolean> rainbow = this.setting("rainbow-prefix", "Enables a proper rainbow prefix.", Boolean.valueOf(true));
   private final Setting<Boolean> synchro = this.setting("synchronized", "Synchronizes the words.", Boolean.valueOf(true), this.rainbow::get);
   private final Setting<Float> rainbowSpeed = this.setting("rainbow-speed", "Rainbow speed for the prefix.", 0.0085F, this.rainbow::get, 0.0F, 0.005F, 4);
   private final Setting<Float> rainbowsaturation = this.setting(
      "rainbow-saturation", "change the saturation of the rainbow", 1.0F, this.rainbow::get, 0.0F, 1.0F, 1
   );
   private final Setting<Float> rainbowLineSpread = this.setting(
      "rainbow-line-spread", "Rainbow spread for the prefix per line.", 0.05F, this.rainbow::get, 0.0F, 0.1F, 2
   );
   private final Setting<Float> rainbowWordSpread = this.setting(
      "rainbow-word-spread", "Rainbow spread for the prefix inside word.", 0.02F, this.rainbow::get, 0.0F, 0.1F, 2
   );
   private final Setting<Boolean> rainbowTimestamps = this.setting(
      "apply-to-timestamps", "Enables the rainbow also for the timestamps.", Boolean.valueOf(false), () -> this.rainbow.get() && this.timestamps.get()
   );
   private final Setting<String> customPrefix = this.setting("custom-prefix", "The prefix for modules added by Venomhack420.", "Venomhack");
   private final Setting<Color> customPrefixColor = this.setting("prefix-color", "Color of the prefix text.", new Color(255, 0, 0), () -> !this.rainbow.get());
   private final Setting<Color> prefixBracketsColor = this.setting(
      "brackets-color", "Color of the brackets.", new Color(170, 170, 170), () -> !this.rainbow.get()
   );
   private final Setting<Boolean> enablePlaceholders = this.setting(
      "enable-placeholders", "Add placeholders that get replaced when sending messages. Seperate placeholder and message with a space.", Boolean.valueOf(true)
   );
   private final Setting<List<String>> placeholders = this.listSetting(
      "placeholders",
      "Seperate with a space, first one is to be replaced with second one.",
      null,
      this.enablePlaceholders::get,
      new String[]{":skull: ☠", ":lit: \ud83d\udd25", ":discord: https://discord.gg/VqRd4MJkbY"}
   );
   private final Setting<Boolean> fancy = this.setting("fancy-chat", "Makes your messages ғᴀɴᴄʏ!", Boolean.valueOf(false));
   private final Setting<Fonts> font = this.setting("font", "Which font to use", Fonts.FANCY, this.fancy::get);
   private final Setting<Boolean> prefix = this.setting("prefix", "Adds a prefix to your chat messages.", Boolean.valueOf(false));
   private final Setting<Boolean> prefixRandom = this.setting(
      "random-prefix", "Uses a random number as your prefix.", Boolean.valueOf(false), this.prefix::get
   );
   private final Setting<String> prefixText = this.setting(
      "prefix-text", "The text to add as your prefix.", "> ", () -> !this.prefixRandom.get() && this.prefix.get()
   );
   private final Setting<Fonts> prefixFont = this.setting(
      "prefix-font", "Set a font for your prefix.", Fonts.DEFAULT, () -> !this.prefixRandom.get() && this.prefix.get()
   );
   private final Setting<Boolean> suffix = this.setting("suffix", "Adds a suffix to your chat messages.", Boolean.valueOf(false));
   private final Setting<Boolean> suffixRandom = this.setting(
      "random-suffix", "Uses a random number as your suffix.", Boolean.valueOf(false), this.suffix::get
   );
   private final Setting<String> suffixText = this.setting(
      "suffix-text", "The text to add as your suffix.", " | Venomhack", () -> !this.suffixRandom.get() && this.suffix.get()
   );
   private final Setting<Fonts> suffixFont = this.setting(
      "suffix-font", "Set a font for your suffix.", Fonts.GREEKISH, () -> !this.suffixRandom.get() && this.suffix.get()
   );
   private float rainbowHue1;
   private float rainbowHue2;
   private final Random random = new Random();

   public ChatControl() {
      super(Module.Categories.CHAT, "chat-control", "Grants you full control over your chat experience.");
   }

   @EventHandler
   private void onRender(RenderEvent.Hud event) {
      if (this.rainbow.get()) {
         this.rainbowHue1 += this.rainbowSpeed.get();
         if (this.rainbowHue1 > 1.0F) {
            --this.rainbowHue1;
         } else if (this.rainbowHue1 < -1.0F) {
            ++this.rainbowHue1;
         }

         this.rainbowHue2 = this.rainbowHue1;
         List<class_7590> visibleMsgs = mc.inGameHud.getChatHud().visibleMessages;

         for(int index = 0; index < 20 && index < visibleMsgs.size(); ++index) {
            class_7590 line = (class_7590)visibleMsgs.get(index);
            if (!(mc.currentScreen instanceof ChatScreen) && line.comp_895() + 200 < mc.inGameHud.getTicks()) {
               break;
            }

            MutableText parsed = TextUtils.parseText(line.comp_896());
            String parsedString = parsed.getString();
            int totalChars = 0;
            int reduceAmount = 0;
            if (this.timestamps.get() && this.rainbowTimestamps.get()) {
               int stampIndexBegin = parsedString.indexOf("<");
               int stampIndexEnd = parsedString.indexOf(">");
               if (stampIndexBegin < stampIndexEnd) {
                  String time = parsedString.substring(stampIndexBegin, stampIndexEnd + 1);
                  parsed.getSiblings().subList(stampIndexBegin, stampIndexEnd + 1).clear();
                  parsed.getSiblings().add(stampIndexBegin, this.applyRgb(time));
                  totalChars = time.length();
                  reduceAmount += stampIndexEnd - stampIndexBegin;
               }
            }

            String vPrefix = "[" + (String)this.customPrefix.get() + "]";
            int vIndex = parsedString.indexOf(vPrefix) - reduceAmount;
            if (vIndex > -1) {
               parsed.getSiblings().subList(vIndex, vIndex + vPrefix.length()).clear();
               parsed.getSiblings().add(vIndex, this.applyRgb(vPrefix));
               totalChars += vPrefix.length();
            }

            this.rainbowHue2 += this.rainbowLineSpread.get();
            if (this.synchro.get()) {
               this.rainbowHue2 += this.rainbowWordSpread.get() * (float)totalChars;
            }

            line.comp_896 = parsed.asOrderedText();
         }
      }
   }

   @EventHandler(
      priority = 199
   )
   private void onMessageRecieve(AddMessageEvent event) {
      MutableText parsed = TextUtils.parseText(event.getMessage().asOrderedText());
      String parsedString = parsed.getString();
      int timestampOffset = 0;
      if (this.timestamps.get()) {
         parsed.getSiblings().add(0, this.timestampText());
         ++timestampOffset;
      }

      int nameIndex = -1;
      int nameOffset = 0;
      if (this.selfHighlight.get()) {
         String name = mc.getSession().getUsername();
         nameIndex = parsedString.indexOf(name);
         if (nameIndex > -1) {
            nameIndex += timestampOffset;
            parsed.getSiblings().subList(nameIndex, nameIndex + name.length()).clear();
            parsed.getSiblings().add(nameIndex, TextUtils.coloredTxt(name, this.sHighlightColor.get()));
            nameOffset = name.length() - 1;
         }
      }

      if (this.friendHighlight.get()) {
         for(String friend : Friends.FRIENDS) {
            if (!this.selfHighlight.get() || !friend.equals(mc.getSession().getUsername())) {
               int friendIndex = parsedString.indexOf(friend);
               if (friendIndex >= 0) {
                  if (friendIndex > nameIndex) {
                     friendIndex -= nameOffset;
                  }

                  friendIndex += timestampOffset;
                  parsed.getSiblings().subList(friendIndex, friendIndex + friend.length()).clear();
                  parsed.getSiblings().add(friendIndex, TextUtils.coloredTxt(friend, this.fHighlightColor.get()));
                  break;
               }
            }
         }
      }

      event.setMessage(parsed);
   }

   @EventHandler(
      priority = 201
   )
   private void onMessageSend(SendMessageEvent event) {
      String message = event.getMessage();
      StringBuilder builder = new StringBuilder();
      if (this.enablePlaceholders.get()) {
         for(String placeholder : this.placeholders.get()) {
            String[] placeHolderArray = placeholder.split(" ", 2);
            if (placeHolderArray.length >= 2) {
               message = message.replace(placeHolderArray[0], placeHolderArray[1]);
            }
         }
      }

      if (event.isCommand()) {
         String[] begin = message.split(" ");
         if (!begin[0].equals("msg")) {
            event.setMessage(message);
            return;
         }

         builder.append("msg ").append(begin[1]).append(" ");
         message = message.substring(begin[0].length() + begin[1].length() + 1);
      }

      if (this.prefix.get()) {
         builder.append(this.getAffix(this.prefixText.get(), this.prefixFont.get(), this.prefixRandom.get()));
         if (!event.isCommand()) {
            builder.append(" ");
         }
      }

      if (this.fancy.get()) {
         message = this.font.get().apply(message);
      }

      builder.append(message);
      if (this.suffix.get()) {
         builder.append(" ").append(this.getAffix(this.suffixText.get(), this.suffixFont.get(), this.suffixRandom.get()));
      }

      event.setMessage(builder.toString());
   }

   private MutableText applyRgb(String text) {
      MutableText prefix = Text.empty();

      for(int i = 0; i < text.length(); ++i) {
         prefix.append(TextUtils.coloredTxt(text.substring(i, i + 1), Color.HSBtoRGB(this.rainbowHue2, this.rainbowsaturation.get(), 1.0F)));
         this.rainbowHue2 -= this.rainbowWordSpread.get();
      }

      return prefix;
   }

   public MutableText timestampText() {
      StringBuilder timeText = new StringBuilder();
      if (this.format.get() == ChatControl.TimeFormat.TWENTY_FOUR_HOUR) {
         timeText.append("HH:mm");
      } else {
         timeText.append("hh:mm");
      }

      if (this.timestampsSeconds.get()) {
         timeText.append(":ss");
      }

      if (this.format.get() == ChatControl.TimeFormat.TWELVE_HOUR) {
         timeText.append(" aa");
      }

      return TextUtils.coloredTxt("<", this.timestampsColor.get())
         .append(TextUtils.coloredTxt(new SimpleDateFormat(timeText.toString()).format(new Date()), this.timestampsColor.get()))
         .append(TextUtils.coloredTxt(">", this.timestampsColor.get()))
         .append(" ");
   }

   private String getAffix(String text, Fonts font, boolean random) {
      return random ? String.format("(%03d) ", this.random.nextInt(0, 1000)) : font.apply(text);
   }

   private void handleClear(boolean bool) {
      if (mc.options != null) {
         if (this.clear.get()) {
            mc.options.getTextBackgroundOpacity().setValue(0.0);
         } else {
            mc.options.getTextBackgroundOpacity().setValue(0.5);
         }
      }
   }

   public MutableText getPrefix() {
      return TextUtils.coloredTxt("[", this.prefixBracketsColor.get())
         .append(TextUtils.coloredTxt(this.customPrefix.get(), this.customPrefixColor.get()))
         .append(TextUtils.coloredTxt("]", this.prefixBracketsColor.get()))
         .append(" ");
   }

   @Override
   public void onEnable() {
      this.handleClear(true);
   }

   @Override
   public void onDisable() {
      if (mc.options.getTextBackgroundOpacity().getValue() == 0.0) {
         mc.options.getTextBackgroundOpacity().setValue(0.5);
      }
   }

   public static enum TimeFormat {
      TWENTY_FOUR_HOUR("24h"),
      TWELVE_HOUR("12h");

      private final String title;

      private TimeFormat(String title) {
         this.title = title;
      }

      @Override
      public String toString() {
         return this.title;
      }
   }
}
