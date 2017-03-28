# multiAgents.py
# --------------
# Licensing Information:  You are free to use or extend these projects for
# educational purposes provided that (1) you do not distribute or publish
# solutions, (2) you retain this notice, and (3) you provide clear
# attribution to UC Berkeley, including a link to http://ai.berkeley.edu.
# 
# Attribution Information: The Pacman AI projects were developed at UC Berkeley.
# The core projects and autograders were primarily created by John DeNero
# (denero@cs.berkeley.edu) and Dan Klein (klein@cs.berkeley.edu).
# Student side autograding was added by Brad Miller, Nick Hay, and
# Pieter Abbeel (pabbeel@cs.berkeley.edu).


from util import manhattanDistance
from game import Directions
import random, util

from game import Agent

class ReflexAgent(Agent):
    """
      A reflex agent chooses an action at each choice point by examining
      its alternatives via a state evaluation function.

      The code below is provided as a guide.  You are welcome to change
      it in any way you see fit, so long as you don't touch our method
      headers.
    """


    def getAction(self, gameState):
        """
        You do not need to change this method, but you're welcome to.

        getAction chooses among the best options according to the evaluation function.

        Just like in the previous project, getAction takes a GameState and returns
        some Directions.X for some X in the set {North, South, West, East, Stop}
        """
        # Collect legal moves and successor states
        legalMoves = gameState.getLegalActions()

        # Choose one of the best actions
        scores = [self.evaluationFunction(gameState, action) for action in legalMoves]
        bestScore = max(scores)
        bestIndices = [index for index in range(len(scores)) if scores[index] == bestScore]
        chosenIndex = random.choice(bestIndices) # Pick randomly among the best

        "Add more of your code here if you want to"

        return legalMoves[chosenIndex]

    def evaluationFunction(self, currentGameState, action):
        """
        Design a better evaluation function here.

        The evaluation function takes in the current and proposed successor
        GameStates (pacman.py) and returns a number, where higher numbers are better.

        The code below extracts some useful information from the state, like the
        remaining food (newFood) and Pacman position after moving (newPos).
        newScaredTimes holds the number of moves that each ghost will remain
        scared because of Pacman having eaten a power pellet.

        Print out these variables to see what you're getting, then combine them
        to create a masterful evaluation function.
        """
        # Useful information you can extract from a GameState (pacman.py)
        successorGameState = currentGameState.generatePacmanSuccessor(action)
        newPos = successorGameState.getPacmanPosition()
        newFood = successorGameState.getFood()
        newGhostStates = successorGameState.getGhostStates()
        newScaredTimes = [ghostState.scaredTimer for ghostState in newGhostStates]

        "*** YOUR CODE HERE ***"
        """
        The startegy to find the evaluation function is as follows:
        - find the distance to all food points in the grid.
        - take the minimum and use as an evaluation point. "the minimum distace to a food point is desired"
        - do the same thing with the ghosts positions. but TAKE THE MAXIMUM because pacman need to GET AWAY from ghosts
        - take the length of the food list as another indicator. "as long as the list is minimal, we are close to our goal"
        after that, give these functions some weights. "I played with the wights until I got useful ones"
        """
        # get food points as list
        foodList = newFood.asList()

        # calculate distances to the successor's position
        foodListD = []
        for food in foodList:
            foodListD.append(int(util.manhattanDistance(food, newPos)))

        # get the minimum to the successor position
        minDisToFood = 0  # INITIALIZATION
        if len(foodListD) is not 0:
            minDisToFood = min(foodListD)

        # get ghost positions as list ' WE HAVE ONLY ONE GHOST IN THE LAYOUT "OPEN CLASSIC"
        ghostsPositions = []
        for ghost in newGhostStates:
            ghostsPositions.append(ghost.getPosition())

        # calculate the distance
        ghostsPositionsD = []
        for ghost in ghostsPositions:
            ghostsPositionsD.append(manhattanDistance(ghost, newPos))

        # get the minimum to successor position
        distToGhost = min(ghostsPositionsD)

        return -2 * minDisToFood - 37 * len(foodList) + 2 * distToGhost  # these numbers are controllers, you can play with them and see their effect on pacman behaviour.

def scoreEvaluationFunction(currentGameState):
    """
      This default evaluation function just returns the score of the state.
      The score is the same one displayed in the Pacman GUI.

      This evaluation function is meant for use with adversarial search agents
      (not reflex agents).
    """
    return currentGameState.getScore()

class MultiAgentSearchAgent(Agent):
    """
      This class provides some common elements to all of your
      multi-agent searchers.  Any methods defined here will be available
      to the MinimaxPacmanAgent, AlphaBetaPacmanAgent & ExpectimaxPacmanAgent.

      You *do not* need to make any changes here, but you can if you want to
      add functionality to all your adversarial search agents.  Please do not
      remove anything, however.

      Note: this is an abstract class: one that should not be instantiated.  It's
      only partially specified, and designed to be extended.  Agent (game.py)
      is another abstract class.
    """

    def __init__(self, evalFn = 'scoreEvaluationFunction', depth = '2'):
        self.index = 0 # Pacman is always agent index 0
        self.evaluationFunction = util.lookup(evalFn, globals())
        self.depth = int(depth)

class MinimaxAgent(MultiAgentSearchAgent):
    """
      Your minimax agent (question 2)
    """
    def recTraversal (self,currentPlayer, depth, state):
        if currentPlayer == state.getNumAgents() and depth == self.depth: 
            return self.evaluationFunction(state)
        elif currentPlayer == state.getNumAgents() and depth != self.depth:
            return self.recTraversal (0,depth + 1, state)
        elif currentPlayer != state.getNumAgents():
            if len(state.getLegalActions(currentPlayer)) == 0:
                return self.evaluationFunction(state)
            
        
            successor = []
            legalActions = state.getLegalActions(currentPlayer)
            for action in state.getLegalActions(currentPlayer):
                successor.append(self.recTraversal(currentPlayer + 1, depth, state.generateSuccessor(currentPlayer, action)))
            #maximum = max (recTraversal(currentPlayer + 1, depth, state.generateSuccessor(currentPlayer, action)))
            #minimum = min (recTraversal(currentPlayer + 1, depth, state.generateSuccessor(currentPlayer, action)))
        
            if currentPlayer == 0: return max(successor)
            else: return min(successor)
    def getAction(self, gameState):
        """
          Returns the minimax action from the current gameState using self.depth
          and self.evaluationFunction.

          Here are some method calls that might be useful when implementing minimax.

          gameState.getLegalActions(agentIndex):
            Returns a list of legal actions for an agent
            agentIndex=0 means Pacman, ghosts are >= 1

          gameState.generateSuccessor(agentIndex, action):
            Returns the successor game state after an agent takes an action

          gameState.getNumAgents():
            Returns the total number of agents in the game
        """
        "*** YOUR CODE HERE ***"
        
        return max(
            gameState.getLegalActions(0),
            key = lambda x: self.recTraversal(1, 1, gameState.generateSuccessor(0, x))
            )
        util.raiseNotDefined()
    
        

class AlphaBetaAgent(MultiAgentSearchAgent):
    """
      Your minimax agent with alpha-beta pruning (question 3)
    """
    def min_value(self, state, alpha, beta, depth, currentPlayer):
        if currentPlayer != state.getNumAgents():
            v = None
            for action in state.getLegalActions(currentPlayer):
                successor = self.min_value(state.generateSuccessor(currentPlayer, action), alpha, beta, depth, currentPlayer + 1)
                if v is None:
                    v = successor
                else:
                    v =  min(v, successor) 
    
                if alpha is not None and v < alpha:
                    return v
                
                if beta is None:beta = v 
                else: beta =  min(beta, v)
    
            return self.evaluationFunction(state) if v is None else v 
        else:
            return self.max_value(state, alpha, beta, depth + 1, 0)

    def max_value(self, state, alpha, beta, depth, currentPlayer):
        if depth <= self.depth:
            v = None
            for action in state.getLegalActions(currentPlayer):
                successor = self.min_value(state.generateSuccessor(currentPlayer, action), alpha, beta, depth, currentPlayer + 1)
                v = max(v, successor)
    
                if beta is not None and v > beta:
                    return v
                
                if alpha is None:alpha = v 
                else: alpha = max(alpha, v)
    
            return self.evaluationFunction(state) if v is None else v 
        else:return self.evaluationFunction(state)
    
    def getAction(self, gameState):
        """
          Returns the minimax action using self.depth and self.evaluationFunction
        """
        "*** YOUR CODE HERE ***"
        alpha, beta = None, None
        v = None
       
        for action in gameState.getLegalActions(0):
            v = max(v, self.min_value(gameState.generateSuccessor(0, action), alpha, beta, 1, 1))
            
            if alpha is None:
               bestAction = action
               alpha = v  
            else:
                if v > alpha:
                    bestAction = action  
                alpha = max(v, alpha)

        return bestAction
        util.raiseNotDefined()

class ExpectimaxAgent(MultiAgentSearchAgent):
    """
      Your expectimax agent (question 4)
    """

    def getAction(self, gameState):
        """
          Returns the expectimax action using self.depth and self.evaluationFunction

          All ghosts should be modeled as choosing uniformly at random from their
          legal moves.
        """
        "*** YOUR CODE HERE ***"
                #util.raiseNotDefined()
        return self.ExpectiMax(gameState, 1, 0)

    def ExpectiMax(self, gameState, depth, agentIndex):
        #10000 trials
        #Average Score: 12.2082
        #Win Rate:      4973/10000 (0.50)
        if depth > self.depth or gameState.isWin() or gameState.isLose():
            return self.evaluationFunction(gameState)
			
        actions = gameState.getLegalActions(agentIndex)
		
        if agentIndex + 1 >= gameState.getNumAgents():
            results = [self.ExpectiMax(gameState.generateSuccessor(agentIndex, action) , depth + 1, 0) for action in actions]
        else:
            results = [self.ExpectiMax(gameState.generateSuccessor(agentIndex, action) , depth, agentIndex + 1) for action in actions]
			
        if agentIndex == 0:
            if depth == 1:
                top = [i for i in range(len(results)) if results[i] == max(results)]
                return actions[random.choice(top)]
            return max(results)
        return sum(results)/len(results)
        util.raiseNotDefined()

def betterEvaluationFunction(currentGameState):
    """
      Your extreme ghost-hunting, pellet-nabbing, food-gobbling, unstoppable
      evaluation function (question 5).

      DESCRIPTION

      used as parameters:
        1. # of food pellets left
        2. distance to the scared ghosts (manhattan)
        3. distance to the closest food pellet (manhattan)
        4. # of power food pellets left
	
	The highest weight is given to (4), since eating the power food pellet and eating the ghost will improve the score

    """

    pos = currentGameState.getPacmanPosition()
    food = currentGameState.getFood()
    ghostStates = currentGameState.getGhostStates()
    currentScore = scoreEvaluationFunction(currentGameState)

    if currentGameState.isLose():
        return -float("inf")
    elif currentGameState.isWin():
        return float("inf")

    food = food.asList()

    foodPelletDistances = []
    for i in food:
        foodPelletDistances.append(util.manhattanDistance(pos, i))

    closestFoodPellet = min(foodPelletDistances)

    # number of power food pellets
    powerFoodPellets = len(currentGameState.getCapsules())

    # number of food pellets
    foodPellets = len(food)

    # ghost distance
    scaredGhosts = []
    for g in ghostStates:
        if g.scaredTimer:
            scaredGhosts.append(g)

    scaredManhattanDistances = []
    closestGhostDistanceScared = 0
    if scaredGhosts:
        for i in ghostStates:
            scaredManhattanDistances.append(util.manhattanDistance(pos, i.getPosition()))
        closestGhostDistanceScared = min(scaredManhattanDistances)
    else:
        closestGhostDistanceScared = 0

    score = currentScore + (-1 * closestFoodPellet) + (-3 * closestGhostDistanceScared) \
            + (-80 * powerFoodPellets) + (-4 * foodPellets)

    return score

# Abbreviation
better = betterEvaluationFunction

