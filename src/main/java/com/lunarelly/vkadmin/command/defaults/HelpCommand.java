package com.lunarelly.vkadmin.command.defaults;

import api.longpoll.bots.model.objects.basic.Message;
import com.lunarelly.vkadmin.command.Command;
import com.lunarelly.vkadmin.rank.RankPriority;

import java.util.List;

public final class HelpCommand extends Command {
    public HelpCommand() {
        super("help");
    }

    @Override
    public void execute(Message message, List<String> args) {
        if (testPermission(message)) {
            sendMessageAsync(message, """
                    ⚙ Доступные команды:
                    - /chat rank <Пользователь> <Ранг> - Сменить ранг пользователя (Manager)
                    - /chat rank list - Посмотреть список рангов (Manager)
                    - /chat staff - Посмотреть список персонала (Moderator)
                    - /chat kick <Пользователь> - Кикнуть пользователя (Manager)
                    - /chat tag <Ранг> - Отметить пользователей с определённым рангом (Owner)
                    - /chat warn <Пользователь> [Количество] - Выдать определённое количество предупреждений пользователю (Admin)
                    - /chat unwarn <Пользователь> [Количество] - Забрать определённое количество предупреждений у пользователя (Admin)
                    - /chat nick <Пользователь> <Ник> - Установить ник пользователю (Manager)
                    - /chat removenick <Пользователь> - Удалить ник пользователя (Manager)"""
            );
        }
    }

    @Override
    public int getRankLevel() {
        return RankPriority.MODERATOR;
    }
}
