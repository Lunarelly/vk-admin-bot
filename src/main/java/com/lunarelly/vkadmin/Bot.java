package com.lunarelly.vkadmin;

import api.longpoll.bots.LongPollBot;
import api.longpoll.bots.exceptions.VkApiException;
import api.longpoll.bots.model.events.messages.MessageNew;
import api.longpoll.bots.model.objects.basic.Message;
import com.lunarelly.vkadmin.data.SettingsData;
import com.lunarelly.vkadmin.handler.command.ChatCommandHandler;
import com.lunarelly.vkadmin.handler.KickHandler;
import com.lunarelly.vkadmin.handler.command.RconCommandHandler;
import com.lunarelly.vkadmin.rank.RankManager;
import com.lunarelly.vkadmin.util.Utils;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public final class Bot extends LongPollBot {
    private final SettingsData settings;
    private final RankManager rankManager;
    private final ChatCommandHandler chatHandler;
    private final RconCommandHandler rconHandler;
    private final KickHandler kickHandler;

    public Bot() throws VkApiException, IOException {
        super();
        AdminManager.initialize(this.vk);
        this.settings = AdminManager.getSettings();
        this.rankManager = AdminManager.getRankManager();
        this.chatHandler = new ChatCommandHandler(AdminManager.getCommandManager());
        this.rconHandler = new RconCommandHandler(this.settings, this.vk);
        this.kickHandler = new KickHandler(AdminManager.getPunishmentManager());
        startPolling();
    }

    @Override
    public void onMessageNew(MessageNew messageNew) {
        Message message = messageNew.getMessage();
        if (Utils.isChat(message.getPeerId())) {
            Message.Action action = message.getAction();
            if (action != null && action.getType().equals(Utils.ACTION_KICK)) {
                this.kickHandler.handleKick(message);
                return;
            }

            String preCommand = message.getText();
            if (preCommand.startsWith(this.settings.getChatCommandPrefix())) {
                List<String> command = new ArrayList<>(Arrays.asList(preCommand.split("\\s+")));
                command.remove(0);
                if (!command.isEmpty()) {
                    this.chatHandler.handleCommand(message, command);
                }
            } else if (preCommand.startsWith(this.settings.getRconCommandSymbol())) {
                String command = preCommand.substring(1).trim();
                if (!(command.isEmpty()) && this.rankManager.get(message.getFromId()).getAvailableCommands().contains(command.split("\\s+")[0].toLowerCase())) {
                    this.rconHandler.handleCommand(message, command);
                }
            }
        } else {
            Utils.sendMessageAsync(this.vk, message.getPeerId(), message.getId(), "❌ Бот доступен только в чате!");
        }
    }

    @Override
    public String getAccessToken() {
        return this.settings.getToken();
    }
}
