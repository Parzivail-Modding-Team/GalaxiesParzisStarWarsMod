package dev.pswg.codecgenerator;

import com.google.auto.service.AutoService;
import com.palantir.javapoet.*;

import javax.annotation.processing.*;
import javax.lang.model.SourceVersion;
import javax.lang.model.element.Element;
import javax.lang.model.element.ElementKind;
import javax.lang.model.element.Modifier;
import javax.lang.model.element.TypeElement;
import javax.tools.Diagnostic;
import java.io.IOException;
import java.util.HashSet;
import java.util.Locale;
import java.util.Set;

/**
 * Generates {@code StringRepresentable} codec interfaces for annotated enums.
 */
@AutoService(Processor.class)
@SupportedAnnotationTypes("dev.pswg.codecgenerator.GenerateEnumCodec")
@SupportedSourceVersion(SourceVersion.RELEASE_25)
public class EnumCodecGenerationProcessor extends AbstractProcessor
{
	/**
	 * The package containing generated codec interfaces.
	 */
	private static final String _generatedPackage = "dev.pswg.generated.codecs";

	/**
	 * Minecraft's string-representable enum contract.
	 */
	private static final ClassName _stringRepresentable = ClassName.get("net.minecraft.util", "StringRepresentable");

	/**
	 * Mojang's standard codec type.
	 */
	private static final ClassName _codec = ClassName.get("com.mojang.serialization", "Codec");

	/**
	 * Netty's byte buffer type used by packet codecs.
	 */
	private static final ClassName _byteBuf = ClassName.get("io.netty.buffer", "ByteBuf");

	/**
	 * Minecraft's packet codec factory.
	 */
	private static final ClassName _byteBufCodecs = ClassName.get("net.minecraft.network.codec", "ByteBufCodecs");

	/**
	 * Minecraft's stream codec type.
	 */
	private static final ClassName _streamCodec = ClassName.get("net.minecraft.network.codec", "StreamCodec");

	/**
	 * Generated names already handled in this processing run.
	 */
	private final Set<String> _generatedTypes = new HashSet<>();

	/**
	 * Processes each annotated enum and writes its generated codec interface.
	 */
	@Override
	public boolean process(Set<? extends TypeElement> annotations, RoundEnvironment roundEnvironment)
	{
		for (var element : roundEnvironment.getElementsAnnotatedWith(GenerateEnumCodec.class))
		{
			if (!(element instanceof TypeElement typeElement) || element.getKind() != ElementKind.ENUM)
			{
				processingEnv.getMessager().printMessage(
						Diagnostic.Kind.ERROR,
						"@GenerateEnumCodec can only be applied to enums",
						element
				);
				continue;
			}

			var generatedName = "I" + typeElement.getSimpleName() + "Codec";
			var generatedQualifiedName = _generatedPackage + "." + generatedName;
			if (!_generatedTypes.add(generatedQualifiedName))
				continue;

			var serializedNames = new HashSet<String>();
			var enumConstants = typeElement.getEnclosedElements().stream()
			                               .filter(enclosedElement -> enclosedElement.getKind() == ElementKind.ENUM_CONSTANT)
			                               .toList();
			var validEnum = true;
			for (var enumConstant : enumConstants)
			{
				var nameAnnotation = enumConstant.getAnnotation(CodecName.class);
				var serializedName = nameAnnotation == null
				                     ? enumConstant.getSimpleName().toString().toLowerCase(Locale.ROOT)
				                     : nameAnnotation.value();
				if (!serializedNames.add(serializedName))
				{
					processingEnv.getMessager().printMessage(
							Diagnostic.Kind.ERROR,
							"Duplicate enum serialized name '" + serializedName + "'",
							enumConstant
					);
					validEnum = false;
				}
			}

			if (validEnum)
				generateCodecInterface(typeElement, generatedName, enumConstants);
		}

		return true;
	}

	/**
	 * Writes the generated interface for one enum.
	 */
	private void generateCodecInterface(TypeElement enumElement, String generatedName, java.util.List<? extends Element> enumConstants)
	{
		var enumType = ClassName.get(enumElement);
		var codecInterface = TypeSpec.interfaceBuilder(generatedName)
		                             .addModifiers(Modifier.PUBLIC)
		                             .addOriginatingElement(enumElement)
		                             .addSuperinterface(_stringRepresentable)
		                             .addField(FieldSpec.builder(
				                             ParameterizedTypeName.get(_codec, enumType),
				                             "CODEC",
				                             Modifier.PUBLIC,
				                             Modifier.STATIC,
				                             Modifier.FINAL
		                             ).initializer("$T.lazyInitialized(() -> $T.fromEnum($T::values))", _codec, _stringRepresentable, enumType).build())
		                             .addField(FieldSpec.builder(
				                             ParameterizedTypeName.get(_streamCodec, _byteBuf, enumType),
				                             "PACKET_CODEC",
				                             Modifier.PUBLIC,
				                             Modifier.STATIC,
				                             Modifier.FINAL
		                             ).initializer(
				                             "$T.idMapper(index -> $T.values()[index], value -> value.ordinal())",
				                             _byteBufCodecs,
				                             enumType
		                             ).build())
		                             .addMethod(serializedNameMethod(enumType, enumConstants))
		                             .build();

		try
		{
			JavaFile.builder(_generatedPackage, codecInterface)
			        .indent("\t")
			        .build()
			        .writeTo(processingEnv.getFiler());
		}
		catch (IOException exception)
		{
			processingEnv.getMessager().printMessage(
					Diagnostic.Kind.ERROR,
					"Failed to generate enum codec interface: " + exception.getMessage(),
					enumElement
			);
		}
	}

	/**
	 * Creates the per-constant serialized-name implementation.
	 */
	private MethodSpec serializedNameMethod(ClassName enumType, java.util.List<? extends Element> enumConstants)
	{
		var method = MethodSpec.methodBuilder("getSerializedName")
		                       .addAnnotation(Override.class)
		                       .addModifiers(Modifier.PUBLIC, Modifier.DEFAULT)
		                       .returns(String.class)
		                       .addStatement("$T value = ($T) this", enumType, enumType);

		var switchBlock = CodeBlock.builder()
		                           .beginControlFlow("return switch (value)");
		for (var enumConstant : enumConstants)
		{
			var nameAnnotation = enumConstant.getAnnotation(CodecName.class);
			var serializedName = nameAnnotation == null
			                     ? enumConstant.getSimpleName().toString().toLowerCase(Locale.ROOT)
			                     : nameAnnotation.value();
			switchBlock.addStatement("case $T.$L -> $S", enumType, enumConstant.getSimpleName(), serializedName);
		}
		switchBlock.endControlFlow().add(";\n");
		return method.addCode(switchBlock.build()).build();
	}
}
