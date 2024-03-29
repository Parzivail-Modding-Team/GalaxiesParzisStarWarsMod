package com.parzivail.pswg.item;

import com.parzivail.pswg.Resources;
import com.parzivail.pswg.container.SwgBlocks;
import com.parzivail.pswg.container.SwgEntities;
import com.parzivail.pswg.container.SwgItems;
import com.parzivail.pswg.entity.FragmentationGrenadeEntity;
import com.parzivail.pswg.entity.ThermalDetonatorEntity;
import com.parzivail.tarkin.api.TarkinLang;
import com.parzivail.util.client.TextUtil;
import com.parzivail.util.item.ICooldownItem;
import com.parzivail.util.item.ICustomVisualItemEquality;
import com.parzivail.util.item.IDefaultNbtProvider;
import com.parzivail.util.item.ILeftClickConsumer;
import net.minecraft.block.Block;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.item.TooltipContext;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.network.packet.s2c.play.EntitySpawnS2CPacket;
import net.minecraft.text.Text;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;
import org.jetbrains.annotations.Nullable;

import java.util.List;

public class FragmentationGrenadeItem extends ThrowableExplosiveItem
{
	@TarkinLang
	public static final String I18N_TOOLTIP_CONTROLS = Resources.tooltip("fragmentation_grenade.controls");

	public FragmentationGrenadeItem(Settings settings)
	{
		this(settings, SwgBlocks.Misc.FragmentationGrenadeBlock);
	}

	public FragmentationGrenadeItem(Settings settings, Block block)
	{
		super(settings, block, SwgItems.Explosives.FragmentationGrenade, new FragmentationGrenadeSoundGroup());
	}

	@Override
	public void throwEntity(World world, ThrowableExplosiveTag tag, ItemStack stack, PlayerEntity player)
	{
		FragmentationGrenadeEntity fg = new FragmentationGrenadeEntity(SwgEntities.Misc.FragmentationGrenade, world);
		fg.setLife(tag.ticksToExplosion);
		fg.setPrimed(tag.primed);
		fg.setVisible(true);
		fg.onSpawnPacket(new EntitySpawnS2CPacket(fg.getId(), fg.getUuid(), player.getX(), player.getY() + 1, player.getZ(), -player.getPitch(), -player.getYaw(), fg.getType(), 0, Vec3d.ZERO, player.getHeadYaw()));
		fg.setOwner(player);
		fg.setVelocity(player, player.getPitch(), player.getYaw(), player.getRoll(), 1.0F, 0F);

		world.spawnEntity(fg);
	}

	@Override
	public void spawnEntity(World world, int power, int life, boolean primed, PlayerEntity player)
	{
		FragmentationGrenadeEntity fg = new FragmentationGrenadeEntity(SwgEntities.Misc.FragmentationGrenade, world);
		fg.setLife(life);
		fg.setPrimed(primed);
		fg.setExplosionPower(power);
		fg.onSpawnPacket(new EntitySpawnS2CPacket(fg.getId(), fg.getUuid(), player.getX(), player.getY() + 1, player.getZ(), -player.getPitch(), -player.getYaw(), fg.getType(), 0, Vec3d.ZERO, player.getHeadYaw()));
		world.spawnEntity(fg);
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
