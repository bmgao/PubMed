package test;

import javax.swing.SwingWorker;

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

public class AnswerWorker extends SwingWorker<Void,Void>
	{
	        protected Void doInBackground() throws Exception
	        {
	        	//Counter to propagate through the list carrying variants
	        	int counter =0;
	        	//Dividing the search query into seperate words to search
				String[] searchQueryWords=tokenizeSentence(searchQuery);
				searchTerms=searchQueryWords;
				
				//While loop to search all text file terms with all search query terms
				while(variants.get(counter)!=null){
					//single search word with a variant
					int[] hitsArray = new int[searchQueryWords.length];
					//Loop to search through all search query words for a text file term
					for(int h =0;h<searchQueryWords.length;h++){
						String variantSearch = variants.get(counter);
						variantSearch = variantSearch.replaceAll(" ","%20");
						System.out.println("SEARCH TERM: "+variantSearch);
						//html string to search on PubMed formulates from the text file and the search query
						String html = ("http://www.ncbi.nlm.nih.gov/pubmed?term="+searchQueryWords[h]+"%20"+variantSearch );
						System.out.println("ANSWER WORKER: "+html);
						//Using jsoup to open the source code of the html address and take information
						try {
							Document doc = Jsoup.connect(html).get();
							//Nothing is returned so store 0 in the hits array
							if(!doc.select("ul.messages").first().text().equalsIgnoreCase("")){
								String resultNumber="0";
								hitsArray[h]=Integer.parseInt(resultNumber);
							}
							//Otherwise store how many hits that combination returned
							else{
								String searchResults = doc.select("div.title_and_pager").first().text();
								//Possible error?
								String resultNumber = searchResults.substring(searchResults.lastIndexOf(" ")+1);
								if(resultNumber.equalsIgnoreCase("")){
									resultNumber="0";
								}
								hitsArray[h]=Integer.parseInt(resultNumber);
							}
							
						} catch (IOException e) {
							try {
								Document doc = Jsoup.connect(html).get();
								if(!doc.select("ul.messages").first().text().equalsIgnoreCase("")){
									String resultNumber="0";
									hitsArray[h]=Integer.parseInt(resultNumber);
								}
								else{
									String searchResults = doc.select("div.title_and_pager").first().text();
									//Possible error?
									String resultNumber = searchResults.substring(searchResults.lastIndexOf(" ")+1);
									if(resultNumber.equalsIgnoreCase("")){
										resultNumber="0";
									}
									hitsArray[h]=Integer.parseInt(resultNumber);
								}
								
							} catch (IOException c) {
								try {
									Document doc = Jsoup.connect(html).get();
									if(!doc.select("ul.messages").first().text().equalsIgnoreCase("")){
										String resultNumber="0";
										hitsArray[h]=Integer.parseInt(resultNumber);
									}
									else{
										String searchResults = doc.select("div.title_and_pager").first().text();
										//Possible error?
										String resultNumber = searchResults.substring(searchResults.lastIndexOf(" ")+1);
										if(resultNumber.equalsIgnoreCase("")){
											resultNumber="0";
										}
										hitsArray[h]=Integer.parseInt(resultNumber);
									}
									
								} catch (IOException g) {
									hitsArray[h]=-1;
								}
								catch(NullPointerException g){
									hitsArray[h]=1;
								}
							}
							catch(NullPointerException c){
								hitsArray[h]=1;
							}
						}
						catch(NullPointerException e){
							hitsArray[h]=1;
						}
					}
					//Storing information in a variant object
					Variant rand = new Variant(variants.get(counter),hitsArray);
					if(searchQueryWords.length>1){
					String html = ("http://www.ncbi.nlm.nih.gov/pubmed?term="+variants.get(counter)+"%20");
					//Do the total search....
					for(int h =0;h<searchQueryWords.length;h++){
						html = html+(searchQueryWords[h]+"%20");
					}
					
					try {
							Document doc = Jsoup.connect(html).get();
							if(!doc.select("ul.messages").first().text().equalsIgnoreCase("")){
								rand.setHits(0);
							}
							else{
								String searchResults = doc.select("div.title_and_pager").first().text();
								String resultNumber = searchResults.substring(searchResults.lastIndexOf(" ")+1);
								if(resultNumber.equalsIgnoreCase("")){
									rand.setHits(0);
								}
								else{
									rand.setHits(Integer.parseInt(resultNumber));
								}
							}

						} catch (IOException e) {
							try {
								Document doc = Jsoup.connect(html).get();
								if(!doc.select("ul.messages").first().text().equalsIgnoreCase("")){
									rand.setHits(0);
								}
								else{
									String searchResults = doc.select("div.title_and_pager").first().text();
									String resultNumber = searchResults.substring(searchResults.lastIndexOf(" ")+1);
									if(resultNumber.equalsIgnoreCase("")){
										rand.setHits(0);
									}
									else{
										rand.setHits(Integer.parseInt(resultNumber));
									}
								}

							} catch (IOException z) {
								try {
									Document doc = Jsoup.connect(html).get();
									if(!doc.select("ul.messages").first().text().equalsIgnoreCase("")){
										rand.setHits(0);
									}
									else{
										String searchResults = doc.select("div.title_and_pager").first().text();
										String resultNumber = searchResults.substring(searchResults.lastIndexOf(" ")+1);
										if(resultNumber.equalsIgnoreCase("")){
											rand.setHits(0);
										}
										else{
											rand.setHits(Integer.parseInt(resultNumber));
										}
									}

								} catch (IOException w) {
									rand.setHits(-1);
								}
								catch(NullPointerException w){
									rand.setHits(1);
								}
							}
							catch(NullPointerException z){
								rand.setHits(1);
							}
						}
						catch(NullPointerException e){
							rand.setHits(1);
						}
					}
					else{
						try{
							rand.setHits(rand.getPhenotypes()[0]);
						}
						catch(ArrayIndexOutOfBoundsException p){
							rand.setHits(0);
						}
					}
					
					SourceReader.print(rand);

					counter++;
					int loadNumber = 100*counter*SourceReader.threadNumber/SourceReader.textFileLines;
					System.out.println(SourceReader.threadNumber+" "+SourceReader.textFileLines+" "+counter);
					SourceReader.loadTime.setValue(loadNumber);
					SourceReader.loadTime.repaint();
					
									
				}

	                return null;
	        }

	        protected void done()
	        {
	        	
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
	        
	        private static String[] searchTerms;
	        private String searchQuery;
	        
	        public String getSearchQuery() {
				return searchQuery;
			}

			public void setSearchQuery(String searchQuery) {
				this.searchQuery = searchQuery;
			}

			public List<String> getVariants() {
				return variants;
			}

			public void setVariants(List<String> list) {
				this.variants = list;
			}
			

			private List<String> variants;
			private final ArrayList sortList = new ArrayList();
	
}
