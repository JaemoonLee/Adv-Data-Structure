import java.io.*;
import java.util.*;
import java.util.regex.Pattern;
import java.util.regex.Matcher;

public class keywordcounter{
	
	public static void main(String[] args){
		String fileName = args[0];
		String CurrentLine;
				
		String KeywordMatching = "((\\$)([a-z]+)(\\s)(\\d+))";      //pattern for strings of type "$facebook 5"
		String QueryMatching = "(\\d+)";                              //pattern for strings of type "5"
			
		Pattern p1 = Pattern.compile(KeywordMatching);
		Pattern p2 = Pattern.compile(QueryMatching); 

		Map<String, Node> hm = new HashMap<String, Node>();
		MaxFibonacciHeap maxfib = new MaxFibonacciHeap();

		PrintWriter writer = null;
		
		try{
			File f = new File(fileName);
			FileReader fr = new FileReader(f);
			BufferedReader br = new BufferedReader(fr);
			
			writer = new PrintWriter("output_file.txt", "UTF-8");

			while (((CurrentLine = br.readLine()).compareToIgnoreCase("stop"))!=0)
			{
				Matcher m1 = p1.matcher(CurrentLine);
				Matcher m2 = p2.matcher(CurrentLine);
				
				if((m1.find())){                                    //if matcher finds a pattern for strings "$facebook 5"
					String word = m1.group(3);
					int key = Integer.parseInt(m1.group(5));

					if(!(hm.containsKey(word)))                     //if there is no same keyword in the hash map, then add to the MaxFib
                    {
						Node n1 = new Node(word,key);
						maxfib.insert(n1); 
						hm.put(word, n1);
					}
					else{                                           //if there is same keyword in the hash map, increase its freq
						Node n = hm.get(word);
						maxfib.increaseKey(n,key);
					}
				}
				else if(!m1.find() && m2.find()){                                 //if matcher finds pattern for strings which are just integer n withouth $,
					int query = Integer.parseInt(m2.group(1));                    //then n most frequently inserted keyword will be written in output file
					Queue<Node> q= new LinkedList<Node>();

					for(int i = 0; i < query; i++)
					{
						Node maxValue = maxfib.removeMax();
						hm.remove(maxValue.keyword);
						Node n1 = new Node(maxValue.keyword, maxValue.freq);
						q.add(n1);                                               //store removed nodes in  the queue. the nodes will be inserted back to MaxFib in that order
						if(i==query-1) 
						{
                            writer.print(n1.keyword);
						}
						else {
                            writer.print(n1.keyword+", ");
						}
					}
					
					writer.println();

					while(q.peek()!=null){                                       //insert back to MaxFib in the queue
						Node n = q.poll();
						maxfib.insert(n);
						hm.put(n.keyword,n);
					}
				}
			}
			br.close();
		}
		catch(Exception e){
			e.printStackTrace();
		}
		finally{
			if(writer!=null){
				try{
					writer.close();
				}
				catch(Exception i){
					i.printStackTrace();
				}
			}
		}
	}
}

