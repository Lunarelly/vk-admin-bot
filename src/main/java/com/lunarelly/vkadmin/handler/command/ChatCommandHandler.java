package com.lunarelly.vkadmin.handler.command;

import api.longpoll.bots.model.objects.basic.Message;
import com.lunarelly.vkadmin.command.Command;
import com.lunarelly.vkadmin.command.CommandManager;

import java.util.List;

public final class ChatCommandHandler implements CommandHandler {
    private final CommandManager commandManager;

    public ChatCommandHandler(CommandManager commandManager) {
        this.commandManager = commandManager;
    }

    public void handleCommand(Message message, List<String> command) {
        Command commandObject = this.commandManager.get(command.get(0));
        if (commandObject != null) {
            command.remove(0);
            if (message.hasReplyMessage()) {
                command.addFirst(message.getReplyMessage().getFromId().toString());
            }
            commandObject.execute(message, command);
        }
    }
}
