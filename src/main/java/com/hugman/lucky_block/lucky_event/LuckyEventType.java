package com.hugman.lucky_block.lucky_event;

import com.mojang.serialization.MapCodec;

public record LuckyEventType<T extends LuckyEvent>(MapCodec<T> codec) {
}
