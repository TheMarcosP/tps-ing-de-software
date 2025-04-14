package anillo;

import java.util.Stack;
import java.util.function.Function;

public class Ring {
    private RingElement currentElement = new NullRingElement();
    private Stack<Function<RingElement, RingElement>> elementRemovers = new Stack<>();

    public Ring next() {
        currentElement = currentElement.getNext();
        return this;
    }

    public Object current() {
        return currentElement.getCargo();
    }

    public Ring add(Object cargo) {
        elementRemovers.push(currentElement::removeOther);
        currentElement = currentElement.addOther(new ValuedRingElement(cargo));
        return this;
    }

    public Ring remove() {
        currentElement = elementRemovers.pop().apply(currentElement);
        return this;
    }
}
