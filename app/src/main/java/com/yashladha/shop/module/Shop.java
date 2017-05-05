package com.yashladha.shop.module;

class Shop {
    private String ownerFirstName;
    private String ownerLastName;
    private String email;
    private String shopName;
    private String password;
    private String shopDescription;
    private String Address;
    private String phoneNumber;
    private String Cateogry;
    private String Zone;

    public Shop(String ownerFirstName, String ownerLastName, String email, String shopName, String password, String shopDescription, String Address, String phoneNumber, String Cateogry, String Zone) {
        this.ownerFirstName = ownerFirstName;
        this.ownerLastName = ownerLastName;
        this.email = email;
        this.shopName = shopName;
        this.password = password;
        this.shopDescription = shopDescription;
        this.Address = Address;
        this.phoneNumber = phoneNumber;
        this.Cateogry = Cateogry;
        this.Zone = Zone;
    }

    public Shop() {

    }

    public String getAddress() {
        return Address;
    }

    public String getPhoneNumber() {
        return phoneNumber;
    }

    public String getCateogry() {
        return Cateogry;
    }

    public String getZone() {
        return Zone;
    }

    Shop(String ownerFirstName, String ownerLastName, String email, String shopName, String password, String shopDescription) {
        this.ownerFirstName = ownerFirstName;
        this.ownerLastName = ownerLastName;
        this.email = email;
        this.shopName = shopName;
        this.password = password;
        this.shopDescription = shopDescription;
    }

    public String getEmail() {
        return email;
    }

    public String getOwnerFirstName() {
        return ownerFirstName;
    }

    public String getOwnerLastName() {
        return ownerLastName;
    }

    public String getShopName() {
        return shopName;
    }

    public String getPassword() {
        return password;
    }

    public String getShopDescription() {
        return shopDescription;
    }
}
