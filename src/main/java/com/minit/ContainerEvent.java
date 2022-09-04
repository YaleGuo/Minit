package com.minit;

import java.util.EventObject;

public final class ContainerEvent extends EventObject {
private Container container = null;
private Object data = null;
private String type = null;
public ContainerEvent(Container container, String type, Object data) {
    super(container);
    this.container = container;
    this.type = type;
    this.data = data;
}

public Object getData() {
    return (this.data);
}

public Container getContainer() {
    return (this.container);
}

public String getType() {
    return (this.type);
}

public String toString() {
    return ("ContainerEvent['" + getContainer() + "','" +
            getType() + "','" + getData() + "']");
}


}