import java.util.Random;

public class DList<Generic> {
      
      public class GenericNode {
        public GenericNode prev;
        public Generic item;
        public GenericNode next;

          public GenericNode(GenericNode p, Generic i, GenericNode n) {
            prev = p;
            item = i;
            next = n;
          }
      }

      public class SentinelNode extends GenericNode{
        public GenericNode prev;
        public int item;
        public GenericNode next;

          public SentinelNode(GenericNode p, int i, GenericNode n) {
            super(null, null, null);
            prev = p;
            item = i;
            next = n;
          }
      }

      private final int SENTINEL_VALUE = -49291919;
      private SentinelNode sentinel;
      private SentinelNode back_sentinel;
      private int size;

      public DList(Generic x) {
        sentinel = createSentinel();
        sentinel.next = new GenericNode(sentinel, x, null);
        back_sentinel = new SentinelNode(sentinel.next, SENTINEL_VALUE, null);
        size = 1;
      }

      public DList(GenericNode g) {
        sentinel = createSentinel();
        g.prev = sentinel;
        sentinel.next = new GenericNode(sentinel, g.item, g.next);
        GenericNode p = sentinel.next;
        while(p.next != null) {
          p = p.next;
        }
        back_sentinel = (SentinelNode)p;
      }

      public DList() {
        sentinel = createSentinel();
        back_sentinel = new SentinelNode(sentinel, SENTINEL_VALUE, null);
        size = 0;
      }
      
      public void insertFront(Generic x) {
        GenericNode oldActualFrontNode = sentinel.next;
        sentinel.next = new GenericNode(sentinel, x, oldActualFrontNode);
        sentinel.next.next.prev = sentinel.next;
        size += 1;
      }

      public void insertBack(Generic x) {
        GenericNode oldActualBackNode = back_sentinel.prev;
        back_sentinel.prev = new GenericNode(oldActualBackNode, x, back_sentinel);
        back_sentinel.prev.prev.next = back_sentinel.prev;
        size += 1;
      }

      public void removeFront() {
        if(size == 0)
          throw new IllegalStateException("Can't remove elements from an empty DList");
        else {
          sentinel.next = sentinel.next.next;
          size -= 1;
        }
      }

      public void removeBack() {
        if(size == 0)
          throw new IllegalStateException("Can't remove elements from an empty DList");
        else {
          back_sentinel.prev = back_sentinel.prev.prev;
          size -= 1;
        }
      }

      public Generic getFront() {
        return sentinel.next.item;
      }

      public Generic getFront(SentinelNode s) {
        return s.next.item;
      }

      public GenericNode getFrontNode() {
        return sentinel.next;
      }

      public GenericNode getNext() {
        return sentinel.next.next;
      }

      public GenericNode getNext(GenericNode s) {
        return s.next;
      }

      public Generic getLast() {
        return back_sentinel.prev.item;
      }

      public Generic getLast(SentinelNode b) {
        return b.prev.item;
      }

      public GenericNode getNextLast() {
        return back_sentinel.prev.prev;
      }

      public GenericNode getNextLast(GenericNode b) {
        return b.prev;
      }

      public int size() {
        return size;
      }

      public void insertItem(Generic item, GenericNode g) {
        if(g.next != null) {
          GenericNode oldActualNode = g.next;
          g.next = new GenericNode(g, item, oldActualNode);
          g.next.next.prev = g.next;
          size++;
        }
        else {
          g.next = new GenericNode(g, item, back_sentinel);
          back_sentinel.prev = g.next;
          size++;
        }
      }

      public Generic getItem(int index) {

        if(index >= size() || index < 0)
          throw new IndexOutOfBoundsException(String.format("No element found in index %d.", index));
        else {
          int distToStart = index;
          int distToEnd = size - index - 1;
          if(distToStart <= distToEnd) {
            GenericNode s = sentinel.next;
            while(true) {
              if(index == 0)
                return s.item;
              s = getNext(s);
              index -= 1;
            }
          }
          else {
            GenericNode b = back_sentinel.prev;
            while(true) {
              if(distToEnd == 0)
                return b.item;
              b = getNextLast(b);
              distToEnd -= 1;
            }
          }
        }
      }

      public String toString() {
        String result = "";
        GenericNode p = sentinel.next;
        int index = 0;
        while(p.item != getLast()) {
          result += String.format("[INDEX %1d]|%2s| ~ ", index, p.item);
          p = p.next;
          index += 1;
        }
        result += String.format("[INDEX %1d]|%2s|", index, p.item);
        return result;
      }

      private GenericNode swapNeighbors(GenericNode g) {
          GenericNode temp = g.next;
          g.next = g.prev;
          g.prev = temp;
          return g;
      }

      private void swap(GenericNode g1, GenericNode g2) {
          GenericNode temp = g1;
          g1 = g2;
          g2 = temp;
          g1 = swapNeighbors(g1);
          g2 = swapNeighbors(g2);
      }

      private SentinelNode createSentinel() {
        return new SentinelNode(null, SENTINEL_VALUE, null);
      }

      private SentinelNode createBackSentinel(GenericNode prev) {
        return new SentinelNode(prev, SENTINEL_VALUE, null);
      }

      private GenericNode innerReverse(GenericNode prev, GenericNode rest) {
        if(rest.prev != null) {
          GenericNode newNode = new GenericNode(prev, rest.item, null);
          newNode.next = innerReverse(newNode, getNextLast(rest));
          return newNode;
        }
        else
          return createBackSentinel(prev);
      }

      public void dreverse() {

        if(size == 0 || size == 1)
          return;
        else if(size > 1) {
          SentinelNode newSentinel = createSentinel();
          newSentinel.next = innerReverse(newSentinel, back_sentinel.prev);
          sentinel = newSentinel;
          GenericNode p = sentinel.next;
          while(p.next.next != null) {
            p = p.next;
          }
          back_sentinel.prev = p;
        }
        else
          throw new IllegalStateException("Invalid DList size.");
      }

      public DList reverse() {

        if(size == 0 || size == 1)
          return this;
        else if(size > 1) {
          return new DList<Generic>(innerReverse(null, back_sentinel.prev));
        }
        else
          throw new IllegalStateException("Invalid DList size.");
      }
  
	    public static void main(String[] args) {
        DList<String> d1 = new DList<>("i");
        d1.insertBack("sell");
        d1.insertBack("propane");
        d1.insertBack("and");
        d1.insertBack("propane");
        d1.insertBack("accessories");
        System.out.println(d1.toString());
	    }
	}