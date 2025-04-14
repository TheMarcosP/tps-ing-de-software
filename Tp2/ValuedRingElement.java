package anillo;

class ValuedRingElement extends RingElement {
    private ValuedRingElement previous = this;
    private ValuedRingElement next = this;
    private Object cargo;

    ValuedRingElement(Object cargo) {
        this.cargo = cargo;
    }

    RingElement getNext() {
        return next;
    }

    Object getCargo() {
        return cargo;
    }

    ValuedRingElement addOther(ValuedRingElement other) {
        other.next = this;
        other.previous = previous;
        previous.next = other;
        previous = other;
        return other;
    }

    RingElement removeOther(RingElement other) {
        return other.removeSelf();
    }

    RingElement removeSelf() {
        previous.next = next;
        next.previous = previous;
        return next;
    }
}
