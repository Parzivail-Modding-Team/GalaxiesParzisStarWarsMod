package com.parzivail.errorman.model;

public record RollbarData(String environment, String code_version, String platform, String language, RollbarBody body, RollbarRequestInfo request)
{
}
