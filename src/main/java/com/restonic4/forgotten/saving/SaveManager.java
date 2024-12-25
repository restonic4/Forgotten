package com.restonic4.forgotten.saving;

import net.fabricmc.loader.api.FabricLoader;
import net.minecraft.client.Minecraft;
import net.minecraft.core.BlockPos;
import net.minecraft.server.MinecraftServer;
import net.minecraft.world.level.storage.LevelResource;

import java.io.*;
import java.nio.file.Path;
import java.util.HashMap;
import java.util.Map;

public class SaveManager {
    private static SaveManager instance;
    private static SaveManager clientInstance;

    private final Map<String, Object> dataStore = new HashMap<>();

    private final String saveFilePath;

    public SaveManager(String saveFilePath) {
        this.saveFilePath = saveFilePath;
    }

    public static SaveManager getInstance(MinecraftServer server) {
        if (SaveManager.instance == null) {
            SaveManager.instance = new SaveManager(server.getWorldPath(LevelResource.ROOT).resolve("forgotten.dat").toString());
        }
        return SaveManager.instance;
    }

    public static SaveManager getClientInstance(Minecraft client) {
        if (SaveManager.clientInstance == null) {
            Path clientRootPath = FabricLoader.getInstance().getGameDir();
            clientInstance = new SaveManager(clientRootPath.resolve("forgotten.dat").toString());
        }
        return SaveManager.clientInstance;
    }

    public void saveToFile() {
        try {
            try (ObjectOutputStream oos = new ObjectOutputStream(new FileOutputStream(saveFilePath))) {
                oos.writeObject(dataStore);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @SuppressWarnings("unchecked")
    public void loadFromFile() {
        try {
            File file = new File(saveFilePath);
            if (!file.exists()) {
                System.out.println("Save file not found.");
                dataStore.clear();
                return;
            }
            try (ObjectInputStream ois = new ObjectInputStream(new FileInputStream(saveFilePath))) {
                Map<String, Object> loadedData = (Map<String, Object>) ois.readObject();
                dataStore.clear();
                dataStore.putAll(loadedData);
            }
        } catch (Exception e) {
            e.printStackTrace();
            System.out.println("Save file not found.");
            dataStore.clear();
        }
    }

    public boolean containsKey(String key) {
        return dataStore.containsKey(key);
    }

    public <T> void save(String key, T value) {
        if (value instanceof BlockPos blockPos) {
            dataStore.put(key, new SerializableBlockPos(blockPos));
        } else {
            dataStore.put(key, value);
        }
    }

    @SuppressWarnings("unchecked")
    public <T> T get(String key, Class<T> type) {
        Object value = dataStore.get(key);

        if (value instanceof SerializableBlockPos serializableBlockPos) {
            return (T) serializableBlockPos.toBlockPos();
        }

        if (type.isInstance(value)) {
            return (T) value;
        }

        return null;
        //throw new IllegalArgumentException(key + " does not exist.");
    }

    public void delete(String key) {
        dataStore.remove(key);
    }
}
