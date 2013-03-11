package com.emptyPockets.main;

import com.badlogic.gdx.Game;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.InputMultiplexer;
import com.emptyPockets.bodyEditor.main.EntityEditorScreen;
import com.emptyPockets.box2d.gui.bodyEditor.BodyEditorScreen;
import com.emptyPockets.gui.GameScreen;
import com.emptyPockets.gui.BasicCarTestScreen;
import com.emptyPockets.gui.Network;
import com.emptyPockets.gui.NetworkScreen;
import com.emptyPockets.test.LasersTesting;
import com.emptyPockets.utils.file.FileSelector;

public class ProgramMain extends Game{
	FileSelector fileSelector;
	
	InputMultiplexer input;
	@Override
	public void create() {
		input = new InputMultiplexer();
		Gdx.input.setInputProcessor(input);
//		BasicCarTestScreen screen = new BasicCarTestScreen(input);
//		NetworkScreen screen = new NetworkScreen(input);
//		Network screen = new Network(input);
//		EntityEditorScreen screen = new EntityEditorScreen(input);
//		BodyEditorScreen screen = new BodyEditorScreen(input);
		LasersTesting screen = new LasersTesting(input);
		setScreen(screen);
	}
	
}