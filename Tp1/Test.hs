module Test 
  where

import Palet
import Route
import Stack
import Truck
import Control.Exception
import System.IO.Unsafe

testF :: Show a => a -> Bool
testF action = unsafePerformIO $ do
    result <- tryJust isException (evaluate action)
    return $ case result of
        Left _ -> True
        Right _ -> False
    where
        isException :: SomeException -> Maybe ()
        isException _ = Just ()

-- Cities --
a = "A"
b = "B"
c = "C"
d = "D"
e = "E"

-- Palets --
paletA2 = newP a 2
paletA8 = newP a 8
paletB1 = newP b 1
paletB8 = newP b 8
paletC2 = newP c 2
paletC4 = newP c 4
paletD8 = newP d 8
paletD1 = newP d 1
paletE2 = newP e 2

-- Routes --
routeD = newR [d]
routeABCD = newR [a, b, c, d]

-- Stacks --
stack = newS 4
stackD1 = stackS stack paletD1
stackD1B1 = stackS stackD1 paletB1
stackD1B1B1 = stackS stackD1B1 paletB1
stackD1B1B1A2 = stackS stackD1B1B1 paletA2
invalidSA2C2RouteABCD = stackS (stackS stack paletA2) paletC2
invalidSE2C2RouteABCD = stackS (stackS stack paletE2) paletC2

-- Trucks --
smallT = newT 1 1 routeD
largeT = newT 2 2 routeABCD
smallTD1 = loadT smallT paletD1
largeTD1 = loadT largeT paletD1
largeTD1C4 = loadT largeTD1 paletC4
largeTD1C4C2 = loadT largeTD1C4 paletC2
largeTD1C4C2C2 = loadT largeTD1C4C2 paletC2

t = [ 
    -- Palet module --
    -- NewP  
    not (testF (newP "A" 50)), 
    testF(newP "B" 0),
    testF(newP "B" (-1)), 
    -- destinationP
    destinationP paletA2 == "A",
    destinationP paletB8 /= "A", 
    -- netP
    netP paletA2 == 2, 
    netP paletB8 /= 2,
    
    -- Route module --
    -- NewR
    not (testF (newR [a, b, c])),
    testF(newR []), 
    testF(newR [b, b, c]),
    -- inRouteR
    inOrderR routeABCD a b,
    not (inOrderR routeABCD b a),
    inOrderR routeABCD b b, 
    testF (inOrderR routeD b c),
    inRouteR routeABCD a,
    not (inRouteR routeABCD e),

    -- Stack module --
    -- NewS
    not (testF (newS 10)), 
    testF (newS 0),
    testF (newS (-1)),
    -- freeCellsS
    freeCellsS stack == 4, 
    freeCellsS stackD1B1B1A2 == 0,
    freeCellsS (popS stackD1B1B1A2 a) == 1,
    -- stackS
    not (testF (stackS stackD1 paletB8)),
    testF(stackS stackD1B1B1A2 paletA2),
    testF(stackS stackD1B1B1 paletB8),
    -- netS
    netS stack == 0,
    netS stackD1 == 1,
    netS stackD1B1B1A2 == 5,

    -- holdsS
    holdsS stack paletD1 routeD,
    holdsS stackD1B1B1 paletA2 routeABCD,
    not (holdsS stackD1B1B1A2 paletA2 routeABCD), 
    not (holdsS stackD1B1B1 paletA8 routeABCD), 
    not (holdsS stackD1 paletA2 routeD), 
    not (holdsS stackD1B1B1 paletC2 routeABCD),
    testF (holdsS invalidSE2C2RouteABCD paletB1 routeABCD), -- error: palets contain city destinations not in route
    testF (holdsS invalidSA2C2RouteABCD paletB1 routeABCD), -- error: palets are not in order according to the route
    -- popS
    popS stack "A" == stack,
    popS stackD1B1B1A2 a == stackD1B1B1,
    popS stackD1B1B1 b == stackD1,
    popS stackD1 c == stackD1, 
    popS stackD1B1B1 d == stackD1B1B1,

    -- Truck module --
    -- NewT
    not (testF (newT 1 1 routeD)),
    testF (newT 0 1 routeD),
    testF (newT 1 0 routeD),
    testF (newT (-1) 1 routeD),
    testF (newT 1 (-1) routeD),
    -- freeCellsT
    freeCellsT largeT == 4,
    freeCellsT largeTD1C4 == 2,
    freeCellsT smallTD1 == 0,
    -- loadT
    not (testF (loadT largeT paletC4)),
    netT (loadT largeTD1C4 paletD1) == 6,
    testF (loadT smallT paletA2), -- error: Palet destination not in route
    testF (loadT smallTD1 paletD1), -- error: Truck is at full capacity
    testF (loadT (loadT (loadT largeT paletB1) paletC2) paletD1), -- error: D1 does not fit in stacks [[B1],[C2]] 
    -- unloadT
    netT (unloadT smallTD1 d) == 0,
    netT (unloadT largeTD1C4C2C2 c) == 1, 
    testF (unloadT smallTD1 a), -- error: city not in route
    testF (unloadT smallT d), -- error: no palets to unload
    -- netT
    netT largeTD1C4C2C2 == 9
    ]
