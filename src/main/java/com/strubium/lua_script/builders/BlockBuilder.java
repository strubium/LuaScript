package com.strubium.lua_script.builders;

import net.minecraft.block.Block;
import net.minecraft.block.material.Material;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.Entity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

public class BlockBuilder {
    private String name;
    private Material material;
    private float hardness;
    private float resistance;

    public BlockBuilder(String name) {
        this.name = name;
        this.material = Material.ROCK;
        this.hardness = 1.0F;
        this.resistance = 1.0F;
    }

    public BlockBuilder setMaterial(Material material) {
        this.material = material;
        return this;
    }

    public BlockBuilder setHardness(float hardness) {
        this.hardness = hardness;
        return this;
    }

    public BlockBuilder setResistance(float resistance) {
        this.resistance = resistance;
        return this;
    }

    public Block build() {
        return new Block(material) {
            @Override
            public float getExplosionResistance(Entity exploder) {
                return resistance;
            }
        }.setRegistryName(name).setHardness(hardness);
    }
}

