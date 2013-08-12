package test;

public class Variant implements Comparable{
	public Variant(String name, int[] phenotypes){
		this.phenotypes=phenotypes;
		this.name=name;
	}

	public int compareTo(Object o) {
		Variant v=(Variant) o;
		return this.hits-v.getHits();
	}
	public int getHits() {
		return hits;
	}
	public int[] getPhenotypes() {
		return phenotypes;
	}
	public void setHits(int hits) {
		this.hits = hits;
	}
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	private int hits;
	private String name;
	private int[] phenotypes;

}
