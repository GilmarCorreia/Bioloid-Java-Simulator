import java.math.BigDecimal;

import coppelia.IntW;
import coppelia.remoteApi;

public class Run {
	
	public static void main(String[] args){
		
		
		//Getting access VREP's libraries ------------------------------------------------------------------
		remoteApi vrep = new remoteApi();
		//--------------------------------------------------------------------------------------------------
			
		//Creating a Java Connection with VREP -------------------------------------------------------------
		int clientID = vrep.simxStart("127.0.0.1",19999,true,true,5000,5);
		//--------------------------------------------------------------------------------------------------
		
		// Criando vetor de ângulos dos motores ------------------------------------------------------------
		double degrees[] = new double[18];
		double angpos[][] = new double[18][18];
		double x9,x10,x11,x12,x13,x14,x15,x16,x17,x18;
		//--------------------------------------------------------------------------------------------------
		
		
		// Setting ID of motors ----------------------------------------------------------------------------		
		IntW id1 = new IntW(1);	IntW id2 = new IntW(1);	IntW id3 = new IntW(1);	IntW id4 = new IntW(1);
		IntW id5 = new IntW(1);	IntW id6 = new IntW(1);	IntW id7 = new IntW(1);	IntW id8 = new IntW(1);
		IntW id9 = new IntW(1);	IntW id10 = new IntW(1);IntW id11 = new IntW(1);IntW id12 = new IntW(1);
		IntW id13 = new IntW(1);IntW id14 = new IntW(1);IntW id15 = new IntW(1);IntW id16 = new IntW(1);
		IntW id17 = new IntW(1);IntW id18 = new IntW(1);IntW robo = new IntW(1);IntW chao = new IntW(1);
		//--------------------------------------------------------------------------------------------------
				
		Bioloid func = new Bioloid(vrep,clientID,id1,id2,id3,id4,id5,id6,id7,id8,id9,id10,id11,id12,id13,id14,id15,id16,id17,id18,robo,chao);
		
		func.techCheck();
		func.clear(5000,degrees);
		func.initialPos(15000,degrees);

		degrees[0]= 333.5;
		degrees[1]= 690;
		degrees[2]= 297.8;
		degrees[3]= 724;
		degrees[4]= 412;
		degrees[5]= 611.2;
		degrees[6]= 355;
		degrees[7]= 664;
		
		int i;
		double k,t,j;
			boolean var = true;
			t=10;
			k = -2;
			j = 1/(t/10);
			
	//-----------------------------------------------------------------------------------------------------------------------------------------------------
			
	//---------------------------------------------------------------- POSIÇÃO 01 -------------------------------------------------------------------------
				
			for(i = 0; i<t; i++){
				degrees[9-1]= degrees[9-1] + 2*j*k;              //
				degrees[10-1]= degrees[10-1] + 2*j*k;            //
				degrees[17-1]= degrees[17-1] + 3*j*k;            // Swing of the center the mass, in the center
				degrees[18-1]= degrees[18-1] + 4*j*k;            // to a support phase in left feet.
				
				degrees[11-1]= degrees[11-1] - j*k;      //
				degrees[13-1]= degrees[13-1] - 2*j*k;    // Left Leg Movement.
				degrees[15-1]= degrees[15-1] + j*k;      //
				
				degrees[12-1]= degrees[12-1] - j*k;      //
				degrees[14-1]= degrees[14-1] - 2*j*k;    // Right Leg Movement.
				degrees[16-1]= degrees[16-1] + j*k;      //
				
				func.setDegrees(1500, degrees);
				System.out.println("Posição " + (((float)i)/10) + ": ");
				func.readMotors();
			}
			System.out.println("Posição 1: ");
			func.readMotors();
			
			for(i=0;i<18 && var;i++){
				angpos[0][i]=degrees[i];
			}
			
	//-----------------------------------------------------------------------------------------------------------------------------------------------------
			
	//---------------------------------------------------------------- POSIÇÃO 02 -------------------------------------------------------------------------
				
			for(i=0;i<t;i++){
				degrees[11-1]= degrees[11-1] + 7*j*k;
				degrees[15-1]= degrees[15-1] + 7*j*k;
				func.setDegrees(1500, degrees);
				System.out.println("Posição " + (1+((((float)i)/10))) + ": ");
				func.readMotors();
			}
			System.out.println("Posição 2: ");
			func.readMotors();
			
			for(i=0;i<18 && var;i++){
				angpos[1][i]=degrees[i];
			}
			
	//-----------------------------------------------------------------------------------------------------------------------------------------------------
			
	//---------------------------------------------------------------- POSIÇÃO 03 -------------------------------------------------------------------------
			while(true){
				for(i = 0; i<t; i++){
					degrees[11-1]= degrees[11-1] - 2.5*j*k;
					degrees[13-1]= degrees[13-1] - 5*j*k;
					degrees[15-1]= degrees[15-1] + 2.5*j*k;
					
					func.setDegrees(1500, degrees);
					System.out.println("Posição " + (2+((((float)i)/10))) + ": ");
					func.readMotors();
				}
				
				System.out.println("Posição 3: ");
				func.readMotors();
				
				for(i=0;i<18 && var;i++){
					angpos[2][i]=degrees[i];
				}

	//-----------------------------------------------------------------------------------------------------------------------------------------------------
				
	//---------------------------------------------------------------- POSIÇÃO 04 -------------------------------------------------------------------------
				for(i = 0; i<t; i++){
					degrees[11-1]= degrees[11-1] - 3.5*j*k;
					degrees[15-1]= degrees[15-1] - 3.5*j*k;
					
					func.setDegrees(1500, degrees);
					System.out.println("Posição " + (3+((((float)i)/10))) + ": ");
					func.readMotors();
				}
				System.out.println("Posição 4: ");
				func.readMotors();
				
				for(i=0;i<18 && var;i++){
					angpos[3][i]=degrees[i];
				}

	//-----------------------------------------------------------------------------------------------------------------------------------------------------
				
	//---------------------------------------------------------------- POSIÇÃO 05 -------------------------------------------------------------------------
					
				for(i=0;i<t;i++){
					degrees[9-1]= degrees[9-1] - 2*j*k;                //
					degrees[10-1]= degrees[10-1] - 2*j*k;              //
					degrees[17-1]= degrees[17-1] - 3*j*k;  // Swing of the center the mass, in the center
					degrees[18-1]= degrees[18-1] - 4*j*k;            // to a support phase in left feet.
					
					func.setDegrees(1500, degrees);
					System.out.println("Posição " + (4+((((float)i)/10))) + ": ");
					func.readMotors();
				}
				System.out.println("Posição 5: ");
				func.readMotors();
				
				for(i=0;i<18 && var;i++){
					angpos[4][i]=degrees[i];
				}
				
	//-----------------------------------------------------------------------------------------------------------------------------------------------------
				
	//---------------------------------------------------------------- POSIÇÃO 06 -------------------------------------------------------------------------
					
				for(i=0;i<t;i++){
					
					degrees[12-1]= degrees[12-1] + 3*j*k;  //
					degrees[14-1]= degrees[14-1] + 5*j*k;    // Right Leg Movement.
		
					degrees[15-1]= degrees[15-1] - 2.5*j*k;
					
					func.setDegrees(1500, degrees);
					System.out.println("Posição " + (5+((((float)i)/10))) + ": ");
					func.readMotors();
				}
				System.out.println("Posição 6: ");
				func.readMotors();
				
				for(i=0;i<18 && var;i++){
					angpos[5][i]=degrees[i];
				}
				
	//-----------------------------------------------------------------------------------------------------------------------------------------------------

	//---------------------------------------------------------------- POSIÇÃO 07 -------------------------------------------------------------------------
					
				
				for(i = 0; i<t; i++){
				
					degrees[9-1]= degrees[9-1] - 2.5*j*k;                //
					degrees[10-1]= degrees[10-1] - 2.5*j*k;              //
					degrees[17-1]= degrees[17-1] - 3.75*j*k;  // Swing of the center the mass, in the center
					degrees[18-1]= degrees[18-1] - 2.5*j*k;            // to a support phase in left feet.
					
					func.setDegrees(1500, degrees);
					System.out.println("Posição " + (6+((((float)i)/10))) + ": ");
					func.readMotors();
				}
				System.out.println("Posição 7: ");
				func.readMotors();
				
				for(i=0;i<18 && var;i++){
					angpos[6][i]=degrees[i];
				}
	//-----------------------------------------------------------------------------------------------------------------------------------------------------
				
	//---------------------------------------------------------------- POSIÇÃO 08 -------------------------------------------------------------------------
					
				
				x9= ((degrees[9-1] - (Math.abs(1024-angpos[2-1][10-1])))/t);
				x11= ((degrees[11-1] - (Math.abs(1024-angpos[2-1][12-1])))/t);
				x13= ((degrees[13-1] - (Math.abs(1024-angpos[2-1][14-1])))/t);
				x15= ((degrees[15-1] - (Math.abs(1024-angpos[2-1][16-1])))/t);
				x17= ((degrees[17-1] - (Math.abs(1024-angpos[2-1][18-1])))/t);
				
				x10= ((degrees[10-1] - (Math.abs(1024-angpos[2-1][9-1])))/t);
				x12= ((degrees[12-1] - (Math.abs(1024-angpos[2-1][11-1])))/t);
				x14= ((degrees[14-1] - (Math.abs(1024-angpos[2-1][13-1])))/t);
				x16= ((degrees[16-1] - (Math.abs(1024-angpos[2-1][15-1])))/t);
				x18= ((degrees[18-1] - (Math.abs(1024-angpos[2-1][17-1])))/t);
				
				for(i = 0; i<t; i++){
					degrees[9-1] -= x9;
					degrees[11-1]-= x11;     // 1024 -645     
					degrees[13-1]-= x13;     // 1024 - 788      
					degrees[15-1]-= x15;     // (degrees[15-1 anterior] - (1024 - 385)) / tempo 
					degrees[17-1]-= x17;
					
					degrees[10-1]-= x10;
					degrees[12-1]-= x12;  // 1024 - 276
					degrees[14-1]-= x14;
				    degrees[16-1]-= x16;  // 1024 - 456
				    degrees[18-1]-= x18; 
				    
					func.setDegrees(1500, degrees);
					System.out.println("Posição " + (7+((((float)i)/10))) + ": ");
					func.readMotors();
				}
				System.out.println("Posição 8: ");
				func.readMotors();
				
				for(i=0;i<18 && var;i++){
					angpos[7][i]=degrees[i];
				}
	//-----------------------------------------------------------------------------------------------------------------------------------------------------
				
	//---------------------------------------------------------------- POSIÇÃO 09 -------------------------------------------------------------------------
					
				x9= ((degrees[9-1] - (Math.abs(1024-angpos[3-1][10-1])))/t);
				x11= ((degrees[11-1] - (Math.abs(1024-angpos[3-1][12-1])))/t);
				x13= ((degrees[13-1] - (Math.abs(1024-angpos[3-1][14-1])))/t);
				x15= ((degrees[15-1] - (Math.abs(1024-angpos[3-1][16-1])))/t);
				x17= ((degrees[17-1] - (Math.abs(1024-angpos[3-1][18-1])))/t);
			
				x10= ((degrees[10-1] - (Math.abs(1024-angpos[3-1][9-1])))/t);
				x12= ((degrees[12-1] - (Math.abs(1024-angpos[3-1][11-1])))/t);
				x14= ((degrees[14-1] - (Math.abs(1024-angpos[3-1][13-1])))/t);
				x16= ((degrees[16-1] - (Math.abs(1024-angpos[3-1][15-1])))/t);
				x18= ((degrees[18-1] - (Math.abs(1024-angpos[3-1][17-1])))/t);
				
				for(i = 0; i<t; i++){			
					degrees[9-1] += x9;
					degrees[11-1]-= x11;          
					degrees[13-1]-= x13;           
					degrees[15-1]-= x15;     
					degrees[17-1]-= x17;
					
					degrees[10-1]+= x10;
					degrees[12-1]-= x12;  
					degrees[14-1]-= x14;
				    degrees[16-1]-= x16;  
				    degrees[18-1]-= x18; 
					
					func.setDegrees(1500, degrees);
					System.out.println("Posição " + (8+((((float)i)/10))) + ": ");
					func.readMotors();
				}
				System.out.println("Posição 9: ");
				func.readMotors();
				
				for(i=0;i<18 && var;i++){
					angpos[8][i]=degrees[i];
				}
	//-----------------------------------------------------------------------------------------------------------------------------------------------------
				
	//---------------------------------------------------------------- POSIÇÃO 10 -------------------------------------------------------------------------
				x9= ((degrees[9-1] - (Math.abs(1024-angpos[4-1][10-1])))/t);
				x11= ((degrees[11-1] - (Math.abs(1024-angpos[4-1][12-1])))/t);
				x13= ((degrees[13-1] - (Math.abs(1024-angpos[4-1][14-1])))/t);
				x15= ((degrees[15-1] - (Math.abs(1024-angpos[4-1][16-1])))/t);
				x17= ((degrees[17-1] - (Math.abs(1024-angpos[4-1][18-1])))/t);
				
				x10= ((degrees[10-1] - (Math.abs(1024-angpos[4-1][9-1])))/t);
				x12= ((degrees[12-1] - (Math.abs(1024-angpos[4-1][11-1])))/t);
				x14= ((degrees[14-1] - (Math.abs(1024-angpos[4-1][13-1])))/t);
				x16= ((degrees[16-1] - (Math.abs(1024-angpos[4-1][15-1])))/t);
				x18= ((degrees[18-1] - (Math.abs(1024-angpos[4-1][17-1])))/t);
				
				for(i = 0; i<t; i++){
					degrees[9-1] -= x9;
					degrees[11-1]-= x11;        
					degrees[13-1]-= x13;          
					degrees[15-1]-= x15;     
					degrees[17-1]+= x17;
					
					degrees[10-1]-= x10;
					degrees[12-1]-= x12;  
					degrees[14-1]-= x14;
				    degrees[16-1]-= x16;  
				    degrees[18-1]-= x18; 
					
					func.setDegrees(1500, degrees);
					System.out.println("Posição " + (9+((((float)i)/10))) + ": ");
					func.readMotors();
				}
				System.out.println("Posição 10: ");
				func.readMotors();
				
				for(i=0;i<18 && var;i++){
					angpos[9][i]=degrees[i];
				}
				
	//-----------------------------------------------------------------------------------------------------------------------------------------------------
				
	//---------------------------------------------------------------- POSIÇÃO 11 -------------------------------------------------------------------------
					
				x9= ((degrees[9-1] - (Math.abs(1024-angpos[5-1][10-1])))/t);
				x11= ((degrees[11-1] - (Math.abs(1024-angpos[5-1][12-1])))/t);
				x13= ((degrees[13-1] - (Math.abs(1024-angpos[5-1][14-1])))/t);
				x15= ((degrees[15-1] - (Math.abs(1024-angpos[5-1][16-1])))/t);
				x17= ((degrees[17-1] - (Math.abs(1024-angpos[5-1][18-1])))/t);
				
				x10= ((degrees[10-1] - (Math.abs(1024-angpos[5-1][9-1])))/t);
				x12= ((degrees[12-1] - (Math.abs(1024-angpos[5-1][11-1])))/t);
				x14= ((degrees[14-1] - (Math.abs(1024-angpos[5-1][13-1])))/t);
				x16= ((degrees[16-1] - (Math.abs(1024-angpos[5-1][15-1])))/t);
				x18= ((degrees[18-1] - (Math.abs(1024-angpos[5-1][17-1])))/t);
				
				for(i=0;i<t;i++){	
					degrees[9-1] -= x9;
					degrees[11-1]-= x11;        
					degrees[13-1]-= x13;        
					degrees[15-1]-= x15;    
					degrees[17-1]-= x17;
					
					degrees[10-1]-= x10;
					degrees[12-1]-= x12;  
					degrees[14-1]-= x14;
				    degrees[16-1]-= x16; 
				    degrees[18-1]-= x18; 
					
					func.setDegrees(1500, degrees);
					System.out.println("Posição " + (10+((((float)i)/10))) + ": ");
					func.readMotors();
				}
				System.out.println("Posição 11: ");
				func.readMotors();
				
				for(i=0;i<18 && var;i++){
					angpos[10][i]=degrees[i];
				}
	//-----------------------------------------------------------------------------------------------------------------------------------------------------
			
	//---------------------------------------------------------------- POSIÇÃO 12 -------------------------------------------------------------------------
					
				x9= ((degrees[9-1] - (Math.abs(1024-angpos[6-1][10-1])))/t);
				x11= ((degrees[11-1] - (Math.abs(1024-angpos[6-1][12-1])))/t);
				x13= ((degrees[13-1] - (Math.abs(1024-angpos[6-1][14-1])))/t);
				x15= ((degrees[15-1] - (Math.abs(1024-angpos[6-1][16-1])))/t);
				x17= ((degrees[17-1] - (Math.abs(1024-angpos[6-1][18-1])))/t);
				
				x10= ((degrees[10-1] - (Math.abs(1024-angpos[6-1][9-1])))/t);
				x12= ((degrees[12-1] - (Math.abs(1024-angpos[6-1][11-1])))/t);
				x14= ((degrees[14-1] - (Math.abs(1024-angpos[6-1][13-1])))/t);
				x16= ((degrees[16-1] - (Math.abs(1024-angpos[6-1][15-1])))/t);
				x18= ((degrees[18-1] - (Math.abs(1024-angpos[6-1][17-1])))/t);
				
				for(i=0;i<t;i++){	
					degrees[9-1] += x9;
					degrees[11-1]-= x11;   // constante add 0.3  
					degrees[13-1]-= x13;         
					degrees[15-1]-= x15;     
					degrees[17-1]-= 0.9*x17;
					
					degrees[10-1]+= x10;
					degrees[12-1]-= x12;  
					degrees[14-1]-= x14;
				    degrees[16-1]-= x16;  
				    degrees[18-1]-= 0.9*x18; 
					
					func.setDegrees(1500, degrees);
					System.out.println("Posição " + (11+((((float)i)/10))) + ": ");
					func.readMotors();
				}
				System.out.println("Posição 12: ");
				func.readMotors();
				
				for(i=0;i<18 && var;i++){
					angpos[11][i]=degrees[i];
				}
	//-----------------------------------------------------------------------------------------------------------------------------------------------------
				
	//---------------------------------------------------------------- POSIÇÃO 13 -------------------------------------------------------------------------
								
				x9= ((degrees[9-1] - (Math.abs(1024-angpos[7-1][10-1])))/t);
				x11= ((degrees[11-1] - (Math.abs(1024-angpos[7-1][12-1])))/t);
				x13= ((degrees[13-1] - (Math.abs(1024-angpos[7-1][14-1])))/t);
				x15= ((degrees[15-1] - (Math.abs(1024-angpos[7-1][16-1])))/t);
				x17= ((degrees[17-1] - (Math.abs(1024-angpos[7-1][18-1])))/t);
				
				x10= ((degrees[10-1] - (Math.abs(1024-angpos[7-1][9-1])))/t);
				x12= ((degrees[12-1] - (Math.abs(1024-angpos[7-1][11-1])))/t);
				x14= ((degrees[14-1] - (Math.abs(1024-angpos[7-1][13-1])))/t);
				x16= ((degrees[16-1] - (Math.abs(1024-angpos[7-1][15-1])))/t);
				x18= ((degrees[18-1] - (Math.abs(1024-angpos[7-1][17-1])))/t);
				
				for(i=0;i<t;i++){	
					degrees[9-1] -= x9;
					degrees[11-1]-= x11;      
					degrees[13-1]-= x13;         
					degrees[15-1]+= x15;     
					degrees[17-1]-= 0.9*x17; // constant add 0.9
					
					degrees[10-1]-= x10;
					degrees[12-1]-= x12;  
					degrees[14-1]-= x14;
				    degrees[16-1]-= x16;  
				    degrees[18-1]-= 0.9*x18; // constant add 0.9
					
					func.setDegrees(1500, degrees);
					System.out.println("Posição " + (12+((((float)i)/10))) + ": ");
					func.readMotors();
				}
				System.out.println("Posição 13: ");
				func.readMotors();
				
				for(i=0;i<18 && var;i++){
					angpos[12][i]=degrees[i];
				}
	//-----------------------------------------------------------------------------------------------------------------------------------------------------
				
	//---------------------------------------------------------------- POSIÇÃO 14 -------------------------------------------------------------------------
				
				x9= ((degrees[9-1]-(angpos[2-1][9-1]))/t);
				x11= ((degrees[11-1]-(angpos[2-1][11-1]))/t);
				x13= ((degrees[13-1]-(angpos[2-1][13-1]))/t);
				x15= ((degrees[15-1]-(angpos[2-1][15-1]))/t);
				x17= ((degrees[17-1]-(angpos[2-1][17-1]))/t);
			
				x10= ((degrees[10-1]-(angpos[2-1][10-1]))/t);
				x12= ((degrees[12-1]-(angpos[2-1][12-1]))/t);
				x14= ((degrees[14-1]-(angpos[2-1][14-1]))/t);
				x16= ((degrees[16-1]-(angpos[2-1][16-1]))/t);
				x18= ((degrees[18-1]-(angpos[2-1][18-1]))/t);
				
				for(i = 0; i<t; i++){			
					degrees[9-1] -= x9;
					degrees[11-1]-= x11;          
					degrees[13-1]-= x13;           
					degrees[15-1]-= x15;     
					degrees[17-1]-= x17;
					
					degrees[10-1]-= x10;
					degrees[12-1]-= x12;  
					degrees[14-1]-= x14;
				    degrees[16-1]-= x16;  
				    degrees[18-1]-= x18; 
					
					func.setDegrees(1500, degrees);
					System.out.println("Posição " + (13+((((float)i)/10))) + ": ");
					func.readMotors();
				}
				System.out.println("Posição 14: ");
				func.readMotors();
				
				for(i=0;i<18 && var;i++){
					angpos[13][i]=degrees[i];
				}
				
				var = false;
			}	
	}
}

/*
Developed By:
Gilmar Correia Jeronimo - e-mail: gilmar.correia@aluno.ufabc.edu.br
Federal University of ABC - Brazil 
2016
*/
