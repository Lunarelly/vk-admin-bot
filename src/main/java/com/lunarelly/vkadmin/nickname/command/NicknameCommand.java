package com.lunarelly.vkadmin.nickname.command;

import api.longpoll.bots.model.objects.basic.Message;
import com.lunarelly.vkadmin.AdminManager;
import com.lunarelly.vkadmin.command.Command;
import com.lunarelly.vkadmin.nickname.NicknameManager;
import com.lunarelly.vkadmin.rank.RankManager;
import com.lunarelly.vkadmin.rank.RankPriority;
import com.lunarelly.vkadmin.util.Utils;

import java.util.List;

public final class NicknameCommand extends Command {
    private final NicknameManager nicknameManager;

    public NicknameCommand(NicknameManager nicknameManager) {
        super("nick");
        this.nicknameManager = nicknameManager;
    }

    @Override
    public void execute(Message message, List<String> args) {
        int userId = message.getFromId();
        if (!testPermission(message)) {
            return;
        }

        if (args.size() < 2) {
            sendMessageAsync(message, "Использование: /chat nick <Пользователь> <Ник>");
            return;
        }

        int targetId = Utils.parseVkId(args.getFirst());
        if (targetId == 0) {
            sendMessageAsync(message, "Айди пользователя указан неверно!");
            return;
        }

        if (!Utils.isUserInChat(this.vk, targetId, message.getPeerId())) {
            sendMessageAsync(message, "Пользователя нет в чате!");
            return;
        }

        RankManager rankManager = AdminManager.getRankManager();
        if (targetId != userId && rankManager.get(targetId).getPriority() >= rankManager.get(userId).getPriority()) {
            sendMessageAsync(message, "Вы не можете сменить ник этому пользователю!");
            return;
        }

        args.removeFirst();
        String nickname = String.join(" ", args).trim();
        if (nickname.length() < 3 || nickname.length() > 16) {
            sendMessageAsync(message, "Ник указан неверно!");
            return;
        }
        this.nicknameManager.set(targetId, nickname);
        sendMessageAsync(message, "Вы установили ник пользователю: " + nickname);
    }

    @Override
    public int getRankLevel() {
        return RankPriority.MANAGER;
    }
}
