public class SList {
      
      public class IntNode {
        public int item;
        public IntNode next;

          public IntNode(int h, IntNode t) {
            item = h;
            next = t;
          }
      }

      private IntNode sentinel;
      private int size;

      public SList(int x) {
        sentinel = new IntNode(-49291919, null);
        sentinel.next = new IntNode(x, null);
        size = 1;
      }

      public SList() {
        sentinel = new IntNode(-49291919, null);
        size = 0;
      }
      
      public void insertFront(int x) {
        IntNode oldActualFrontNode = sentinel.next;
        sentinel.next = new IntNode(x, oldActualFrontNode);
        size += 1;
      }

      public void insertBack(int x) {
        IntNode p = sentinel;
        
        while(p.next != null) {
          p = p.next;
        }

        p.next = new IntNode(x, null);
        size += 1;
      }

      public void removeFront() {
        if(size == 0)
          throw new IllegalStateException("Can't remove elements from an empty SList");
        else {
          sentinel.next = sentinel.next.next;
          size -= 1;
        }
      }

      private IntNode getRemoveFront(IntNode s) {
          if(size == 0)
            throw new IllegalStateException("Can't remove elements from an empty SList");
          else {
            return s.next;
          }
        }

      public void removeBack() {
        if(size == 0)
          throw new IllegalStateException("Can't remove elements from an empty SList");
        else {
          IntNode p = sentinel;
          
          while(p.next.next != null) {
            p.next = p.next.next;
          }

          p.next = null;
          size -= 1;
        }
      }

      public void removeBack(IntNode s) {
        if(size == 0)
          throw new IllegalStateException("Can't remove elements from an empty SList");
        else {
          IntNode p = s;
          
          while(p.next.next != null) {
            p.next = p.next.next;
          }

          p.next = null;
          size -= 1;
        }
      }

      public int getFront() {
        return sentinel.next.item;
      }

      public int size() {
        return size;
      }

      public int getItem(int index) {

        if(index >= size() || index < 0)
          throw new IndexOutOfBoundsException(String.format("No element found in index %d.", index));
        else {
          IntNode s = sentinel.next;
          while(index >= 0) {
            if(index == 0)
              return s.item;
            else {
              s = getRemoveFront(s);
              index -= 1;
            }
          }
        }
        throw new IllegalStateException("getItem failed.");
      }
  
	    public static void main(String[] args) {
	      SList s1 = new SList(10);
	      s1.insertFront(5);
        s1.insertFront(1);
        s1.insertFront(0);
        System.out.println("size of SList: " + s1.size());
        System.out.println(s1.getItem(0));
        System.out.println(s1.getItem(1));
        System.out.println(s1.getItem(2));
        System.out.println(s1.getItem(3));
        System.out.println(s1.getItem(4));
	    }
	}