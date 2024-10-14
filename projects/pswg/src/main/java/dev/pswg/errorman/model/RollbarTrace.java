package dev.pswg.errorman.model;

public record RollbarTrace(RollbarException exception, com.parzivail.errorman.model.RollbarFrame[] frames)
{
}
