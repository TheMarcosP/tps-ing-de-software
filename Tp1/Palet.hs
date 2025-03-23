module Palet (Palet, newP, destinationP, netP)
  where

data Palet = Pal String Int deriving (Eq, Show)

-- | Construye un palet dada una ciudad de destino y un peso en toneladas
newP :: String -> Int -> Palet
newP city weight
    | weight <= 0 = error "Weight in tonnes must be positive"
    | otherwise = Pal city weight

-- | Responde la ciudad destino del palet
destinationP :: Palet -> String
destinationP (Pal city _) = city

-- | Responde el peso en toneladas del palet
netP :: Palet -> Int
netP (Pal _ weight) = weight
