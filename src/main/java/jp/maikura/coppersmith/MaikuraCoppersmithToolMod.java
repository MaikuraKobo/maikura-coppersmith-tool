package jp.maikura.coppersmith;

import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.itemgroup.v1.ItemGroupEvents;
import net.fabricmc.fabric.api.event.player.UseBlockCallback;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemGroups;
import net.minecraft.item.ItemStack;
import net.minecraft.item.ItemUsageContext;
import net.minecraft.item.Items;
import net.minecraft.component.type.TooltipDisplayComponent;
import net.minecraft.item.tooltip.TooltipType;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;
import net.minecraft.registry.RegistryKey;
import net.minecraft.registry.RegistryKeys;
import net.minecraft.sound.SoundCategory;
import net.minecraft.sound.SoundEvents;
import net.minecraft.text.Text;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Formatting;
import net.minecraft.util.Hand;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

import java.util.HashMap;
import java.util.function.Consumer;
import java.util.Map;

public class MaikuraCoppersmithToolMod implements ModInitializer {
    public static final String MOD_ID = "maikura_coppersmith_tool";
    public static final int MAX_WAX = 100;

    public static final Identifier COPPERSMITH_TOOL_ID = Identifier.of(MOD_ID, "coppersmith_tool");
    public static final Identifier WAXED_COPPERSMITH_TOOL_ID = Identifier.of(MOD_ID, "waxed_coppersmith_tool");

    public static final RegistryKey<Item> COPPERSMITH_TOOL_KEY = RegistryKey.of(RegistryKeys.ITEM, COPPERSMITH_TOOL_ID);
    public static final RegistryKey<Item> WAXED_COPPERSMITH_TOOL_KEY = RegistryKey.of(RegistryKeys.ITEM, WAXED_COPPERSMITH_TOOL_ID);

    public static final Item COPPERSMITH_TOOL = Registry.register(
            Registries.ITEM,
            COPPERSMITH_TOOL_KEY,
            new CoppersmithToolItem(new Item.Settings().registryKey(COPPERSMITH_TOOL_KEY).maxCount(1).maxDamage(1024).enchantable(1), false)
    );

    public static final Item WAXED_COPPERSMITH_TOOL = Registry.register(
            Registries.ITEM,
            WAXED_COPPERSMITH_TOOL_KEY,
            new CoppersmithToolItem(new Item.Settings().registryKey(WAXED_COPPERSMITH_TOOL_KEY).maxCount(1).maxDamage(1024).enchantable(1), true)
    );

    private static final Map<String, String> OXIDIZE = new HashMap<>();
    private static final Map<String, String> RESTORE = new HashMap<>();
    private static final Map<String, String> WAX = new HashMap<>();


    @Override
    public void onInitialize() {
        MaikuraComponents.init();
        registerCopperMaps();

        UseBlockCallback.EVENT.register((player, world, hand, hitResult) -> {
            ItemStack stack = player.getStackInHand(hand);
            if (!(stack.getItem() instanceof CoppersmithToolItem toolItem)) {
                return ActionResult.PASS;
            }

            BlockState state = world.getBlockState(hitResult.getBlockPos());
            if (!isCopperBlock(state) && !isUnwaxedCopper(state)) {
                return ActionResult.PASS;
            }

            boolean restore = player.isSneaking();
            return toolItem.applyFromCallback(world, player, stack, hitResult.getBlockPos(), restore);
        });

        ItemGroupEvents.modifyEntriesEvent(ItemGroups.TOOLS).register(entries -> {
            entries.add(COPPERSMITH_TOOL);
            entries.add(WAXED_COPPERSMITH_TOOL);
        });
    }

    private static void registerCopperMaps() {
        addCopperFamily("copper_block", "exposed_copper", "weathered_copper", "oxidized_copper", "waxed_copper_block", "waxed_exposed_copper", "waxed_weathered_copper", "waxed_oxidized_copper");
        addCopperFamily("cut_copper", "exposed_cut_copper", "weathered_cut_copper", "oxidized_cut_copper", "waxed_cut_copper", "waxed_exposed_cut_copper", "waxed_weathered_cut_copper", "waxed_oxidized_cut_copper");
        addCopperFamily("cut_copper_stairs", "exposed_cut_copper_stairs", "weathered_cut_copper_stairs", "oxidized_cut_copper_stairs", "waxed_cut_copper_stairs", "waxed_exposed_cut_copper_stairs", "waxed_weathered_cut_copper_stairs", "waxed_oxidized_cut_copper_stairs");
        addCopperFamily("cut_copper_slab", "exposed_cut_copper_slab", "weathered_cut_copper_slab", "oxidized_cut_copper_slab", "waxed_cut_copper_slab", "waxed_exposed_cut_copper_slab", "waxed_weathered_cut_copper_slab", "waxed_oxidized_cut_copper_slab");
        addCopperFamily("chiseled_copper", "exposed_chiseled_copper", "weathered_chiseled_copper", "oxidized_chiseled_copper", "waxed_chiseled_copper", "waxed_exposed_chiseled_copper", "waxed_weathered_chiseled_copper", "waxed_oxidized_chiseled_copper");
        addCopperFamily("copper_grate", "exposed_copper_grate", "weathered_copper_grate", "oxidized_copper_grate", "waxed_copper_grate", "waxed_exposed_copper_grate", "waxed_weathered_copper_grate", "waxed_oxidized_copper_grate");
        addCopperFamily("copper_bulb", "exposed_copper_bulb", "weathered_copper_bulb", "oxidized_copper_bulb", "waxed_copper_bulb", "waxed_exposed_copper_bulb", "waxed_weathered_copper_bulb", "waxed_oxidized_copper_bulb");
        addCopperFamily("copper_door", "exposed_copper_door", "weathered_copper_door", "oxidized_copper_door", "waxed_copper_door", "waxed_exposed_copper_door", "waxed_weathered_copper_door", "waxed_oxidized_copper_door");
        addCopperFamily("copper_trapdoor", "exposed_copper_trapdoor", "weathered_copper_trapdoor", "oxidized_copper_trapdoor", "waxed_copper_trapdoor", "waxed_exposed_copper_trapdoor", "waxed_weathered_copper_trapdoor", "waxed_oxidized_copper_trapdoor");
        addCopperFamily("copper_bars", "exposed_copper_bars", "weathered_copper_bars", "oxidized_copper_bars", "waxed_copper_bars", "waxed_exposed_copper_bars", "waxed_weathered_copper_bars", "waxed_oxidized_copper_bars");
        addCopperFamily("copper_chain", "exposed_copper_chain", "weathered_copper_chain", "oxidized_copper_chain", "waxed_copper_chain", "waxed_exposed_copper_chain", "waxed_weathered_copper_chain", "waxed_oxidized_copper_chain");
    }

    private static void addCopperFamily(String fresh, String exposed, String weathered, String oxidized, String waxedFresh, String waxedExposed, String waxedWeathered, String waxedOxidized) {
        addStep(fresh, exposed);
        addStep(exposed, weathered);
        addStep(weathered, oxidized);
        addStep(waxedFresh, waxedExposed);
        addStep(waxedExposed, waxedWeathered);
        addStep(waxedWeathered, waxedOxidized);

        addRestore(exposed, fresh);
        addRestore(weathered, exposed);
        addRestore(oxidized, weathered);
        addRestore(waxedExposed, waxedFresh);
        addRestore(waxedWeathered, waxedExposed);
        addRestore(waxedOxidized, waxedWeathered);

        addWax(fresh, waxedFresh);
        addWax(exposed, waxedExposed);
        addWax(weathered, waxedWeathered);
        addWax(oxidized, waxedOxidized);
    }

    private static void addStep(String from, String to) {
        if (blockExists(from) && blockExists(to)) {
            OXIDIZE.put(from, to);
        }
    }

    private static void addRestore(String from, String to) {
        if (blockExists(from) && blockExists(to)) {
            RESTORE.put(from, to);
        }
    }

    private static void addWax(String from, String to) {
        if (blockExists(from) && blockExists(to)) {
            WAX.put(from, to);
        }
    }

    public static boolean isUnwaxedCopper(BlockState state) {
        Identifier id = Registries.BLOCK.getId(state.getBlock());
        return WAX.containsKey(id.getPath());
    }

    public static boolean isCopperBlock(BlockState state) {
        Identifier id = Registries.BLOCK.getId(state.getBlock());
        String path = id.getPath();
        return WAX.containsKey(path) || OXIDIZE.containsKey(path) || RESTORE.containsKey(path) || path.startsWith("waxed_") && (OXIDIZE.containsKey(path) || RESTORE.containsKey(path));
    }

    private static boolean blockExists(String path) {
        return !Registries.BLOCK.get(Identifier.ofVanilla(path)).equals(Blocks.AIR) || path.equals("air");
    }

    private static Block blockByPath(String path) {
        return Registries.BLOCK.get(Identifier.ofVanilla(path));
    }

    private static boolean transformCopper(World world, BlockPos pos, BlockState oldState, String targetPath, boolean playWaxSound) {
        Block targetBlock = blockByPath(targetPath);
        if (targetBlock == Blocks.AIR) {
            return false;
        }
        BlockState newState = targetBlock.getStateWithProperties(oldState);
        world.setBlockState(pos, newState, Block.NOTIFY_ALL);
        world.syncWorldEvent(null, playWaxSound ? 3003 : 3005, pos, 0);
        return true;
    }

    private static ActionResult applyCopperTransform(World world, PlayerEntity player, ItemStack stack, BlockPos pos, boolean restore, boolean waxCapable) {
        BlockState oldState = world.getBlockState(pos);
        Identifier id = Registries.BLOCK.getId(oldState.getBlock());
        String path = id.getPath();

        String target = restore ? RESTORE.get(path) : OXIDIZE.get(path);
        if (target == null) {
            return ActionResult.PASS;
        }

        boolean willAutoWax = waxCapable && WAX.containsKey(target);
        String finalTarget = willAutoWax ? WAX.get(target) : target;
        if (willAutoWax && !world.isClient() && !consumeWax(stack, player, world)) {
            return ActionResult.FAIL;
        }

        if (!world.isClient()) {
            transformCopper(world, pos, oldState, finalTarget, willAutoWax);
            if (!player.isCreative()) {
                stack.damage(1, player);
            }
        }
        world.playSound(player, pos, restore ? SoundEvents.ITEM_AXE_SCRAPE : SoundEvents.ITEM_HONEYCOMB_WAX_ON, SoundCategory.BLOCKS, 0.8F, restore ? 0.85F : 1.1F);
        return ActionResult.SUCCESS;
    }

    private static ActionResult toggleHighlight(World world, PlayerEntity user) {
        if (world.isClient()) {
            MaikuraCoppersmithToolClientHooks.toggleHighlight(user);
        }
        return ActionResult.SUCCESS;
    }

    private static boolean consumeWax(ItemStack stack, PlayerEntity player, World world) {
        int wax = getWax(stack);
        if (wax <= 0) {
            if (!world.isClient()) {
                player.sendMessage(Text.literal("ワックス残量が足りません").formatted(Formatting.RED), true);
            }
            return false;
        }
        setWax(stack, wax - 1);
        return true;
    }

    public static int getWax(ItemStack stack) {
        Integer value = stack.get(MaikuraComponents.WAX);
        return value == null ? 0 : Math.max(0, Math.min(MAX_WAX, value));
    }

    public static void setWax(ItemStack stack, int value) {
        stack.set(MaikuraComponents.WAX, Math.max(0, Math.min(MAX_WAX, value)));
    }

    public static class CoppersmithToolItem extends Item {
        private final boolean waxCapable;

        public CoppersmithToolItem(Settings settings, boolean waxCapable) {
            super(settings);
            this.waxCapable = waxCapable;
        }

        public ActionResult applyFromCallback(World world, PlayerEntity player, ItemStack stack, BlockPos pos, boolean restore) {
            return applyCopperTransform(world, player, stack, pos, restore, waxCapable);
        }

        @Override
        public ActionResult useOnBlock(ItemUsageContext context) {
            World world = context.getWorld();
            PlayerEntity player = context.getPlayer();
            if (player == null) {
                return ActionResult.PASS;
            }

            // Right click oxidizes. Shift + right click restores when the target is copper.
            boolean restore = player.isSneaking();
            BlockState targetState = world.getBlockState(context.getBlockPos());
            boolean copperTarget = isCopperBlock(targetState) || isUnwaxedCopper(targetState);

            ActionResult result = applyCopperTransform(world, player, context.getStack(), context.getBlockPos(), restore, waxCapable);
            if (result != ActionResult.PASS) {
                return result;
            }

            // When looking at any copper block, do not fall through to highlight toggling.
            // This prevents the outline from toggling after copper is fully restored.
            if (restore && copperTarget) {
                return ActionResult.SUCCESS;
            }

            return ActionResult.PASS;
        }

        @Override
        public ActionResult use(World world, PlayerEntity user, Hand hand) {
            ItemStack stack = user.getStackInHand(hand);

            // Shift + right click in air toggles the unwaxed-copper outline.
            if (user.isSneaking()) {
                return toggleHighlight(world, user);
            }

            if (waxCapable) {
                ItemStack offHand = user.getOffHandStack();
                if (offHand.isOf(Items.HONEYCOMB) || offHand.isOf(Items.HONEYCOMB_BLOCK)) {
                    if (!world.isClient()) {
                        int add = offHand.isOf(Items.HONEYCOMB_BLOCK) ? 20 : 5;
                        int before = getWax(stack);
                        int after = Math.min(MAX_WAX, before + add);
                        if (after > before) {
                            setWax(stack, after);
                            if (!user.isCreative()) {
                                offHand.decrement(1);
                            }
                            world.playSound(null, user.getBlockPos(), SoundEvents.ITEM_HONEYCOMB_WAX_ON, SoundCategory.PLAYERS, 0.8F, 1.0F);
                            user.sendMessage(Text.literal("ワックス残量: " + after + " / " + MAX_WAX), true);
                        } else {
                            user.sendMessage(Text.literal("ワックス残量は最大です").formatted(Formatting.YELLOW), true);
                        }
                    }
                    return ActionResult.SUCCESS;
                }
            }

            // Normal air right click does nothing, avoiding accidental outline toggles.
            return ActionResult.PASS;
        }

        @Override
        public void appendTooltip(ItemStack stack, Item.TooltipContext context, TooltipDisplayComponent displayComponent, Consumer<Text> textConsumer, TooltipType type) {
            if (waxCapable) {
                textConsumer.accept(Text.literal("Wax: " + getWax(stack) + " / " + MAX_WAX).formatted(Formatting.GOLD));
                textConsumer.accept(Text.literal("オフハンドのハニカムで+5 / ハニカムブロックで+20").formatted(Formatting.GRAY));
            }
            textConsumer.accept(Text.literal("右クリック: 酸化").formatted(Formatting.GRAY));
            textConsumer.accept(Text.literal("Shift+右クリック: 復元").formatted(Formatting.GRAY));
            textConsumer.accept(Text.literal("空中Shift+右クリック: 未ワックス銅の枠表示ON/OFF").formatted(Formatting.GRAY));
        }
    }
}
