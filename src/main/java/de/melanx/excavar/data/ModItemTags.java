package de.melanx.excavar.data;

import de.melanx.excavar.Excavar;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.registries.Registries;
import net.minecraft.data.PackOutput;
import net.minecraft.data.tags.TagsProvider;
import net.minecraft.resources.Identifier;
import net.minecraft.tags.ItemTags;
import net.minecraft.tags.TagKey;
import net.minecraft.world.item.Item;

import javax.annotation.Nonnull;
import java.util.concurrent.CompletableFuture;

public class ModItemTags extends TagsProvider<Item> {

    public static final TagKey<Item> AXES = TagKey.create(Registries.ITEM, Identifier.fromNamespaceAndPath(Excavar.MODID, "axes"));
    public static final TagKey<Item> HOES = TagKey.create(Registries.ITEM, Identifier.fromNamespaceAndPath(Excavar.MODID, "hoes"));
    public static final TagKey<Item> PICKAXES = TagKey.create(Registries.ITEM, Identifier.fromNamespaceAndPath(Excavar.MODID, "pickaxes"));
    public static final TagKey<Item> SHOVELS = TagKey.create(Registries.ITEM, Identifier.fromNamespaceAndPath(Excavar.MODID, "shovels"));
    public static final TagKey<Item> SWORDS = TagKey.create(Registries.ITEM, Identifier.fromNamespaceAndPath(Excavar.MODID, "swords"));
    public static final TagKey<Item> AIOTS = TagKey.create(Registries.ITEM, Identifier.fromNamespaceAndPath("c", "tools/aiots"));
    public static final TagKey<Item> PAXELS = TagKey.create(Registries.ITEM, Identifier.fromNamespaceAndPath("c", "tools/paxels"));

    public ModItemTags(PackOutput output, CompletableFuture<HolderLookup.Provider> lookupProvider) {
        super(output, Registries.ITEM, lookupProvider, Excavar.MODID);
    }

    @Override
    protected void addTags(@Nonnull HolderLookup.Provider provider) {
        this.tag(ModItemTags.AXES).addTag(ItemTags.AXES)
                .addOptionalTag(ModItemTags.AIOTS)
                .addOptionalTag(ModItemTags.PAXELS);
        this.tag(ModItemTags.HOES).addTag(ItemTags.HOES)
                .addOptionalTag(ModItemTags.AIOTS);
        this.tag(ModItemTags.PICKAXES).addTag(ItemTags.PICKAXES)
                .addOptionalTag(ModItemTags.AIOTS)
                .addOptionalTag(ModItemTags.PAXELS);
        this.tag(ModItemTags.SHOVELS).addTag(ItemTags.SHOVELS)
                .addOptionalTag(ModItemTags.AIOTS)
                .addOptionalTag(ModItemTags.PAXELS);
        this.tag(ModItemTags.SWORDS).addTag(ItemTags.SWORDS)
                .addOptionalTag(ModItemTags.AIOTS);
    }
}
