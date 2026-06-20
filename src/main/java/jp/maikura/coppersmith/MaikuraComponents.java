package jp.maikura.coppersmith;

import com.mojang.serialization.Codec;
import net.minecraft.component.ComponentType;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;
import net.minecraft.util.Identifier;

public final class MaikuraComponents {
    public static final ComponentType<Integer> WAX = Registry.register(
            Registries.DATA_COMPONENT_TYPE,
            Identifier.of(MaikuraCoppersmithToolMod.MOD_ID, "wax"),
            ComponentType.<Integer>builder().codec(Codec.INT).build()
    );

    public static void init() {
        // Force data component registration during mod initialization, before registries are frozen.
    }

    private MaikuraComponents() {
    }
}
