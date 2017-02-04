import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.HashMap;

/**
 * The main class for the HashBrown data structure, a two-dimensional, four-way linked node network with
 * intuitive directional traversal and indexing system. Not the most practical of data structures out there, but at
 * least it can graph things or potentially calculate shortest paths on a grid.
 */

public class HashBrown<Generic> {

	interface Function<G> {
		G operation(double x, double y);
	}

	public class Container {

		private final BigDecimal EMPTY_POS = new BigDecimal(3030303015.0);
		private BigDecimal posX, posY;
		private Generic item;
		private Container neighbor_up, neighbor_right, neighbor_down, neighbor_left;
		private final Container empty = null;

		public Container() {
			item = null;
			posX = EMPTY_POS;
			posY = EMPTY_POS;
			neighbor_up = neighbor_right = neighbor_down = neighbor_left = empty;
		}

		public Container(BigDecimal px, BigDecimal py) {
			item = null;
			posX = px;
			posY = py;
			neighbor_up = neighbor_right = neighbor_down = neighbor_left = empty;
		}

		public Container(Generic i, BigDecimal px, BigDecimal py) {
			item = i;
			posX = px;
			posY = py;
			neighbor_up = neighbor_right = neighbor_down = neighbor_left = empty;
		}

		public Container(Generic i, double px, double py) {
			item = i;
			posX = BigDecimal.valueOf(px);
			posY = BigDecimal.valueOf(py);
			neighbor_up = neighbor_right = neighbor_down = neighbor_left = empty;
		}

		public Container(BigDecimal px, BigDecimal py, Container nup, Container nright, Container ndown, Container nleft) {
			item = null;
			posX = px;
			posY = py;
			neighbor_up = initNeighbor(nup);
			neighbor_right = initNeighbor(nright);
			neighbor_down = initNeighbor(ndown);
			neighbor_left = initNeighbor(nleft);
		}

		public Container(double px, double py, Container nup, Container nright, Container ndown, Container nleft) {
			item = null;
            posX = BigDecimal.valueOf(px);
            posY = BigDecimal.valueOf(py);
			neighbor_up = initNeighbor(nup);
			neighbor_right = initNeighbor(nright);
			neighbor_down = initNeighbor(ndown);
			neighbor_left = initNeighbor(nleft);
		}

		public Container(Generic i, BigDecimal px, BigDecimal py, Container nup, Container nright, Container ndown, Container nleft) {
			item = i;
			posX = px;
			posY = py;
			neighbor_up = initNeighbor(nup);
			neighbor_right = initNeighbor(nright);
			neighbor_down = initNeighbor(ndown);
			neighbor_left = initNeighbor(nleft);
		}

		public Container(Generic i, double px, double py, Container nup, Container nright, Container ndown, Container nleft) {
			item = i;
			posX = new BigDecimal(px);
			posY = new BigDecimal(py);
			neighbor_up = initNeighbor(nup);
			neighbor_right = initNeighbor(nright);
			neighbor_down = initNeighbor(ndown);
			neighbor_left = initNeighbor(nleft);
		}

		private Container initNeighbor(Container neighbor) {
			return (neighbor == null) ? empty : neighbor;
		}

		public boolean isEmpty() {
			return item == null;
		}

		public Generic getItem() {
			if (isEmpty()) {
				throw new NullPointerException(String.format("Current container of at location (%1$s, %2$s) " +
						"contains no item.", posX, posY));
			}
			return item;
		}

		public Generic setItem(Generic newItem) {
			if (isEmpty()) {
				item = newItem;
				return null;
			} else {
				Generic prevItem = item;
				item = newItem;
				return prevItem;
			}
		}

		public BigDecimal getPosX() {
			return posX;
		}

		public BigDecimal getPosY() {
			return posY;
		}

		public Container empty() {
			return empty;
		}

		public Container getNeighbor(String dir) {
			switch(dir) {
				case "up":
					return neighbor_up;
				case "right":
					return neighbor_right;
				case "down":
					return neighbor_down;
				case "left":
					return neighbor_left;
				default:
					throw new IllegalArgumentException(String.format("Input %s is not a valid neighbor direction.",
						dir));
			}
		}

		public Container traverse(String dir, int times) {
			if (times <= 0) {
				return this;
			}
			switch (dir) {
				case "up":
					return neighbor_up.traverse(dir, times-1);
				case "right":
					return neighbor_right.traverse(dir, times-1);
				case "down":
					return neighbor_down.traverse(dir, times-1);
				case "left":
					return neighbor_left.traverse(dir, times-1);
				default:
					throw new IllegalArgumentException(String.format("Input %s is not a valid neighbor direction.",
						dir));
			}
		}

		private Container switchOutNeighbor(Container newNeighbor, String dir) {
			Container prevNeighbor;
			prevNeighbor = getNeighbor(dir);
			switch (dir) {
				case "up":
					neighbor_up = newNeighbor;
					break;
				case "right":
					neighbor_right = newNeighbor;
					break;
				case "down":
					neighbor_down = newNeighbor;
					break;
				case "left":
					neighbor_left = newNeighbor;
					break;
                default:
                    throw new IllegalArgumentException(String.format("Input %s is not a valid neighbor direction.",
                            dir));
			}
			return (isEmpty()) ? empty : prevNeighbor;
		}

		public Container setNeighbor(Container newNeighbor, String dir) {
			switch (dir) {
				case "up":
					return switchOutNeighbor(newNeighbor, dir);
				case "right":
					return switchOutNeighbor(newNeighbor, dir);
				case "down":
					return switchOutNeighbor(newNeighbor, dir);
				case "left":
					return switchOutNeighbor(newNeighbor, dir);
				default:
					throw new IllegalArgumentException(String.format("Input %s is not a valid neighbor direction.",
						dir));
			}
		}
	}

	private HashMap<String, Container> map;
	private boolean canReplace = true;
	private double minX, maxX, minY, maxY;
	private double dX, dY;
    private BigDecimal stepX, stepY;

	public HashBrown (boolean cr) {
		map = new HashMap<String, Container>();
		BigDecimal x, y;
		x = y = new BigDecimal(0);
		Container start = new Container(x, y);
		map.put(cPair(x.doubleValue(), y.doubleValue()), start);
		minX = maxX = minY = maxY = 0;
		canReplace = cr;
        stepX = BigDecimal.valueOf(dX);
        stepY = BigDecimal.valueOf(dY);
	}

	public HashBrown(Generic origin, boolean cr) {
		map = new HashMap<String, Container>();
		BigDecimal x, y;
		x = y = new BigDecimal(0);
		Container start = new Container(origin, x, y);
		map.put(cPair(x.doubleValue(), y.doubleValue()), start);
		minX = maxX = minY = maxY = 0;
		canReplace = cr;
		dX = dY = 1;
        stepX = BigDecimal.valueOf(dX);
        stepY = BigDecimal.valueOf(dY);
	}

	public HashBrown(Generic origin) {
		map = new HashMap<String, Container>();
		BigDecimal x, y;
		x = y = new BigDecimal(0);
		Container start = new Container(origin, x, y);
		map.put(cPair(x.doubleValue(), y.doubleValue()), start);
		minX = maxX = minY = maxY = 0;
		dX = dY = 1;
        stepX = BigDecimal.valueOf(dX);
        stepY = BigDecimal.valueOf(dY);
	}

	public HashBrown() {
		map = new HashMap<String, Container>();
		BigDecimal x, y;
		x = y = new BigDecimal(0);
		Container start = new Container(x, y);
		map.put(cPair(x.doubleValue(), y.doubleValue()), start);
		minX = maxX = minY = maxY = 0;
		dX = dY = 1;
        stepX = BigDecimal.valueOf(dX);
        stepY = BigDecimal.valueOf(dY);
	}

	public HashBrown(Generic[][] data, double lowerX, double lowerY) {
		minX = maxX = lowerX;
		minY = maxY = lowerY;
		dX = dY = 1;
        stepX = BigDecimal.valueOf(dX);
        stepY = BigDecimal.valueOf(dY);
		map = new HashMap<String, Container>();
		for (double currY = lowerY; currY < lowerY + data.length; currY++) {
			double iY = currY - lowerY;
			if (iY > 0) {
                assert (data[(int) iY].length == data[(int) (iY - 1)].length);
            }
			for (double currX = lowerX; currX < lowerX + data[0].length; currX++) {
				double iX = currX - lowerX;
				Container currContainer = new Container(data[(int)iY][(int)iX], currX, currY);
				map.put(cPair(currX, currY), currContainer);
				checkBounds(currX, currY);
			}
		}
		updateAllNeighbors();
	}

	public HashBrown(Generic[][] data, double lowerX, double lowerY, boolean cr) {
		minX = maxX = lowerX;
		minY = maxY = lowerY;
		map = new HashMap<String, Container>();
		dX = dY = 1;
        stepX = BigDecimal.valueOf(dX);
        stepY = BigDecimal.valueOf(dY);
		for (double currY = lowerY; currY < lowerY + data.length; currY++) {
			double iY = currY - lowerY;
			if (iY > 0) {
				assert (data[(int) iY].length == data[(int) iY - 1].length);
			}
			for (double currX = lowerX; currX < lowerX + data[0].length; currX++) {
				double iX = currX - lowerX;
				Container currContainer = new Container(data[(int)iY][(int)iX], currX, currY);
				map.put(cPair(currX, currY), currContainer);
				checkBounds(currX, currY);
			}
		}
		updateAllNeighbors();
		canReplace = cr;
	}

	public HashBrown(Function<Generic> f, double lowerX, double upperX, double lowerY, double upperY) {
		minX = maxX = lowerX;
		minY = maxY = lowerY;
		dX = dY = 1;
        stepX = BigDecimal.valueOf(dX);
        stepY = BigDecimal.valueOf(dY);
		map = new HashMap<String, Container>();
		assert(upperX >= lowerX && upperY >= lowerY);
		for (double currY = lowerY; currY <= upperY; currY++) {
			for (double currX = lowerX; currX <= upperX; currX++) {
				Container currContainer = new Container(f.operation(currX, currY), currX, currY);
				map.put(cPair(currX, currY), currContainer);
				checkBounds(currX, currY);
			}
		}
		updateAllNeighbors();
	}

	public HashBrown(Function<Generic> f, double lowerX, double upperX, double lowerY, double upperY, double deltaX, double deltaY) {
		minX = maxX = lowerX;
		minY = maxY = lowerY;
		map = new HashMap<String, Container>();
		dX = deltaX;
		dY = deltaY;
        stepX = BigDecimal.valueOf(dX);
        stepY = BigDecimal.valueOf(dY);
		assert(upperX >= lowerX && upperY >= lowerY);
		for (BigDecimal currY = BigDecimal.valueOf(lowerY); currY.doubleValue() <= upperY; currY = currY.add(stepY)) {
			for (BigDecimal currX = BigDecimal.valueOf(lowerX); currX.doubleValue() <= upperX; currX = currX.add(stepX)) {
				Container currContainer = new Container(f.operation(currX.doubleValue(),
						currY.doubleValue()), currX.doubleValue(),
						currY.doubleValue());
				map.put(cPair(currX.doubleValue(),
						currY.doubleValue()), currContainer);
				checkBounds(currX.doubleValue(),
						currY.doubleValue());
			}
		}
		updateAllNeighbors();
	}

	private void updateAllNeighbors() {
		for (Container c : map.values()) {
			updateNeighbors(c.getPosX(), c.getPosY());
		}
	}

	private String cPair(double x, double y) {
		return String.format("(%1$s, %2$s)", x, y);
	}

	private boolean hasLocation(double x, double y) {
		return map.get(cPair(x, y)) != null;
	}

	public Generic setNode(Generic i, int x, int y) {
		if (hasLocation(x, y)) {
			return map.get(cPair(x, y)).setItem(i);
		} else {
			throw new IllegalArgumentException(String.format("No node exists at location %s.", cPair(x, y)));
		}
	}

	public Container getCont(double x, double y) {
		return map.get(cPair(x, y));
	}

	private void checkBounds(double x, double y) {

		if (x > maxX) {
            maxX = x;
        } else if (x < minX) {
            minX = x;
        }

		if (y > maxY) {
            maxY = y;
        } else if (y < minY) {
            minY = y;
        }
	}

	private void updateNeighbors(double x, double y) {
        BigDecimal roundX = BigDecimal.valueOf(x);
        BigDecimal roundY = BigDecimal.valueOf(y);
		Container curr = getCont(roundX.doubleValue(), roundY.doubleValue());
		Container up = getCont(roundX.doubleValue(), roundY.add(stepY).doubleValue());
		Container right = getCont(roundX.add(stepX).doubleValue(), roundY.doubleValue());
		Container down = getCont(roundX.doubleValue(), roundY.subtract(stepY).doubleValue());
		Container left = getCont(roundX.subtract(stepX).doubleValue(), roundY.doubleValue());
		if (up != null) {
            up.setNeighbor(curr, "down");
        }
		if (right != null) {
            right.setNeighbor(curr, "left");
        }
		if (down != null) {
            down.setNeighbor(curr, "up");
        }
		if (left != null) {
            left.setNeighbor(curr, "right");
        }
	}

	private void updateNeighbors(BigDecimal x, BigDecimal y) {
		Container curr = getCont(x.doubleValue(), y.doubleValue());
		Container up = getCont(x.doubleValue(), y.add(stepY).doubleValue());
		Container right = getCont(x.add(stepX).doubleValue(), y.doubleValue());
		Container down = getCont(x.doubleValue(), y.subtract(stepY).doubleValue());
		Container left = getCont(x.subtract(stepX).doubleValue(), y.doubleValue());
		if(up != null) {
            up.setNeighbor(curr, "down");
        }
		if(right != null) {
            right.setNeighbor(curr, "left");
        }
		if(down != null) {
            down.setNeighbor(curr, "up");
        }
		if(left != null) {
            left.setNeighbor(curr, "right");
        }
	}

	public double[] getDimensions() {
        BigDecimal lenX = BigDecimal.valueOf(maxX).subtract(BigDecimal.valueOf(minX)).divide(stepX, RoundingMode.HALF_UP);
        BigDecimal lenY = BigDecimal.valueOf(maxY).subtract(BigDecimal.valueOf(minY)).divide(stepY, RoundingMode.HALF_UP);
		return new double[]{lenX.doubleValue() + 1, lenY.doubleValue() + 1};
	}

	public String getDimensionString() {
		return String.format("(%1$s x %2$s)", getDimensions()[0], getDimensions()[1]);
	}

	public void expand(Generic i, double x, double y, String dir) {
		if (hasLocation(x, y)) {
			Container curr = map.get(cPair(x, y));
			switch (dir) {
				case "up":
					if (!hasLocation(x, y + dY) || (canReplace)) {
						Container newContainer = new Container(i, x, y + dY,
								getCont(x, y + 2*dY), getCont(x + dX, y + dY),
								getCont(x, y), getCont(x - dX, y + dY));
						map.put(cPair(x, y + dY), newContainer);
						checkBounds(x, y + dY);
					} else {
                        System.out.printf("Cannot insert node into location of existing node at (%1$s, %2$s)\n",
                                x, y + dY);
                    }
					updateNeighbors(x, y + dY);
					break;
				case "right":
					if (!hasLocation(x + dX, y) || canReplace) {
						Container newContainer = new Container(i, x + dX, y,
								getCont(x + dY, y + dY), getCont(x + 2*dX, y),
								getCont(x + dX, y - dY), getCont(x, y));
						map.put(cPair(x + dX, y), newContainer);
						checkBounds(x + 1, y);
					} else {
                        System.out.printf("Cannot insert node into location of existing node at (%1$s, %2$s)\n",
                                x + 1, y);
                    }
					updateNeighbors(x + dX, y);
					break;
				case "down":
					if (!hasLocation(x, y - dY) || canReplace) {
						Container newContainer = new Container(i, x, y - dY,
								getCont(x, y), getCont(x + dX, y - dY),
								getCont(x, y - 2*dY), getCont(x - dX, y - dY));
						map.put(cPair(x, y - dY), newContainer);
						checkBounds(x, y - dY);
					}
					else {
                        System.out.printf("Cannot insert node into location of existing node at (%1$s, %2$s)\n",
                                x, y - dY);
                    }
					updateNeighbors(x, y - dY);
					break;
				case "left":
					if (!hasLocation(x - dX, y) || canReplace) {
						Container newContainer = new Container(i, x - dX, y, getCont(x - 1, y + 1), getCont(x, y),
								getCont(x - 1, y - 1), getCont(x - 2, y));
						map.put(cPair(x - 1, y), newContainer);
						checkBounds(x - 1, y);
					}
					else {
                        System.out.printf("Cannot insert node into location of existing node at (%1$s, %2$s)\n",
                                x - 1, y);
                    }
					updateNeighbors(x - 1, y);
					break;
				default:
					throw new IllegalArgumentException(String.format("%s is not a valid direction.", dir));
			}
		} else {
            throw new IllegalArgumentException(String.format("%s is not an existing location on the map.",
                    cPair(x, y)));
        }
	}

	private String toString(BigDecimal y) {
		if (y.doubleValue() < minY || y.doubleValue() > maxY) {
            return "";
        }
		String result = "";
        for (BigDecimal x = BigDecimal.valueOf(minX); x.doubleValue() <= maxX; x = x.add(stepX)) {
			Container curr = map.get(cPair(x.doubleValue(), y.doubleValue()));
			if (curr != null) {
				if (x.doubleValue() == 0 && y.doubleValue() == 0) {
                    result += String.format("< %1$7.5s >", map.get(cPair(x.doubleValue(), y.doubleValue())).getItem());
                } else {
                    result += String.format("| %1$7.5s |", map.get(cPair(x.doubleValue(), y.doubleValue())).getItem());
                }
			} else {
				result += String.format("| %1$7.5s |", map.get(cPair(x.doubleValue(), y.doubleValue())));
			}
		}
		return result + "\n" + toString(y.subtract(stepY));
	}

	public String toString() {
		return " HASHBROWN " + new String(new char[(int)((maxX - minX)/dX)]).replace("\0", "===========") + "\n"
                + toString(BigDecimal.valueOf(maxY));
	}

	public static BigDecimal valOf(double n) { return BigDecimal.valueOf(n);}

	public static void main(String[] args) {
		System.out.println("Please use HashBrownTests.java for unit testing.");
	}
}