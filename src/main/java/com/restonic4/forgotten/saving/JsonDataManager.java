package com.restonic4.forgotten.saving;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.internal.LinkedTreeMap;
import com.google.gson.reflect.TypeToken;
import net.minecraft.core.BlockPos;
import net.minecraft.server.MinecraftServer;
import net.minecraft.world.level.storage.LevelResource;

import java.io.*;
import java.lang.reflect.Type;
import java.nio.file.Path;
import java.util.HashMap;
import java.util.Map;

public class JsonDataManager {
    private static final Gson GSON = new GsonBuilder().setPrettyPrinting().create();
    private static final String FILE_NAME = "forgotten.json";

    private final Map<String, Object> data = new HashMap<>();

    public void saveToDisk(MinecraftServer server) {
        Path savePath = server.getWorldPath(LevelResource.ROOT).resolve(FILE_NAME);

        try (Writer writer = new FileWriter(savePath.toFile())) {
            GSON.toJson(data, writer);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void loadFromDisk(MinecraftServer server) {
        Path savePath = server.getWorldPath(LevelResource.ROOT).resolve(FILE_NAME);

        if (!savePath.toFile().exists()) {
            return;
        }

        try (Reader reader = new FileReader(savePath.toFile())) {
            Type type = new TypeToken<Map<String, Object>>() {}.getType();
            Map<String, Object> loadedData = GSON.fromJson(reader, type);

            if (loadedData != null) {
                data.clear();
                data.putAll(loadedData);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void save(String key, Object value) {
        data.put(key, value);
    }

    public Object get(String key) {
        return data.get(key);
    }

    public BlockPos getBlockPos(String key) {
        Object rawData = data.get(key);

        if (rawData instanceof BlockPos blockPos) {
            return blockPos;
        } else {
            LinkedTreeMap<String, Double> foundData = (LinkedTreeMap<String, Double>) data.get(key);
            return new BlockPos(foundData.get("x").intValue(), foundData.get("y").intValue(), foundData.get("z").intValue());
        }
    }

    public boolean contains(String key) {
        return data.containsKey(key);
    }

    public void delete(String key) {
        data.remove(key);
    }
}
