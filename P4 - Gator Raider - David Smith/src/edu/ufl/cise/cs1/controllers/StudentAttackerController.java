package edu.ufl.cise.cs1.controllers;

import game.controllers.AttackerController;
import game.models.Defender;
import game.models.Game;
import game.models.Node;
import game.system._Maze;

import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.List;

public final class StudentAttackerController implements AttackerController {

	public void init(Game game) {
	}

	public void shutdown(Game game) {
	}

	public int update(Game game, long timeDue) {

		//makes a list of defender nodes
		List<Node> defenderLocations = new ArrayList<Node>();
		for (int i = 0; i < game.getDefenders().size(); i++) {
			defenderLocations.add(game.getDefenders().get(i).getLocation());
		} // getting data

		List<Integer> possibleDirs = game.getAttacker().getPossibleDirs(true);

		if(possibleDirs.size() == 1){
			return possibleDirs.get(0);
		}

			//variable inits
			Node attackerPosition = game.getAttacker().getLocation();
			Node closestDNOde = game.getAttacker().getTargetNode(defenderLocations, true);
			Node closestPP = game.getAttacker().getTargetNode(game.getCurMaze().getPowerPillNodes(), true);
			int ddistance = attackerPosition.getPathDistance(closestDNOde);
			int ppdistance = attackerPosition.getPathDistance(closestPP);
			Node inFront = attackerPosition.getNeighbor(game.getAttacker().getDirection());
			int attackerDirection = game.getAttacker().getDirection();
			boolean beingChased = false;

			//determines if defenders are vulnerable
			boolean dIsVul = false;
			List <Defender> vulnerableDefenders = new ArrayList <Defender>();
			for(int i = 0; i < game.getDefenders().size(); i++){
				if(game.getDefenders().get(i).isVulnerable()) {
					dIsVul = true;
					vulnerableDefenders.add(game.getDefenders().get(i));
				}
			}

			//finds closest defender
			Defender closestDefender = game.getDefender(0);
			for(int i = 0; i < game.getDefenders().size(); i++){
				if(game.getDefenders().get(i).getLocation() == closestDNOde)
					closestDefender = game.getDefenders().get(i);
			}
			int dDirection = closestDefender.getDirection();
			//
			//end of initial getting and setting data

		//go to nearest power pill if being chased
		if(!closestDefender.isVulnerable() && ddistance < 20 && ddistance > -1 && attackerDirection == dDirection){
			if(game.getAttacker().getNextDir(closestPP, true) != closestDefender.getReverse())
				return game.getAttacker().getNextDir(closestPP, true);
		}

			//run away from immediate threats - needs fix so doesn't get caught in circles
			if(!closestDefender.isVulnerable() && ddistance < 10 && ddistance > -1){
				return game.getAttacker().getNextDir(closestDNOde, false);
			}

			// if defense is vulnerable, indexes vulnerable defenders and attacks nearest
			if(dIsVul) {

				Node closestVulDNode = closestDNOde;
				List<Node> vulnerableDefenderLocations = new ArrayList<Node>();
				for (int i = 0; i < vulnerableDefenders.size(); i++) {
					vulnerableDefenderLocations.add(vulnerableDefenders.get(i).getLocation());
				} // getting data

				closestVulDNode = game.getAttacker().getTargetNode(vulnerableDefenderLocations, true);

				// goes after nearest defender
				return game.getAttacker().getNextDir(closestVulDNode, true);

			}

			//waits for defender to get close before eating power pill
			if(!closestDefender.isVulnerable() && ddistance > 10 && ppdistance > -1 && ppdistance < 5){
				return game.getAttacker().getReverse();
			}

			return game.getAttacker().getNextDir(game.getAttacker().getTargetNode(game.getPillList(), true), true);

	}

}
