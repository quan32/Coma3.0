package quan.coma;

public class Product {
	private int id;
	private float ratio;
	private String name;
	
	Product(){
		this.id=0;
		this.ratio=101.0f;
		this.name="";
	}
	Product(int id, float ratio, String name){
		this.id=id;
		this.ratio=ratio;
		this.name=name;
	}
	public int getId() {
		return id;
	}
	public void setId(int id) {
		this.id = id;
	}
	public float getRatio() {
		return ratio;
	}
	public void setRatio(float ratio) {
		this.ratio = ratio;
	}
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}

}
