package de.cravingsex.skinviewer;

import net.fabricmc.api.ClientModInitializer;

public final class SkinViewerClient implements ClientModInitializer {
    @Override
    public void onInitializeClient() {
        SkinViewerConfig.load();
    }
}
