package com.lunarelly.vkadmin.punishment.command;

import api.longpoll.bots.model.objects.basic.Message;
import com.lunarelly.vkadmin.command.Command;
import com.lunarelly.vkadmin.punishment.PunishmentManager;
import com.lunarelly.vkadmin.util.Utils;

public abstract class PunishmentCommand extends Command {
    protected final PunishmentManager punishmentManager;

    public PunishmentCommand(String name, PunishmentManager punishmentManager) {
        super(name);
        this.punishmentManager = punishmentManager;
    }

    protected void kickAsync(Message message, int userId) {
        this.punishmentManager.removeUser(Utils.getChatFromPeerId(message.getPeerId()), userId, true);
    }
}
