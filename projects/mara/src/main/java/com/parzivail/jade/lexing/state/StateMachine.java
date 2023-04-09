package com.parzivail.jade.lexing.state;

import com.parzivail.jade.lexing.TokenizeState;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.HashMap;

public class StateMachine
{
	private final HashMap<TokenizeState, Method> _states = new HashMap<>();

	private TokenizeState state;

	public StateMachine(TokenizeState init)
	{
		state = init;

		for (var method : getClass().getDeclaredMethods())
		{
			var sah = method.getAnnotation(StateArrivalHandler.class);
			if (sah != null)
				if (_states.put(sah.value(), method) != null)
					throw new RuntimeException(String.format("Multiple handlers registered for state arrival %s", sah.value()));
		}
	}

	public void setState(TokenizeState next)
	{
		state = next;
		run();
	}

	public void run()
	{
		try
		{
			var handler = _states.get(state);
			if (handler != null)
				handler.invoke(this);
		}
		catch (IllegalAccessException |
		       InvocationTargetException e)

		{
			throw new RuntimeException(e);
		}
	}

	public TokenizeState getState()
	{
		return state;
	}
}
