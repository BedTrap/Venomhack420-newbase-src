package de.tyrannus.venomhack.commands.commands;

import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import com.mojang.brigadier.builder.RequiredArgumentBuilder;
import de.tyrannus.venomhack.commands.Command;
import de.tyrannus.venomhack.commands.arguments.HudElementArgumentType;
import de.tyrannus.venomhack.commands.arguments.SettingArgumentType;
import de.tyrannus.venomhack.commands.arguments.SettingValueArgumentType;
import de.tyrannus.venomhack.modules.render.hud.HudElement;
import de.tyrannus.venomhack.settings.Setting;
import de.tyrannus.venomhack.utils.ChatUtils;
import de.tyrannus.venomhack.utils.TextUtils;
import java.awt.Color;
import net.minecraft.server.command.ServerCommandSource;
import net.minecraft.text.Text;
import net.minecraft.text.MutableText;

public class HudElementCommand extends Command {
   public HudElementCommand() {
      super("hud-element", "Allows you to toggle or config hud elements.");
   }

   @Override
   protected void build(LiteralArgumentBuilder<ServerCommandSource> builder) {
      builder.then(
         ((RequiredArgumentBuilder)((RequiredArgumentBuilder)this.arg("element", new HudElementArgumentType())
                  .executes(
                     context -> {
                        HudElement element = (HudElement)context.getArgument("element", HudElement.class);
                        ChatUtils.info(
                           Text.literal("")
                              .append(TextUtils.coloredTxt("[", Color.CYAN).append(element.getParsedName()).append("]"))
                              .append(": ")
                              .append(element.description)
                              .append(" Currently ")
                              .append(TextUtils.coloredTxt(element.isActive() ? "Active" : "Inactive", Color.ORANGE))
                              .append(".")
                        );
                        if (element.SETTINGS.isEmpty()) {
                           return 1;
                        } else {
                           ChatUtils.sendMsg("The " + element.getParsedName() + " specific settings are:");
               
                           for(Setting<?> setting : element.SETTINGS) {
                              ChatUtils.sendMsg(
                                 Text.literal("")
                                    .append(this.formatSetting(setting))
                                    .append(" : ")
                                    .append(TextUtils.coloredTxt(setting.storeValue(), Color.ORANGE))
                              );
                           }
               
                           return 1;
                        }
                     }
                  ))
               .then(
                  this.lit("toggle")
                     .executes(
                        context -> {
                           HudElement element = (HudElement)context.getArgument("element", HudElement.class);
                           element.toggle();
                           ChatUtils.info(
                              TextUtils.coloredTxt("Toggled " + element.getParsedName(), Color.WHITE)
                                 .append(element.isActive() ? TextUtils.coloredTxt(" ON", Color.GREEN) : TextUtils.coloredTxt(" OFF", Color.RED))
                           );
                           return 1;
                        }
                     )
               ))
            .then(
               ((RequiredArgumentBuilder)((RequiredArgumentBuilder)this.arg("setting", new SettingArgumentType(null))
                        .executes(
                           context -> {
                              Setting setting = (Setting)context.getArgument("setting", Setting.class);
                              ChatUtils.info(
                                 Text.literal("[")
                                    .append(this.formatSetting(setting))
                                    .append("]: ")
                                    .append(setting.getDescription())
                                    .append(" Currently set to ")
                                    .append(TextUtils.coloredTxt(setting.storeValue(), Color.ORANGE))
                                    .append(".")
                              );
                              return 1;
                           }
                        ))
                     .then(
                        this.lit("reset")
                           .executes(
                              context -> {
                                 Setting setting = (Setting)context.getArgument("setting", Setting.class);
                                 setting.reset();
                                 ChatUtils.info(
                                    Text.literal("Reset ")
                                       .append(this.formatSetting(setting))
                                       .append(" to default value of ")
                                       .append(TextUtils.coloredTxt(setting.storeValue(), Color.ORANGE))
                                       .append(".")
                                 );
                                 return 1;
                              }
                           )
                     ))
                  .then(
                     this.arg("value", new SettingValueArgumentType())
                        .executes(
                           context -> {
                              Setting setting = (Setting)context.getArgument("setting", Setting.class);
                              String value = (String)context.getArgument("value", String.class);
                              if (setting.parseValue(value)) {
                                 ChatUtils.info(
                                    Text.literal("Set ")
                                       .append(setting.parsedName())
                                       .append(" to ")
                                       .append(TextUtils.coloredTxt(value, Color.ORANGE))
                                       .append(".")
                                 );
                              } else {
                                 ChatUtils.info(
                                    Text.literal("Bad argument! Can't set ")
                                       .append(setting.parsedName())
                                       .append(" to ")
                                       .append(TextUtils.coloredTxt(value, Color.ORANGE))
                                       .append(".")
                                 );
                              }
                     
                              return 1;
                           }
                        )
                  )
            )
      );
   }

   private MutableText formatSetting(Setting<?> setting) {
      return TextUtils.coloredTxt(setting.parsedName(), Color.LIGHT_GRAY);
   }
}
