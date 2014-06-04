package rentalShop;

import java.util.Calendar;

class Customer {
	protected String id = "";
	protected String name = "";
	protected String sex = "";
	protected String phone = "";
	protected String address = "";
	protected Calendar date = Calendar.getInstance();
	private boolean isAvailable = true;



	String toCSV() {
		StringBuffer csv = new StringBuffer();
		csv.append(id);
		csv.append(",");
		csv.append(name);
		csv.append(",");
		csv.append(sex);
		csv.append(",");
		csv.append(phone);
		csv.append(",");
		csv.append(address);
		csv.append(",");
		csv.append(getYear());
		csv.append(",");
		csv.append(getMonth());
		csv.append(",");
		csv.append(getDay());
		csv.append(",");
		csv.append(isAvailable);
		return csv.toString();

	}

	void fromCSV(String csv) {
		String[] sArray;
		sArray = csv.split(",");
		this.id = sArray[0];
		this.name = sArray[1];
		this.sex = sArray[2];
		this.phone = sArray[3];
		this.address = sArray[4];
		this.date.set(Integer.parseInt(sArray[5]), Integer.parseInt(sArray[6]), Integer.parseInt(sArray[7]));
		this.isAvailable = (sArray[8].equals("true") ? true : false);
	}

	//セッターゲッター類
	//セッターについては引数が空文字列かどうかの判断ももたせる（商品情報変更時用）
	void setId (String id) {
		this.id = id;
	}

	void setName (String name) {
		if (!name.equals(""))
			this.name = name;
	}

	void setSex (String sex) {
		if (!sex.equals(""))
			this.sex = sex;
	}
	void setPhone (String phone) {
		if (!phone.equals(""))
			this.phone = phone;
	}
	void setAddress(String address) {
		if (!address.equals(""))
			this.address = address;
	}
	void setYear(String year) {
		if (!year.equals(""))
			date.set(Calendar.YEAR, Integer.parseInt(year));
	}
	void setMonth (String month) {
		if (!month.equals(""))
			date.set(Calendar.MONTH, Integer.parseInt(month) - 1);
	}
	void setDay (String day) {
		if (!day.equals(""))
			date.set(Calendar.DATE, Integer.parseInt(day));
	}
	void setAvailable (boolean Available) {
		this.isAvailable = Available;
	}

	String getId() {
		return id;
	}

	String getName() {
		return name;
	}

	String getSex() {
		return sex;
	}
	String getPhone() {
		return phone;
	}
	//機能的にはgetDateがあれば十分だが、一応
	String getYear() {
		return Integer.toString(date.get(Calendar.YEAR));
	}
	String getMonth() {
		return Integer.toString(date.get(Calendar.MONTH));
	}
	String getDay() {
		return Integer.toString(date.get(Calendar.DATE));
	}
	String getDate() {
		return Integer.toString(date.get(Calendar.YEAR)) + "/" + Integer.toString(date.get(Calendar.MONTH) + 1) + "/" + Integer.toString(date.get(Calendar.DATE));
	}
	String getDateForSearch() {
		return Integer.toString(date.get(Calendar.YEAR)) + Integer.toString(date.get(Calendar.MONTH) + 1) + Integer.toString(date.get(Calendar.DATE));
	}
	String getAddress() {
		return address;
	}
	boolean getAvailable() {
		return isAvailable;
	}

}
