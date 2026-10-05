package dev.pswg.codecgenerator;

import com.google.auto.service.AutoService;
import com.palantir.javapoet.*;

import javax.annotation.processing.*;
import javax.lang.model.SourceVersion;
import javax.lang.model.element.*;
import javax.lang.model.type.DeclaredType;
import javax.lang.model.type.MirroredTypeException;
import javax.lang.model.type.TypeKind;
import javax.lang.model.type.TypeMirror;
import javax.tools.Diagnostic;

import java.io.IOException;
import java.util.*;
import java.util.function.Function;

/**
 * An annotation processor that generates record codecs as an
 * implementable interface that defines MAP_CODEC and CODEC fields and,
 * optionally, a PACKET_CODEC field.
 */
@AutoService(Processor.class)
@SupportedAnnotationTypes("dev.pswg.codecgenerator.GenerateCodec")
@SupportedSourceVersion(SourceVersion.RELEASE_25)
public class CodecGenerationProcessor extends AbstractProcessor
{
	/**
	 * Represents a reference to a defined, pre-existing codec expression
	 *
	 * @param className   The class in which the codec is defined
	 * @param elementName The name of the codec field
	 * @param customExpression An adapted expression for codec types whose native type differs
	 */
	private record CodecType(TypeName className, String elementName, CodeBlock customExpression)
	{
		/**
		 * Creates a reference to a codec field.
		 */
		private CodecType(TypeName className, String elementName)
		{
			this(className, elementName, null);
		}

		/**
		 * Creates a codec reference whose expression requires an adaptation.
		 */
		private CodecType(CodeBlock customExpression)
		{
			this(null, null, customExpression);
		}

		/**
		 * Returns the Java expression that evaluates to the codec.
		 */
		private CodeBlock asExpression()
		{
			if (customExpression != null)
				return customExpression;

			return CodeBlock.of("$T.$L", className, elementName);
		}

		/**
		 * Returns a readable representation of the codec reference for processor logging.
		 */
		@Override
		public String toString()
		{
			return customExpression == null
					? "%s#%s".formatted(className(), elementName())
					: customExpression.toString();
		}
	}

	/**
	 * Holds a standard codec expression and whether it decodes an absent optional
	 * record field as {@link Optional#empty()}.
	 *
	 * @param expression The codec expression for the field value
	 * @param optionalField Whether the component uses {@code optionalFieldOf}
	 */
	private record StandardCodecExpression(CodeBlock expression, boolean optionalField)
	{
	}

	/**
	 * Contains a mapping between qualified type names and
	 * the "best fit" codec that may be automatically selected
	 */
	private static final HashMap<String, GenStandardCodec> defaultCodecTypes = new HashMap<>();

	/**
	 * Contains a mapping between qualified type names and
	 * all available codecs, each keyed by their enum constant
	 */
	private static final HashMap<String, Map<GenStandardCodec, CodecType>> codecTypes = new HashMap<>();

	/**
	 * Contains a mapping between qualified type names and
	 * the "best fit" packet codec that may be automatically selected
	 */
	private static final HashMap<String, GenPacketCodec> defaultPacketCodecTypes = new HashMap<>();

	/**
	 * Contains a mapping between qualified type names and
	 * all available packet codecs, each keyed by their enum
	 * constant
	 */
	private static final HashMap<String, Map<GenPacketCodec, CodecType>> packetCodecTypes = new HashMap<>();

	/**
	 * The core Minecraft codecs class
	 */
	private static final TypeName MC_TYPES = ClassName.get("net.minecraft.util", "ExtraCodecs");
	private static final TypeName MC_PACKET_TYPES = ClassName.get("net.minecraft.network.codec", "ByteBufCodecs");

	/**
	 * The core PSWG codec class
	 */
	private static final TypeName GALAXIES_CODECS = ClassName.get("dev.pswg.codec", "GalaxiesCodecs");

	/**
	 * Initializes the standard and packet codec type registries.
	 */
	public CodecGenerationProcessor()
	{
		var mojangTypes = ClassName.get("com.mojang.serialization", "Codec");

		registerCodecsForType(
				"boolean",
				GenStandardCodec.BOOL,
				Map.of(
						GenStandardCodec.BOOL, new CodecType(mojangTypes, "BOOL")
				)
		);
		registerCodecsForType(
				"byte",
				GenStandardCodec.BYTE,
				Map.of(
						GenStandardCodec.BYTE, new CodecType(mojangTypes, "BYTE"),
						GenStandardCodec.UNSIGNED_BYTE, new CodecType(MC_TYPES, "UNSIGNED_BYTE")
				)
		);
		registerCodecsForType(
				"short",
				GenStandardCodec.SHORT,
				Map.of(
						GenStandardCodec.SHORT, new CodecType(mojangTypes, "SHORT")
				)
		);
		registerCodecsForType(
				"int",
				GenStandardCodec.INT,
				Map.of(
						GenStandardCodec.INT, new CodecType(mojangTypes, "INT"),
						GenStandardCodec.RGB, new CodecType(MC_TYPES, "RGB_COLOR_CODEC"),
						GenStandardCodec.ARGB, new CodecType(MC_TYPES, "ARGB_COLOR_CODEC"),
						GenStandardCodec.NON_NEGATIVE_INT, new CodecType(MC_TYPES, "NON_NEGATIVE_INT"),
						GenStandardCodec.POSITIVE_INT, new CodecType(MC_TYPES, "POSITIVE_INT"),
						GenStandardCodec.UNICODE_CODEPOINT, new CodecType(MC_TYPES, "CODEPOINT")
				)
		);
		registerCodecsForType(
				"long",
				GenStandardCodec.LONG,
				Map.of(
						GenStandardCodec.LONG, new CodecType(mojangTypes, "LONG")
				)
		);
		registerCodecsForType(
				"float",
				GenStandardCodec.FLOAT,
				Map.of(
						GenStandardCodec.FLOAT, new CodecType(mojangTypes, "FLOAT"),
						GenStandardCodec.NON_NEGATIVE_FLOAT, new CodecType(MC_TYPES, "NON_NEGATIVE_FLOAT"),
						GenStandardCodec.POSITIVE_FLOAT, new CodecType(MC_TYPES, "POSITIVE_FLOAT")
				)
		);
		registerCodecsForType(
				"double",
				GenStandardCodec.DOUBLE,
				Map.of(
						GenStandardCodec.DOUBLE, new CodecType(mojangTypes, "DOUBLE")
				)
		);
		registerCodecsForType("java.lang.Boolean", GenStandardCodec.BOOL, Map.of(GenStandardCodec.BOOL, new CodecType(mojangTypes, "BOOL")));
		registerCodecsForType("java.lang.Byte", GenStandardCodec.BYTE, Map.of(
				GenStandardCodec.BYTE, new CodecType(mojangTypes, "BYTE"),
				GenStandardCodec.UNSIGNED_BYTE, new CodecType(MC_TYPES, "UNSIGNED_BYTE")
		));
		registerCodecsForType("java.lang.Short", GenStandardCodec.SHORT, Map.of(GenStandardCodec.SHORT, new CodecType(mojangTypes, "SHORT")));
		registerCodecsForType("java.lang.Integer", GenStandardCodec.INT, Map.of(
				GenStandardCodec.INT, new CodecType(mojangTypes, "INT"),
				GenStandardCodec.RGB, new CodecType(MC_TYPES, "RGB_COLOR_CODEC"),
				GenStandardCodec.ARGB, new CodecType(MC_TYPES, "ARGB_COLOR_CODEC"),
				GenStandardCodec.NON_NEGATIVE_INT, new CodecType(MC_TYPES, "NON_NEGATIVE_INT"),
				GenStandardCodec.POSITIVE_INT, new CodecType(MC_TYPES, "POSITIVE_INT"),
				GenStandardCodec.UNICODE_CODEPOINT, new CodecType(MC_TYPES, "CODEPOINT")
		));
		registerCodecsForType("java.lang.Long", GenStandardCodec.LONG, Map.of(GenStandardCodec.LONG, new CodecType(mojangTypes, "LONG")));
		registerCodecsForType("java.lang.Float", GenStandardCodec.FLOAT, Map.of(
				GenStandardCodec.FLOAT, new CodecType(mojangTypes, "FLOAT"),
				GenStandardCodec.NON_NEGATIVE_FLOAT, new CodecType(MC_TYPES, "NON_NEGATIVE_FLOAT"),
				GenStandardCodec.POSITIVE_FLOAT, new CodecType(MC_TYPES, "POSITIVE_FLOAT")
		));
		registerCodecsForType("java.lang.Double", GenStandardCodec.DOUBLE, Map.of(GenStandardCodec.DOUBLE, new CodecType(mojangTypes, "DOUBLE")));
		registerCodecsForType(
				"java.lang.String",
				GenStandardCodec.STRING,
				Map.of(
						GenStandardCodec.STRING, new CodecType(mojangTypes, "STRING"),
						GenStandardCodec.ESCAPED_STRING, new CodecType(MC_TYPES, "ESCAPED_STRING"),
						GenStandardCodec.PLAYER_NAME, new CodecType(MC_TYPES, "PLAYER_NAME"),
						GenStandardCodec.NON_EMPTY_STRING, new CodecType(MC_TYPES, "NON_EMPTY_STRING"),
						GenStandardCodec.IDENTIFIER_PATH, new CodecType(MC_TYPES, "IDENTIFIER_PATH")
				)
		);
		registerCodecsForType(
				"java.nio.ByteBuffer",
				GenStandardCodec.BYTE_BUFFER,
				Map.of(
						GenStandardCodec.BYTE_BUFFER, new CodecType(mojangTypes, "BYTE_BUFFER")
				)
		);
		registerCodecsForType(
				"java.util.stream.IntStream",
				GenStandardCodec.INT_STREAM,
				Map.of(
						GenStandardCodec.INT_STREAM, new CodecType(mojangTypes, "INT_STREAM")
				)
		);
		registerCodecsForType(
				"java.util.stream.LongStream",
				GenStandardCodec.LONG_STREAM,
				Map.of(
						GenStandardCodec.LONG_STREAM, new CodecType(mojangTypes, "LONG_STREAM")
				)
		);
		registerCodecsForType(
				"com.google.gson.JsonElement",
				GenStandardCodec.JSON_ELEMENT,
				Map.of(
						GenStandardCodec.JSON_ELEMENT, new CodecType(MC_TYPES, "JSON_ELEMENT")
				)
		);
		registerCodecsForType(
				"org.joml.Vector3f",
				GenStandardCodec.VECTOR_3F,
				Map.of(
						GenStandardCodec.VECTOR_3F, new CodecType(CodeBlock.of(
								"$T.VECTOR3F.xmap($T::new, value -> value)",
								MC_TYPES,
								ClassName.get("org.joml", "Vector3f")
						))
				)
		);
		registerCodecsForType(
				"org.joml.Vector3fc",
				GenStandardCodec.VECTOR_3F,
				Map.of(GenStandardCodec.VECTOR_3F, new CodecType(MC_TYPES, "VECTOR3F"))
		);
		registerCodecsForType(
				"org.joml.Vector4f",
				GenStandardCodec.VECTOR_4F,
				Map.of(
						GenStandardCodec.VECTOR_4F, new CodecType(CodeBlock.of(
								"$T.VECTOR4F.xmap($T::new, value -> value)",
								MC_TYPES,
								ClassName.get("org.joml", "Vector4f")
						))
				)
		);
		registerCodecsForType(
				"org.joml.Vector4fc",
				GenStandardCodec.VECTOR_4F,
				Map.of(GenStandardCodec.VECTOR_4F, new CodecType(MC_TYPES, "VECTOR4F"))
		);
		registerCodecsForType(
				"org.joml.Quaternionf",
				GenStandardCodec.QUATERNION_F,
				Map.of(
						GenStandardCodec.QUATERNION_F, new CodecType(CodeBlock.of(
								"$T.QUATERNIONF.xmap($T::new, value -> value)",
								MC_TYPES,
								ClassName.get("org.joml", "Quaternionf")
						)),
						GenStandardCodec.ROTATION, new CodecType(CodeBlock.of(
								"$T.QUATERNIONF.xmap($T::new, value -> value)",
								MC_TYPES,
								ClassName.get("org.joml", "Quaternionf")
						))
				)
		);
		registerCodecsForType(
				"org.joml.Quaternionfc",
				GenStandardCodec.QUATERNION_F,
				Map.of(
						GenStandardCodec.QUATERNION_F, new CodecType(MC_TYPES, "QUATERNIONF"),
						GenStandardCodec.ROTATION, new CodecType(MC_TYPES, "QUATERNIONF")
				)
		);
		registerCodecsForType(
				"org.joml.AxisAngle4f",
				GenStandardCodec.AXIS_ANGLE_4F,
				Map.of(
						GenStandardCodec.AXIS_ANGLE_4F, new CodecType(MC_TYPES, "AXISANGLE4F")
				)
		);
		registerCodecsForType(
				"org.joml.Matrix4f",
				GenStandardCodec.MATRIX_4F,
				Map.of(
						GenStandardCodec.MATRIX_4F, new CodecType(CodeBlock.of(
								"$T.MATRIX4F.xmap($T::new, value -> value)",
								MC_TYPES,
								ClassName.get("org.joml", "Matrix4f")
						))
				)
		);
		registerCodecsForType(
				"org.joml.Matrix4fc",
				GenStandardCodec.MATRIX_4F,
				Map.of(GenStandardCodec.MATRIX_4F, new CodecType(MC_TYPES, "MATRIX4F"))
		);
		registerCodecsForType(
				"java.time.Instant",
				GenStandardCodec.INSTANT,
				Map.of(
						GenStandardCodec.INSTANT, new CodecType(MC_TYPES, "INSTANT")
				)
		);
		registerCodecsForType(
				"net.minecraft.util.dynamic.Codecs.TagEntryId",
				GenStandardCodec.TAG_ENTRY_ID,
				Map.of(
						GenStandardCodec.TAG_ENTRY_ID, new CodecType(MC_TYPES, "TAG_ENTRY_ID")
				)
		);
		registerCodecsForType(
				"java.util.BitSet",
				GenStandardCodec.BIT_SET,
				Map.of(
						GenStandardCodec.BIT_SET, new CodecType(MC_TYPES, "BIT_SET")
				)
		);
		registerCodecsForType(
				"com.mojang.authlib.properties.Property",
				GenStandardCodec.GAME_PROFILE_PROPERTY,
				Map.of(
						GenStandardCodec.GAME_PROFILE_PROPERTY, new CodecType(MC_TYPES, "GAME_PROFILE_PROPERTY")
				)
		);
		registerCodecsForType(
				"com.mojang.authlib.properties.PropertyMap",
				GenStandardCodec.GAME_PROFILE_PROPERTY_MAP,
				Map.of(
						GenStandardCodec.GAME_PROFILE_PROPERTY_MAP, new CodecType(MC_TYPES, "GAME_PROFILE_PROPERTY_MAP")
				)
		);
		registerCodecsForType(
				"net.minecraft.resources.Identifier",
				GenStandardCodec.IDENTIFIER,
				Map.of(
						GenStandardCodec.IDENTIFIER, new CodecType(ClassName.get("net.minecraft.resources", "Identifier"), "CODEC")
				)
		);
		registerCodecsForType(
				"net.minecraft.world.item.crafting.Ingredient",
				GenStandardCodec.AUTOMATIC,
				Map.of(GenStandardCodec.AUTOMATIC, new CodecType(ClassName.get("net.minecraft.world.item.crafting", "Ingredient"), "CODEC"))
		);
		registerCodecsForType(
				"byte[]",
				GenStandardCodec.BASE_64,
				Map.of(
						GenStandardCodec.BASE_64, new CodecType(MC_TYPES, "BASE_64")
				)
		);

		registerPacketCodecsForType(
				"boolean",
				GenPacketCodec.BOOL,
				Map.of(
						GenPacketCodec.BOOL, new CodecType(MC_PACKET_TYPES, "BOOL")
				)
		);
		registerPacketCodecsForType(
				"byte",
				GenPacketCodec.BYTE,
				Map.of(
						GenPacketCodec.BYTE, new CodecType(MC_PACKET_TYPES, "BYTE")
				)
		);
		registerPacketCodecsForType(
				"short",
				GenPacketCodec.SHORT,
				Map.of(
						GenPacketCodec.SHORT, new CodecType(MC_PACKET_TYPES, "SHORT")
				)
		);
		registerPacketCodecsForType(
				"int",
				GenPacketCodec.VAR_INT,
				Map.of(
						GenPacketCodec.VAR_INT, new CodecType(MC_PACKET_TYPES, "VAR_INT"),
						GenPacketCodec.UNSIGNED_SHORT, new CodecType(MC_PACKET_TYPES, "UNSIGNED_SHORT"),
						GenPacketCodec.INTEGER, new CodecType(MC_PACKET_TYPES, "INT"),
						GenPacketCodec.SYNC_ID, new CodecType(MC_PACKET_TYPES, "CONTAINER_ID")
				)
		);
		registerPacketCodecsForType(
				"java.util.OptionalInt",
				GenPacketCodec.OPTIONAL_INT,
				Map.of(
						GenPacketCodec.OPTIONAL_INT, new CodecType(MC_PACKET_TYPES, "OPTIONAL_VAR_INT")
				)
		);
		registerPacketCodecsForType(
				"long",
				GenPacketCodec.VAR_LONG,
				Map.of(
						GenPacketCodec.VAR_LONG, new CodecType(MC_PACKET_TYPES, "VAR_LONG"),
						GenPacketCodec.LONG, new CodecType(MC_PACKET_TYPES, "LONG")
				)
		);
		registerPacketCodecsForType(
				"float",
				GenPacketCodec.FLOAT,
				Map.of(
						GenPacketCodec.FLOAT, new CodecType(MC_PACKET_TYPES, "FLOAT"),
						GenPacketCodec.DEGREES, new CodecType(MC_PACKET_TYPES, "ROTATION_BYTE")
				)
		);
		registerPacketCodecsForType(
				"double",
				GenPacketCodec.DOUBLE,
				Map.of(
						GenPacketCodec.DOUBLE, new CodecType(MC_PACKET_TYPES, "DOUBLE")
				)
		);
		registerPacketCodecsForType(
				"byte[]",
				GenPacketCodec.BYTE_ARRAY,
				Map.of(
						GenPacketCodec.BYTE_ARRAY, new CodecType(MC_PACKET_TYPES, "BYTE_ARRAY")
				)
		);
		registerPacketCodecsForType(
				"java.lang.String",
				GenPacketCodec.STRING,
				Map.of(
						GenPacketCodec.STRING, new CodecType(MC_PACKET_TYPES, "STRING_UTF8")
				));
		registerPacketCodecsForType(
				"net.minecraft.nbt.NbtElement",
				GenPacketCodec.NBT_ELEMENT,
				Map.of(
						GenPacketCodec.NBT_ELEMENT, new CodecType(MC_PACKET_TYPES, "TAG"),
						GenPacketCodec.UNLIMITED_NBT_ELEMENT, new CodecType(MC_PACKET_TYPES, "TRUSTED_TAG")
				)
		);
		registerPacketCodecsForType(
				"net.minecraft.nbt.NbtCompound",
				GenPacketCodec.NBT_COMPOUND,
				Map.of(
						GenPacketCodec.NBT_COMPOUND, new CodecType(MC_PACKET_TYPES, "COMPOUND_TAG"),
						GenPacketCodec.UNLIMITED_NBT_COMPOUND, new CodecType(MC_PACKET_TYPES, "TRUSTED_COMPOUND_TAG")
				)
		);
		registerPacketCodecsForType(
				"java.util.Optional<net.minecraft.nbt.NbtCompound>",
				GenPacketCodec.OPTIONAL_NBT,
				Map.of(
						GenPacketCodec.OPTIONAL_NBT, new CodecType(MC_PACKET_TYPES, "OPTIONAL_COMPOUND_TAG")
				)
		);
		registerPacketCodecsForType(
				"org.joml.Vector3f",
				GenPacketCodec.VECTOR_3F,
				Map.of(
						GenPacketCodec.VECTOR_3F, new CodecType(CodeBlock.of(
								"$T.VECTOR3F.map($T::new, vector -> vector)",
								MC_PACKET_TYPES,
								ClassName.get("org.joml", "Vector3f")
						))
				)
		);
		registerPacketCodecsForType(
				"org.joml.Vector3fc",
				GenPacketCodec.VECTOR_3F,
				Map.of(GenPacketCodec.VECTOR_3F, new CodecType(MC_PACKET_TYPES, "VECTOR3F"))
		);
		registerPacketCodecsForType(
				"org.joml.Quaternionf",
				GenPacketCodec.QUATERNION_F,
				Map.of(
						GenPacketCodec.QUATERNION_F, new CodecType(CodeBlock.of(
								"$T.QUATERNIONF.map($T::new, rotation -> rotation)",
								MC_PACKET_TYPES,
								ClassName.get("org.joml", "Quaternionf")
						))
				)
		);
		registerPacketCodecsForType(
				"org.joml.Quaternionfc",
				GenPacketCodec.QUATERNION_F,
				Map.of(GenPacketCodec.QUATERNION_F, new CodecType(MC_PACKET_TYPES, "QUATERNIONF"))
		);
		registerPacketCodecsForType(
				"com.mojang.authlib.properties.PropertyMap",
				GenPacketCodec.PROPERTY_MAP,
				Map.of(
						GenPacketCodec.PROPERTY_MAP, new CodecType(MC_PACKET_TYPES, "GAME_PROFILE_PROPERTIES")
				)
		);
		registerPacketCodecsForType(
				"com.mojang.authlib.GameProfile",
				GenPacketCodec.GAME_PROFILE,
				Map.of(
						GenPacketCodec.GAME_PROFILE, new CodecType(MC_PACKET_TYPES, "GAME_PROFILE")
				)
		);
		registerPacketCodecsForType(
				"net.minecraft.resources.Identifier",
				GenPacketCodec.IDENTIFIER,
				Map.of(
						GenPacketCodec.IDENTIFIER, new CodecType(ClassName.get("net.minecraft.resources", "Identifier"), "STREAM_CODEC")
				)
		);
	}

	/**
	 * Registers the provided codecs for a given type.
	 *
	 * @param qualifiedType   The fully qualified name of the type for which codecs are being registered.
	 * @param defaultCodec    The default codec to be used for the specified type.
	 * @param availableCodecs A map of available codecs for the specified type, where each codec is mapped to its corresponding {@link CodecType}.
	 */
	private void registerCodecsForType(String qualifiedType, GenStandardCodec defaultCodec, Map<GenStandardCodec, CodecType> availableCodecs)
	{
		codecTypes.put(qualifiedType, availableCodecs);
		defaultCodecTypes.put(qualifiedType, defaultCodec);
	}

	/**
	 * Registers the provided packet codecs for a given type.
	 *
	 * @param qualifiedType   The fully qualified name of the type for which packet codecs are being registered.
	 * @param defaultCodec    The default packet codec to be used for the specified type.
	 * @param availableCodecs A map of available packet codecs for the specified type, where each packet codec
	 *                        is mapped to its corresponding {@link CodecType}.
	 */
	private void registerPacketCodecsForType(String qualifiedType, GenPacketCodec defaultCodec, Map<GenPacketCodec, CodecType> availableCodecs)
	{
		packetCodecTypes.put(qualifiedType, availableCodecs);
		defaultPacketCodecTypes.put(qualifiedType, defaultCodec);
	}

	/**
	 * Emits a diagnostic note prefixed with this processor's name.
	 */
	private void log(String message)
	{
		processingEnv.getMessager().printMessage(Diagnostic.Kind.NOTE, "(CodecGen AP) %s".formatted(message));
	}

	/**
	 * Generates codec interfaces for records annotated with {@link GenerateCodec}.
	 */
	@Override
	public boolean process(Set<? extends TypeElement> annotations, RoundEnvironment roundEnv)
	{
		for (TypeElement annotationElement : annotations)
		{
			for (Element element : roundEnv.getElementsAnnotatedWith(annotationElement))
			{
				if (element.getKind() == ElementKind.RECORD)
				{
					TypeElement classElement = (TypeElement)element;
					log("Processing record: " + classElement.getQualifiedName());

					var className = classElement.getSimpleName().toString();
					var packageName = getPackageName(classElement);
					var interfaceName = "I" + className + "Codec";

					var generatedInterface = generateInterface(packageName, classElement, interfaceName);
					if (generatedInterface == null)
					{
						log("Skipped record.");
						continue;
					}

					try
					{
						generatedInterface.writeTo(processingEnv.getFiler());
					}
					catch (IOException e)
					{
						processingEnv.getMessager().printError(e.toString(), element);
					}
				}
			}
		}

		return true;
	}

	/**
	 * Returns the package used for generated codec interfaces.
	 */
	private String getPackageName(TypeElement classElement)
	{
		return "dev.pswg.generated.codecs";
	}

	/**
	 * Builds a generated interface containing the requested standard and packet codecs.
	 */
	private JavaFile generateInterface(String packageName, TypeElement classElement, String interfaceName)
	{
		var codec = generateCodec(classElement);
		if (codec == null)
			return null;

		var includePacketCodec = classElement.getAnnotation(GenerateCodec.class).packetCodec();
		var packetCodec = includePacketCodec ? generatePacketCodec(classElement) : null;
		if (includePacketCodec && packetCodec == null)
			return null;

		var iface = TypeSpec.interfaceBuilder(interfaceName)
		                    .addModifiers(Modifier.PUBLIC)
		                    .addOriginatingElement(classElement)
		                    .addField(codec.mapCodec())
		                    .addField(codec.codec());

		if (packetCodec != null)
			iface.addField(packetCodec);

		return JavaFile.builder(packageName, iface.build())
		               .indent("\t")
		               .build();
	}

	/**
	 * Holds the two standard codec fields generated for a record.
	 *
	 * @param mapCodec The field codec for the record
	 * @param codec The value codec for the record
	 */
	private record GeneratedStandardCodec(FieldSpec mapCodec, FieldSpec codec)
	{
	}

	/**
	 * Generates standard map and value codecs for the given record.
	 *
	 * @param classElement The class element for which the codec is to be generated.
	 *
	 * @return The generated codec fields, or null if a suitable component codec could not be found.
	 */
	private GeneratedStandardCodec generateCodec(TypeElement classElement)
	{
		var stateComponentType = TypeName.get(classElement.asType());
		var codecType = ClassName.get("com.mojang.serialization", "Codec");
		var mapCodecType = ClassName.get("com.mojang.serialization", "MapCodec");
		var parameterizedCodec = ParameterizedTypeName.get(codecType, stateComponentType);
		var parameterizedMapCodec = ParameterizedTypeName.get(mapCodecType, stateComponentType);
		var recordCodecBuilder = ClassName.get("com.mojang.serialization.codecs", "RecordCodecBuilder");

		var components = classElement.getRecordComponents();
		var annotation = classElement.getAnnotation(GenerateCodec.class);
		CodeBlock mapCodecInitializer;
		if (components.isEmpty())
		{
			mapCodecInitializer = CodeBlock.of("$T.unit(new $T())", mapCodecType, stateComponentType);
		}
		else
		{
			var codecInitializer = CodeBlock.builder()
			                                .add("$T.mapCodec(instance -> instance.group(\n", recordCodecBuilder)
			                                .indent();
			var first = true;
			for (var component : components)
			{
				var codecExpression = getStandardCodec(component, annotation.strict());
				if (codecExpression == null)
					return null;

				if (!first)
					codecInitializer.add(",\n");
				first = false;

				var codecDefault = component.getAnnotation(CodecDefault.class);
				var codecName = component.getAnnotation(CodecName.class);
				var fieldName = codecName == null ? component.getSimpleName().toString() : codecName.value();
				var fieldCodec = CodeBlock.builder().add("$L", codecExpression.expression());

				if (codecDefault != null)
					fieldCodec.add(".optionalFieldOf($S, $L)", fieldName, codecDefault.value());
				else if (codecExpression.optionalField())
					fieldCodec.add(".optionalFieldOf($S)", fieldName);
				else
					fieldCodec.add(".fieldOf($S)", fieldName);

				fieldCodec.add(".forGetter($T::$L)", stateComponentType, component.getSimpleName().toString());
				codecInitializer.add("$L", fieldCodec.build());
			}

			codecInitializer.unindent()
			                .add("\n).apply(instance, $T::new))", stateComponentType);
			mapCodecInitializer = codecInitializer.build();
		}

		var mapCodec = FieldSpec.builder(parameterizedMapCodec, "MAP_CODEC")
		                        .addModifiers(Modifier.PUBLIC, Modifier.STATIC, Modifier.FINAL)
		                        .initializer(mapCodecInitializer)
		                        .build();
		var codecInitializer = annotation.strict()
				? CodeBlock.of("$T.strict(MAP_CODEC)", GALAXIES_CODECS)
				: CodeBlock.of("MAP_CODEC.codec()");
		var codec = FieldSpec.builder(parameterizedCodec, "CODEC")
		                      .addModifiers(Modifier.PUBLIC, Modifier.STATIC, Modifier.FINAL)
		                      .initializer(codecInitializer)
		                      .build();

		return new GeneratedStandardCodec(mapCodec, codec);
	}

	/**
	 * Resolves a record component's standard codec, including component annotations
	 * and recursive collection types.
	 */
	private StandardCodecExpression getStandardCodec(RecordComponentElement component, boolean strict)
	{
		var componentType = component.asType();
		var optionalField = isDeclaredType(componentType, "java.util.Optional");
		if (optionalField)
		{
			var optionalArguments = getTypeArguments(componentType);
			if (optionalArguments.size() != 1)
			{
				reportError(component, "Optional codec fields must declare exactly one type argument.");
				return null;
			}
			componentType = optionalArguments.get(0);
		}

		var codecSize = component.getAnnotation(CodecSize.class);
		if (codecSize != null && !isCollectionType(componentType))
		{
			reportError(component, "@CodecSize can only be applied to List or Map record components.");
			return null;
		}

		if (codecSize != null && (codecSize.min() < 0 || codecSize.max() < codecSize.min()))
		{
			reportError(component, "@CodecSize requires 0 <= min <= max.");
			return null;
		}

		var codecRange = component.getAnnotation(CodecRange.class);
		if (codecRange != null && (!Double.isFinite(codecRange.min())
				|| !Double.isFinite(codecRange.max())
				|| codecRange.min() > codecRange.max()))
		{
			reportError(component, "@CodecRange requires finite bounds with min <= max.");
			return null;
		}

		var useCodec = component.getAnnotation(UseCodec.class);
		var selfCodec = component.getAnnotation(SelfCodec.class) != null;
		var customSource = useCodec == null ? null : useCodec.customCodec();
		if (!selfCodec && customSource != null && !customSource.member().isEmpty())
		{
			if (optionalField && (codecRange != null || codecSize != null))
			{
				reportError(component, "Optional custom codecs cannot be combined with @CodecRange or @CodecSize.");
				return null;
			}
			var expression = getCodecSourceExpression(customSource);
			if (expression == null)
				return null;
			if (codecRange != null)
				expression = applyRangeValidation(expression, componentType, codecRange, component);
			if (expression == null)
				return null;
			if (codecSize != null)
			{
				expression = applyCustomCollectionSize(expression, componentType, codecSize);
				if (expression == null)
					return null;
			}
			if (strict)
				expression = CodeBlock.of("$T.catchDecoderException($L)", MC_TYPES, expression);
			return new StandardCodecExpression(expression, false);
		}

		var selectedCodec = useCodec == null ? GenStandardCodec.AUTOMATIC : useCodec.codec();
		var expression = buildStandardCodec(componentType, component, selfCodec, selectedCodec, codecSize, codecRange, true);
		if (expression == null)
			return null;

		if (strict)
			expression = CodeBlock.of("$T.catchDecoderException($L)", MC_TYPES, expression);

		return new StandardCodecExpression(expression, optionalField);
	}

	/**
	 * Builds a standard codec expression from a type mirror without parsing type
	 * names or generic arguments from source strings.
	 */
	private CodeBlock buildStandardCodec(
			TypeMirror type,
			RecordComponentElement component,
			boolean selfCodec,
			GenStandardCodec selectedCodec,
			CodecSize size,
			CodecRange range,
			boolean rootType
	)
	{
		if (isDeclaredType(type, "java.util.Optional"))
		{
			reportError(component, "Optional values are supported as record fields, but not nested inside collections.");
			return null;
		}

		if (isDeclaredType(type, "java.util.List"))
		{
			var arguments = getTypeArguments(type);
			if (arguments.size() != 1)
			{
				reportError(component, "List codec fields must declare exactly one type argument.");
				return null;
			}
			if (range != null)
			{
				reportError(component, "@CodecRange can only be applied to a numeric component.");
				return null;
			}

			var elementCodec = buildStandardCodec(arguments.get(0), component, selfCodec, selectedCodec, null, null, false);
			if (elementCodec == null)
				return null;
			if (rootType && size != null)
				return CodeBlock.of("$L.listOf($L, $L)", elementCodec, size.min(), size.max());
			return CodeBlock.of("$L.listOf()", elementCodec);
		}

		if (isDeclaredType(type, "java.util.Map"))
		{
			var arguments = getTypeArguments(type);
			if (arguments.size() != 2)
			{
				reportError(component, "Map codec fields must declare key and value type arguments.");
				return null;
			}
			if (range != null)
			{
				reportError(component, "@CodecRange can only be applied to a numeric component.");
				return null;
			}

			var keyCodec = buildStandardCodec(arguments.get(0), component, false, GenStandardCodec.AUTOMATIC, null, null, false);
			var valueCodec = buildStandardCodec(arguments.get(1), component, selfCodec, selectedCodec, null, null, false);
			if (keyCodec == null || valueCodec == null)
				return null;

			CodeBlock mapCodec = CodeBlock.of("$T.strictUnboundedMap($L, $L)", MC_TYPES, keyCodec, valueCodec);
			if (rootType && size != null)
			{
				mapCodec = CodeBlock.of("$T.sizeLimitedMap($L, $L)", MC_TYPES, mapCodec, size.max());
				if (size.min() > 0)
					mapCodec = applyMinimumSize(mapCodec, size.min());
			}
			return mapCodec;
		}

		if (range != null)
		{
			if (selectedCodec == GenStandardCodec.AUTOMATIC && !selfCodec)
				return createNativeRangeCodec(type, range, component);
		}

		if (selfCodec)
		{
			if (!(type instanceof DeclaredType declaredType) || !(declaredType.asElement() instanceof TypeElement typeElement))
			{
				reportError(component, "@SelfCodec requires a declared type with a static CODEC field.");
				return null;
			}
			return CodeBlock.of("$T.CODEC", ClassName.get(typeElement));
		}

		var typeKey = getTypeKey(type);
		var codecTypeMap = codecTypes.get(typeKey);
		if (codecTypeMap == null)
		{
			reportError(component, "No standard codec is registered for component type " + type + ".");
			return null;
		}

		var requestedCodec = selectedCodec == GenStandardCodec.AUTOMATIC
				? defaultCodecTypes.get(typeKey)
				: selectedCodec;
		var codecType = codecTypeMap.get(requestedCodec);
		if (codecType == null)
		{
			reportError(component, "Codec " + requestedCodec + " is not registered for component type " + type + ".");
			return null;
		}

		var expression = codecType.asExpression();
		if (range != null)
			expression = applyRangeValidation(expression, type, range, component);
		return expression;
	}

	/**
	 * Creates the native range codec used for automatically mapped numeric types.
	 */
	private CodeBlock createNativeRangeCodec(TypeMirror type, CodecRange range, Element element)
	{
		var numericType = getTypeKey(type);
		var codecType = ClassName.get("com.mojang.serialization", "Codec");

		switch (numericType)
		{
			case "byte", "java.lang.Byte" ->
			{
				var min = rangeBoundLiteral(numericType, range.min(), element);
				var max = rangeBoundLiteral(numericType, range.max(), element);
				return min == null || max == null ? null : createRangeValidation(CodeBlock.of("$T.BYTE", codecType), range, min, max);
			}
			case "short", "java.lang.Short" ->
			{
				var min = rangeBoundLiteral(numericType, range.min(), element);
				var max = rangeBoundLiteral(numericType, range.max(), element);
				return min == null || max == null ? null : createRangeValidation(CodeBlock.of("$T.SHORT", codecType), range, min, max);
			}
			case "int", "java.lang.Integer" ->
			{
				var min = rangeBoundLiteral(numericType, range.min(), element);
				var max = rangeBoundLiteral(numericType, range.max(), element);
				return min == null || max == null
						? null
						: CodeBlock.of("$T.intRange($L, $L)", codecType, min, max);
			}
			case "long", "java.lang.Long" ->
			{
				var min = rangeBoundLiteral(numericType, range.min(), element);
				var max = rangeBoundLiteral(numericType, range.max(), element);
				return min == null || max == null ? null : createRangeValidation(CodeBlock.of("$T.LONG", codecType), range, min, max);
			}
			case "float", "java.lang.Float" ->
			{
				var min = (float)range.min();
				var max = (float)range.max();
				if (!Float.isFinite(min) || !Float.isFinite(max))
				{
					reportError(element, "@CodecRange bounds for float components must be representable as finite floats.");
					return null;
				}
				return CodeBlock.of("$T.floatRange($L, $L)", MC_TYPES, floatLiteral(min), floatLiteral(max));
			}
			case "double", "java.lang.Double" ->
			{
				return CodeBlock.of("$T.doubleRange($L, $L)", codecType, doubleLiteral(range.min()), doubleLiteral(range.max()));
			}
			default ->
			{
				reportError(element, "@CodecRange can only be applied to byte, short, int, long, float, or double components.");
				return null;
			}
		}
	}

	/**
	 * Applies an inclusive numeric range to an existing codec expression.
	 */
	private CodeBlock applyRangeValidation(CodeBlock codec, TypeMirror type, CodecRange range, Element element)
	{
		var numericType = getTypeKey(type);
		var minLiteral = rangeBoundLiteral(numericType, range.min(), element);
		var maxLiteral = rangeBoundLiteral(numericType, range.max(), element);
		if (minLiteral == null || maxLiteral == null)
		{
			if (!isNumericType(type))
				reportError(element, "@CodecRange can only be applied to byte, short, int, long, float, or double components.");
			return null;
		}
		return createRangeValidation(codec, range, minLiteral, maxLiteral);
	}

	/**
	 * Formats and validates a range bound for its numeric component type.
	 */
	private String rangeBoundLiteral(String numericType, double value, Element element)
	{
		return switch (numericType)
		{
			case "byte", "java.lang.Byte" -> integerBound(value, Byte.MIN_VALUE, Byte.MAX_VALUE, element);
			case "short", "java.lang.Short" -> integerBound(value, Short.MIN_VALUE, Short.MAX_VALUE, element);
			case "int", "java.lang.Integer" -> integerBound(value, Integer.MIN_VALUE, Integer.MAX_VALUE, element);
			case "long", "java.lang.Long" ->
			{
				var bound = integerBound(value, Long.MIN_VALUE, Long.MAX_VALUE, element);
				yield bound == null ? null : bound + "L";
			}
			case "float", "java.lang.Float" ->
			{
				var floatValue = (float)value;
				if (!Float.isFinite(floatValue))
				{
					reportError(element, "@CodecRange bounds for float components must be representable as finite floats.");
					yield null;
				}
				yield floatLiteral(floatValue);
			}
			case "double", "java.lang.Double" -> doubleLiteral(value);
			default -> null;
		};
	}

	/**
	 * Creates a validation wrapper around a codec expression.
	 */
	private CodeBlock createRangeValidation(CodeBlock codec, CodecRange range, String minLiteral, String maxLiteral)
	{
		var dataResultType = ClassName.get("com.mojang.serialization", "DataResult");
		return CodeBlock.of(
				"$L.validate(value -> value >= $L && value <= $L ? $T.success(value) : $T.error(() -> $S + value))",
				codec,
				minLiteral,
				maxLiteral,
				dataResultType,
				dataResultType,
				"Value must be within range [" + range.min() + ";" + range.max() + "]: "
		);
	}

	/**
	 * Wraps an explicitly supplied collection codec in size checks.
	 */
	private CodeBlock applyCustomCollectionSize(CodeBlock codec, TypeMirror type, CodecSize size)
	{
		if (isDeclaredType(type, "java.util.List"))
		{
			return CodeBlock.of(
					"$L.validate(value -> value.size() >= $L && value.size() <= $L ? $T.success(value) : $T.error(() -> $S + value.size()))",
					codec,
					size.min(),
					size.max(),
					ClassName.get("com.mojang.serialization", "DataResult"),
					ClassName.get("com.mojang.serialization", "DataResult"),
					"Collection size must be in range [" + size.min() + ";" + size.max() + "]: "
			);
		}

		var mapCodec = CodeBlock.of("$T.sizeLimitedMap($L, $L)", MC_TYPES, codec, size.max());
		return size.min() == 0 ? mapCodec : applyMinimumSize(mapCodec, size.min());
	}

	/**
	 * Adds the minimum-size check not provided by the native maximum-size map codec.
	 */
	private CodeBlock applyMinimumSize(CodeBlock codec, int minimum)
	{
		var dataResultType = ClassName.get("com.mojang.serialization", "DataResult");
		return CodeBlock.of(
				"$L.validate(value -> value.size() >= $L ? $T.success(value) : $T.error(() -> $S + value.size()))",
				codec,
				minimum,
				dataResultType,
				dataResultType,
				"Map size must be at least " + minimum + ": "
		);
	}

	/**
	 * Returns true when a type mirror is the named declared type.
	 */
	private boolean isDeclaredType(TypeMirror type, String qualifiedName)
	{
		if (type.getKind() != TypeKind.DECLARED)
			return false;

		var declaredType = (DeclaredType)type;
		return declaredType.asElement() instanceof TypeElement typeElement
				&& typeElement.getQualifiedName().contentEquals(qualifiedName);
	}

	/**
	 * Returns the declared generic arguments of a type mirror.
	 */
	private List<? extends TypeMirror> getTypeArguments(TypeMirror type)
	{
		return type instanceof DeclaredType declaredType ? declaredType.getTypeArguments() : List.of();
	}

	/**
	 * Returns whether the type is a supported list or map collection.
	 */
	private boolean isCollectionType(TypeMirror type)
	{
		return isDeclaredType(type, "java.util.List") || isDeclaredType(type, "java.util.Map");
	}

	/**
	 * Returns a stable lookup key for primitive and declared types.
	 */
	private String getTypeKey(TypeMirror type)
	{
		if (type.getKind() == TypeKind.DECLARED && ((DeclaredType)type).asElement() instanceof TypeElement typeElement)
			return typeElement.getQualifiedName().toString();
		return type.toString();
	}

	/**
	 * Returns whether a type mirror is one of the supported numeric types.
	 */
	private boolean isNumericType(TypeMirror type)
	{
		return switch (getTypeKey(type))
		{
			case "byte", "java.lang.Byte", "short", "java.lang.Short", "int", "java.lang.Integer", "long", "java.lang.Long", "float", "java.lang.Float", "double", "java.lang.Double" -> true;
			default -> false;
		};
	}

	/**
	 * Converts an integral annotation bound into a checked integer literal.
	 */
	private String integerBound(double value, long minimum, long maximum, Element element)
	{
		var decimal = java.math.BigDecimal.valueOf(value).stripTrailingZeros();
		if (decimal.scale() > 0)
		{
			reportError(element, "Integer codec bounds must be whole numbers.");
			return null;
		}

		try
		{
			var integer = decimal.longValueExact();
			if (integer < minimum || integer > maximum)
			{
				reportError(element, "@CodecRange bound is outside the range supported by the component type.");
				return null;
			}
			return Long.toString(integer);
		}
		catch (ArithmeticException exception)
		{
			reportError(element, "@CodecRange bound is outside the range supported by the component type.");
			return null;
		}
	}

	/**
	 * Formats a float literal for generated Java source.
	 */
	private String floatLiteral(float value)
	{
		return Float.toString(value) + "F";
	}

	/**
	 * Formats a double literal for generated Java source.
	 */
	private String doubleLiteral(double value)
	{
		return Double.toString(value) + "D";
	}

	/**
	 * Resolves a custom codec source annotation to its static Java expression.
	 */
	private CodeBlock getCodecSourceExpression(CodecSource codecSource)
	{
		if (!(processingEnv.getTypeUtils().asElement(getCodecSourceType(codecSource)) instanceof TypeElement sourceType))
			return null;
		return CodeBlock.of("$T.$L", ClassName.get(sourceType), codecSource.member());
	}

	/**
	 * Reports a processor error attached to the source element.
	 */
	private void reportError(Element element, String message)
	{
		processingEnv.getMessager().printMessage(Diagnostic.Kind.ERROR, message, element);
	}

	/**
	 * Generates a static final field representing a packet codec for the given class element.
	 * The packet codec is constructed using the record components of the class.
	 *
	 * @param classElement The class element for which the packet codec is to be generated.
	 *
	 * @return A {@link FieldSpec} representing the packet codec, or null if the class has no components or a suitable codec could not be found.
	 */
	private FieldSpec generatePacketCodec(TypeElement classElement)
	{
		var registryByteBufType = ClassName.get("net.minecraft.network", "RegistryFriendlyByteBuf");
		var recordType = TypeName.get(classElement.asType());
		var packetCodecType = ClassName.get("net.minecraft.network.codec", "StreamCodec");
		var parameterizedCodec = ParameterizedTypeName.get(packetCodecType, registryByteBufType, recordType);

		CodeBlock.Builder encodeBuilder = CodeBlock.builder();
		CodeBlock.Builder decodeBuilder = CodeBlock.builder();

		var paramNames = new ArrayList<String>();
		for (var component : classElement.getRecordComponents())
		{
			var nestedCodecType = getCodec(
					component,
					UseCodec::customPacket,
					UseCodec::packet,
					GenPacketCodec.AUTOMATIC,
					packetCodecTypes,
					defaultPacketCodecTypes,
					"PACKET_CODEC",
					"packet codec"
			);
			if (nestedCodecType == null)
				return null;

			var paramName = component.getSimpleName().toString();
			paramNames.add(paramName);

			encodeBuilder.addStatement("$L.encode(registryByteBuf, value.$L())", nestedCodecType.asExpression(), component.getSimpleName().toString());
			decodeBuilder.addStatement("var $L = $L.decode(registryByteBuf)", paramName, nestedCodecType.asExpression());
		}

		decodeBuilder.addStatement(
				"return new $1T($2L)",
				recordType,
				String.join(", ", paramNames)
		);

		var anonPacketCodec = TypeSpec.anonymousClassBuilder("")
		                              .addSuperinterface(ParameterizedTypeName.get(packetCodecType, registryByteBufType, recordType))
		                              .addMethod(MethodSpec.methodBuilder("encode")
		                                                   .addAnnotation(Override.class)
		                                                   .addModifiers(Modifier.PUBLIC)
		                                                   .returns(void.class)
		                                                   .addParameter(registryByteBufType, "registryByteBuf")
		                                                   .addParameter(recordType, "value")
		                                                   .addCode(encodeBuilder.build())
		                                                   .build())
		                              .addMethod(MethodSpec.methodBuilder("decode")
		                                                   .addAnnotation(Override.class)
		                                                   .addModifiers(Modifier.PUBLIC)
		                                                   .returns(recordType)
		                                                   .addParameter(registryByteBufType, "registryByteBuf")
		                                                   .addCode(decodeBuilder.build())
		                                                   .build())
		                              .build();

		return FieldSpec.builder(parameterizedCodec, "PACKET_CODEC")
		                .addModifiers(Modifier.PUBLIC, Modifier.STATIC, Modifier.FINAL)
		                .initializer(CodeBlock.builder()
		                                      .add("$L", anonPacketCodec)
		                                      .build())
		                .build();
	}

	/**
	 * Retrieves the codec type for the given record component based on its type and optional {@link UseCodec} annotation.
	 *
	 * @param component The record component element for which the codec type is to be retrieved.
	 *
	 * @return The corresponding {@link CodecType} if a suitable codec is found, otherwise null.
	 */
	private <TGenCodec extends Enum<TGenCodec>> CodecType getCodec(
			RecordComponentElement component,
			Function<UseCodec, CodecSource> customCodecGetter,
			Function<UseCodec, TGenCodec> codecGetter,
			TGenCodec automaticMember,
			HashMap<String, Map<TGenCodec, CodecType>> codecTypes,
			HashMap<String, TGenCodec> defaultCodecs,
			String memberName,
			String friendlyName
	)
	{
		var useCodecInstance = Optional.ofNullable(component.getAnnotation(UseCodec.class));

		// If a custom codec is requested, it takes the highest precedence
		if (useCodecInstance.map(customCodecGetter).filter(src -> !"".equals(src.member())).orElse(null) instanceof CodecSource codecSource)
		{
			var customCodecType = new CodecType(getCodecSourceExpression(codecSource));
			log("Custom %s requested for %s: %s".formatted(friendlyName, component.getSimpleName().toString(), customCodecType));
			return customCodecType;
		}

		if (component.getAnnotation(SelfCodec.class) != null)
		{
			var type = component.asType();
			if (!(type instanceof DeclaredType declaredType) || !(declaredType.asElement() instanceof TypeElement typeElement))
			{
				log("Self codec requested for non-declared type %s".formatted(type));
				return null;
			}
			var codecType = new CodecType(ClassName.get(typeElement), memberName);
			log("Requested element %s of type %s use defined %s member within type, using %s: %s".formatted(component.getSimpleName().toString(), type, memberName, friendlyName, codecType));
			return codecType;
		}

		var type = component.asType().toString();
		var codecTypeMap = codecTypes.get(type);

		if (codecTypeMap != null)
		{
			// If AUTOMATIC is specified, or if no @UseCodec annotation exists,
			// use the default codec for this type
			var requestedCodec = useCodecInstance
					.map(codecGetter)
					.filter(c -> c != automaticMember)
					.orElse(defaultCodecs.get(type));

			if (!codecTypeMap.containsKey(requestedCodec))
			{
				// If a codec is specified which doesn't support this type, fail early
				log("Found element %s of type %s, which is not supported by %s %s!".formatted(component.getSimpleName().toString(), type, friendlyName, requestedCodec));
				return null;
			}

			var codecType = codecTypeMap.get(requestedCodec);
			log("Found element %s of type %s, using %s: %s".formatted(component.getSimpleName().toString(), type, friendlyName, codecType));
			return codecType;
		}

		// No supported codec was found
		log("Found element %s of type %s, which has no supported %s!".formatted(component.getSimpleName().toString(), type, friendlyName));
		return null;
	}

	/**
	 * Gets the source type of a {@link CodecSource} as a {@link TypeMirror}
	 *
	 * @param codecSource The source to extract from
	 *
	 * @return The extracted source type
	 */
	private TypeMirror getCodecSourceType(CodecSource codecSource)
	{
		try
		{
			// This will throw an exception:
			//     javax.lang.model.type.MirroredTypeException: Attempt to access Class object for TypeMirror...
			// which will give us the mirror
			codecSource.source();
		}
		catch (MirroredTypeException mte)
		{
			return mte.getTypeMirror();
		}

		throw new RuntimeException("Source type did not result in a mirror");
	}
}
