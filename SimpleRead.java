import java.io.*;
import java.util.*;
import java.awt.*;
import javax.comm.*;

public class SimpleRead implements Runnable, SerialPortEventListener {
    
	/**
	 * The below map defines static data which can be stored in DB but for small
	 * project purpose it is kept in memory as Map. The Map will be initialized
	 * with default values when this program is started
	 */	
	
	static Map<String, CardDetail> cardIdMap = new HashMap<>();
	static Map<String, CardHolder> cardIdMap1 = new HashMap<>();
	static{
		System.out.println("Loading all card data....");
		CardDetail("54006B42265B", "A", "06/05/2018", "10");
		CardDetail("54006B423944", "B", "23/03/2018", "20");

		CardDetail("54006B42403D", "C", "28/10/2018", "25");

		CardDetail("54006B45562C", "D", "07/02/2018", "30");

		CardHolder("54006B427C01", "Ram","2016A","250");
		/* ADD ALL REMAINING HERE */
		
		
		
		System.out.println("Loaded all card data");
		System.out.println("\n\n\n************************************************************");
		System.out.println("Ready to detect card, Please flash the card to the detector\n\n");
	}
	
	static void CardDetail(String cardNo, String name, String mdate, String price){
		cardIdMap.put(cardNo, new CardDetail(cardNo, name, mdate, price));
	}
	static void CardHolder(String cardNo, String C_name, String C_ID, String string){
		cardIdMap1.put(cardNo, new CardHolder(cardNo, C_name, C_ID, string));
	}
	
	static class CardDetail{
		private String name, mdate, cardNo, price;
		
		private int topUpBalance=0;
		
		public CardDetail(String cardNo, String name, String mdate, String price){
			this.cardNo =cardNo;
			this.name = name;
			this.mdate = mdate;
			this.price = price;
		}
		@Override
		public String toString() {
			String ret = "Card No: " + cardNo + "\nProduct: " + name + "\nmdate: " + mdate +"\nPrice: " + price;
			return ret;
		}
	}
		

	static class CardHolder{
		public String C_name, C_ID, cardNo, S_credit;
		
			
		public CardHolder(String cardNo, String C_name, String C_ID, String S_credit){
			this.cardNo =cardNo;
			this.C_name = C_name;
			this.C_ID = C_ID;
			this.S_credit =S_credit;
		}
		
		
		public void setCardNo(String cardNo){
			this.cardNo = cardNo;
		}
		
		@Override
		public String toString() {
			String ret1 = "Card No: " + cardNo + "\nCustomer: " + C_name + "\nCustomerID: " + C_ID;
			
			return ret1;
		}
		
		public String toString2() {
			String ret2="StoreCredits: "+S_credit;
			return ret2;
		}
	}
	
	
	static CommPortIdentifier portId;
    static Enumeration portList;
	
    
    
    InputStream inputStream;
    SerialPort serialPort;
    Thread readThread;

    public static void main(String[] args) {
        portList = CommPortIdentifier.getPortIdentifiers();

        while (portList.hasMoreElements()) {
            portId = (CommPortIdentifier) portList.nextElement();
            if (portId.getPortType() == CommPortIdentifier.PORT_SERIAL) {
                 if (portId.getName().equals("COM3")) {
			//                if (portId.getName().equals("/dev/term/a")) {
                    SimpleRead reader = new SimpleRead();
                }
            }
        }
    }

    public SimpleRead() {
        try {
            serialPort = (SerialPort) portId.open("SimpleReadApp", 2000);
        } catch (PortInUseException e) {System.out.println(e);}
        try {
            inputStream = serialPort.getInputStream();
        } catch (IOException e) {System.out.println(e);}
	try {
            serialPort.addEventListener(this);
	} catch (TooManyListenersException e) {System.out.println(e);}
        serialPort.notifyOnDataAvailable(true);
        try {
            serialPort.setSerialPortParams(9600,
                SerialPort.DATABITS_8,
                SerialPort.STOPBITS_1,
                SerialPort.PARITY_NONE);
        } catch (UnsupportedCommOperationException e) {System.out.println(e);}
        readThread = new Thread(this);
        readThread.start();
    }
    
    @Override
    public void run() {
        try {
            Thread.sleep(20000);
        } catch (InterruptedException e) {System.out.println(e);}
    }
    
    @Override
    public void serialEvent(SerialPortEvent event) {
        switch(event.getEventType()) {
        case SerialPortEvent.BI:
        case SerialPortEvent.OE:
        case SerialPortEvent.FE:
        case SerialPortEvent.PE:
        case SerialPortEvent.CD:
        case SerialPortEvent.CTS:
        case SerialPortEvent.DSR:
        case SerialPortEvent.RI:
        case SerialPortEvent.OUTPUT_BUFFER_EMPTY:
            break;
        case SerialPortEvent.DATA_AVAILABLE:
            byte[] readBuffer = new byte[20];

            try {
                while (inputStream.available() > 0) {
                    int numBytes = inputStream.read(readBuffer);
                }
                processCardIdentification(new String(readBuffer).trim().replaceAll(" ", "").replaceAll("\t", "").replaceAll("\n", ""));
                //System.out.print(new String(readBuffer));
            } catch (Exception e) {System.out.println(e);}
            break;
        }
    }

    /**
     * This will process the card id
     * @param string
     */
	
	
	
        	
	int PRICE, sum=0,money;
	
	private void processCardIdentification(String cardId) {
		if(cardIdMap.containsKey(cardId)){
			CardDetail details = cardIdMap.get(cardId);
			System.out.println(details.toString());
			PRICE=Integer.parseInt(details.price);
			sum=sum+PRICE;
			System.out.println("TOTAL AMOUNT= "+sum);
		}
		else if(cardIdMap1.containsKey(cardId)){
			CardHolder details = cardIdMap1.get(cardId);
			System.out.println(details.toString());
			System.out.println("Your previous balance");

			System.out.println(details.toString2());
			//S_credit=S_credit-sum;
			int newsum=Integer.parseInt(details.S_credit)-sum;
			
			details.S_credit = String.valueOf(newsum);

			System.out.println("Now after deduction your balance is= "+details.S_credit);	
		}
		
		else{
			System.out.println("Card with Id: '"+cardId+"' is not allocated to anyone.");
		}
		System.out.println("\n\n\n\n");
		System.out.println("************************************************************");
		System.out.println("Ready to detect card, Please flash the card to the detector\n");
	
	}
}