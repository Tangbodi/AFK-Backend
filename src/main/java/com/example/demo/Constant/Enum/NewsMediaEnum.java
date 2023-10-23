package com.example.demo.Constant.Enum;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum NewsMediaEnum {
    STARDEW_VALLEY(102,"https://www.away-from-keyboard.com/ICON/GAME/102.png"),//1
    DOTA2(123,"https://www.away-from-keyboard.com/ICON/GAME/123.png"),//1
    THE_WITCHER_3(126,"https://www.away-from-keyboard.com/ICON/GAME/126.png"),//1
    MONSTER_HUNTER(129,"https://www.away-from-keyboard.com/ICON/GAME/129.png"),//1
    BALDURS_GATE_3(138,"https://www.away-from-keyboard.com/ICON/GAME/138.png"),//1
    COUNTER_STRIKE(201,"https://www.away-from-keyboard.com/ICON/GAME/201.png"),//1
    RAINBOW_SIX(204,"https://www.away-from-keyboard.com/ICON/GAME/204.png"),//1
    PUBG(225,"https://www.away-from-keyboard.com/ICON/GAME/225.png"),//1
    GRAND_THEFT_AUTO_V(243,"https://www.away-from-keyboard.com/ICON/GAME/243.png"),//1
    CRUSADER_KINGS_3(405,"https://www.away-from-keyboard.com/ICON/GAME/405.png"),//1
    CIVILIZATION_VI(417,"https://www.away-from-keyboard.com/ICON/GAME/417.png"),//1
    STELLARIS(423,"https://www.away-from-keyboard.com/ICON/GAME/423.png"),//1
    ENDLESS_LEGEND(426,"https://www.away-from-keyboard.com/ICON/GAME/426.png"),//1
    NBA_2K(315,"https://www.away-from-keyboard.com/ICON/GAME/315.png"),//1
    EA_SPORTS_FC(324,"https://www.away-from-keyboard.com/ICON/GAME/324.png"),//1
    TITANFALL(231,"https://www.away-from-keyboard.com/ICON/GAME/231.png"),//1
    APEX_LEGENDS(234,"https://www.away-from-keyboard.com/ICON/GAME/234.png"),//1
    CALL_OF_DUTY(207,"https://www.away-from-keyboard.com/ICON/GAME/207.png"),//1
    TOTAL_WAR(402,"https://www.away-from-keyboard.com/ICON/GAME/402.png"),//1
    STARFIELD(249,"https://www.away-from-keyboard.com/ICON/GAME/249.png"),//1
    SINS_OF_A_SOLAR_EMPIRE(420,"https://www.away-from-keyboard.com/ICON/GAME/420.png"),//1
    YU_GI_OH(507,"https://www.away-from-keyboard.com/ICON/GAME/507.png"),//1
    MOUNT_AND_BLADE(603,"https://www.away-from-keyboard.com/ICON/GAME/603.png"),//1
    DARK_SOULS(609,"https://www.away-from-keyboard.com/ICON/GAME/609.png"),//1
    HOGWARTS_LEGACY(612,"https://www.away-from-keyboard.com/ICON/GAME/612.png"),//1
    PATH_OF_EXILE(618,"https://www.away-from-keyboard.com/ICON/GAME/618.png"),//1
    GOD_OF_WAR(621,"https://www.away-from-keyboard.com/ICON/GAME/621.png"),//1
    THE_ELDER_SCROLLS(624,"https://www.away-from-keyboard.com/ICON/GAME/624.png"),//1
    DAVE_THE_DIVER(630,"https://www.away-from-keyboard.com/ICON/GAME/630.png"),//1
    CYBERPUNK_2077(135,"https://www.away-from-keyboard.com/ICON/GAME/135.png"),//1
    ELDEN_RING(117,"https://www.away-from-keyboard.com/ICON/GAME/117.png"),//1
    LEAGUE_OF_LEGENDS(447,"https://www.away-from-keyboard.com/ICON/GAME/447.png"),//2
    DENTINY_2(237,"https://www.away-from-keyboard.com/ICON/GAME/237.png"),//2
    WORLD_OF_WARCRAFT(111,"https://www.away-from-keyboard.com/ICON/GAME/111.png"),//2
    FINAL_FANTASY_XVI(114,"https://www.away-from-keyboard.com/ICON/GAME/114.png"),//2
    VALORANT(210,"https://www.away-from-keyboard.com/ICON/GAME/210.png"),//2
    FORTNITE(240,"https://www.away-from-keyboard.com/ICON/GAME/240.png"),//2
    HEARTHSTONE(516,"https://www.away-from-keyboard.com/ICON/GAME/516.png"),//2
    THE_LEGEND_OF_ZELDA(600,"https://www.away-from-keyboard.com/ICON/GAME/600.png");//2

    private final Integer gameId;

    private final String MediaUrl;

    public static String GetMediaUrl(Integer gameId) {
        for (NewsMediaEnum newsMediaEnum : NewsMediaEnum.values()) {
            if (gameId.equals(newsMediaEnum.getGameId())) {
                return newsMediaEnum.getMediaUrl();
            }
        }
        return null;
    }
    public static Integer GetGameId (String MediaUrl) {
        for (NewsMediaEnum newsMediaEnum : NewsMediaEnum.values()) {
            if (MediaUrl.equals(newsMediaEnum.getMediaUrl())) {
                return newsMediaEnum.getGameId();
            }
        }
        return null;
    }
}
