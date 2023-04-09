package com.parzivail.mara.lexing.state;

import com.parzivail.mara.lexing.TokenizeState;

public record StateTransition(TokenizeState from, TokenizeState to)
{
}
