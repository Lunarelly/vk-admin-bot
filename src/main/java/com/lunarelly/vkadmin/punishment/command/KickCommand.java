package com.lunarelly.vkadmin.punishment.command;

import api.longpoll.bots.model.objects.basic.Message;
import com.lunarelly.vkadmin.punishment.PunishmentManager;
import com.lunarelly.vkadmin.rank.RankManager;
import com.lunarelly.vkadmin.rank.RankPriority;
import com.lunarelly.vkadmin.util.Utils;

import java.util.List;

public final class KickCommand extends PunishmentCommand {
    private final RankManager rankManager;

    public KickCommand(PunishmentManager punishmentManager, RankManager rankManager) {
        super("kick", punishmentManager);
        this.rankManager = rankManager;
    }

    @Override
    public void execute(Message message, List<String> args) {
        int userId = message.getFromId();
        if (!testPermission(message)) {
            return;
        }

        if (args.isEmpty()) {
            sendMessageAsync(message, "Использование: /chat kick <Пользователь>");
            return;
        }

        int targetId = Utils.parseVkId(args.get(0));
        if (targetId == 0) {
            sendMessageAsync(message, "Айди пользователя указан неверно!");
            return;
        }

        if (targetId == userId) {
            sendMessageAsync(message, "Вы не можете кикнуть самого себя!");
            return;
        }

        if (this.rankManager.get(targetId).getPriority() >= this.rankManager.get(userId).getPriority()) {
            sendMessageAsync(message, "Вы не можете кикнуть этого пользователя!");
            return;
        }

        kickAsync(message, targetId);
    }

    @Override
    public int getRankLevel() {
        return RankPriority.MANAGER;
    }
}
