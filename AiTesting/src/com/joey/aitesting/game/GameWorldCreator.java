package com.joey.aitesting.game;

import java.util.ArrayList;

import com.joey.aitesting.game.entities.Obstacle;
import com.joey.aitesting.game.entities.Vehicle;
import com.joey.aitesting.game.entities.Wall2D;
import com.joey.aitesting.game.shapes.Rectangle2D;
import com.joey.aitesting.game.shapes.Vector2D;

public class GameWorldCreator {
	
	public static void CreateGameWorld(GameWorld world) {
		CreateWallAvoidanceWorld(world);
	}

	private static void CreateEntity(GameWorld world, float maxVel,
			float maxForce) {
		CreateWallAvoidanceEntity(world, maxVel, maxForce);
	}

	public static void CreatePathFollowWorld(GameWorld world) {
		
	}

	public static void CreateWallAvoidanceWorld(GameWorld world) {
		//Add outer Boundary
		Wall2D.addRectangle(world, world.worldBounds, 30, true);
		
		//Add random walls and ensure they dont overlap
		ArrayList<Rectangle2D> hold = new ArrayList<Rectangle2D>();
		while (hold.size() < 10) {
			Vector2D p = world.worldBounds.getRandomPos();
			float size = 250;
			Rectangle2D r = new Rectangle2D(p.x, p.y, p.x + size, p.y + size);

			if (world.worldBounds.contains(r)) {
				boolean ok = true;
				for (Rectangle2D r2 : hold) {
					if (r2.intersects(r)) {
						ok = false;
					}
				}
				if (ok) {
					hold.add(r);
				}
			}

		}

		for (Rectangle2D r : hold) {
			Wall2D.addRectangle(world, r, 0, false);
		}
	}

	public static void addObstacles(GameWorld world, int count, float radius) {
		synchronized (world.obstacles) {
			for (int i = 0; i < count; i++) {
				Obstacle ob = new Obstacle(world.worldBounds.getRandomPos(),
						radius);
				world.obstacles.add(ob);
			}
		}
	}

	// Add a new viechle in random position
	public static void addEntity(GameWorld world, int count, float maxVel,float maxForce) {
		synchronized (world.quadTree) {
			for (int i = 0; i < count; i++) {
				CreateEntity(world, maxVel, maxForce);
			}

		}
	}

	public static void CreateWallAvoidanceEntity(GameWorld world, float maxVel,
			float maxForce) {
		Vehicle entity = new Vehicle(world);
		entity.pos.setLocation(world.worldBounds.getRandomPos());
		entity.maxSpeed = maxVel;
		entity.maxForce = maxForce;
		entity.vel.setLocation(
				(float) (entity.maxSpeed * (1 - 2 * Math.random())),
				(float) (entity.maxSpeed * (1 - 2 * Math.random())));
		entity.mass = 1;
		entity.scale = new Vector2D(1, 1);

		entity.steering.useWallAvoidance = true;
		entity.steering.wallAvoidanceWeight = 10;
		entity.steering.feelerCount = 3;
		entity.steering.feelerFOV = (float) Math.PI;
		entity.steering.feelerLength = 150;

		if (world.vehicles.size() > 0) {
			Vehicle v = world.vehicles.get(0);
			if (v != entity) {
				entity.steering.evadeVehicle = v;
				entity.steering.evadePanicDistance = 500;
				entity.steering.useEvade = true;
				entity.steering.useEvadePanic = true;
				entity.steering.evadeWeight = 5;

				entity.steering.seperationWeight = 2f;
				entity.steering.alignmentWeight = 5f;
				entity.steering.cohesionWeight = 1f;

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

	}

	// Add remove random set of viechle in random position
	public static void removeVehicles(GameWorld world, int count) {
		synchronized (world.quadTree) {
			for (int i = 0; i < count; i++) {
				if (world.vehicles.size() > 1) {
					world.removeVehicle(world.vehicles
							.get(1 + (int) ((world.vehicles.size() - 1) * Math
									.random())));
				}
			}

		}
	}
}