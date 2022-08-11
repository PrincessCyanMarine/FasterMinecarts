package net.cyanmarine.fast_minecarts.mixin;

import net.cyanmarine.fast_minecarts.FastMinecarts;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.PoweredRailBlock;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(PoweredRailBlock.class)
public class PoweredRailBlockMixin {
    @Inject(cancellable = true, at = @At("HEAD"), method = "isPoweredByOtherRails(Lnet/minecraft/world/World;Lnet/minecraft/util/math/BlockPos;Lnet/minecraft/block/BlockState;ZI)Z")
    void isPoweredByOtherRailsInject(World world, BlockPos pos, BlockState state, boolean bl, int distance, CallbackInfoReturnable<Boolean> cir) {
        if (world.getGameRules().getBoolean(FastMinecarts.ALWAYS_POWERED)) cir.setReturnValue(true);
    }
}
