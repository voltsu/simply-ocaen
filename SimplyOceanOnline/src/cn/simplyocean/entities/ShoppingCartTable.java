package cn.simplyocean.entities;

import java.io.Serializable;

import com.lidroid.xutils.db.annotation.Column;
import com.lidroid.xutils.db.annotation.Id;
import com.lidroid.xutils.db.annotation.NoAutoIncrement;
import com.lidroid.xutils.db.annotation.Table;

@Table(name = "shoppingcart")
public class ShoppingCartTable implements Serializable{
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	@Id @NoAutoIncrement
	private int pid;
	@Column(column = "pname")
	private String pname;
	@Column(column = "qty")
	private int qty;
	@Column(column = "pprice")
	private float pprice;
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
	public int getQty() {
		return qty;
	}
	public void setQty(int qty) {
		this.qty = qty;
	}
	public float getPprice() {
		return pprice;
	}
	public void setPprice(float pprice) {
		this.pprice = pprice;
	}
	
	public String getPpic() {
		return ppic;
	}
	public void setPpic(String ppic) {
		this.ppic = ppic;
	}
	@Override
	public String toString() {
		return "ShoppingCartTable [pid=" + pid + ", pname=" + pname + ", qty="
				+ qty + ", pprice=" + pprice + ", ppic=" + ppic + "]";
	}
	
}
