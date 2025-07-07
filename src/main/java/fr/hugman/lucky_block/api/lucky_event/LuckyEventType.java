package fr.hugman.lucky_block.api.lucky_event;

import com.mojang.serialization.MapCodec;

/**
 * @author Hugman
 * @since 1.0.0
 */
public record LuckyEventType<T extends LuckyEvent>(MapCodec<T> codec) {
}
