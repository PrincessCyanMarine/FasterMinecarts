package net.cyanmarine.fast_minecarts.mixin;

import net.cyanmarine.fast_minecarts.FastMinecarts;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.block.RailBlock;
import net.minecraft.block.enums.RailShape;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.vehicle.AbstractMinecartEntity;
import net.minecraft.text.Text;
import net.minecraft.util.math.Direction;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.GameRules;
import net.minecraft.world.World;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import static net.cyanmarine.fast_minecarts.FastMinecarts.slowDown;

@Mixin(AbstractMinecartEntity.class)
public abstract class AbstractMinecartEntityMixin {
    @Shadow protected abstract double getMaxSpeed();

    AbstractMinecartEntity minecart = (AbstractMinecartEntity) (Object) this;

    @Inject(at = @At("RETURN"), method = "Lnet/minecraft/entity/vehicle/AbstractMinecartEntity;getMaxSpeed()D", cancellable = true)
    private void getMaxSpeedInject(CallbackInfoReturnable<Double> cir) {
        GameRules gamerules = minecart.world.getGameRules();
        if (!slowDown(minecart))
            cir.setReturnValue(gamerules.get(FastMinecarts.MINECART_MAX_SPEED).get() / 20.0);
    }

    @Inject(at = @At("HEAD"), method = "Lnet/minecraft/entity/vehicle/AbstractMinecartEntity;tick()V")
    private void tickInjectHead(CallbackInfo ci) {
        double d = this.getMaxSpeed();
        Vec3d vec3d = minecart.getVelocity();
        minecart.setVelocity(MathHelper.clamp(vec3d.x, -d, d), vec3d.y, MathHelper.clamp(vec3d.z, -d, d));
    }

    @Inject(at = @At("TAIL"), method = "Lnet/minecraft/entity/vehicle/AbstractMinecartEntity;tick()V")
    private void tickInjectTail(CallbackInfo ci) {
        GameRules gamerules = minecart.world.getGameRules();
        if (gamerules.getBoolean(FastMinecarts.SPEEDOMETER))
            minecart.getPassengerList().forEach(passenger -> {
                if (passenger instanceof PlayerEntity)
                    ((PlayerEntity) passenger).sendMessage(Text.literal(String.format("%.2f", minecart.getVelocity().length() * 20) + "/" + String.format("%.2f", getMaxSpeed() * 20)), true);
            });
    }
}
