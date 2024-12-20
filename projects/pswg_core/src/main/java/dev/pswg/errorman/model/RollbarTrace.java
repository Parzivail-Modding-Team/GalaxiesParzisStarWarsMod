package dev.pswg.errorman.model;

public record RollbarTrace(RollbarException exception, RollbarFrame[] frames)
{
}
