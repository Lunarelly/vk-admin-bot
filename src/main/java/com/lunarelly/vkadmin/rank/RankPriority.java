package com.lunarelly.vkadmin.rank;

public final class RankPriority {
    public static final int DEFAULT = 1;
    public static final int MODERATOR = RankPriority.DEFAULT + 1;
    public static final int ADMIN = RankPriority.MODERATOR + 1;
    public static final int BUILDER = RankPriority.ADMIN + 1;
    public static final int MANAGER = RankPriority.BUILDER + 1;
    public static final int DEVELOPER = RankPriority.MANAGER + 1;
    public static final int OWNER = RankPriority.DEVELOPER + 1;
}
