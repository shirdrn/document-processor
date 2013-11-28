package org.shirdrn.document.processor.common;


public abstract class AbstractComponent implements Component {

	protected final Context context;
	private Component next;
	
	public AbstractComponent(Context context) {
		this.context = context;
	}
	
	@Override
	public Component getNext() {
		return next;
	}
	
	@Override
	public Component setNext(Component next) {
		this.next = next;	
		return next;
	}
	
}
