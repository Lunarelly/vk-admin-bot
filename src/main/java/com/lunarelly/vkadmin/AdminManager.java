package com.lunarelly.vkadmin;

import api.longpoll.bots.methods.VkBotsMethods;
import com.google.gson.Gson;
import com.google.gson.stream.JsonReader;
import com.lunarelly.vkadmin.command.CommandManager;
import com.lunarelly.vkadmin.data.AvailableCommandsData;
import com.lunarelly.vkadmin.data.SettingsData;
import com.lunarelly.vkadmin.nickname.NicknameManager;
import com.lunarelly.vkadmin.punishment.PunishmentManager;
import com.lunarelly.vkadmin.rank.RankManager;
import com.lunarelly.vkadmin.util.Utils;
import lombok.Getter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;

public final class AdminManager {
    @Getter
    private static final Logger logger = LoggerFactory.getLogger(AdminManager.class);

    @Getter
    private static VkBotsMethods vk;

    @Getter
    private static SettingsData settings;
    @Getter
    private static AvailableCommandsData availableCommands;

    @Getter
    private static CommandManager commandManager;
    @Getter
    private static NicknameManager nicknameManager;
    @Getter
    private static RankManager rankManager;
    @Getter
    private static PunishmentManager punishmentManager;

    private AdminManager() {}

    private static void loadData() throws FileNotFoundException {
        settings = (new Gson()).fromJson(new JsonReader(new FileReader(
                Utils.getCurrentWorkingDirectory() + Utils.getDirectorySeparator() + "settings.json"
        )), SettingsData.class);
        availableCommands = (new Gson()).fromJson(new JsonReader(new FileReader(
                Utils.getCurrentWorkingDirectory() + Utils.getDirectorySeparator() + "allowed_commands.json"
        )), AvailableCommandsData.class);
    }

    private static void registerManagers() throws IOException {
        commandManager = new CommandManager();
        nicknameManager = new NicknameManager(commandManager);
        rankManager = new RankManager(commandManager, nicknameManager);
        punishmentManager = new PunishmentManager(commandManager, nicknameManager, rankManager, vk);
    }

    public static void initialize(VkBotsMethods vk) throws IOException {
        AdminManager.vk = vk;
        loadData();
        registerManagers();
    }
}
