package de.tyrannus.venomhack.commands.commands;

import com.mojang.brigadier.arguments.BoolArgumentType;
import com.mojang.brigadier.arguments.StringArgumentType;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import com.mojang.brigadier.builder.RequiredArgumentBuilder;
import de.tyrannus.venomhack.commands.Command;
import de.tyrannus.venomhack.commands.Commands;
import de.tyrannus.venomhack.commands.arguments.SettingArgumentType;
import de.tyrannus.venomhack.commands.arguments.SettingValueArgumentType;
import de.tyrannus.venomhack.modules.Module;
import de.tyrannus.venomhack.settings.Setting;
import de.tyrannus.venomhack.settings.settings.FloatSetting;
import de.tyrannus.venomhack.utils.ChatUtils;
import de.tyrannus.venomhack.utils.MathUtil;
import de.tyrannus.venomhack.utils.TextUtils;
import java.awt.Color;
import net.minecraft.server.command.ServerCommandSource;
import net.minecraft.text.Text;
import net.minecraft.text.MutableText;

public class ModuleCommand extends Command {
   private final Module module;

   public ModuleCommand(Module module) {
      super(module.getName(), module.description);
      this.module = module;
      Commands.DISPATCHER.register(this.getBuilder());
   }

   @Override
   protected void build(LiteralArgumentBuilder<ServerCommandSource> builder) {
      ((LiteralArgumentBuilder)((LiteralArgumentBuilder)((LiteralArgumentBuilder)((LiteralArgumentBuilder)((LiteralArgumentBuilder)((LiteralArgumentBuilder)((LiteralArgumentBuilder)builder.executes(
                              context -> {
                                 ChatUtils.info(
                                    Text.literal("")
                                       .append(TextUtils.coloredTxt("[", Color.CYAN).append(this.module.getParsedName()).append("]"))
                                       .append(": ")
                                       .append(this.module.description)
                                       .append(" Currently ")
                                       .append(TextUtils.coloredTxt(this.module.isActive() ? "Active" : "Inactive", Color.ORANGE))
                                       .append(".")
                                 );
                                 ChatUtils.sendMsg(
                                    Text.literal("Generic settings: '")
                                       .append(this.format("toggle"))
                                       .append("', '")
                                       .append(this.format("bind"))
                                       .append("', '")
                                       .append(this.format("rename"))
                                       .append("', '")
                                       .append(this.format("release-toggle"))
                                       .append("', '")
                                       .append(this.format("silent"))
                                       .append("' and '")
                                       .append(this.format("drawn"))
                                       .append("'.")
                                 );
                                 if (this.module.SETTINGS.isEmpty()) {
                                    return 1;
                                 } else {
                                    ChatUtils.sendMsg("The " + this.module.getParsedName() + " specific settings are:");
                        
                                    for(Setting<?> setting : this.module.SETTINGS) {
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
                           .then(this.lit("toggle").executes(context -> {
                              this.module.toggle();
                              return 1;
                           })))
                        .then(this.lit("drawn").executes(context -> {
                           this.module.drawn = !this.module.drawn;
                           StringBuilder stringBuilder = new StringBuilder();
                           if (this.module.drawn) {
                              stringBuilder.append("Added ");
                           } else {
                              stringBuilder.append("Removed ");
                           }
                  
                           stringBuilder.append(this.module.getParsedName());
                           if (this.module.drawn) {
                              stringBuilder.append(" to ");
                           } else {
                              stringBuilder.append(" from ");
                           }
                  
                           ChatUtils.info(stringBuilder.append("the array list hud.").toString());
                           return 1;
                        })))
                     .then(
                        ((LiteralArgumentBuilder)this.lit("bind")
                              .executes(
                                 context -> {
                                    ChatUtils.info(
                                       Text.literal("[")
                                          .append(TextUtils.coloredTxt("Bind", Color.LIGHT_GRAY))
                                          .append("]: Binds the module to the specified key. ")
                                          .append(this.module.getParsedName())
                                          .append(this.module.bind.get().getCode() == 0 ? " has not been keybound yet" : " is bound to the key ")
                                          .append(
                                             this.module.bind.get().getCode() == -1
                                                ? Text.literal("")
                                                : TextUtils.coloredTxt(this.module.bind.get().getKey().getLocalizedText().getString(), Color.ORANGE)
                                          )
                                          .append(".")
                                    );
                                    return 1;
                                 }
                              ))
                           .then(this.arg("key", StringArgumentType.greedyString()).executes(context -> {
                              BindCommand.doBind((String)context.getArgument("key", String.class), this.module);
                              return 1;
                           }))
                     ))
                  .then(
                     ((LiteralArgumentBuilder)((LiteralArgumentBuilder)this.lit("rename")
                              .executes(
                                 context -> {
                                    ChatUtils.info(
                                       Text.literal("[")
                                          .append(this.format("rename"))
                                          .append("]: Modify what the module shows up as. Currently it shows up as ")
                                          .append(TextUtils.coloredTxt(this.module.getParsedName(), Color.ORANGE))
                                          .append(".")
                                    );
                                    return 1;
                                 }
                              ))
                           .then(this.lit("reset").executes(context -> {
                              this.module.rename("");
                              ChatUtils.info("Reset " + TextUtils.parseName(this.module.getParsedName()) + "'s name.");
                              return 1;
                           })))
                        .then(
                           this.arg("name", StringArgumentType.greedyString())
                              .executes(
                                 context -> {
                                    this.module.rename((String)context.getArgument("name", String.class));
                                    ChatUtils.info(
                                       Text.literal(TextUtils.parseName(this.module.getName()))
                                          .append(" now shows up as ")
                                          .append(TextUtils.coloredTxt(this.module.getParsedName(), Color.ORANGE))
                                          .append(".")
                                    );
                                    return 1;
                                 }
                              )
                        )
                  ))
               .then(
                  ((LiteralArgumentBuilder)this.lit("release-toggle")
                        .executes(
                           context -> {
                              ChatUtils.info(
                                 Text.literal("[")
                                    .append(this.format("release-toggle"))
                                    .append("]: Whether to toggle the module off after releasing the bind key. Currently set to ")
                                    .append(TextUtils.coloredTxt(String.valueOf(this.module.toggleOnRelease), Color.ORANGE))
                                    .append(".")
                              );
                              return 1;
                           }
                        ))
                     .then(
                        this.arg("bool", BoolArgumentType.bool())
                           .executes(
                              context -> {
                                 this.module.toggleOnRelease = context.getArgument("bool", Boolean.class);
                                 ChatUtils.info(
                                    Text.literal("Set toggle on bind release for module ")
                                       .append(this.module.getParsedName())
                                       .append(" to ")
                                       .append(TextUtils.coloredTxt(String.valueOf(this.module.toggleOnRelease), Color.ORANGE))
                                       .append(".")
                                 );
                                 return 1;
                              }
                           )
                     )
               ))
            .then(
               ((LiteralArgumentBuilder)this.lit("silent")
                     .executes(
                        context -> {
                           ChatUtils.info(
                              Text.literal("[")
                                 .append(this.format("silent"))
                                 .append("]: Stops the module from sending a message when being toggled. Currently set to ")
                                 .append(TextUtils.coloredTxt(String.valueOf(this.module.silent), Color.ORANGE))
                                 .append(".")
                           );
                           return 1;
                        }
                     ))
                  .then(
                     this.arg("bool", BoolArgumentType.bool())
                        .executes(
                           context -> {
                              this.module.silent = context.getArgument("bool", Boolean.class);
                              ChatUtils.info(
                                 Text.literal("Made module ")
                                    .append(this.module.getParsedName())
                                    .append(
                                       TextUtils.coloredTxt(!this.module.silent ? " not silent anymore" : " silent", Color.ORANGE).append(".")
                                    )
                              );
                              return 1;
                           }
                        )
                  )
            ))
         .then(
            ((RequiredArgumentBuilder)((RequiredArgumentBuilder)this.arg("setting", new SettingArgumentType(this.module))
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
                  .then(this.lit("reset").executes(context -> {
                     Setting setting = (Setting)context.getArgument("setting", Setting.class);
                     setting.reset();
                     MutableText text = Text.literal("Reset ").append(this.formatSetting(setting)).append(" to default value of ");
                     if (setting instanceof FloatSetting set) {
                        text.append(TextUtils.coloredTxt(MathUtil.format(MathUtil.round(set.get(), set.getPrecision())), Color.ORANGE));
                     } else {
                        text.append(TextUtils.coloredTxt(setting.storeValue(), Color.ORANGE));
                     }
            
                     ChatUtils.info(text.append("."));
                     return 1;
                  })))
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
         );
   }

   private MutableText formatSetting(Setting<?> setting) {
      return this.format(setting.parsedName());
   }

   private MutableText format(String string) {
      return TextUtils.coloredTxt(string, Color.LIGHT_GRAY);
   }
}
