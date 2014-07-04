package vmarcinko.nanocubes;

import java.util.*;

public class Node implements Content {
    private final Long value;

    private final Map<Long, Link<Node>> childLinks = new HashMap<>();
    private Link<? extends Content> contentLink = null;

    public Node(Long value) {
        this.value = value;
    }

    /**
     * Creates a new child link to node keyed on value.
     */
    public Node newProperChild(Long value) {
        Node childNode = new Node(value);
        childLinks.put(value, new Link<>(false, childNode));
        return childNode;
    }

    /**
     * Creates a shared child link to node keyed on value
     */
    public void newSharedChild(Long value, Node node) {
        Node nodeChild = node.childLinks.get(value).getTarget();
        childLinks.put(value, new Link<>(true, nodeChild));
    }

    public Map<Long, Link<Node>> getChildLinks() {
        return childLinks;
    }

    public Node getChild(Long value) {
        Link<Node> childLink = childLinks.get(value);
        if (childLink == null) {
            return null;
        }
        return childLink.getTarget();
    }

    /**
     * Convenience method to create a shared content link to the content in given node.
     */
    public void setSharedContentWithNode(Node node) {
        setContent(true, node.contentLink.getTarget());
    }

    public void setContent(boolean shared, Content content) {
        this.contentLink = new Link<>(shared, content);
    }

    public boolean isContentShared() {
        if (contentLink == null) {
            throw new IllegalStateException("There is no content");
        }
        return contentLink.isShared();
    }

    public Content getContent() {
        if (contentLink == null) {
            return null;
        }
        return contentLink.getTarget();
    }

    public <C extends Content> C getContent(Class<C> clazz) {
        return (C) getContent();
    }

    @Override
    public Content shallowCopy() {
        // Creates a new copy of the node with shared content and shared children
        Node copy = new Node(value);
        copy.setSharedContentWithNode(this);
        for (Long label : childLinks.keySet()) {
            copy.newSharedChild(label, this);
        }
        return copy;
    }

    @Override
    public void appendPrettyPrint(StringBuilder sb, int depth) {
        String valueDescription = value == null ? "--dimension-root--" : value.toString();
        sb.append("Node(").append(valueDescription).append(")");

        // display children
        if (!childLinks.isEmpty()) {
            appendTabbedNewLine(sb, depth + 1);
            sb.append("# children:");
            for (Link<Node> childLink : childLinks.values()) {
                appendTabbedNewLine(sb, depth + 2);
                childLink.appendPrettyPrint(sb, depth + 2);
            }
        }

        // display content
        if (contentLink != null) {
            appendTabbedNewLine(sb, depth + 1);
            sb.append("# content: ");
            contentLink.appendPrettyPrint(sb, depth + 2);
        }
    }

    private void appendTabbedNewLine(StringBuilder sb, int tabCount) {
        sb.append("\n");
        for (int i = 0; i < tabCount; i++) {
            sb.append("\t");
        }
    }

}
