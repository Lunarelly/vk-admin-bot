package com.lunarelly.vkadmin.data;

import lombok.Getter;

import java.util.List;

@Getter
public final class SettingsData {
    private String token;
    private String chatCommandPrefix;
    private String rconCommandSymbol;
    private String rconIp;
    private int rconPort;
    private String rconPassword;
    private List<String> allowedCommands;
}
