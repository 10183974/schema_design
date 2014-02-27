package duke.hbase.cm.tdg;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class RandomKofN {
    public List<Integer> generate(int k, int N){
        if (true){
            List<Integer> fakeIndex = new ArrayList<Integer>();
            for(int i=0;i<8;i++)
                 fakeIndex.add(i+1);
            return fakeIndex;
        }
    	
    	//return k distinct random number from 1 to N
		List<Integer> index = new ArrayList<Integer>();
		for(int i=0;i<N;i++){
			index.add(i+1);
		}
		Collections.shuffle(index);			 
		List<Integer> resList = index.subList(0, k);

//	 	for(int a:resList)
//			 System.out.print(a + ", ");
//	 	System.out.println("");		
		return resList;	
	}

}
