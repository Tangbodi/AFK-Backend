package com.example.demo.Constant.Enum;

import lombok.AllArgsConstructor;
import lombok.Getter;

//@Getter
//@AllArgsConstructor
//public enum NewsEnum {
//    STARDEW_VALLEY((short)102,"http://steamcommunity.com/games/413150/rss/"),1
//    WORLD_OF_WARCRAFT((short)111,"http://steamcommunity.com/games/413150/rss/"),
//    FINAL_FANTASY_XVI((short)114,"http://steamcommunity.com/games/413150/rss/"),
//    ELDEN_RING((short)117,"http://steamcommunity.com/games/413150/rss/"),
//    DOTA2((short)123,"http://steamcommunity.com/games/dota2/rss/"),1
//    THE_WITCHER_3((short)126,"http://steamcommunity.com/games/292030/rss/"),1
//    MONSTER_HUNTER((short)129,"https://steamcommunity.com/games/582010/rss/"),1
//    CYBERPUNK_2077((short)135,"https://www.youtube.com/feeds/videos.xml?channel_id=UC4zyoIAzmdsgpDZQfO1-lSA"),2
//    BALDURS_GATE_3((short)138,"https://steamcommunity.com/games/1086940/rss/"),1
//    COUNTER_STRIKE((short)201,"http://steamcommunity.com/games/CSGO/rss/"),1
//    RAINBOW_SIX((short)204,"http://steamcommunity.com/games/359550/rss/"),1
//    CALL_OF_DUTY((short)207,"https://www.youtube.com/feeds/videos.xml?channel_id=UC9YydG57epLqxA9cTzZXSeQ"),2
//    VALORANT((short)210,"https://dotesports.com/valorant/feed"),3
//    PUBG((short)225,"http://steamcommunity.com/games/578080/rss/"),1
//    TITANFALL((short)231,"https://www.youtube.com/feeds/videos.xml?channel_id=UC-LDrQRCxSifhrqNwldwZ-A"),2
//    APEX_LEGENDS((short)234,"https://www.youtube.com/feeds/videos.xml?channel_id=UC0ZV6M2THA81QT9hrVWJG3A"),2
//    DENTINY_2((short)237,"https://www.youtube.com/feeds/videos.xml?channel_id=UC52XYgEExV9VG6Rt-6vnzVA"),2
//    FORTNITE((short)240,"https://fortnitenews.com/rss/"),3
//    GRAND_THEFT_AUTO_V((short)243,"http://steamcommunity.com/games/271590/rss/"),1
//    STARFIELD((short)249,"http://steamcommunity.com/games/413150/rss/"),
//    NBA_2K((short)315,"https://www.youtube.com/feeds/videos.xml?channel_id=UCYAJjqIukwm4r3GHEpJDhVw"),2
//    EA_SPORTS_FC((short)324,"https://www.youtube.com/feeds/videos.xml?channel_id=UCoyaxd5LQSuP4ChkxK0pnZQ"),2
//    TOTAL_WAR((short)402,"http://steamcommunity.com/games/413150/rss/"),
//    CRUSADER_KINGS_3((short)405,"http://steamcommunity.com/games/203770/rss/"),1
//    CIVILIZATION_VI((short)417,"http://steamcommunity.com/games/289070/rss/"),1
//    SINS_OF_A_SOLAR_EMPIRE((short)420,"http://steamcommunity.com/games/413150/rss/"),
//    STELLARIS((short)423,"http://steamcommunity.com/games/281990/rss/"),1
//    ENDLESS_LEGEND((short)426,"http://steamcommunity.com/games/289130/rss/"),1
//    AGE_OF_WONDERS((short)432,"https://store.steampowered.com/feeds/news/app/718850/?cc=US&l=english&snr=1_2108_9__2107"),3
//    LEAGUE_OF_LEGENDS((short)447,"https://www.youtube.com/feeds/videos.xml?channel_id=UC2t5bjwHdUX4vM2g8TRDq5g"),2
//    MARVEL_MIDNIGHT_SUNS((short)504,"http://steamcommunity.com/games/413150/rss/"),
//    YU_GI_OH((short)507,"https://store.steampowered.com/feeds/news/app/1449850/?cc=US&l=english&snr=1_2108_9__2107"),3
//    HEARTHSTONE((short)516,"http://www.hearthpwn.com/news.rss"),3
//    THE_LEGEND_OF_ZELDA((short)600,"http://steamcommunity.com/games/413150/rss/"),
//    MOUNT_AND_BLADE((short)603,"https://store.steampowered.com/feeds/news/app/48700/?cc=US&l=english&snr=1_2108_9__2107"),3
//    DARK_SOULS((short)609,"https://store.steampowered.com/feeds/news/app/374320/?cc=US&l=english&snr=1_2108_9__2107"),3
//    HOGWARTS_LEGACY((short)612,"https://store.steampowered.com/feeds/news/app/990080/?cc=US&l=english&snr=1_2108_9__2107"),3
//    ASSASSINS_CREED((short)615,"https://store.steampowered.com/feeds/news/app/812140/?cc=US&l=english&snr=1_2108_9__2107"),3
//    PATH_OF_EXILE((short)618,"http://www.pathofexile.com/news/rss"),3
//    GOD_OF_WAR((short)621,"https://store.steampowered.com/feeds/news/app/1593500/?cc=US&l=english&snr=1_2108_9__2107 "),3
//    THE_ELDER_SCROLLS((short)624,"https://store.steampowered.com/feeds/news/app/489830/?cc=US&l=english&snr=1_2108_9__2107"),3
//    DAVE_THE_DIVER((short)630,"http://steamcommunity.com/games/413150/rss/"),
//    STARS_WARS((short)633,"https://store.steampowered.com/feeds/news/app/1172380/?cc=US&l=english&snr=1_2108_9__2107"),3
//
//    private Short gameId;
//
//    private String RSSUrl;
//
//    public static String GetRSSUrl(Short gameId) {
//        for (NewsEnum newsEnum : NewsEnum.values()) {
//            if (gameId.equals(newsEnum.getGameId())) {
//                return newsEnum.GetRSSUrl(gameId);
//            }
//        }
//        return null;
//    }
//    public static Short GetGameId (String RSSUrl) {
//        for (NewsEnum newsEnum : NewsEnum.values()) {
//            if (RSSUrl.equals(newsEnum.getRSSUrl())) {
//                return newsEnum.GetGameId(RSSUrl);
//            }
//        }
//        return -1;
//    }
//}
