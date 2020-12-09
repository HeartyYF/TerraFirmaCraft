package net.dries007.tfc.common.blocks.plant;

import java.util.function.Supplier;

import net.minecraft.block.*;
import net.minecraft.fluid.Fluid;
import net.minecraft.fluid.FluidState;
import net.minecraft.fluid.Fluids;
import net.minecraft.item.BlockItemUseContext;
import net.minecraft.state.StateContainer;
import net.minecraft.util.Direction;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.shapes.ISelectionContext;
import net.minecraft.util.math.shapes.VoxelShape;
import net.minecraft.util.math.vector.Vector3d;
import net.minecraft.world.IBlockReader;
import net.minecraft.world.IWorld;
import net.minecraft.world.World;

import net.dries007.tfc.common.fluids.FluidProperty;
import net.dries007.tfc.common.fluids.IFluidLoggable;
import net.dries007.tfc.common.fluids.TFCFluids;

public abstract class TFCKelpTopBlock extends TopPlantBlock implements IFluidLoggable
{
    public static TFCKelpTopBlock create(AbstractBlock.Properties properties, Supplier<? extends Block> bodyBlock, Direction direction, VoxelShape shape, FluidProperty fluid)
    {
        return new TFCKelpTopBlock(properties, bodyBlock, direction, shape)
        {
            @Override
            public FluidProperty getFluidPropertyAbstract()
            {
                return fluid;
            }

            @Override
            public Supplier<? extends Block> getBodyBlockAbstract()
            {
                return bodyBlock;
            }
        };
    }

    protected TFCKelpTopBlock(AbstractBlock.Properties properties, Supplier<? extends Block> bodyBlock, Direction direction, VoxelShape shape)
    {
        super(properties, bodyBlock, direction, shape);
    }

    @Override
    public BlockState getStateForPlacement(BlockItemUseContext context)
    {
        World world = context.getLevel();
        BlockState state = defaultBlockState().setValue(AGE, world.getRandom().nextInt(25));
        FluidState fluidState = world.getFluidState(context.getClickedPos());
        if (getFluidProperty().canContain(fluidState.getType()))
        {
            return state.setValue(getFluidProperty(), getFluidProperty().keyFor(fluidState.getType()));
        }
        return null;
    }

    //Dear Alcatraz; I'm not sure what this does
    //I'm pretty sure this is where it converts the top to the body block so I'm putting the bit of extra handling here for that
    //Basically the Abstract tall plant system believes that we're _not_ waterlogged (so that we can use our own fluid logging)
    //It takes 'waterlogged' as a boolean in the constructor which I hardcode to being false in the superclass
    //So I have to recreate their handling at the top level essentially
    @Override
    public BlockState updateShape(BlockState stateIn, Direction facing, BlockState facingState, IWorld worldIn, BlockPos currentPos, BlockPos facingPos)
    {
        if (facing == growthDirection.getOpposite() && !stateIn.canSurvive(worldIn, currentPos))
        {
            worldIn.getBlockTicks().scheduleTick(currentPos, this, 1);
        }
        if (facing != growthDirection || !facingState.is(this) && !facingState.is(getBodyBlock()))
        {
            //Not sure if this is necessary
            worldIn.getLiquidTicks().scheduleTick(currentPos, TFCFluids.SALT_WATER.getSource(), Fluids.WATER.getTickDelay(worldIn));
            worldIn.getLiquidTicks().scheduleTick(currentPos, Fluids.WATER, Fluids.WATER.getTickDelay(worldIn));
            return super.updateShape(stateIn, facing, facingState, worldIn, currentPos, facingPos);
        }
        else// this is where it converts the top block to a body block when it gets placed on top of another top block
        {
            return this.getBodyBlock().defaultBlockState().setValue(getFluidProperty(), stateIn.getValue(getFluidProperty()));
        }
    }

    @Override
    protected boolean canGrowInto(BlockState state)
    {
        Fluid fluid = state.getFluidState().getType().getFluid();
        return getFluidProperty().canContain(fluid) && fluid != Fluids.EMPTY;
    }

    @Override
    protected boolean canAttachToBlock(Block blockIn)
    {
        return blockIn != Blocks.MAGMA_BLOCK;
    }

    @Override
    @SuppressWarnings("deprecation")
    public FluidState getFluidState(BlockState state)
    {
        return IFluidLoggable.super.getFluidState(state);
    }

    @Override
    protected void createBlockStateDefinition(StateContainer.Builder<Block, BlockState> builder)
    {
        super.createBlockStateDefinition(builder);
        builder.add(getFluidProperty());
    }

    @Override
    public boolean canPlaceLiquid(IBlockReader worldIn, BlockPos pos, BlockState state, Fluid fluidIn)
    {
        return false;
    }

    @Override
    public boolean placeLiquid(IWorld worldIn, BlockPos pos, BlockState state, FluidState fluidStateIn)
    {
        return false;
    }

    @Override
    public FluidProperty getFluidProperty()
    {
        return getFluidPropertyAbstract();
    }

    @Override
    protected AbstractBodyPlantBlock getBodyBlock()
    {
        return (AbstractBodyPlantBlock) getBodyBlockAbstract().get();
    }

    public abstract FluidProperty getFluidPropertyAbstract();

    public abstract Supplier<? extends Block> getBodyBlockAbstract();

    @Override
    public AbstractBlock.OffsetType getOffsetType() {
        return AbstractBlock.OffsetType.XZ;
    }

    @Override
    public VoxelShape getShape(BlockState state, IBlockReader worldIn, BlockPos pos, ISelectionContext context)
    {
        VoxelShape voxelshape = super.getShape(state, worldIn, pos, context);
        Vector3d vector3d = state.getOffset(worldIn, pos);
        return voxelshape.move(vector3d.x, vector3d.y, vector3d.z);
    }
}