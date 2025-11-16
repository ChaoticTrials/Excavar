package de.melanx.excavar.api;

import net.minecraft.core.Holder;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.tags.TagKey;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;

import java.util.*;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

public class ConfiguredSameTags {

    private static final Map<TagKey<Block>, Set<Holder<Block>>> TAGS = new HashMap<>();

    public static void refreshTags(List<? extends String> tags) {
        Set<Pattern> tagPatterns = tags.stream().map(s -> Pattern.compile("^" + s.replace("*", ".*") + "$")).collect(Collectors.toSet());

        TAGS.clear();
        for (Pattern pattern : tagPatterns) {
            BuiltInRegistries.BLOCK.getTagNames().forEach(tag -> {
                if (tag.location().toString().matches(pattern.pattern())) {
                    Iterable<Holder<Block>> tagOrEmpty = BuiltInRegistries.BLOCK.getTagOrEmpty(tag);
                    Set<Holder<Block>> blocks = new HashSet<>();
                    for (Holder<Block> blockHolder : tagOrEmpty) {
                        blocks.add(blockHolder);
                    }

                    TAGS.put(tag, blocks);
                }
            });
        }
    }

    public static boolean isAllowed(BlockState originalState, BlockState otherState) {
        for (TagKey<Block> blockTagKey : originalState.getTags().toList()) {
            if (TAGS.containsKey(blockTagKey) && otherState.is(blockTagKey)) {
                return true;
            }
        }

        return false;
    }
}
