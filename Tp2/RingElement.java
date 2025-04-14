package anillo;

abstract class RingElement {
    abstract RingElement getNext();
    abstract Object getCargo();
    abstract ValuedRingElement addOther(ValuedRingElement other);
    abstract RingElement removeOther(RingElement other);
    abstract RingElement removeSelf();
}
