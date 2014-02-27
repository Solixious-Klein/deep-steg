package ds;

import java.awt.Color;
import java.awt.image.BufferedImage;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;

import javax.imageio.ImageIO;

public class Steganography {
	
	private BufferedImage originalImage, stegImage;
	private int height, width;
	
	private File file;
	
	private int depth;
	
	private int pointer1,pointer2,pointer3,pointer4;
	
	public Steganography(BufferedImage image, File file) {
		
		this.originalImage = image;
		height=image.getHeight();
		width=image.getWidth();
		this.file = file;
		pointer1=0;
		pointer2=0;
		depth=1;
	}
	
	public Steganography(BufferedImage image) {
		this.originalImage = image;
		height=image.getHeight();
		width=image.getWidth();
		pointer3=0;
		pointer4=0;
		depth=1;
	}
	
	public void setDepth(int depth) {
		
		this.depth=depth;
	}
	
	private void initializeUserSpace() {
		
		stegImage=new BufferedImage(width, height, BufferedImage.TYPE_INT_RGB);
	}
	
	private byte[] getImageBytes() {
		int pixels=width*height;
		byte b[]=new byte[pixels*3];
		int ctr=0;
		
		for(int i=0;i<width;i++) {
			for(int j=0;j<height;j++) {
				Color c=new Color(originalImage.getRGB(i, j));
				byte red=(byte)(c.getRed()-128);
				byte green=(byte)(c.getGreen()-128);
				byte blue=(byte)(c.getBlue()-128);
				b[ctr]=red;
				b[ctr+1]=green;
				b[ctr+2]=blue;
				ctr+=3;
			}
		}
		
		return b;
	}
	
	private byte[] getFileBytes() {
		
		int size=(int)file.length();
		byte b[]=new byte[size];
		
		DataInputStream din=null;
		try {
			
			din=new DataInputStream(new FileInputStream(file));
		}
		catch(FileNotFoundException e) {
			e.printStackTrace();
		}
		
		for(int i=0;i<size;i++) {
			
			try {
				b[i]=din.readByte();
			}
			catch(IOException e) {
				e.printStackTrace();
			}
		}
		
		try {
			din.close();
		}
		catch(IOException e) {
			e.printStackTrace();
		}
		
		return b;
	}
	
	public void writeToImage(byte img[],byte b) {
		for(int i=0;i<8;i++) {
			if(pointer1==depth) {
				pointer1=0;
				pointer2++;
			}
			img[pointer2]=(byte)(img[pointer2] & 127 - (byte)(Math.pow(2, pointer1)));
			img[pointer2]=(byte)(img[pointer2] | ((byte)(Math.pow(2, pointer1)) *
					(b & (byte)(Math.pow(2,i)))/(byte)(Math.pow(2, i))));
			pointer1++;
		}
	}
	
	public byte readFromImage(byte img[]) {
		byte b=0;
		for(int i=0;i<8;i++) {
			if(pointer3==depth) {
				pointer3=0;
				pointer4++;
			}
			b=(byte)(b+(byte)(Math.pow(2, i) * 
					(img[pointer4] & (byte)(Math.pow(2, pointer3))) / 
					(byte)(Math.pow(2, pointer3))));
			pointer3++;
		}
		return b;
	}
	
	public void save(String path,byte img[]) {
		int ctr=0;
		for(int i=0;i<width;i++) {
			for(int j=0;j<height;j++) {
				int red=img[ctr]+128;
				int green=img[ctr+1]+128;
				int blue=img[ctr+2]+128;
				Color c=new Color(red,green,blue);
				stegImage.setRGB(i, j, c.getRGB());
				ctr+=3;
			}
		}
		try
        {
            ImageIO.write(stegImage,path.substring(path.lastIndexOf('.')+1,path.length()),new File(path));
        }
        catch(IOException e) {
        	e.printStackTrace();
        }
	}
	
	public void extract(String path) {
		byte img[]=getImageBytes();
		try {
			File f=new File(path);
			f.createNewFile();
			DataOutputStream dos=new DataOutputStream(new FileOutputStream(f));
			for(int i=0;i<file.length();i++) {
				dos.writeByte(readFromImage(img));
			}
			dos.close();
		}
		catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	public void hide() {
		byte img[]=getImageBytes();
		byte b[]=getFileBytes();
		initializeUserSpace();
		for(int i=0;i<b.length;i++)
			writeToImage(img,b[i]);
		save("D:\\hid.png",img);
		
	}
	
	public static void main(String args[]) {
		BufferedImage bi=null;
		try {
			bi=ImageIO.read(new File("D:\\hid.png"));
		}
		catch(IOException e) {
			e.printStackTrace();
		}
		Steganography steg=new Steganography(bi,new File("C:\\TC\\BIN\\FIRST.EXE"));
		steg.extract("D:\\aaa.exe");
	}
}
