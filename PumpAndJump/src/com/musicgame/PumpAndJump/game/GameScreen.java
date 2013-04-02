package com.musicgame.PumpAndJump.game;

import java.util.ArrayList;

import com.badlogic.gdx.Screen;

public class GameScreen implements Screen
{
	ArrayList<GameThread> currentThreads;
	@Override
	public void render(float delta)
	{
		for(GameThread thread:currentThreads)
		{
			thread.render(delta);
		}
	}

	@Override
	public void resize(int width, int height)
	{
	}

	@Override
	public void show() {
	}

	@Override
	public void hide() {
	}

	@Override
	public void pause() {
	}

	@Override
	public void resume() {
	}

	@Override
	public void dispose() {
	}

}
