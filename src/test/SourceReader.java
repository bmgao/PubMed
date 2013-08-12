package test;

import org.jsoup.*;
import org.jsoup.nodes.Document;

import java.io.*;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import java.util.StringTokenizer;
import java.util.TreeMap;

import java.awt.BorderLayout;
import java.awt.EventQueue;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JPanel;
import javax.swing.JProgressBar;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.KeyStroke;
import javax.swing.SwingWorker;
import javax.swing.border.EtchedBorder;
import com.google.common.collect.Lists;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.RunnableFuture;

//%20 is a space in PubMed

public class SourceReader {
	public static void main(String[] args) throws IOException{
		
		//GUI START
		
		final JFrame frame= new JFrame();
		final int frameWidth = 600;
		final int frameHeight = 275;
		JPanel searchPanel = new JPanel();
		//Creating the text output area. The test area is uneditable and has a scroll window.
		final JTextArea textArea = new JTextArea(10,30);
		textArea.setEditable(false);
		JScrollPane scrolledPane = new JScrollPane(textArea);
		textArea.setBorder(new EtchedBorder());
		//Labels for the text fields
		JLabel searchLabel = new JLabel("Query: ");
		final JLabel fileLabel=new JLabel("Text file: none\t\t");
		//Test fields for input
		final JTextField searchField = new JTextField(35);
		//Search button
		JButton searchButton = new JButton("Search");
		//Add everything to the selection panel
		searchPanel.add(searchLabel);
		searchPanel.add(searchField);
		searchPanel.add(searchButton);
		frame.add(searchPanel,BorderLayout.NORTH);
		frame.add(scrolledPane,BorderLayout.CENTER);
		//JButton clearButton = new JButton("Clear");
		final JCheckBox logBox = new JCheckBox("Enable log");
		JPanel loadPanel = new JPanel();
		loadPanel.setBorder(new EtchedBorder());
		loadPanel.add(fileLabel);
		loadTime.setValue(0);
		loadPanel.add(loadTime);
		frame.add(loadPanel,BorderLayout.SOUTH);
		loadTime.setStringPainted(true);
		//loadPanel.add(clearButton);
		loadPanel.add(logBox);
		
		//Menu components for loading, saving, credits, viewing the data and help
        //Create the menu bar
        JMenuBar menuBar = new JMenuBar();
        //Add "File" and "Options" as JMenus to JMenuBar. Setting etched border
        JMenu file = new JMenu("File");
        JMenu options = new JMenu("Options");
        menuBar.add(file);
        menuBar.add(options);
        menuBar.setBorder(new EtchedBorder());
        searchPanel.setBorder(new EtchedBorder());
        //Adding JMenuItems to Options menu
        JMenuItem helpMenuItem = new JMenuItem("Help");
        //helpMenuItem.setAccelerator(KeyStroke.getKeyStroke("control H"));
        JMenuItem creditsMenuItem = new JMenuItem("Credits");
        options.add(helpMenuItem);
        options.add(creditsMenuItem);
        JMenuItem loadMenuItem = new JMenuItem("Load...");
        loadMenuItem.setAccelerator(KeyStroke.getKeyStroke("control O"));
        //file.add(saveMenuItem);
        file.add(loadMenuItem);
        //Adding the menu to the frame
        frame.setJMenuBar(menuBar);
        
		//GUI END
		
		
		//Carrying out a 2D search with uploaded text file and query (query terms are separated by a space)
		class searchPubMed implements ActionListener
		{
			public void actionPerformed(ActionEvent event)
			{
				//Getting what's in the search field and breaking it up to different terms by looking for spaces
				String searchQuery = searchField.getText();
				String[] searchQueryWords=tokenizeSentence(searchQuery);
				//Storing search terms in an array
				searchTerms=searchQueryWords;
				//Printing out the search terms in the console for debugging
				for(int z = 0; z<searchQueryWords.length;z++){
					System.out.print(searchQueryWords[z]+"\t");
				}
				System.out.println("");
				
				//Writing the search terms to an excel file called ReadOut.xls
				try{
					PrintWriter out= new PrintWriter(new BufferedWriter(new FileWriter("ReadOut.xls")));
					out.write("Gene\t");
					for(int z = 0; z<searchTerms.length;z++){
					    out.write(searchTerms[z]+"\t");
				    }
					out.write("\n");
				    out.close();
				}catch(IOException e){
				}
				
				//Calling AnswerWorker class to execute a 2D search on PubMed with search term array and text file terms
				for(int j = 0; j<finalList.size();j++){
					AnswerWorker test = new AnswerWorker();
					//Setting the arrays that AnswerWorker uses to the one from the textfile and search field
					test.setSearchQuery(searchQuery);
					test.setVariants(finalList.get(j));
					exec.execute(test);
				}	
				//Shutting down the answer worker
				exec.shutdown();
			}
		}
		
		//Everytime the calculate button is pressed, the intCalc code runs
		ActionListener listener = new searchPubMed();
		searchButton.addActionListener(listener);
        
        //Loads a variant text file
        class loadFunction implements ActionListener
        {
            public void actionPerformed(ActionEvent event)
            {
            	//loading a text file with search terms separated by line
            	JFileChooser fChoose = new JFileChooser(); 
            	TextFileFilter startFilter = new TextFileFilter();
            	fChoose.addChoosableFileFilter( startFilter );
            	fChoose.setFileFilter( startFilter );
            	fChoose.setDialogTitle("Select a variant file...");
            	int returnVal = fChoose.showOpenDialog( frame );
            	
            	//Printing what file was selected
            	if ( returnVal == JFileChooser.APPROVE_OPTION ) {		
            			textArea.append("The file selected was: " + 
            				fChoose.getSelectedFile().getPath() +"\n\n");
            	}
            	
            	//Text file reader with all the variants
				try {
					BufferedReader f = new BufferedReader(new FileReader(fChoose.getSelectedFile().getPath()));
					//Storing textfile terms into variants array from text file
	        		int index=0;
	        	    String str;
	        	    while ((str = f.readLine()) != null) {
	        	        variants[index]=str;
	        	        index++;
	        	    }
	        	    textFileLines=index;
	        	    index=0; 
	        		for (String s : variants) {  
	        		    if(s!=null){
	        			list.add(s);
	        		    }
	        		}  
	        		
	        		///@@@@@@@@@@@@@@//IMPORTANT FOR PARTITIONING
	        		//Partitions the text file array for multiple threads
	        		finalList = Lists.partition(list,1);
	        		
	        		System.out.println(finalList.toString());
	        		
	        		fileLabel.setText("Text file: "+fChoose.getSelectedFile().getName()+"\t\t");
	        		fileLabel.repaint();

				} catch (FileNotFoundException e) {
				} catch (IOException e) {
					textArea.append("Read error...\n\n");
				}catch (NullPointerException e){
				}
        	
            }
        }
        
        //Every time the load menu item is pressed, the loadFunction code runs
        ActionListener loadListener = new loadFunction();
        loadMenuItem.addActionListener(loadListener);

        //help function!!!!!
        class helpFunction implements ActionListener
        {
            public void actionPerformed(ActionEvent event)
            {
            	textArea.append("1. Load the text file containing gene variants (File>Load..)\n2. Seperate desired search terms by a space in query box\n3. Click Search button\n4. Happiness\n\n");
        	
            }
        }
        //Every time the load menu item is pressed, the loadFunction code runs
        ActionListener helpListener = new helpFunction();
        helpMenuItem.addActionListener(helpListener);
        
        //help function!!!!!
        class creditFunction implements ActionListener
        {
            public void actionPerformed(ActionEvent event)
            {
            	textArea.append("Created by Bruce Gao under the request of Dr. Richard Wilson. University of Calgary. All rights reserved 2012 \nEmail: bmgao@ucalgary.ca\n\n");
        	
            }
        }
        //Every time the load menu item is pressed, the loadFunction code runs
        ActionListener creditListener = new creditFunction();
        creditsMenuItem.addActionListener(creditListener);
        
        //clear function!!!!!
        class clearFunction implements ActionListener
        {
            public void actionPerformed(ActionEvent event)
            {
            	textArea.setText("");
            	textArea.repaint();
        		fileLabel.setText("Text file: none\t\t");
        		fileLabel.repaint();
        		for(int h = 0; h<variants.length;h++){
        			variants[h]=null;
        		}
            }
        }
        //Every time the load menu item is pressed, the clearFunction code runs
        ActionListener clearListener = new clearFunction();
       // clearButton.addActionListener(clearListener);
		
		//Adjusting size of the frame, making it able to be closed with the x button, giving it a title and making the frame visible
		frame.setSize(frameWidth,frameHeight);
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		frame.setTitle("PubMed Search Engine by Bruce Gao. University of Calgary 2012");
		frame.setVisible(true);
		

    }
    public static String[] tokenizeSentence(String str) {
        StringTokenizer tokenizer = new StringTokenizer(str, " ");
        List tempList = new ArrayList();
        while (tokenizer.hasMoreElements()) {
            tempList.add(tokenizer.nextElement());
        }
        String[] ret = new String[tempList.size()];
        tempList.toArray(ret);
        return ret;
    }
    
	public static synchronized void print(Variant rand) {
		System.out.print(rand.getName()+"\t");
		for(int j = 0; j<rand.getPhenotypes().length;j++){
			System.out.print(rand.getPhenotypes()[j]+"\t");
		}
		System.out.println("");
		try {
			//String fileSavePath="/Users/bruceGao/Desktop/workspace/ReadOut.txt";
			PrintWriter out= new PrintWriter(new BufferedWriter(new FileWriter("ReadOut.xls",true)));
			
			out.write(rand.getName()+"\t");
			for(int z = 0; z<rand.getPhenotypes().length;z++){
				out.write(rand.getPhenotypes()[z]+"\t");
			}
			out.write("\n");
			    //System.out.println(key + " " + value);  
				//textArea.append("\n"+key + " " + value+"\n");
		    out.close();
			

			} 
		catch (IOException e) {
		}
		
	}
  
	private static String[] searchTerms;
	
    public static int textFileLines;
    public static final JProgressBar loadTime = new JProgressBar(0,100);
    
    private static int counter;
    public final JTextArea textArea = new JTextArea(10,30);
    private static List<String> list = new ArrayList<String>();
    private static List<List<String>> finalList; 
    private final static String[] variants = new String[10000];
    //@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@
    public static int threadNumber = 50;
    //THE THREAD POOL IMPORTANT TOO
    private final static ExecutorService exec = Executors.newFixedThreadPool(threadNumber);
    
}

