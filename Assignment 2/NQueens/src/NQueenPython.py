from QueenPython import Queen

TRIALS = 10
N = 1500
moves = 0 #this value should be tuned manually (time to re-initiate), should increase as N increases
QueenList = []
Q = set(QueenList)
i,j=0,0
list = [] 
attacks = []
board = dict()
equalMin = []
for i in range(0,N):
    for j in range(0,N):
        board[(i,j)] = False
#printBoard()
    
def main():
 
    print"The board is {} * {}".format(N, N)
    tries = 0
    sumT = 0.0
    
    for tries in range(0,TRIALS):
        
        from datetime import datetime
        from datetime import timedelta

        y = datetime.now()
 
        #initiate the game
        initiate()
        moves = 0
        x = datetime.now()
        
        while thereAreConflicts():
 
        #flush after a certain amount of time
            if  moves > 1000:
                print "shaked"
                initiate()
                x = datetime.now()
                moves = 0
                continue
                 
 
            #pick any conflicting queen
            list = []#contains list of conflicting queens
            for q in Q:
                if (q.conflicts != 0):
                    list+=[q]
            from random import randint
            
            randomNum = randint(0,len(list)-1) #pick a random conflicting queen
            q = list[randomNum]
    

            #move it
            board[(q.x,q.y)] = False #free current position
            attacks = []#array of attacks in each cell in the queen's row
            for k in range(0,N):
                attacks+=[getAttacks(q.x, k)]
   
            #pick one of the least conflicting cells
            minValue = min(attacks)
            minIndex = attacks.index(minValue)#findMinIdx(attacks)
            equalMin = []
            for k in range(0,len(attacks)):
                if attacks[k] == minValue :
                    equalMin+=[k]
            
            #from random import randint
            randomIdx = randint(0,len(equalMin)-1) #pick a random conflicting queen
            finalIdx = equalMin[randomIdx]

            #update the board
            board[(q.x,finalIdx)] = True;
            moves +=1
            q.y = minIndex

            #update others, the location may attack others
            for queen in Q:
                queen.conflicts = getAttacks(queen.x, queen.y)

        time = millis(y) / 1000.0
        print time
        print " secs!\n",
        sumT = sumT + time
        tries+= 1
        #printBoard();

    print"\nAverage of {} runs:{} ".format(TRIALS,sumT / tries) 
    #printBoard();



    



# returns the elapsed milliseconds since the start of the program
def millis(start_time):
    from datetime import datetime
    #from datetime import timedelta
    dt = datetime.now() - start_time
    ms = (dt.days * 24 * 60 * 60 + dt.seconds) * 1000 + dt.microseconds / 1000.0
    return ms

    
def thereAreConflicts():
    #return false if there are no conflicts
    for q in Q:
        if q.conflicts != 0:
            return True
    return False

def initiate():
    #board = new boolean[N][N]
    #clear the list, if it is not cleared (used in flush)
    Q.clear()
    board.clear()
    attacks = []
    equalMin = []
    list = []
    for i in range(0,N):
        for j in range(0,N):
            board[(i,j)] = False

    #A queen is put on a random row
    k = N - 1
    while k >= 0 :
        putQueen(k)
        k -= 1


def getAttacks(i, j):
    #returns how many attacks are on a given cell (i, j)

    attacks = 0

    #attacks within row
    for k in range(0,N):
        if board[(i,k)] and k != j:
            attacks+=1

    #attacks within column
    for k in range(0,N):
        if board[(k,j)] and k != i:
            attacks+=1

    attacks = attacks + getAttacksDia(i, j)

    return attacks

def getAttacksDia(i, j):
    return attacksRightUpperDia(i - 1, j + 1) + attacksLeftUpperDia(i - 1, j - 1)
    + attacksRightLowerDia(i + 1, j + 1) + attacksLeftLowerDia(i + 1, j - 1)



def attacksLeftLowerDia(i, j):
    s = 0
    while j >= 0 and i != N:
        if (board[(i,j)]):
            s+=1
        i+=1 
        j-=1
    return s


def attacksRightLowerDia(i, j):
    s = 0
    while i != N and j != N:
        if (board[(i,j)]):
            s+=1
        i+=1 
        j+=1
    return s


def attacksLeftUpperDia(i, j):
    s = 0
    while i >= 0 and j >= 0:
        if (board[(i,j)]):
            s+=1
        i-=1 
        j-=1
    return s


def attacksRightUpperDia( i,  j):
    s = 0;
    while i >= 0 and j != N:
        if (board[(i,j)]):
            s+=1
        i-=1 
        j+=1
    return s

def printBoard():
    for i in range(0,N):
        for j in range(0,N):
            if (board[(i,j)]):
                print "{}({})".format(board[(i,j)],getAttacks(i, j) ),
            else:
                print "[{}]\t  ".format(getAttacks(i, j)),
        print
    
    #for i in range(0,8): print i

        
    
    print "----------------------"
    


def putQueen(i):
    from random import randint

    #places a queen on the least conflicting column, in the row i
    randomNum = randint(0,N-1) #pick a random number as column index
    attacks = getAttacks(i, randomNum)
    q = Queen(i, randomNum, attacks) 
    #object carries useful info about a queen
    Q.add(q) #put it on a list
    board[(i,randomNum)] = True #update the board
    for queen in Q :
        queen.conflicts = getAttacks(queen.x, queen.y)


if __name__ == '__main__':
       main()