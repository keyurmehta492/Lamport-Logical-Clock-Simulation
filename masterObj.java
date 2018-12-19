import java.io.BufferedWriter;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;

public class masterObj  extends Thread{
	
	int logiClk,eventCnt;
	BlockingQueue<Queue> mesQ;
	ArrayList<processObj> pQueRef;
	HashMap<Integer,Integer> processClk = new HashMap<Integer,Integer>();
	int avgLogiClk=0,offset,CorrLogiClk;
	
	File fout;
	FileOutputStream fos;
	BufferedWriter bw;
	
	masterObj() {
		logiClk = 0;
		eventCnt = 0;
		mesQ = new LinkedBlockingQueue<Queue>();
		//create file to write the POs logical clock
		fout = new File("Thread_LogiClk.txt");
		
		try{
			fos = new FileOutputStream(fout);
			bw = new BufferedWriter(new OutputStreamWriter(fos));
		
			bw.write("P0,P1,P2,P3");
			bw.newLine();
		}
		catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	//set the reference of all the processes
	public void setProcessRef(ArrayList<processObj> pQueRef){
			this.pQueRef = pQueRef;
	}
		
	public void run() {
		
		System.out.println("Master Thread " + Thread.currentThread().getId() + " started..");
		
		try {
			
			while(true){
				
				if(!mesQ.isEmpty()){
					processClk.put(mesQ.peek().getThreadId(),mesQ.poll().getLogiClk());
					logiClk++;
					
					//calculate average if all 4 values are received
					if(processClk.keySet().size() == 4){
						avgLogiClk = 0;
						
						logiClk++;
						System.out.println("Master calculation and its logiClk: " + logiClk);
						
						for(Integer val : processClk.keySet()){
							System.out.println(val + " " + processClk.get(val));
							avgLogiClk += processClk.get(val);
						}
																	
						//add master's logical clock
						avgLogiClk += logiClk;
						
						//calculate average of value
						CorrLogiClk = avgLogiClk / 5;
						System.out.println("CorrLogiClk: " + CorrLogiClk);
						
						//adjust master objects logical clock
						logiClk = logiClk + (CorrLogiClk-logiClk) + 1;
												
						
						StringBuilder sb = new StringBuilder();
						//calculate the offset for each Processes
						for(Integer val : processClk.keySet()){
							offset = CorrLogiClk - processClk.get(val);
							System.out.println("Offset of pId " + val + " is: " + offset);
							//Send the offset to the Processes 
							sendClkPO(pQueRef.get(val),offset);
							sb.append(processClk.get(val)).append(",");
						}

												
						bw.write(sb.toString());
						bw.newLine();
						bw.flush();
						System.out.println("Written to file");
						
						processClk.clear();
						
					}
				}//calculate Average
				else{
					//internal event of master
					Thread.sleep(20);
					logiClk++;
					//System.out.println("Master Thread Internal Event: " + logiClk);					
				}
				
			}//while

			
		} catch (Exception e) {
			e.printStackTrace();
		}
		finally{
			try {
				bw.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	}


	public void sendClkPO(processObj pQueRef, int offset){
		Queue q = new Queue();
		q.setLogiClk(offset);
		q.setThreadId(-1);
		try {
			pQueRef.mesQ.put(q);
		} catch (InterruptedException e) {
			
			e.printStackTrace();
		}
	}

}//class masterObj