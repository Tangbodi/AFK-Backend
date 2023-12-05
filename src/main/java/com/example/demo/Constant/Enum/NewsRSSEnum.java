package com.example.demo.Constant.Enum;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum NewsRSSEnum {
    STARDEW_VALLEY(102,"http://steamcommunity.com/games/413150/rss/"),//1
    DOTA2(123,"http://steamcommunity.com/games/dota2/rss/"),//1
    THE_WITCHER_3(126,"http://steamcommunity.com/games/292030/rss/"),//1
    MONSTER_HUNTER(129,"https://steamcommunity.com/games/582010/rss/"),//1
    BALDURS_GATE_3(138,"https://steamcommunity.com/games/1086940/rss/"),//1
    COUNTER_STRIKE(201,"http://steamcommunity.com/games/CSGO/rss/"),//1
    RAINBOW_SIX(204,"http://steamcommunity.com/games/359550/rss/"),//1
    PUBG(225,"http://steamcommunity.com/games/578080/rss/"),//1
    GRAND_THEFT_AUTO_V(243,"http://steamcommunity.com/games/271590/rss/"),//1
    CRUSADER_KINGS_3(405,"http://steamcommunity.com/games/203770/rss/"),//1
    CIVILIZATION_VI(417,"http://steamcommunity.com/games/289070/rss/"),//1
    STELLARIS(423,"http://steamcommunity.com/games/281990/rss/"),//1
    ENDLESS_LEGEND(426,"http://steamcommunity.com/games/289130/rss/"),//1
    NBA_2K(315,"https://store.steampowered.com/feeds/news/app/2338770/?cc=US&l=english&snr=1_2108_9__2107"),//1
    EA_SPORTS_FC(324,"https://store.steampowered.com/feeds/news/app/2195250/?cc=US&l=english&snr=1_2108_9__2107"),//1
    TITANFALL(231,"https://store.steampowered.com/feeds/news/app/1237970/?cc=US&l=english&snr=1_2108_9__2107"),//1
    APEX_LEGENDS(234,"https://store.steampowered.com/feeds/news/app/1172470/?cc=US&l=english&snr=1_2108_9__2107"),//1
    CALL_OF_DUTY(207,"https://store.steampowered.com/feeds/news/app/1938090/?cc=US&l=english&snr=1_2108_9__2107"),//1
    TOTAL_WAR(402,"https://store.steampowered.com/feeds/news/app/1142710/?cc=US&l=english&snr=1_2108_9__2107"),//1
    STARFIELD(249,"https://store.steampowered.com/feeds/news/app/1716740/?cc=US&l=english&snr=1_2108_4__2107"),//1
    SINS_OF_A_SOLAR_EMPIRE(420,"https://store.steampowered.com/feeds/news/app/204880/?cc=US&l=english&snr=1_2108_4__2107"),//1
    YU_GI_OH(507,"https://store.steampowered.com/feeds/news/app/1449850/?cc=US&l=english&snr=1_2108_4__2107"),//1
    MOUNT_AND_BLADE(603,"https://store.steampowered.com/feeds/news/app/261550/?cc=US&l=english&snr=1_2108_4__2107"),//1
    DARK_SOULS(609,"https://store.steampowered.com/feeds/news/app/374320/?cc=US&l=english&snr=1_2108_9__2107"),//1
    HOGWARTS_LEGACY(612,"https://store.steampowered.com/feeds/news/app/990080/?cc=US&l=english&snr=1_2108_4__2107"),//1
    PATH_OF_EXILE(618,"https://store.steampowered.com/feeds/news/app/238960/?cc=US&l=english&snr=1_2108_9__2107"),//1
    GOD_OF_WAR(621,"https://store.steampowered.com/feeds/news/app/1593500/?cc=US&l=english&snr=1_2108_9__2107"),//1
    THE_ELDER_SCROLLS(624,"https://store.steampowered.com/feeds/news/app/306130/?cc=US&l=english&snr=1_2108_9__2107"),//1
    DAVE_THE_DIVER(630,"https://store.steampowered.com/feeds/news/app/1868140/?cc=US&l=english&snr=1_2108_9__2107"),//1
    CYBERPUNK_2077(135,"https://store.steampowered.com/feeds/news/app/1091500/?cc=US&l=english&snr=1_2108_9__2107"),//1
    ELDEN_RING(117,"https://store.steampowered.com/feeds/news/app/1245620/?cc=US&l=english&snr=1_2108_9__2107"),//1
    LEAGUE_OF_LEGENDS(447,"https://gamerant.com/feed/league-of-legends-news/"),//2
    DENTINY_2(237,"https://gamerant.com/feed/tag/destiny-2/"),//2
    WORLD_OF_WARCRAFT(111,"https://gamerant.com/feed/tag/world-of-warcraft/"),//2

    FINAL_FANTASY_XVI(114,"https://gamerant.com/feed/tag/final-fantasy-16/"),//2
    VALORANT(210,"https://gamerant.com/feed/tag/valorant/"),//2
    FORTNITE(240,"https://gamerant.com/feed/fortnite-news/"),//2
    HEARTHSTONE(516,"https://gamerant.com/feed/tag/hearthstone/"),//2
    THE_LEGEND_OF_ZELDA(600,"https://gamerant.com/feed/tag/the-legend-of-zelda/");//2

    private final Integer gameId;

    private final String RSSUrl;

    public static String GetRSSUrl(Integer gameId) {
        for (NewsRSSEnum newsRSSEnum : NewsRSSEnum.values()) {
            if (gameId.equals(newsRSSEnum.getGameId())) {
                return newsRSSEnum.getRSSUrl();
            }
        }
        return null;
    }
    public static Integer GetGameId (String RSSUrl) {
        for (NewsRSSEnum newsRSSEnum : NewsRSSEnum.values()) {
            if (RSSUrl.equals(newsRSSEnum.getRSSUrl())) {
                return newsRSSEnum.getGameId();
            }
        }
        return null;
    }
}
