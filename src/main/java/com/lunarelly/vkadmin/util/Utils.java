package com.lunarelly.vkadmin.util;

import api.longpoll.bots.exceptions.VkApiException;
import api.longpoll.bots.methods.VkBotsMethods;
import api.longpoll.bots.methods.impl.messages.Send;
import api.longpoll.bots.model.objects.basic.User;
import com.lunarelly.vkadmin.AdminManager;
import org.jetbrains.annotations.Nullable;

import java.nio.file.FileSystems;
import java.util.List;
import java.util.concurrent.ExecutionException;

public final class Utils {
    public static final String ACTION_KICK = "chat_kick_user";

    private Utils() {}

    public static String getCurrentWorkingDirectory() {
        return FileSystems.getDefault().getPath("").toAbsolutePath().toString();
    }

    public static String getDirectorySeparator() {
        return FileSystems.getDefault().getSeparator();
    }

    public static int parseVkId(String data) {
        try {
            String id = (data.split("\\|")[0]).replaceAll("\\D", "");
            if (id.isEmpty()) {
                return 0;
            }
            return Integer.parseInt(id);
        } catch (NumberFormatException e) {
            return 0;
        }
    }

    public static int getChatFromPeerId(int id) {
        return id - 2000000000;
    }

    public static void sendMessageAsync(VkBotsMethods vk, int peerId, int replyId, String messageText) {
        vk.messages.send()
                .setPeerId(peerId)
                .setReplyTo(replyId)
                .setMessage(messageText)
                .executeAsync();
    }

    @Nullable
    public static Send.ResponseBody.Response sendEditableMessage(VkBotsMethods vk, int peerId, int replyId, String messageText) {
        try {
            Send.ResponseBody responseBody = vk.messages.send()
                    .setPeerIds(peerId)
                    .setReplyTo(replyId)
                    .setMessage(messageText)
                    .executeAsync().get();
            if (responseBody.getResponse() instanceof List<?>) {
                return (Send.ResponseBody.Response) ((List<?>) responseBody.getResponse()).getFirst();
            } else {
                return null;
            }
        } catch (Exception e) {
            AdminManager.getLogger().error(e.getMessage());
            return null;
        }
    }

    public static void editMessageAsync(VkBotsMethods vk, int peerId, int messageId, String messageText) {
        vk.messages.edit()
                .setPeerId(peerId)
                .setConversationMessageId(messageId)
                .setMessage(messageText)
                .setDisableMentions(true)
                .executeAsync();
    }

    public static void kickAsync(VkBotsMethods vk, int chatId, int userId) {
        vk.messages.removeChatUser()
                .setChatId(chatId)
                .setUserId(userId)
                .executeAsync();
    }

    public static List<User> getChatMembers(VkBotsMethods vk, int chatId) throws VkApiException, ExecutionException, InterruptedException {
        return vk.messages.getConversationMembers()
                .setPeerId(chatId)
                .executeAsync()
                .get()
                .getResponse()
                .getProfiles();
    }

    public static boolean isChat(int peerId) {
        return Utils.getChatFromPeerId(peerId) > 0;
    }

    public static boolean isUserInChat(VkBotsMethods vk, int userId, int chatId) {
        try {
            for (User user : Utils.getChatMembers(vk, chatId)) {
                if (user.getId() == userId) {
                    return true;
                }
            }
        } catch (VkApiException | ExecutionException | InterruptedException e) {
            AdminManager.getLogger().error(e.getMessage());
        }
        return false;
    }

    @Nullable
    public static User getUserFromChat(VkBotsMethods vk, int userId, int chatId) {
        try {
            for (User user : Utils.getChatMembers(vk, chatId)) {
                if (user.getId() == userId) {
                    return user;
                }
            }
        } catch (VkApiException | ExecutionException | InterruptedException e) {
            AdminManager.getLogger().error(e.getMessage());
        }
        return null;
    }

    public static String createMention(int userId, String context) {
        return "[id" + userId + "|" + context + "]";
    }
}
