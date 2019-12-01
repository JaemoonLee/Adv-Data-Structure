import java.io.*;
import java.util.*;

public class MaxFibonacciHeap{

    Node CurrentMax = null;
    int count = 0;

	public void insert(Node n){
		
		count = count + 1;						      	//increase count by 1. This is number of nodes in the Max Fibonacci Heap
					
		if(CurrentMax==null)							//check whether Heap is empty or not
		{
			CurrentMax = n;
		}		
		else{										//if Heap is not empty, then add node to right of the CurrentMax
			n.leftward = CurrentMax;
			n.rightward = CurrentMax.rightward;
			CurrentMax.rightward = n;
			
			if(n.rightward!=null){						//if the CurrentMax node has a right node, adjust previous pointer of n's right node
				n.rightward.leftward = n;
			}
			
			if(n.rightward==null){						//if CurrentMax do not have a right node, make CurrentMax as right node of n
				n.rightward = CurrentMax;
				CurrentMax.leftward = n;
			}			
			setMax(n);								//update max node
		}
    }
    
	public void increaseKey(Node n, int freq){
		int currentFreq = n.freq;
		
		if(currentFreq > (currentFreq + freq)){			//check if sum of existing freq and passed freq is greater than existing one
			throw new IllegalArgumentException("Not valid freqency");
		} 
		
		n.freq = currentFreq + freq;					//calculate new freq
		
		Node itsParent = n.parent;					//bring parent node's detail
		
		if(itsParent != null){						     //if parent node exists, then check its freq and perfrom further operation
			if(itsParent.freq < n.freq){
				removeFromList(n);				        //If new freq is greater than parent node's freq, remove current node from the current list 
				itsParent.degree = itsParent.degree - 1;	//reduce parent node's degree by 1
				
				if(itsParent.child == n){			     //if child pointer of parent points to n and parent have other child, update itsParent's child pointer to point to next child
					itsParent.child = n.rightward;
				}
				
				if(itsParent.degree == 0){		        	//if parent node have only child node n, set child pointer of parent to null
					itsParent.child = null;
				}
					
				addToRootLevel(n);				       //add n to root level doubly circular list
				
				n.parent = null;				       	//set n's parent pointer to null
				n.ChildCut = false;				        //set Chilcut field of n to false since n is put on the root level list
				cascadingCut(n);					     // call cascading cut operation starting from the previous parents 
			}
		}
		setMax(n);									//update max node
	}

	public void cascadingCut(Node itsParent){
		Node temp = itsParent.parent;

		if(temp != null){
			if(itsParent.ChildCut == false){	     	//Stop operation if ChildCut field of parent is false. This means that parent losts its child for the first time
				itsParent.ChildCut = true;
			}
			else{								    	//continue untill reaching a node whose ChildCut is false
				removeFromList(itsParent);
				temp.degree = temp.degree - 1;
				
				if(temp.child == itsParent){		     	//if itsParent's parent node have other child, update child pointer of temp to point to next child
					temp.child = itsParent.rightward;
				}
				
				if(temp.degree == 0){				     //if itsParent's parent node do not have other child, set child pointer of temp to null
					temp.child = null;
				}
				
				addToRootLevel(itsParent);		    //add itsParent to root level doubly circular list

				itsParent.parent = null;				    //set parent pointer of itsParent to null
				itsParent.ChildCut = false;			    //set ChildCut field of itsparent to false since it is in the root level

				cascadingCut(temp);					    // call cascading cut for its parents
			}
		}
	}

	public Node removeMax(){

		Node max = CurrentMax;

		if(max != null){							// if CurrentMax is not null, then add each of subtree rooted at its child to root level
			Node firstChild = max.child;
			Node right; 
			for(int i = 0; i < max.degree; i++){
				right = firstChild.rightward;
				removeFromList(firstChild);			//adjust left and right pointers of each child
				addToRootLevel(firstChild);		// add child in the root level list

				firstChild.parent = null;			//set parent pointer of child to null
				firstChild.ChildCut = false;		//set ChildCut of firstChild to false since it is in the root level list
				firstChild = right;					//recursively perform operation
			}				

		removeFromList(max);						//adjust left and right pointers of CurrentMax

		if (max == max.rightward) {					//if Max Fibonacci Heap has only one node, set CurrentMax to null	
	            CurrentMax = null;
	    } 
	    else {
	        CurrentMax = CurrentMax.rightward;		//else temporarily set next element as new CurrentMax
	        pairwiseCombine();						//call the pairwise combine operation
	    }
		count -= 1;									//decrement count of each element
		return max;									//return deleted max element
	}
	return null;
}

	public void pairwiseCombine(){

		int len = 100;								         //default len

		List<Node> degreeTable = new ArrayList<Node>(len);

		for(int i = 0; i < len; i++){				         //initiate degree table
			degreeTable.add(null);
		}

		Node tem = CurrentMax;
		int totalNumOfRoot = 0;						//varaiable in order to count number of max trees

		if(tem != null){								//count number of trees if Fibonacci Heap is not empty
			do{
				totalNumOfRoot = totalNumOfRoot +1;
				tem = tem.rightward;
			}while(tem != CurrentMax);
		}

		for(int i = 0 ; i < totalNumOfRoot; i++){

			Node right = tem.rightward;					         //store next node of tem
			int temDegree = tem.degree;					         //bring degree of max node

			for(;;){
				Node x = degreeTable.get(temDegree);	         //if there is an max tree having same degree in degreeTable, then merge		
				if(x == null) break;

				if(tem.freq < x.freq){	
					Node a = x;
					x = tem;
					tem = a;
				}

				removeFromList(x);
				x.parent = tem;						           //combine two trees and make a smaller node as a child of the other
                x.ChildCut = false;				               //set ChildCut field of x to false since x becomes a child

				if(tem.child != null){				           //if max node already have children,  insert node x as a fist child of parent's child list
						x.leftward = tem.child;
           				x.rightward = tem.child.rightward;
           				tem.child.rightward = x;
            			x.rightward.leftward = x;
				}
				else{								            //if max node does not have child, then insert new node as a child				
						tem.child = x;
						x.rightward = x;
						x.leftward = x;
					}

                tem.degree += 1;						//increase degree by 1 since new node has been added
                
				degreeTable.set(temDegree,null);		//remove entry of the degree table
				temDegree = temDegree++;				//proceed to next degree
			}

			degreeTable.set(temDegree,tem);				//store newly computed max tree
			tem = right;
		}

		CurrentMax = null;

		for(Node y: degreeTable){					//add each max tree in the degree table to the root level list
			if(y == null) continue;
			if(CurrentMax != null){
					removeFromList(y);
					addToRootLevel(y);
					setMax(y);
				}
			else{
					CurrentMax = y;
				}
			}	
	}

    public void setMax(Node n){                    //check whether new node has greater freq than that of existing max node or not and update
		if(CurrentMax.freq < n.freq){
			CurrentMax = n;
		}
	}

	public void removeFromList(Node a){            //adjust pointers of left and right nodes
		a.leftward.rightward = a.rightward;
		a.rightward.leftward = a.leftward;
	}

	public void addToRootLevel(Node b){         //add a node at root level list and adjust pointers of current max node, newly added node, and old right node of currnt max node
			Node next = CurrentMax.rightward;
			CurrentMax.rightward = b;
			b.rightward = next;
			next.leftward = b;
			b.leftward = CurrentMax;
	}

	
}
