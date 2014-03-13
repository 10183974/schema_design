package duke.hbase.sd;

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Iterator;

public class Transformation {

	private Method transformationRule;
	private ArrayList<Object> arguments;
	
	public Method getTransformationRule() {
		return transformationRule;
	}
	public void setTransformationRule(Method transformationRule) {
		this.transformationRule = transformationRule;
	}
	public ArrayList<Object> getArguments() {
		return arguments;
	}
	public void setArguments(ArrayList<Object> arguments) {
		this.arguments = arguments;
	}
	public String toString() {
		StringBuffer sb = new StringBuffer();
		sb.append("Rule: " + getTransformationRule().getName() + "\n");
		sb.append("arguments: \n");
		Iterator<Object> a_itr = getArguments().iterator();
		while(a_itr.hasNext()) {
		  Object obj = a_itr.next();
		  sb.append(obj.toString());
		}
		return sb.toString();
	}
}
