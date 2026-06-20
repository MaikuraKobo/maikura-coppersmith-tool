package jp.maikura.coppersmith.client;

import jp.maikura.coppersmith.MaikuraCoppersmithToolMod;
import net.minecraft.block.BlockState;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.render.DrawStyle;
import net.minecraft.item.ItemStack;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraft.world.debug.gizmo.GizmoDrawing;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

public final class UnwaxedCopperOutlineRenderer {
    private static final int SCAN_RADIUS = 12;
    private static final int SCAN_INTERVAL_TICKS = 10;
    private static final int MAX_OUTLINES = 192;
    private static final float OUTER_WIDTH = 0.90F;
    private static final float INNER_WIDTH = 0.55F;

    private static boolean enabled = false;
    private static long lastScanTime = Long.MIN_VALUE;
    private static final List<BlockPos> targets = new ArrayList<>();

    private UnwaxedCopperOutlineRenderer() {
    }

    public static void toggle() {
        enabled = !enabled;
        if (!enabled) {
            targets.clear();
            lastScanTime = Long.MIN_VALUE;
        }
    }

    public static boolean isEnabled() {
        return enabled;
    }

    public static void render() {
        MinecraftClient client = MinecraftClient.getInstance();
        if (client == null || client.player == null || client.world == null || !enabled) {
            targets.clear();
            return;
        }

        ItemStack mainHand = client.player.getMainHandStack();
        ItemStack offHand = client.player.getOffHandStack();
        if (!mainHand.isOf(MaikuraCoppersmithToolMod.COPPERSMITH_TOOL)
                && !mainHand.isOf(MaikuraCoppersmithToolMod.WAXED_COPPERSMITH_TOOL)
                && !offHand.isOf(MaikuraCoppersmithToolMod.COPPERSMITH_TOOL)
                && !offHand.isOf(MaikuraCoppersmithToolMod.WAXED_COPPERSMITH_TOOL)) {
            targets.clear();
            lastScanTime = Long.MIN_VALUE;
            return;
        }

        World world = client.world;
        long time = world.getTime();
        if (lastScanTime == Long.MIN_VALUE || time - lastScanTime >= SCAN_INTERVAL_TICKS) {
            lastScanTime = time;
            rescan(world, client.player.getBlockPos());
        }

        for (BlockPos pos : targets) {
            GizmoDrawing.box(pos, 0.090F, DrawStyle.stroked(0xFF000000, OUTER_WIDTH)).ignoreOcclusion().withLifespan(12);
            GizmoDrawing.box(pos, 0.045F, DrawStyle.stroked(0xFFFFB02E, INNER_WIDTH)).ignoreOcclusion().withLifespan(12);
        }
    }

    private static void rescan(World world, BlockPos origin) {
        List<BlockPos> found = new ArrayList<>();
        for (BlockPos pos : BlockPos.iterate(origin.add(-SCAN_RADIUS, -SCAN_RADIUS, -SCAN_RADIUS), origin.add(SCAN_RADIUS, SCAN_RADIUS, SCAN_RADIUS))) {
            BlockState state = world.getBlockState(pos);
            if (MaikuraCoppersmithToolMod.isUnwaxedCopper(state)) {
                found.add(pos.toImmutable());
            }
        }
        found.sort(Comparator.comparingDouble(pos -> pos.getSquaredDistance(origin)));
        targets.clear();
        int count = Math.min(MAX_OUTLINES, found.size());
        for (int i = 0; i < count; i++) {
            targets.add(found.get(i));
        }
    }
}
