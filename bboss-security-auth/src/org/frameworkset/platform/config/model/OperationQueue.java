package org.frameworkset.platform.config.model;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * <p>Title: </p> 
 *
 * <p>Description: </p>
 *
 * <p>Copyright: Copyright (c) 2006</p>
 *
 * <p>Company: 三一集团</p>
 *
 * @author biaoping.yin
 * @version 1.0
 */
public class OperationQueue implements java.io.Serializable {
    private List<Operation> queue = new ArrayList<Operation>();
    
  

	public static void main(String[] args) {
        OperationQueue operationqueue = new OperationQueue();
    }

    /**
     * 添加操作项，并且根据优先级对操作排序
     * @param oper
     */
    public void addOperation(Operation oper)
    {
    	addOperation(oper,true);
    }
    
    /**
     * 添加操作项，并且根据sort参数的值决定是否根据优先级对操作排序，如果sort=true则排序，否则不排序
     * @param oper
     * @param sort
     */
    public void addOperation(Operation oper,boolean sort)
    {
        queue.add(oper);
        if(sort)
        	sort();
        
    }
    
    

    public Operation getOperation(int i)
    {
        return (Operation)queue.get(i);
    }

    public int size()
    {
        return queue.size();
    }

    public List<Operation> getList()
    {
        return this.queue;
    }

    public void sort()
    {
        Collections.sort(queue,new OperComparable());
    }


    class OperComparable implements java.util.Comparator
    {
        /**根据号码长度对指令排序*/
        public int compare(Object o1, Object o2) {
            Operation m1 = (Operation)o1;
            Operation m2 = (Operation)o2;

            return m1.getPriority().compareTo(m2.getPriority());
        }


    }


}
