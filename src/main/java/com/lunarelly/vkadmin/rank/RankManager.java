package com.lunarelly.vkadmin.rank;

import com.lunarelly.vkadmin.command.CommandManager;
import com.lunarelly.vkadmin.nickname.NicknameManager;
import com.lunarelly.vkadmin.rank.command.RankCommand;
import com.lunarelly.vkadmin.rank.command.StaffCommand;
import com.lunarelly.vkadmin.rank.command.TagCommand;
import com.lunarelly.vkadmin.util.Utils;
import org.iq80.leveldb.DB;
import org.iq80.leveldb.Options;

import java.io.File;
import java.io.IOException;

import static org.iq80.leveldb.impl.Iq80DBFactory.factory;

public final class RankManager implements AutoCloseable {
    private final DB database;

    private void registerCommands(CommandManager commandManager, NicknameManager nicknameManager) {
        commandManager.register(new RankCommand(this));
        commandManager.register(new StaffCommand(this, nicknameManager));
        commandManager.register(new TagCommand(this));
    }

    public RankManager(CommandManager commandManager, NicknameManager nicknameManager) throws IOException {
        this.database = factory.open(new File(Utils.getCurrentWorkingDirectory() + Utils.getDirectorySeparator() + "ranks"), new Options());
        registerCommands(commandManager, nicknameManager);
    }

    public Rank get(int userId) {
        byte[] result = this.database.get(String.valueOf(userId).getBytes());
        return result == null ? Rank.DEFAULT : Rank.from(new String(result));
    }

    public void set(int userId, Rank rank) {
        byte[] key = String.valueOf(userId).getBytes();
        if (rank == Rank.DEFAULT && this.database.get(key) != null) {
            this.database.delete(key);
        } else {
            this.database.put(String.valueOf(userId).getBytes(), rank.name().toLowerCase().getBytes());
        }
    }

    @Override
    public void close() throws IOException {
        this.database.close();
    }
}
