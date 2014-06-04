package rentalShop;

import java.util.Calendar;

class Rental {
	private String id = "";
	private String customerId = "";
	private String[] itemId = new String[0];
	private Calendar date = Calendar.getInstance();
	private Calendar returnDate = Calendar.getInstance();
	private boolean isRent = true;
	//ヌルヌル対策
	//別にヌルヌルさせてもJTableの見た目が悪くなるだけで害はないんだけど　気になる
	Rental(int maxRent) {
		 itemId = new String[maxRent];
		 for (int i = 0; i < itemId.length; i++) {
			 itemId[i] = "";
		 }
	}
	String toCSV() {
		StringBuffer csv = new StringBuffer();
		csv.append(id);
		csv.append(",");
		csv.append(customerId);
		csv.append(",");
		csv.append(getYear());
		csv.append(",");
		csv.append(getMonth());
		csv.append(",");
		csv.append(getDay());
		csv.append(",");
		csv.append(getReturnYear());
		csv.append(",");
		csv.append(getReturnMonth());
		csv.append(",");
		csv.append(getReturnDay());
		csv.append(",");
		csv.append(getIsRent());
		csv.append(",");
		for (int i = 0; i < itemId.length; i++) {
		csv.append(itemId[i]);
		csv.append(",");
		}
		return csv.toString();
		
	}
	
	void fromCSV(String csv) {
		String[] sArray;
		sArray = csv.split(",");
		this.id = sArray[0];
		this.customerId = sArray[1];
		this.date.set(Integer.parseInt(sArray[2]), Integer.parseInt(sArray[3]), Integer.parseInt(sArray[4]));
		this.returnDate.set(Integer.parseInt(sArray[5]),Integer.parseInt(sArray[6]),Integer.parseInt(sArray[7]));
		this.isRent = (sArray[8].equals("true") ? true : false);
		this.itemId = new String[sArray.length - 9];
		for (int i = 0; i < sArray.length - 9; i++) {
			this.itemId[i] = sArray[i + 9];
		}
		killFractions();
	}
	
	void setId(String id) {
		this.id = id;
	}
	
	void setCustomerId(String customerId) {
		if (!customerId.equals(""))
			this.customerId = customerId;
	}

	void setItemId(String[] itemId) {
		for(int i = 0; i < itemId.length; i++) {
			if (!itemId[i].equals(""))
				this.itemId[i] = itemId[i];
		}
	}
	
	void setYear(String year) {
		if (!year.equals(""))
			date.set(Calendar.YEAR, Integer.parseInt(year));
	}
	void setMonth (String month) {
		if (!month.equals(""))
			date.set(Calendar.MONTH, Integer.parseInt(month));
	}
	void setDay (String day) {
		if (!day.equals(""))
			date.set(Calendar.DATE, Integer.parseInt(day));
	}
	void setReturnYear(String year) {
		if (!year.equals(""))
			returnDate.set(Calendar.YEAR, Integer.parseInt(year));
	}
	void setReturnMonth(String month) {
		if (!month.equals(""))
			returnDate.set(Calendar.MONTH, Integer.parseInt(month));
	}
	void setReturnDay(String day) {
		if(!day.equals(""))
			returnDate.set(Calendar.DATE,Integer.parseInt(day));
	}
	void setIsRent(boolean isRent) {
		this.isRent = isRent;
	}
	
	//getInstanceで作成する際、分以下も取得されている
	//延滞計算するのに邪魔になるので殺す　端数殺すべし　慈悲はない　イヤーッ！
	void killFractions() {
		date.set(Calendar.HOUR, 0);
		date.set(Calendar.MINUTE, 0);
		date.set(Calendar.SECOND, 0);
		date.set(Calendar.MILLISECOND, 0);
		returnDate.set(Calendar.HOUR, 0);
		returnDate.set(Calendar.MINUTE, 0);
		returnDate.set(Calendar.SECOND, 0);
		returnDate.set(Calendar.MILLISECOND, 0);
	}
	
	String getId() {
		return id;
	}
	String getCustomerId() {
		return customerId;
	}
	
	String getItemId(int index) {
		if (index < itemId.length && index >= 0) {
			return itemId[index];
		} else {
			return "";
		}
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
	String getReturnYear() {
		return Integer.toString(returnDate.get(Calendar.YEAR));
	}
	String getReturnMonth() {
		return Integer.toString(returnDate.get(Calendar.MONTH));
	}
	String getReturnDay() {
		return Integer.toString(returnDate.get(Calendar.DATE));
	}
	//以下2つは表示用なのであらかじめMONTHに加算
	//生データが欲しいなら上のやつで
	String getDate() {
		return Integer.toString(date.get(Calendar.YEAR)) + "/" + Integer.toString(date.get(Calendar.MONTH) + 1) + "/" + Integer.toString(date.get(Calendar.DATE));
	}
	//検索用
	String getDateForSearch() {
		return Integer.toString(date.get(Calendar.YEAR)) + Integer.toString(date.get(Calendar.MONTH) + 1) + Integer.toString(date.get(Calendar.DATE));
	}
	String getReturnDate() {
		return Integer.toString(returnDate.get(Calendar.YEAR)) + "/" + Integer.toString(returnDate.get(Calendar.MONTH) + 1) + "/" + Integer.toString(returnDate.get(Calendar.DATE)); 
	}
	boolean getIsRent() {
		return isRent;
	}
	
	int getLength() {
		return itemId.length;
	}
	boolean isDelayed() {
		Calendar cal = Calendar.getInstance();
		cal.set(Calendar.HOUR, 0);
		cal.set(Calendar.MINUTE, 0);
		cal.set(Calendar.SECOND, 0);
		cal.set(Calendar.MILLISECOND, 0);
		if (cal.compareTo(returnDate) <= 0) {
			return false;
		} else {
			return true;
		}
	}
}
