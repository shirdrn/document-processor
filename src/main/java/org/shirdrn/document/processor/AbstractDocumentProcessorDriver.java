package org.shirdrn.document.processor;

import org.shirdrn.document.processor.common.Component;
import org.shirdrn.document.processor.utils.ReflectionUtils;

public abstract class AbstractDocumentProcessorDriver {

	public abstract void process();
	
	protected void run(Component[] chain) {
		for (int i = 0; i < chain.length - 1; i++) {
			Component current = chain[i];
			Component next = chain[i + 1];
			current.setNext(next);
		}
		
		for (Component componennt : chain) {
			componennt.fire();
		}
	}
	
	public static void start(Class<? extends AbstractDocumentProcessorDriver> driverClass) {
		AbstractDocumentProcessorDriver driver = ReflectionUtils.newInstance(driverClass);
		driver.process();
	}
}
