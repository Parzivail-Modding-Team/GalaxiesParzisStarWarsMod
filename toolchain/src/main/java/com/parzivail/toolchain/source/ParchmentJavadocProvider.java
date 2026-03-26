package com.parzivail.toolchain.source;

import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import net.fabricmc.fernflower.api.IFabricJavadocProvider;
import org.jetbrains.java.decompiler.main.DecompilerContext;
import org.jetbrains.java.decompiler.modules.decompiler.vars.VarVersionPair;
import org.jetbrains.java.decompiler.struct.StructClass;
import org.jetbrains.java.decompiler.struct.StructField;
import org.jetbrains.java.decompiler.struct.StructMethod;

import java.io.File;
import java.io.IOException;
import java.util.*;

/**
 * Supplies Parchment Javadoc overlays to Vineflower for official-name jars.
 */
public final class ParchmentJavadocProvider implements IFabricJavadocProvider
{
	/**
	 * The access flag used to identify static fields.
	 */
	private static final int ACC_STATIC = 0x0008;

	/**
	 * The access flag used to identify record classes.
	 */
	private static final int ACC_RECORD = 0x10000;

	/**
	 * The documented classes keyed by internal class name.
	 */
	private final Map<String, ParchmentClassEntry> _classes;

	/**
	 * Creates a provider backed by an extracted Parchment JSON export.
	 *
	 * @param parchmentJsonFile the extracted Parchment JSON file
	 */
	public ParchmentJavadocProvider(File parchmentJsonFile)
	{
		_classes = loadClasses(parchmentJsonFile);
	}

	@Override
	public String getClassDoc(StructClass structClass)
	{
		var classEntry = _classes.get(structClass.qualifiedName);

		if (classEntry == null)
		{
			return null;
		}

		if (!isRecord(structClass))
		{
			return classEntry.classDoc();
		}

		List<String> parts = new ArrayList<>();

		if (classEntry.classDoc() != null)
		{
			parts.add(classEntry.classDoc());
		}

		var addedParam = false;

		for (var component : structClass.getRecordComponents())
		{
			var field = classEntry.fields().get(fieldKey(component.getName(), component.getDescriptor()));

			if (field == null || joinLines(field.javadoc()) == null)
			{
				continue;
			}

			if (!addedParam && classEntry.classDoc() != null)
			{
				parts.add("");
				addedParam = true;
			}

			parts.add("@param " + component.getName() + " " + joinLines(field.javadoc()));
		}

		return parts.isEmpty() ? null : String.join("\n", parts);
	}

	@Override
	public String getFieldDoc(StructClass structClass, StructField structField)
	{
		if (isRecord(structClass) && !isStatic(structField))
		{
			return null;
		}

		var classEntry = _classes.get(structClass.qualifiedName);

		if (classEntry == null)
		{
			return null;
		}

		var field = classEntry.fields().get(fieldKey(structField.getName(), structField.getDescriptor()));
		return field != null ? joinLines(field.javadoc()) : null;
	}

	@Override
	public String getMethodDoc(StructClass structClass, StructMethod structMethod)
	{
		var classEntry = _classes.get(structClass.qualifiedName);

		if (classEntry == null)
		{
			return null;
		}

		var method = classEntry.methods().get(methodKey(structMethod.getName(), structMethod.getDescriptor()));

		if (method == null)
		{
			return null;
		}

		List<String> parts = new ArrayList<>();
		var methodDoc = joinLines(method.javadoc());

		if (methodDoc != null)
		{
			parts.add(methodDoc);
		}

		var addedParam = false;

		if (method.parameters() != null)
		{
			for (var parameter : method.parameters())
			{
				if (parameter.javadoc() == null || parameter.javadoc().isBlank())
				{
					continue;
				}

				if (!addedParam && methodDoc != null)
				{
					parts.add("");
					addedParam = true;
				}

				parts.add("@param " + resolveParameterName(structMethod, parameter) + " " + parameter.javadoc());
			}
		}

		return parts.isEmpty() ? null : String.join("\n", parts);
	}

	/**
	 * Loads Parchment classes into class/member lookup tables.
	 *
	 * @param parchmentJsonFile the extracted Parchment JSON file
	 *
	 * @return the documented classes keyed by internal name
	 */
	private static Map<String, ParchmentClassEntry> loadClasses(File parchmentJsonFile)
	{
		try
		{
			var objectMapper = new ObjectMapper();
			objectMapper.disable(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES);
			var parchmentData = objectMapper.readValue(parchmentJsonFile, ParchmentData.class);
			Map<String, ParchmentClassEntry> classes = new HashMap<>();

			if (parchmentData.classes() == null)
			{
				return classes;
			}

			for (var parchmentClass : parchmentData.classes())
			{
				Map<String, ParchmentData.ParchmentField> fields = new HashMap<>();
				Map<String, ParchmentData.ParchmentMethod> methods = new HashMap<>();

				if (parchmentClass.fields() != null)
				{
					for (var field : parchmentClass.fields())
					{
						fields.put(fieldKey(field.name(), field.descriptor()), field);
					}
				}

				if (parchmentClass.methods() != null)
				{
					for (var method : parchmentClass.methods())
					{
						methods.put(methodKey(method.name(), method.descriptor()), method);
					}
				}

				classes.put(
						parchmentClass.name(),
						new ParchmentClassEntry(joinLines(parchmentClass.javadoc()), fields, methods)
				);
			}

			return classes;
		}
		catch (IOException exception)
		{
			throw new IllegalStateException("Failed to read Parchment export from " + parchmentJsonFile, exception);
		}
	}

	/**
	 * Joins a list of Javadoc lines into one Vineflower documentation block.
	 *
	 * @param lines the Javadoc lines
	 *
	 * @return the joined documentation string, or {@code null} when empty
	 */
	private static String joinLines(List<String> lines)
	{
		if (lines == null || lines.isEmpty())
		{
			return null;
		}

		return String.join("\n", lines);
	}

	/**
	 * Builds a stable field lookup key.
	 *
	 * @param name       the field name
	 * @param descriptor the field descriptor
	 *
	 * @return the field lookup key
	 */
	private static String fieldKey(String name, String descriptor)
	{
		return name + descriptor;
	}

	/**
	 * Builds a stable method lookup key.
	 *
	 * @param name       the method name
	 * @param descriptor the method descriptor
	 *
	 * @return the method lookup key
	 */
	private static String methodKey(String name, String descriptor)
	{
		return name + descriptor;
	}

	/**
	 * Resolves the parameter name currently used by Vineflower for a method.
	 *
	 * @param structMethod the method being documented
	 * @param parameter    the documented Parchment parameter entry
	 *
	 * @return the decompiled parameter name when available, otherwise the normalized Parchment name
	 */
	private static String resolveParameterName(StructMethod structMethod, ParchmentData.ParchmentParameter parameter)
	{
		var methodWrapper = DecompilerContext.getContextProperty(DecompilerContext.CURRENT_METHOD_WRAPPER);

		if (methodWrapper == null || methodWrapper.methodStruct != structMethod)
		{
			return stripMethodArg(parameter.name());
		}

		var descriptor = methodWrapper.desc();
		var accessFlags = structMethod.getAccessFlags();
		var index = structMethod.hasModifier(ACC_STATIC) ? 0 : 1;

		for (var parameterType : descriptor.params)
		{
			if (index == parameter.index())
			{
				var parameterName = methodWrapper.varproc.getVarName(new VarVersionPair(index, 0));
				var clashingName = methodWrapper.varproc.getClashingName(new VarVersionPair(index, 0));

				if (clashingName != null)
				{
					parameterName = clashingName;
				}

				if (parameterName == null)
				{
					return stripMethodArg(parameter.name());
				}

				return structMethod.getVariableNamer().renameParameter(accessFlags, parameterType, parameterName, index);
			}

			index += parameterType.stackSize;
		}

		return stripMethodArg(parameter.name());
	}

	/**
	 * Returns whether a class is a record.
	 *
	 * @param structClass the class to inspect
	 *
	 * @return {@code true} when the class uses the record access flag
	 */
	private static boolean isRecord(StructClass structClass)
	{
		return (structClass.getAccessFlags() & ACC_RECORD) != 0;
	}

	/**
	 * Returns whether a field is static.
	 *
	 * @param structField the field to inspect
	 *
	 * @return {@code true} when the field uses the static access flag
	 */
	private static boolean isStatic(StructField structField)
	{
		return (structField.getAccessFlags() & ACC_STATIC) != 0;
	}

	/**
	 * Strips the synthetic `p` prefix used by some Parchment parameter names.
	 *
	 * @param argumentName the published Parchment parameter name
	 *
	 * @return the normalized parameter name
	 */
	private static String stripMethodArg(String argumentName)
	{
		if (argumentName != null
		    && argumentName.length() > 1
		    && argumentName.startsWith("p")
		    && Character.isUpperCase(argumentName.charAt(1)))
		{
			var withoutPrefix = argumentName.substring(1);
			return withoutPrefix.substring(0, 1).toLowerCase(Locale.ROOT) + withoutPrefix.substring(1);
		}

		return argumentName;
	}

	/**
	 * The cached Parchment entries for one class.
	 *
	 * @param classDoc the joined class documentation
	 * @param fields   the documented fields keyed by name and descriptor
	 * @param methods  the documented methods keyed by name and descriptor
	 */
	private record ParchmentClassEntry(
			String classDoc,
			Map<String, ParchmentData.ParchmentField> fields,
			Map<String, ParchmentData.ParchmentMethod> methods
	)
	{
	}
}
