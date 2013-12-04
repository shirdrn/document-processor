package org.shirdrn.document.processor.utils;

import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;

import org.shirdrn.document.processor.common.Term;

public class SortUtils {

	public static void heapSort(Entry<String, Term>[] a, boolean isDescOrder, int topN) {
		Entry<String, Term> tmp; // 用于交换的暂存单元
		buildHeap(a); // 执行初始建堆，并调整
		for (int i = 0; i < a.length; i++) {
            // 交换堆顶元素array[0]和堆中最后一个元素array[array.length-1-i]
            tmp = a[0];
            a[0] = a[a.length - 1 - i];
            a[a.length - 1 - i] = tmp;
            // 每次交换堆顶元素和堆中最后一个元素之后，都要对堆进行调整
            adjustHeap(a, 0, a.length - 1 - i, isDescOrder, topN);
		}
	}
	
	/**
     * 建堆方法:
     * 调整堆中0~array.length/2个结点，保持堆的性质
     */
    private static void buildHeap(Entry<String, Term>[] a) {
        // 求出当前堆中最后一个存在孩子结点的索引
        int pos = (a.length - 1) / 2;
        // 从该结点结点开始，执行建堆操作
        for (int i = pos; i >= 0; i--) {
//            adjustHeap(a, i, a.length); // 在建堆过程中，及时调整堆中索引为i的结点
        }
    }

    /**
     * 
     * 调整堆的方法
     * @param s 待调整结点的索引
     * @param m 待调整堆的结点的数量(亦即：排除叶子结点)
     * @param isDescOrder 是否是降序(是则为大顶堆, 否则为小根堆)
     * @param topN 堆选排序topN个元素
     */
    private static void adjustHeap(Entry<String, Term>[] a, int s, int m, boolean isDescOrder, int topN) {
    	Entry<String, Term> tmp = a[s]; // 当前待调整的结点
        int i = 2 * s + 1; // 当前待调整结点的左孩子结点的索引(i+1为当前调整结点的右孩子结点的索引)
        while (i < m) {
        	if(isDescOrder) {
        		if (i + 1 < m && a[i].getValue().getChi() > a[i + 1].getValue().getChi()) { // 如果右孩子小于左孩子(找到比当前待调整结点大的孩子结点)
        			i = i + 1;
        		}
        		if(topN == 0) {
        			break;
        		}
        		topN--;
        		if (a[s].getValue().getChi() > a[i].getValue().getChi()) {
                    a[s] = a[i]; // 孩子结点小于当前待调整结点，将孩子结点放到当前待调整结点的位置上
                    s = i; // 重新设置待调整的下一个结点的索引
                    i = 2 * s + 1;
                } else { // 如果当前待调整结点小于它的左右孩子，则不需要调整，直接退出
                    break;
                }
        	} else {
        		if (i + 1 < m && a[i].getValue().getChi() < a[i + 1].getValue().getChi()) { // 如果右孩子大于左孩子(找到比当前待调整结点大的孩子结点)
        			i = i + 1;
        		}
        		if(topN == 0) {
        			break;
        		}
        		topN--;
        		if (a[s].getValue().getChi() < a[i].getValue().getChi()) {
                    a[s] = a[i]; // 孩子结点大于当前待调整结点，将孩子结点放到当前待调整结点的位置上
                    s = i; // 重新设置待调整的下一个结点的索引
                    i = 2 * s + 1;
                } else { // 如果当前待调整结点大于它的左右孩子，则不需要调整，直接退出
                    break;
                }
        	}
            a[s] = tmp; // 当前待调整的结点放到比其大的孩子结点位置上
        }
    }
    
    
    
    @SuppressWarnings("unchecked")
	public static void main(String[] args) {
    	Map<String, Term> terms = new HashMap<String, Term>();
    	Term t1 = new Term("hello");
    	t1.setTfidf(12.5450);
    	terms.put("01", t1);
    	Term t2 = new Term("world");
    	t2.setTfidf(1.2211);
    	terms.put("02", t2);
    	Term t3 = new Term("just");
    	t3.setTfidf(19.3654);
    	terms.put("03", t3);
    	Term t4 = new Term("shit");
    	t4.setTfidf(6.8974);
    	terms.put("04", t4);
    	
    	Entry<String, Term>[] a = new Entry[terms.size()];
		a = terms.entrySet().toArray(a);
		for(Entry<String, Term> t : a) {
    		System.out.println(t.getKey() + "\t:" + t.getValue());
    	}
		
		System.out.println();
		
    	heapSort(a, true, 2);
    	for(int i=0; i<a.length; i++) {
    		Entry<String, Term> t = a[i];
    		System.out.println(t.getKey() + "\t:" + t.getValue());
    	}
	}
}
