//PGM.java

import java.lang.*;
import java.io.*;
import java.awt.*;
import java.awt.event.*;
import javax.swing.*;

////pgmimage
class PGM
{
	private String tFilePath;

	//pgm imageheader
	private String type;
	private String comment;
	private int cols,rows,maxgray;

	//pgm imagedata
	private int[][] pixel;
	
	//constructor
	public PGM()
	{
		tFilePath="";
		type="";
		comment="";
		cols=0;
		rows=0;
		maxgray=0;
		pixel=null;
	}

	//get functions
	public String getFilePath()
	{
		return(tFilePath);
	}

	public String getType()
	{
		return(type);
	}

	public String getComment()
	{
		return(comment);
	}

	public int getCols()
	{
		return(cols);
	}

	public int getRows()
	{
		return(rows);
	}

	public int getMaxGray()
	{
		return(maxgray);
	}

	public int getPixel(int tr,int tc)
	{
		return(tr<0||tr>rows-1||tc<0||tc>cols-1?0:pixel[tr][tc]);
	}

	

	//set functions
	public void setFilePath(String ttFilePath)
	{
		tFilePath=ttFilePath;
	}

	public void setType(String ttype)
	{
		type=ttype;
	}

	public void setComment(String tcomment)
	{
		comment=tcomment;
	}

	public void setDimension(int tcols,int trows)
	{
		rows=trows;
		cols=tcols;
		pixel=new int[rows][cols];
	}

	public void setMaxGray(int tmaxgray)
	{
		maxgray=tmaxgray;
	}

	public void setPixel(int tr,int tc,int tpval)
	{
		if(tr<0||tr>rows-1||tc<0||tc>cols-1) return;
		else pixel[tr][tc]=tpval;
	}

	//methods
	public void readImage()
	{
		FileInputStream fin;
		
		try
		{
			fin=new FileInputStream(tFilePath);
	
		    int tr,tc,c;
		    String tstr;
		    
		    //read first line of ImageHeader
		    tstr="";
		    c=fin.read();
		    tstr+=(char)c;
		    c=fin.read();
		    tstr+=(char)c;
		    type=tstr;

		    //read second line of ImageHeader
		    c=fin.read(); //read Lf (linefeed)
		    c=fin.read(); //read '#'
			tstr="";
		    boolean iscomment=false;
		    while((char)c=='#') //read comment
		    {
				iscomment=true;
			    tstr+=(char)c;
		        while(c!=10&&c!=13)
		        {
		            c=fin.read();
				    tstr+=(char)c;
		     	}
		        c=fin.read(); //read next '#'
		 	}
		    comment=tstr;
		    
		    //read third line of ImageHeader
		    //read cols
			if(iscomment==true) c=fin.read();
		    tstr+=(char)c;
		    while(c!=32&&c!=10&&c!=13)
		    {
		        c=fin.read();
		        tstr+=(char)c;
		 	}
		    tstr=tstr.substring(0,tstr.length()-1);
		    cols=Integer.parseInt(tstr);
		    
		    //read rows
			c=fin.read();
			tstr="";
		    tstr+=(char)c;
		    while(c!=32&&c!=10&&c!=13)
		    {
		        c=fin.read();
		        tstr+=(char)c;
		 	}
		    tstr=tstr.substring(0,tstr.length()-1);
		    rows=Integer.parseInt(tstr);
		    
		    //read maxgray
			c=fin.read();
			tstr="";
		    tstr+=(char)c;
		    while(c!=32&&c!=10&&c!=13)
		    {
		        c=fin.read();
		        tstr+=(char)c;
		 	}
		    tstr=tstr.substring(0,tstr.length()-1);
		    maxgray=Integer.parseInt(tstr);
		    
		    //read pixels from ImageData
			pixel=new int[rows][cols];
		    for(tr=0;tr<rows;tr++)
		    {
		    	for(tc=0;tc<cols;tc++)
		    	{
		    		c=(int)fin.read();
		    		setPixel(tr,tc,c);
		    	}
		    }
		    
			fin.close();
		}
		catch(Exception e)
		{
			System.out.println("Error: "+e.getMessage());
		}
	}

	public void writeImage()
	{
		FileOutputStream fout;
		
		try
		{
			fout=new FileOutputStream(tFilePath);
			
			//write image header
			//write PGM magic value 'P5'
			String tstr;
			tstr="P5"+"\n";
			fout.write(tstr.getBytes());
			
			//write comment
			comment=comment+"\n";
			//fout.write(comment.getBytes());

			//write cols
			tstr=Integer.toString(cols);
			fout.write(tstr.getBytes());
			fout.write(32); //write blank space
			
			//write rows
			tstr=Integer.toString(rows);
			fout.write(tstr.getBytes());
			fout.write(32); //write blank space
			
			//write maxgray
			tstr=Integer.toString(maxgray);
			tstr=tstr+"\n";
			fout.write(tstr.getBytes());
			
			for(int r=0;r<rows;r++)
			{
				for(int c=0;c<cols;c++)
				{
					fout.write(getPixel(r,c));
				}
			}

			fout.close();
		}
		catch(Exception e)
		{
			System.out.println("Error: "+e.getMessage());
		}
	}

	public void copyImage(String tstrOuttFilePath)
	{
		PGM imgout=new PGM();

		//create new image
		imgout.setFilePath(tstrOuttFilePath);
		imgout.setType(getType());
		imgout.setComment(getComment());
		imgout.setDimension(getCols(),getRows());
		imgout.setMaxGray(getMaxGray());
		for(int r=0;r<getRows();r++)
		{
			for(int c=0;c<getCols();c++)
			{
				imgout.setPixel(r,c,getPixel(r,c));
			}
		}
		imgout.writeImage();
	}
}
