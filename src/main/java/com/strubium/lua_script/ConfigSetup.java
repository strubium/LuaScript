package com.strubium.lua_script;

import java.io.File;
import java.io.IOException;

public class ConfigSetup {
    public static final File luaDir = new File(LuaScript.configDir, "lua");
    public static final File initFile = new File(ConfigSetup.luaDir, "init.lua");
    public static final File tickFile = new File(ConfigSetup.luaDir, "tick.lua");


    public static void loadLuaFolder(){
        // Create the lua directory if it doesn't exist
        if (!luaDir.exists()) {
            boolean dirsCreated = luaDir.mkdirs();  // mkdirs() creates the directory and any necessary parent directories
            if (dirsCreated) {
               LuaScript.LOGGER.info("Directory 'lua/' created successfully.");
            } else {
                LuaScript.LOGGER.info("Failed to create directory 'lua/'.");
            }
        }
    }

    public static void loadInit(){
        // Check if the file exists, if not, create it
        if (!initFile.exists()) {
            try {
                if (initFile.createNewFile()) {
                    LuaScript.LOGGER.info("File 'init.lua' created successfully at: " + initFile.getAbsolutePath());
                } else {
                    LuaScript.LOGGER.info("Failed to create file 'init.lua'.");
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        } else {
            LuaScript.LOGGER.info("File 'init.lua' already exists at: " + initFile.getAbsolutePath());
        }
    }

    public static void loadTick(){
        // Check if the file exists, if not, create it
        if (!tickFile.exists()) {
            try {
                if (tickFile.createNewFile()) {
                    LuaScript.LOGGER.info("File 'init.lua' created successfully at: " + tickFile.getAbsolutePath());
                } else {
                    LuaScript.LOGGER.info("Failed to create file 'init.lua'.");
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        } else {
            LuaScript.LOGGER.info("File 'init.lua' already exists at: " + tickFile.getAbsolutePath());
        }
    }

}
