package dev.pswg.errorman.model;

public record RollbarResponse(int err, String message, RollbarResponseResult result)
{
}
