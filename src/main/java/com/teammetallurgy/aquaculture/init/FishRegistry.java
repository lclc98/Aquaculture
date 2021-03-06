package com.teammetallurgy.aquaculture.init;

import com.google.common.collect.Lists;
import com.teammetallurgy.aquaculture.Aquaculture;
import com.teammetallurgy.aquaculture.entity.AquaFishEntity;
import com.teammetallurgy.aquaculture.entity.FishType;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityClassification;
import net.minecraft.entity.EntitySpawnPlacementRegistry;
import net.minecraft.entity.EntityType;
import net.minecraft.item.Item;
import net.minecraft.util.ResourceLocation;
import net.minecraft.world.gen.Heightmap;
import net.minecraftforge.event.RegistryEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

import javax.annotation.Nonnull;
import java.util.List;

@Mod.EventBusSubscriber(modid = Aquaculture.MOD_ID, bus = Mod.EventBusSubscriber.Bus.MOD)
public class FishRegistry {
    public static List<EntityType> fishEntities = Lists.newArrayList();

    /**
     * Same as {@link #register(Item, String, FishType)}, but with default size
     */
    public static Item register(@Nonnull Item fishItem, @Nonnull String name) {
        return register(fishItem, name, FishType.MEDIUM);
    }

    /**
     * Registers the fish item, fish entity and fish bucket
     *
     * @param fishItem The fish item to be registered
     * @param name     The fish name
     * @return The fish Item that was registered
     */
    public static Item register(@Nonnull Item fishItem, @Nonnull String name, FishType fishSize) {
        AquaItems.register(fishItem, name);
        EntityType<AquaFishEntity> fish = EntityType.Builder.create(AquaFishEntity::new, EntityClassification.WATER_CREATURE).size(fishSize.getWidth(), fishSize.getHeight()).build("minecraft:cod"); //TODO Change when Forge allow for custom datafixers
        registerEntity(name, fish);
        AquaFishEntity.SIZES.put(fish, fishSize);
        return fishItem;
    }

    public static <T extends Entity> EntityType<T> registerEntity(String name, EntityType<T> entityType) {
        ResourceLocation location = new ResourceLocation(Aquaculture.MOD_ID, name);
        entityType.setRegistryName(location);
        fishEntities.add(entityType);
        return entityType;
    }

    @SubscribeEvent
    public static void registerFish(RegistryEvent.Register<EntityType<?>> event) {
        for (EntityType entityType : fishEntities) {
            event.getRegistry().register(entityType);
            EntitySpawnPlacementRegistry.register(entityType, EntitySpawnPlacementRegistry.PlacementType.IN_WATER, Heightmap.Type.MOTION_BLOCKING_NO_LEAVES, AquaFishEntity::canSpawnHere);
        }
    }
}