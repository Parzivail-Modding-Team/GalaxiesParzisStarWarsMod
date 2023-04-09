package com.parzivail.jade.lexing.state;

import com.parzivail.jade.lexing.TokenizeState;

public record StateTransition(TokenizeState from, TokenizeState to)
{
}
