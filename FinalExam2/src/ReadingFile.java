/*Name: Aishwarya Nitin Kapse
 * UCI NetID: akapse
 *Petrinet
 */


import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

class BaseAll {

	private String name;

	public BaseAll(String name) {
		super();
		this.name = name;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	@Override
	public String toString() {
		return name;
	}
}

class Place extends BaseAll {
	public static final int MAXTOKENS = 100;
	private int tokensHeld = 0;
	private int maxTokens = MAXTOKENS;

	public Place(String name, int tokensHeld) {
		super(name);
		this.tokensHeld = tokensHeld;
	}

	public boolean hasAtLeastTokens(int threshold) {
		return (tokensHeld >= threshold);
	}

	public boolean maxTokensReached(int newTokens) {
		if (hasUnlimitedMaxTokens()) {
			return false;
		}

		return (tokensHeld + newTokens > maxTokens);
	}

	private boolean hasUnlimitedMaxTokens() {
		return maxTokens == MAXTOKENS;
	}

	public int getTokens() {
		return tokensHeld;
	}

	public void setTokens(int tokensHeld) {
		this.tokensHeld = tokensHeld;
	}

	public void setMaxTokens(int max) {
		this.maxTokens = max;
	}

	public void addTokens(int weight) {
		this.tokensHeld += weight;
	}

	public void removeTokens(int weight) {
		this.tokensHeld -= weight;
	}
}

class Transition extends BaseAll {

	public Transition(String transitionName) {
		super(transitionName);
	}

	private List<Edge> incomingEdge = new ArrayList<Edge>();
	private List<Edge> outgoingEdge = new ArrayList<Edge>();

	public boolean canFire() {
		boolean canFire = true;

		canFire = !this.isNotConnected();

		for (Edge arc : incomingEdge) {
			canFire = canFire & arc.canFire();
		}

		for (Edge arc : outgoingEdge) {
			canFire = canFire & arc.canFire();
		}
		return canFire;
	}

	public void fire() {
		for (Edge arc : incomingEdge) {
			arc.fire();
		}

		for (Edge arc : outgoingEdge) {
			arc.fire();
		}
	}

	public void addIncoming(Edge arc) {
		this.incomingEdge.add(arc);
	}

	
	public void addOutgoing(Edge arc) {
		this.outgoingEdge.add(arc);
	}

	
	public boolean isNotConnected() {
		return incomingEdge.isEmpty() && outgoingEdge.isEmpty();
	}

	@Override
	public String toString() {
		return super.toString() + (isNotConnected() ? " IS NOT CONNECTED" : "") + (canFire() ? " READY TO FIRE" : "");
	}
}

enum Direction {

	PLACE_TO_TRANSITION {
		@Override
		public boolean canFire(Place p, int weight) {
			return p.hasAtLeastTokens(weight);
		}

		@Override
		public void fire(Place p, int weight) {
			p.removeTokens(weight);
		}

	},

	TRANSITION_TO_PLACE {
		@Override
		public boolean canFire(Place p, int weight) {
			return !p.maxTokensReached(weight);
		}

		@Override
		public void fire(Place p, int weight) {
			p.addTokens(weight);
		}

	};

	public abstract boolean canFire(Place p, int weight);

	public abstract void fire(Place p, int weight);

}

class Edge extends BaseAll {
	Place place;
	Transition transition;
	Direction direction;
	int weight = 1;

	private Edge(String name, Direction d, Place p, Transition t) {
		super(name);
		this.direction = d;
		this.place = p;
		this.transition = t;
	}

	protected Edge(String name, Place p, Transition t) {
		this(name, Direction.PLACE_TO_TRANSITION, p, t);
		t.addIncoming(this);
	}

	protected Edge(String name, Transition t, Place p) {
		this(name, Direction.TRANSITION_TO_PLACE, p, t);
		t.addOutgoing(this);
	}

	public boolean canFire() {
		return direction.canFire(place, weight);
	}

	public void fire() {
		this.direction.fire(place, this.weight);
	}

	public void setWeight(int weight) {
		this.weight = weight;
	}

	public int getWeight() {
		return weight;
	}
}

class Petrinet extends BaseAll {

	private static final String nl = "\n";
	List<Place> places = new ArrayList<Place>();
	List<Transition> transitions = new ArrayList<Transition>();
	List<Edge> edges = new ArrayList<Edge>();

	public Petrinet(String name) {
		super(name);
	}

	public void add(BaseAll o) {
		if (o instanceof Edge) {
			edges.add((Edge) o);
		} else if (o instanceof Place) {
			places.add((Place) o);
		} else if (o instanceof Transition) {
			transitions.add((Transition) o);
		}
	}

	public List<Transition> getTransitionsAbleToFire() {
		ArrayList<Transition> list = new ArrayList<Transition>();
		for (Transition t : transitions) {
			if (t.canFire()) {
				list.add(t);
			}
		}
		return list;
	}

	public Transition transition(String name) {
		Transition t = new Transition(name);
		transitions.add(t);
		return t;
	}

	public void printPlaceList() {
		for (Place p : places) {
			System.out.print(p.getName());
			System.out.print(" ");
			System.out.print(p.getTokens());
			System.out.println();
		}
	}

	public void printTransitionList() {
		for (Transition t : transitions) {
			System.out.println(t.getName());
		}
	}

	public void printEdgeList() {
		for (Edge e : edges) {
			System.out.println(e.getName());
		}
	}

	public Place place(String name, int initial) {
		Place p = new Place(name, initial);
		places.add(p);
		return p;
	}

	public Edge arc(String name, Place p, Transition t) {
		Edge arc = new Edge(name, p, t);
		edges.add(arc);
		return arc;
	}

	public Edge arc(String name, Transition t, Place p) {
		Edge arc = new Edge(name, t, p);
		edges.add(arc);
		return arc;
	}

	@Override
	public String toString() {
		StringBuilder sb = new StringBuilder("Petrinet ");
		sb.append(super.toString()).append(nl);
		sb.append("---Transitions---").append(nl);
		for (Transition t : transitions) {
			sb.append(t).append(nl);
		}
		sb.append("---Places---").append(nl);
		for (Place p : places) {
			sb.append(p).append(nl);
		}
		return sb.toString();
	}

	public List<Place> getPlaces() {
		return places;
	}

	public List<Transition> getTransitions() {
		return transitions;
	}

	public List<Edge> getEdges() {
		return edges;
	}
}

public class ReadingFile {

	public static void main(String[] args) throws IOException {
		String file = args[0];
		int maxCycles = Integer.valueOf(args[1]);
		@SuppressWarnings("resource")
		BufferedReader br = new BufferedReader(new FileReader(file));
		String oneLine = br.readLine();
		Petrinet p1 = new Petrinet("This is the Petrinet");
		Edge e;
		while (oneLine != null) {
			String[] tokenize = oneLine.split(" ");
			if (tokenize[0].equals("place")) {
				Place p = new Place(tokenize[1], Integer.valueOf(tokenize[2]));
				p1.add(p);
			} else if (tokenize[0].equals("transition")) {
				Transition t = new Transition(tokenize[1]);
				p1.add(t);
			} else if (tokenize[0].equals("edge")) {
				boolean flag = true;

				for (Place p : p1.places) {
					if (tokenize[1].equals(p.getName())) {
						for (Transition t : p1.transitions) {
							if (tokenize[2].equals(t.getName())) {
								e = new Edge("incoming", p, t);
								p1.add(e);
								flag = false;
								break;
							}
						}
						break;
					}
				}

				if (flag) {
					for (Transition t : p1.transitions) {
						if (tokenize[1].equals(t.getName())) {
							for (Place p : p1.places) {
								if (tokenize[2].equals(p.getName())) {
									e = new Edge("outgoing", t, p);
									p1.add(e);
									break;
								}
							}
							break;
						}
					}
				}
			} else {
				System.out.println("Invalid Entry");
			}
			oneLine = br.readLine();
		}

		List<Transition> ableToFire = new ArrayList<Transition>();
		for (int i = 0; i < maxCycles; i++) {
			ableToFire = p1.getTransitionsAbleToFire();
			int size = ableToFire.size();
			if(size > 0){
				Transition t = ableToFire.get(0);
				t.fire();
				System.out.println("Cycle No: " + " " + (i + 1));
				for (Place p : p1.places) {
					System.out.print("Place: " + p.getName() + "  " + "No of Tokens: " + p.getTokens());
					System.out.println();
				}
				System.out.println();
			}
			
			if(size == 0){
				break;
			}
		}
	}
}

/*NB: Used internet help.*/
