module Truck (Truck, newT, freeCellsT, loadT, unloadT, netT)
  where

import Palet
import Stack
import Route

data Truck = Tru [ Stack ] Route deriving (Eq, Show)

-- | Construye un camión con la cantidad de bahías, la altura de las mismas y la ruta indicada
newT :: Int -> Int -> Route -> Truck
newT nStacks capacity route
    | nStacks <= 0 = error "Number of stacks must be positive"
    | capacity <= 0 = error "Capacity must be positive"
    | otherwise = Tru (replicate nStacks (newS capacity)) route

-- | Responde la cantidad de celdas disponibles en el camión
freeCellsT :: Truck -> Int
freeCellsT (Tru stacks _) = sum (map freeCellsS stacks)

-- | Carga un palet en el camión
loadT :: Truck -> Palet -> Truck
loadT truck palet
    | freeCellsT truck == 0 = error "Truck is at full capacity"
    | not (inRouteR route (destinationP palet)) = error "Palet destination not in route"
    | netT loadedTruck == netT truck = error "No stack can hold the palet"
    | otherwise = loadedTruck
      where
        Tru stacks route = truck
        loadedTruck = Tru (stackPalet stacks palet route) route
        stackPalet :: [Stack] -> Palet -> Route -> [Stack]
        stackPalet [] _ _ = []
        stackPalet (stack : stacks) palet route
            | holdsS stack palet route = stackS stack palet : stacks
            | otherwise = stack : stackPalet stacks palet route

-- | Responde un camión al que se le han descargado los palets que podían descargarse en la ciudad
unloadT :: Truck -> String -> Truck
unloadT (Tru stacks route) city
    | not (inRouteR route city) = error "City not in route"
    | netT unloadedTruck == netT (Tru stacks route) = error "No palets to unload"
    | otherwise = unloadedTruck
      where
        unloadedTruck = Tru (map (`popS` city) stacks) route

-- | Responde el peso neto en toneladas de los palets en el camión
netT :: Truck -> Int
netT (Tru stacks _) = sum (map netS stacks)
