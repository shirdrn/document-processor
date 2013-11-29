package org.shirdrn.document.processor.common;


public abstract class AbstractComponent implements Component {

	protected final Context context;
	private Component next;
	protected String charSet = "UTF-8";
	
	public AbstractComponent(Context context) {
		this.context = context;
		String charSet = context.getConfiguration().get("processor.common.charset");
		if(charSet != null) {
			this.charSet = charSet;
		}
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
