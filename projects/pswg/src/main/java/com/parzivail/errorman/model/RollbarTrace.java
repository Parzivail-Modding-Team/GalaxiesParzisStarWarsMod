package com.parzivail.errorman.model;

public record RollbarTrace(RollbarException exception, RollbarFrame[] frames)
{
}
