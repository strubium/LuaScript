package com.strubium.lua_script.lua;

import org.luaj.vm2.*;
import org.luaj.vm2.lib.jse.JsePlatform;

public class LuaEngine {
    private Globals globals;

    public LuaEngine() {
        globals = JsePlatform.standardGlobals();  // Use Globals to manage Lua environment
    }

    public Globals getGlobals() {
        return globals;
    }
}

