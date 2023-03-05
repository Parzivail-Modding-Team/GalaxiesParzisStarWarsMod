package com.parzivail.errorman.model;

import com.google.gson.annotations.SerializedName;

public record RollbarException(@SerializedName("class") String clazz, String message)
{
}
