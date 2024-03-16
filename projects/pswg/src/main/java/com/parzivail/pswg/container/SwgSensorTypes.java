package com.parzivail.pswg.container;

import com.google.common.collect.Maps;
import com.parzivail.pswg.Resources;
import com.parzivail.pswg.entity.amphibian.WorrtAttackablesSensor;
import com.parzivail.pswg.entity.amphibian.WorrtSpecificSensor;
import com.parzivail.pswg.entity.mammal.BanthaBrain;
import com.parzivail.pswg.entity.rodent.SandSkitterSpecificSensor;
import net.minecraft.entity.ai.brain.sensor.Sensor;
import net.minecraft.entity.ai.brain.sensor.SensorType;
import net.minecraft.entity.ai.brain.sensor.TemptationsSensor;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;
import net.minecraft.util.Identifier;

import java.util.Map;
import java.util.function.Supplier;

public class SwgSensorTypes
{
	public static final Map<Identifier, SensorType<?>> SENSOR_TYPES = Maps.newLinkedHashMap();

	public static final SensorType<TemptationsSensor> BANTHA_TEMPTATIONS = register("bantha_temptations", () -> new TemptationsSensor(BanthaBrain.getTemptItems()));
	public static final SensorType<SandSkitterSpecificSensor> SAND_SKITTER_SPECIFIC_SENSOR = register("sand_skitter_specific_sensor", SandSkitterSpecificSensor::new);
	public static final SensorType<WorrtSpecificSensor> WORRT_SPECIFIC_SENSOR = register("worrt_specific_sensor", WorrtSpecificSensor::new);
	public static final SensorType<WorrtAttackablesSensor> WORRT_ATTACKABLES_SENSOR = register("worrt_attackables", WorrtAttackablesSensor::new);

	private static <U extends Sensor<?>> SensorType<U> register(String string, Supplier<U> supplier) {
		SensorType<U> sensorType = new SensorType<>(supplier);
		SENSOR_TYPES.put(Resources.id(string), sensorType);
		return sensorType;
	}

	public static void register()
	{
		SENSOR_TYPES.forEach((resourceLocation, sensorType) -> Registry.register(Registries.SENSOR_TYPE, resourceLocation, sensorType));
	}

}
