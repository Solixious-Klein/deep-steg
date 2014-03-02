/*
 * @author : The Arcanist
 * @version : 1.0
 * @Created : 2nd March 2013
 */
package ds;

import java.awt.image.BufferedImage;
import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;

import javax.imageio.ImageIO;

public class DeepSteg {

	public static void main(String args[]) {
		if(args.length==5) {
			if(args[0].equalsIgnoreCase("+h")) {
				int depth=Integer.parseInt(args[1]);
				String imageSource=args[2];
				String fileSource=args[3];
				String imageDestination=args[4];
				File secret=new File(fileSource);
				BufferedImage bi=null;
				try {
					bi=ImageIO.read(new File(imageSource));
				}
				catch(Exception e) {
					e.printStackTrace();
				}
				Steganography steg=new Steganography(bi,secret);

				if(steg.hide(imageDestination, depth))
					System.out.println("\nDestination file successfully outputted");
				else
					System.out.println("\nThe file size crosses limit for the given image.\nTry increasing depth.");
			}
			else {
				System.out.println("Invalid switch '"+args[0]+"'");
			}
		}
		else if(args.length==3) {
			if(args[0].equalsIgnoreCase("-h")) {
				String imageSource=args[1];
				String fileDestination=args[2];
				BufferedImage bi=null;
				try {
					bi=ImageIO.read(new File(imageSource));
				}
				catch(Exception e) {
					e.printStackTrace();
				}
				Steganography steg=new Steganography(bi);
				if(steg.extract(fileDestination))
					System.out.println("\nFile Extracted Successfully!");
				else
					System.out.println("\nAn error was encountered while extracting file.");
			}
		}
		else if(args.length==1) {
			if(args[0].equalsIgnoreCase("-help")){
				System.out.println("______                  _____ _                      __   _____ ");
				System.out.println("|  _  \\                /  ___| |                    /  | |  _  |");
				System.out.println("| | | |___  ___ _ __   \\ `--.| |_ ___  __ _  __   __`| | | |/' |");
				System.out.println("| | | / _ \\/ _ \\ '_ \\   `--. \\ __/ _ \\/ _` | \\ \\ / / | | |  /| |");
				System.out.println("| |/ /  __/  __/ |_) | /\\__/ / ||  __/ (_| |  \\ V / _| |_\\ |_/ /");
				System.out.println("|___/ \\___|\\___| .__/  \\____/ \\__\\___|\\__, |   \\_/  \\___(_)___/ ");
				System.out.println("               | |                     __/ |                    ");
				System.out.println("               |_|                    |___/                     ");
				System.out.println("\n\n---------------------------------------------------------------------------------");
				System.out.println("\n   Description:\n" +
						"\n---------------------------------------------------------------------------------" +
						"\n   Deep Steg v1.0 is a steganography software made in java and is used to hide\n" +
						"   files inside pixels of an images.\n" +
						"\n---------------------------------------------------------------------------------" +
						"\n\n\n\n" +
						"\n---------------------------------------------------------------------------------" +
						"\n   Format:" +
						"\n---------------------------------------------------------------------------------" +
						"\n   java ds.DeepSteg [mode] [param1] [param2] [param3] [param4]\n" +
						"\n---------------------------------------------------------------------------------" +
						"\n\n   [mode] -----> +h -----> for hiding file mode\n" +
						"                 -h -----> for extracting hidden file mode\n\n" +
						"   For mode +h,\n" +
						"   [param1] -----> Depth [1-4]\n" +
						"   [param2] -----> Source Image's Name/Path\n" +
						"   [param3] -----> Secret File's Name/Path\n" +
						"   [param4] -----> Destination Image's Name/Path\n\n" +
						"   For mode -h,\n" +
						"   [param1] -----> Carrier Image's Name/Path\n" +
						"   [param2] -----> Extracted File's Name/Path\n" +
						"\n---------------------------------------------------------------------------------"
						);
			}
			else {
				System.out.println("Invalid switch '"+args[0]+"'");
			}
		}
		else if(args.length==0) {
			BufferedReader br=new BufferedReader(new InputStreamReader(System.in));
			System.out.println("______                  _____ _                      __   _____ ");
			System.out.println("|  _  \\                /  ___| |                    /  | |  _  |");
			System.out.println("| | | |___  ___ _ __   \\ `--.| |_ ___  __ _  __   __`| | | |/' |");
			System.out.println("| | | / _ \\/ _ \\ '_ \\   `--. \\ __/ _ \\/ _` | \\ \\ / / | | |  /| |");
			System.out.println("| |/ /  __/  __/ |_) | /\\__/ / ||  __/ (_| |  \\ V / _| |_\\ |_/ /");
			System.out.println("|___/ \\___|\\___| .__/  \\____/ \\__\\___|\\__, |   \\_/  \\___(_)___/ ");
			System.out.println("               | |                     __/ |                    ");
			System.out.println("               |_|                    |___/                     ");
			System.out.println("\n\nType '1' for Hiding Mode\nType '2' for Extraction Mode\n\n");
			String ch=null;
			try {
				ch=br.readLine();
			}
			catch(IOException e) {
				e.printStackTrace();
			}
			if(ch.equals("1")) {
				try {
					System.out.print("\n\nEnter the depth [1-4] : ");
					int depth=Integer.parseInt(br.readLine());
					System.out.print("Source Image : ");
					String imageSource=br.readLine();
					System.out.print("Secret File : ");
					String fileSource=br.readLine();
					System.out.print("Destination Image : ");
					String imageDestination=br.readLine();
					File secret=new File(fileSource);
					BufferedImage bi=null;
					try {
						bi=ImageIO.read(new File(imageSource));
					}
					catch(Exception e) {
						e.printStackTrace();
					}
					Steganography steg=new Steganography(bi,secret);
					if(steg.hide(imageDestination, depth))
						System.out.println("\nDestination file successfully outputted");
					else
						System.out.println("\nThe file size crosses limit for the given image.\nTry increasing depth.");
					
				}
				catch(IOException e) {
					e.printStackTrace();
				}
			}
			else if(ch.equals("2")) {
				try {
					System.out.print("\n\nCarrier Image : ");
					String imageSource=br.readLine();
					System.out.print("Extracted File Destination : ");
					String fileDestination=br.readLine();
					BufferedImage bi=null;
					bi=ImageIO.read(new File(imageSource));
					Steganography steg=new Steganography(bi);
					if(steg.extract(fileDestination))
						System.out.println("\nFile Extracted Successfully!");
					else
						System.out.println("\nAn error was encountered while extracting file.");
				}
				catch(Exception e) {
					e.printStackTrace();
				}
			}
			else {
				
			}
		}
		else {
			System.out.println("Wrong parameter format.\n");
			System.out.println("Type the following for help...");
			System.out.println("java ds.DeepSteg -help");
		}
	}
}
