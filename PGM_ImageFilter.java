//PGM_ImageFilter.java

import java.lang.*;
import java.io.*;
import java.lang.Math.*;

public class PGM_ImageFilter
{
	String inFilePath,outFilePath;
	boolean printStatus=false;

	//constructor
	public PGM_ImageFilter()
	{
		inFilePath="";
		outFilePath="";
	}

	//get functions
	public String get_inFilePath()
	{
		return(inFilePath);
	}
	
	public String get_outFilePath()
	{
		return(outFilePath);
	}
	
	//set functions
	public void set_inFilePath(String tFilePath)
	{
		inFilePath=tFilePath;
	}
	
	public void set_outFilePath(String tFilePath)
	{
		outFilePath=tFilePath;
	}

	//methods
	public void resize(int wout,int hout)
	{
		PGM imgin=new PGM();
		PGM imgout=new PGM();
	
		if(printStatus==true)
		{
			System.out.print("\nResizing...");
		}
		int r,c,inval,outval;
	
		//read input image
		imgin.setFilePath(inFilePath);
		imgin.readImage();
	
		//set output-image header
		imgout.setFilePath(outFilePath);
		imgout.setType("P5");
		imgout.setComment("#resized image");
		imgout.setDimension(wout,hout);
		imgout.setMaxGray(imgin.getMaxGray());
	
		//resize algorithm (linear)
		double win,hin;
		int xi,ci,yi,ri;
	
		win=imgin.getCols();
		hin=imgin.getRows();
	
		for(r=0;r<imgout.getRows();r++)
		{
			for(c=0;c<imgout.getCols();c++)
			{
				xi=c;
				yi=r;
	
				ci=(int)(xi*((double)win/(double)wout));
				ri=(int)(yi*((double)hin/(double)hout));
				
				inval=imgin.getPixel(ri,ci);
				outval=inval;
	
				imgout.setPixel(yi,xi,outval);
			}
		}
	
		if(printStatus==true)
		{
			System.out.println("done.");
		}
	
		//write output image
		imgout.writeImage();
	}
	
}
