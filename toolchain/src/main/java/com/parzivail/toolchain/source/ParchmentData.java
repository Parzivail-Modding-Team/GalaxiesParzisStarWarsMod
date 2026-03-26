package com.parzivail.toolchain.source;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import java.util.List;

/**
 * A Parchment export file containing parameter names and Javadoc overlays for Mojmap names.
 *
 * @param version  the Parchment schema version
 * @param classes  the documented class entries
 * @param packages the documented package entries
 */
@JsonIgnoreProperties(ignoreUnknown = true)
public record ParchmentData(
		String version,
		List<ParchmentClass> classes,
		List<ParchmentPackage> packages
)
{
	/**
	 * A documented class entry in the Parchment export.
	 *
	 * @param name    the internal class name in Mojmap naming
	 * @param fields  the documented fields for the class
	 * @param methods the documented methods for the class
	 * @param javadoc the class Javadoc lines
	 */
	@JsonIgnoreProperties(ignoreUnknown = true)
	public record ParchmentClass(
			String name,
			List<ParchmentField> fields,
			List<ParchmentMethod> methods,
			List<String> javadoc
	)
	{
	}

	/**
	 * A documented field entry in the Parchment export.
	 *
	 * @param name       the field name in Mojmap naming
	 * @param descriptor the field descriptor
	 * @param javadoc    the field Javadoc lines
	 */
	@JsonIgnoreProperties(ignoreUnknown = true)
	public record ParchmentField(
			String name,
			String descriptor,
			List<String> javadoc
	)
	{
	}

	/**
	 * A documented method entry in the Parchment export.
	 *
	 * @param name       the method name in Mojmap naming
	 * @param descriptor the method descriptor
	 * @param parameters the documented method parameters
	 * @param javadoc    the method Javadoc lines
	 */
	@JsonIgnoreProperties(ignoreUnknown = true)
	public record ParchmentMethod(
			String name,
			String descriptor,
			List<ParchmentParameter> parameters,
			List<String> javadoc
	)
	{
	}

	/**
	 * A documented parameter entry in the Parchment export.
	 *
	 * @param index   the parameter slot index
	 * @param name    the Parchment parameter name
	 * @param javadoc the optional parameter Javadoc line
	 */
	@JsonIgnoreProperties(ignoreUnknown = true)
	public record ParchmentParameter(
			int index,
			String name,
			String javadoc
	)
	{
	}

	/**
	 * A documented package entry in the Parchment export.
	 *
	 * @param name    the package name
	 * @param javadoc the package Javadoc lines
	 */
	@JsonIgnoreProperties(ignoreUnknown = true)
	public record ParchmentPackage(
			String name,
			List<String> javadoc
	)
	{
	}
}
