package com.teammetallurgy.aquaculture.item.neptunium;

import com.teammetallurgy.aquaculture.Aquaculture;
import net.minecraft.block.BlockState;
import net.minecraft.entity.Entity;
import net.minecraft.item.IItemTier;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.PickaxeItem;
import net.minecraft.tags.FluidTags;
import net.minecraft.world.World;

import javax.annotation.Nonnull;

public class NeptuniumPickaxe extends PickaxeItem {
    private boolean inWater = false;

    public NeptuniumPickaxe(IItemTier tier, int attackDamage, float attackSpeed) {
        super(tier, attackDamage, attackSpeed, new Item.Properties().group(Aquaculture.GROUP));
    }

    @Override
    public void inventoryTick(@Nonnull ItemStack stack, World world, Entity entity, int i, boolean b) {
        if (this.inWater != entity.areEyesInFluid(FluidTags.WATER)) {
            this.inWater = entity.areEyesInFluid(FluidTags.WATER);
        }
    }

    @Override
    public float getDestroySpeed(@Nonnull ItemStack stack, BlockState state) {
        float defaultSpeed = super.getDestroySpeed(stack, state);
        return this.inWater ? (defaultSpeed * 5.0F) * 5.0F : defaultSpeed;
    }
}