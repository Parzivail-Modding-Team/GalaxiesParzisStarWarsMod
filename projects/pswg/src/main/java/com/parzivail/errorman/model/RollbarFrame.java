package com.parzivail.errorman.model;

public record RollbarFrame(String filename, String class_name, String method, int lineno)
{
}
