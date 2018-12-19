import java.util.ArrayList;
import java.util.Random;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;

class processObj extends Thread {
	
	int logiClk,eventCnt,t,iteration,pId,prob;
	BlockingQueue<Queue> mesQ;
	masterObj mo;
	ArrayList<processObj> pQueRef;
	Random rand = new Random();
	int SimuIteration;
	
	processObj(masterObj mo, int t,int pId,int SimuIteration, int prob) {
		
		logiClk = 0;
		eventCnt = 0;
		iteration = 0;
		this.t = t;
		this.pId = pId;
		this.SimuIteration = SimuIteration;
		this.prob = prob;
		mesQ = new LinkedBlockingQueue<Queue>();
				
		//set the reference to master object
		this.mo = mo;
	}
	
	//set the reference of all the processes
	public void setProcessRef(ArrayList<processObj> pQueRef){
		this.pQueRef = pQueRef;
	}
	
	public void run() {
		System.out.println("Process Thread " + pId + " started..");
		
				
		while(iteration < SimuIteration){			
			
			int eventSel = rand.nextInt(10);
						
			//send event
			if(eventCnt == t){
				System.out.println("Send event of Thread " + pId + " with LogiClk: " + logiClk);
				//send logical clock value to other PO 
				sendClkMaster(mo,pId,logiClk);
				
				//increment the logical clock and event counter t
				logiClk++;
				eventCnt = 1;
								
				if(eventSel < 4){
					if(eventSel != pId){
						System.out.println("Send event of Process to other Process..");
						
						//send logical clock value to other PO
						sendClkPo(pQueRef.get(eventSel),pId,logiClk);
						
						//increment the logical clock and event counter t
						eventCnt++;
						logiClk++;
					}
				}
							
			}//send
			
			else {
				//receive event
	
				if(eventSel > prob){
					logiClk++;
					if(!mesQ.isEmpty()){
						//Receive from master
						if(mesQ.peek().getThreadId() == -1) {
							System.out.println(pId + " received offset " + mesQ.peek().getLogiClk());
							
							
							//Byzantine failure						
							if(eventSel == 9){
								System.out.println("Byzantine failure. Do not update the clock");
								mesQ.poll();
							}
							else
								logiClk = logiClk + mesQ.poll().getLogiClk() + 1;
							
							//increment the iteration of PO
							iteration++;
							
							System.out.println("Receive event of Thread " + pId + " with new logicalClk: " + logiClk 
									+ " ITERATION: " + iteration);
														
						}
						//Receive from other process
						else {
							int temp = mesQ.poll().getLogiClk();
							System.out.println("Internal P2P");
							
							if(logiClk <= temp)
								logiClk = temp+1;
						}
					}//msg
					
				}
				
				//internal event
				else if(eventSel < prob){
					try {
						Thread.sleep((eventSel+1)*10);
					} catch (Exception e) {
						e.printStackTrace();
					}
					
					//increment the logical clock value depending on the random value
					logiClk += (eventSel+1);
					
					System.out.println("Internal event of Thread " + pId + " with increment of "+ eventSel+ " Clock: " + logiClk);
				}
				
				//increment the event count either receive or internal event
				eventCnt++;	
			}
			
		}//iteration
		System.out.println("goodBye"+ pId);
					
	}//run
	
	
	//send logical clock value to MO
	public void sendClkMaster(masterObj mo,int pId, int logiClk){
		Queue q = new Queue();
		q.setLogiClk(logiClk);
		q.setThreadId(pId);
		
		try {
			mo.mesQ.put(q);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
	}
	
	//send logical clock value to other PO
	public void sendClkPo(processObj po,int pId, int logiClk){
		Queue q = new Queue();
		q.setLogiClk(logiClk);
		q.setThreadId(pId);
		
		try {
			po.mesQ.put(q);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
	}
	
}