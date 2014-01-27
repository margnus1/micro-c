import java.util.*;

class Call implements RtlInsn {
    private Integer temp;
    private String label;
    private List<Integer> args;

    public Call (Integer _temp, String _label, List<Integer> _args){
	temp =_temp;
	label =_label;
	args =_args;
    }

    public Integer getTemp (){
	return temp;
    }

    public void setTemp (Integer _temp){
	temp =_temp;
    }

    public String getLabel (){
	return label;
    }

    public void setLabel (String _label){
	label =_label;
    }

    public List<Integer> getArgs (){
	return args;
    }

    public void setArgs (List<Integer> _args){
	args =_args;
    }

    public String toString(){
	StringBuffer printArgs = new StringBuffer();
	for (int arg : args) {
	    printArgs.append(Rtl.regToString(arg));
	}

	return "call" + "(" + Rtl.regToString(temp) + " " + 
	    label + " " + printArgs + ")";
    }
}