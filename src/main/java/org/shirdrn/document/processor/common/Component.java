package org.shirdrn.document.processor.common;

public interface Component {

	void fire();
	Component getNext();
	Component setNext(Component next);
}
