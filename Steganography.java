/*
 * @author : The Arcanist
 * @version : 1.0
 * @Created : 2nd March 2013
 */

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
	
	private void writeToImage(byte img[],byte b) {
		for(int i=0;i<8;i++) {
			if(pointer1==depth) {
				pointer1=0;
				pointer2++;
			}
			img[pointer2]=(byte)(img[pointer2] & 255 - (byte)(Math.pow(2, pointer1)));
			img[pointer2]=(byte)(img[pointer2] | ((byte)(Math.pow(2, pointer1)) *
					(b & (byte)(Math.pow(2,i)))/(byte)(Math.pow(2, i))));
			pointer1++;
		}
	}
	
	private byte readFromImage(byte img[]) {
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
	
	private void save(String path,byte img[]) {
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
	
	private byte[] sizeToBytes(int size) {
		byte b[]=new byte[4];
		for(int i=0;i<4;i++) {
			b[i]=(byte)((size%255)-128);
			size=size/255;
		}
		return b;
	}
	private int bytesToSize(byte b[]) {
		int size=0;
		for(int i=3;i>=0;i--) {
			size=(int)(size+((b[i]+128)*Math.pow(255,i)));
		}
		return size;
	}
	private boolean sizeWithinLimit() {
		int size=(int)file.length();
		int pixels=height*width;
		int possibleBytes=((pixels*3)*depth)/8;
		int actualBytes=possibleBytes-15;
		return (actualBytes<=size);
	}
	
	public int getSizeAllowed() {
		int pixels=height*width;
		int possibleBytes=((pixels*3)*depth)/8;
		int actualBytes=possibleBytes-15;
		return actualBytes;
	}
	
	public void setDepth(int depth) {
		
		this.depth=depth;
	}
	
	public boolean hide(String imgPath) {
		
		if(sizeWithinLimit()) {
			return false;
		}
		byte img[]=getImageBytes();
		byte b[]=getFileBytes();
		initializeUserSpace();
		
		//write the depth info
		byte temp=(byte)depth;
		depth=1;
		writeToImage(img,temp);
		depth=temp;
		
		//write file type (extension) info
		String path=file.getPath();
		String ext=null;
		if(path.lastIndexOf('.')!=-1) {
			ext=path.substring(path.lastIndexOf('.'), path.length());
			byte ex[]=ext.getBytes();
			byte len=(byte)ex.length;
			writeToImage(img,len);
			for(int i=0;i<len;i++) {
				writeToImage(img,ex[i]);
			}
		}
		else {
			byte len=0;
			writeToImage(img,len);
		}
		
		//write the size info
		byte s[]=sizeToBytes((int)file.length());
		for(int i=0;i<4;i++) {
			writeToImage(img,s[i]);
		}
		
		//write file data
		for(int i=0;i<b.length;i++)
			writeToImage(img,b[i]);
		
		save(imgPath,img);
		
		return true;
	}
	public boolean hide(String imgPath,int depth) {
		this.depth=depth;
		return hide(imgPath);
	}
	
	public boolean extract(String path) {
		
		path=path.toLowerCase();
		byte img[]=getImageBytes();
		try {
			//read the depth
			depth=1;
			depth=readFromImage(img);
			
			if(!(depth>0 && depth<8))
				return false;
			
			//read file type (extension) info
			byte l=readFromImage(img);
			byte ex[]=new byte[l];
			for(int i=0;i<l;i++) {
				ex[i]=readFromImage(img);
			}
			String ext=new String(ex).toLowerCase();
			File f=new File(path.endsWith(ext)?path:path+ext);
			f.createNewFile();
			DataOutputStream dos=new DataOutputStream(new FileOutputStream(f));
						
			//read the size info
			byte b[]=new byte[4];
			b[0]=readFromImage(img);
			b[1]=readFromImage(img);
			b[2]=readFromImage(img);
			b[3]=readFromImage(img);
			int size=bytesToSize(b);
			
			for(int i=0;i<size;i++) {
				dos.writeByte(readFromImage(img));
			}
			dos.close();
		}
		catch (Exception e) {
			e.printStackTrace();
			return false;
		}
		return true;
	}
}
