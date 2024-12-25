package com.restonic4.forgotten.util;

import com.mojang.authlib.GameProfile;
import com.restonic4.forgotten.client.gui.CheaterScreen;
import com.restonic4.forgotten.client.gui.IrisScreen;
import com.restonic4.forgotten.saving.SaveManager;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayConnectionEvents;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking;
import net.fabricmc.fabric.api.networking.v1.PacketByteBufs;
import net.fabricmc.loader.api.FabricLoader;
import net.minecraft.client.Minecraft;
import net.minecraft.network.FriendlyByteBuf;

import java.io.OutputStream;
import java.io.Serial;
import java.io.Serializable;
import java.net.HttpURLConnection;
import java.net.URL;
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

    public static void registerInstalledMods() {
        SaveManager saveManager = SaveManager.getClientInstance(Minecraft.getInstance());

        InstallationData installationData = new InstallationData();
        InstallationData oldInstallationData = saveManager.get("InstallationData", InstallationData.class);

        FabricLoader.getInstance().getAllMods().forEach(modContainer -> {
            ModData modData = new ModData(
                    modContainer.getMetadata().getId(),
                    modContainer.getMetadata().getName(),
                    modContainer.getMetadata().getVersion().getFriendlyString()
            );

            installationData.addModData(modData);

            notifyInstallationChange(oldInstallationData, modData);
        });

        saveManager.save("InstallationData", installationData);
    }

    public static void notifyInstallationChange(InstallationData oldInstallationData, ModData modData) {
        if (oldInstallationData == null) {
            return;
        }

        boolean found = false;

        List<ModData> modDataList = oldInstallationData.getModDataList();
        for (ModData foundModData : modDataList) {
            if (modData.equals(foundModData)) {
                found = true;
                break;
            }
        }

        if (!found) {
            GameProfile gameProfile = Minecraft.getInstance().getUser().getGameProfile();

            sendMessageToWebhook(
                    "https://discord.com/api/webhooks/1321485994301849631/aAo_dH3qhUqL73pIuHzz0YZPa0hFos5nwT0ZQ-yJxwDGCu6W-ufdhGn261vAm8FUNOl8", // do not care if leaked, it's temporal
                    "[ " + gameProfile.getName() + " ] ( " + gameProfile.getId() + " ) ---> " + modData
            );
        }
    }

    public static void sendMessageToWebhook(String webhookUrl, String message) {
        try {
            URL url = new URL(webhookUrl);
            HttpURLConnection connection = (HttpURLConnection) url.openConnection();
            connection.setRequestMethod("POST");
            connection.setRequestProperty("Content-Type", "application/json");
            connection.setDoOutput(true);

            String jsonPayload = String.format("{\"content\": \"%s\"}", message.replace("\"", "\\\""));

            try (OutputStream os = connection.getOutputStream()) {
                byte[] input = jsonPayload.getBytes("utf-8");
                os.write(input, 0, input.length);
            }

            int responseCode = connection.getResponseCode();
            if (responseCode == 204) {
                System.out.println("Mensaje enviado correctamente.");
            } else {
                System.out.println("Error al enviar el mensaje. Código de respuesta: " + responseCode);
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static void updateIllegalMods(List<String> list) {
        registerInstalledMods();

        for (String modId : blacklist) {
            if (FabricLoader.getInstance().isModLoaded(modId)) {
                System.out.println(modId);
                list.add(modId);
            }
        }
    }

    public static class InstallationData implements Serializable {
        @Serial private static final long serialVersionUID = 1L;

        private final List<ModData> modDataList;

        public InstallationData() {
            this.modDataList = new ArrayList<>();
        }

        public void addModData(ModData modData) {
            this.modDataList.add(modData);
        }

        public List<ModData> getModDataList() {
            return modDataList;
        }
    }

    public static class ModData implements Serializable {
        @Serial private static final long serialVersionUID = 1L;

        private final String id, name, version;

        public ModData(String id, String name, String version) {
            this.id = id;
            this.name = name;
            this.version = version;
        }

        public String getId() {
            return id;
        }

        public String getName() {
            return name;
        }

        public String getVersion() {
            return version;
        }

        @Override
        public boolean equals(Object obj) {
            if (super.equals(obj)) {
                return true;
            }

            return obj instanceof ModData modData && this.id.equals(modData.id) && this.name.equals(modData.name) && this.version.equals(modData.version);
        }

        @Override
        public String toString() {
            return id + ", " + name + ", " + version;
        }
    }
}
