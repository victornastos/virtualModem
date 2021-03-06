
import java.util.ArrayList;
import java.io.*;
import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.List;
import java.util.*;

public class virtualModem {
 public static void main(String[] param) {
 (new virtualModem()).demo();
 }
 public void demo() {
 int k;
 String message;
 Modem modem;
 ArrayList<Long> myTime = new ArrayList<Long>();
 ArrayList<Byte> myBytes = new ArrayList<Byte>();
 ArrayList<Byte> myBytesError = new ArrayList<Byte>();
 ArrayList<String> gps = new ArrayList<String>();
 ArrayList<String> gpsElem = new ArrayList<String>();
 ArrayList<String> width = new ArrayList<String>();
 ArrayList<String> length = new ArrayList<String>();
 ArrayList<Byte> myBytesGps = new ArrayList<Byte>();
 ArrayList<Long> myXor= new ArrayList<Long>();
 ArrayList<Integer> timesResend= new ArrayList<Integer>();
 modem=new Modem();
 modem.setSpeed(70000);
 modem.setTimeout(2000);
 modem.open("ithaki");
 message = "";
 for (;;) {
 try {
 k=modem.read();

 if (k==-1) break;
 System.out.print((char)k);

 message = message + (char)k;
 if(message.indexOf("\r\n\n\n")>-1){
 break;
 }
 }

 catch (Exception x) {
 break;
 }
}

final long NANOSEC_PER_SEC = 1000l*1000*1000;

long startTime = System.nanoTime();

k=0;
//loop gia parapanw apo 4 lepta
while ((System.nanoTime()-startTime)< 6*60*NANOSEC_PER_SEC){/* 60*/
 message="";
  //Echo request code

 modem.write("E1892\r".getBytes());

  long time = System.currentTimeMillis();
  for(;;){
  k=modem.read();
  if(k==-1){
    break;
  }
  message = message + (char)k;
  System.out.print((char)k);
  if(message.indexOf("PSTOP")>-1){
  break;
  }
  }

  long endTime = System.currentTimeMillis();
  long difference = (endTime- time);
  message = "";
  myTime.add(difference);

  System.out.print("\nTime difference: " + (difference));
  System.out.println("");
 }

  System.out.println(myTime);
//dimiourgia file.txt
  File file = new File("MyFile.txt");
try {
if (!file.exists()) {
	     file.createNewFile();
	  }

  }
  catch (IOException ioe) {
	   ioe.printStackTrace();
   }
  //metafora twn stoixeiwn ths listas sto txt
   BufferedWriter bw = null;
   FileWriter fw = null;

 try{

      fw = new FileWriter("MyFile.txt");
      bw = new BufferedWriter(fw);

      int t=myTime.size();

      for(int i=0; i<t; i++){
      bw.write(Long.toString(myTime.get(i))+"\n");
      }
    bw.close();
    }
    catch (IOException ioe) {
	   ioe.printStackTrace();
	  }

  ////////////////////Image request code///////////////////////////
  //System.out.print((char)k+ "NAS\n");
 modem.setSpeed(500000);
 modem.write("M1394\r".getBytes());

  //ektupwsh twn bytes apo to image request code
  for(;;){
  k=modem.read();
  if(k==-1)
  break;

    myBytes.add((byte)k);
  }
//////////imageNoError/////////////

try (FileOutputStream imageNoError = new FileOutputStream("imageNoError.jpg")){

    int m=myBytes.size();

    for(int i=0; i<m; i++){
    imageNoError.write(myBytes.get(i));
    }
imageNoError.close();
}
catch (IOException e) {
      e.printStackTrace();
    }

    modem.write("G8234\r".getBytes());
    //ektupwsh twn bytes apo to image request code
    for(;;){
    k=modem.read();
    if(k==-1)
    break;

      myBytesError.add((byte)k);
      //System.out.println("" + "VICCCCC");
      //System.out.print((int)k);
      //System.out.print((char)k);
    }
/////////////ImageError////////////////
  try (FileOutputStream imageError = new FileOutputStream("imageError.jpg")){

    //FileOutputStream imageError = new FileOutputStream("imageError.jpg");
        int m=myBytesError.size();

        for(int i=0; i<m; i++){
        imageError.write(myBytesError.get(i));
    }
    imageError.close();
    }
    catch (IOException e) {
    			e.printStackTrace();
    		}

///////////gps/////////////
 modem.setSpeed(70000);
message="";
modem.write("P6559R=1002999\r".getBytes());
for(;;){
k=modem.read();
if(k==-1){
  break;
}
message = message + (char)k;
System.out.print((char)k);

if(message.indexOf("START ITHAKI GPS TRACKING\r\n")>-1){
message = "";
}
if(message.indexOf("0000*")>-1)
    {
      k=modem.read();
      System.out.print((char)k);
      message=message+(char)k;
      k=modem.read();
      System.out.print((char)k);
      message=message+(char)k;
      gps.add(message);
      message="";
    }
}
System.out.print(gps);
for(int i=0; i<gps.size(); i=i+10)
{
  message=gps.get(i);
  gpsElem.add(message);
  message="";
}
System.out.print("\n" +gpsElem);
//////////////////////WIDTH//////////////////////
for(int i=0; i<1; i++)
{
  message=gpsElem.get(i).substring(18,22);
  String messageNew=gpsElem.get(i).substring(23,27);
  int widthEx= Integer.parseInt(messageNew);
  int messageExtra=(int)(widthEx*(0.006));
  String widthString=Integer.toString(messageExtra);
  String resultWidth=message+widthString;
  width.add(resultWidth);
  message="";
}
for(int i=1; i<gpsElem.size(); i++)
{
  message=gpsElem.get(i).substring(20,24);
  String messageNew=gpsElem.get(i).substring(25,29);
  int widthEx= Integer.parseInt(messageNew);
  int messageExtra=(int)(widthEx*(0.006));
  String widthString=Integer.toString(messageExtra);
  String resultWidth=message+widthString;
  width.add(resultWidth);
  message="";
}
//////////////////LENGTHHHHHHHHH////////////////////////
for(int i=0; i<1; i++)
{
  message=gpsElem.get(i).substring(31,35);
  String messageNew=gpsElem.get(i).substring(36,40);
  int lengthEx= Integer.parseInt(messageNew);
  int messageExtra=(int)(lengthEx*(0.006));
  String lengthString=Integer.toString(messageExtra);
  String resultLength=message+lengthString;
  length.add(resultLength);
  message="";
}
for(int i=1; i<gpsElem.size(); i++)
{
  message=gpsElem.get(i).substring(33,37);
  String messageNew=gpsElem.get(i).substring(38,42);
  int widthEx= Integer.parseInt(messageNew);
  int messageExtra=(int)(widthEx*(0.006));
  String lengthString=Integer.toString(messageExtra);
  String resultLength=message+lengthString;
  //System.out.print(gps.get(i).substring(10,20)+);
  length.add(resultLength);
  message="";
}
System.out.print("\nWIDTH\n" +width );
System.out.print("\nLENGTH\n" +length );

modem.write(("P6559T="+ length.get(0)+width.get(0)+ "T=" + length.get(1)+width.get(1) + "T=" + length.get(2)+width.get(2)+"T=" + length.get(3)+width.get(3)+ "T="+ length.get(4)+width.get(4)+"\r").getBytes());
for(;;){
k=modem.read();
if(k==-1)
break;
myBytesGps.add((byte)k);
}
try (FileOutputStream imageGps = new FileOutputStream("imageGps.jpg")){

      int m=myBytesGps.size();

      for(int i=0; i<m; i++){
      imageGps.write(myBytesGps.get(i));
}
  imageGps.close();
  }
  catch (IOException e) {
        e.printStackTrace();
      }

  int resultXor=0;
  int fcs=0;
  int counter=0;
  long startTimeXor=0;
  long endTimeXor=0;
  long differenceXor=0;
  int resend=0;
  int times=0;

       final long NANOSEC_PER_SECX = 1000l*1000*1000;

       long startTimeX = System.nanoTime();

       k=0;

  while ((System.nanoTime()-startTimeX)< 6*60*NANOSEC_PER_SECX){

    message="";

    if(counter==0 || fcs==resultXor){
       modem.write("Q3002\r".getBytes());
       System.out.print("\nACK\n");
       timesResend.add(resend);

       resend=0;
     }
    else if(fcs!=resultXor){
      modem.write("R7598\r".getBytes());
      System.out.print("\nNACK!!!!!!!!!!!!!!!\n");
      resend++;
      System.out.print(resend);
    }

     startTimeXor = System.currentTimeMillis();
     for(;;){
     k=modem.read();
     if(k==-1){
       break;
     }
     message = message + (char)k;
     System.out.print((char)k);

     if(message.indexOf("PSTOP")>-1){
       resultXor=0;
       endTimeXor = System.currentTimeMillis();
       for(int i=31; i<47; i++)
       {
        resultXor=resultXor^(message.charAt(i));
       }
      if(counter==0 || fcs==resultXor){
        differenceXor=(endTimeXor-startTimeXor);
        myXor.add(differenceXor);
      }
       System.out.print("\nRESULTXOR: " +resultXor);
        fcs=Integer.parseInt(message.substring(49,52));
        System.out.print("\nFCS: " + fcs+ "\n");
        counter++;

     break;
     }

     }
   }
   System.out.print(myXor);

   File fileX = new File("MyXor.txt");
 try {
 if (!fileX.exists()) {
       fileX.createNewFile();
    }

   }
   catch (IOException ioe) {
     ioe.printStackTrace();
    }
   //metafora twn stoixeiwn ths listas sto txt
    BufferedWriter bwXor = null;
    FileWriter fwXor = null;

  try{

       fwXor = new FileWriter("MyXor.txt");
       bwXor = new BufferedWriter(fwXor);

       int tXor=myXor.size();

       for(int i=0; i<tXor; i++){
       bwXor.write(Long.toString(myXor.get(i))+"\n");
       }
     bwXor.close();
     }
     catch (IOException ioe) {
     ioe.printStackTrace();
    }

/////////////FILE MYTIMES/////////////
    File fileT = new File("MyTimes.txt");
  try {
  if (!fileT.exists()) {
        fileT.createNewFile();
     }

    }
    catch (IOException ioe) {
      ioe.printStackTrace();
     }
    //metafora twn stoixeiwn ths listas sto txt
     BufferedWriter bwTimes = null;
     FileWriter fwTimes = null;

   try{

        fwTimes = new FileWriter("MyTimes.txt");
        bwTimes = new BufferedWriter(fwTimes);

        int timesR=timesResend.size();

        for(int i=0; i<timesR; i++){
        bwTimes.write(Long.toString(timesResend.get(i))+"\n");
        }
      bwTimes.close();
      }
      catch (IOException ioe) {
      ioe.printStackTrace();
     }

  modem.close();
} /* gia th sunarthsh demo*/


}
