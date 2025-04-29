package com.lunarelly.vkadmin.rank.command;

import api.longpoll.bots.model.objects.basic.Message;
import com.lunarelly.vkadmin.AdminManager;
import com.lunarelly.vkadmin.command.Command;
import com.lunarelly.vkadmin.rank.Rank;
import com.lunarelly.vkadmin.rank.RankManager;
import com.lunarelly.vkadmin.rank.RankPriority;
import com.lunarelly.vkadmin.util.Utils;
import org.apache.commons.lang3.StringUtils;

import java.util.Arrays;
import java.util.List;

public final class RankCommand extends Command {
    private final RankManager rankManager;

    public RankCommand(RankManager rankManager) {
        super("rank");
        this.rankManager = rankManager;
    }

    @Override
    public void execute(Message message, List<String> args) {
        int userId = message.getFromId();
        if (!testPermission(message)) {
            return;
        }

        if (args.isEmpty()) {
            sendMessageAsync(message, "Использование:\n- /chat rank <Пользователь> <Ранг>\n- /chat rank list");
            return;
        }

        String arg = args.get(0).toLowerCase();
        if (arg.equals("list")) {
            sendMessageAsync(message, "Список доступных рангов: " + StringUtils.join(Arrays.stream(Rank.values()).map(Rank::getDisplayName).toList(), ", "));
            return;
        }

        if (args.size() < 2) {
            sendMessageAsync(message, "Использование:\n- /chat rank <Пользователь> <Ранг>\n- /chat rank list");
            return;
        }

        int targetId = Utils.parseVkId(arg);
        if (targetId == 0) {
            sendMessageAsync(message, "Айди пользователя указан неверно!");
            return;
        }

        if (targetId == userId) {
            sendMessageAsync(message, "Вы не можете сменить свой ранг!");
            return;
        }

        Rank rank = Rank.tryFrom(args.get(1));
        if (rank == null) {
            sendMessageAsync(message, "Данного ранга не существует!");
            return;
        }

        if (rank != Rank.DEFAULT && !Utils.isUserInChat(this.vk, targetId, message.getPeerId())) {
            sendMessageAsync(message, "Пользователя нет в чате!");
            return;
        }

        if (rank.getPriority() >= this.rankManager.get(userId).getPriority()) {
            sendMessageAsync(message, "Вы не можете установить ранг " + rank.getDisplayName() + "!");
            return;
        }

        Rank currentRank = this.rankManager.get(targetId);
        if (currentRank.getPriority() >= this.rankManager.get(userId).getPriority()) {
            sendMessageAsync(message, "Вы не можете сменить ранг этому пользователю!");
            return;
        }

        if (currentRank == rank) {
            sendMessageAsync(message, "Ранг этого пользователя и так " + rank.getDisplayName() + "!");
            return;
        }

        this.rankManager.set(targetId, rank);
        sendMessageAsync(message, "Вы успешно установили ранг " + rank.getDisplayName() + " пользователю!");
        AdminManager.getLogger().info("Пользователь (ID: " + userId + ") выдал ранг " + rank.getDisplayName() + " пользователю (ID: " + targetId + ")");
    }

    @Override
    public int getRankLevel() {
        return RankPriority.MANAGER;
    }
}
