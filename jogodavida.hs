{-# LANGUAGE ParallelListComp #-}
import System.Environment
lenx = 30
leny = 30
-- soma de 2 pontos
sumpoint (x,y) (u,v) = (x+u,y+v)
--mapeia os espacos vizinhos do ponto         retorno de map sao todos os pontos vizinhos de (x,y)          apenas desconsidera pontos inferiores a (0,0)
nbhood (x,y) = [(u,v) | (u,v) <- map(sumpoint(x,y)) ([(a,b) | a <- [-1,0,1], b <- [-1,0,1], a /= 0 || b /= 0]), u >= 0 && v >= 0 ]
--retorna as celulas vizinhas do ponto
nboors (x,y) cells = filter (`elem` cells ) (nbhood (x,y))

-- Entrada: Copia da lista de entrada (para percorrer), lista de entrada
--Saida: Lista dos pontos que devem ser alterados
game :: [(Int,Int)] -> [(Int,Int)] -> [(Int,Int)] -> [(Int,Int)]
game [] _ output = output
game (cell:r) cells output
    |length neib > 3 || length neib < 2 = game r cells (cell:output ++ newCells) -- adiciona as celulas mortas a lista de saida
    |otherwise = game r cells (output ++ newCells) --  sobrevive
    where neib = nboors cell cells
          newCells = [x | x <- (nbhood cell), (notElem x neib) && length (nboors x cells) == 3 && (notElem x output)] -- celulas geradas por multiplicacao

-- aUb - aIb, concatena todos os elementos de duas listas exceto aqueles que se repetem
inoutputUnion inp outp = filter (`notElem` inp) outp  ++  filter (`notElem` outp) inp

-- recebe a entrada e chama a função principal com a devida formatacao
nextRound cells = game cells cells []

-- pular n geracoes
skip cells 1 = nextRound cells
skip cells n =
    if n > 0 then
        let rec = inoutputUnion cells (skip cells (n-1)) in -- formatacao para "ENTRADA" para utilizar novamente
        inoutputUnion cells (inoutputUnion rec (nextRound rec)) -- Lembrando que devemos retornar os pontos que devem ser ALTERADOS. Logo, precisamos utilizar a mesma uniao para retirar os elementos na intersecao 
    else
        []
cellChar = "o"
voidChar = "-"
matrix :: (Int,Int) -> [(Int,Int)] -> [Char]
matrix (x,y) cells
    | y >= leny = "\n"
    | x >= lenx = "\n" ++ matrix(0,y+1) cells
    | (x,y) `elem` cells = cellChar ++ matrix (x+1,y) cells
    | otherwise = voidChar ++ matrix(x+1,y) cells

imprimir cells = putStr (matrix (0,0) cells)

start' cells gen = do
    imprimir cells
    print (gen)
    putStr "Avancar: "
    line<-getLine
    let nrCells = (skip cells (read line)) in start' (  inoutputUnion cells nrCells  ) (gen + read(line)::Int )

start cells = start' cells 0

main = do
    (arg1:arg2:args) <- getArgs
    print (skip (read arg1) (read arg2) )