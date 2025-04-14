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
        other.previous = this.previous;
        this.previous.next = other;
        this.previous = other;
        return other;
    }

    RingElement removeOther(RingElement other) {
        return other.removeSelf();
    }

    RingElement removeSelf() {
        this.previous.next = this.next;
        this.next.previous = this.previous;
        return this.next;
    }
}
