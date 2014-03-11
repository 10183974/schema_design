package duke.hbase.sd;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;

public class RuleBasedTREnumerator {

	private Collection<? extends Transformation> enumerateForJoinQ(
			Application app) {
		// TODO Auto-generated method stub
		return null;
	}

	private Collection<? extends Transformation> enumerateForScanQ(
			Application app) {
		// TODO Auto-generated method stub
		return null;
	}

	private Collection<? extends Transformation> enumerateForUpdateQ(
			Application app) {
		// TODO Auto-generated method stub
		return null;
	}

	private Collection<? extends Transformation> enumerateForWriteQ(
			Application app) {
		// TODO Auto-generated method stub
		return null;
	}

	private Collection<? extends Transformation> enumerateForReadQ(Application app) {
		return null;		
	}
	
	public ArrayList<Transformation> enumerate(Application app) throws Exception {
		
		ArrayList<Transformation> transformations = new ArrayList<Transformation>();
		
		Iterator<Query> q_itr = app.getQueries().iterator();
		while(q_itr.hasNext()) {
			Query q = q_itr.next();
			switch(q.getType()) {
			case "read":
				transformations.addAll(enumerateForReadQ(app));
				break;
			case "write":
				transformations.addAll(enumerateForWriteQ(app));
			break;
			case "update":
				transformations.addAll(enumerateForUpdateQ(app));
			break;	
			case "scan":
				transformations.addAll(enumerateForScanQ(app));
				break;
			case "join":
				transformations.addAll(enumerateForJoinQ(app));
				break;
			default:
				throw new Exception("Invalid query type");
			}
			
		}
	
		System.out.println("Rulebased enumerator suggests " + transformations.size() + " transformations");
		return transformations;
	}
}
