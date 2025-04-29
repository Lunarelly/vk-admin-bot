package com.lunarelly.vkadmin.command;

import api.longpoll.bots.methods.VkBotsMethods;
import api.longpoll.bots.methods.impl.messages.Send;
import api.longpoll.bots.model.objects.basic.Message;
import com.lunarelly.vkadmin.AdminManager;
import com.lunarelly.vkadmin.util.Utils;
import lombok.Getter;

import java.util.List;

public abstract class Command {
    @Getter
    protected final String name;
    protected final VkBotsMethods vk;

    public Command(String name) {
        this.name = name;
        this.vk = AdminManager.getVk();
    }

    protected boolean testPermissionSilent(int userId) {
        return AdminManager.getRankManager().get(userId).getPriority() >= getRankLevel();
    }

    protected boolean testPermission(Message message) {
        if (!testPermissionSilent(message.getFromId())) {
            sendMessageAsync(message, "❌ У Вас нет прав на эту команду!");
            return false;
        } else {
            return true;
        }
    }

    protected void sendMessageAsync(Message message, String messageText) {
        Utils.sendMessageAsync(this.vk, message.getPeerId(), message.getId(), messageText);
    }

    protected Send.ResponseBody.Response sendEditableMessage(Message message, String messageText) {
        return Utils.sendEditableMessage(this.vk, message.getPeerId(), message.getId(), messageText);
    }

    protected void editMessageAsync(Message message, int messageId, String messageText) {
        Utils.editMessageAsync(this.vk, message.getPeerId(), messageId, messageText);
    }

    public abstract void execute(Message message, List<String> args);

    public abstract int getRankLevel();
}
