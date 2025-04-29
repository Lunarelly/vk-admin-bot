package com.lunarelly.vkadmin.rank;

import com.lunarelly.vkadmin.AdminManager;
import lombok.Getter;
import org.jetbrains.annotations.Nullable;

import java.util.List;

@Getter
public enum Rank {
    DEFAULT(RankPriority.DEFAULT, "Default", AdminManager.getAvailableCommands().getDefaultRank()),
    MODERATOR(RankPriority.MODERATOR, "Moderator", AdminManager.getAvailableCommands().getModeratorRank()),
    ADMIN(RankPriority.ADMIN, "Admin", AdminManager.getAvailableCommands().getAdminRank()),
    BUILDER(RankPriority.BUILDER, "Builder", AdminManager.getAvailableCommands().getBuilderRank()),
    MANAGER(RankPriority.MANAGER, "Manager", AdminManager.getAvailableCommands().getManagerRank()),
    DEVELOPER(RankPriority.DEVELOPER, "Developer", AdminManager.getAvailableCommands().getDeveloperRank()),
    OWNER(RankPriority.OWNER, "Owner", AdminManager.getAvailableCommands().getOwnerRank());

    private final int priority;
    private final String displayName;
    private final List<String> availableCommands;

    Rank(int priority, String displayName, List<String> availableCommands) {
        this.priority = priority;
        this.displayName = displayName;
        this.availableCommands = availableCommands;
    }

    public static Rank from(String value) {
        return switch (value.toLowerCase()) {
            case "moderator" -> Rank.MODERATOR;
            case "admin" -> Rank.ADMIN;
            case "builder" -> Rank.BUILDER;
            case "manager" -> Rank.MANAGER;
            case "developer" -> Rank.DEVELOPER;
            case "owner" -> Rank.OWNER;
            default -> Rank.DEFAULT;
        };
    }

    @Nullable
    public static Rank tryFrom(String value) {
        return switch (value.toLowerCase()) {
            case "default" -> Rank.DEFAULT;
            case "moderator" -> Rank.MODERATOR;
            case "admin" -> Rank.ADMIN;
            case "builder" -> Rank.BUILDER;
            case "manager" -> Rank.MANAGER;
            case "developer" -> Rank.DEVELOPER;
            case "owner" -> Rank.OWNER;
            default -> null;
        };
    }
}
