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
		
		//Creating vector of angles of the motors ----------------------------------------------------------
		double degrees[] = new double[18];
		//--------------------------------------------------------------------------------------------------
		
		
		// Setting ID of motors ----------------------------------------------------------------------------		
		IntW id1 = new IntW(1);	IntW id2 = new IntW(1);	IntW id3 = new IntW(1);	IntW id4 = new IntW(1);
		IntW id5 = new IntW(1);	IntW id6 = new IntW(1);	IntW id7 = new IntW(1);	IntW id8 = new IntW(1);
		IntW id9 = new IntW(1);	IntW id10 = new IntW(1);IntW id11 = new IntW(1);IntW id12 = new IntW(1);
		IntW id13 = new IntW(1);IntW id14 = new IntW(1);IntW id15 = new IntW(1);IntW id16 = new IntW(1);
		IntW id17 = new IntW(1);IntW id18 = new IntW(1);IntW robo = new IntW(1);IntW chao = new IntW(1);
		//--------------------------------------------------------------------------------------------------
		
		
		BIOLOID func = new BIOLOID(vrep,clientID,id1,id2,id3,id4,id5,id6,id7,id8,id9,id10,id11,id12,id13,id14,id15,id16,id17,id18,robo,chao);
		func.techCheck();
		
		// COMEÇAR A ESCREVER SEU CÓDIGO AQUI --------------------------------------------------------------
		
			// Funções do simulador (Todas as operações realizadas em cima dos motores são funções da Class Bioloid
				// - NomeDoObjeto.initialPos(int Velocidade, double[] PosicaoDosMotores) --> Retorna a posição inicial do Robô
				// - NomeDoObjeto.clear(int Velocidade, double[] PosicaoDosMotores) --> Retorna a movimentação dos servos cujos ângulos estão na sua metade (512°)
				// - NomeDoObjeto.setDegrees(int Velocidade, double[] PosicaoDosMotores) --> Retorna a movimentação dos servos a um certo conjunto de angulos definidos
				// - NomeDoObjeto.readMotor(int x) --> Retorna qual valor de ângulo está uma junta x 
				// - NomeDoObjeto.readMotors() --> Retorna os valores dos ângulos de todos os servos
				// - NomeDoObjeto.readGyroSensor() --> Retorna os valores dos eixos x,y e z do sensor de giroscópio
				// - NomeDoObjeto.readCM() --> Retorna a posição do centro de massa do robô em x, y e z.
			// ----------------------------------------------------------------------------------------------

		// --------------------------------------------------------------------------------------------------
		
		
	}
}

/*
 Developed By:
 Gilmar Correia Jeronimo - e-mail: gilmar.correia@aluno.ufabc.edu.br
 Federal University of ABC - Brazil 
 2016
*/
