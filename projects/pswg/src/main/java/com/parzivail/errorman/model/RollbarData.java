package com.parzivail.errorman.model;

public record RollbarData(String environment, String platform, String language, RollbarBody body, RollbarRequestInfo request)
{
}
