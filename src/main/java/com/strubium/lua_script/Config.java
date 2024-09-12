package com.strubium.lua_script;

import java.io.File;
import java.io.IOException;

public class Config {
    public static final File luaDir = new File(LuaScript.configDir, "lua");
    public static final File file = new File(Config.luaDir, "init.lua");

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
        if (!file.exists()) {
            try {
                if (file.createNewFile()) {
                    LuaScript.LOGGER.info("File 'init.lua' created successfully at: " + file.getAbsolutePath());
                } else {
                    LuaScript.LOGGER.info("Failed to create file 'init.lua'.");
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        } else {
            LuaScript.LOGGER.info("File 'init.lua' already exists at: " + file.getAbsolutePath());
        }
    }

}
