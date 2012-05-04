package com.joey.aitesting.game.steering;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;

import com.joey.aitesting.game.entities.BaseGameEntity;
import com.joey.aitesting.game.entities.Obstacle;
import com.joey.aitesting.game.entities.Vehicle;
import com.joey.aitesting.game.maths.Transformations;
import com.joey.aitesting.game.shapes.Vector2D;
import com.joey.aitesting.game.shapes.Rectangle2D;
import com.joey.aitesting.game.shapes.WorldWrapper;
import com.joey.aitesting.game.steering.behaviors.Alignment;
import com.joey.aitesting.game.steering.behaviors.Arrive;
import com.joey.aitesting.game.steering.behaviors.Cohesion;
import com.joey.aitesting.game.steering.behaviors.Evade;
import com.joey.aitesting.game.steering.behaviors.Flee;
import com.joey.aitesting.game.steering.behaviors.ObstacleAvoidance;
import com.joey.aitesting.game.steering.behaviors.Persuit;
import com.joey.aitesting.game.steering.behaviors.Seek;
import com.joey.aitesting.game.steering.behaviors.Seperation;
import com.joey.aitesting.game.steering.behaviors.Wander;

public class SteeringBehaviors {
	
	Vehicle vehicle;

	public boolean drawBehaviour = false;
	//Fleeing Paramaters
	public float fleeWeight = 1;
	public boolean useFlee = false;
	public boolean useFleePanic = false;
	public float fleePanicDistance;
	public Vector2D fleePos;
	
	//Seek Parameters
	public float seekWeight = 1;
	public boolean useSeek = false;
	public Vector2D seekPos;

	//Arrive Parameters
	public float arriveWeight = 1;
	public boolean useArrive = false;
	public Vector2D arrivePos;
	
	//Persuit Parameters
	public float persuitWeight = 1;
	public boolean usePersuit = false;
	public Vehicle persuitVehicle;
	
	//Evade Parameters
	public float evadeWeight = 1;
	public float evadePanicDistance = 30;
	public boolean useEvade = false;
	public boolean useEvadePanic = false;
	public Vehicle evadeVehicle;

	//Wander Parameters
	public float wanderWeight = 1;
	public boolean useWander = false;
	public float wanderRadius;
	public float wanderDistance;
	public float wanderJitter;
	public Vector2D wanderVector;
	
	//Flocking Parameters	
	public float cohesionWeight = 1f;
	public boolean useCohesion = false;
	
	public float seperationWeight = 1f;
	public boolean useSeperation = false;
	
	public float alignmentWeight = 1f;
	public boolean useAlignment = false;
	
	public float neighborRadius = 100;
	HashSet<Vehicle> neighbors = new HashSet<Vehicle>();
	Rectangle2D regHold[] = null;//For search around edge of world
	
	public float obstacleAvoidanceWeight =1f;
	public boolean useObstacleAvoidance = false;
	public float obstacleSearchBoxDistance = 100;
	
	public SteeringBehaviors(Vehicle vehicle) {
		this.vehicle = vehicle;
	}

	public boolean isSpacePartitioningOn() {
		return true;
	}

	

	public void calculateNeighbobors(Vehicle vehicle, HashSet<Vehicle> neighbors, Rectangle2D reg){
		if(vehicle.world.worldBounds.contains(reg)){
			vehicle.world.quadTree.getPointsInRegion(reg, neighbors);
		}else{
			//If not fully contained in the world search off wrapped world
			if(regHold == null){
				regHold = new Rectangle2D[4];
				regHold[0] = new Rectangle2D(0,0,5,5);
				regHold[1] = new Rectangle2D(0,0,5,5);
				regHold[2] = new Rectangle2D(0,0,5,5);
				regHold[3] = new Rectangle2D(0,0,5,5);
			}
			int count = WorldWrapper.getOverlapRegions(reg, vehicle.world.worldBounds, regHold);
			
			
			for(int i = 0; i < count; i++){
				vehicle.world.quadTree.getPointsInRegion(regHold[i], neighbors);
			}
		}
	}
	
	public Vector2D calculate(float updateTime) {
		Vector2D rst = new Vector2D();
		
		
		Vector2D hold = new Vector2D();
		Vector2D point = new Vector2D();

		if(useCohesion||useAlignment||useSeperation){
			neighbors.clear();
			Rectangle2D reg = new Rectangle2D(
					vehicle.pos.x-neighborRadius, vehicle.pos.y-neighborRadius, 
					vehicle.pos.x+neighborRadius, vehicle.pos.y+neighborRadius);
			reg.ensureOrder();
			calculateNeighbobors(vehicle,neighbors, reg);
			//Remove self
			neighbors.remove(vehicle);
//			//System.out.println("Finding Neighbours: "+neighbors.size());
		}else{
			neighbors.clear();
		}
		
		if (useSeek) {
			hold.setLocation(0,0);
			WorldWrapper.moveToClosest(vehicle.pos, seekPos, point,
					vehicle.world.worldBounds);
			Seek.seek(vehicle, point, hold);
			hold.scale(seekWeight);
			rst.add(hold);
			
			////System.out.println("Seek : "+rst);
		}

		if (useFlee) {
			hold.setLocation(0,0);
			WorldWrapper.moveToClosest(vehicle.pos, fleePos, point,
					vehicle.world.worldBounds);
			if(useFleePanic){
				Flee.flee(vehicle, point, fleePanicDistance, hold);
			}else{
				Flee.flee(vehicle, point, hold);
			}
			hold.scale(fleeWeight);
			rst.add(hold);
			
			////System.out.println("Flee : "+rst);
		}

		if (useArrive) {
			hold.setLocation(0,0);
			WorldWrapper.moveToClosest(vehicle.pos, arrivePos, point,
					vehicle.world.worldBounds);
			Arrive.arrive(point, vehicle, 1, hold);
			hold.scale(arriveWeight);
			rst.add(hold);
			
			////System.out.println("Arrive : "+rst);
		}

		if (usePersuit) {
			hold.setLocation(0,0);
			Persuit.persuit(vehicle, persuitVehicle, hold);
			hold.scale(persuitWeight);
			rst.add(hold);
			
			////System.out.println("Persuit : "+rst);
		}
		
		if (useEvade) {
			hold.setLocation(0,0);
			if(useEvadePanic){
				Evade.evade(vehicle, evadeVehicle,evadePanicDistance, hold);
			}else{
				Evade.evade(vehicle, evadeVehicle, hold);	
			}
			
			hold.scale(evadeWeight);
			rst.add(hold);
			
			////System.out.println("Evade : "+rst);
		}
		
		if(useWander){
			hold.setLocation(0,0);
			if(wanderVector == null){
				wanderVector = new Vector2D(vehicle.vel);
				wanderVector.normalise();
			}
			Wander.wander(vehicle,updateTime, wanderJitter, wanderRadius, wanderDistance, wanderVector, hold);
			
			hold.scale(wanderWeight);
			rst.add(hold);
			
			////System.out.println("Wander : "+rst);
		}

		if(useSeperation){
			hold.setLocation(0,0);
			Seperation.seperation(vehicle, neighbors, hold);
			hold.scale(seperationWeight);
			rst.add(hold);
			
			//System.out.println("Seperation : "+hold);
		}
		if(useAlignment){
			hold.setLocation(0,0);
			Alignment.alignment(vehicle, neighbors, hold);
			hold.scale(alignmentWeight);
			rst.add(hold);
			
			//System.out.println("Alignment : "+hold);
		}
		if(useCohesion){
			hold.setLocation(0,0);
			Cohesion.cohesion(vehicle, neighbors, hold);
			hold.scale(cohesionWeight);
			rst.add(hold);
			
			
			//System.out.println("Cohesion : "+hold);
		}
		
		if(useObstacleAvoidance){
			hold.setLocation(0,0);
			ObstacleAvoidance.obstacleAvoidance(vehicle,vehicle.world.getObstacles(), obstacleSearchBoxDistance,hold);
			hold.scale(obstacleAvoidanceWeight);
			rst.add(hold);		
			//System.out.println("Cohesion : "+hold);
		}
		
		if (rst.lengthSq() > vehicle.maxForce * vehicle.maxForce) {
			rst.normalise();
			rst.scale(vehicle.maxForce);
		}
		
		//System.out.println("End : "+rst);
		return rst;
	}

	
	



	
	
	public static float turnaroundTime(Vehicle pAgent, Vector2D TargetPos, float coefficient) {
		Vector2D toTarget = new Vector2D(TargetPos);
		toTarget.subtract(pAgent.pos);
		float dot = pAgent.velHead.dot(toTarget);
		return (dot - 1.0f) * -coefficient;
	}
	
	
	
	
	public Vehicle getVehicle() {
		return vehicle;
	}

	public void setVehicle(Vehicle vehicle) {
		this.vehicle = vehicle;
	}

	public boolean isUseSeek() {
		return useSeek;
	}

	public void setUseSeek(boolean useSeek) {
		this.useSeek = useSeek;
	}

	public Vector2D getSeekPos() {
		return seekPos;
	}

	public void setSeekPos(Vector2D seekPos) {
		this.seekPos = seekPos;
	}
}
