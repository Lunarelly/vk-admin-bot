package com.lunarelly.vkadmin.rank.command;

import api.longpoll.bots.exceptions.VkApiException;
import api.longpoll.bots.model.objects.basic.Message;
import api.longpoll.bots.model.objects.basic.User;
import com.lunarelly.vkadmin.AdminManager;
import com.lunarelly.vkadmin.command.Command;
import com.lunarelly.vkadmin.rank.Rank;
import com.lunarelly.vkadmin.rank.RankManager;
import com.lunarelly.vkadmin.rank.RankPriority;
import com.lunarelly.vkadmin.util.Utils;

import java.util.List;
import java.util.concurrent.ExecutionException;

public final class TagCommand extends Command {
    private final RankManager rankManager;

    public TagCommand(RankManager rankManager) {
        super("tag");
        this.rankManager = rankManager;
    }

    @Override
    public void execute(Message message, List<String> args) {
        if (!testPermission(message)) {
            return;
        }

        if (args.isEmpty()) {
            sendMessageAsync(message, "Использование: /tag <Ранг>");
            return;
        }

        Rank rank = Rank.tryFrom(args.get(0));
        if (rank == null) {
            sendMessageAsync(message, "Данного ранга не существует!");
            return;
        }

        String result;
        try {
            boolean haveUsersInRank = false;
            boolean needComma = false;
            StringBuilder messageText = new StringBuilder();
            for (User user : Utils.getChatMembers(this.vk, message.getPeerId())) {
                int userId = user.getId();
                if (this.rankManager.get(userId) == rank) {
                    if (!haveUsersInRank) {
                        messageText.append(rank.getDisplayName()).append(":\n");
                        haveUsersInRank = true;
                    }

                    if (needComma) {
                        messageText.append(", ");
                    } else {
                        needComma = true;
                    }
                    messageText.append(Utils.createMention(userId, user.getFirstName() + " " + user.getLastName()));
                }
            }
            if (haveUsersInRank) {
                result = messageText.toString();
            } else {
                result = "Нет участников с данным рангом!";
            }
        } catch (VkApiException | ExecutionException | InterruptedException e) {
            AdminManager.getLogger().error(e.getMessage());
            result = "Что-то пошло не так!";
        }
        sendMessageAsync(message, result);
    }

    @Override
    public int getRankLevel() {
        return RankPriority.OWNER;
    }
}
