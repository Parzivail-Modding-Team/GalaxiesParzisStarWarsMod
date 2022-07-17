package com.parzivail.util.client.texture.remote;

import org.apache.commons.io.FilenameUtils;
import org.apache.commons.lang3.builder.ToStringBuilder;

public record RemoteTextureUrl(String url)
{
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
