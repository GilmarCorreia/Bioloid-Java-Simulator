import coppelia.remoteApi;
import coppelia.CharWA;
import coppelia.FloatW;
import coppelia.FloatWA;
import coppelia.IntW;
import coppelia.IntWA;
import coppelia.StringW;
import coppelia.StringWA;

public class Bioloid{

	public remoteApi vrep;
	int clientID; // Setting client
	int cont = 0; // counter variable
	int i = 0;
	
	//Setting ID of Motors -------------------------------------------------------------------------------
	
	IntW id1;IntW id2;IntW id3;IntW id4;IntW id5;IntW id6;IntW id7;	IntW id8;IntW id9;
	IntW id10;IntW id11;IntW id12;IntW id13;IntW id14;IntW id15;IntW id16;IntW id17;IntW id18;
	IntW robot; IntW floor;
	
	//----------------------------------------------------------------------------------------------------
	
	//Setting the variables for motors positions ---------------------------------------------------------
	
	FloatW position1 = new FloatW(0); FloatW position2 = new FloatW(0);	FloatW position3 = new FloatW(0);
	FloatW position4 = new FloatW(0); FloatW position5 = new FloatW(0);	FloatW position6 = new FloatW(0);
	FloatW position7 = new FloatW(0); FloatW position8 = new FloatW(0);	FloatW position9 = new FloatW(0);
	FloatW position10 = new FloatW(0);FloatW position11 = new FloatW(0);FloatW position12 = new FloatW(0);
	FloatW position13 = new FloatW(0);FloatW position14 = new FloatW(0);FloatW position15 = new FloatW(0);
	FloatW position16 = new FloatW(0);FloatW position17 = new FloatW(0);FloatW position18 = new FloatW(0);
	
	//----------------------------------------------------------------------------------------------------
	
	//Setting sensors variables --------------------------------------------------------------------------
	FloatWA gyroData=new FloatWA(0);
	FloatWA CM=new FloatWA(0);
	//----------------------------------------------------------------------------------------------------
	
	public Bioloid(remoteApi vrep, int clientID, IntW id1 , IntW id2, IntW id3, IntW id4, IntW id5, IntW id6 ,IntW id7 ,IntW id8 ,IntW id9 ,IntW id10, IntW id11, IntW id12, IntW id13, IntW id14, IntW id15, IntW id16,IntW id17,IntW id18,IntW robot, IntW floor){
		this.vrep=vrep;
		this.clientID=clientID;
		this.id1 = id1;
		this.id2 = id2;
		this.id3 = id3;
		this.id4 = id4;
		this.id5 = id5;
		this.id6 = id6;
		this.id7 = id7;
		this.id8 = id8;
		this.id9 = id9;
		this.id10 = id10;
		this.id11 = id11;
		this.id12 = id12;
		this.id13 = id13;
		this.id14 = id14;
		this.id15 = id15;
		this.id16 = id16;
		this.id17 = id17;
		this.id18 = id18;
		this.robot = robot;
		this.floor = floor;
	}
	
	public void techCheck(){
	
		if (clientID!=-1){
			System.out.println("Connected to remote API server");
			
			if(vrep.simxGetObjectHandle(clientID, "ART_1",id1, vrep.simx_opmode_oneshot_wait)!=vrep.simx_return_ok){
				System.out.println("Motor 1 disconnected");
			}
			else{
				System.out.println("Motor 1 connected");
			}
			
			if(vrep.simxGetObjectHandle(clientID, "ART_2",id2, vrep.simx_opmode_oneshot_wait)!=vrep.simx_return_ok){
				System.out.println("Motor 2 disconnected");
			}
			else{
				System.out.println("Motor 2 connected");
			}
			
			if(vrep.simxGetObjectHandle(clientID, "ART_3",id3, vrep.simx_opmode_oneshot_wait)!=vrep.simx_return_ok){
				System.out.println("Motor 3 disconnected");
			}
			else{
				System.out.println("Motor 3 connected");
			}
			
			if(vrep.simxGetObjectHandle(clientID, "ART_4",id4, vrep.simx_opmode_oneshot_wait)!=vrep.simx_return_ok){
				System.out.println("Motor 4 disconnected");
			}
			else{
				System.out.println("Motor 4 connected");
			}
			
			if(vrep.simxGetObjectHandle(clientID, "ART_5",id5, vrep.simx_opmode_oneshot_wait)!=vrep.simx_return_ok){
				System.out.println("Motor 5 disconnected");
			}
			else{
				System.out.println("Motor 5 connected");
			}
			
			if(vrep.simxGetObjectHandle(clientID, "ART_6",id6, vrep.simx_opmode_oneshot_wait)!=vrep.simx_return_ok){
				System.out.println("Motor 6 disconnected");
			}
			else{
				System.out.println("Motor 6 connected");
			}
			
			if(vrep.simxGetObjectHandle(clientID, "ART_7",id7, vrep.simx_opmode_oneshot_wait)!=vrep.simx_return_ok){
				System.out.println("Motor 7 disconnected");
			}
			else{
				System.out.println("Motor 7 connected");
			}
			
			if(vrep.simxGetObjectHandle(clientID, "ART_8",id8, vrep.simx_opmode_oneshot_wait)!=vrep.simx_return_ok){
				System.out.println("Motor 8 disconnected");
			}
			else{
				System.out.println("Motor 8 connected");
			}
			
			if(vrep.simxGetObjectHandle(clientID, "ART_9",id9, vrep.simx_opmode_oneshot_wait)!=vrep.simx_return_ok){
				System.out.println("Motor 9 disconnected");
			}
			else{
				System.out.println("Motor 9 connected");
			}
			
			if(vrep.simxGetObjectHandle(clientID, "ART_10",id10, vrep.simx_opmode_oneshot_wait)!=vrep.simx_return_ok){
				System.out.println("Motor 10 disconnected");
			}
			else{
				System.out.println("Motor 10 connected");
			}
			
			if(vrep.simxGetObjectHandle(clientID, "ART_11",id11, vrep.simx_opmode_oneshot_wait)!=vrep.simx_return_ok){
				System.out.println("Motor 11 disconnected");
			}
			else{
				System.out.println("Motor 11 connected");
			}
			
			if(vrep.simxGetObjectHandle(clientID, "ART_12",id12, vrep.simx_opmode_oneshot_wait)!=vrep.simx_return_ok){
				System.out.println("Motor 12 disconnected");
			}
			else{
				System.out.println("Motor 12 connected");
			}
			
			if(vrep.simxGetObjectHandle(clientID, "ART_13",id13, vrep.simx_opmode_oneshot_wait)!=vrep.simx_return_ok){
				System.out.println("Motor 13 disconnected");
			}
			else{
				System.out.println("Motor 13 connected");
			}
			
			if(vrep.simxGetObjectHandle(clientID, "ART_14",id14, vrep.simx_opmode_oneshot_wait)!=vrep.simx_return_ok){
				System.out.println("Motor 14 disconnected");
			}
			else{
				System.out.println("Motor 14 connected");
			}
			
			if(vrep.simxGetObjectHandle(clientID, "ART_15",id15, vrep.simx_opmode_oneshot_wait)!=vrep.simx_return_ok){
				System.out.println("Motor 15 disconnected");
			}
			else{
				System.out.println("Motor 15 connected");
			}
			
			if(vrep.simxGetObjectHandle(clientID, "ART_16",id16, vrep.simx_opmode_oneshot_wait)!=vrep.simx_return_ok){
				System.out.println("Motor 16 disconnected");
			}
			else{
				System.out.println("Motor 16 connected");
			}
			
			if(vrep.simxGetObjectHandle(clientID, "ART_17",id17, vrep.simx_opmode_oneshot_wait)!=vrep.simx_return_ok){
				System.out.println("Motor 17 disconnected");
			}
			else{
				System.out.println("Motor 17 connected");
			}
			
			if(vrep.simxGetObjectHandle(clientID, "ART_18",id18, vrep.simx_opmode_oneshot_wait)!=vrep.simx_return_ok){
				System.out.println("Motor 18 disconnected");
			}
			else{
				System.out.println("Motor 18 connected");
			}	
			if(vrep.simxGetObjectHandle(clientID, "BIOLOID",robot, vrep.simx_opmode_oneshot_wait)!=vrep.simx_return_ok){
				System.out.println("robot disconnected");
			}
			else{
				System.out.println("robot connected");
			}	
			
			if(vrep.simxGetObjectHandle(clientID, "5mx5mWoodenFloor",floor, vrep.simx_opmode_oneshot_wait)!=vrep.simx_return_ok){
				System.out.println("Floor disconnected");
			}
			else{
				System.out.println("Floor connected");
			}
		}
		else{
			System.out.println("Failed connecting to remote API server - Try to run the simulator");
			System.exit(0);
		}
	}
		
	public void initialPos(int connectionTime, double degrees[]){
		cont = 0;
		while(vrep.simxGetConnectionId(clientID)!=-1 && cont!=connectionTime){
			vrep.simxSetJointTargetPosition(clientID,id1.getValue(),(float)(-2.62 + (336 * 0.00511711875)),vrep.simx_opmode_streaming);
			vrep.simxSetJointTargetPosition(clientID,id2.getValue(),(float)(-2.62 + (687 * 0.00511711875)),vrep.simx_opmode_streaming);
			vrep.simxSetJointTargetPosition(clientID,id3.getValue(),(float)(-2.62 + (298 * 0.00511711875)),vrep.simx_opmode_streaming);
			vrep.simxSetJointTargetPosition(clientID,id4.getValue(),(float)(-2.62 + (724 * 0.00511711875)),vrep.simx_opmode_streaming);
			vrep.simxSetJointTargetPosition(clientID,id5.getValue(),(float)(-2.62 + (412 * 0.00511711875)),vrep.simx_opmode_streaming);
			vrep.simxSetJointTargetPosition(clientID,id6.getValue(),(float)(-2.62 + (611 * 0.00511711875)),vrep.simx_opmode_streaming);
			vrep.simxSetJointTargetPosition(clientID,id7.getValue(),(float)(-2.62 + (355 * 0.00511711875)),vrep.simx_opmode_streaming);
			vrep.simxSetJointTargetPosition(clientID,id8.getValue(),(float)(-2.62 + (664 * 0.00511711875)),vrep.simx_opmode_streaming);
			vrep.simxSetJointTargetPosition(clientID,id9.getValue(),(float)(-2.62 + (491 * 0.00511711875)),vrep.simx_opmode_streaming);
			vrep.simxSetJointTargetPosition(clientID,id10.getValue(),(float)(-2.62 + (530 * 0.00511711875)),vrep.simx_opmode_streaming);		
			vrep.simxSetJointTargetPosition(clientID,id11.getValue(),(float)(-2.62 + (394 * 0.00511711875)),vrep.simx_opmode_streaming);
			vrep.simxSetJointTargetPosition(clientID,id12.getValue(),(float)(-2.62 + (625 * 0.00511711875)),vrep.simx_opmode_streaming);
			vrep.simxSetJointTargetPosition(clientID,id13.getValue(),(float)(-2.62 + (278 * 0.00511711875)),vrep.simx_opmode_streaming);
    		vrep.simxSetJointTargetPosition(clientID,id14.getValue(),(float)(-2.62 + (743 * 0.00511711875)),vrep.simx_opmode_streaming);
			vrep.simxSetJointTargetPosition(clientID,id15.getValue(),(float)(-2.62 + (616 * 0.00511711875)),vrep.simx_opmode_streaming);
			vrep.simxSetJointTargetPosition(clientID,id16.getValue(),(float)(-2.62 + (405 * 0.00511711875)),vrep.simx_opmode_streaming);
			vrep.simxSetJointTargetPosition(clientID,id17.getValue(),(float)(-2.62 + (490 * 0.00511711875)),vrep.simx_opmode_streaming);
			vrep.simxSetJointTargetPosition(clientID,id18.getValue(),(float)(-2.62 + (530 * 0.00511711875)),vrep.simx_opmode_streaming);
			cont++;
		}
		degrees[0]= 336;
		degrees[1]= 687;
		degrees[2]= 298;
		degrees[3]= 724;
		degrees[4]= 412;
		degrees[5]= 611;
		degrees[6]= 355;
		degrees[7]= 664;
		degrees[8]= 491;
		degrees[9]= 530;
		degrees[10]= 394;
		degrees[11]= 625;
		degrees[12]= 278;
		degrees[13]= 743;
		degrees[14]= 616;
		degrees[15]= 405;
		degrees[16]= 490;
		degrees[17]= 530;
	
	}
	
	public void clear(int connectionTime, double degrees[]){
		cont = 0;
		while(vrep.simxGetConnectionId(clientID)!=-1 && cont!=connectionTime){
			vrep.simxSetJointTargetPosition(clientID,id1.getValue(),0,vrep.simx_opmode_streaming);
			vrep.simxSetJointTargetPosition(clientID,id2.getValue(),0,vrep.simx_opmode_streaming);
			vrep.simxSetJointTargetPosition(clientID,id3.getValue(),0,vrep.simx_opmode_streaming);
			vrep.simxSetJointTargetPosition(clientID,id4.getValue(),0,vrep.simx_opmode_streaming);
			vrep.simxSetJointTargetPosition(clientID,id5.getValue(),0,vrep.simx_opmode_streaming);
			vrep.simxSetJointTargetPosition(clientID,id6.getValue(),0,vrep.simx_opmode_streaming);
			vrep.simxSetJointTargetPosition(clientID,id7.getValue(),(float)-0.78,vrep.simx_opmode_streaming);
			vrep.simxSetJointTargetPosition(clientID,id8.getValue(),(float) 0.78,vrep.simx_opmode_streaming);
			vrep.simxSetJointTargetPosition(clientID,id9.getValue(),0,vrep.simx_opmode_streaming);
			vrep.simxSetJointTargetPosition(clientID,id10.getValue(),0,vrep.simx_opmode_streaming);
			vrep.simxSetJointTargetPosition(clientID,id11.getValue(),0,vrep.simx_opmode_streaming);
			vrep.simxSetJointTargetPosition(clientID,id12.getValue(),0,vrep.simx_opmode_streaming);
			vrep.simxSetJointTargetPosition(clientID,id13.getValue(),0,vrep.simx_opmode_streaming);
			vrep.simxSetJointTargetPosition(clientID,id14.getValue(),0,vrep.simx_opmode_streaming);
			vrep.simxSetJointTargetPosition(clientID,id15.getValue(),0,vrep.simx_opmode_streaming);
			vrep.simxSetJointTargetPosition(clientID,id16.getValue(),0,vrep.simx_opmode_streaming);
			vrep.simxSetJointTargetPosition(clientID,id17.getValue(),0,vrep.simx_opmode_streaming);
			vrep.simxSetJointTargetPosition(clientID,id18.getValue(),0,vrep.simx_opmode_streaming);
			cont++;
		}
		
		for(i=0;i<18;i++){
			if(i==6)
				degrees[i]=361;
			else{
				if(i==7)
					degrees[i]=663;
				else
					degrees[i]=512;
			}
			
		}
	
	}
	
	public void setDegrees(int connectionTime, double degrees[]){
		cont = 0;
		
		
		while(vrep.simxGetConnectionId(clientID)!=-1 && cont!=connectionTime){
			vrep.simxSetJointTargetPosition(clientID,id1.getValue(),(float)(-2.62 + (degrees[0] * 0.00511711875)),vrep.simx_opmode_streaming);
			vrep.simxSetJointTargetPosition(clientID,id2.getValue(),(float)(-2.62 + (degrees[1] * 0.00511711875)),vrep.simx_opmode_streaming);
			vrep.simxSetJointTargetPosition(clientID,id3.getValue(),(float)(-2.62 + (degrees[2] * 0.00511711875)),vrep.simx_opmode_streaming);
			vrep.simxSetJointTargetPosition(clientID,id4.getValue(),(float)(-2.62 + (degrees[3] * 0.00511711875)),vrep.simx_opmode_streaming);
			vrep.simxSetJointTargetPosition(clientID,id5.getValue(),(float)(-2.62 + (degrees[4] * 0.00511711875)),vrep.simx_opmode_streaming);
			vrep.simxSetJointTargetPosition(clientID,id6.getValue(),(float)(-2.62 + (degrees[5] * 0.00511711875)),vrep.simx_opmode_streaming);
			vrep.simxSetJointTargetPosition(clientID,id7.getValue(),(float)(-2.62 + (degrees[6] * 0.00511711875)),vrep.simx_opmode_streaming);
			vrep.simxSetJointTargetPosition(clientID,id8.getValue(),(float)(-2.62 + (degrees[7] * 0.00511711875)),vrep.simx_opmode_streaming);
			vrep.simxSetJointTargetPosition(clientID,id9.getValue(),(float)(-2.62 + (degrees[8] * 0.00511711875)),vrep.simx_opmode_streaming);
			vrep.simxSetJointTargetPosition(clientID,id10.getValue(),(float)(-2.62 + (degrees[9] * 0.00511711875)),vrep.simx_opmode_streaming);
			vrep.simxSetJointTargetPosition(clientID,id11.getValue(),(float)(-2.62 + (degrees[10] * 0.00511711875)),vrep.simx_opmode_streaming);
			vrep.simxSetJointTargetPosition(clientID,id12.getValue(),(float)(-2.62 + (degrees[11] * 0.00511711875)),vrep.simx_opmode_streaming);
			vrep.simxSetJointTargetPosition(clientID,id13.getValue(),(float)(-2.62 + (degrees[12] * 0.00511711875)),vrep.simx_opmode_streaming);
      		vrep.simxSetJointTargetPosition(clientID,id14.getValue(),(float)(-2.62 + (degrees[13] * 0.00511711875)),vrep.simx_opmode_streaming);
			vrep.simxSetJointTargetPosition(clientID,id15.getValue(),(float)(-2.62 + (degrees[14] * 0.00511711875)),vrep.simx_opmode_streaming);
			vrep.simxSetJointTargetPosition(clientID,id16.getValue(),(float)(-2.62 + (degrees[15] * 0.00511711875)),vrep.simx_opmode_streaming);
			vrep.simxSetJointTargetPosition(clientID,id17.getValue(),(float)(-2.62 + (degrees[16] * 0.00511711875)),vrep.simx_opmode_streaming);
			vrep.simxSetJointTargetPosition(clientID,id18.getValue(),(float)(-2.62 + (degrees[17] * 0.00511711875)),vrep.simx_opmode_streaming);
			cont++;
		}
		
	}

	public void readMotor(int motor){
		cont = 0;
		while(cont<=3000){	
			vrep.simxGetJointPosition(clientID, id1.getValue(), position1, vrep.simx_opmode_streaming);
			vrep.simxGetJointPosition(clientID, id2.getValue(), position2, vrep.simx_opmode_streaming);
			vrep.simxGetJointPosition(clientID, id3.getValue(), position3, vrep.simx_opmode_streaming);
			vrep.simxGetJointPosition(clientID, id4.getValue(), position4, vrep.simx_opmode_streaming);
			vrep.simxGetJointPosition(clientID, id5.getValue(), position5, vrep.simx_opmode_streaming);
			vrep.simxGetJointPosition(clientID, id6.getValue(), position6, vrep.simx_opmode_streaming);
			vrep.simxGetJointPosition(clientID, id7.getValue(), position7, vrep.simx_opmode_streaming);
			vrep.simxGetJointPosition(clientID, id8.getValue(), position8, vrep.simx_opmode_streaming);
			vrep.simxGetJointPosition(clientID, id9.getValue(), position9, vrep.simx_opmode_streaming);
			vrep.simxGetJointPosition(clientID, id10.getValue(), position10, vrep.simx_opmode_streaming);
			vrep.simxGetJointPosition(clientID, id11.getValue(), position11, vrep.simx_opmode_streaming);
			vrep.simxGetJointPosition(clientID, id12.getValue(), position12, vrep.simx_opmode_streaming);
			vrep.simxGetJointPosition(clientID, id13.getValue(), position13, vrep.simx_opmode_streaming);
			vrep.simxGetJointPosition(clientID, id14.getValue(), position14, vrep.simx_opmode_streaming);
			vrep.simxGetJointPosition(clientID, id15.getValue(), position15, vrep.simx_opmode_streaming);
			vrep.simxGetJointPosition(clientID, id16.getValue(), position16, vrep.simx_opmode_streaming);
			vrep.simxGetJointPosition(clientID, id17.getValue(), position17, vrep.simx_opmode_streaming);
			vrep.simxGetJointPosition(clientID, id18.getValue(), position18, vrep.simx_opmode_streaming);
			cont++;
		}
		
		switch(motor){
			case 1:
				System.out.print("Motor 1: ");
				System.out.printf("%.4f\n", ((position1.getValue()+2.62)/0.00511711875));
				break;
			case 2:
				System.out.print("Motor 2: ");
				System.out.printf("%.4f\n", ((position2.getValue()+2.62)/0.00511711875));
				break;
			case 3:
				System.out.print("Motor 3: ");
				System.out.printf("%.4f\n", ((position3.getValue()+2.62)/0.00511711875));
				break;
			case 4:
				System.out.print("Motor 4: ");
				System.out.printf("%.4f\n", ((position4.getValue()+2.62)/0.00511711875));
				break;
			case 5:
				System.out.print("Motor 5: ");
				System.out.printf("%.4f\n", ((position5.getValue()+2.62)/0.00511711875));
				break;
			case 6:
				System.out.print("Motor 6: ");
				System.out.printf("%.4f\n", ((position6.getValue()+2.62)/0.00511711875));
				break;
			case 7:
				System.out.print("Motor 7: ");
				System.out.printf("%.4f\n", ((position7.getValue()+2.62)/0.00511711875));
				break;
			case 8:
				System.out.print("Motor 8: ");
				System.out.printf("%.4f\n", ((position8.getValue()+2.62)/0.00511711875));
				break;
			case 9:
				System.out.print("Motor 9: ");
				System.out.printf("%.4f\n", ((position9.getValue()+2.62)/0.00511711875));
				break;
			case 10:
				System.out.print("Motor 10: ");
				System.out.printf("%.4f\n", ((position10.getValue()+2.62)/0.00511711875));
				break;
			case 11:
				System.out.print("Motor 11: ");
				System.out.printf("%.4f\n", ((position11.getValue()+2.62)/0.00511711875));
				break;
			case 12:
				System.out.print("Motor 12: ");
				System.out.printf("%.4f\n", ((position12.getValue()+2.62)/0.00511711875));
				break;
			case 13:
				System.out.print("Motor 13: ");
				System.out.printf("%.4f\n", ((position13.getValue()+2.62)/0.00511711875));
				break;
			case 14:
				System.out.print("Motor 14: ");
				System.out.printf("%.4f\n", ((position14.getValue()+2.62)/0.00511711875));
				break;
			case 15:
				System.out.print("Motor 15: ");
				System.out.printf("%.4f\n", ((position15.getValue()+2.62)/0.00511711875));
				break;
			case 16:
				System.out.print("Motor 16: ");
				System.out.printf("%.4f\n", ((position16.getValue()+2.62)/0.00511711875));
				break;
			case 17:
				System.out.print("Motor 17: ");
				System.out.printf("%.4f\n", ((position17.getValue()+2.62)/0.00511711875));
				break;
			case 18:
				System.out.print("Motor 18: ");
				System.out.printf("%.4f\n", ((position18.getValue()+2.62)/0.00511711875));
				break;
		}
		
	}	
	
	public void readMotors(){
		cont = 0;
		while(cont<=3000){
			vrep.simxGetJointPosition(clientID, id1.getValue(), position1, vrep.simx_opmode_streaming);
			vrep.simxGetJointPosition(clientID, id2.getValue(), position2, vrep.simx_opmode_streaming);
			vrep.simxGetJointPosition(clientID, id3.getValue(), position3, vrep.simx_opmode_streaming);
			vrep.simxGetJointPosition(clientID, id4.getValue(), position4, vrep.simx_opmode_streaming);
			vrep.simxGetJointPosition(clientID, id5.getValue(), position5, vrep.simx_opmode_streaming);
			vrep.simxGetJointPosition(clientID, id6.getValue(), position6, vrep.simx_opmode_streaming);
			vrep.simxGetJointPosition(clientID, id7.getValue(), position7, vrep.simx_opmode_streaming);
			vrep.simxGetJointPosition(clientID, id8.getValue(), position8, vrep.simx_opmode_streaming);
			vrep.simxGetJointPosition(clientID, id9.getValue(), position9, vrep.simx_opmode_streaming);
			vrep.simxGetJointPosition(clientID, id10.getValue(), position10, vrep.simx_opmode_streaming);
			vrep.simxGetJointPosition(clientID, id11.getValue(), position11, vrep.simx_opmode_streaming);
			vrep.simxGetJointPosition(clientID, id12.getValue(), position12, vrep.simx_opmode_streaming);
			vrep.simxGetJointPosition(clientID, id13.getValue(), position13, vrep.simx_opmode_streaming);
			vrep.simxGetJointPosition(clientID, id14.getValue(), position14, vrep.simx_opmode_streaming);
			vrep.simxGetJointPosition(clientID, id15.getValue(), position15, vrep.simx_opmode_streaming);
			vrep.simxGetJointPosition(clientID, id16.getValue(), position16, vrep.simx_opmode_streaming);
			vrep.simxGetJointPosition(clientID, id17.getValue(), position17, vrep.simx_opmode_streaming);
			vrep.simxGetJointPosition(clientID, id18.getValue(), position18, vrep.simx_opmode_streaming);
		cont++;	
		}
		System.out.print("Motor 1: ");
		System.out.printf("%.1f\n", ((position1.getValue()+2.62)/0.00511711875));
		System.out.print("Motor 2: ");
		System.out.printf("%.1f\n", ((position2.getValue()+2.62)/0.00511711875));
		System.out.print("Motor 3: ");
		System.out.printf("%.1f\n", ((position3.getValue()+2.62)/0.00511711875));
		System.out.print("Motor 4: ");
		System.out.printf("%.1f\n", ((position4.getValue()+2.62)/0.00511711875));
		System.out.print("Motor 5: ");
		System.out.printf("%.1f\n", ((position5.getValue()+2.62)/0.00511711875));
		System.out.print("Motor 6: ");
		System.out.printf("%.1f\n", ((position6.getValue()+2.62)/0.00511711875));
		System.out.print("Motor 7: ");
		System.out.printf("%.1f\n", ((position7.getValue()+2.62)/0.00511711875));
		System.out.print("Motor 8: ");
		System.out.printf("%.1f\n", ((position8.getValue()+2.62)/0.00511711875));
		System.out.print("Motor 9: ");
		System.out.printf("%.1f\n", ((position9.getValue()+2.62)/0.00511711875));
		System.out.print("Motor 10: ");
		System.out.printf("%.1f\n", ((position10.getValue()+2.62)/0.00511711875));
		System.out.print("Motor 11: ");
		System.out.printf("%.1f\n", ((position11.getValue()+2.62)/0.00511711875));
		System.out.print("Motor 12: ");
		System.out.printf("%.1f\n", ((position12.getValue()+2.62)/0.00511711875));
		System.out.print("Motor 13: ");
		System.out.printf("%.1f\n", ((position13.getValue()+2.62)/0.00511711875));
		System.out.print("Motor 14: ");
		System.out.printf("%.1f\n", ((position14.getValue()+2.62)/0.00511711875));
		System.out.print("Motor 15: ");
		System.out.printf("%.1f\n", ((position15.getValue()+2.62)/0.00511711875));
		System.out.print("Motor 16: ");
		System.out.printf("%.1f\n", ((position16.getValue()+2.62)/0.00511711875));
		System.out.print("Motor 17: ");
		System.out.printf("%.1f\n", ((position17.getValue()+2.62)/0.00511711875));
		System.out.print("Motor 18: ");
		System.out.printf("%.1f\n", ((position18.getValue()+2.62)/0.00511711875));		
	}

	public void readGyroSensor(){ 
		long startTime=System.currentTimeMillis();
        
        // Initialize streaming of the desired data:
        int ret=vrep.simxCallScriptFunction(clientID,"GyroSensor",vrep.sim_scripttype_childscript,"getGyroData",null,null,null,null,null,gyroData,null,null,vrep.simx_opmode_streaming);
        while (System.currentTimeMillis()-startTime < 28)
        {
            ret=vrep.simxCallScriptFunction(clientID,"GyroSensor",vrep.sim_scripttype_childscript,"getGyroData",null,null,null,null,null,gyroData,null,null,vrep.simx_opmode_buffer);
            if (ret==vrep.simx_return_ok) // After initialization of streaming, it will take a few ms before the first value arrives, so check the return code
                System.out.format("Gyro data: %f, %f, %f\n",gyroData.getArray()[0],gyroData.getArray()[1],gyroData.getArray()[2]);
        }
        
        // Thanks coppelia adms: http://www.forum.coppeliarobotics.com/viewtopic.php?f=9&t=5850 
	}

    public void readCM(){
    	vrep.simxGetObjectPosition(clientID, robot.getValue(), floor.getValue(), CM, vrep.simx_opmode_streaming);
    	System.out.println("x: " + CM.getArray()[0]);
    	System.out.println("y: " + CM.getArray()[1]);
    	System.out.println("z: " + CM.getArray()[2]);
    }
}

