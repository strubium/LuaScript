package com.strubium.lua_script;

import com.strubium.lua_script.Tags;
import com.strubium.lua_script.lua.LuaEngine;
import com.strubium.lua_script.lua.LuaManager;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.event.FMLPostInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.File;
import java.io.IOException;

@Mod(modid = Tags.MOD_ID, name = Tags.MOD_NAME, version = Tags.VERSION)
public class LuaScript {

    public static LuaEngine luaEngine;
    public static LuaManager functionManager;
    public static String modConfigDir;

    public static final Logger LOGGER = LogManager.getLogger(Tags.MOD_NAME);

    /**
     * <a href="https://cleanroommc.com/wiki/forge-mod-development/event#overview">
     *     Take a look at how many FMLStateEvents you can listen to via the @Mod.EventHandler annotation here
     * </a>
     */
    @Mod.EventHandler
    public void preInit(FMLPreInitializationEvent event) {
        LOGGER.info("Hello From {}!", Tags.MOD_NAME);

        // Get the mod configuration directory
        File configDir = event.getModConfigurationDirectory();
        modConfigDir = String.valueOf(event.getModConfigurationDirectory());

        // Define the folder and file path
        File luaDir = new File(configDir, "lua");
        File file = new File(luaDir, "init.lua");

        // Create the lua directory if it doesn't exist
        if (!luaDir.exists()) {
            boolean dirsCreated = luaDir.mkdirs();  // mkdirs() creates the directory and any necessary parent directories
            if (dirsCreated) {
                LOGGER.info("Directory 'lua/' created successfully.");
            } else {
                LOGGER.info("Failed to create directory 'lua/'.");
            }
        }

        // Check if the file exists, if not, create it
        if (!file.exists()) {
            try {
                if (file.createNewFile()) {
                    LOGGER.info("File 'init.lua' created successfully at: " + file.getAbsolutePath());
                } else {
                    LOGGER.info("Failed to create file 'init.lua'.");
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        } else {
            LOGGER.info("File 'init.lua' already exists at: " + file.getAbsolutePath());
        }
    }

    @Mod.EventHandler
    public void init(FMLPostInitializationEvent event) {
        luaEngine = new LuaEngine();
        functionManager = new LuaManager(luaEngine.getGlobals());


        LuaManager.loadScript("lua/init.lua");
    }
}
