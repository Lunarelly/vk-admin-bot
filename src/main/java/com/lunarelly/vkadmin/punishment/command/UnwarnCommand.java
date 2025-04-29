package com.lunarelly.vkadmin.punishment.command;

import api.longpoll.bots.model.objects.basic.Message;
import api.longpoll.bots.model.objects.basic.User;
import com.lunarelly.vkadmin.punishment.PunishmentManager;
import com.lunarelly.vkadmin.rank.RankManager;
import com.lunarelly.vkadmin.rank.RankPriority;
import com.lunarelly.vkadmin.util.Utils;

import java.util.List;

public final class UnwarnCommand extends PunishmentCommand {
    private final RankManager rankManager;

    public UnwarnCommand(PunishmentManager punishmentManager, RankManager rankManager) {
        super("unwarn", punishmentManager);
        this.rankManager = rankManager;
    }

    @Override
    public void execute(Message message, List<String> args) {
        int userId = message.getFromId();
        if (!testPermission(message)) {
            return;
        }

        if (args.isEmpty()) {
            sendMessageAsync(message, "Использование:\n- /chat unwarn <Пользователь> [Количество]");
            return;
        }

        int targetId = Utils.parseVkId(args.get(0));
        if (targetId == 0) {
            sendMessageAsync(message, "Айди пользователя указан неверно!");
            return;
        }

        if (targetId == userId) {
            sendMessageAsync(message, "Вы не можете забирать предупреждения у самого себя!");
            return;
        }

        User target = Utils.getUserFromChat(this.vk, targetId, message.getPeerId());
        if (target == null) {
            sendMessageAsync(message, "Пользователя нет в чате!");
            return;
        }

        int targetPriority = this.rankManager.get(targetId).getPriority();
        if (targetPriority >= this.rankManager.get(userId).getPriority() || targetPriority >= RankPriority.MANAGER) {
            sendMessageAsync(message, "Вы не можете убирать предупреждения этого пользователя!");
            return;
        }

        int amount;
        if (args.size() >= 2) {
            try {
                amount = Integer.parseInt(args.get(1));
            } catch (NumberFormatException ignored) {
                amount = 1;
            }
        } else {
            amount = 1;
        }

        String messageText;
        if (this.punishmentManager.getWarns(targetId) == 0) {
            messageText = "У этого пользователя нет предупреждений!";
        } else {
            this.punishmentManager.removeWarn(targetId, amount);
            messageText = "Предупреждения пользователя " +
                    Utils.createMention(targetId, target.getFirstName() + " " + target.getLastName()) + ": " +
                    this.punishmentManager.getWarns(targetId) + "/" + PunishmentManager.MAX_WARNS;
        }
        sendMessageAsync(message, messageText);
    }

    @Override
    public int getRankLevel() {
        return RankPriority.ADMIN;
    }
}
