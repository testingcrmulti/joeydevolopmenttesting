package com.joey.aitesting.game;

import com.badlogic.gdx.Gdx;
import com.joey.aitesting.game.cellSpace.QuadTree;
import com.joey.aitesting.game.entities.BaseGameEntity;
import com.joey.aitesting.game.entities.Obstacle;
import com.joey.aitesting.game.entities.Vehicle;
import com.joey.aitesting.game.entities.Wall2D;
import com.joey.aitesting.game.graphics.GameWorldViewer;
import com.joey.aitesting.game.shapes.Rectangle2D;
import com.joey.aitesting.game.shapes.Vector2D;

import java.awt.geom.Point2D;
import java.util.ArrayList;

public class GameWorld {
	public static final int MAX_CELL_COUNT = 2;
	ArrayList<Vehicle> vehicles;
	ArrayList<Obstacle> obstacles;
	ArrayList<Wall2D> walls;
	public QuadTree<Vehicle> quadTree;
	public Rectangle2D worldBounds;
	boolean paused = false;

	public GameWorld(Rectangle2D worldBounds) {
		vehicles = new ArrayList<Vehicle>();
		obstacles = new ArrayList<Obstacle>();
		walls = new ArrayList<Wall2D>();
		quadTree = new QuadTree<Vehicle>(worldBounds, MAX_CELL_COUNT);
		setWorldSize(worldBounds);
	}

	public void update(float updateTime) {
		if (paused) {
			return;
		}
		for (int i = 0; i < vehicles.size(); i++) {	
			Vehicle v = vehicles.get(i);
			v.update(updateTime);
		}
		updateQuadTree();
	}

	public void updateQuadTree() {
		quadTree.rebuild();
	}

	public void setWorldSize(Rectangle2D worldBounds) {

		this.worldBounds = worldBounds;
		worldBounds.ensureOrder();
		quadTree.setWorldSize(worldBounds, MAX_CELL_COUNT);
	}

	public ArrayList<Vehicle> getVehicles() {
		// TODO Auto-generated method stub
		return vehicles;
	}
	public ArrayList<Obstacle> getObstacles() {
		// TODO Auto-generated method stub
		return obstacles;
	}
	public ArrayList<Wall2D> getWalls() {
		// TODO Auto-generated method stub
		return walls;
	}

	public void addVehicle(Vehicle v) {
		vehicles.add(v);
		quadTree.addEntity(v);
	}

	public void removeVehicle(Vehicle v) {
		vehicles.remove(v);
		quadTree.removeEntity(v);
	}

	public void addObstacles(int count, float radius) {
		synchronized (obstacles) {
			for (int i = 0; i < count; i++) {
				Obstacle ob = new Obstacle(worldBounds.getRandomPos(), radius);
				obstacles.add(ob);
			}
		}
	}
	// Add a new viechle in random position
	public void addVehicles(int count, float maxVel, float maxForce) {
		synchronized (quadTree) {
			for (int i = 0; i < count; i++) {

				Vehicle entity = new Vehicle(this);
				entity.pos.setLocation(worldBounds.getRandomPos());
				entity.maxSpeed = maxVel;
				entity.maxForce = maxForce;
				entity.mass = 1;
				entity.scale = new Vector2D(1, 1);

				
				
				entity.steering.useWallAvoidance = true;
				entity.steering.wallAvoidanceWeight = 10;
				entity.steering.feelerCount = 3;
				entity.steering.feelerFOV = (float) Math.PI;
				entity.steering.feelerLength = 150;
				
				
				entity.maxForce = 2000;
				entity.maxSpeed = 300;
				entity.vel.setLocation(
						(float) (entity.maxSpeed * (1 - 2 * Math.random())),
						(float) (entity.maxSpeed * (1 - 2 * Math.random())));
				
//				if(vehicles.size() < 1){
//					entity.steering.drawBehaviour = true;
//				}
				if (vehicles.size() > 0) {
					Vehicle v = vehicles.get(0);
					if (v != entity) {
						entity.steering.evadeVehicle= v;
						entity.steering.evadePanicDistance = 500;
						entity.steering.useEvade = true;
						entity.steering.useEvadePanic = true;
						entity.steering.evadeWeight = 5;
						
						

						entity.steering.seperationWeight = 2f;
						entity.steering.alignmentWeight = 5f;
						entity.steering.cohesionWeight= 1f;

						entity.steering.useSeperation = true;
						entity.steering.useAlignment = true;
						entity.steering.useCohesion = true;
						entity.steering.neighborRadius = 60;
						
						entity.steering.drawBehaviour = false;
					}
				} else {
					entity.steering.useWander = true;
					entity.steering.wanderDistance = 100;
					entity.steering.wanderRadius = 60;
					entity.steering.wanderJitter = 30;
					entity.steering.drawBehaviour = true;	
				}

				addVehicle(entity);
			}

		}
	}

	// Add remove random set of viechle in random position
	public void removeVehicles(int count) {
		synchronized (quadTree) {
			for (int i = 0; i < count; i++) {
				if (vehicles.size() > 1) {
					removeVehicle(vehicles
							.get(1 + (int) ((vehicles.size() - 1) * Math
									.random())));
				}
			}

		}
	}

	public void addWall(Wall2D w) {
		synchronized (walls) {
			walls.add(w);
		}
	}

}
