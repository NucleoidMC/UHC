package fr.hugman.uhc.impl.game;

import eu.pb4.sgui.api.GuiHelpers;
import eu.pb4.sgui.api.gui.GuiInterface;
import fr.hugman.uhc.impl.UHC;
import fr.hugman.uhc.api.gui.creator.UHCModulesGui;
import fr.hugman.uhc.api.modifier.*;
import fr.hugman.uhc.api.module.UHCModule;
import org.jetbrains.annotations.Nullable;
import xyz.nucleoid.plasmid.api.game.GameActivity;
import xyz.nucleoid.plasmid.api.game.GameAttachment;
import xyz.nucleoid.plasmid.api.game.event.GameActivityEvents;
import xyz.nucleoid.stimuli.event.DroppedItemsResult;
import xyz.nucleoid.stimuli.event.EventResult;
import xyz.nucleoid.stimuli.event.block.BlockBreakEvent;
import xyz.nucleoid.stimuli.event.block.BlockDropItemsEvent;
import xyz.nucleoid.stimuli.event.entity.EntityDropItemsEvent;
import xyz.nucleoid.stimuli.event.world.ExplosionDetonatedEvent;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.function.Consumer;
import java.util.stream.Stream;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Holder;
import net.minecraft.core.HolderSet;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceKey;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Explosion;
import net.minecraft.world.level.block.state.BlockState;

public final class ModuleManager {
    public static final GameAttachment<ModuleManager> ATTACHMENT = GameAttachment.create(UHC.id("module_manager"));

    private final List<Holder<UHCModule>> modules;

    public ModuleManager(List<Holder<UHCModule>> modules) {
        this.modules = new ArrayList<>(modules);
    }

    public ModuleManager(HolderSet<UHCModule> modules) {
        this(modules.stream().toList());
    }

    public boolean isEmpty() {
        return modules.isEmpty();
    }

    public List<Holder<UHCModule>> modules() {
        return modules;
    }

    public List<Modifier> modifiers() {
        List<Modifier> modifiers = new ArrayList<>();
        for (var moduleEntry : modules) {
            modifiers.addAll(moduleEntry.value().modifiers());
        }
        return modifiers;
    }

    public List<ResourceKey<UHCModule>> keys() {
        return modules.stream().map(moduleRegistryEntry -> moduleRegistryEntry.unwrapKey().orElse(null)).filter(Objects::nonNull).toList();
    }

    public void forEach(Consumer<UHCModule> action) {
        modules.forEach(moduleEntry -> action.accept(moduleEntry.value()));
    }

    public <V extends Modifier> List<V> modifiers(ModifierType<V> type) {
        //TODO: cache modules so it's quicker to sort by type
        return ModuleManager.modifiers(modules, type);
    }

    public boolean enableModule(Holder<UHCModule> module) {
        if (modules.contains(module)) {
            return false;
        }
        return modules.add(module);
    }

    public boolean disableModule(Holder<UHCModule> module) {
        if (!modules.contains(module)) {
            return false;
        }
        return modules.remove(module);
    }

    /**
     * Builds a GUI for the player to check the list of currently active modules
     *
     * @param player The player to build the GUI for
     * @return The GUI
     */
    public GuiInterface buildGui(ServerPlayer player) {
        boolean isInGui = GuiHelpers.getCurrentGui(player) != null;
        UHCModulesGui gui = new UHCModulesGui(player, Mth.clamp(1, Mth.ceil((float) modules.size() / 9) + (isInGui ? 1 : 0), 6), modules);
        gui.setTitle(Component.translatable("ui.uhc.modules.title"));
        return gui;
    }

    /**
     * Filters a registry entry list of modules by type
     *
     * @return A list of modifiers of the specified type
     */
    public static <V extends Modifier> List<V> modifiers(List<Holder<UHCModule>> modules, ModifierType<V> type) {
        List<V> modifiers = new ArrayList<>();
        for (var moduleEntry : modules) {
            for (Modifier modifier : moduleEntry.value().modifiers()) {
                if (modifier.getType() == type) {
                    modifiers.add((V) modifier);
                }
            }
        }
        return modifiers;
    }


    public static <V extends Modifier> Stream<V> streamModifiers(Stream<Holder<UHCModule>> modules, ModifierType<V> type) {
        return modules
                .map(Holder::value)
                .flatMap(module -> module.modifiers().stream())
                .filter(modifier -> modifier.getType() == type)
                .map(modifier -> (V) modifier);
    }

    // LISTENERS
    public void setupListeners(GameActivity activity, UHCPlayerManager playerManager) {
        activity.listen(EntityDropItemsEvent.EVENT, this::onMobLoot);
        activity.listen(BlockBreakEvent.EVENT, this::onBlockBroken);
        activity.listen(BlockDropItemsEvent.EVENT, this::onBlockDrop);
        activity.listen(ExplosionDetonatedEvent.EVENT, this::onExplosion);
        activity.listen(GameActivityEvents.TICK, () -> this.tick(playerManager));
    }

    private void tick(UHCPlayerManager playerManager) {
        playerManager.forEachAlive(player -> {
            for (ReplaceStackModifier piece : this.modifiers(ModifierType.REPLACE_STACK)) {
                var inv = player.getInventory();
                for (int i = 0; i < inv.getContainerSize(); i++) {
                    ItemStack stack = inv.getItem(i);
                    if (piece.predicate().test(stack)) {
                        inv.setItem(i, piece.stack().copy());
                    }
                }
            }
        });
    }

    private EventResult onBlockBroken(ServerPlayer playerEntity, ServerLevel world, BlockPos pos) {
        for (TraversalBreakModifier piece : this.modifiers(ModifierType.TRAVERSAL_BREAK)) {
            piece.breakBlock(world, playerEntity, pos);
        }
        return EventResult.ALLOW;
    }

    private EventResult onExplosion(Explosion explosion, List<BlockPos> positions) {
        positions.forEach(pos -> {
            for (TraversalBreakModifier piece : this.modifiers(ModifierType.TRAVERSAL_BREAK)) {
                piece.breakBlock(explosion.level(), explosion.getIndirectSourceEntity(), pos);
            }
        });
        return EventResult.ALLOW;
    }


    private DroppedItemsResult onMobLoot(LivingEntity livingEntity, List<ItemStack> itemStacks) {
        boolean keepOld = true;
        List<ItemStack> stacks = new ArrayList<>();
        for (EntityLootModifier piece : this.modifiers(ModifierType.ENTITY_LOOT)) {
            if (piece.test(livingEntity)) {
                stacks.addAll(piece.getLoots((ServerLevel) livingEntity.level(), livingEntity));
                if (piece.shouldReplace()) keepOld = false;
            }
        }
        if (keepOld) stacks.addAll(itemStacks);
        return DroppedItemsResult.pass(stacks);
    }

    private DroppedItemsResult onBlockDrop(@Nullable Entity entity, ServerLevel world, BlockPos pos, BlockState state, List<ItemStack> itemStacks) {
        boolean keepOld = true;
        List<ItemStack> stacks = new ArrayList<>();
        for (BlockLootModifier piece : this.modifiers(ModifierType.BLOCK_LOOT)) {
            if (piece.test(state, world.getRandom())) {
                piece.spawnExperience(world, pos);
                stacks.addAll(piece.getLoots(world, pos, entity, entity instanceof LivingEntity ? ((LivingEntity) entity).getUseItem() : ItemStack.EMPTY));
                if (piece.shouldReplace()) keepOld = false;
            }
        }
        if (keepOld) stacks.addAll(itemStacks);
        return DroppedItemsResult.pass(stacks);
    }


    @Override
    public String toString() {
        return "ModuleManager[" +
                "modules=" + modules + ']';
    }
}
