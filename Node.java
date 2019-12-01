import java.io.*;

public class Node{

	Node child, parent, leftward, rightward;
	int freq;
	int degree = 0;
	boolean ChildCut = false;	
    String keyword;

	public Node(String keyword, int freq){
		this.keyword = keyword;
		this.freq = freq;        
        this.degree = 0;
        this.ChildCut = false;
        this.leftward = this;
		this.rightward = this;
		this.parent = null;
		this.child = null;
	}

}
