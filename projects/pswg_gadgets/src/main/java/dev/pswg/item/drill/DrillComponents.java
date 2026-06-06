package dev.pswg.item.drill;

import java.util.HashMap;

public class DrillComponents
{

	public static HashMap<String, Extractor> extractorMap = new HashMap<>();
	public static HashMap<String, Drill> drillMap = new HashMap<>();
	public static HashMap<String, Capsule> capsuleMap = new HashMap<>();

	public static void register(){
		extractorMap.put("baseExtractor", new Extractor(1f, 1f, 1f));
		extractorMap.put("laserExtractor", new Extractor(2f, 0.75f, 0.5f));

		drillMap.put("baseDrill", new Drill(1f, 3, 300));

		capsuleMap.put("baseCapsule", new Capsule(4, 0));
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
