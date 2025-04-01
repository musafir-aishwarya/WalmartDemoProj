package com.walmart.demo.DTO;

public class ItemDTO {
	  private String name;
	    private String description;
	    private double price;
	    private String imagePath; // URL or path to image
		public String getName() {
			return name;
		}
		public void setName(String name) {
			this.name = name;
		}
		public String getDescription() {
			return description;
		}
		public void setDescription(String description) {
			this.description = description;
		}
		public double getPrice() {
			return price;
		}
		public void setPrice(double price) {
			this.price = price;
		}
		public String getImagePath() {
			return imagePath;
		}
		public void setImagePath(String imagePath) {
			this.imagePath = imagePath;
		}
		public void setId(Long id) {
			// TODO Auto-generated method stub
			
		}


}
