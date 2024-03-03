package com.parzivail.pswg.item;

import com.parzivail.pswg.Resources;
import com.parzivail.pswg.container.*;
import com.parzivail.pswg.entity.ThermalDetonatorEntity;
import com.parzivail.tarkin.api.TarkinLang;
import com.parzivail.util.client.TextUtil;
import com.parzivail.util.item.ICooldownItem;
import com.parzivail.util.item.ICustomVisualItemEquality;
import com.parzivail.util.item.IDefaultNbtProvider;
import com.parzivail.util.item.ILeftClickConsumer;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.item.TooltipContext;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.*;
import net.minecraft.network.packet.s2c.play.EntitySpawnS2CPacket;
import net.minecraft.text.Text;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;
import org.jetbrains.annotations.Nullable;

import java.util.List;

public class ThermalDetonatorItem extends ThrowableExplosiveItem implements ILeftClickConsumer, IDefaultNbtProvider, ICooldownItem, ICustomVisualItemEquality
{
	@TarkinLang
	public static final String I18N_TOOLTIP_CONTROLS = Resources.tooltip("thermal_detonator.controls");

	public ThermalDetonatorItem(Settings settings)
	{
		super(settings, SwgBlocks.Misc.ThermalDetonatorBlock, SwgItems.Explosives.ThermalDetonator, new ThermalDetonatorSoundGroup());
	}

	@Override
	public void throwEntity(World world, ThrowableExplosiveTag tag, ItemStack stack, PlayerEntity player)
	{
		ThermalDetonatorEntity td = new ThermalDetonatorEntity(SwgEntities.Misc.ThermalDetonator, world);
		td.setLife(tag.ticksToExplosion);
		td.setPrimed(tag.primed);
		td.setVisible(true);
		td.onSpawnPacket(new EntitySpawnS2CPacket(td.getId(), td.getUuid(), player.getX(), player.getY() + 1, player.getZ(), -player.getPitch(), -player.getYaw(), td.getType(), 0, Vec3d.ZERO, player.getHeadYaw()));
		td.setOwner(player);
		td.setVelocity(player, player.getPitch(), player.getYaw(), player.getRoll(), 1.0F, 0F);

		world.spawnEntity(td);
	}

	@Override
	public void spawnEntity(World world, int power, int life, boolean primed, PlayerEntity player)
	{
		ThermalDetonatorEntity td = new ThermalDetonatorEntity(SwgEntities.Misc.ThermalDetonator, world);
		td.setLife(life);
		td.setPrimed(primed);
		td.setExplosionPower(power);
		td.onSpawnPacket(new EntitySpawnS2CPacket(td.getId(), td.getUuid(), player.getX(), player.getY() + 1, player.getZ(), -player.getPitch(), -player.getYaw(), td.getType(), 0, Vec3d.ZERO, player.getHeadYaw()));
		world.spawnEntity(td);
	}

	@Override
	public void appendTooltip(ItemStack stack, @Nullable World world, List<Text> tooltip, TooltipContext context)
	{
		super.appendTooltip(stack, world, tooltip, context);

		var mc = MinecraftClient.getInstance();
		var opt = mc.options;
		tooltip.add(Text.translatable(I18N_TOOLTIP_CONTROLS, TextUtil.stylizeKeybind(opt.attackKey.getBoundKeyLocalizedText()), TextUtil.stylizeKeybind(opt.useKey.getBoundKeyLocalizedText())));
	}
}
