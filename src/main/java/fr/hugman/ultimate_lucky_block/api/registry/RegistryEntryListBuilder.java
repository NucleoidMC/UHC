package fr.hugman.ultimate_lucky_block.api.registry;

import com.google.common.collect.ImmutableList;
import net.minecraft.registry.entry.RegistryEntry;
import net.minecraft.registry.entry.RegistryEntryList;

/**
 * @author Hugman
 * @since 1.0.0
 */
public abstract class RegistryEntryListBuilder<B extends RegistryEntryListBuilder<B, R>, R> {
    protected final ImmutableList.Builder<RegistryEntry<R>> entries = ImmutableList.builder();

    protected abstract B getThis();

    public B add(R event) {
        return this.add(RegistryEntry.of(event));
    }

    public B add(R... events) {
        for (R event : events) {
            this.add(event);
        }
        return getThis();
    }

    public B add(RegistryEntry<R> entry) {
        this.entries.add(entry);
        return getThis();
    }

    public B add(RegistryEntry<R>... events) {
        for (RegistryEntry<R> event : events) {
            this.add(event);
        }
        return getThis();
    }

    public B add(Iterable<RegistryEntry<R>> events) {
        for (RegistryEntry<R> event : events) {
            this.add(event);
        }
        return getThis();
    }

    public B add(RegistryEntryList<R> entry) {
        this.entries.addAll(entry);
        return getThis();
    }
}
