package com.hugman.uhc.game.phase;

import net.minecraft.util.StringIdentifiable;

public enum UHCGamePhase implements StringIdentifiable {
    WARMUP("warmup"),
    FIGHT("fight");

    public static final StringIdentifiable.EnumCodec<UHCGamePhase> CODEC = StringIdentifiable.createCodec(UHCGamePhase::values);
    private final String id;

    UHCGamePhase(final String id) {
        this.id = id;
    }

    public String getId() {
        return this.id;
    }

    public static UHCGamePhase getFromId(String id) {
        return CODEC.byId(id);
    }

    @Override
    public String asString() {
        return this.id;
    }
}
