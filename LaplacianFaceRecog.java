//LaplacianFaceRecog.java

import java.lang.*;
import java.io.*;
import java.awt.*;
import java.awt.event.*;
import javax.swing.*;

class LaplacianFaceRecog extends JFrame implements ActionListener
{
	JFrame frmMain=new JFrame("Face Recognition Using LaplacianFaces");
	JLabel lblTestPath=new JLabel("Input Test Image:");
	JTextField txtTestPath=new JTextField("images\\test\\face16d.pgm");
	JButton btRecognize=new JButton("Recognize");
	JLabel lblResult=new JLabel("Result:");
	JTextArea txtResult=new JTextArea("");
	JScrollPane spResult=new JScrollPane(txtResult);
	JFrame frmImage1=new JFrame("Input Image");
	JFrame frmImage2=new JFrame("Matched Image");

	//system declarations
	int MaxFaceIndex=17;
	double DifferenceThreshold=15000.0;
	int NumFaces;
	int MaxFaces=100;
	int FaceTemplate[][];
	int Faces[][][];
	int LaplacianFaces[][][];
	String FaceFileNames[];
	String tResult="";
	
	//constructor
	public LaplacianFaceRecog()
	{
		frmMain.setDefaultLookAndFeelDecorated(true);
		frmMain.setResizable(false);
		frmMain.setBounds(100,100,315,250);
		frmMain.getContentPane().setLayout(null);
		
		lblTestPath.setBounds(17,15,100,20);
		frmMain.getContentPane().add(lblTestPath);
		txtTestPath.setBounds(15,35,170,20);
		frmMain.getContentPane().add(txtTestPath);
		
		lblResult.setBounds(17,65,100,20);
		frmMain.getContentPane().add(lblResult);
		spResult.setBounds(15,85,280,120);
		frmMain.getContentPane().add(spResult);
		txtResult.setEditable(false);
		
		btRecognize.setBounds(193,35,100,20);
		btRecognize.addActionListener(this);
		frmMain.getContentPane().add(btRecognize);
		
		frmImage1.setDefaultLookAndFeelDecorated(true);
		frmImage1.setResizable(false);
		frmImage1.setBounds(450,100,200,150);
		
		frmImage2.setDefaultLookAndFeelDecorated(true);
		frmImage2.setResizable(false);
		frmImage2.setBounds(670,100,200,150);
		
		frmImage1.setVisible(true);
		frmImage2.setVisible(true);
		frmMain.setVisible(true);
	}
	
	//events
	public void actionPerformed(ActionEvent evt)
	{
		if(evt.getSource()==btRecognize)
		{
			if(new File(txtTestPath.getText()).exists()==false)
			{
				JOptionPane.showMessageDialog(null,"Test Image, not found.");
				return;
			}
			
			drawImage(frmImage1.getGraphics(),txtTestPath.getText());
			
			tResult=" ";
			txtResult.setText(tResult);
			train();
			test();
		}
	}
	
	//internal methods
	void drawImage(Graphics g,String tPath)
	{
		PGM tpgm=new PGM();
		tpgm.setFilePath(tPath);
		tpgm.readImage();
		
		g.clearRect(0,0,200,150);
		for(int r=0;r<tpgm.getRows();r++)
		{
			for(int c=0;c<tpgm.getCols();c++)
			{
				int intensity=tpgm.getPixel(r,c);
				Color color=new Color(intensity,intensity,intensity);
				g.setColor(color);
				g.fillRect(c,r+30,1,1);
			}
		}
	}
	
	
	//methods
	public void addResultText(String tStr)
	{
		tResult=tResult+tStr;
		txtResult.setText(tResult);
	}
	
	public void train()
	{
		int xBase,yBase,xSub,ySub;
		int xLow,xHigh,yLow,yHigh;
		int GrayLevel;
		int CellSum,CellAvg;
		int i,j;
		int xDiv,yDiv;
		int BlockWidth,BlockHeight;
		int StartX,StartY;
		int SizeX,SizeY;
		
		//set system parameters
		SizeX=80;
		SizeY=80;
		xDiv=20;
		yDiv=20;
		BlockWidth=SizeX/xDiv;
		BlockHeight=SizeY/yDiv;
		
		FaceTemplate=new int[xDiv][yDiv];
		Faces=new int[xDiv][yDiv][MaxFaces];
		LaplacianFaces=new int[xDiv][yDiv][MaxFaces];
		FaceFileNames=new String[MaxFaces];
		NumFaces=0;
		
		addResultText("Training...");
		PGM pgm1=new PGM();
		for(i=0;i<=MaxFaceIndex;i++)
		{
			for(j=97;j<=99;j++)//'a' to 'b'
			{
				NumFaces=NumFaces+1;
				FaceFileNames[NumFaces]="images\\train\\face"+i+(char)j+".pgm";
				PGM_ImageFilter imgFilter=new PGM_ImageFilter();
				imgFilter.set_inFilePath(FaceFileNames[NumFaces]);
				imgFilter.set_outFilePath("temp.pgm");
				imgFilter.resize(SizeX,SizeY);
				pgm1.setFilePath("temp.pgm");
				pgm1.readImage();
				
				for(xBase=0;xBase<=xDiv-1;xBase++)
				{
			for(yBase=0;yBase<=yDiv-1;yBase++)
					{
						StartX=xBase*BlockWidth;
						StartY=yBase*BlockHeight;
						xLow=StartX;
						xHigh=StartX+BlockWidth-1;
						yLow=StartY;
						yHigh=StartY+BlockHeight-1;
						
						CellSum=0;
						for(xSub=xLow;xSub<=xHigh;xSub++)
						{
							for(ySub=yLow;ySub<=yHigh;ySub++)
							{
								GrayLevel=pgm1.getPixel(xSub,ySub);
								CellSum=CellSum+GrayLevel;
							}
						}
						CellAvg=CellSum/(BlockWidth*BlockHeight);
						Faces[xBase][yBase][NumFaces]=CellAvg;
					}
				}
			}
		}
		
		for(xBase=0;xBase<=xDiv-1;xBase++)
		{
			for(yBase=0;yBase<=yDiv-1;yBase++)
			{
				CellSum=0;
				for(i=1;i<=NumFaces;i++)
				{
					CellSum=CellSum+Faces[xBase][yBase][i];
				}
				CellAvg=CellSum/NumFaces;
				FaceTemplate[xBase][yBase]=CellAvg;
			}
		}

		for(xBase=0;xBase<=xDiv-1;xBase++)
		{
			for(yBase=0;yBase<=yDiv-1;yBase++)
			{
				for(i=1;i<=NumFaces;i++)
				{
					LaplacianFaces[xBase][yBase][i]=Faces[xBase][yBase][i]-FaceTemplate[xBase][yBase];
				}
			}
		}

		PGM pgm2=new PGM();
		pgm2.setFilePath("template.pgm");
		pgm2.setType("P5");
		pgm2.setComment("");
		pgm2.setDimension(SizeX,SizeY);
		pgm2.setMaxGray(255);
		
		for(xBase=0;xBase<=xDiv-1;xBase++)
		{
			for(yBase=0;yBase<=yDiv-1;yBase++)
			{
				StartX=xBase*BlockWidth;
				StartY=yBase*BlockHeight;
				xLow=StartX;
				xHigh=StartX+BlockWidth-1;
				yLow=StartY;
				yHigh=StartY+BlockHeight-1;
				
				for(xSub=xLow;xSub<=xHigh;xSub++)
				{
					for(ySub=yLow;ySub<=yHigh;ySub++)
					{
						GrayLevel=FaceTemplate[xBase][yBase];
						pgm2.setPixel(xSub,ySub,GrayLevel);
					}
				}
			}
		}
		
		pgm2.writeImage();
		addResultText("done.");
	}
	
	public void test()
	{
		int xBase,yBase,xSub,ySub;
		int xLow,xHigh,yLow,yHigh;
		int GrayLevel;
		int CellSum,CellAvg;
		int i,j;
		int xDiv,yDiv;
		int BlockWidth,BlockHeight;
		int StartX,StartY;
		int SizeX,SizeY;
		
		//set system parameters
		SizeX=80;
		SizeY=80;
		xDiv=20;
		yDiv=20;
		BlockWidth=SizeX/xDiv;
		BlockHeight=SizeY/yDiv;
		
		int TestFace[][]=new int[xDiv][yDiv];
		int TestLaplacianFace[][]=new int[xDiv][yDiv];
		int LaplacianDiff;
		int MinLaplacianIndex;
		double TotalLaplacianDiff,MinLaplacianDiff;
		
		addResultText("\nTesting...");
		PGM pgm1=new PGM();
		pgm1.setFilePath(txtTestPath.getText());
		pgm1.readImage();
		
		for(xBase=0;xBase<=xDiv-1;xBase++)
		{
			for(yBase=0;yBase<=yDiv-1;yBase++)
			{
				StartX=xBase*BlockWidth;
				StartY=yBase*BlockHeight;
				xLow=StartX;
				xHigh=StartX+BlockWidth-1;
				yLow=StartY;
				yHigh=StartY+BlockHeight-1;
				
				CellSum=0;
				for(xSub=xLow;xSub<=xHigh;xSub++)
				{
					for(ySub=yLow;ySub<=yHigh;ySub++)
					{
						GrayLevel=pgm1.getPixel(xSub,ySub);
						CellSum=CellSum+GrayLevel;
					}
				}
				CellAvg=CellSum/(BlockWidth*BlockHeight);
				TestFace[xBase][yBase]=CellAvg;
			}
		}
		
		PGM pgm2=new PGM();
		pgm2.setFilePath("diff.pgm");
		pgm2.setType("P5");
		pgm2.setComment("");
		pgm2.setDimension(SizeX,SizeY);
		pgm2.setMaxGray(255);
		
		for(xBase=0;xBase<=xDiv-1;xBase++)
		{
			for(yBase=0;yBase<=yDiv-1;yBase++)
			{
				StartX=xBase*BlockWidth;
				StartY=yBase*BlockHeight;
				xLow=StartX;
				xHigh=StartX+BlockWidth-1;
				yLow=StartY;
				yHigh=StartY+BlockHeight-1;
				
				for(xSub=xLow;xSub<=xHigh;xSub++)
				{
					for(ySub=yLow;ySub<=yHigh;ySub++)
					{
						GrayLevel=TestFace[xBase][yBase];
						pgm2.setPixel(xSub,ySub,GrayLevel);
					}
				}
			}
		}
		
		for(xBase=0;xBase<=xDiv-1;xBase++)
		{
			for(yBase=0;yBase<=yDiv-1;yBase++)
			{
				TestLaplacianFace[xBase][yBase]=TestFace[xBase][yBase]-FaceTemplate[xBase][yBase];
			}
		}
		             
		MinLaplacianDiff=2147483647; //2^32
		MinLaplacianIndex=-1;
		for(i=1;i<=NumFaces;i++)
		{
			TotalLaplacianDiff=0;
			for(xBase=0;xBase<=xDiv-1;xBase++)
			{
				for(yBase=0;yBase<=yDiv-1;yBase++)
				{
					TotalLaplacianDiff=TotalLaplacianDiff+java.lang.Math.abs(TestLaplacianFace[xBase][yBase]-LaplacianFaces[xBase][yBase][i]);
				}
			}
			if(MinLaplacianDiff>TotalLaplacianDiff)
			{
				MinLaplacianDiff=TotalLaplacianDiff;
				MinLaplacianIndex=i;
			}
		}
		
		pgm2.writeImage();
		
		if(MinLaplacianDiff>DifferenceThreshold)
		{
			frmImage2.getGraphics().clearRect(0,0,200,150);
			addResultText("done.");
			addResultText("\n\nNot Matched.");
			JOptionPane.showMessageDialog(null,"Not Matched.");
		}
		else
		{
			PGM pgmMatched=new PGM();
			pgmMatched.setFilePath(FaceFileNames[MinLaplacianIndex]);
			pgmMatched.readImage();
			pgmMatched.setFilePath("matched.pgm");
			pgmMatched.writeImage();
			drawImage(frmImage2.getGraphics(),"matched.pgm");
			addResultText("done.");
			addResultText("\n\nMatched: "+FaceFileNames[MinLaplacianIndex]);
		}
	}
	
	public static void main(String args[])
	{
		new LaplacianFaceRecog();
	}
}