package duke.hbase.sd;

import java.lang.reflect.Method;
import java.util.ArrayList;

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
}
