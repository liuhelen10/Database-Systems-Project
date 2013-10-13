import java.util.*;

public class BST<E> extends AbstractSet<E> {

    protected Entry<E> root;
    protected int size;

    public BST() {
        root = null;
        size = 0;
    }

    // added for testing purpose only
    public Entry<E> getRoot() {
        return root;
    }

    public int size() {
        return size;
    }

    public boolean isEmpty() {
        return size == 0;
    }
    

    public Iterator<E> iterator() {
        return new TreeIterator();
    }

    public boolean contains(Object obj)  {
        return containsAux(root, obj);
    }

    private boolean containsAux(Entry<E> ent, Object obj) {
        if (ent == null) {
            return false;
        }
        else {
            int comp = ((Comparable)obj).compareTo(ent.element);
            if (comp == 0) {
                return true;
            }
            else if (comp < 0) {
                return containsAux(ent.left, obj);
            }
            else {
                return containsAux(ent.right, obj);
            }
        }
    }

    
    public boolean add(E element) {
        if (root == null) {
            root = new Entry<E>(element, null);
            size++;
            return true;
        }
        else {
            return addAux(root, element);
        }
    }

    private boolean addAux(Entry<E> curr, E element) {
        //        System.out.println("In addAux: " + curr.element);
    	Pair eltToAdd = (Pair)element;
    	String eltToAddKey = eltToAdd.getKey();
    	Pair currPair = (Pair)curr.element;
        //int comp = ((Comparable)eltToAddKey).compareTo(currPair.getKey());
    	//int comp = Integer.parseInt(eltToAddKey) - Integer.parseInt(currPair.getKey()); 
    	int comp;
    	try {
    		comp = Integer.parseInt(eltToAddKey) - Integer.parseInt(currPair.getKey());
    	} catch (NumberFormatException e) {
    		comp = ((Comparable)eltToAddKey).compareTo(currPair.getKey());
    	}
    	
    	
        if (comp == 0) {
        	//Pair eltToAdd = (Pair)element;
        	int locationToAdd = eltToAdd.getLocation().get(eltToAdd.getLocation().size() - 1);
        	currPair.getLocation().add(locationToAdd);
            return true;
        }
        else if (comp < 0) {
            if (curr.left == null) {
                curr.left = new Entry<E>(element, curr);
                size++;
                return true;
            }
            else {
                return addAux(curr.left, element);
            }
        }
        else {
            if (curr.right == null) {
                curr.right = new Entry<E>(element, curr);
                size++;
                return true;
            }
            else {
                return addAux(curr.right, element);
            }
        }
        /*
        int comp = "AAA".compareTo("BBB");
        if (comp == 0) {
            System.out.println("same");
        }
        else if (comp < 0) {
            System.out.println("AAA < BBB");
        }
        else {
            System.out.println("AAA > BBB");
        }
        */
    }
  
    // Let n be the number of nodes in the BST, then the averageCaseTime(n)
    // is Theta(log_2 n) and worstCaseTime(n) is Theta(n).
    private Entry<E> findMin (Entry<E> ent) {
        if (ent == null) {
            return null;
        }
        else if (ent.left == null) {
            return ent;
        }
        else {
            return findMin(ent.left);
        }
    }
  
    private Entry<E> findMax (Entry<E> ent) {
        if (ent == null) {
            return null;
        }
        else if (ent.right == null) {
            return ent;
        }
        else {
            return findMax(ent.right);
        }
    }
  
    // Pre: This would normally be called with the root node as
    //      the argument.
    //
    // Let n be the number of nodes in the BST, then the averageCaseTime(n)
    // is Theta(log_2 n) and worstCaseTime(n) is Theta(n).
    // 
    private Entry<E> deleteMin (Entry<E> ent) {
        Entry<E> min = findMin(ent);
        if (min == null) {
            ;
        }
        else if (min == root) {
            root = root.right;
            root.parent = null;
        }
        else if (min == min.parent.right) {
            min.parent.right = min.right;
            if (min.right != null) {
                min.right.parent = min.parent;
            }
        }
        else if (min == min.parent.left) {
            min.parent.left = min.right;
            if (min.right != null) {
                min.right.parent = min.parent;
            }
        }
        else {
            System.out.println("You should not be here in deleteMin....");
        }
        return min;
    }
  
  
    // Pre: This would normally be called with the root node as
    //      the argument.
    // Similar to deleteMin.
    private Entry<E> deleteMax (Entry<E> ent) {
        Entry<E> max = findMax(ent);
        //        System.out.println("max in deleteMax.... " + max);
        if (max == null) {
            //            System.out.println("max in deleteMax is null");
            ;
        }
        else if (max == root) {
            //            System.out.println("max in deleteMax is same as root");
            root = root.left;
            root.parent = null;
        }
        else if (max == max.parent.left) {
            //            System.out.println("20 is left child in deleteMax....");
            max.parent.left = max.left;
            if (max.left != null) {
                max.left.parent = max.parent;
            }
        }
        else if (max == max.parent.right) {
            //            System.out.println("20 is right child in deleteMax....");
            //            System.out.println("20's parent is " + max.parent);
            max.parent.right = max.left;
            if (max.left != null) {
                max.left.parent = max.parent;
            }
        }
        else {
            System.out.println("You should not be here in deleteMax....");
        }
        return max;
    }
  
  
    // Pre: ent is already found to be the min entry
    private Entry<E> deleteFoundMin (Entry<E> ent) {
        if (ent == root) {
            root = root.right;
        }
        else {
            ent.parent.left = ent.right;
        }
        return ent;
    }

    // Pre: ent is already found to be the max entry
    private Entry<E> deleteFoundMax (Entry<E> ent) {
        if (ent == root) {
            root = root.left;
        }
        else {
            ent.parent.right = ent.left;
        }
        return ent;
    }


    // Post: If, before this call, this BST object contained
    //       an element equal to o, then an element equal to o has been
    //       removed from this BST object and true has been
    //       returned.  Otherwise, false has been returned.  The
    //       averageCaseTime(n) is Theta(log_2 n) and worstCaseTime(n)
    //       is Theta(n).
    public boolean remove (Object o) {
        Entry<E> ent = getEntry(o);
        if (ent == null) {
            return false;
        }
        deleteEntry(ent);
        return true;
    }
  
    // Post: the Entry p has been removed from this BST.
    //       It returns the entry just deleted.
    protected Entry<E> deleteEntry(Entry<E> p) {
        size--;
        if (p.left == null && p.right == null) {
            if (p.parent.left == p) {
                deleteFoundMin(p);
            }
            else {
                deleteFoundMax(p);
            }
        }
        else if (p.left == null && p.right != null) {
            Entry<E> min = deleteMin(p.right);
            p.element = min.element;
        }
        else { // (p.left != null && p.right == null) or
               // (p.left != null && p.right != null)
            Entry<E> max = deleteMax(p.left);
            p.element = max.element;
        }
        return p;
    }

    // Post: if there is an Entry whose element is elem, such an
    //       Entry has been returned.  Otherwise, null has been
    //       returned. The averageTime(n) is O(log n) and 
    //       worstTime(n) is O(n).
    public Entry<E> getEntry (Object o)  {
        return getEntryAux(root, o);
    }
  
    private Entry<E> getEntryAux (Entry<E> ent, Object o) {
        Pair pairToGet = (Pair)o;
        String pairToGetKey = pairToGet.getKey();
    	if (ent == null) {
            return null;
        }
        else {
            Pair currPair = (Pair)ent.element;
        	int comp;
        	try {
        		comp = Integer.parseInt(pairToGetKey) - Integer.parseInt(currPair.getKey());
        	} catch (NumberFormatException e) {
        		comp = ((Comparable)pairToGetKey).compareTo(currPair.getKey());
        	}
            if (comp == 0) {
                return ent;
            }
            else if (comp < 0) {
                return getEntryAux(ent.left, o);
            }
            else {
                return getEntryAux(ent.right, o);
            }
        }
    }


    // Post: if e has a successor, that successor Entry has been
    //       returned.  Otherwise, null has been returned.
    public Entry<E> successor(Entry<E> e) {
        if (e == null) {
            return null;
        }
        else if (e.right != null) {
            // successor is leftmost Entry in right subtree of e
            Entry<E> p = e.right;
            while (p.left != null) {
                p = p.left;
            }
            return p;
        }
        else {
            // go up the tree to the left as far as possible, then go up
            // to the right.
            Entry<E> p = e.parent;
            Entry<E> ch = e;
            while (p != null && ch == p.right) {
                ch = p;
                p = p.parent;
            }
            return p;
        }
    }
    
    // Post: if e has a predecessor, that predecessor Entry has been
    //       returned.  Otherwise, null has been returned.
    public Entry<E> predecessor(Entry<E> e) {
        if (e == null) {
            return null;
        }
        else if (e.left != null) {
        	
        	
        	// successor is rightmost Entry in left subtree of e
            Entry<E> p = e.left;
            while (p.right != null) {
                p = p.right;
            }
            return p;
        }
        else {
            // go up the tree to the right as far as possible, then go up
            // to the left.
            Entry<E> p = e.parent;
            Entry<E> ch = e;
            while (p != null && ch == p.left) {
                ch = p;
                p = p.parent;
            }
            return p;
        }
    }

    protected static class Entry<E> {
        E element;
        Entry<E> left = null;
        Entry<E> right = null;
        Entry<E> parent;

        // Post: this Entry has been initialized from element and parent.
        Entry(E element, Entry<E> parent) {
            this.element = element;
            this.parent = parent;
        }
        public String toString() {
            return "" + (Integer)element;
        }
    }
    
    private class TreeIterator implements Iterator<E> {

        private Entry<E> lastReturned = null;
        private Entry<E> next;
    
        // Post: next has been initialized to the smallest
        //       Entry in this BST.
        public TreeIterator() {
            next = root;
            if (next != null) {
                while (next.left != null) {
                    next = next.left;
                }
            }
        }


        // Post: true has been returned if this TreeIterator
        //       is not positioned beyond the end of the
        //       binary search tree.  Otherwise, false
        //       has been returned.
        public boolean hasNext() {
            return next != null;
        }


        // Post: the next element in the binary search tree
        //       has been returned.
        public E next() {
            if (next == null)
                throw new NoSuchElementException();
            lastReturned = next;
            next = successor(next);
            return lastReturned.element;
        }
        
        public Entry<E> nextEntry() {
        	if (next == null)
        		throw new NoSuchElementException();
        	lastReturned = next;
        	next = successor(next);
        	return lastReturned;
        }

        
        // Pre:  the element that was last returned by this
        //       TreeIterator is still in the BinSearchTree. 
        // Post: the element last returned by this TreeIterator
        //       has been removed from the binary search tree.
        public void remove() {
            if (lastReturned == null) {
                throw new IllegalStateException();
            }
            if (lastReturned.left != null && lastReturned.right != null) {
                next = lastReturned;
            }
            BST.this.remove(lastReturned.element);
            //      deleteEntry(lastReturned);
            lastReturned = null;
        }
    }


    // Pictorial representation of a BST, using in order traversal.
    public void display() {
        displayAux(root, 0);
    }
    private void displayAux(Entry<E> ent, int level) {
        if (ent != null) {
            displayAux(ent.left, level + 1);
            for (int i = 0; i <= level; i++) {
                System.out.print("   ");
            }
            System.out.println(ent.element);
            displayAux(ent.right, level + 1);
        }
    }


    public void breadthFirst() {
        System.out.println("In breadthFirst traversal");
        if (root != null) {
            breadthFirstAux(root);
        }
    }
    private void breadthFirstAux(Entry<E> ent) {
        // Use a linked list as a queue...
        LinkedList<Entry> queue = new LinkedList<Entry>();
        Entry<E> front = null;

        queue.add(ent);
        while (queue.size() > 0) {
            front = queue.remove();
            System.out.println(front.element);
            if (front.left != null) {
                queue.add(front.left);
            }
            if (front.right != null) {
                queue.add(front.right);
            }
        }
    }

    public static void main(String[] args) {
        
        /*BST<Integer> t1 = new BST<Integer>();
    
        Integer i1 = new Integer(33);
        Integer i2 = new Integer(11);
        Integer i3 = new Integer(22);
        Integer i4 = new Integer(3);
        Integer i5 = new Integer(5);
        Integer i6 = new Integer(1);
        Integer i7 = new Integer(7);
        Integer i8 = new Integer(12);
        Integer i9 = new Integer(55);
        Integer i10 = new Integer(80);
        System.out.println("Size: " + t1.size());
        t1.add(i1);
        t1.add(i2);

//          t1.add(i3);
//          t1.add(i4);
//          t1.add(i5);
//          t1.add(i6);
//          t1.add(i7);
//          t1.add(i8);
//          t1.add(i9);
//          t1.add(i10);

        System.out.println("Size: " + t1.size());
        
        System.out.println("\nWith an iterator: . . . . . . . . . . .");
        Iterator<Integer> itr = t1.iterator();
        while (itr.hasNext()) {
            System.out.println(itr.next());
        } 
    
        System.out.println("\nTesting contains: . . . . . . . . . . .");
        if (t1.contains(i2))
            System.out.println("Found 11");
        else
            System.out.println("Did not find 11");
    
        if (t1.contains(new Integer(9)))
            System.out.println("Found 9");
        else
            System.out.println("Did not find 9");
    
        t1.add(i3);
        t1.add(i4);
        t1.add(i5);
        t1.add(i6);
        t1.add(i7);
        t1.add(i8);
        t1.add(i9);
        t1.add(i10);
    
        t1.display();
    
        System.out.println("\nTesting remove: . . . . . . . . . . .");
        boolean removed = t1.remove(i1);
        if (removed) {
            System.out.println("removed: " + i1);
        }
        else {
            System.out.println("removed: " + i1 + " is not in the tree");
        }
        
        t1.display();
        t1.breadthFirst(); */       
        

        BST<Integer> t1 = new BST<Integer>();
        int i1 = 33; int i2 = 22; int i3 = 11; int i4 = 48;
        int i5 = 40; int i6 = 30; int i7 = 28;
        t1.add(i1);        t1.add(i2);        t1.add(i3);        t1.add(i4);
        t1.add(i5);        t1.add(i6);        t1.add(i7);        t1.display();

        System.out.println("findMin and findMax test ..............");
        //        Entry<Integer> min = t1.findMin(t1.getRoot());
        Entry<Integer> min = t1.findMin(t1.root);
        //        System.out.println(min);
        System.out.println(min.element);
        //        Entry<Integer> max = t1.findMax(t1.getRoot());
        Entry<Integer> max = t1.findMax(t1.root);
        //        System.out.println(max);
        System.out.println(max.element);
        t1.display();

        System.out.println("deleteFoundMin(11) and deleteFoundMax(48) test ..............");
        t1.deleteFoundMin(min);
        t1.deleteFoundMax(max);
        t1.display();

        System.out.println("Add some more nodes ..............");
        t1.add(45);        t1.add(38);        t1.add(10);        t1.add(20);
        t1.display();

        System.out.println("deleteMin and deleteMax test ..............");
        //        t1.deleteMin(t1.getRoot());
        t1.deleteMin(t1.root);
        //        t1.deleteMax(t1.getRoot());
        t1.deleteMax(t1.root);
        t1.display();

        System.out.println("getEntry test ..............");
        Entry<Integer> gete = t1.getEntry(22);
        //        System.out.println("22: " + gete);
        System.out.println("22: " + gete.element);

        System.out.println("remove test ..............");
        boolean deleted = t1.remove(22);
        System.out.println("   removed 22 (T/F): " + deleted);
        t1.display();

    }
}