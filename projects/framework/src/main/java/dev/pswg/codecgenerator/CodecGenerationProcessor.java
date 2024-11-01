package dev.pswg.codecgenerator;

import com.google.auto.service.AutoService;
import com.squareup.javapoet.*;

import javax.annotation.processing.AbstractProcessor;
import javax.annotation.processing.Processor;
import javax.annotation.processing.RoundEnvironment;
import javax.lang.model.SourceVersion;
import javax.lang.model.element.*;
import javax.lang.model.type.MirroredTypeException;
import javax.lang.model.type.TypeMirror;
import javax.tools.Diagnostic;
import javax.tools.StandardLocation;
import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.util.Set;

/**
 * An annotation processor that generates record codecs as an
 * implementable interface that defines CODEC and PACKET_CODEC
 * static fields.
 */
@AutoService(Processor.class)
public class CodecGenerationProcessor extends AbstractProcessor
{
	/**
	 * Represents a qualified reference to a defined, pre-existing codec
	 *
	 * @param className   The class in which the codec is defined
	 * @param elementName The name of the codec field
	 */
	private record CodecType(TypeName className, String elementName)
	{
		@Override
		public String toString()
		{
			return "%s#%s".formatted(className(), elementName());
		}
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

	public CodecGenerationProcessor()
	{
		var mojangTypes = ClassName.get("com.mojang.serialization", "Codec");
		var mcTypes = ClassName.get("net.minecraft.util.dynamic", "Codecs");

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
						GenStandardCodec.UNSIGNED_BYTE, new CodecType(mcTypes, "UNSIGNED_BYTE")
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
						GenStandardCodec.RGB, new CodecType(mcTypes, "RGB"),
						GenStandardCodec.ARGB, new CodecType(mcTypes, "ARGB"),
						GenStandardCodec.NON_NEGATIVE_INT, new CodecType(mcTypes, "NON_NEGATIVE_INT"),
						GenStandardCodec.POSITIVE_INT, new CodecType(mcTypes, "POSITIVE_INT"),
						GenStandardCodec.UNICODE_CODEPOINT, new CodecType(mcTypes, "CODEPOINT")
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
						GenStandardCodec.NON_NEGATIVE_FLOAT, new CodecType(mcTypes, "NON_NEGATIVE_FLOAT"),
						GenStandardCodec.POSITIVE_FLOAT, new CodecType(mcTypes, "POSITIVE_FLOAT")
				)
		);
		registerCodecsForType(
				"double",
				GenStandardCodec.DOUBLE,
				Map.of(
						GenStandardCodec.DOUBLE, new CodecType(mojangTypes, "DOUBLE")
				)
		);
		registerCodecsForType(
				"java.lang.String",
				GenStandardCodec.STRING,
				Map.of(
						GenStandardCodec.STRING, new CodecType(mojangTypes, "STRING"),
						GenStandardCodec.ESCAPED_STRING, new CodecType(mcTypes, "ESCAPED_STRING"),
						GenStandardCodec.PLAYER_NAME, new CodecType(mcTypes, "PLAYER_NAME"),
						GenStandardCodec.NON_EMPTY_STRING, new CodecType(mcTypes, "NON_EMPTY_STRING"),
						GenStandardCodec.IDENTIFIER_PATH, new CodecType(mcTypes, "IDENTIFIER_PATH")
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
						GenStandardCodec.JSON_ELEMENT, new CodecType(mcTypes, "JSON_ELEMENT")
				)
		);
		registerCodecsForType(
				"net.minecraft.util.dynamic.Codecs.VECTOR_3F",
				GenStandardCodec.VECTOR_3F,
				Map.of(
						GenStandardCodec.VECTOR_3F, new CodecType(mcTypes, "VECTOR_3F")
				)
		);
		registerCodecsForType(
				"net.minecraft.util.dynamic.Codecs.VECTOR_4F",
				GenStandardCodec.VECTOR_4F,
				Map.of(
						GenStandardCodec.VECTOR_4F, new CodecType(mcTypes, "VECTOR_4F")
				)
		);
		registerCodecsForType(
				"org.joml.Quaternionf",
				GenStandardCodec.QUATERNION_F,
				Map.of(
						GenStandardCodec.QUATERNION_F, new CodecType(mcTypes, "QUATERNION_F"),
						GenStandardCodec.ROTATION, new CodecType(mcTypes, "ROTATION")
				)
		);
		registerCodecsForType(
				"org.joml.AxisAngle4f",
				GenStandardCodec.AXIS_ANGLE_4F,
				Map.of(
						GenStandardCodec.AXIS_ANGLE_4F, new CodecType(mcTypes, "AXIS_ANGLE_4F")
				)
		);
		registerCodecsForType(
				"org.joml.Matrix4f",
				GenStandardCodec.MATRIX_4F,
				Map.of(
						GenStandardCodec.MATRIX_4F, new CodecType(mcTypes, "MATRIX_4F")
				)
		);
		registerCodecsForType(
				"java.time.Instant",
				GenStandardCodec.INSTANT,
				Map.of(
						GenStandardCodec.INSTANT, new CodecType(mcTypes, "INSTANT")
				)
		);
		registerCodecsForType(
				"net.minecraft.util.dynamic.Codecs.TagEntryId",
				GenStandardCodec.TAG_ENTRY_ID,
				Map.of(
						GenStandardCodec.TAG_ENTRY_ID, new CodecType(mcTypes, "TAG_ENTRY_ID")
				)
		);
		registerCodecsForType(
				"java.util.BitSet",
				GenStandardCodec.BIT_SET,
				Map.of(
						GenStandardCodec.BIT_SET, new CodecType(mcTypes, "BIT_SET")
				)
		);
		registerCodecsForType(
				"com.mojang.authlib.properties.Property",
				GenStandardCodec.GAME_PROFILE_PROPERTY,
				Map.of(
						GenStandardCodec.GAME_PROFILE_PROPERTY, new CodecType(mcTypes, "GAME_PROFILE_PROPERTY")
				)
		);
		registerCodecsForType(
				"com.mojang.authlib.properties.PropertyMap",
				GenStandardCodec.GAME_PROFILE_PROPERTY_MAP,
				Map.of(
						GenStandardCodec.GAME_PROFILE_PROPERTY_MAP, new CodecType(mcTypes, "GAME_PROFILE_PROPERTY_MAP")
				)
		);
		registerCodecsForType(
				"com.mojang.authlib.GameProfile",
				GenStandardCodec.GAME_PROFILE_WITH_PROPERTIES,
				Map.of(
						GenStandardCodec.GAME_PROFILE_WITH_PROPERTIES, new CodecType(mcTypes, "GAME_PROFILE_WITH_PROPERTIES")
				)
		);
		registerCodecsForType(
				"byte[]",
				GenStandardCodec.BASE_64,
				Map.of(
						GenStandardCodec.BASE_64, new CodecType(mcTypes, "BASE_64")
				)
		);

		var packetCodecsType = ClassName.get("net.minecraft.network.codec", "PacketCodecs");
		registerPacketCodecsForType(
				"boolean",
				GenPacketCodec.BOOL,
				Map.of(
						GenPacketCodec.BOOL, new CodecType(packetCodecsType, "BOOL")
				)
		);
		registerPacketCodecsForType(
				"byte",
				GenPacketCodec.BYTE,
				Map.of(
						GenPacketCodec.BYTE, new CodecType(packetCodecsType, "BYTE")
				)
		);
		registerPacketCodecsForType(
				"short",
				GenPacketCodec.SHORT,
				Map.of(
						GenPacketCodec.SHORT, new CodecType(packetCodecsType, "SHORT")
				)
		);
		registerPacketCodecsForType(
				"int",
				GenPacketCodec.VAR_INT,
				Map.of(
						GenPacketCodec.VAR_INT, new CodecType(packetCodecsType, "VAR_INT"),
						GenPacketCodec.UNSIGNED_SHORT, new CodecType(packetCodecsType, "UNSIGNED_SHORT"),
						GenPacketCodec.INTEGER, new CodecType(packetCodecsType, "INTEGER"),
						GenPacketCodec.SYNC_ID, new CodecType(packetCodecsType, "SYNC_ID")
				)
		);
		registerPacketCodecsForType(
				"java.util.OptionalInt",
				GenPacketCodec.OPTIONAL_INT,
				Map.of(
						GenPacketCodec.OPTIONAL_INT, new CodecType(packetCodecsType, "OPTIONAL_INT")
				)
		);
		registerPacketCodecsForType(
				"long",
				GenPacketCodec.VAR_LONG,
				Map.of(
						GenPacketCodec.VAR_LONG, new CodecType(packetCodecsType, "VAR_LONG"),
						GenPacketCodec.LONG, new CodecType(packetCodecsType, "LONG")
				)
		);
		registerPacketCodecsForType(
				"float",
				GenPacketCodec.FLOAT,
				Map.of(
						GenPacketCodec.FLOAT, new CodecType(packetCodecsType, "FLOAT"),
						GenPacketCodec.DEGREES, new CodecType(packetCodecsType, "DEGREES")
				)
		);
		registerPacketCodecsForType(
				"double",
				GenPacketCodec.DOUBLE,
				Map.of(
						GenPacketCodec.DOUBLE, new CodecType(packetCodecsType, "DOUBLE")
				)
		);
		registerPacketCodecsForType(
				"byte[]",
				GenPacketCodec.BYTE_ARRAY,
				Map.of(
						GenPacketCodec.BYTE_ARRAY, new CodecType(packetCodecsType, "BYTE_ARRAY")
				)
		);
		registerPacketCodecsForType(
				"java.lang.String",
				GenPacketCodec.STRING,
				Map.of(
						GenPacketCodec.STRING, new CodecType(packetCodecsType, "STRING")
				));
		registerPacketCodecsForType(
				"net.minecraft.nbt.NbtElement",
				GenPacketCodec.NBT_ELEMENT,
				Map.of(
						GenPacketCodec.NBT_ELEMENT, new CodecType(packetCodecsType, "NBT_ELEMENT"),
						GenPacketCodec.UNLIMITED_NBT_ELEMENT, new CodecType(packetCodecsType, "UNLIMITED_NBT_ELEMENT")
				)
		);
		registerPacketCodecsForType(
				"net.minecraft.nbt.NbtCompound",
				GenPacketCodec.NBT_COMPOUND,
				Map.of(
						GenPacketCodec.NBT_COMPOUND, new CodecType(packetCodecsType, "NBT_COMPOUND"),
						GenPacketCodec.UNLIMITED_NBT_COMPOUND, new CodecType(packetCodecsType, "UNLIMITED_NBT_COMPOUND")
				)
		);
		registerPacketCodecsForType(
				"java.util.Optional<net.minecraft.nbt.NbtCompound>",
				GenPacketCodec.OPTIONAL_NBT,
				Map.of(
						GenPacketCodec.OPTIONAL_NBT, new CodecType(packetCodecsType, "OPTIONAL_NBT")
				)
		);
		registerPacketCodecsForType(
				"org.joml.Vector3f",
				GenPacketCodec.VECTOR_3F,
				Map.of(
						GenPacketCodec.VECTOR_3F, new CodecType(packetCodecsType, "VECTOR_3F")
				)
		);
		registerPacketCodecsForType(
				"org.joml.Quaternionf",
				GenPacketCodec.QUATERNION_F,
				Map.of(
						GenPacketCodec.QUATERNION_F, new CodecType(packetCodecsType, "QUATERNION_F")
				)
		);
		registerPacketCodecsForType(
				"com.mojang.authlib.properties.PropertyMap",
				GenPacketCodec.PROPERTY_MAP,
				Map.of(
						GenPacketCodec.PROPERTY_MAP, new CodecType(packetCodecsType, "PROPERTY_MAP")
				)
		);
		registerPacketCodecsForType(
				"com.mojang.authlib.GameProfile",
				GenPacketCodec.GAME_PROFILE,
				Map.of(
						GenPacketCodec.GAME_PROFILE, new CodecType(packetCodecsType, "GAME_PROFILE")
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

	private void log(String message)
	{
		processingEnv.getMessager().printMessage(Diagnostic.Kind.NOTE, "(CodecGen AP) %s".formatted(message));
	}

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

					writeInterfaceToFile(generatedInterface);
				}
			}
		}

		return true;
	}

	private String getPackageName(TypeElement classElement)
	{
		return "dev.pswg.generated.codecs";
	}

	private JavaFile generateInterface(String packageName, TypeElement classElement, String interfaceName)
	{
		var codec = generateCodec(classElement);
		if (codec == null)
			return null;

		var packetCodec = generatePacketCodec(classElement);
		if (packetCodec == null)
			return null;

		var iface = TypeSpec.interfaceBuilder(interfaceName)
		                    .addModifiers(Modifier.PUBLIC)
		                    .addField(codec)
		                    .addField(packetCodec)
		                    .build();

		return JavaFile.builder(packageName, iface)
		               .indent("\t")
		               .build();
	}

	/**
	 * Generates a static final field representing a codec for the given class element.
	 * The codec is constructed using the record components of the class.
	 *
	 * @param classElement The class element for which the codec is to be generated.
	 *
	 * @return A {@link FieldSpec} representing the codec, or null if the class has no components or a suitable codec could not be found.
	 */
	private FieldSpec generateCodec(TypeElement classElement)
	{
		var stateComponentType = TypeName.get(classElement.asType());
		var codecType = ClassName.get("com.mojang.serialization", "Codec");
		var parameterizedCodec = ParameterizedTypeName.get(codecType, stateComponentType);

		var recordCodecBuilder = ClassName.get("com.mojang.serialization.codecs", "RecordCodecBuilder");

		// TODO: this will need to generate an anonymous class (?) for record with more than 16 members
		var codecInitializer = CodeBlock.builder()
		                                .add("$T.create(instance -> instance.group(\n", recordCodecBuilder)
		                                .indent();

		var components = classElement.getRecordComponents();
		if (components.isEmpty())
			return null;

		var first = true;
		for (var component : components)
		{
			var nestedCodecType = getCodecType(component);
			if (nestedCodecType == null)
				return null;

			if (!first)
				codecInitializer.add(",\n");
			first = false;

			codecInitializer.add("$3T.$4L.fieldOf($2S).forGetter($1T::$2L)", stateComponentType, component.getSimpleName().toString(), nestedCodecType.className(), nestedCodecType.elementName());
		}

		codecInitializer.unindent()
		                .add("\n).apply(instance, $T::new))", stateComponentType);

		return FieldSpec.builder(parameterizedCodec, "CODEC")
		                .addModifiers(Modifier.PUBLIC, Modifier.STATIC, Modifier.FINAL)
		                .initializer(codecInitializer.build())
		                .build();
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
		var registryByteBufType = ClassName.get("net.minecraft.network", "RegistryByteBuf");
		var stateComponentType = TypeName.get(classElement.asType());
		var packetCodecType = ClassName.get("net.minecraft.network.codec", "PacketCodec");
		var parameterizedCodec = ParameterizedTypeName.get(packetCodecType, registryByteBufType, stateComponentType);

		// TODO: this will need to generate an anonymous class for record with more than 8 members
		var codecInitializer = CodeBlock.builder()
		                                .add("$T.tuple(\n", packetCodecType)
		                                .indent();

		var components = classElement.getRecordComponents();
		if (components.isEmpty())
			return null;

		for (var component : components)
		{
			var nestedCodecType = getPacketCodecType(component);
			if (nestedCodecType == null)
				return null;

			codecInitializer.add("$1T.$2L, ", nestedCodecType.className(), nestedCodecType.elementName());
			codecInitializer.add("$1T::$2L,\n", stateComponentType, component.getSimpleName().toString());
		}

		codecInitializer
				.add("$T::new\n", stateComponentType)
				.unindent()
				.add(")", stateComponentType);

		return FieldSpec.builder(parameterizedCodec, "PACKET_CODEC")
		                .addModifiers(Modifier.PUBLIC, Modifier.STATIC, Modifier.FINAL)
		                .initializer(codecInitializer.build())
		                .build();
	}

	/**
	 * Retrieves the codec type for the given record component based on its type and optional {@link UseCodec} annotation.
	 *
	 * @param component The record component element for which the codec type is to be retrieved.
	 *
	 * @return The corresponding {@link CodecType} if a suitable codec is found, otherwise null.
	 */
	private CodecType getCodecType(RecordComponentElement component)
	{
		var annotation = Optional.ofNullable(component.getAnnotation(UseCodec.class));
		if (annotation.map(UseCodec::customCodec).orElse(null) instanceof CodecSource codecSource)
		{
			var customCodecType = new CodecType(ClassName.get(getCodecSourceType(codecSource)), codecSource.member());
			log("Custom codec requested for %s: %s".formatted(component.getSimpleName().toString(), customCodecType));
			return customCodecType;
		}

		var type = component.asType().toString();
		var codecTypeMap = codecTypes.get(type);

		if (codecTypeMap != null)
		{
			var requestedCodec = annotation
					.map(UseCodec::codec)
					.orElse(defaultCodecTypes.get(type));

			if (!codecTypeMap.containsKey(requestedCodec))
			{
				log("Found element %s of type %s, which is not supported by codec %s!".formatted(component.getSimpleName().toString(), type, requestedCodec));
				return null;
			}

			var codecType = codecTypeMap.get(requestedCodec);
			log("Found element %s of type %s, using codec: %s".formatted(component.getSimpleName().toString(), type, codecType));
			return codecType;
		}

		log("Found element %s of type %s, which has no supported codec!".formatted(component.getSimpleName().toString(), type));
		return null;
	}

	/**
	 * Retrieves the packet codec type for the given record component based on its type and optional {@link UseCodec} annotation.
	 *
	 * @param component The record component element for which the packet codec type is to be retrieved.
	 *
	 * @return The corresponding {@link CodecType} if a suitable packet codec is found, otherwise null.
	 */
	private CodecType getPacketCodecType(RecordComponentElement component)
	{
		var annotation = Optional.ofNullable(component.getAnnotation(UseCodec.class));
		if (annotation.map(UseCodec::customPacket).orElse(null) instanceof CodecSource codecSource)
		{
			var customCodecType = new CodecType(ClassName.get(getCodecSourceType(codecSource)), codecSource.member());
			log("Custom codec requested for %s: %s".formatted(component.getSimpleName().toString(), customCodecType));
			return customCodecType;
		}

		var type = component.asType().toString();
		var codecTypeMap = packetCodecTypes.get(type);

		if (codecTypeMap != null)
		{
			var requestedCodec = annotation
					.map(UseCodec::packet)
					.orElse(defaultPacketCodecTypes.get(type));

			if (!codecTypeMap.containsKey(requestedCodec))
			{
				log("Found element %s of type %s, which is not supported by packet codec %s!".formatted(component.getSimpleName().toString(), type, requestedCodec));
				return null;
			}

			var codecType = codecTypeMap.get(requestedCodec);
			log("Found element %s of type %s, using packet codec: %s".formatted(component.getSimpleName().toString(), type, codecType));
			return codecType;
		}

		log("Found element %s of type %s, which has no supported packet codec!".formatted(component.getSimpleName().toString(), type));
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

	private void writeInterfaceToFile(JavaFile file)
	{
		try
		{
			Path path = Paths.get(processingEnv.getFiler().getResource(StandardLocation.SOURCE_OUTPUT, "", "dummy").toUri());
			var dir = path.getParent();
			file.writeToFile(dir.toFile());
			log("Generated in " + dir);
		}
		catch (IOException e)
		{
			log("Failed: %s".formatted(e.getMessage()));
		}
	}

	@Override
	public Set<String> getSupportedAnnotationTypes()
	{
		return Set.of(GenerateCodec.class.getCanonicalName());
	}

	@Override
	public SourceVersion getSupportedSourceVersion()
	{
		return SourceVersion.latestSupported();
	}
}
