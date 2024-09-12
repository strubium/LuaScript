package com.strubium.lua_script;

import com.example.lua_script.Tags;
import com.strubium.lua_script.lua.LuaEngine;
import com.strubium.lua_script.lua.LuaManager;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

@Mod(modid = Tags.MOD_ID, name = Tags.MOD_NAME, version = Tags.VERSION)
public class LuaScript {

    public static LuaEngine luaEngine;
    public static LuaManager functionManager;

    public static final Logger LOGGER = LogManager.getLogger(Tags.MOD_NAME);

    /**
     * <a href="https://cleanroommc.com/wiki/forge-mod-development/event#overview">
     *     Take a look at how many FMLStateEvents you can listen to via the @Mod.EventHandler annotation here
     * </a>
     */
    @Mod.EventHandler
    public void preInit(FMLPreInitializationEvent event) {
        luaEngine = new LuaEngine();
        functionManager = new LuaManager(luaEngine.getGlobals());

        LOGGER.info("Hello From {}!", Tags.MOD_NAME);
    }

}
