package filters;

import twitter4j.Status;

import java.util.ArrayList;
import java.util.List;

public class AndFilter implements Filter{
    private final Filter leftChild;
    private final Filter rightChild;

    public AndFilter(Filter child1, Filter child2) {
        this.leftChild = child1;
        this.rightChild = child2;
    }

    @Override
    public boolean matches(Status status) {
        return leftChild.matches(status) && rightChild.matches(status);
    }

    @Override
    public List<String> terms() {
        final List result = new ArrayList(leftChild.terms());
        result.addAll(rightChild.terms());
        return result;
    }

    public String toString() {
        return "(" + leftChild.toString() + " and " + rightChild.toString() + ")";
    }
}
