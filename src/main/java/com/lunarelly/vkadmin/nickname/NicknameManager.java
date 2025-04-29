package com.lunarelly.vkadmin.nickname;

import com.lunarelly.vkadmin.command.CommandManager;
import com.lunarelly.vkadmin.nickname.command.NicknameCommand;
import com.lunarelly.vkadmin.nickname.command.RemoveNicknameCommand;
import com.lunarelly.vkadmin.util.Utils;
import org.iq80.leveldb.DB;
import org.iq80.leveldb.Options;
import org.jetbrains.annotations.Nullable;

import java.io.File;
import java.io.IOException;

import static org.iq80.leveldb.impl.Iq80DBFactory.factory;

public final class NicknameManager implements AutoCloseable {
    private final DB database;

    private void registerCommands(CommandManager commandManager) {
        commandManager.register(new NicknameCommand(this));
        commandManager.register(new RemoveNicknameCommand(this));
    }

    public NicknameManager(CommandManager commandManager) throws IOException {
        this.database = factory.open(new File(Utils.getCurrentWorkingDirectory() + Utils.getDirectorySeparator() + "nicknames"), new Options());
        registerCommands(commandManager);
    }

    @Nullable
    public String get(int userId) {
        byte[] result = this.database.get(String.valueOf(userId).getBytes());
        return result == null ? null : new String(result);
    }

    public void set(int userId, String nickname) {
        this.database.put(String.valueOf(userId).getBytes(), nickname.getBytes());
    }

    public void delete(int userId) {
        if (get(userId) != null) {
            this.database.delete(String.valueOf(userId).getBytes());
        }
    }

    @Override
    public void close() throws Exception {
        this.database.close();
    }
}
