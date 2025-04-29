package com.lunarelly.vkadmin;

public final class Main {
    public static void main(String[] args) {
        AdminManager.getLogger().info("VK Admin bot is now running!");
        try {
            new Bot();
        } catch (Exception e) {
            AdminManager.getLogger().error(e.getMessage());
            System.exit(1);
        }
    }
}