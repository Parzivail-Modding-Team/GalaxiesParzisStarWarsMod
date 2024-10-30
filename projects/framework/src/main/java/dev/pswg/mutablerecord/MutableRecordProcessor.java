package dev.pswg.mutablerecord;

import com.google.auto.service.AutoService;
import com.squareup.javapoet.*;

import javax.annotation.processing.AbstractProcessor;
import javax.annotation.processing.Processor;
import javax.annotation.processing.RoundEnvironment;
import javax.lang.model.SourceVersion;
import javax.lang.model.element.*;
import javax.tools.Diagnostic;
import javax.tools.StandardLocation;
import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Set;

/**
 * An annotation processor that generates record builders
 * and mutators
 */
@AutoService(Processor.class)
public class MutableRecordProcessor extends AbstractProcessor
{
	private void log(String message)
	{
		processingEnv.getMessager().printMessage(Diagnostic.Kind.NOTE, "(MutableRecord AP) %s".formatted(message));
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
					var interfaceName = "I" + className + "Builder";

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
		return "dev.pswg.generated.recordbuilders";
	}

	private JavaFile generateInterface(String packageName, TypeElement classElement, String interfaceName)
	{
		var iface = TypeSpec.interfaceBuilder(interfaceName)
		                    .addModifiers(Modifier.PUBLIC);

		for (var component : classElement.getRecordComponents())
		{
			iface.addMethod(generateGetterMethod(component));
			iface.addMethod(generateBuilderMethod(classElement, component));
		}

		return JavaFile.builder(packageName, iface.build())
		               .indent("\t")
		               .build();
	}

	/**
	 * Generates a getter method for a specified record component.
	 *
	 * @param component The record component element for which the getter method is generated.
	 *
	 * @return A method for the generated getter method.
	 */
	private MethodSpec generateGetterMethod(RecordComponentElement component)
	{
		var componentName = component.getSimpleName().toString();
		var componentType = TypeName.get(component.asType());

		return MethodSpec.methodBuilder(componentName)
		                 .addModifiers(Modifier.PUBLIC, Modifier.ABSTRACT)
		                 .returns(componentType)
		                 .build();
	}

	/**
	 * Generates a builder method for a specified record component in the given class.
	 *
	 * @param classElement The class element representing the record.
	 * @param component    The specific record component for which the builder method is generated.
	 *
	 * @return A method for the generated builder method.
	 */
	private MethodSpec generateBuilderMethod(TypeElement classElement, RecordComponentElement component)
	{
		var componentName = component.getSimpleName().toString();
		var componentType = TypeName.get(component.asType());
		var builderMethodName = "with" + capitalize(componentName);

		var methodBuilder = MethodSpec.methodBuilder(builderMethodName)
		                              .addModifiers(Modifier.PUBLIC, Modifier.DEFAULT)
		                              .returns(TypeName.get(classElement.asType()))
		                              .addParameter(componentType, componentName);

		var returnStatement = CodeBlock.builder()
		                               .add("return new $T(", TypeName.get(classElement.asType()));

		var first = true;
		for (var recordComponent : classElement.getRecordComponents())
		{
			if (!first)
				returnStatement.add(", ");
			first = false;

			if (recordComponent.equals(component))
				// return the passed parameter
				returnStatement.add("$L", componentName);
			else
				// return the existing record component
				returnStatement.add("$L()", recordComponent.getSimpleName().toString());
		}

		returnStatement.add(")");

		methodBuilder.addStatement(returnStatement.build());

		return methodBuilder.build();
	}

	/**
	 * Capitalizes the first character of the given string.
	 *
	 * @param str The string to capitalize
	 *
	 * @return The capitalized string or the original string if it is null or empty
	 */
	private String capitalize(String str)
	{
		if (str == null || str.isEmpty())
			return str;

		return str.substring(0, 1).toUpperCase() + str.substring(1);
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
		return Set.of(MutableRecord.class.getCanonicalName());
	}

	@Override
	public SourceVersion getSupportedSourceVersion()
	{
		return SourceVersion.latestSupported();
	}
}
