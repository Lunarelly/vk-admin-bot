package com.lunarelly.vkadmin.data;

import lombok.Getter;

import java.util.List;

@Getter
public final class AvailableCommandsData {
    private List<String> defaultRank;
    private List<String> moderatorRank;
    private List<String> adminRank;
    private List<String> builderRank;
    private List<String> managerRank;
    private List<String> developerRank;
    private List<String> ownerRank;
}
