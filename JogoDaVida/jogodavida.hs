{-# LANGUAGE ParallelListComp #-}
import System.Environment
lenx = 30
leny = 30
-- soma de 2 pontos
sumpoint (x,y) (u,v) = (x+u,y+v)
--mapeia os espacos vizinhos do ponto
nbhood (x,y) = map(sumpoint(x,y)) [(a,b) | a <- [-1..1], b <- [-1..1], a /= 0 || b /= 0]
--retorna as celulas vizinhas do ponto
nboors (x,y) cells = filter (`elem` (nbhood(x,y)) ) cells

-- Entrada: Copia da lista de entrada (para percorrer), lista de entrada
--Saida: Lista dos pontos que devem ser alterados
game :: [(Int,Int)] -> [(Int,Int)] -> [(Int,Int)] -> [(Int,Int)]
game [] _ output = output
game (cell:r) cells output
    |length neib > 3 || length neib < 2 = game r cells (cell:output ++ newCells) -- adiciona as celulas mortas a lista de saida
    |otherwise = game r cells (output ++ newCells) --  sobrevive
    where neib = nboors cell cells
          newCells = [(x,y) | (x,y) <- (nbhood cell), x >= 0 && y >= 0 && (notElem (x,y) neib) && length (nboors (x,y) cells) == 3 && (notElem (x,y) output)] -- celulas geradas por multiplicacao

-- funcao principal
nextRound cells = game cells cells []

cellChar = "o"
voidChar = "-"
matrix :: (Int,Int) -> [(Int,Int)] -> [Char]
matrix (x,y) cells
    | y >= leny = "\n"
    | x >= lenx = "\n" ++ matrix(0,y+1) cells
    | (x,y) `elem` cells = cellChar ++ matrix (x+1,y) cells
    | otherwise = voidChar ++ matrix(x+1,y) cells


imprimir cells = putStr (matrix (0,0) cells)
test cells = nextRound cells
start cells = do
    imprimir cells
    line <- getLine
    let nrCells = nextRound cells in start (  filter (`notElem` nrCells) cells  ++  filter (`notElem` cells) nrCells  )

main = do
    arg1:args <- getArgs
    print (nextRound (read arg1) )