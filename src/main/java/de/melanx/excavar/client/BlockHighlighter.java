package de.melanx.excavar.client;

import com.google.common.collect.Lists;
import com.mojang.blaze3d.vertex.PoseStack;
import de.melanx.excavar.ShapeUtil;
import de.melanx.excavar.api.Excavador;
import net.minecraft.client.Minecraft;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.ShapeRenderer;
import net.minecraft.core.BlockPos;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.Vec3;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.Shapes;
import net.minecraft.world.phys.shapes.VoxelShape;

import java.awt.Color;
import java.util.List;

public class BlockHighlighter {

    public static final Color LIGHT_GRAY = new Color(255, 255, 255, 30);
    private final Excavador excavador;
    private VoxelShape shape;
    private final ClientLevel level;

    public BlockHighlighter(Excavador excavador) {
        this.excavador = excavador;
        this.level = Minecraft.getInstance().level;
    }

    public BlockHighlighter(BlockHitResult hitResult) {
        this.level = Minecraft.getInstance().level;
        LocalPlayer player = Minecraft.getInstance().player;
        //noinspection ConstantConditions
        BlockState state = this.level.getBlockState(hitResult.getBlockPos());
        ResourceLocation shapeId = de.melanx.excavar.api.shape.Shapes.getSelectedShape();
        if (shapeId == de.melanx.excavar.api.shape.Shapes.SHAPELESS) {
            shapeId = ShapeUtil.getShapeId(state.getBlock());
        }
        //noinspection ConstantConditions
        this.excavador = new Excavador(shapeId, hitResult.getBlockPos(), this.level, player, hitResult.getDirection(), state);
    }

    private VoxelShape shape() {
        if (this.shape == null) {
            //noinspection ConstantConditions
            ItemStack heldItem = Minecraft.getInstance().player.getMainHandItem();
            int maxBlocks = (ClientConfig.considerDurability.get() && heldItem.isDamageableItem())
                    ? heldItem.getMaxDamage() - heldItem.getDamageValue() - (ClientConfig.preventToolsBreaking.get()
                                                                             ? 2
                                                                             : 1) // we need to increase this by 1, otherwise it would display 1 block too much
                    : Integer.MAX_VALUE;
            this.excavador.findBlocks(maxBlocks);
            List<VoxelShape> allShapes = Lists.newArrayList();
            for (BlockPos pos : this.excavador.getBlocksToMine()) {
                VoxelShape blockShape = this.excavador.level.getBlockState(pos).getShape(this.excavador.level, pos, CollisionContext.empty());
                double dx = pos.getX() - this.excavador.start.getX();
                double dy = pos.getY() - this.excavador.start.getY();
                double dz = pos.getZ() - this.excavador.start.getZ();
                allShapes.add(blockShape.move(dx, dy, dz));
            }

            this.shape = Shapes.or(Shapes.empty(), allShapes.toArray(new VoxelShape[]{})).optimize();
        }

        return this.shape;
    }

    public void render(MultiBufferSource buffer, PoseStack poseStack) {
        poseStack.pushPose();
        Vec3 projection = Minecraft.getInstance().gameRenderer.getMainCamera().getPosition();
        poseStack.translate(this.excavador.start.getX() - projection.x, this.excavador.start.getY() - projection.y, this.excavador.start.getZ() - projection.z);

        VoxelShape allBlocksShape = this.shape();
        ShapeRenderer.renderShape(poseStack, buffer.getBuffer(RenderType.lines()), allBlocksShape, 0, 0, 0, Color.WHITE.getRGB());
        ShapeRenderer.renderShape(poseStack, buffer.getBuffer(HiddenRenderTypes.HIDDEN_OUTLINES), allBlocksShape, 0, 0, 0, LIGHT_GRAY.getRGB());
        poseStack.popPose();
    }
}
