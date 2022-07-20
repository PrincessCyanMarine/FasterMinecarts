package net.cyanmarine.fast_minecarts;

import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.gamerule.v1.CustomGameRuleCategory;
import net.fabricmc.fabric.api.gamerule.v1.GameRuleFactory;
import net.fabricmc.fabric.api.gamerule.v1.GameRuleRegistry;
import net.fabricmc.fabric.api.gamerule.v1.rule.DoubleRule;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.block.RailBlock;
import net.minecraft.block.enums.RailShape;
import net.minecraft.entity.vehicle.AbstractMinecartEntity;
import net.minecraft.text.Text;
import net.minecraft.util.Identifier;
import net.minecraft.world.GameRules;
import net.minecraft.world.World;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class FastMinecarts implements ModInitializer {
    public static final String MOD_ID = "fast_minecarts";
    public static Identifier getId(String name) { return new Identifier(MOD_ID, name); }
    public static final Logger LOGGER = LoggerFactory.getLogger(MOD_ID);

    public static final CustomGameRuleCategory GAME_RULE_CATEGORY = new CustomGameRuleCategory(getId("gamerules"), Text.translatable("gamerules.fast_minecarts"));
    public static final GameRules.Key<DoubleRule> MINECART_MAX_SPEED = GameRuleRegistry.register("minecartMaxSpeed", GAME_RULE_CATEGORY, GameRuleFactory.createDoubleRule(30.0, 0.0, 30.0));
    public static final GameRules.Key<GameRules.BooleanRule> SOUL_SAND_SLOWDOWN = GameRuleRegistry.register("minecartSlowdownOnSoulSand", GAME_RULE_CATEGORY, GameRuleFactory.createBooleanRule(true));
    public static final GameRules.Key<GameRules.BooleanRule> SLIMEBLOCK_SLOWDOWN = GameRuleRegistry.register("minecartSlowdownOnSlimeblock", GAME_RULE_CATEGORY, GameRuleFactory.createBooleanRule(true));
    public static final GameRules.Key<GameRules.BooleanRule> CURVE_SLOWDOWN = GameRuleRegistry.register("minecartSlowdownOnCurves", GAME_RULE_CATEGORY, GameRuleFactory.createBooleanRule(true));
    public static final GameRules.Key<GameRules.BooleanRule> NAMED_SLOW_SLOWDOWN = GameRuleRegistry.register("minecartSlowdownIfNamedSlow", GAME_RULE_CATEGORY, GameRuleFactory.createBooleanRule(true));
    public static final GameRules.Key<GameRules.BooleanRule> SPEEDOMETER = GameRuleRegistry.register("minecartSpeedometer", GAME_RULE_CATEGORY, GameRuleFactory.createBooleanRule(false));

    @Override
    public void onInitialize() {
        LOGGER.info("Fast Minecarts is installed");
    }

    public static boolean slowDown(AbstractMinecartEntity minecart) {
        World world = minecart.world;
        BlockState underneath = world.getBlockState(minecart.getBlockPos().down());
        GameRules gamerules = world.getGameRules();

        if (gamerules.getBoolean(SOUL_SAND_SLOWDOWN) && underneath.isOf(Blocks.SOUL_SAND)) return true;
        if (gamerules.getBoolean(SLIMEBLOCK_SLOWDOWN) && underneath.isOf(Blocks.SLIME_BLOCK)) return true;
        if (gamerules.getBoolean(CURVE_SLOWDOWN)) {
            BlockState rail = world.getBlockState(minecart.getBlockPos());
            if (rail.isOf(Blocks.RAIL) && isCurved(rail.get(RailBlock.SHAPE))) return true;
            BlockState next = world.getBlockState(minecart.getBlockPos().add(minecart.getMovementDirection().getVector()));
            if (next.isOf(Blocks.RAIL) && isCurved(next.get(RailBlock.SHAPE))) return true;
        }

        if (gamerules.getBoolean(NAMED_SLOW_SLOWDOWN)) {
            Text customName = minecart.getCustomName();
            if (customName != null && customName.getString().toLowerCase().contains("slow")) return true;
        }

        return false;
    }

    public static boolean isCurved(RailShape shape) {
        return shape == RailShape.NORTH_EAST || shape == RailShape.NORTH_WEST || shape == RailShape.SOUTH_EAST || shape == RailShape.SOUTH_WEST;
    }
}
