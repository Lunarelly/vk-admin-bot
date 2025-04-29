package com.lunarelly.vkadmin.handler;

import api.longpoll.bots.model.objects.basic.Message;
import com.lunarelly.vkadmin.punishment.PunishmentManager;
import com.lunarelly.vkadmin.util.Utils;

public final class KickHandler {
    private final PunishmentManager punishmentManager;

    public KickHandler(PunishmentManager punishmentManager) {
        this.punishmentManager = punishmentManager;
    }

    public void handleKick(Message message) {
        this.punishmentManager.removeUser(Utils.getChatFromPeerId(message.getPeerId()), message.getAction().getMemberId(), true);
    }
}
