package com.restonic4.forgotten.util;

import com.restonic4.forgotten.client.gui.CheaterScreen;
import com.restonic4.forgotten.client.gui.IrisScreen;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayConnectionEvents;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking;
import net.fabricmc.fabric.api.networking.v1.PacketByteBufs;
import net.fabricmc.loader.api.FabricLoader;
import net.minecraft.client.Minecraft;
import net.minecraft.network.FriendlyByteBuf;

import java.util.ArrayList;
import java.util.List;

public class ModCheck {

    /*

    Yeah yeah, ik, ik, I am blacklisting some mods because people tend to cheat a lot, and I am making it a little bit harder for them, at least on my SMP, everyone cheats
    with minimaps, health cheats, chests detectors and other bullshit like that.

     */

    static String[] blacklist = {
            //Minimaps
            "xaerominimap",
            "journeymap",
            "voxelmap",
            "mapwriter",
            "zansminimap",
            "reisminimap",
            "betterpvp",
            "antiqueatlas",
            "yamm",

            //Life and blocks and entity data
            "jade",
            "waila",
            "hwylawaila",
            "theoneprobe",
            "neat",
            "ingameinfoxml",
            "damageindicators",
            "f3health",
            "f3c",
            "f3",
            "f3commands",
            "clean-debug",
            "mr_no_f3",
            "simple_f3",
            "rebindable-f3",
            "mr_no_f3debugscreen",

            //Others
            "xray",
            "advancedxray",
            "advanced-xray-fabric",
            "chestesp",
            "cheatutils",
            "baritone",
            "impact",
            "meteor-client",
            "liquidbounce",
            "cheatbreaker",
            "playerradar",
            "kamiblue",
            "wurst",
            "minihud",
            "player-finder",
            "litematica",
            "tweakeroo",
            "truesight",
            "bcs",
            "mr_hotbarcoordinates",
            "coordinatesdisplay",
            "nether_coordinates",
            "overworld_coordinates",
            "coordinates",
            "quickcoordscopy",
            "faction-coords",
            "deathcoords",
            "mapcoordinates",
            "ams-easy-coordinates",
            "mr_info_tools",
            "easynavigator",
            "mcg",
            "coordinatebar",
            "guicompass",
            "deathfinder",
            "ccoords",
            "directionhud",
            "claimpoints",
            "deathlocation",
            "mr_easycoords",
            "coordbook",
            "coords",
            "netherlink",
            "customcoords",
            "portallinkingcompass",
            "mystuff",
            "geotagged_screenshots",
            "coords_copy",
            "ezcoords",
            "dmc",
            "coordkeeper",
            "xykey",
            "quick-print-coords",
            "yach",
            "where_is_the_portal",
            "miner_overview",
            "coordinate-chatter",
            "aligner",
            "coordfinder",
            "xyzbook",
            "position-bar",
            "triangulationmod",
            "fire",
            "simplepositions",
            "advancedhud",
            "coordinatelist",
            "slash-portal",
            "simple-hud",
            "umollu_ash",
            "simple-coordinates",
            "coords-hud-common",
            "coords-hud",
            "explorerssuite",
            "coordmanager",
            "nether-coords",
            "simplecalculator",
            "nether-portal-coords",
            "portal-helper",
            "bbor"
    };

    private static final List<String> foundMods = new ArrayList<>();

    public static void check() {
        System.out.println("//////////// CHECKING MODS START ////////////");

        ClientPlayConnectionEvents.JOIN.register((clientPacketListener, packetSender, minecraft) -> {
            updateIllegalMods(foundMods);
        });

        updateIllegalMods(foundMods);

        ClientTickEvents.END_CLIENT_TICK.register(client -> {
            if (!foundMods.isEmpty() && (Minecraft.getInstance().screen == null || !(Minecraft.getInstance().screen instanceof CheaterScreen))) {
                String blacklistedModsString = String.join(", ", foundMods);
                Minecraft.getInstance().forceSetScreen(new CheaterScreen(blacklistedModsString));
            }
        });

        System.out.println("//////////// CHECKING MODS END ////////////");
    }

    public static void updateIllegalMods(List<String> list) {
        for (String modId : blacklist) {
            if (FabricLoader.getInstance().isModLoaded(modId)) {
                System.out.println(modId);
                list.add(modId);
            }
        }
    }
}
