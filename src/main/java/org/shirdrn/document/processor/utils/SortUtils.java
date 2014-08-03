package org.shirdrn.document.processor.utils;

import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.shirdrn.document.processor.common.Term;

public class SortUtils {
	
	private static final Log LOG = LogFactory.getLog(SortUtils.class);
	private final Entry<String, Term>[] array;
	private boolean isDescOrder;
	private int topN;
	private final Result result = new Result();
	
	@SuppressWarnings("unchecked")
	public SortUtils(Map<String, Term> terms, boolean isDescOrder, int topN) {
		super();
		array = new Entry[terms.size()];
		terms.entrySet().toArray(array);
		result.setArray(array);
		result.setEndIndex(array.length - 1);
		
		if(LOG.isDebugEnabled()) {
			print();
		}
		this.isDescOrder = isDescOrder;
		this.topN = topN;
	}
	
	private void print() {
		for(int i=0; i<array.length; i++) {
			Entry<String, Term> t = array[i];
			System.out.println(t.getKey() + "\t:" + t.getValue());
		}
	}

	public Result heapSort() {
		Entry<String, Term> tmp; // 用于交换的暂存单元
		buildHeap(); // 执行初始建堆，并调整
		if(LOG.isDebugEnabled()) {
			System.out.println("Build heap:");
			for (int j = 0; j < array.length; j++) {
				System.out.print(array[j].getValue().getChi() + "    ");
			}
			System.out.println();
		}
		
		for (int i = 0; i < array.length; i++) {
			// 交换堆顶元素array[0]和堆中最后一个元素array[array.length-1-i]
			tmp = array[0];
			array[0] = array[array.length - 1 - i];
			array[array.length - 1 - i] = tmp;
			if(--topN < 0) {
				result.setStartIndex(array.length - i);
				break;
			}
			// 每次交换堆顶元素和堆中最后一个元素之后，都要对堆进行调整
			adjustHeap(0, array.length - 1 - i);
			
			if(LOG.isDebugEnabled()) {
				for (int j = 0; j < array.length; j++) {
					System.out.print(array[j].getValue().getChi() + "    ");
				}
				System.out.println();
			}
			
		}
		return result;
	}
	
	/**
     * 建堆方法:
     * 调整堆中0~array.length/2个结点，保持堆的性质
     */
    private void buildHeap() {
    	// 求出当前堆中最后一个存在孩子结点的索引
		int pos = (array.length - 1) / 2;
		// 从该结点结点开始，执行建堆操作
		for (int i = pos; i >= 0; i--) {
			adjustHeap(i, array.length); // 在建堆过程中，及时调整堆中索引为i的结点
		}
    }

    /**
     * 
     * 调整堆的方法
     * @param s 待调整结点的索引
     * @param m 待调整堆的结点的数量(亦即：排除叶子结点)
     */
    private void adjustHeap(int s, int m) {
    	if(isDescOrder) {
    		adjustForAsc(s, m);
    	} else {
    		adjustForDesc(s, m);
    	}
    }
    
    private void adjustForDesc(int s, int m) {
    	Entry<String, Term> tmp = array[s]; // 当前待调整的结点
		int i = 2 * s + 1; // 当前待调整结点的左孩子结点的索引(i+1为当前调整结点的右孩子结点的索引)
		while (i < m) {
			if (i + 1 < m && array[i].getValue().getChi() > array[i + 1].getValue().getChi()) { // 如果右孩子大于左孩子(找到比当前待调整结点大的孩子结点)
				i = i + 1;
			}
			if (array[s].getValue().getChi() > array[i].getValue().getChi()) {
				array[s] = array[i]; // 孩子结点大于当前待调整结点，将孩子结点放到当前待调整结点的位置上
				s = i; // 重新设置待调整的下一个结点的索引
				i = 2 * s + 1;
			} else { // 如果当前待调整结点大于它的左右孩子，则不需要调整，直接退出
				break;
			}
			array[s] = tmp; // 当前待调整的结点放到比其大的孩子结点位置上
		}
    }
    
    private void adjustForAsc(int s, int m) {
    	Entry<String, Term> tmp = array[s]; // 当前待调整的结点
		int i = 2 * s + 1; // 当前待调整结点的左孩子结点的索引(i+1为当前调整结点的右孩子结点的索引)
		while (i < m) {
			if (i + 1 < m && array[i].getValue().getChi() < array[i + 1].getValue().getChi()) { // 如果右孩子大于左孩子(找到比当前待调整结点大的孩子结点)
				i = i + 1;
			}
			if (array[s].getValue().getChi() < array[i].getValue().getChi()) {
				array[s] = array[i]; // 孩子结点大于当前待调整结点，将孩子结点放到当前待调整结点的位置上
				s = i; // 重新设置待调整的下一个结点的索引
				i = 2 * s + 1;
			} else { // 如果当前待调整结点大于它的左右孩子，则不需要调整，直接退出
				break;
			}
			array[s] = tmp; // 当前待调整的结点放到比其大的孩子结点位置上
		}
    }
    
    public static class Result {
    	
    	private Entry<String, Term>[] array;
    	private int startIndex;
    	private int endIndex;
    	
		public Entry<String, Term>[] getArray() {
			return array;
		}
		public void setArray(Entry<String, Term>[] array) {
			this.array = array;
		}
		public int getStartIndex() {
			return startIndex;
		}
		public void setStartIndex(int startIndex) {
			this.startIndex = startIndex;
		}
		public int getEndIndex() {
			return endIndex;
		}
		public void setEndIndex(int endIndex) {
			this.endIndex = endIndex;
		}
		public Entry<String, Term> get(int index) {
			return array[index];
		}
    }
    
    static void put(Map<String, Term> terms, String word, double chi) {
    	Term t = new Term(word);
    	t.setChi(chi);
    	terms.put(word, t);
    }
    
	public static void main(String[] args) {
    	Map<String, Term> terms = new HashMap<String, Term>();
    	put(terms, "god", 1.3250);
    	put(terms, "hello", 121.5450);
    	put(terms, "world", 104.2211);
    	put(terms, "just", 3.9802);
    	put(terms, "shift", 34.8719);
    	put(terms, "lion", 78.3223);
    	put(terms, "polo", 87.1209);
    	
		SortUtils sorter = new SortUtils(terms, false, 4);
		Result result = sorter.heapSort();
    	for(int i=result.getStartIndex(); i<=result.getEndIndex(); i++) {
    		Entry<String, Term> t = result.get(i);
    		System.out.println(t.getKey() + "\t:" + t.getValue());
    	}
	}
}
