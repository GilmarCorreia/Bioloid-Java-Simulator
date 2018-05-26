RemoteApi V-REP para Java em Eclipse (Bioloid Robotis Premium Simulador)

Como configurar?

	- Instalar o eclipse em java e o V-REP educacional
	- Na pasta do V-REP adicionar o arquivo Bioloid.ttm em (C:\Program Files\V-REP3\V-REP_PRO_EDU\models\robots\mobile)
	- Na pasta do V-REP adicionar o arquivo RunBioloid_v2.ttt em (C:\Program Files\V-REP3\V-REP_PRO_EDU\scenes)
	- Em C:\Program Files\V-REP3\V-REP_PRO_EDU\programming\remoteApiBindings\java\java temos todas as classes para funcionar a comunica��o, elas j� est�o inclusas no projeto,
	por�m se atualizar o V-REP, elas n�o ser�o atualizadas. Portanto, se isso acontecer, no src do seu projeto, copie a pasta coppelia e cole dentro do projeto.
	- Abrir eclipse
	- Em File, clique em Open Projects From File System
	- Clique em Directory
	- Abra a pasta Simulador VREP - Blank
	- No eclipse, abra a pasta do projeto, src, default package, Run.java
	- Para o projeto rodar, voc� precisa alterar no pacote coppelia o arquivo remoteApi.java, nele encontrar� o seguinte c�digo:
	  -> System.loadLibrary("remoteApiJava");
	  Alterar para:	
	  -> System.load("C:/Program Files/V-REP3/V-REP_PRO_EDU/programming/remoteApiBindings/java/lib/64Bit/remoteApiJava.dll"); ou colocar o diret�rio que se encontra o arquivo
	obs: Lembre-se, se estiver usando o V-REP 64 bits, use a dll da pasta 64Bit, se estiver usando o V-REP 32 bits, use o da pasta 32Bit. 
	- obs: alguns arquivos de exemplos de programa��o s�o listados:
		- Simulador VREP - Locomo��o Cont�nua
		- Simulador VREP - Locomo��o Discreta
		- Simulador VREP - RNA's

Entendendo a biblioteca

Existem 2 classes principais uma Run.java e Bioloid.java

	A Run.java executa as opera��es matem�ticas e cria a conex�o Java V-REP
	A Bioloid.java � um objeto no qual entra os dados de todos objetos da cena e com isso se utiliza algumas fun��es para posicionar o �ngulo dos motores e l�-los, entre elas:
		
		1 - NomeDoObjeto.initialPos(int Velocidade, double[] PosicaoDosMotores)
			Retorna a posi��o inicial do Rob�
		2 - NomeDoObjeto.clear(int Velocidade, double[] PosicaoDosMotores)
			Retorna a movimenta��o dos servos cujos �ngulos est�o na sua metade (512�)
		3 - NomeDoObjeto.setDegrees(int Velocidade, double[] PosicaoDosMotores)
			Retorna a movimenta��o dos servos a um certo conjunto de angulos definidos
		4 - NomeDoObjeto.readMotor(int x)
			Retorna qual valor de �ngulo est� uma junta x 
		5 - NomeDoObjeto.readMotors()
			Retorna os valores dos �ngulos de todos os servos
		6 - NomeDoObjeto.readGyroSensor()
			Retorna os valores dos eixos x,y e z do sensor de girosc�pio
		7 - NomeDoObjeto.readCM()
			Retorna a posi��o do centro de massa do rob� em x, y e z.