package com.lunarelly.vkadmin.command;

import com.lunarelly.vkadmin.command.defaults.HelpCommand;
import org.jetbrains.annotations.Nullable;

import java.util.HashMap;
import java.util.Map;

public final class CommandManager {
    private final Map<String, Command> commandMap = new HashMap<>();

    public CommandManager() {
        registerDefaults();
    }

    @Nullable
    public Command get(String name) {
        return this.commandMap.get(name);
    }

    public void register(Command command) {
        this.commandMap.put(command.getName(), command);
    }

    private void registerDefaults() {
        register(new HelpCommand());
    }
}
