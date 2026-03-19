package dev.pswg.codecgenerator;

import com.google.auto.service.AutoService;
import com.palantir.javapoet.*;

import javax.annotation.processing.*;
import javax.lang.model.SourceVersion;
import javax.lang.model.element.*;
import javax.lang.model.type.MirroredTypeException;
import javax.lang.model.type.TypeMirror;
import javax.tools.Diagnostic;
import java.io.IOException;
import java.util.*;
import java.util.function.Function;

/**
 * An annotation processor that generates record codecs as an
 * implementable interface that defines CODEC and PACKET_CODEC
 * static fields.
 */
@AutoService(Processor.class)
@SupportedAnnotationTypes("dev.pswg.codecgenerator.GenerateCodec")
@SupportedSourceVersion(SourceVersion.RELEASE_25)
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

	/**
	 * The core Minecraft codecs class
	 */
	private static final TypeName MC_TYPES = ClassName.get("net.minecraft.util", "ExtraCodecs");
	private static final TypeName MC_PACKET_TYPES = ClassName.get("net.minecraft.network.codec", "ByteBufCodecs");

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
						GenStandardCodec.RGB, new CodecType(MC_TYPES, "RGB"),
						GenStandardCodec.ARGB, new CodecType(MC_TYPES, "ARGB"),
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
						GenStandardCodec.VECTOR_3F, new CodecType(MC_TYPES, "VECTOR3F")
				)
		);
		registerCodecsForType(
				"org.joml.Vector4f",
				GenStandardCodec.VECTOR_4F,
				Map.of(
						GenStandardCodec.VECTOR_4F, new CodecType(MC_TYPES, "VECTOR4F")
				)
		);
		registerCodecsForType(
				"org.joml.Quaternionf",
				GenStandardCodec.QUATERNION_F,
				Map.of(
						GenStandardCodec.QUATERNION_F, new CodecType(MC_TYPES, "QUATERNION_F"),
						GenStandardCodec.ROTATION, new CodecType(MC_TYPES, "ROTATION")
				)
		);
		registerCodecsForType(
				"org.joml.AxisAngle4f",
				GenStandardCodec.AXIS_ANGLE_4F,
				Map.of(
						GenStandardCodec.AXIS_ANGLE_4F, new CodecType(MC_TYPES, "AXIS_ANGLE_4F")
				)
		);
		registerCodecsForType(
				"org.joml.Matrix4f",
				GenStandardCodec.MATRIX_4F,
				Map.of(
						GenStandardCodec.MATRIX_4F, new CodecType(MC_TYPES, "MATRIX_4F")
				)
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
						GenPacketCodec.VECTOR_3F, new CodecType(MC_PACKET_TYPES, "VECTOR3F")
				)
		);
		registerPacketCodecsForType(
				"org.joml.Quaternionf",
				GenPacketCodec.QUATERNION_F,
				Map.of(
						GenPacketCodec.QUATERNION_F, new CodecType(MC_PACKET_TYPES, "QUATERNIONF")
				)
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
		                    .addOriginatingElement(classElement)
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
			var nestedCodecType = getCodec(
					component,
					UseCodec::customCodec,
					UseCodec::codec,
					GenStandardCodec.AUTOMATIC,
					codecTypes,
					defaultCodecTypes,
					"CODEC",
					"codec"
			);
			if (nestedCodecType == null)
				return null;

			if (!first)
				codecInitializer.add(",\n");
			first = false;

			String fieldInitializer;

			var codecDefault = component.getAnnotation(CodecDefault.class);
			if (codecDefault != null)
			{
				fieldInitializer = "optionalFieldOf($2S, %s)".formatted(codecDefault.value());
			}
			else
				fieldInitializer = "fieldOf($2S)";

			var classTypeName = nestedCodecType.className().toString();
			if (classTypeName.startsWith("java.util.List"))
			{
				var listArg = ClassName.bestGuess(classTypeName.substring("java.util.List<".length(), classTypeName.length() - 1));
				codecInitializer.add("$5T.listOrSingle($3T.$4L)." + fieldInitializer + ".forGetter($1T::$2L)", stateComponentType, component.getSimpleName().toString(), listArg, nestedCodecType.elementName(), MC_TYPES);
			}
			else
				codecInitializer.add("$3T.$4L." + fieldInitializer + ".forGetter($1T::$2L)", stateComponentType, component.getSimpleName().toString(), nestedCodecType.className(), nestedCodecType.elementName());
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
			
			// PacketCodecs.collection(ArrayList::new, PACKET_CODEC, 65536)
			var classTypeName = nestedCodecType.className().toString();
			if (classTypeName.startsWith("java.util.List"))
			{
				var arrayListType = ClassName.get("java.util", "ArrayList");

				var listArg = ClassName.bestGuess(classTypeName.substring("java.util.List<".length(), classTypeName.length() - 1));

				encodeBuilder.addStatement("$4T.collection(null, $1T.$2L, 65536).encode(registryByteBuf, value.$3L())", listArg, nestedCodecType.elementName(), component.getSimpleName().toString(), MC_PACKET_TYPES);
				decodeBuilder.addStatement("var $1L = $4T.collection($5T::new, $2T.$3L, 65536).decode(registryByteBuf)", paramName, listArg, nestedCodecType.elementName(), MC_PACKET_TYPES, arrayListType);
			}
			else
			{
				encodeBuilder.addStatement("$1T.$2L.encode(registryByteBuf, value.$3L())", nestedCodecType.className(), nestedCodecType.elementName(), component.getSimpleName().toString());
				decodeBuilder.addStatement("var $1L = $2T.$3L.decode(registryByteBuf)", paramName, nestedCodecType.className(), nestedCodecType.elementName());
			}
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
		if (component.getAnnotation(SelfCodec.class) != null)
		{
			var type = component.asType();
			var codecType = new CodecType(ClassName.get(type), memberName);
			log("Requested element %s of type %s use defined %s member within type, using %s: %s".formatted(component.getSimpleName().toString(), type, memberName, friendlyName, codecType));
			return codecType;
		}

		var useCodecInstance = Optional.ofNullable(component.getAnnotation(UseCodec.class));

		// If a custom codec is requested, it takes the highest precedence
		if (useCodecInstance.map(customCodecGetter).filter(src -> !"".equals(src.member())).orElse(null) instanceof CodecSource codecSource)
		{
			var customCodecType = new CodecType(ClassName.bestGuess(getCodecSourceType(codecSource).toString()), codecSource.member());
			log("Custom %s requested for %s: %s".formatted(friendlyName, component.getSimpleName().toString(), customCodecType));
			return customCodecType;
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
