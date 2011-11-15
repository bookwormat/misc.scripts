import sys
from math import *

PRECISION = 4

def fixpoint(guess,expr,precision=PRECISION):
    improved_guess = round(eval(expr),precision)
    if guess == improved_guess: return guess
    else: return fixpoint(improved_guess,expr)

args = sys.argv
print("%s"%fixpoint(float(args[1]),args[2]))
