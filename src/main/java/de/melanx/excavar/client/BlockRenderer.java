package de.melanx.excavar.client;

import com.mojang.blaze3d.vertex.PoseStack;
import de.melanx.excavar.ShapeUtil;
import de.melanx.excavar.api.shape.Shape;
import de.melanx.excavar.api.shape.Shapes;
import de.melanx.excavar.config.ListHandler;
import net.minecraft.client.Minecraft;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.state.BlockOutlineRenderState;
import net.minecraft.client.renderer.state.LevelRenderState;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.BlockHitResult;
import net.neoforged.neoforge.client.CustomBlockOutlineRenderer;

import javax.annotation.Nonnull;

import static de.melanx.excavar.client.ClientExcavar.EXCAVAR;

public class BlockRenderer implements CustomBlockOutlineRenderer {

    private BlockHighlighter blockHighlighter = null;
    private Matcher matcher = new Matcher(BlockPos.ZERO, null, Blocks.AIR.defaultBlockState(), null, null);

    @Override
    public boolean render(@Nonnull BlockOutlineRenderState renderState, @Nonnull MultiBufferSource.BufferSource buffer, @Nonnull PoseStack poseStack, boolean translucentPass, @Nonnull LevelRenderState levelRenderState) {
        LocalPlayer player = Minecraft.getInstance().player;
        //noinspection ConstantConditions
        if (!ClientConfig.enableOutline.get() || !ListHandler.isToolAllowed(player.getMainHandItem())) {
            return false;
        }

        if (!(Minecraft.getInstance().hitResult instanceof BlockHitResult hitResult)) {
            return false;
        }

        BlockState state = player.level().getBlockState(hitResult.getBlockPos());

        if (!ShapeUtil.miningAllowed(state)) {
            return false;
        }

        if (EXCAVAR.isDown() && (player.isShiftKeyDown() || !ClientConfig.onlyWhileSneaking.get())) {
            if (!this.matcher.matches(hitResult.getBlockPos(), hitResult.getDirection(), state, player.getMainHandItem(), Shapes.getShape(Shapes.getSelectedShape())) || this.blockHighlighter == null) {
                this.blockHighlighter = new BlockHighlighter(hitResult);
                this.matcher = new Matcher(hitResult.getBlockPos(), hitResult.getDirection(), state, player.getMainHandItem(), Shapes.getShape(Shapes.getSelectedShape()));
            }

            this.blockHighlighter.render(buffer, poseStack);
        } else {
            this.blockHighlighter = null;
        }

        return false;
    }

    private record Matcher(@Nonnull BlockPos pos, Direction side, @Nonnull BlockState state,
                           ItemStack tool, Shape shape) {

        public boolean matches(BlockPos otherPos, Direction /* hello from the */ otherSide, BlockState state, ItemStack tool, Shape shape) {
            return this.pos.equals(otherPos) && this.side == otherSide && this.state.equals(state) && this.tool == tool && this.shape == shape;
        }
    }
}
