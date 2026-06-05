package fr.hugman.uhc.impl.game.phase;

import net.minecraft.util.StringRepresentable;

public enum UHCGamePhase implements StringRepresentable {
    WARMUP("warmup"),
    FIGHT("fight");

    public static final StringRepresentable.EnumCodec<UHCGamePhase> CODEC = StringRepresentable.fromEnum(UHCGamePhase::values);
    private final String id;

    UHCGamePhase(final String id) {
        this.id = id;
    }

    public String getId() {
        return this.id;
    }

    public static UHCGamePhase getFromId(String id) {
        return CODEC.byName(id);
    }

    @Override
    public String getSerializedName() {
        return this.id;
    }
}
