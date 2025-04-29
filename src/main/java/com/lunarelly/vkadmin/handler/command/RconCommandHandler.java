package com.lunarelly.vkadmin.handler.command;

import api.longpoll.bots.methods.VkBotsMethods;
import api.longpoll.bots.model.objects.basic.Message;
import com.lunarelly.vkadmin.data.SettingsData;
import com.lunarelly.vkadmin.network.rcon.Rcon;

import java.io.IOException;

public final class RconCommandHandler implements CommandHandler {
    private final SettingsData settings;
    private final VkBotsMethods vk;

    public RconCommandHandler(SettingsData settings, VkBotsMethods vk) {
        this.settings = settings;
        this.vk = vk;
    }

    public void handleCommand(Message message, String command) {
        String messageText;
        try (Rcon rcon = new Rcon(this.settings.getRconIp(), this.settings.getRconPort(), this.settings.getRconPassword().getBytes())) {
            messageText = "Ответ сервера:\n" + rcon.command(command);
        } catch (IOException e) {
            messageText = "Сервер не отвечает!";
            System.out.println(e.getMessage());
        }
        this.vk.messages.send()
                .setPeerId(message.getPeerId())
                .setReplyTo(message.getId())
                .setMessage(messageText)
                .executeAsync();
    }
}
