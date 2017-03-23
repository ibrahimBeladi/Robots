class Queen:
    
    def __init__ (self, x, y,conflicts):
        self.x = x;
        self.y = y;
        self.conflicts = conflicts;
    

   
    def toString(self):
        return "Queen [x=" + self.x + ", y=" + self.y + ", conflicts=" + self.conflicts + "]"
  