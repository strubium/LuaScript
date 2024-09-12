package com.strubium.lua_script;

import java.io.File;

public class Config {
    public static final File luaDir = new File(LuaScript.configDir, "lua");

    public static final void loadFolder(){
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


}
