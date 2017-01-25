import java.util.Random;
import java.lang.Math;

public class SkipList<Generic extends Comparable> {
      
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

      public class SentinelNode extends GenericNode {
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

      public class Position {
        public int posFlag;

        public Position(int pf) {
          posFlag = pf;
        }
      }

      public class NodePair {
        public GenericNode node0, node1;

        public NodePair(GenericNode n0, GenericNode n1) {
          node0 = n0;
          node1 = n1;
        }
      }

      private final int SENTINEL_VALUE = -49291919;
      private SentinelNode sentinel1, sentinel2, sentinel3, sentinel4;
      private SentinelNode backSentinel1, backSentinel2, backSentinel3, backSentinel4;
      private int size1, size2, size3, size4, index;
      private Random rng = new Random();
      private DList<Position> positions;
      private DList<Integer> indices1, indices2, indices3, indices4;


      public SkipList(Generic x) {
        sentinel1 = initList(x, sentinel1, backSentinel1);
        sentinel2 = initList(x, sentinel2, backSentinel2);
        sentinel3 = initList(x, sentinel3, backSentinel3);
        sentinel4 = initList(x, sentinel4, backSentinel4);
        backSentinel1 = (SentinelNode)sentinel1.next.next;
        backSentinel2 = (SentinelNode)sentinel2.next.next;
        backSentinel3 = (SentinelNode)sentinel3.next.next;
        backSentinel4 = (SentinelNode)sentinel4.next.next;

        index = 0;

        positions = new DList<>(new Position(4));
        indices1 = new DList<>(new Integer(0));

        size1 = size2 = size3 = size4 = 1;
        index++;

      }

      public SkipList(GenericNode g) {
        
      }

      public SkipList() {
        initList(sentinel1, backSentinel1);
        initList(sentinel2, backSentinel2);
        initList(sentinel3, backSentinel3);
        initList(sentinel4, backSentinel4);
        backSentinel1 = (SentinelNode)sentinel1.next;
        backSentinel2 = (SentinelNode)sentinel2.next;
        backSentinel3 = (SentinelNode)sentinel3.next;
        backSentinel4 = (SentinelNode)sentinel4.next;

        positions = new DList<>();

        size1 = size2 = size3 = size4 = 0;
        index = 0;
      }
      
      private SentinelNode initList(Generic x, SentinelNode front, SentinelNode back) {
        front = createSentinel();
        GenericNode mid = new GenericNode(front, x, null);
        back = new SentinelNode(mid, SENTINEL_VALUE, null);
        front.next = mid;
        mid.next = back;
        return front;
      }

      private SentinelNode initList(SentinelNode front, SentinelNode back) {
        front = createSentinel();
        back = new SentinelNode(front, SENTINEL_VALUE, null);
        front.next = back;
        return front;
      }

      private void insertFrontDeep(Generic x, SentinelNode front) {
          GenericNode oldActualFrontNode = front.next;
          front.next = new GenericNode(front, x, oldActualFrontNode);
          if(front.next.next != null)
            front.next.next.prev = front.next;
      }

      public void insertFront(Generic x) {
        insertFrontDeep(x, sentinel1);

        double luck = rng.nextInt(800);
        int fun = 1;

        if(luck < 400) {
          insertFrontDeep(x, sentinel2);
          fun++;
          size2++;
        }
        if(luck < 200) {
          insertFrontDeep(x, sentinel3);
          fun++;
          size3++;
        }
        if(luck < 100) {
          insertFrontDeep(x, sentinel4);
          fun++;
          size4++;
        }
        size1 += 1;
        positions.insertFront(new Position(fun));
        index++;
      }

      private void insertBackDeep(Generic x, SentinelNode back, int layer) {
          GenericNode p = getLayer(layer).next;
          for(int i = size(layer)-1; i > 0; i++)
            if(p.next.item != null)
              p = p.next;
          p.next = new GenericNode(p, x, p.next);
          getLayer(-layer).prev = p.next;
      }

      public void insertBack(Generic x) {

        double luck = rng.nextInt(800);
        int fun = 1;

        insertBackDeep(x, backSentinel1, 1);


        if(luck < 400) {
          insertBackDeep(x, backSentinel2, 2);
          fun++;
          size2++;
        }
        if(luck < 200) {
          insertBackDeep(x, backSentinel3, 3);
          fun++;
          size3++;
        }
        if(luck < 100) {
          insertBackDeep(x, backSentinel4, 4);
          fun++;
          size4++;
        }
        size1 += 1;
        positions.insertBack(new Position(fun));
        index++;
      }

      public void removeFront() {
        if(size() == 0)
          throw new IllegalArgumentException("Can't remove elements from an empty DList");
        else {
          sentinel1.next = sentinel1.next.next;

          Position firstPos = positions.getItem(0);
          if(firstPos.posFlag > 1) {
            sentinel2.next = sentinel2.next.next;
            size2--;
          }
          if(firstPos.posFlag > 2) {
            sentinel3.next = sentinel3.next.next;
            size3--;
          }
          if(firstPos.posFlag > 3) {
            sentinel4.next = sentinel4.next.next;
            size4--;
          }
          size1--;
          positions.removeFront();
        }
      }

      public void removeBack() {
        if(size() == 0)
          throw new IllegalArgumentException("Can't remove elements from an empty DList");
        else {
          backSentinel1.prev = backSentinel1.prev.prev;

          Position lastPos = positions.getItem(positions.size() - 1);
          if(lastPos.posFlag > 1) {
            backSentinel2.prev = backSentinel2.prev.prev;
            size2--;
          }
          if(lastPos.posFlag > 2) {
            backSentinel3.prev = backSentinel3.prev.prev;
            size3--;
          }
          if(lastPos.posFlag > 3) {
            backSentinel4.prev = backSentinel4.prev.prev;
            size4--;
          }
          size1--;
          positions.removeBack();
        }
      }

      public Generic getFront() {
        return sentinel1.next.item;
      }

      public Generic getFront(SentinelNode s) {
        return s.next.item;
      }

      public GenericNode getFrontNode() {
        return sentinel1.next;
      }

      public GenericNode getNext() {
        return sentinel1.next.next;
      }

      public GenericNode getNext(GenericNode s) {
        return s.next;
      }

      public Generic getLast() {
        return backSentinel1.prev.item;
      }

      public Generic getLast(SentinelNode b) {
        return b.prev.item;
      }

      public GenericNode getNextLast() {
        return backSentinel1.prev.prev;
      }

      public GenericNode getNextLast(GenericNode b) {
        return b.prev;
      }

      public int size(int layer) {
        switch (layer) {
          case 1: return size1;
          case 2: return size2;
          case 3: return size3;
          case 4: return size4;
          default: throw new IllegalArgumentException("Specified number is not a valid layer.");
        }
      }

      public int size() {
        return size1;
      }

      private void insertItemDeeper(Generic item, GenericNode g, int layer) {
        GenericNode oldNode = g.next;
        g.next = new GenericNode(g, item, oldNode);
        g.next.next = new GenericNode(g.next, oldNode.item, oldNode.next);
        if(g.next.next.item == null)
          getLayer(-layer).prev = g.next;
      }

      private void insertItemDeep(Generic item, GenericNode g) {

        if(greaterThan(g.item, item)) {
          insertFront(item);
        }
        else {
          if(getIndex(g.item) == size(1) - 1) {
            insertBack(item);
          }
          else {
            insertItemDeeper(item, g, 1);
            int index = getIndex(g.item, 1);
            int luck = rng.nextInt(800);
            int newPosFlag = 1;
            Position position = positions.getItem(index);
            size1++;

            for(int n = 2; n < 5; n++) {
              if(luck < 1600 / Math.pow(2, n) && position.posFlag >= n) {
                insertItemDeeper(item, getNode(getIndex(g.item, n), n), n);
                newPosFlag++;
                if(n == 2)
                  size2++;
                if(n == 3)
                  size3++;
                if(n == 4)
                  size4++;
              }
            }

            DList<Position>.GenericNode p = positions.getFrontNode();
            for(int k = index; k > 0; k--) {
              p = p.next;
            }
            positions.insertItem(new Position(newPosFlag), p);
          }
        }
      }

      public void insertItem(Generic item) {

        if(size(1) == 0) {
          insertFront(item);
        }
        else {
          int layer = 4;
          GenericNode p = sentinel4.next;

          while(layer > 0) {
            boolean readyToJump = false;
            
            if(p.next.item == null)
              readyToJump = true;
            if(lessThan(item, p.item))
              readyToJump = true;
            if(p.item == item)
              return;
            
            while(!readyToJump) {
              // System.out.println("Inserting: " + item + ", currently on layer " + layer + " and object " + p.item);
              
              if(greaterThan(item, p.item)) {
                
                if(p.next.next == null)
                  readyToJump = true; 
                
                else if(lessThan(item, p.next.item))
                  readyToJump = true;
                
                else {
                  p = p.next;
                  if(p.next == null)
                    readyToJump = true; 
                  }
                }
              
              else if(equalsTo(item, p.item)) {
                return;
              }
              
              else {
                if(p.prev.prev == null)
                  readyToJump = true;
                else
                  p = p.prev;
              }
            }
            if(layer > 1) {
              p = jumpNodes(layer, p.item);
              // System.out.println("Jumped from layer " + layer + " to " + (layer-1));
            }
            layer--;
          }
          insertItemDeep(item, p);
          printString();
        }

      }

      public Generic getItem(int index) {

        if(index >= size() || index < 0)
          throw new IndexOutOfBoundsException(String.format("No element found in index %d.", index));
        else {
          int distToStart = index;
          int distToEnd = size() - index - 1;
          if(distToStart <= distToEnd) {
            GenericNode s = sentinel1.next;
            while(true) {
              if(index == 0)
                return s.item;
              s = getNext(s);
              index -= 1;
            }
          }
          else {
            GenericNode b = backSentinel1.prev;
            while(true) {
              if(distToEnd == 0)
                return b.item;
              b = getNextLast(b);
              distToEnd -= 1;
            }
          }
        }
      }

      public Generic getItem(int index, int layer) {

        if(index >= size() || index < 0)
          throw new IndexOutOfBoundsException(String.format("No element found in index %d.", index));
        else {
          int distToStart = index;
          int distToEnd = size() - index - 1;
          if(distToStart <= distToEnd) {
            GenericNode s = getLayer(layer).next;
            while(true) {
              if(index == 0)
                return s.item;
              s = getNext(s);
              index -= 1;
            }
          }
          else {
            GenericNode b = getLayer(-layer).prev;
            while(true) {
              if(distToEnd == 0)
                return b.item;
              b = getNextLast(b);
              distToEnd -= 1;
            }
          }
        }
      }

      private SentinelNode getLayer(int n) {
        switch (n) {
          case 1: return sentinel1;
          case 2: return sentinel2;
          case 3: return sentinel3;
          case 4: return sentinel4;
          case -1: return backSentinel1;
          case -2: return backSentinel2;
          case -3: return backSentinel3;
          case -4: return backSentinel4;
          default: throw new IllegalArgumentException("Specified int does not correspond to any layer.");
        }
      }

      public GenericNode getNode(int index, int layer) {

        if(index >= size(layer) || index < 0)
          return getLayer(-layer).prev;
        else {
          // System.out.println("=====STARTING NODE RETRIEVAL=====");
          int distToStart = index;
          int distToEnd = size(layer) - index - 1;
          if(distToStart <= distToEnd) {
            GenericNode s = getLayer(layer).next;
            while(true) {
              // System.out.println("s " + s.item);
              if(index == 0)
                return s;
              s = getNext(s);
              index -= 1;
            }
          }
          else {
            GenericNode b = getLayer(-layer).prev;
            while(true) {
              // System.out.println("b " + b.item);
              if(distToEnd == 0)
                return b;
              b = getNextLast(b);
              distToEnd -= 1;
            }
          }
        }
      }

      public DList<Integer> getIndexLayer(int n) {
        switch (n) {
          case 1: return indices1;
          case 2: return indices2;
          case 3: return indices3;
          case 4: return indices4;
          default: throw new IllegalArgumentException("Specified int does not correspond to any index layer.");
        }
      }

      public int getIndex(Generic item) {
        if(size() <= 0)
          throw new IllegalArgumentException(String.format("Item %s does not exist in list.", item));

        int index = 0;
        GenericNode p = new GenericNode(null, sentinel1.next.item, sentinel1.next.next);
        while(p != null) {
          if(item.compareTo(p.item) == 0)
            return index;
          index++;
          p = p.next;
        }
        throw new IllegalArgumentException(String.format("Item %s does not exist in list.", item));
      }

      public int getIndex(Generic item, int layer) {
        if(size(layer) <= 0)
          throw new IllegalArgumentException(String.format("Item %s does not exist in list.", item));

        boolean isUnder = false;
        int index = 0;
        GenericNode p = new GenericNode(null, getLayer(layer).next.item, getLayer(layer).next.next);
        while(p != null) {
          if(p.item == null)
            break;
          if(lessThan(item, p.item))
            isUnder = true;
          if(equalsTo(item, p.item))
            return index;
          if(greaterThan(item, p.item) && isUnder)
            return index-1;
          index++;
          p = p.next;
        }
        return index;
      }

      private GenericNode jumpNodes(int upperLayerNum, Generic item) {

        if(upperLayerNum <= 1 || upperLayerNum > 4)
          throw new IllegalArgumentException(String.format("Can't jump from layer %d.", upperLayerNum));

        return getNode(getIndex(item, upperLayerNum-1), upperLayerNum-1);

      }

      private boolean lessThan(Generic item1, Generic item2) {
        return item1.compareTo(item2) < 0;
      }

      private boolean greaterThan(Generic item1, Generic item2) {
        return item1.compareTo(item2) > 0;
      }

      private boolean equalsTo(Generic item1, Generic item2) {
        return item1.compareTo(item2) == 0;
      }

      public Generic getItem(Generic item) {

        for(int n = 1; n < 5; n++)
          heapSort(n);

        if(size() == 0)
          throw new IndexOutOfBoundsException("Cannot retrieve items from empty list!");
        if(item == sentinel1.next)
          return item;
        
        int layer = 4;

        while(layer > 0) {
          boolean readyToJump = false;
          GenericNode p = new GenericNode(getLayer(layer), getLayer(layer).next.item, getLayer(layer).next.next);
          
          if(p.next == null)
            readyToJump = true;
          if(lessThan(item, p.item))
            readyToJump = true;
          
          while(!readyToJump) {
            System.out.println("Searching for: " + item + ", currently on layer " + layer + " and object " + p.item);
            if(greaterThan(item, p.item)) {
              if(p.next.item == null)
                  readyToJump = true; 
              else if(lessThan(item, p.next.item))
                readyToJump = true;
              else {
                p = p.next;
                if(p.next == null)
                  readyToJump = true; 
                }
              }
            else if(equalsTo(item, p.item))
              return p.item;
            else {
              p = p.prev;
            }
          }
          if(layer > 1)
            p = jumpNodes(layer, p.item);
          layer--;
        }

        throw new IllegalArgumentException(String.format("Item %s was not found in list.", item));
      }

      public GenericNode getNode(int index) {

        if(index >= size() || index < 0)
          throw new IndexOutOfBoundsException(String.format("No element found in index %d.", index));
        else {
          int distToStart = index;
          int distToEnd = size() - index - 1;
          if(distToStart <= distToEnd) {
            GenericNode s = sentinel1.next;
            while(true) {
              if(index == 0)
                return s;
              s = getNext(s);
              index -= 1;
            }
          }
          else {
            GenericNode b = backSentinel1.prev;
            while(true) {
              if(distToEnd == 0)
                return b;
              b = getNextLast(b);
              distToEnd -= 1;
            }
          }
        }
      }

      public String toString() {
        return "=====SKIPLIST START!=====\n" + 
        String.format("Layer 1: %1s \n=====\nLayer 2: %2s \n=====\nLayer 3: %3s \n=====\nLayer 4: %4s", toString(1), toString(2), toString(3), toString(4));
      }

      public String toString(int layer) {
        String result = "";
        GenericNode p = getLayer(layer).next;
        int index = 0;
        while(p.next.item != null) {
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

      public NodePair swapItems(GenericNode node0, GenericNode node1) {
        Generic temp = node0.item;
        node0.item = node1.item;
        node1.item = temp;
        return new NodePair(node0, node1);
      }

      private void heapify(SkipList list, int index, int layer) {

        if(list.size() <= 1 || index >= list.size() || index <= 0)
          return;

        if(index % 2 == 0) {
          GenericNode child2 = list.getNode(index, layer);
          GenericNode child1 = list.getNode(index - 1, layer);
          GenericNode parent = list.getNode(index/2 - 1, layer);
          if(greaterThan(child1.item, parent.item)) {
            NodePair newNodes = swapItems(child1, parent);
            child1 = newNodes.node0;
            parent = newNodes.node1;
          }
          if(greaterThan(child2.item, parent.item)) {
            NodePair newNodes = swapItems(child2, parent);
            child2 = newNodes.node0;
            parent = newNodes.node1;
          }
          if(greaterThan(child1.item, child2.item)) {
            NodePair newNodes = swapItems(child2, child1);
            child2 = newNodes.node0;
            child1 = newNodes.node1;
          }
        }
        else {
          GenericNode child1 = list.getNode(index, layer);
          GenericNode parent = list.getNode((index - 1)/2, layer);
          if(greaterThan(child1.item, parent.item)) {
            NodePair newNodes = swapItems(child1, parent);
            child1 = newNodes.node0;
            parent = newNodes.node1;
          }
          if(index < list.size(layer) - 1) {
            GenericNode child2 = list.getNode(index + 1, layer);
            if(greaterThan(child1.item, parent.item)) {
              NodePair newNodes = swapItems(child2, parent);
              child2 = newNodes.node0;
              parent = newNodes.node1;
            }
            if(greaterThan(child1.item, child2.item)) {
              NodePair newNodes = swapItems(child2, child1);
              child2 = newNodes.node0;
              child1 = newNodes.node1;
            }
          }
        }
      }

      private void heapifyList(SkipList list, int layer) {

        if(list.size(layer) <= 1)
          return;

        int index = list.size(layer) - 1;

        while(index > 0) {
          heapify(list, index, layer);
          index -= 2;
        }
      }

      public void printString() {
        System.out.println(this.toString());
      }

      public void heapSort(int layer) {

        if(size(layer) <= 1)
          return;

        heapifyList(this, layer);
        SentinelNode sent = getLayer(layer);

        int range = size(layer) - 1;
        int k = 0;

        for(;range > 0;range--) {
          NodePair newNodes = swapItems(sent.next, getNode(range, layer));
          sent.next = newNodes.node0;
          k = range;
          GenericNode p = new GenericNode(sent.prev, sent.next.item, sent.next);
          while(k > -1) {
            p = p.next;
            k--;
          }
          p = newNodes.node1;
          for(int n = range - 2; n >= 1; n--) {
            heapify(this, n, layer);
          }
        }
      }
  
      public void heapSort() {
        for(int n = 1; n < 5; n++)
          heapSort(n);
      }

	    public static void main(String[] args) {
        SkipList<Integer> s = new SkipList<Integer>(0);
        s.printString();
        s.insertItem(0);
        s.insertItem(1);
        s.insertItem(1);
        s.insertItem(3);
        s.insertItem(2);
        s.insertItem(-1);
        s.insertItem(5);
        s.insertItem(-6);
	    }
	}