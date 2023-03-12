package project;

class DoublyNode<T>{
	
	T val;
	DoublyNode<T> next,prev;
	
	DoublyNode(T val){
		
		next=prev=null;
		this.val=val;
		
	}
	
}

public class MyDoublyLinkedList<T>{
	
	DoublyNode<T> head,tail;
	
	MyDoublyLinkedList(){
		
		head=tail=null;
		
	}
	
	void insertAtEnd(T val) {
		
		DoublyNode<T> newNode=new DoublyNode<>(val);
		
		if(head==null && tail==null) {
			head=tail=newNode;
			newNode.prev=newNode;
			newNode.next=newNode;
		}
		else {
			
			tail.next=newNode;
			newNode.prev=tail;
			tail=newNode;
			tail.next=head;
			head.prev=tail;
			
		}
		
	}
	
}