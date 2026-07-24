package dev.pswg.item.drill;

import java.util.HashMap;

public class DrillComponents
{

	public static HashMap<String, Extractor> extractorMap = new HashMap<>();
	public static HashMap<String, Drill> drillMap = new HashMap<>();
	public static HashMap<String, Capsule> capsuleMap = new HashMap<>();

	public static void register(){
		extractorMap.put("baseExtractor", new Extractor(1f, 1f, 1f));
		extractorMap.put("laserExtractor", new Extractor(2.5f, 0.75f, 0.5f));
		extractorMap.put("plasteelExtractor", new Extractor(1f, 1.65f, 0.35f));

		drillMap.put("baseDrill", new Drill(2 * 1f, 3, 600));
		drillMap.put("titaniumDrill", new Drill(2 * 1.25f, 4, 900));
		drillMap.put("plasteelDrill", new Drill(2 * 1.75f, 3, 300));

		capsuleMap.put("baseCapsule", new Capsule(5, 0));
		capsuleMap.put("transparisteelCapsule", new Capsule(6, -100));
		capsuleMap.put("reinforcedCapsule", new Capsule(4, 200));
	}
	public static Extractor getDefaultExtractor(){
		return extractorMap.get("baseExtractor");
	}
	public static Drill getDefaultDrill(){
		return drillMap.get("baseDrill");
	}
	public static Capsule getDefaultCapsule(){
		return capsuleMap.get("baseCapsule");
	}

	public static class Extractor{
		float extractionMultiplier;
		float extractionSpeed;
		float durabilityModifier;

		public Extractor(float extractionMultiplier, float extractionSpeed, float durabilityModifier){
			this.extractionMultiplier = extractionMultiplier;
			this.extractionSpeed = extractionSpeed;
			this.durabilityModifier = durabilityModifier;
		}
	}

	public static class Capsule{
		int maxBlockCount;
		int durabilityBoost;

		public Capsule(int maxBlockCount, int durabilityBoost){
			this.maxBlockCount = maxBlockCount;
			this.durabilityBoost = durabilityBoost;
		}
	}
	public static class Drill{
		float miningSpeed;
		int miningLevel;
		int durabilityBase;

		public Drill(float miningSpeed, int miningLevel, int durabilityBase){
			this.miningSpeed = miningSpeed;
			this.miningLevel = miningLevel;
			this.durabilityBase = durabilityBase;
		}
	}
}
