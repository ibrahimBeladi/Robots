# search.py
# ---------
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
from platform import node




"""
In search.py, you will implement generic search algorithms which are called by
Pacman agents (in searchAgents.py).
"""

import util

class SearchProblem:
    """
    This class outlines the structure of a search problem, but doesn't implement
    any of the methods (in object-oriented terminology: an abstract class).

    You do not need to change anything in this class, ever.
    """

    def getStartState(self):
        """
        Returns the start state for the search problem.
        """
        util.raiseNotDefined()

    def isGoalState(self, state):
        """
          state: Search state

        Returns True if and only if the state is a valid goal state.
        """
        util.raiseNotDefined()

    def getSuccessors(self, state):
        """
          state: Search state

        For a given state, this should return a list of triples, (successor,
        action, stepCost), where 'successor' is a successor to the current
        state, 'action' is the action required to get there, and 'stepCost' is
        the incremental cost of expanding to that successor.
        """
        util.raiseNotDefined()

    def getCostOfActions(self, actions):
        """
         actions: A list of actions to take

        This method returns the total cost of a particular sequence of actions.
        The sequence must be composed of legal moves.
        """
        util.raiseNotDefined()


def tinyMazeSearch(problem):
    """
    Returns a sequence of moves that solves tinyMaze.  For any other maze, the
    sequence of moves will be incorrect, so only use this for tinyMaze.
    """
    from game import Directions
    s = Directions.SOUTH
    w = Directions.WEST
    return  [s, s, w, s, w, w, s, w]

def depthFirstSearch(problem):
    """
    Search the deepest nodes in the search tree first.

    Your search algorithm needs to return a list of actions that reaches the
    goal. Make sure to implement a graph search algorithm.

    To get started, you might want to try some of these simple commands to
    understand the search problem that is being passed in:

    print "Start:", problem.getStartState()
    print "Is the start a goal?", problem.isGoalState(problem.getStartState())
    print "Start's successors:", problem.getSuccessors(problem.getStartState())
    """
    "*** YOUR CODE HERE ***"

    fringe = util.Stack()
    exploredList = []
    # a set of already expanded nodes
    exploredSet = set(exploredList)
    startState = problem.getStartState()

    ''' note:
        nodes are implemented in this game as (x,y) coordinate, however the successor function returns
        next states in the form of
        [(x,y),the required move to come to this state eg: 'south', no. of steps), (next successor), (next successor) etc]
        as a list, so when I push a node to the fringe I push the node and it ancestors (the entire plan so far
        as explained by the instructor) however if I want to call problem.getSuccessor(node) or
        problem.isGoalState(node) I have to pass only the (x,y) of last node in that plan
        and that is achieved by node[-1][0], where node[-1] will give me the last node in the plan
        in the form [(x,y),'south', 1] and node[-1][0] will give me (x,y)
         '''

    ''' I implemented getPlan(node) method so it takes the plan list from the fringe if the last node
        that plan is a goal node, and it returns the list of action required to reach that goal
        from the starting state, eg [south,west,west,east]  '''
    if problem.isGoalState(startState):return None
    fringe.push([(startState,None,None)])

    while not fringe.isEmpty():
        node = fringe.pop()
        if node[-1][0] not in exploredSet:
            exploredSet.add(node[-1][0])
            if problem.isGoalState(node[-1][0]):return getPlan(node)
            for i,j,k in problem.getSuccessors(node[-1][0]):
                    fringe.push(node+[(i,j,k)])
        elif node[-1][0] in exploredSet: continue



    util.raiseNotDefined()

def getPlan(node):
    sol = []
    for i,j,k in node:
        if not j == None:
            sol += [j]
    return sol

def breadthFirstSearch(problem):
    """Search the shallowest nodes in the search tree first."""
    "*** YOUR CODE HERE ***"
    fringe = util.Queue()
    exploredList = []
    # a set of already expanded nodes
    exploredSet = set(exploredList)
    startState = problem.getStartState()

    if problem.isGoalState(startState):return None
    fringe.push([(startState,None,None)])

    while not fringe.isEmpty():
        node = fringe.pop()
        if node[-1][0] not in exploredSet:
            exploredSet.add(node[-1][0])
            if problem.isGoalState(node[-1][0]):return getPlan(node)
            for i,j,k in problem.getSuccessors(node[-1][0]):
                    fringe.push(node+[(i,j,k)])
        elif node[-1][0] in exploredSet: continue
    util.raiseNotDefined()

def uniformCostSearch(problem):
    """Search the node of least total cost first."""
        # state = position

    initState = problem.getStartState()
    frontier = util.PriorityQueue()
    frontier.push((initState, []), 0)
    exploredList = []
    exploredSet = set(exploredList)



    while not frontier.isEmpty():
        node = frontier.pop()

        if problem.isGoalState(node[0]):
            return node[1] # path returned

        exploredSet.add(node[0]) # postion explored

    # getSuccessors returns a tuple of [(x,y), direction, # of steps]

        for position, direction, steps in problem.getSuccessors(node[0]):
            if not position in exploredSet:
                child = node[1] + [direction] # new path
                cost = problem.getCostOfActions(child) # g(n)
                frontier.push((position, child), cost)
    return []

def nullHeuristic(state, problem=None):
    """
    A heuristic function estimates the cost from the current state to the nearest
    goal in the provided SearchProblem.  This heuristic is trivial.
    """

    return 0

def aStarSearch(problem, heuristic=nullHeuristic):
    """Search the node that has the lowest combined cost and heuristic first."""

    # state = position

    initState = problem.getStartState()
    frontier = util.PriorityQueue()
    frontier.push((initState, []), 0)
    exploredList = []
    exploredSet = set(exploredList)



    while not frontier.isEmpty():
        node = frontier.pop()

        if problem.isGoalState(node[0]):
            return node[1] # path returned

        exploredSet.add(node[0]) # position explored

    # getSuccessors returns a tuple of [(x,y), direction, # of steps]

        for position, direction, steps in problem.getSuccessors(node[0]):
            if not position in exploredSet:
                child = node[1] + [direction] # new path
                cost = problem.getCostOfActions(child) + heuristic(position, problem) # g(n) + h(n)
                frontier.push((position, child), cost)
    return []

# Abbreviations
bfs = breadthFirstSearch
dfs = depthFirstSearch
astar = aStarSearch
ucs = uniformCostSearch
