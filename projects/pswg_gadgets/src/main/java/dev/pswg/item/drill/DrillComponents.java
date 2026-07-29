package dev.pswg.item.drill;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import dev.pswg.Gadgets;
import net.minecraft.resources.Identifier;

import java.util.HashMap;

public class DrillComponents
{

	public static final Extractor BASE_EXTRACTOR = new Extractor(Gadgets.id("base_extractor"), 1f, 1f, 1f);
	public static final Extractor LASER_EXTRACTOR = new Extractor(Gadgets.id("laser_extractor"), 2.5f, 0.75f, 0.5f);
	public static final Extractor PLASTEEL_EXTRACTOR = new Extractor(Gadgets.id("plasteel_extractor"), 1f, 1.65f, 0.35f);

	public static final Drill BASE_DRILL = new Drill(Gadgets.id("base_drill"), 2f, 3, 600);
	public static final Drill TITANIUM_DRILL = new Drill(Gadgets.id("titanium_drill"), 2.5f, 5, 900);
	public static final Drill PLASTEEL_DRILL = new Drill(Gadgets.id("plasteel_drill"), 3.5f, 3, 300);

	public static final Capsule BASE_CAPSULE = new Capsule(Gadgets.id("base_capsule"), 4, 200);
	public static final Capsule TRANSPARISTEEL_CAPSULE = new Capsule(Gadgets.id("transparisteel_capsule"), 6, -100);
	public static final Capsule REINFORCED_CAPSULE = new Capsule(Gadgets.id("reinforced_capsule"), 5,  300);

	public static void register(){
	}

	public static class Extractor{
		public static final Codec<Extractor> CODEC = RecordCodecBuilder.create(extractorInstance -> extractorInstance.group(
			Identifier.CODEC.fieldOf("identifier").forGetter(Extractor::id),
			Codec.FLOAT.fieldOf("extractionMultiplier").forGetter(Extractor::getExtractionMultiplier),
			Codec.FLOAT.fieldOf("extractionSpeed").forGetter(Extractor::getExtractionSpeed),
			Codec.FLOAT.fieldOf("durabilityModifier").forGetter(Extractor::getDurabilityModifier)
		).apply(extractorInstance, Extractor::new)
		);

		Identifier id;
		float extractionMultiplier;
		float extractionSpeed;
		float durabilityModifier;

		public Extractor(Identifier identifier, float extractionMultiplier, float extractionSpeed, float durabilityModifier){
			this.id = identifier;
			this.extractionMultiplier = extractionMultiplier;
			this.extractionSpeed = extractionSpeed;
			this.durabilityModifier = durabilityModifier;
		}
		public Identifier id(){
			return id;
		}
		public float getExtractionMultiplier()
		{
			return extractionMultiplier;
		}

		public float getExtractionSpeed()
		{
			return extractionSpeed;
		}

		public float getDurabilityModifier()
		{
			return durabilityModifier;
		}
	}

	public static class Capsule{
		public static final Codec<Capsule> CODEC = RecordCodecBuilder.create(capsuleInstance -> capsuleInstance.group(
				Identifier.CODEC.fieldOf("identifier").forGetter(Capsule::id),
				Codec.INT.fieldOf("maxBlockCount").forGetter(Capsule::getMaxBlockCount),
				Codec.INT.fieldOf("durabilityBoost").forGetter(Capsule::getDurabilityBoost)
				).apply(capsuleInstance, Capsule::new)
		);

		Identifier id;
		int maxBlockCount;
		int durabilityBoost;


		public Capsule(Identifier identifier, int maxBlockCount, int durabilityBoost){
			this.id = identifier;
			this.maxBlockCount = maxBlockCount;
			this.durabilityBoost = durabilityBoost;
		}
		public Identifier id(){
			return id;
		}
		public int getDurabilityBoost()
		{
			return durabilityBoost;
		}

		public int getMaxBlockCount()
		{
			return maxBlockCount;
		}
	}
	public static class Drill{
		public static final Codec<Drill> CODEC = RecordCodecBuilder.create(drillInstance -> drillInstance.group(
				Identifier.CODEC.fieldOf("identifier").forGetter(Drill::id),
				Codec.FLOAT.fieldOf("miningSpeed").forGetter(Drill::getMiningSpeed),
				Codec.INT.fieldOf("mininsgLevel").forGetter(Drill::getMiningLevel),
				Codec.INT.fieldOf("durabilityBase").forGetter(Drill::getDurabilityBase)
		).apply(drillInstance, Drill::new));

		Identifier id;
		float miningSpeed;
		int miningLevel;
		int durabilityBase;

		public Drill(Identifier identifier, float miningSpeed, int miningLevel, int durabilityBase){
			this.id = identifier;
			this.miningSpeed = miningSpeed;
			this.miningLevel = miningLevel;
			this.durabilityBase = durabilityBase;
		}
		public Identifier id(){
			return id;
		}
		public float getMiningSpeed()
		{
			return miningSpeed;
		}

		public int getMiningLevel()
		{
			return miningLevel;
		}

		public int getDurabilityBase()
		{
			return durabilityBase;
		}

	}
}
