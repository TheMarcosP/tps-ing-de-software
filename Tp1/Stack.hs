module Stack (Stack, newS, freeCellsS, stackS, netS, holdsS, popS)
  where

import Palet
import Route

data Stack = Sta [Palet] Int deriving (Eq, Show)

-- | Construye una pila con la capacidad indicada
newS :: Int -> Stack 
newS capacity
    | capacity <= 0 = error "Capacity must be positive"
    | otherwise = Sta [] capacity

-- | Responde la cantidad de celdas disponibles en la pila
freeCellsS :: Stack -> Int
freeCellsS (Sta palets capacity) = capacity - length palets

-- | Apila el palet indicado en la pila
stackS :: Stack -> Palet -> Stack
stackS stack palet
    | freeCellsS stack == 0 = error "Stack is at full capacity"
    | netS stack + netP palet > 10 = error "Stack weight limit exceeded"
    | otherwise = Sta (palet : palets) capacity
      where
        Sta palets capacity = stack

-- | Responde el peso neto de los palets en la pila
netS :: Stack -> Int
netS (Sta palets _) = sum (map netP palets)

-- | Indica si la pila puede aceptar el palet considerando las ciudades en la ruta
holdsS :: Stack -> Palet -> Route -> Bool
holdsS stack palet route
    | not (foldr (&&) True [inRouteR route (destinationP p) | p <- palets]) =
        error "Invalid stack: palets contain city destinations not in route"
    | not (foldr (&&) True [inOrderR route (destinationP p1) (destinationP p2) | (p1, p2) <- zip palets (tail palets)]) =
        error "Invalid stack: palets are not in order according to the route"
    | freeCellsS stack == 0 || netS stack + netP palet > 10 || not (inRouteR route (destinationP palet)) = False
    | null palets = True
    | otherwise = inOrderR route (destinationP palet) (destinationP (head palets))
      where
        Sta palets _ = stack

-- | Quita del tope los palets con destino en la ciudad indicada
popS :: Stack -> String -> Stack
popS (Sta [] capacity) _ = Sta [] capacity
popS (Sta (palet : palets) capacity) city
    | destinationP palet /= city = Sta (palet : palets) capacity
    | otherwise = popS (Sta palets capacity) city


