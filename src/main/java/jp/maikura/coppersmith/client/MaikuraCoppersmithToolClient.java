package jp.maikura.coppersmith.client;

import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.rendering.v1.world.WorldRenderEvents;

public final class MaikuraCoppersmithToolClient implements ClientModInitializer {
    @Override
    public void onInitializeClient() {
        WorldRenderEvents.END_MAIN.register(context -> UnwaxedCopperOutlineRenderer.render());
    }
}
