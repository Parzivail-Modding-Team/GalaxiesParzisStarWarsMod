package com.parzivail.errorman.model;

public record RollbarResponse(int err, String message, RollbarResponseResult result)
{
}
