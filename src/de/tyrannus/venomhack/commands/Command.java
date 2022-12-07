package de.tyrannus.venomhack.commands;

import com.mojang.brigadier.arguments.ArgumentType;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import com.mojang.brigadier.builder.RequiredArgumentBuilder;
import de.tyrannus.venomhack.utils.TextUtils;
import net.minecraft.server.command.ServerCommandSource;
import net.minecraft.server.command.CommandManager;
import net.minecraft.client.MinecraftClient;

public abstract class Command {
   protected static final MinecraftClient mc = MinecraftClient.getInstance();
   private final String name;
   private final String description;
   private final LiteralArgumentBuilder<ServerCommandSource> builder;

   public Command(String name, String description) {
      this.name = TextUtils.inverseParse(name);
      this.description = description;
      this.builder = this.lit(name);
   }

   protected abstract void build(LiteralArgumentBuilder<ServerCommandSource> var1);

   public String getName() {
      return this.name;
   }

   public String getDescription() {
      return this.description;
   }

   public LiteralArgumentBuilder<ServerCommandSource> getBuilder() {
      this.build(this.builder);
      return this.builder;
   }

   protected LiteralArgumentBuilder<ServerCommandSource> lit(String literal) {
      return CommandManager.literal(literal);
   }

   protected <T> RequiredArgumentBuilder<ServerCommandSource, T> arg(String name, ArgumentType<T> type) {
      return CommandManager.argument(name, type);
   }
}
