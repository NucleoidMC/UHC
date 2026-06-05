package fr.hugman.uhc.api.modifier;

import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import fr.hugman.uhc.impl.game.UHCPlayerManager;
import net.minecraft.core.Holder;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.effect.MobEffectInstance;

public record PermanentEffectModifier(
        Holder<MobEffect> effect,
        int amplifier
) implements Modifier {
    public static final MapCodec<PermanentEffectModifier> CODEC = RecordCodecBuilder.mapCodec(instance -> instance.group(
            MobEffect.CODEC.fieldOf("effect").forGetter(PermanentEffectModifier::effect),
            Codec.intRange(0, Integer.MAX_VALUE).optionalFieldOf("amplifier", 0).forGetter(PermanentEffectModifier::amplifier)
    ).apply(instance, PermanentEffectModifier::new));

    @Override
    public ModifierType<?> getType() {
        return ModifierType.PERMANENT_EFFECT;
    }

    public void setEffect(ServerPlayer player) {
        player.addEffect(new MobEffectInstance(this.effect, -1, this.amplifier, false, false, true));
    }

    @Override
    public void enable(UHCPlayerManager playerManager) {
        playerManager.forEachAlive(this::setEffect);
    }

    @Override
    public void disable(UHCPlayerManager playerManager) {
        playerManager.forEachAlive(player -> player.removeEffect(this.effect));
    }
}
