package anillo;

class NullRingElement extends RingElement {
    RingElement getNext() {
        throw new RuntimeException("Cannot get next element, ring is empty");
    }

    Object getCargo() {
        throw new RuntimeException("Cannot get cargo, ring is empty");
    }

    ValuedRingElement addOther(ValuedRingElement other) {
        return other;
    }

    RingElement removeOther(RingElement other) {
        return this;
    }

    RingElement removeSelf() {
        throw new RuntimeException("Cannot remove element, ring is empty");
    }
}
