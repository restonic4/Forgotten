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
        if (value instanceof BlockPos blockPos) {
            data.put(key, new Vector3Wrapper(blockPos.getX(), blockPos.getY(), blockPos.getZ()));
        } else {
            data.put(key, value);
        }
    }

    public Object get(String key) {
        return data.get(key);
    }

    public int getInt(String key) {
        Object dataObj = data.get(key);

        if (dataObj == null) {
            return 0;
        }

        if (dataObj instanceof Double d) {
            return d.intValue();
        }

        return (int) dataObj;
    }

    public boolean getBoolean(String key) {
        Object dataObj = data.get(key);

        if (dataObj == null) {
            return false;
        }

        if (dataObj instanceof Boolean b) {
            return b;
        }

        return (boolean) dataObj;
    }

    public BlockPos getBlockPos(String key) {
        Object rawData = data.get(key);

        if (rawData instanceof Vector3Wrapper vector3Wrapper) {
            return vector3Wrapper.toBlockPos();
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

    private class Vector3Wrapper {
        private float x, y, z;

        public Vector3Wrapper(float x, float y, float z) {
            this.x = x;
            this.y = y;
            this.z = z;
        }

        public void set(float x, float y, float z) {
            this.x = x;
            this.y = y;
            this.z = z;
        }

        public float x() {
            return this.x;
        }

        public float y() {
            return this.y;
        }

        public float z() {
            return this.z;
        }

        public BlockPos toBlockPos() {
            return new BlockPos((int) this.x, (int) this.y, (int) this.z);
        }
    }
}
