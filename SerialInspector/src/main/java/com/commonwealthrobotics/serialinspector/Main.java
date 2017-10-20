package com.commonwealthrobotics.serialinspector;

import java.io.DataInputStream;
import java.io.IOException;

import gnu.io.NRSerialPort;

public class Main {
	private static long start = 0;
	private static boolean started=false;
	private static void dump(DataInputStream in, String tag) throws IOException, InterruptedException{
		if(in.available()>1){
			if(!started){
				started=true;
				start = System.currentTimeMillis();
			}
			System.out.print("\r\n"+(System.currentTimeMillis()-start)+tag);
			while(in.available()>0){
				int val =in.read();
				if(val<127 && val>31){
					char b = (char)val;
					System.out.print(b);
				}else if(val ==13){
					System.out.print("\r\n");
					break;
				}else{
					System.out.print(" 0x"+Integer.toHexString(val)+" ");
				}
			}
		}
	}
	public static void main(String[] args) throws Exception {
		int baudRate = Integer.parseInt(args[0]);
		NRSerialPort serial1 = new NRSerialPort(args[1], baudRate);
		serial1.connect();
		NRSerialPort serial2 = new NRSerialPort(args[2], baudRate);
		serial2.connect();
		DataInputStream in1 = new DataInputStream(serial1.getInputStream());
		DataInputStream in2 = new DataInputStream(serial2.getInputStream());

		Runtime.getRuntime().addShutdownHook(new Thread() {
			public void run() {
				serial1.disconnect();
				serial2.disconnect();
				System.out.println("Clean shutdown");
			}
		});
		
		
		while(true){
			Thread.sleep(1);
			dump(in1,args[1]+"Tx>>");
			dump(in2,args[2]+"Rx\t\t\t\t<<");
		}

	}
}
