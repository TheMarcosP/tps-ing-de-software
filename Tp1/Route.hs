module Route (Route, newR, inOrderR, inRouteR)
  where

data Route = Rou [String] deriving (Eq, Show)

-- | Construye una ruta a partir de una lista de ciudades
newR :: [String] -> Route
newR [] = error "Route cannot be empty"
newR cities
    | hasDuplicates cities = error "Route cannot contain duplicate cities"
    | otherwise = Rou cities
      where
        hasDuplicates :: [String] -> Bool
        hasDuplicates [] = False
        hasDuplicates (x : xs) = elem x xs || hasDuplicates xs

-- | Indica si la primera ciudad consultada está antes que la segunda ciudad en la ruta
inOrderR :: Route -> String -> String -> Bool
inOrderR route city1 city2
    | not (inRouteR route city1 && inRouteR route city2) = error "City not in route"
    | otherwise = isBefore cities city1 city2
      where
        Rou cities = route
        isBefore :: [String] -> String -> String -> Bool
        isBefore (x : xs) city1 city2
            | x == city1 = True
            | x == city2 = False
            | otherwise = isBefore xs city1 city2

-- | Indica si la ciudad consultada está en la ruta
inRouteR :: Route -> String -> Bool
inRouteR (Rou cities) city = elem city cities
