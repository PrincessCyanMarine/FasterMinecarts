package net.cyanmarine.fast_minecarts.mixin;

import net.cyanmarine.fast_minecarts.FastMinecarts;
import net.minecraft.entity.vehicle.FurnaceMinecartEntity;
import net.minecraft.world.GameRules;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import static net.cyanmarine.fast_minecarts.FastMinecarts.slowDown;

@Mixin(FurnaceMinecartEntity.class)
public class FurnaceMinecartEntityMixin {
    FurnaceMinecartEntity minecart = ((FurnaceMinecartEntity) (Object) this);

    @Inject(at = @At("RETURN"), method = "Lnet/minecraft/entity/vehicle/FurnaceMinecartEntity;getMaxSpeed()D", cancellable = true)
    private void getMaxSpeedInject(CallbackInfoReturnable<Double> cir) {
        GameRules gamerules = minecart.world.getGameRules();
        if (!slowDown(minecart))
            cir.setReturnValue(gamerules.get(FastMinecarts.MINECART_MAX_SPEED).get() / 20.0);
    }
}
