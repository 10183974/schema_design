import java.util.Random;

public class TestIBytenterator {

	public static void main(String[] args) {
		// TODO Auto-generated method stub
		byte[] buffer={0,0,0,0,0,0,0};
	
		int len;
		int off=7;	
			//buf[0]=(byte)(generator.nextInt());
			Random generator=new Random();
		    //int bytes = generator.nextInt(1000);
			int bytes = 21;
			
		    System.out.println(bytes);
		    System.out.println(Integer.toBinaryString(bytes));
			  //int bytes=generator.nextInt();
		    try {
		      
		      /*buffer[0] = (byte)((bytes & 31));
		      buffer[1] = (byte)(((bytes >> 5) & 31) );
		      buffer[2] = (byte)(((bytes >> 10) & 31) );
		      buffer[3] = (byte)(((bytes >> 15) & 31) );
		      buffer[4] = (byte)(((bytes >> 20) & 31) );
		      buffer[5] = (byte)(((bytes >> 25) & 31) );
		      buffer[6] = (byte)(((bytes >> 30) & 31) );*/
		      buffer[0] = (byte)((bytes & 7));
		      buffer[1] = (byte)(((bytes >> 3) & 7) );
		      buffer[2] = (byte)(((bytes >> 6) & 7) );
		      buffer[3] = (byte)(((bytes >> 9) & 7) );
		      buffer[4] = (byte)(((bytes >> 12) & 7) );
		      buffer[5] = (byte)(((bytes >> 15) & 7) );
		      buffer[6] = (byte)(((bytes >> 18) & 7) );
		      /*
		    	buffer[0] = (byte)(((bytes) & 255));
			      buffer[1] = (byte)(((bytes >> 8) & 255));
			      buffer[2] = (byte)(((bytes >> 16) & 255));
			      buffer[3] = (byte)(((bytes >> 24) & 255));
			     // buffer[4] = (byte)(((bytes >> 32) & 255));
			     //buffer[5] = (byte)(((bytes >> 40) & 255));
		      //buffer[5] = (byte)(((bytes >> 25) & 31)+' ' );*/
		    } catch (ArrayIndexOutOfBoundsException e) { /* ignore it */ }
		
			StringBuilder sb = new StringBuilder();
			//int num=0;
			while(off>0) { 
				off--;
				//num=32*num+buffer[off];
				sb.append(buffer[off]);}
			//System.out.println(num);
			Integer i=Integer.parseInt(sb.toString(),8);
			System.out.println(i.toString());

	}

}
