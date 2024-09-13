package com.strubium.lua_script.lua;

import com.strubium.lua_script.Tags;
import com.strubium.lua_script.LuaScript;
import com.strubium.lua_script.builders.BlockBuilder;
import com.strubium.lua_script.util.FileUtils;
import net.minecraft.block.Block;
import net.minecraft.client.renderer.block.model.ModelResourceLocation;
import net.minecraft.block.material.MapColor;
import net.minecraft.block.material.Material;
import net.minecraft.block.state.IBlockState;
import net.minecraft.client.Minecraft;
import net.minecraft.enchantment.Enchantment;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.item.Item;
import net.minecraft.item.ItemBlock;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.datafix.FixTypes;
import net.minecraft.util.datafix.IDataFixer;
import net.minecraft.util.datafix.IDataWalker;
import net.minecraft.util.datafix.IFixableData;
import net.minecraftforge.client.model.ModelLoader;
import net.minecraftforge.fml.common.registry.GameRegistry;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import org.luaj.vm2.*;

import java.io.File;
import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.LocalDateTime;

import static com.strubium.lua_script.LuaScript.luaEngine;
import static com.strubium.lua_script.LuaScript.modConfigDir;

public class LuaManager {
    private final Globals globals;

    public LuaManager(Globals globals) {
        this.globals = globals;
        registerFunctions();
        registerMinecraftFunctions();
    }

    /**
     * Registers all the functions that can be used in a Lua file
     */
    private void registerFunctions() {
        globals.set("log", new LuaFunction() {
            @Override
            public LuaValue call(LuaValue arg) {
                LuaScript.LOGGER.info(arg.tojstring());
                return arg;
            }
        });

        globals.set("error", new LuaFunction() {
            @Override
            public LuaValue call(LuaValue arg) {
                LuaScript.LOGGER.error(arg.tojstring(),  Tags.MOD_NAME);
                return arg;
            }
        });

        globals.set("power", new LuaFunction() {
            @Override
            public LuaValue call(LuaValue base, LuaValue exp) {
                if (base.isnumber() && exp.isnumber()) {
                    return LuaValue.valueOf(Math.pow(base.todouble(), exp.todouble()));
                } else {
                    return LuaValue.NIL;
                }
            }
        });

        globals.set("modulus", new LuaFunction() {
            @Override
            public LuaValue call(LuaValue a, LuaValue b) {
                if (a.isnumber() && b.isnumber()) {
                    return LuaValue.valueOf(a.toint() % b.toint());
                } else {
                    return LuaValue.NIL;
                }
            }
        });

        globals.set("random", new LuaFunction() {
            @Override
            public LuaValue call(LuaValue min, LuaValue max) {
                int result = (int) (Math.random() * (max.toint() - min.toint() + 1)) + min.toint();
                return LuaValue.valueOf(result);
            }
        });

        globals.set("format", new LuaFunction() {
            @Override
            public LuaValue call(LuaValue format, LuaValue arg) {
                return LuaValue.valueOf(String.format(format.tojstring(), arg.tojstring()));
            }
        });

        globals.set("currentTimeMillis", new LuaFunction() {
            @Override
            public LuaValue call() {
                return LuaValue.valueOf(System.currentTimeMillis());
            }
        });

        globals.set("getCurrentDateTime", new LuaFunction() {
            @Override
            public LuaValue call() {
                return LuaValue.valueOf(LocalDateTime.now().toString());
            }
        });

        globals.set("sleep", new LuaFunction() {
            @Override
            public LuaValue call(LuaValue millis) {
                try {
                    Thread.sleep(millis.tolong());
                    return LuaValue.TRUE;
                } catch (InterruptedException e) {
                    e.printStackTrace();
                    return LuaValue.FALSE;
                }
            }
        });

        globals.set("fileExists", new LuaFunction() {
            @Override
            public LuaValue call(LuaValue path) {
                File file = new File(path.tojstring());
                return file.exists() ? LuaValue.TRUE : LuaValue.FALSE;
            }
        });

        globals.set("deleteFile", new LuaFunction() {
            @Override
            public LuaValue call(LuaValue path) {
                File file = new File(path.tojstring());
                return file.delete() ? LuaValue.TRUE : LuaValue.FALSE;
            }
        });

        globals.set("renameFile", new LuaFunction() {
            @Override
            public LuaValue call(LuaValue oldPath, LuaValue newPath) {
                File oldFile = new File(oldPath.tojstring());
                File newFile = new File(newPath.tojstring());
                return oldFile.renameTo(newFile) ? LuaValue.TRUE : LuaValue.FALSE;
            }
        });

        globals.set("getFileSize", new LuaFunction() {
            @Override
            public LuaValue call(LuaValue path) {
                File file = new File(path.tojstring());
                return file.exists() ? LuaValue.valueOf(file.length()) : LuaValue.NIL;
            }
        });

        globals.set("getFileExtension", new LuaFunction() {
            @Override
            public LuaValue call(LuaValue path) {
                File file = new File(path.tojstring());
                String fileName = file.getName();
                int dotIndex = fileName.lastIndexOf('.');
                return dotIndex >= 0 ? LuaValue.valueOf(fileName.substring(dotIndex + 1)) : LuaValue.NIL;
            }
        });

        globals.set("runScript", new LuaFunction() {
            @Override
            public LuaValue call(LuaValue script) {
                loadScript(String.valueOf(script));
                return LuaValue.valueOf(String.valueOf(script));
            }
        });

        globals.set("runScriptCustomDir", new LuaFunction() {
            @Override
            public LuaValue call(LuaValue script, LuaValue relativePath) {
                loadScript(String.valueOf(script), String.valueOf(relativePath));
                return LuaValue.valueOf(String.valueOf(script));
            }
        });

        globals.set("mergeFiles", new LuaFunction() {
            @Override
            public LuaValue call(LuaValue file1, LuaValue file2, LuaValue outputFile) {
                String localDir = FileUtils.getCurrentWorkingDirectory("resources/scripts");
                File fileA = new File(localDir.concat( "/" + file1.tojstring()));
                File fileB = new File(localDir.concat("/" +file2.tojstring()));
                File outFile = new File(localDir.concat("/" + outputFile.tojstring()));

                if (!fileA.exists() || !fileB.exists()) {
                    return LuaValue.error("One or both input files do not exist.");
                }

                try {
                    // Read the contents of the first and second files
                    String contentA = FileUtils.readFile(fileA.getPath());
                    String contentB = FileUtils.readFile(fileB.getPath());

                    // Merge the contents
                    String mergedContent = contentA + contentB;

                    // Write the merged content back to the output file
                    FileUtils.writeFile(outFile.getPath(), mergedContent);

                    return LuaValue.TRUE;
                } catch (IOException e) {
                    e.printStackTrace();
                    return LuaValue.FALSE;
                }
            }
        });



        // Add more functions as needed
    }
    private void registerMinecraftFunctions(){
        globals.set("getBlockAt", new LuaFunction() {
            @Override
            public LuaValue call(LuaValue x, LuaValue y, LuaValue z) {
                World world = Minecraft.getMinecraft().world;
                BlockPos pos = new BlockPos(x.toint(), y.toint(), z.toint());
                IBlockState state = world.getBlockState(pos);
                return LuaValue.valueOf(state.getBlock().getRegistryName().toString());
            }
        });

        globals.set("getPlayerPosition", new LuaFunction() {
            @Override
            public LuaValue call() {
                EntityPlayer player = Minecraft.getMinecraft().player;
                if (player != null) {
                    return LuaValue.listOf(new LuaValue[] {
                            LuaValue.valueOf(player.posX),
                            LuaValue.valueOf(player.posY),
                            LuaValue.valueOf(player.posZ)
                    });
                }
                return LuaValue.NIL;
            }
        });

        globals.set("teleportPlayer", new LuaFunction() {
            @Override
            public LuaValue call(LuaValue x, LuaValue y, LuaValue z) {
                EntityPlayer player = Minecraft.getMinecraft().player;
                if (player != null) {
                    player.setPositionAndUpdate(x.todouble(), y.todouble(), z.todouble());
                    return LuaValue.TRUE;
                }
                return LuaValue.FALSE;
            }
        });


        globals.set("createBlock", new LuaFunction() {
            @Override
            public LuaValue call(LuaValue name, LuaValue hardness, LuaValue resistance) {
                String blockName = name.tojstring();
                Block customBlock = new BlockBuilder(blockName).setHardness(hardness.tofloat()).setResistance(resistance.tofloat()).build();

                // Register the block
                GameRegistry.findRegistry(Block.class).register(customBlock);

                // Create the item form of the block
                Item blockItem = new ItemBlock(customBlock)
                        .setRegistryName(blockName);

                // Register the item form
                GameRegistry.findRegistry(Item.class).register(blockItem);

                ModelLoader.setCustomModelResourceLocation(Item.getItemFromBlock(customBlock), 0, new ModelResourceLocation(customBlock.getRegistryName(), "inventory"));

                return LuaValue.TRUE;
            }
        });

        globals.set("createItem", new LuaFunction() {
            @Override
            public LuaValue call(LuaValue name, LuaValue itemProperties) {
                String itemName = name.tojstring();
                Item customItem = new Item()
                        .setRegistryName(itemName);

                // Apply additional item properties (if any are provided in itemProperties)
                if (!itemProperties.isnil()) {
                    applyItemProperties(customItem, itemProperties);
                }

                // Register the item
                GameRegistry.findRegistry(Item.class).register(customItem);

                // Set the custom model resource location for the item
                ModelLoader.setCustomModelResourceLocation(
                        customItem,
                        0,
                        new ModelResourceLocation(customItem.getRegistryName(), "inventory")
                );

                return LuaValue.TRUE;
            }
        });

        globals.set("remapItemWithDataFixer", new LuaFunction() {
            @Override
            public LuaValue call(LuaValue oldItemName, LuaValue newItemName) {
                EntityPlayer player = Minecraft.getMinecraft().player;
                if (player != null) {
                    IDataFixer dataFixer = Minecraft.getMinecraft().getDataFixer();

                    // Create the custom Fixer for item remapping
                    IFixableData fixer = new IFixableData() {
                        @Override
                        public int getFixVersion() {
                            return 922;  // Define a version for the fixer (arbitrary version number)
                        }

                        @Override
                        public NBTTagCompound fixTagCompound(NBTTagCompound compound) {
                            // Check if the old item is in the compound (NBT data)
                            String itemId = compound.getString("id");

                            if (itemId.equals(oldItemName.tojstring())) {
                                // Change the old item ID to the new one
                                compound.setString("id", newItemName.tojstring());
                            }

                            return compound;  // Return the modified NBT data
                        }
                    };

                    // Register the fixer with the DataFixer
                    ((net.minecraft.util.datafix.DataFixer) dataFixer).registerWalker(FixTypes.ITEM_INSTANCE, (IDataWalker) fixer);

                    // Apply the data fixer to the player's inventory
                    InventoryPlayer inventory = player.inventory;
                    for (int i = 0; i < inventory.getSizeInventory(); i++) {
                        ItemStack stack = inventory.getStackInSlot(i);
                        if (stack != null && !stack.isEmpty()) {
                            // Write the current item stack to NBT
                            NBTTagCompound nbt = new NBTTagCompound();
                            stack.writeToNBT(nbt);

                            // Apply the data fixer to the NBT data
                            NBTTagCompound fixedNbt = dataFixer.process(FixTypes.ITEM_INSTANCE, nbt, 922);  // Directly use the version number

                            // If NBT data was changed, update the item stack
                            if (!fixedNbt.equals(nbt)) {
                                stack = new ItemStack(fixedNbt);  // Create a new ItemStack with the fixed NBT
                                inventory.setInventorySlotContents(i, stack);  // Update the slot with the new ItemStack
                            }
                        }
                    }

                    return LuaValue.TRUE;  // Return true if successful
                }
                return LuaValue.FALSE;
            }
        });

    }

    private void applyItemProperties(Item item, LuaValue itemProperties) {
        // Similar to the applyItemProperties method in createBlock
        LuaValue maxStackSizeValue = itemProperties.get("maxStackSize");
        if (!maxStackSizeValue.isnil()) {
            try {
                int maxStackSize = maxStackSizeValue.toint();
                item.setMaxStackSize(maxStackSize);
            } catch (Exception e) {
                System.err.println("Invalid maxStackSize value: " + maxStackSizeValue);
            }
        }

        // Add more properties as needed...
    }



    /**
     * Loads and executes a Lua script from the specified path.
     *
     * @param scriptPath the relative path to the Lua script file within the "resources/scripts/" directory
     */
    public static void loadScript(String scriptPath) {
        loadScript(scriptPath, modConfigDir);
    }

    /**
     * Loads and executes a Lua script from the specified path.
     *
     * @param scriptPath the path to the Lua script file
     * @param relativePath the relative path to the script  Ex: "resources/scripts/"
     */
    public static void loadScript(String scriptPath, String relativePath) {
        try {
            // Use Paths to properly handle file paths
            Path scriptFile = Paths.get(relativePath, scriptPath);

            Globals globals = luaEngine.getGlobals();

            // Ensure the file exists and load the content
            String scriptContent = FileUtils.readFile(String.valueOf(scriptFile));  // Ensure proper encoding
            LuaValue chunk = globals.load(scriptContent);  // Load the script from content
            chunk.call();  // Execute the script

        } catch (Exception e) {
            LuaScript.LOGGER.error("Error loading Lua script: " + e.getMessage(), e);
            e.printStackTrace();
        }
    }
}