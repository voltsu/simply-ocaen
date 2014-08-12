package cn.simplyocean.entities;

import com.lidroid.xutils.db.annotation.Column;
import com.lidroid.xutils.db.annotation.Id;
import com.lidroid.xutils.db.annotation.NoAutoIncrement;

public class ProductTable {
	@Id
	@NoAutoIncrement
	private int pid;
	@Column(column = "pname")
	private String pname;
	@Column(column = "pprice")
	private double pprice;
	@Column(column = "pinventory")
	private int pinventory;
	@Column(column = "ptype")
	private int ptype;
	@Column(column = "pdescription")
	private String pdescription;
	@Column(column = "ppic")
	private String ppic;
	public int getPid() {
		return pid;
	}
	public void setPid(int pid) {
		this.pid = pid;
	}
	public String getPname() {
		return pname;
	}
	public void setPname(String pname) {
		this.pname = pname;
	}
	public double getPprice() {
		return pprice;
	}
	public void setPprice(double pprice) {
		this.pprice = pprice;
	}
	public int getPinventory() {
		return pinventory;
	}
	public void setPinventory(int pinventory) {
		this.pinventory = pinventory;
	}
	public int getPtype() {
		return ptype;
	}
	public void setPtype(int ptype) {
		this.ptype = ptype;
	}
	public String getPdescription() {
		return pdescription;
	}
	public void setPdescription(String pdescription) {
		this.pdescription = pdescription;
	}
	public String getPpic() {
		return ppic;
	}
	public void setPpic(String ppic) {
		this.ppic = ppic;
	}
	@Override
	public String toString() {
		return "ProductList [pid=" + pid + ", pname=" + pname + ", pprice="
				+ pprice + ", pinventory=" + pinventory + ", ptype=" + ptype
				+ ", pdescription=" + pdescription + ", ppic=" + ppic + "]";
	}
	
}
