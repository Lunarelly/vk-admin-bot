package com.lunarelly.vkadmin.punishment;

import api.longpoll.bots.methods.VkBotsMethods;
import com.lunarelly.vkadmin.AdminManager;
import com.lunarelly.vkadmin.command.CommandManager;
import com.lunarelly.vkadmin.nickname.NicknameManager;
import com.lunarelly.vkadmin.punishment.command.KickCommand;
import com.lunarelly.vkadmin.punishment.command.UnwarnCommand;
import com.lunarelly.vkadmin.punishment.command.WarnCommand;
import com.lunarelly.vkadmin.rank.Rank;
import com.lunarelly.vkadmin.rank.RankManager;
import com.lunarelly.vkadmin.util.Utils;
import org.iq80.leveldb.DB;
import org.iq80.leveldb.Options;

import java.io.File;
import java.io.IOException;

import static org.iq80.leveldb.impl.Iq80DBFactory.factory;

public final class PunishmentManager implements AutoCloseable {
    public static final int MAX_WARNS = 3;

    private final DB warnsDatabase;

    private final NicknameManager nicknameManager;
    private final RankManager rankManager;
    private final VkBotsMethods vk;

    private void registerCommands(CommandManager commandManager) {
        commandManager.register(new KickCommand(this, this.rankManager));
        commandManager.register(new WarnCommand(this, this.rankManager));
        commandManager.register(new UnwarnCommand(this, this.rankManager));
    }

    public PunishmentManager(
            CommandManager commandManager,
            NicknameManager nicknameManager,
            RankManager rankManager,
            VkBotsMethods vk
    ) throws IOException {
        this.warnsDatabase = factory.open(new File(Utils.getCurrentWorkingDirectory() + Utils.getDirectorySeparator() + "warns"), new Options());
        this.nicknameManager = nicknameManager;
        this.rankManager = rankManager;
        this.vk = vk;
        registerCommands(commandManager);
    }

    public int getWarns(int userId) {
        byte[] result = this.warnsDatabase.get(String.valueOf(userId).getBytes());
        return result == null ? 0 : Integer.parseInt(new String(result));
    }

    public void addWarn(int userId, int amount) {
        this.warnsDatabase.put(String.valueOf(userId).getBytes(), String.valueOf(amount + getWarns(userId)).getBytes());
    }

    public void removeWarn(int userId, int amount) {
        this.warnsDatabase.put(String.valueOf(userId).getBytes(), String.valueOf(Math.max(getWarns(userId) - amount, 0)).getBytes());
    }

    public boolean checkWarns(int userId, int addedAmount) {
        return (getWarns(userId) + addedAmount) < PunishmentManager.MAX_WARNS;
    }

    public void removeUser(int chatId, int userId, boolean kick) {
        Rank rank = this.rankManager.get(userId);
        if (rank != Rank.OWNER) {
            if (this.nicknameManager.get(userId) != null) {
                this.nicknameManager.delete(userId);
            }

            if (rank != Rank.DEFAULT) {
                this.rankManager.set(userId, Rank.DEFAULT);
            }

            int warns = getWarns(userId);
            if (warns != 0) {
                removeWarn(userId, warns);
            }

            if (kick) {
                Utils.kickAsync(this.vk, chatId, userId);
            }
            AdminManager.getLogger().info("Пользователь (ID: " + userId + ") был кикнут из беседы");
        }
    }

    @Override
    public void close() throws IOException {
        this.warnsDatabase.close();
    }
}
