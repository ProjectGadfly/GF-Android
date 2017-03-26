package com.example.gadfly.projectgadfly;

/**
 * Created by papak on 3/21/2017.
 */

class Representatives {
    public String name, phone_number, email, district, state, photo_url, party;

    /**
     * Constructor for representatives class
     * @param name Name of the representative
     * @param phone_number Phone number of the representative
     * @param photo_url Url to the photo of the representative
     */
    Representatives(String name, String phone_number, String photo_url) {
        this.name = name;
        this.phone_number = phone_number;
        this.email = "STOCK";
        this.district = "STOCK";
        this.state = "STOCK";
        this.photo_url = photo_url;
        this.party = "STOCK";
    }

    // Constructor for future development with more fields for legislator
    Representatives(String name, String phone_number, String email, String district,
                    String state, String photo_url, String party) {
        this.name = name;
        this.phone_number = phone_number;
        this.email = email;
        this.district = district;
        this.state = state;
        this.photo_url = photo_url;
        this.party = party;
    }
}
