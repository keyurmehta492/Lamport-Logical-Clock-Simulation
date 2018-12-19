import java.util.ArrayList;

public class LamportSimu {

	public static void main(String[] args) throws InterruptedException {
		
		//Set logical unit of threshold
		int t=50;
		
		//Set Simulation iteration count
		int SimuIteration = 1000;
		
		//Set the probability of the event between 1 to 10
		int prob = 6;
		
		//to store the reference of all POs
		ArrayList<processObj> pQueRef = new ArrayList<processObj>();
		
		//Master Object
		masterObj mo = new masterObj();
		
		for(int i=0; i<4;i++){
			processObj po = new processObj(mo,t,i,SimuIteration,prob);
			
			//save the reference of the thread in list
			pQueRef.add(po);
			
			//Start the process thread
			po.start();
			
		}
		
		//set the reference of all thread to all threads
		for(int i=0; i<4;i++){
			pQueRef.get(i).setProcessRef(pQueRef);
		}
		
		//set the reference of all thread to master threads
		mo.setProcessRef(pQueRef);
		
		//start master thread
		mo.start();
	
	}
	
}


