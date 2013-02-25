package com.emptyPockets.bodyEditor.main;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.InputMultiplexer;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.GL10;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer.ShapeType;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.emptyPockets.bodyEditor.entity.Entity;
import com.emptyPockets.bodyEditor.entity.PolygonEntity;
import com.emptyPockets.bodyEditor.main.controls.EntityEditorControlsManager;
import com.emptyPockets.graphics.GraphicsToolkit;
import com.emptyPockets.gui.StageScreen;

public class EntityEditorScreen extends StageScreen {
	TextureAtlas textureAtlas;
	
	EntityEditorControlsManager controls;
	Entity entity;
	
	//Temp for camera projections 
	Vector3 _tmpCam2MouseVec = new Vector3();
	Vector2 _panelToCamsizeA = new Vector2();
	Vector2 _panelToCamsizeB = new Vector2();
	
	OrthographicCamera editorCamera;
	ShapeRenderer shape;
	boolean snapToGrid = true;
	float gridSize = 10;
	
	public EntityEditorScreen(InputMultiplexer inputMultiplexer) {
		super(inputMultiplexer);
		setClearColor(Color.DARK_GRAY);
		
		editorCamera = new OrthographicCamera(Gdx.graphics.getWidth(), Gdx.graphics.getHeight());
		
		controls = new EntityEditorControlsManager(this);
		controls.update();
		
		setEntity(new PolygonEntity());
	}

	@Override
	public void createStage(Stage stage) {
		stage.addActor(controls);
	}

	@Override
	public void show() {
		super.show();
		shape = new ShapeRenderer();
	}
	
	@Override
	public void hide() {
		super.hide();
		shape.dispose();
		shape = null;
	}
	
	@Override
	public void addInputMultiplexer(InputMultiplexer input) {
		super.addInputMultiplexer(input);
		input.addProcessor(controls.getInputMultiplexer());		
	}
	
	@Override
	public void removeInputMultiplexer(InputMultiplexer input) {
		super.removeInputMultiplexer(input);
		input.removeProcessor(controls.getInputMultiplexer());		
	}
	
	@Override
	public void resize(int width, int height) {
		super.resize(width, height);
		controls.resize(width,height);
		editorCamera.viewportWidth = width;
		editorCamera.viewportHeight = height;
	}

	@Override
	public void drawBackground(float delta) {
        Gdx.gl.glLineWidth(1f);
		editorCamera.update();
		shape.setProjectionMatrix(editorCamera.combined);
		
		Color major = Color.WHITE;
		major.a = 1;
		
		Color minor = Color.WHITE;
		minor.a = 0.5f;
		
		Color grid = Color.WHITE;
		grid.a = 0.1f;
		
		GraphicsToolkit.drawGrid(shape, editorCamera,gridSize, grid);
		GraphicsToolkit.drawGrid(shape, editorCamera,5*gridSize, minor);
		GraphicsToolkit.drawGrid(shape, editorCamera,10*gridSize, major);
		GraphicsToolkit.drawAxis(shape, editorCamera);
	}
	
	@Override
	public void drawScreen(float delta) {
        Gdx.gl.glLineWidth(1f);
		editorCamera.update();
		shape.setProjectionMatrix(editorCamera.combined);
		
		Entity entity = getEntity();

		
		controls.drawScreen(shape, entity);
	}
	
	@Override
	public void drawOverlay(float delta) {
		// TODO Auto-generated method stub

	    long lastFlip = 0;
	    long flipTime = 1000;
	    boolean display = false;
	    
	    //..In your rendering loop
	    if(lastFlip+flipTime > System.currentTimeMillis()){
	         display = false;
	         lastFlip = System.currentTimeMillis();
	    }
	}
	
	public void updateScreenControl(){
		controls.update();
	}

	public Entity getEntity() {
		return entity;
	}

	public void setEntity(Entity entity) {
		this.entity = entity;
		updateScreenControl();
	}

	public OrthographicCamera getEditorCamera() {
		return editorCamera;
	}

	public boolean isSnapToGrid() {
		return snapToGrid;
	}

	public void setSnapToGrid(boolean snapToGrid) {
		this.snapToGrid = snapToGrid;
	}

	public float getGridSize() {
		return gridSize;
	}

	public void setGridSize(float gridSize) {
		this.gridSize = gridSize;
	}

	
	public void camToPanel(float x, float y, Vector2 vec){
		synchronized (_tmpCam2MouseVec) {
			_tmpCam2MouseVec.set(x,y,0);
			editorCamera.unproject(_tmpCam2MouseVec);
			vec.x = _tmpCam2MouseVec.x;
			vec.y = _tmpCam2MouseVec.y;
		}
	}
	
	public void panelToCam(float x, float y, Vector2 vec){
		synchronized (_tmpCam2MouseVec) {
			_tmpCam2MouseVec.set(x,y,0);
			editorCamera.project(_tmpCam2MouseVec);
			vec.x = _tmpCam2MouseVec.x;
			vec.y = _tmpCam2MouseVec.y;
		}
	}
	
	public float panelToCam(float val){
		camToPanel(0, 0, _panelToCamsizeA);
		camToPanel(0, val, _panelToCamsizeB);
		return _panelToCamsizeA.dst(_panelToCamsizeB);
	}

}
