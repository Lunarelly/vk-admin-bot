package com.lunarelly.vkadmin.rank.command;

import api.longpoll.bots.exceptions.VkApiException;
import api.longpoll.bots.methods.impl.messages.Send;
import api.longpoll.bots.model.objects.basic.Message;
import api.longpoll.bots.model.objects.basic.User;
import com.lunarelly.vkadmin.AdminManager;
import com.lunarelly.vkadmin.command.Command;
import com.lunarelly.vkadmin.nickname.NicknameManager;
import com.lunarelly.vkadmin.rank.Rank;
import com.lunarelly.vkadmin.rank.RankManager;
import com.lunarelly.vkadmin.rank.RankPriority;
import com.lunarelly.vkadmin.util.Utils;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.ExecutionException;

public final class StaffCommand extends Command {
    private final RankManager rankManager;
    private final NicknameManager nicknameManager;

    public StaffCommand(RankManager rankManager, NicknameManager nicknameManager) {
        super("staff");
        this.rankManager = rankManager;
        this.nicknameManager = nicknameManager;
    }

    @Override
    public void execute(Message message, List<String> args) {
        if (!testPermission(message)) {
            return;
        }

        Send.ResponseBody.Response response = sendEditableMessage(message, "Получаю список персонала...");
        if (response == null) {
            sendMessageAsync(message, "Что-то пошло не так!");
            return;
        }

        String usersResult;
        try {
            StringBuilder messageText = new StringBuilder("\uD83D\uDC64 Активный персонал:");
            List<Rank> ranks = Arrays.asList(Rank.values());
            Collections.reverse(ranks);
            for (Rank rank : ranks) {
                if (rank == Rank.DEFAULT) {
                    continue;
                }

                boolean haveUsersInRank = false;
                boolean needComma = false;
                for (User user : Utils.getChatMembers(this.vk, message.getPeerId())) {
                    int tempUserId = user.getId();
                    if (this.rankManager.get(tempUserId) == rank) {
                        if (!haveUsersInRank) {
                            messageText.append("\n\n").append(rank.getDisplayName()).append(":\n");
                            haveUsersInRank = true;
                        }

                        if (needComma) {
                            messageText.append(", ");
                        } else {
                            needComma = true;
                        }
                        String nickname = this.nicknameManager.get(tempUserId);
                        messageText.append(Utils.createMention(tempUserId, nickname == null ?
                                user.getFirstName() + " " + user.getLastName() :
                                nickname
                        ));
                    }
                }
            }
            usersResult = messageText.toString();
        } catch (VkApiException | ExecutionException | InterruptedException e) {
            AdminManager.getLogger().error(e.getMessage());
            usersResult = "Что-то пошло не так!";
        }

        Integer messageId = response.getConversationMessageId();
        editMessageAsync(message, messageId == null ? 0 : messageId, usersResult);
    }

    @Override
    public int getRankLevel() {
        return RankPriority.MODERATOR;
    }
}
