import sys

class GameOfLife:

    def __init__(self, io):
        self.field = self._init_field_from_stream(io)
        self.numrows = len(self.field)
        if self.numrows < 1:
            raise Error("no rows found!")
        self.numcols = len(self.field[0])


    def set_pattern(self, pattern):
        for coords in pattern:
            x,y = coords
            self.field[x][y] = self.sign_element
        
        
    def __str__(self):
        repr = []
        for line in self.field:
            repr.append("\n");
            for column in line:
                repr.append(column)
        return " ".join(repr)


    def _init_blank_field(self, rows,columns,sign_unset):
        field = []  
        for i in range(rows):
            field.append([])
            for j in range(columns):
                field[i].append(sign_unset)
        return field

    def _init_field_from_stream(self, io, sep=" "):
        field = []
        lines = io.readlines()
        for line in lines:
            line = line.strip()
            if line.startswith("#"):
                continue

            element_keyword = "ELEMENT"
            if line.startswith(element_keyword):
                self.sign_element =  line.split("=")[1].strip()
                continue
            
            row = []
            field.append(row)
            for char in line[:-2].split(sep):
                if (not hasattr(self, "sign_background")) and char != self.sign_element:
                    self.sign_background = char
                row.append(char)
        return field       

    def move(self):
        newfield = self._init_blank_field(self.numrows, self.numcols, self.sign_background)
        for i in range(len(self.field)):
            for j in range(len(self.field[i])):
                
                state = self.sign_background
                num = self.numNeighbours(self.field, i,j)
                if (num in [2,3] and self.field[i][j] == self.sign_element) or num == 3:
                    state = self.sign_element

                newfield[i][j] = state
        self.field = newfield

    def numNeighbours(self, field, i, j):
        coordpairs = [(i-1, j), (i+1, j), (i, j-1), (i, j+1), 
                      (i+1, j+1), (i-1, j-1), (i-1,j+1),(i+1,j-1)]
        neighbours = 0
        for coords in coordpairs:
            x,y = coords
            if x < 0 or x >= len(field) or y < 0 or y >= len(field[j]):
                continue
            if field[x][y] == self.sign_element:
                neighbours += 1
        return neighbours


if len(sys.argv) > 1:
    cycles = int(sys.argv[1])
else:
    cycles = 5

gof = GameOfLife(sys.stdin)

for i in range(cycles):
    print(gof)
    gof.move()
