package com.parzivail.pswg.mixin;

import com.parzivail.pswg.client.input.IJetpackControlContainer;
import com.parzivail.pswg.client.input.JetpackControls;
import net.minecraft.entity.LivingEntity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;

import java.util.EnumSet;

@Mixin(LivingEntity.class)
public class LivingEntityAccessor implements IJetpackControlContainer
{
	@Unique
	public short jetpackControls;

	@Override
	public void pswg_setJetpackControls(EnumSet<JetpackControls> controls)
	{
		this.jetpackControls = JetpackControls.pack(controls);
	}

	@Override
	public EnumSet<JetpackControls> pswg_getJetpackControls()
	{
		return JetpackControls.unpack(this.jetpackControls);
	}
}
