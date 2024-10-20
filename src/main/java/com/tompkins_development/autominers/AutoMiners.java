package com.tompkins_development.autominers;

import com.tompkins_development.autominers.block.ModBlocks;
import com.tompkins_development.autominers.block.entity.ModBlockEntities;
import com.tompkins_development.autominers.gui.menu.ModMenuTypes;
import com.tompkins_development.autominers.gui.screen.AutoMinerScreen;
import com.tompkins_development.autominers.item.ModItems;
import com.tompkins_development.autominers.networking.handlers.ClientPayloadHandler;
import com.tompkins_development.autominers.networking.payloads.BlockBreakPayload;
import net.minecraft.world.item.CreativeModeTabs;
import net.neoforged.neoforge.client.event.RegisterMenuScreensEvent;
import net.neoforged.neoforge.network.event.RegisterPayloadHandlersEvent;
import net.neoforged.neoforge.network.registration.HandlerThread;
import net.neoforged.neoforge.network.registration.PayloadRegistrar;
import org.slf4j.Logger;

import com.mojang.logging.LogUtils;

import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.ModContainer;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.fml.common.Mod;
import net.neoforged.fml.config.ModConfig;
import net.neoforged.fml.event.lifecycle.FMLClientSetupEvent;
import net.neoforged.fml.event.lifecycle.FMLCommonSetupEvent;
import net.neoforged.neoforge.common.NeoForge;
import net.neoforged.neoforge.event.BuildCreativeModeTabContentsEvent;
import net.neoforged.neoforge.event.server.ServerStartingEvent;

// The value here should match an entry in the META-INF/neoforge.mods.toml file
@Mod(AutoMiners.MOD_ID)
public class AutoMiners
{
    public static final String MOD_ID = "autominers";

    private static final Logger LOGGER = LogUtils.getLogger();

    public AutoMiners(IEventBus modEventBus, ModContainer modContainer)
    {
        modEventBus.addListener(this::commonSetup);

        NeoForge.EVENT_BUS.register(this);

        ModItems.register(modEventBus);
        ModBlocks.register(modEventBus);
        ModBlockEntities.register(modEventBus);
        ModMenuTypes.register(modEventBus);


        modEventBus.addListener(this::addCreative);

        modContainer.registerConfig(ModConfig.Type.COMMON, Config.SPEC);
    }

    private void commonSetup(final FMLCommonSetupEvent event)
    {
    }

    // Add the example block item to the building blocks tab
    private void addCreative(BuildCreativeModeTabContentsEvent event)
    {
        if(event.getTabKey() == CreativeModeTabs.TOOLS_AND_UTILITIES) {
            event.accept(ModItems.STONE_DRILL);
            event.accept(ModItems.BLANK_UPGRADE_CARD);
            event.accept(ModItems.FORTUNE_UPGRADE_CARD);
            event.accept(ModItems.REVEALING_UPGRADE_CARD);
            event.accept(ModItems.SPEED_UPGRADE_CARD);
        }
        if(event.getTabKey() == CreativeModeTabs.INGREDIENTS) {
            event.accept(ModItems.FERRULITE_INGOT);
            event.accept(ModItems.FERRULITE_NUGGET);
            event.accept(ModItems.RAW_FERRULITE);
        }
        if(event.getTabKey() == CreativeModeTabs.NATURAL_BLOCKS) {
            event.accept(ModBlocks.FERRULITE_ORE);
        }
        if(event.getTabKey() == CreativeModeTabs.FUNCTIONAL_BLOCKS) {
            event.accept(ModBlocks.AUTO_MINER);
        }
    }

    // You can use SubscribeEvent and let the Event Bus discover methods to call
    @SubscribeEvent
    public void onServerStarting(ServerStartingEvent event)
    {

    }

    @EventBusSubscriber(modid = MOD_ID, bus = EventBusSubscriber.Bus.MOD)
    public static class ModEvents {
        @SubscribeEvent
        public static void register(final RegisterPayloadHandlersEvent event) {
            PayloadRegistrar registrar = event.registrar("1");

            registrar = registrar.executesOn(HandlerThread.NETWORK);

            registrar.playToClient(
                    BlockBreakPayload.TYPE,
                    BlockBreakPayload.STREAM_CODEC,
                    ClientPayloadHandler::handleBlockBreak
            );

        }
    }


    // You can use EventBusSubscriber to automatically register all static methods in the class annotated with @SubscribeEvent
    @EventBusSubscriber(modid = MOD_ID, bus = EventBusSubscriber.Bus.MOD, value = Dist.CLIENT)
    public static class ClientModEvents
    {
        @SubscribeEvent
        public static void onClientSetup(FMLClientSetupEvent event)
        {
        }

        @SubscribeEvent
        public static void registerScreens(RegisterMenuScreensEvent event)
        {
            // Register your container screen with the menu
            event.register(ModMenuTypes.AUTO_MINER.get(), AutoMinerScreen::new);
        }
    }
}
