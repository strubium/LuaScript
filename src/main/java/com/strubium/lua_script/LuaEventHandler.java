package com.strubium.lua_script;

import com.strubium.lua_script.lua.LuaManager;
import net.minecraftforge.fml.common.event.FMLLoadCompleteEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.gameevent.TickEvent;

public class LuaEventHandler {

    @SubscribeEvent
    public void clientTick(TickEvent.ServerTickEvent event) {
        if (event.phase == TickEvent.Phase.END) {
            LuaManager.loadScript("lua/tick.lua");
        }
    }

    @SubscribeEvent
    public static void onLoadComplete(FMLLoadCompleteEvent event) {
        LuaManager.loadScript("lua/fixup.lua");
    }
}