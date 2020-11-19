package com.parzivail.pswg.client.texture.remote;

import org.apache.commons.io.FilenameUtils;
import org.apache.commons.lang3.builder.ToStringBuilder;

public class RemoteTextureUrl
{
	private final String url;

	public RemoteTextureUrl(final String url)
	{
		this.url = url;
	}

	public String getUrl()
	{
		return url;
	}

	public String getHash()
	{
		return FilenameUtils.getBaseName(url);
	}

	@Override
	public String toString()
	{
		return new ToStringBuilder(this)
				.append("url", url)
				.append("hash", getHash())
				.toString();
	}
}
