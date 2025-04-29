package com.lunarelly.vkadmin.nickname.command;

import api.longpoll.bots.model.objects.basic.Message;
import com.lunarelly.vkadmin.AdminManager;
import com.lunarelly.vkadmin.command.Command;
import com.lunarelly.vkadmin.nickname.NicknameManager;
import com.lunarelly.vkadmin.rank.RankManager;
import com.lunarelly.vkadmin.rank.RankPriority;
import com.lunarelly.vkadmin.util.Utils;

import java.util.List;

public final class RemoveNicknameCommand extends Command {
    private final NicknameManager nicknameManager;

    public RemoveNicknameCommand(NicknameManager nicknameManager) {
        super("removenick");
        this.nicknameManager = nicknameManager;
    }

    @Override
    public void execute(Message message, List<String> args) {
        int userId = message.getFromId();
        if (!testPermission(message)) {
            return;
        }

        RankManager rankManager = AdminManager.getRankManager();
        int targetId = args.isEmpty() ? userId : Utils.parseVkId(args.getFirst());
        if (this.nicknameManager.get(targetId) == null) {
            sendMessageAsync(message, "У этого пользователя нет ника!");
            return;
        }

        if (targetId != userId && rankManager.get(targetId).getPriority() >= rankManager.get(userId).getPriority()) {
            sendMessageAsync(message, "Вы не можете убрать ник этого пользователя!");
            return;
        }

        this.nicknameManager.delete(targetId);
        sendMessageAsync(message, "Вы успешно убрали ник пользователя!");
    }

    @Override
    public int getRankLevel() {
        return RankPriority.MANAGER;
    }
}
