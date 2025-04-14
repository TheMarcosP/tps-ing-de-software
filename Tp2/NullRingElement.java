package anillo;

class NullRingElement extends RingElement {
    RingElement getNext() {
        throw new RuntimeException("Ring is empty");
    }

    Object getCargo() {
        throw new RuntimeException("Ring is empty");
    }

    ValuedRingElement addOther(ValuedRingElement other) {
        return other;
    }

    RingElement removeOther(RingElement other) {
        return this;
    }

    RingElement removeSelf() {
        throw new RuntimeException("Ring is empty");
    }
}
