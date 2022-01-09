package de.melanx.excavar.client;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import de.melanx.excavar.api.Excavador;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.LevelRenderer;
import net.minecraft.core.BlockPos;
import net.minecraft.world.phys.Vec3;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.Shapes;
import net.minecraft.world.phys.shapes.VoxelShape;

import java.util.ArrayList;
import java.util.List;

public class BlockHighlighter {
    
    private final Excavador excavador;
    private VoxelShape shape;

    public BlockHighlighter(Excavador excavador) {
        this.excavador = excavador;
    }
    
    private VoxelShape shape() {
        if (this.shape == null) {
            this.excavador.findBlocks();
            List<VoxelShape> allShapes = new ArrayList<>();
            for (BlockPos pos : this.excavador.getBlocksToMine()) {
                VoxelShape blockShape = this.excavador.level.getBlockState(pos).getVisualShape(this.excavador.level, pos, CollisionContext.empty());
                double dx = pos.getX() - this.excavador.start.getX();
                double dy = pos.getY() - this.excavador.start.getY();
                double dz = pos.getZ() - this.excavador.start.getZ();
                allShapes.add(blockShape.move(dx, dy, dz));
            }
            this.shape = Shapes.or(Shapes.empty(), allShapes.toArray(new VoxelShape[]{})).optimize();
        }
        return this.shape;
    }
    
    public void render(PoseStack poseStack) {
        poseStack.pushPose();
        Vec3 projection = Minecraft.getInstance().gameRenderer.getMainCamera().getPosition();
        poseStack.translate(this.excavador.start.getX() - projection.x, this.excavador.start.getY() - projection.y, this.excavador.start.getZ() - projection.z);
        
        VertexConsumer vertex = null; // TODO get an outline buffer.
        LevelRenderer.renderVoxelShape(poseStack, vertex, this.shape(), 0, 0, 0, 1, 1, 1, 1);
        poseStack.popPose();
    }
}
