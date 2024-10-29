package dev.pswg.codecgenerator;

import com.google.auto.service.AutoService;
import com.squareup.javapoet.*;

import javax.annotation.processing.AbstractProcessor;
import javax.annotation.processing.Processor;
import javax.annotation.processing.RoundEnvironment;
import javax.lang.model.SourceVersion;
import javax.lang.model.element.Element;
import javax.lang.model.element.ElementKind;
import javax.lang.model.element.Modifier;
import javax.lang.model.element.TypeElement;
import javax.tools.Diagnostic;
import javax.tools.StandardLocation;
import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Set;

/**
 * An annotation processor that generates record codecs as an
 * implementable interface that defines CODEC and PACKET_CODEC
 * static fields.
 */
@AutoService(Processor.class)
public class CodecGenerationProcessor extends AbstractProcessor
{
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
					log("Processing class: " + classElement.getQualifiedName());

					//					GenerateCodec annotation = classElement.getAnnotation(GenerateCodec.class);

					var className = classElement.getSimpleName().toString();
					var packageName = getPackageName(classElement);
					var interfaceName = "I" + className + "Codec";

					var generatedInterface = generateInterface(packageName, classElement, interfaceName);
					writeInterfaceToFile(packageName, generatedInterface);
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
		var iface = TypeSpec.interfaceBuilder(interfaceName)
		                    .addModifiers(Modifier.PUBLIC)
		                    .addField(generateCodec(classElement))
		                    //		                    .addField(generatePacketCodec(classElement))
		                    .build();

		return JavaFile.builder(packageName, iface)
		               .indent("\t")
		               .build();
	}

	/*
	public static final Codec<BlasterItem.StateComponent> CODEC = RecordCodecBuilder.create(
				instance -> instance.group(
						                    Codec.BOOL.fieldOf("isAiming").forGetter(BlasterItem.StateComponent::isAiming),
						                    Codec.LONG.fieldOf("lastFired").forGetter(BlasterItem.StateComponent::lastFired),
						                    Codec.LONG.fieldOf("fireCooldown").forGetter(BlasterItem.StateComponent::fireCooldown)
				                    )
				                    .apply(instance, BlasterItem.StateComponent::new)
		);
	 */

	private FieldSpec generateCodec(TypeElement classElement)
	{
		var stateComponentType = TypeName.get(classElement.asType());
		var codecType = ClassName.get("com.mojang.serialization", "Codec");
		var parameterizedCodec = ParameterizedTypeName.get(codecType, stateComponentType);

		var recordCodecBuilder = ClassName.get("com.mojang.serialization.codecs", "RecordCodecBuilder");

		var codecInitializer = CodeBlock.builder()
		                                .add("$T.create(instance -> instance.group(\n", recordCodecBuilder)
		                                .indent();

		codecInitializer.add("$T.BOOL.fieldOf(\"isAiming\").forGetter($T::isAiming),\n", codecType, stateComponentType);
		codecInitializer.add("$T.LONG.fieldOf(\"lastFired\").forGetter($T::lastFired),\n", codecType, stateComponentType);
		codecInitializer.add("$T.LONG.fieldOf(\"fireCooldown\").forGetter($T::fireCooldown)\n", codecType, stateComponentType);

		codecInitializer.unindent()
		                .add(").apply(instance, $T::new))", stateComponentType);

		return FieldSpec.builder(parameterizedCodec, "CODEC")
		                .addModifiers(Modifier.PUBLIC, Modifier.STATIC, Modifier.FINAL)
		                .initializer(codecInitializer.build())
		                .build();
	}

	private FieldSpec generatePacketCodec(TypeElement classElement)
	{
		return FieldSpec.builder(TypeName.OBJECT, "PACKET_CODEC")
		                .addModifiers(Modifier.PUBLIC, Modifier.STATIC, Modifier.FINAL)
		                .build();
	}

	private void writeInterfaceToFile(String packageName, JavaFile file)
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
