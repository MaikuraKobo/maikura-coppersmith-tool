package jp.maikura.coppersmith;

import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.text.Text;

public final class MaikuraCoppersmithToolClientHooks {
    private MaikuraCoppersmithToolClientHooks() {
    }

    public static void toggleHighlight(PlayerEntity player) {
        jp.maikura.coppersmith.client.UnwaxedCopperOutlineRenderer.toggle();
        player.sendMessage(Text.literal("未ワックス銅 枠表示: " + (jp.maikura.coppersmith.client.UnwaxedCopperOutlineRenderer.isEnabled() ? "ON" : "OFF")), true);
    }
}
